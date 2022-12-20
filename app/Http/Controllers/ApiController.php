<?php
namespace App\Http\Controllers;

use App\Helpers\HelpersFunction;
use App\Http\Controllers\Controller;
use App\Models\BayarSewa;
use App\Models\Kos;
use App\Models\Setting;
use App\Models\Sewa;
use App\Models\User;
use App\Models\Wilayah;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;
use Illuminate\Validation\Rule;
use Mail;
use URL;
use Validator;

class ApiController extends Controller
{

    public function __construct()
    {
        date_default_timezone_set('Asia/Jakarta');
    }

    public function getSewa(Request $request)
    {

        $user_id = auth('api')->user()->id;
        $user    = User::where('id', '=', $user_id)->first();

        if ($user->level === 'pemilik') {

            $sewa = DB::select("SELECT a.id,c.id AS kos_id,
                                       c.nama AS nama_kos,
                                       a.nama_kamar,
                                       a.tgl_sewa,
                                       a.tgl_jatuh_tempo,
                                       a.bulan_sewa,
                                       a.harga AS harga_total,
                                       b.nama AS nama_penyewa,
                                       a.`status`,
                                       TIMESTAMPDIFF(DAY, DATE(a.tgl_jatuh_tempo),DATE(NOW())) AS hari_jatuh_tempo
                                FROM sewa a
                                LEFT JOIN users b ON a.user_penyewa_id = b.id
                                LEFT JOIN kos c ON a.kos_id = c.id

                                WHERE ((a.`status` = 'AKT') OR (a.`status` = 'STOP')) AND c.user_pemilik_id = $user_id");

            echo json_encode(
                array(
                    'error'     => false,
                    'error_msg' => 'Data berhasil diambil',
                    'dataList'  => $sewa,
                )
            );

        } else {

            $sewa = DB::select("SELECT a.id,c.id AS kos_id,
                                      a.nama_kamar,
                                      a.tgl_sewa,
                                      DATE_FORMAT(a.tgl_jatuh_tempo,'%d-%m-%Y %H:%i:%s') AS tgl_jatuh_tempo,
                                      a.bulan_sewa,
                                      a.harga AS harga_total,
                                      b.nama AS nama_penyewa,
                                      a.`status`,
                                      c.nama AS nama_kos,
                                      TIMESTAMPDIFF(DAY, DATE(a.tgl_jatuh_tempo),DATE(NOW())) AS hari_jatuh_tempo
                                FROM sewa a
                                LEFT JOIN users b ON a.user_penyewa_id = b.id
                                LEFT JOIN kos c ON a.kos_id = c.id
                                WHERE ((a.`status` = 'AKT') OR (a.`status` = 'STOP'))   AND a.user_penyewa_id = $user_id");

            echo json_encode(
                array(
                    'error'     => false,
                    'error_msg' => 'Data berhasil diambil',
                    'dataList'  => $sewa,
                )
            );

        }
    }

    public function updateSewa(Request $request)
    {

        $user_id = auth('api')->user()->id;
        $user    = User::where('id', '=', $user_id)->first();

        if ($user->level === 'pemilik') {

            $id = $request->id;

            $sewa             = Sewa::where("id", "=", $id)->first();
            $sewa->nama_kamar = $request->nama_kamar;
            $sewa->bulan_sewa = $request->bulan_sewa;
            $sewa->harga      = $request->harga;
            $sewa->save();

            $response["error"]     = false;
            $response["error_msg"] = "Perubahan berhasil dilakukan";

            echo json_encode($response);

        }

    }

    public function berhentiSewa(Request $request)
    {

        $sewa_id = $request->sewa_id;

        $sewa             = Sewa::where("id", "=", $sewa_id)->first();
        $sewa->status     = 'STOP';
        $sewa->tgl_keluar = date('Y-m-d H:i:s');
        $sewa->save();

        $response["error"]     = false;
        $response["error_msg"] = "Permohonan berhasil diajukan";

        $sewa_det = DB::select("SELECT  b.id AS id_penyewa,
                                        d.id AS id_pemilik,
                                        c.nama AS nama_kos,
                                        a.nama_kamar AS nama_kamar,
                                        b.nama AS nama_penyewa
                                FROM sewa a
                                LEFT JOIN users b ON a.user_penyewa_id = b.id
                                LEFT JOIN kos c ON a.kos_id = c.id
                                LEFT JOIN users d ON c.user_pemilik_id = d.id
                                WHERE a.id = $sewa_id")[0];

        $id_penyewa   = $sewa_det->id_penyewa;
        $id_pemilik   = $sewa_det->id_pemilik;
        $nama_kos     = $sewa_det->nama_kos;
        $nama_kamar   = $sewa_det->nama_kamar;
        $nama_penyewa = $sewa_det->nama_penyewa;

        HelpersFunction::simpanNotifikasi($id_pemilik, "Penyewa atas nama : " . $nama_penyewa . " (" . $nama_kos . " :: " . $nama_kamar . ") mengajukan berhenti sewa");
        HelpersFunction::simpanNotifikasi($id_penyewa, "Silahkan menemui pemilik sewa untuk pengembalian kunci dan pelunasan sisa sewa jika ada");

        echo json_encode($response);

    }

    public function setujuiBerhentiSewa(Request $request)
    {

        $sewa_id = $request->sewa_id;

        $sewa         = Sewa::where("id", "=", $sewa_id)->first();
        $sewa->status = 'NAK';
        $sewa->save();

        $response["error"]     = false;
        $response["error_msg"] = "Status berhasil diubah";

        $sewa_det = DB::select("SELECT  b.id AS id_penyewa,
                                        d.id AS id_pemilik,
                                        c.nama AS nama_kos,
                                        a.nama_kamar AS nama_kamar,
                                        b.nama AS nama_penyewa
                                FROM sewa a
                                LEFT JOIN users b ON a.user_penyewa_id = b.id
                                LEFT JOIN kos c ON a.kos_id = c.id
                                LEFT JOIN users d ON c.user_pemilik_id = d.id
                                WHERE a.id = $sewa_id")[0];

        $id_penyewa   = $sewa_det->id_penyewa;
        $id_pemilik   = $sewa_det->id_pemilik;
        $nama_kos     = $sewa_det->nama_kos;
        $nama_kamar   = $sewa_det->nama_kamar;
        $nama_penyewa = $sewa_det->nama_penyewa;

        HelpersFunction::simpanNotifikasi($id_penyewa, "Terimakasih sudah melakukan sewa kos di kamarkos.com\n.Mohon untuk memberikan rating dan pengalaman anda pada halaman riwayat sewa");

        echo json_encode($response);

    }

    public function bayar(Request $request)
    {

        $user_id = auth('api')->user()->id;
        $user    = User::where('id', '=', $user_id)->first();

        if ($user->level === 'pemilik') {

            $id = $request->id;

            $sewa             = Sewa::where("id", "=", $id)->first();
            $sewa->nama_kamar = $request->nama_kamar;
            $sewa->bulan_sewa = $request->bulan_sewa;
            $sewa->harga      = $request->harga;
            $sewa->status     = 'AKT';
            $sewa->save();

            // if ($request->hasfile('foto_pembayaran')) {
            //     $file      = $request->file('foto_pembayaran');
            //     $extension = $file->getClientOriginalExtension();
            //     $filename  = time() . '.' . $extension;
            //     $file->move('uploads/', $filename);
            //     $request->foto_pembayaran = $filename;
            // }

            if ($request->has('foto_pembayaran')) {

                $image = $request->foto_pembayaran; // your base64 encoded

                if ($image !== "NO_IMAGE") {

                    $image = str_replace('data:image/png;base64,', '', $image);
                    $image = str_replace(' ', '+', $image);

                    $imageName = 'bayar-' . $sewa->id . '-' . date('Y-m-d') . '.jpg';
                    file_put_contents('uploads/' . $imageName, base64_decode($image));
                    $request->foto_pembayaran = $imageName;
                }
            }

            BayarSewa::updateOrCreate(
                [
                    'sewa_id'         => $sewa->id,
                    'tgl_jatuh_tempo' => $sewa->tgl_jatuh_tempo,
                ],
                [
                    'foto_pembayaran'   => $request->foto_pembayaran,
                    'rekening_bank'     => 'BANK|0000000|PENGIRIM',
                    'tgl_bayar'         => date('Y-m-d H:i:s'),
                    'status'            => 'BBV',
                    'diverifikasi_oleh' => 'PEM',
                ]);

            $sewa                  = Sewa::where("id", "=", $id)->first();
            $sewa->tgl_jatuh_tempo = Carbon::createFromFormat('Y-m-d H:i:s', $sewa->tgl_jatuh_tempo)->addMonth($sewa->bulan_sewa);
            $sewa->save();

            $response["error"]     = false;
            $response["error_msg"] = "Pembayaran berhasil dilakukan";

            echo json_encode($response);

        } else {

            $id = $request->id;

            $sewa = Sewa::where("id", "=", $id)->first();

            if ($request->has('foto_pembayaran')) {

                $image = $request->foto_pembayaran; // your base64 encoded

                if ($image !== "NO_IMAGE") {

                    $image = str_replace('data:image/png;base64,', '', $image);
                    $image = str_replace(' ', '+', $image);

                    $imageName = 'bayar-' . $sewa->id . '-' . date('Y-m-d') . '.jpg';
                    file_put_contents('uploads/' . $imageName, base64_decode($image));
                    $request->foto_pembayaran = $imageName;
                }
            }

            BayarSewa::updateOrCreate(
                [
                    'sewa_id'         => $sewa->id,
                    'tgl_jatuh_tempo' => $sewa->tgl_jatuh_tempo,
                ],
                [
                    'foto_pembayaran' => $request->foto_pembayaran,
                    'rekening_bank'   => 'BANK|0000000|PENGIRIM',
                    'tgl_bayar'       => date('Y-m-d H:i:s'),
                    'status'          => 'BBU',
                ]);

            HelpersFunction::simpanNotifikasi($sewa->kos->user_pemilik_id, "Bukti pembayaran untuk " . $sewa->kos->nama . "::" . $sewa->nama_kamar . " atas nama " . $sewa->penyewa->nama . " telah diupload ! Menunggu verifikasi dari sistem  )");
            HelpersFunction::simpanNotifikasi($user_id, "Bukti pembayaran untuk " . $sewa->kos->nama . "::" . $sewa->nama_kamar . " atas nama " . $sewa->penyewa->nama . " telah kami terima ! Menunggu verifikasi dari sistem  )");

            $response["error"]     = false;
            $response["error_msg"] = "Bukti pembayaran berhasil dikirimkan";

            echo json_encode($response);

        }

    }

    public function updateLokasi(Request $request)
    {

        $kos = Kos::where('id', '=', $request->kos_id)->first();

        $kos->lat = $request->lat;
        $kos->lng = $request->lng;

        $kos->save();

        $response["error"]     = false;
        $response["error_msg"] = "Booking berhasil dilakukan";

        echo json_encode($response);
    }

    public function getTagihan(Request $request)
    {

        $user_id = auth('api')->user()->id;
        $user    = User::where('id', '=', $user_id)->first();

        if ($user->level === 'pemilik') {

            $sewa = DB::select("SELECT a.id,
                                       c.nama AS nama_kos,
                                       a.nama_kamar,
                                       a.tgl_sewa,
                                       a.tgl_jatuh_tempo,
                                       a.bulan_sewa,
                                       a.harga AS harga_total,
                                       b.nama AS nama_penyewa,
                                       a.`status`,
                                       IFNULL(d.status_bayar,0) AS status_bayar,
                                       TIMESTAMPDIFF(DAY, DATE(a.tgl_jatuh_tempo),DATE(NOW())) AS hari_jatuh_tempo,
                                       IFNULL(e.`status`,'PND') AS kode_status_bayar
                                FROM sewa a
                                LEFT JOIN users b ON a.user_penyewa_id = b.id
                                LEFT JOIN kos c ON a.kos_id = c.id

                                LEFT JOIN ( SELECT COUNT(id) AS status_bayar,tgl_jatuh_tempo,sewa_id
                                            FROM bayar_sewa
                                            WHERE `status` = 'BBV') d ON a.tgl_jatuh_tempo = d.tgl_jatuh_tempo AND a.id = d.sewa_id

                                LEFT JOIN ( SELECT `status`,tgl_jatuh_tempo, sewa_id
                                            FROM bayar_sewa) e ON a.tgl_jatuh_tempo = e.tgl_jatuh_tempo AND a.id = e.sewa_id

                                WHERE (a.`status` = 'AKT' OR a.`status` = 'BOK')
                                    AND c.user_pemilik_id = $user_id
                                    AND IFNULL(d.status_bayar,0) = 0
                                    AND TIMESTAMPDIFF(DAY, DATE(a.tgl_jatuh_tempo),DATE(NOW())) > -7");

            echo json_encode(
                array(
                    'error'     => false,
                    'error_msg' => 'Data berhasil diambil',
                    'dataList'  => $sewa,
                )
            );

        } else {

            $sewa = DB::select("SELECT a.id,
                                      a.nama_kamar,
                                      a.tgl_sewa,
                                      DATE_FORMAT(a.tgl_jatuh_tempo,'%d-%m-%Y %H:%i:%s') AS tgl_jatuh_tempo,
                                      a.bulan_sewa,
                                      a.harga AS harga_total,
                                      b.nama AS nama_penyewa,
                                      a.`status`,
                                      IFNULL(d.status_bayar,0) AS status_bayar,
                                      c.nama AS nama_kos,
                                      TIMESTAMPDIFF(DAY, DATE(a.tgl_jatuh_tempo),DATE(NOW())) AS hari_jatuh_tempo,
                                      IFNULL(e.`status`,'PND') AS kode_status_bayar
                                FROM sewa a
                                LEFT JOIN users b ON a.user_penyewa_id = b.id
                                LEFT JOIN kos c ON a.kos_id = c.id

                                LEFT JOIN ( SELECT COUNT(id) AS status_bayar,
                                                    tgl_jatuh_tempo,
                                                    sewa_id
                                            FROM bayar_sewa
                                            WHERE `status` = 'BBV') d ON a.tgl_jatuh_tempo = d.tgl_jatuh_tempo AND a.id = d.sewa_id

                                LEFT JOIN ( SELECT `status`,
                                            tgl_jatuh_tempo,
                                            sewa_id
                                            FROM bayar_sewa) e ON a.tgl_jatuh_tempo = e.tgl_jatuh_tempo AND a.id = e.sewa_id

                                WHERE (a.`status` = 'AKT' OR a.`status` = 'BOK')
                                    AND a.user_penyewa_id = $user_id
                                    AND IFNULL(d.status_bayar,0) = 0
                                    AND TIMESTAMPDIFF(DAY, DATE(a.tgl_jatuh_tempo),DATE(NOW())) > -7");

            echo json_encode(
                array(
                    'error'     => false,
                    'error_msg' => 'Data berhasil diambil',
                    'dataList'  => $sewa,
                )
            );

        }
    }

    public function booking(Request $request)
    {
        $kos_id = $request->kos_id;

        $user_id = auth('api')->user()->id;
        //$user = User::where('id', '=', $user_id)->first();

        $kos = Kos::where("id", "=", $kos_id)->first();

        $this->validate($request, [
            'bulan_sewa' => 'required',
        ]);

        // $user_id = Session::get('user_id');
        $tanggal = date('Y-m-d H:i:s');

        Sewa::create([
            'user_penyewa_id' => $user_id,
            'kos_id'          => $kos->id,
            'tgl_sewa'        => $tanggal,
            'tgl_jatuh_tempo' => $tanggal,
            'nama_kamar'      => 'BOOKING',
            'bulan_sewa'      => $request->bulan_sewa,
            'harga'           => ($request->bulan_sewa * $kos->harga_sewa),
            'status'          => 'BOK',
        ]);

        $response["error"]     = false;
        $response["error_msg"] = "Booking berhasil dilakukan";

        echo json_encode($response);

    }

    public function loadDetailKos(Request $request)
    {
        $kos_id = $request->kos_id;

        $kos = Kos::where('id', '=', $kos_id)->first();

        $response["error"] = false;

        $response["kos"]["nama"]       = $kos->nama;
        $response["kos"]["alamat"]     = $kos->email;
        $response["kos"]["telp"]       = $kos->telp;
        $response["kos"]["foto"]       = $kos->foto;
        $response["kos"]["tipe"]       = $kos->tipe;
        $response["kos"]["fasilitas"]  = $kos->fasilitas;
        $response["kos"]["deskripsi"]  = $kos->deskripsi;
        $response["kos"]["jml_kamar"]  = $kos->jml_kamar;
        $response["kos"]["harga_sewa"] = $kos->harga_sewa;
        $response["kos"]["wilayah"]    = $kos->wilayah;
        $response["kos"]["tipe"]       = $kos->tipe;

        echo json_encode($response);

    }

    public function getKecamatan()
    {
        header('content-type: application/json');

        // $kecamatan = Wilayah::all();
        $kab = "35.10."; //KAB. BANYUWANGI

        $kecamatan = Wilayah::where('kode', 'like', $kab . '%')
            ->whereRaw('CHAR_LENGTH(kode) = 8')
            ->orderBy('nama', 'ASC')->get();

        echo json_encode(
            array(
                'error'     => false,
                'error_msg' => 'Data berhasil diambil',
                'dataList'  => $kecamatan,
            )
        );
    }

    public function getFasilitasList(Request $request)
    {
        $fasilitas = $request->fasilitas_list;

        $fas = DB::select("SELECT nama FROM fasilitas WHERE id IN ($fasilitas)");

        // $replace = array(
        //     '{"nama":"' => '',
        //     '"'         => '',
        //     '}'         => '',
        // );

        $fasilitas_nama = "Fasilitas :\n\r";
        foreach ($fas as $f) {
            $fasilitas_nama .= $f->nama . "\n\r";
        }

        echo json_encode(
            array(
                'error'     => false,
                'error_msg' => 'Data berhasil diambil',
                'fasilitas' => substr($fasilitas_nama, 0, -1),
            )
        );

    }

    public function getKelurahan(Request $request)
    {
        // $kec = urldecode($kec);

        $kec = $request->kec_kode;

        $kelurahan = Wilayah::where('kode', 'like', $kec . '.%')
            ->whereRaw('CHAR_LENGTH(kode) = 13')
            ->orderBy('nama', 'ASC')->get();

        // $response["error"] = 0;
        // $response["msg"]   = "Data berhasil diambil";
        // $response["data"]  = $kel;

        // echo json_encode($response);
        echo json_encode(
            array(
                'error'     => false,
                'error_msg' => 'Data berhasil diambil',
                'dataList'  => $kelurahan,
            )
        );
    }

    public function updateRating(Request $request)
    {
        header('content-type: application/json');

        $sewa_id         = $request->sewa_id;
        $rating_nilai    = $request->rating_nilai;
        $rating_komentar = $request->rating_komentar;

        $sewa = Sewa::where('id', '=', $sewa_id)->first();

        $sewa->rating_nilai    = $rating_nilai;
        $sewa->rating_komentar = $rating_komentar;

        $sewa->save();

        $response["error"]     = false;
        $response["error_msg"] = "Rating berhasil diupdate";

        echo json_encode($response);
    }

    public function updateDataKos(Request $request)
    {
        header('content-type: application/json');

        $kos_id = $request->id;

        if ($kos_id > 0) {
            //edit

            $kos = Kos::where('id', '=', $kos_id)->first();

            $kos->nama    = $request->nama;
            $kos->alamat  = $request->alamat;
            $kos->wilayah = $request->wilayah;
            $kos->telp    = $request->telp;

            $arrTipe   = array('KOS CAMPUR' => 'CMP', 'KOS PUTRA' => 'PTR', 'KOS PUTRI', 'PUT');
            $kos->tipe = $arrTipe[$request->tipe];

            $kos->fasilitas  = $request->fasilitas;
            $kos->deskripsi  = $request->deskripsi;
            $kos->jml_kamar  = $request->jml_kamar;
            $kos->harga_sewa = $request->harga_sewa;

            if ($request->has('foto')) {

                $image = $request->foto; // your base64 encoded

                if ($image !== "NO_IMAGE") {

                    $image = str_replace('data:image/png;base64,', '', $image);
                    $image = str_replace(' ', '+', $image);

                    $imageName = 'kos-' . $kos_id . '-' . date('Y-m-d') . '.jpg';
                    file_put_contents('uploads/' . $imageName, base64_decode($image));
                    $kos->foto = $imageName;
                }
            }

            $kos->save();

            $response["error"]     = false;
            $response["error_msg"] = "Data berhasil diupdate";

            echo json_encode($response);
        } else {
            //create new

            $validator = Validator::make($request->all(), [
                'nama'       => 'required|unique:kos,nama',
                'jml_kamar'  => 'required',
                'alamat'     => 'required',
                'telp'       => 'required',
                'fasilitas'  => 'required',
                'deskripsi'  => 'required',
                'harga_sewa' => 'required',
            ]);

            if ($validator->fails()) {

                $error   = $validator->errors();
                $replace = array(
                    '{'            => '',
                    '}'            => '',
                    '['            => '',
                    ']'            => '',
                    ':'            => '',
                    '"'            => '',
                    '"nama"'       => '',
                    '"jml_kamar"'  => '',
                    '"alamat"'     => '',
                    '"telp"'       => '',
                    '"fasilitas"'  => '',
                    '"deskripsi"'  => '',
                    '"harga_sewa"' => '',
                );

                $response["error"]     = true;
                $response["error_msg"] = strtr($error, $replace);

                echo json_encode($response);

            } else {

                $kos = new Kos;

                $kos->user_pemilik_id = auth('api')->user()->id;

                $kos->nama    = $request->nama;
                $kos->alamat  = $request->alamat;
                $kos->wilayah = $request->wilayah;
                $kos->telp    = $request->telp;

                $arrTipe   = array('KOS CAMPUR' => 'CMP', 'KOS PUTRA' => 'PTR', 'KOS PUTRI', 'PUT');
                $kos->tipe = $arrTipe[$request->tipe];

                $kos->fasilitas  = $request->fasilitas;
                $kos->deskripsi  = $request->deskripsi;
                $kos->jml_kamar  = $request->jml_kamar;
                $kos->harga_sewa = $request->harga_sewa;

                if ($request->has('foto')) {

                    $image = $request->foto; // your base64 encoded

                    if ($image !== "NO_IMAGE") {

                        $image = str_replace('data:image/png;base64,', '', $image);
                        $image = str_replace(' ', '+', $image);

                        $imageName = 'kos-' . $kos_id . '-' . date('Y-m-d') . '.png';
                        file_put_contents('uploads/' . $imageName, base64_decode($image));
                        $kos->foto = $imageName;
                    }
                }

                $kos->lat = $request->lat;
                $kos->lng = $request->lng;

                $kos->save();

                $response["error"]     = false;
                $response["error_msg"] = "Data berhasil dimasukkan";

                echo json_encode($response);
            }
        }
    }

    public function updateDataProfile(Request $request)
    {
        header('content-type: application/json');

        $user_id = auth('api')->user()->id;

        $user         = User::where('id', '=', $user_id)->first();
        $user->nama   = $request->nama;
        $user->telp   = $request->telp;
        $user->alamat = $request->alamat;

        $user->ktp_nomor     = $request->ktp_nomor;
        $user->rekening_bank = $request->nama_bank . '|' . $request->no_rekening . '|' . $request->nama_pemilik_rekening;

        if ($request->has('foto')) {

            $image = $request->foto; // your base64 encoded

            if ($image !== "NO_IMAGE") {

                $image = str_replace('data:image/png;base64,', '', $image);
                $image = str_replace(' ', '+', $image);

                $imageName = 'foto-' . $user_id . '-' . date('Y-m-d') . '.jpg';
                file_put_contents('uploads/' . $imageName, base64_decode($image));
                $user->foto = $imageName;

            }
        }

        if ($request->has('ktp_foto')) {

            $image = $request->ktp_foto; // your base64 encoded

            if ($image !== "NO_IMAGE") {

                $image = str_replace('data:image/png;base64,', '', $image);
                $image = str_replace(' ', '+', $image);

                $imageName = 'ktp-' . $user_id . '-' . date('Y-m-d') . '.png';
                file_put_contents('uploads/' . $imageName, base64_decode($image));
                $user->ktp_foto = $imageName;

            }
        }

        $user->save();

        $response["error"]     = false;
        $response["error_msg"] = "Data berhasil diupdate";

        $user                 = User::where('id', '=', $user_id)->first();
        $response["foto"]     = $user->foto;
        $response["ktp_foto"] = $user->ktp_foto;

        echo json_encode($response);
    }

    public function getKosList(Request $request)
    {

        header('content-type: application/json');

        $filter     = $request->filter; //kecamatan|kelurahan|harga_max|tipe
        $exp_filter = explode("|", $filter);

        $kecamatan = ($exp_filter[0] === "00.00.00" ? "All" : $exp_filter[0]);
        $kelurahan = ($exp_filter[1] === "00.00.00.0000" ? "All" : $exp_filter[1]);
        $tipe_kos  = ($exp_filter[2] === "All" ? "All" : $exp_filter[2]);
        $harga_max = ($exp_filter[3] === "All" ? "All" : $exp_filter[3]);

        //kecamatan|kelurahan|max_harga
        /**
         * if(kelurahan === 'ALL'){
         *  if(kecamatan === 'ALL ){
         *     search semua kos
         *  }else{
         *     cari kos dengan kecamatan
         *  }
         * }else{
         *   cari kos dengan kecamatan dan kelurahan
         * }
         *
         */

        $user_id = auth('api')->user()->id;

        $user = User::where('id', '=', $user_id)->first();

        if ($user->level === 'pemilik') {

            $kos = DB::table(DB::raw('kos a'))
                ->selectRaw('a.id,a.nama,a.alamat,b.nama AS kecamatan,c.nama AS kelurahan,a.tipe,
                         a.fasilitas,a.deskripsi,a.harga_sewa,a.lng,a.lat, a.foto,
                         IFNULL(ROUND(AVG(d.rating_nilai),0),0) AS rating,
                         IFNULL(e.kmr_terisi,0) AS kmr_terisi,
                         (a.jml_kamar - IFNULL(e.kmr_terisi,0)) AS kmr_tersisa')
                ->leftJoin(DB::raw('wilayah b'), DB::raw('SUBSTRING(a.wilayah,1,8)'), '=', 'b.kode')
                ->leftJoin(DB::raw('wilayah c'), 'a.wilayah', '=', 'c.kode')
                ->leftJoin(DB::raw('sewa d'), 'a.id', '=', 'd.kos_id')
                ->leftJoin(DB::raw(" (SELECT kos_id,COUNT(id) AS kmr_terisi
                                  FROM sewa WHERE `status` = 'AKT'
                                  GROUP BY kos_id) e"),
                    function ($join) {
                        $join->on('a.id', '=', 'e.kos_id');
                    });

            if ($kelurahan === 'All') {
                if ($kecamatan !== 'All') {
                    $kos->where('b.kode', '=', $kecamatan);
                }
            } else {
                $kos->where('b.kode', '=', $kecamatan);
                $kos->where('c.kode', '=', $kelurahan);
            }

            if ($harga_max !== 'All') {
                $kos->where('a.harga_sewa', '<=', $harga_max);
            }

            if ($tipe_kos !== 'All') {
                $kos->where('a.tipe', '=', $tipe_kos);
            }

            $kos->where('a.user_pemilik_id', '=', $user_id);
            $kos->groupBy('a.id');

            echo json_encode(
                array(
                    'error'     => false,
                    'error_msg' => 'Data berhasil diambil',
                    'dataList'  => $kos->get(),
                )
            );

        } else {

            $kos = DB::table(DB::raw('kos a'))
                ->selectRaw('a.id,a.nama,a.alamat,b.nama AS kecamatan,c.nama AS kelurahan,a.tipe,
                     a.fasilitas,a.deskripsi,a.harga_sewa,a.lng,a.lat, a.foto,
                     IFNULL(ROUND(AVG(d.rating_nilai),0),0) AS rating,
                     IFNULL(e.kmr_terisi,0) AS kmr_terisi,
                     (a.jml_kamar - IFNULL(e.kmr_terisi,0)) AS kmr_tersisa')
                ->leftJoin(DB::raw('wilayah b'), DB::raw('SUBSTRING(a.wilayah,1,8)'), '=', 'b.kode')
                ->leftJoin(DB::raw('wilayah c'), 'a.wilayah', '=', 'c.kode')
                ->leftJoin(DB::raw('sewa d'), 'a.id', '=', 'd.kos_id')
                ->leftJoin(DB::raw(" (SELECT kos_id,COUNT(id) AS kmr_terisi
                              FROM sewa WHERE `status` = 'AKT'
                              GROUP BY kos_id) e"),
                    function ($join) {
                        $join->on('a.id', '=', 'e.kos_id');
                    })
                ->leftJoin(DB::raw('users f'), 'a.user_pemilik_id', '=', 'f.id');

            if ($kelurahan === 'All') {
                if ($kecamatan !== 'All') {
                    $kos->where('b.kode', '=', $kecamatan);
                }
            } else {
                $kos->where('b.kode', '=', $kecamatan);
                $kos->where('c.kode', '=', $kelurahan);
            }

            if ($harga_max !== 'All') {
                $kos->where('a.harga_sewa', '<=', $harga_max);
            }

            if ($tipe_kos !== 'All') {
                $kos->where('a.tipe', '=', $tipe_kos);
            }

            $kos->whereRaw('(a.jml_kamar - IFNULL(e.kmr_terisi,0)) > 0');
            $kos->whereRaw('f.verifikasi = "Y"');

            $kos->groupBy('a.id');

            echo json_encode(
                array(
                    'error'     => false,
                    'error_msg' => 'Data berhasil diambil',
                    'dataList'  => $kos->get(),
                )
            );

        }

    }

    public function register(Request $request)
    {

        $validator = Validator::make($request->all(), [
            'nama'     => 'required',
            'email'    => 'required|email|unique:users,email',
            'password' => 'required|confirmed',
            'level'    => Rule::in(['pemilik', 'penyewa']), // option1 or option2 values
        ]);

        if ($validator->fails()) {

            $error   = $validator->errors();
            $replace = array(
                '{'          => '',
                '}'          => '',
                '['          => '',
                ']'          => '',
                ':'          => '',
                '"'          => '',
                '"email"'    => '',
                '"password"' => '',

            );

            $response["error"]     = true;
            $response["error_msg"] = strtr($error, $replace);

            echo json_encode($response);

        } else {

            try {

                $input                  = $request->all();
                $input['password']      = Hash::make($input['password']);
                $input['rekening_bank'] = $input['nama_bank'] . '|' . $input['nomor_rekening'] . '|' . $input['rekening_atas_nama'];
                $user                   = User::create($input);

                $to_email = $request->email;
                $to_name  = $request->nama;
                $data     = array('body' => 'selamat datang di rumahkos.com, untuk mengaktifkan akun anda, silahkan klik <a href="' . URL::to('/aktivasi-akun/' . md5($to_email)) . '">disini</a>');

                Mail::send('email.registration', $data, function ($message) use ($to_name, $to_email) {
                    $message->subject('Registrasi di rumahkos.com');
                    $message->to($to_email, $to_name);
                    $message->from('donotreply@rumahkos.com', 'Rumahkos.com');
                });

                $response["error"]     = false;
                $response["error_msg"] = "Register berhasil. Link aktivasi akun anda telah dikirimkan ke email";
                echo json_encode($response);

            } catch (\Throwable $th) {

                $response["error"]     = true;
                $response["error_msg"] = $th;
                echo json_encode($response);

            }

        }

    }

    public function login(Request $request)
    {

        header('content-type: application/json');

        if (Auth::attempt(['email' => request('email'), 'password' => request('password')])) {
            $user = Auth::user();

            $setting = Setting::where('nama', '=', "bank")->first();

            if ($user->status === 'AKT') {
                $response["error"] = false;

                $response["user"]["id"]             = $user->id;
                $response["user"]["nama"]           = $user->nama;
                $response["user"]["email"]          = $user->email;
                $response["user"]["telp"]           = $user->telp;
                $response["user"]["alamat"]         = $user->alamat;
                $response["user"]["foto"]           = $user->foto;
                $response["user"]["ktp_foto"]       = $user->ktp_foto;
                $response["user"]["ktp_nomor"]      = $user->ktp_nomor;
                $response["user"]["level"]          = $user->level;
                $response["user"]["rekening_admin"] = $setting->nilai; //$user->rekening_bank;
                $response["user"]["rekening"]       = $user->rekening_bank;
                $response["user"]['token']          = $user->createToken('nApp')->accessToken;
                $response["user"]["verifikasi"]     = $user->verifikasi;

                echo json_encode($response);
            } else {
                $response["error"]     = true;
                $response["error_msg"] = "Status akun anda tidak/belum aktif!\nHubungi admin untuk informasi";
                echo json_encode($response);
            }

        } else {
            //return response()->json(['error' => 'Unauthorised'], 401);
            $response["error"]     = true;
            $response["error_msg"] = "Periksa kembali username dan password anda";
            echo json_encode($response);
        }

    }

    public function resetPassword(Request $request)
    {
        header('content-type: application/json');

        $validator = Validator::make($request->all(), [
            'email' => 'required|email',
        ]);

        if ($validator->fails()) {
            $error   = $validator->errors();
            $replace = array(
                '{'       => '',
                '}'       => '',
                '['       => '',
                ']'       => '',
                ':'       => '',
                '"'       => '',
                '"email"' => '',

            );

            $response["error"]     = true;
            $response["error_msg"] = strtr($error, $replace);

            echo json_encode($response);
        } else {
            $email = $request->email;

            $user = User::where('email', $email)->first();

            if ($user->count() > 0) {

                $str_random        = Str::random(10);
                $user->reset_token = $str_random;

                $user->save();

                $to_email = $request->email;
                $to_name  = $request->nama;

                $data = array('body' => 'Anda telah meminta reset password di rumahkos.com, klik <a href="' . URL::to('/reset-password/' . $str_random) . '">disini</a> untuk melanjutkan');
                Mail::send('email.reset_password', $data, function ($message) use ($to_name, $to_email) {
                    $message->subject('Reset Password @rumahkos.com');
                    $message->to($to_email, $to_name);
                    $message->from('donotreply@rumahkos.com', 'Rumahkos.com');
                });

                $response["error"]     = false;
                $response["error_msg"] = "Reset link dikirimkan ke email";

                echo json_encode($response);

            }
        }
    }

    public function checkActiveSewa(Request $request)
    {

        $sewa_id = $request->sewa_id;
        $cek     = Sewa::where('id', '=', $sewa_id)->where('status', '=', 'AKT')->count();

        if ($cek > 0) {

            $sewa = Sewa::where('id', '=', $sewa_id)->first();

            $response["error"] = false;

            $response["sewa"]["nama_kos"]        = $sewa->kos->nama;
            $response["sewa"]["nama_kamar"]      = $sewa->nama_kamar;
            $response["sewa"]["nama_penyewa"]    = $sewa->penyewa->nama;
            $response["sewa"]["tgl_jatuh_tempo"] = $sewa->tgl_jatuh_tempo;
            $response["sewa"]["bulan_tagihan"]   = $sewa->bulan_sewa;
            $response["sewa"]["nominal"]         = $sewa->harga;

            echo json_encode($response);

        } else {
            $response["error"]     = true;
            $response["error_msg"] = "Data sewa aktif untuk kode ini tidak ditemukan";
            echo json_encode($response);
        }

    }

    public function sendFirebaseToken(Request $request)
    {

        $user_id = auth('api')->user()->id;

        $token_id = $request->token_id;

        DB::table('users')
            ->where('id', $user_id)
            ->update(['firebase_token' => $token_id]);

        $response["error"]     = false;
        $response["error_msg"] = "Send Token BERHASIL";

        echo json_encode($response);

    }
}
