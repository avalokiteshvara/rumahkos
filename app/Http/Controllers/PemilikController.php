<?php

namespace App\Http\Controllers;

use App\Helpers\HelpersFunction;
use App\Models\BayarSewa;
use App\Models\Fasilitas;
use App\Models\Kos;
use App\Models\MutasiDana;
use App\Models\Sewa;
use App\Models\User;
use App\Models\Wilayah;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Session;
use Illuminate\Support\Facades\URL;

class PemilikController extends Controller
{

    // use HelperFunctions;

    public function __construct()
    {
        $this->middleware('pemilik');
        date_default_timezone_set('Asia/Jakarta');
    }

    public function index()
    {
        return view('pemilik.index');
    }

    public function getKelurahan(Request $request)
    {
        $kec = $request->kec;

        $html = "<option value=''>Pilih Kelurahan</option>";

        $row = Wilayah::where('kode', 'like', $kec . '.%')
            ->whereRaw('CHAR_LENGTH(kode) = 13')->get();

        foreach ($row as $r) {
            $html .= "<option value='$r->kode'>$r->nama</option>";
        }

        echo $html;

    }

    public function riwayatTagihanSewa()
    {
        // $month   = date('m');
        $user_id = Session::get('user_id');

        $sewa = DB::select("SELECT a.tgl_jatuh_tempo,a.tgl_bayar,
                                    b.nama_kamar,b.bulan_sewa,b.harga,
                                    c.nama AS nama_kos
                            FROM bayar_sewa a
                            LEFT JOIN sewa b ON a.sewa_id = b.id
                            LEFT JOIN kos c ON b.kos_id = c.id
                            WHERE a.`status` = 'BBV' AND c.user_pemilik_id = $user_id");

        return view('pemilik.tagihan.riwayat', ['data' => $sewa]);
    }

    public function updateLokasi(Request $request)
    {

        $latLng = explode('|', $request->latLng);

        $kos      = Kos::where('id', $request->kos_id)->first();
        $kos->lat = $latLng[0];
        $kos->lng = $latLng[1];
        $kos->save();

    }

    public function rumahKosHapus($id)
    {
        $kos = Kos::whereRaw("sha1(id) = ?", [$id]);
        $kos->delete();
        return redirect(URL::to('/pemilik/rumah-kos'));
    }

    public function rumahKosTambah(Request $request)
    {
        switch ($request->method()) {
            case 'POST':

                $this->validate($request, [
                    'nama'        => 'required',
                    'alamat'      => 'required',
                    'wilayah'     => 'required',
                    'telp'        => 'required',
                    'tipe'        => 'required',
                    'fasilitas.*' => 'required',
                    'deskripsi'   => 'required',
                    'jml_kamar'   => 'required',
                    'harga_sewa'  => 'required',
                    'foto.*'      => 'image|mimes:jpeg,png,jpg,gif,svg|max:2024',
                ]);

                $input = $request->except(['_token', 'kecamatan']);
                // var_dump($input);
                //$kos   = Kos::create($input);
                $kos                  = new Kos();
                $kos->user_pemilik_id = $input['user_pemilik_id'];
                $kos->nama            = $input['nama'];
                $kos->alamat          = $input['alamat'];
                $kos->wilayah         = $input['wilayah'];
                $kos->telp            = $input['telp'];
                $kos->tipe            = $input['tipe'];
                $kos->fasilitas       = implode(",", $input['fasilitas']); //$input['fasilitas'];
                $kos->deskripsi       = $input['deskripsi'];
                $kos->jml_kamar       = $input['jml_kamar'];
                $kos->harga_sewa      = $input['harga_sewa'];
                $kos->lng             = $input['lng'];
                $kos->lat             = $input['lat'];

                if ($request->hasfile('foto')) {

                    $foto_list = "";
                    foreach ($request->file('foto') as $image) {
                        $ext      = $image->getClientOriginalExtension();
                        $filename = mt_rand() . '.' . $ext;
                        $image->move('uploads/', $filename);
                        $foto_list .= $filename . ',';
                    }

                    $kos->foto = substr($foto_list, 0, -1);

                    $kos->save();
                } else {
                    $kos->save();
                }

                return redirect(URL::to('/pemilik/rumah-kos'));

                break;
            case 'GET':

                $kab = "35.10."; //KAB. BANYUWANGI

                $kec = Wilayah::where('kode', 'like', $kab . '%')
                    ->whereRaw('CHAR_LENGTH(kode) = 8');

                $kecOptions = array('' => 'Pilih Kecamatan') + $kec->pluck('nama', 'kode')->toArray();

                $fasilitas = Fasilitas::get();

                return view('pemilik.kos.tambah', ['kec' => $kecOptions, 'fasilitas' => $fasilitas]);

                break;

            default:
                # code...
                break;
        }
    }

    public function deleteImage($kos_id, $image_name)
    {
        $kos      = Kos::whereRaw("sha1(id) = ?", [$kos_id])->first();
        $old_list = explode(',', $kos->foto);
        $new_list = "";
        foreach ($old_list as $foto) {
            if ($foto != $image_name) {
                $new_list .= $foto . ',';
            }
        }

        if (substr($new_list, 0, -1) == '0') {
            $kos->foto = "";
        } else {
            $kos->foto = substr($new_list, 0, -1);
        }

        $kos->save();

        return redirect(URL::to('/pemilik/rumah-kos-edit/' . sha1($kos->id)));
    }

    public function rumahKosEdit($id, Request $request)
    {
        switch ($request->method()) {
            case 'POST':

                $this->validate($request, [
                    'nama'        => 'required',
                    'alamat'      => 'required',
                    'wilayah'     => 'required',
                    'telp'        => 'required',
                    'tipe'        => 'required',
                    'fasilitas.*' => 'required',
                    'deskripsi'   => 'required',
                    'jml_kamar'   => 'required',
                    'harga_sewa'  => 'numeric|required',
                    'foto.*'      => 'image|mimes:jpeg,png,jpg,gif,svg|max:2024',
                ]);

                $kos   = Kos::whereRaw("sha1(id) = ?", [$id])->first();
                $input = $request->except(['_token', 'kecamatan']);

                // $kos->user_pemilik_id = $input['user_pemilik_id'];
                $kos->nama       = $input['nama'];
                $kos->alamat     = $input['alamat'];
                $kos->wilayah    = $input['wilayah'];
                $kos->telp       = $input['telp'];
                $kos->tipe       = $input['tipe'];
                $kos->fasilitas  = implode(",", $input['fasilitas']); //$input['fasilitas'];
                $kos->deskripsi  = $input['deskripsi'];
                $kos->jml_kamar  = $input['jml_kamar'];
                $kos->harga_sewa = $input['harga_sewa'];
                $kos->lng        = $input['lng'];
                $kos->lat        = $input['lat'];

                $foto_list = $kos->foto;
                //$kos->update($request->except(['_token', 'kecamatan']));

                if ($request->hasfile('foto')) {

                    $foto_list .= ",";
                    foreach ($request->file('foto') as $image) {
                        $ext      = $image->getClientOriginalExtension();
                        $filename = mt_rand() . '.' . $ext;
                        $image->move('uploads/', $filename);
                        $foto_list .= $filename . ',';
                    }

                    if ($foto_list[0] == ',' || $foto_list[0] == '0') {
                        $foto_list = substr($foto_list, 1);
                    }

                    $kos->foto = substr($foto_list, 0, -1);

                    $kos->save();

                } else {
                    $kos->save();
                }

                return redirect(URL::to('/pemilik/rumah-kos'));

                break;
            case 'GET':

                $kos = Kos::whereRaw("sha1(id) =  ?", [$id])->first();

                $kab = "35.10."; //KAB. BANYUWANGI

                $kec = Wilayah::where('kode', 'like', $kab . '%')
                    ->whereRaw('CHAR_LENGTH(kode) = 8');

                $kecOptions = array('' => 'Pilih Kecamatan') + $kec->pluck('nama', 'kode')->toArray();

                $fasilitas = Fasilitas::get();
                return view('pemilik.kos.edit', ['data' => $kos, 'kec' => $kecOptions, 'kos_id' => $id, 'fasilitas' => $fasilitas, 'selected_fasilitas' => explode(",", $kos->fasilitas)]);

                break;

            default:
                # code...
                break;
        }
    }

    public function penyewaTambah($id, Request $request)
    {
        switch ($request->method()) {
            case 'POST':

                //create data user
                //create sewa berdasarkan user itu

                $this->validate($request, [
                    'nama'       => 'required',
                    'email'      => 'required|email|unique:users,email',
                    'telp'       => 'required',
                    'ktp_nomor'  => 'required',
                    'nama_kamar' => 'required',
                    'bulan_sewa' => 'required',
                    'harga'      => 'required',

                ]);

                $kos  = Kos::whereRaw("sha1(id) = ?", [$id])->first();
                $user = User::create([
                    'nama'      => $request->nama,
                    'email'     => $request->email,
                    'password'  => Hash::make($request->email),
                    'telp'      => $request->telp,
                    'ktp_nomor' => $request->ktp_nomor,
                    'level'     => 'penyewa',
                ]);

                Sewa::create([
                    'user_penyewa_id' => $user->id,
                    'kos_id'          => $kos->id,
                    'nama_kamar'      => $request->nama_kamar,
                    'tgl_sewa'        => date('Y-m-d H:i:s'),
                    'tgl_jatuh_tempo' => date('Y-m-d H:i:s'),
                    'bulan_sewa'      => $request->bulan_sewa,
                    'harga'           => $request->harga,
                    'status'          => 'AKT',

                ]);

                return redirect(URL::to('/pemilik/rumah-kos-penyewa/' . $id));

                break;
            case 'GET':

                $kos = Kos::whereRaw("sha1(id) = ?", [$id])->first();
                return view('pemilik.penyewa.tambah', ['kos_id' => $id, 'harga' => $kos->harga_sewa]);

                break;

            default:
                # code...
                break;
        }
    }

    public function permohonanPenarikanDana(Request $request)
    {
        $user_id = Session::get('user_id');

        $user     = User::where('id', '=', $user_id)->first();
        $rekening = explode('|', $user->rekening_bank);

        if (count($rekening) < 3) {
            $rekening = array("", "", "");
        }

        $mutasi_masuk = MutasiDana::where('user_id', '=', $user_id)
            ->where('status', '=', 'IN')
            ->get()->sum('nominal');

        $mutasi_keluar = MutasiDana::where('user_id', '=', $user_id)
            ->where('status', '=', 'OUT')
            ->get()->sum('nominal');

        $dana_tersisa = $mutasi_masuk - $mutasi_keluar;

        switch ($request->method()) {
            case 'POST':

                $this->validate($request, [

                    'bank_nama'     => 'required',
                    'bank_rekening' => 'required',
                    'bank_penerima' => 'required',
                    'nominal'       => 'required|numeric|min:10000|max:' . $dana_tersisa,

                ]);

                MutasiDana::create([
                    'user_id'       => $user_id,
                    'rekening_bank' => $request->bank_nama . '|' . $request->bank_rekening . '|' . $request->bank_penerima,
                    'nominal'       => $request->nominal,
                    'status'        => 'PND',
                    'keterangan'    => 'Permohonan penarikan dana ' . date('Y-m-d H:i:s'),
                ]);

                return redirect(URL::to('/pemilik/mutasi-dana'));

                break;
            case 'GET':

                return view('pemilik.mutasidana.tambah', ['rek_info' => $rekening]);

                break;

            default:
                # code...
                break;
        }
    }

    public function mutasiDana(Request $request)
    {

        $user_id = Session::get('user_id');

        $mutasi       = MutasiDana::where('user_id', '=', $user_id)->orderBy('updated_at', 'desc')->get();
        $mutasi_masuk = MutasiDana::where('user_id', '=', $user_id)
            ->where('status', '=', 'IN')
            ->get()->sum('nominal');

        $mutasi_keluar = MutasiDana::where('user_id', '=', $user_id)
            ->where('status', '=', 'OUT')
            ->get()->sum('nominal');

        return view('pemilik.mutasidana.list', ['data' => $mutasi, 'dana_tersisa' => $mutasi_masuk - $mutasi_keluar]);

    }

    public function tagihanSewa()
    {
        // $month   = date('m');
        $user_id = Session::get('user_id');

        $penyewa = DB::select(" SELECT  a.id,a.nama_kamar,a.tgl_sewa,a.tgl_jatuh_tempo,a.bulan_sewa,a.harga AS harga_total,
                                        b.nama AS nama_penyewa,
                                        IFNULL(d.status_bayar,0) AS status_bayar,
                                        c.nama AS nama_kos,
                                        TIMESTAMPDIFF(DAY, DATE(a.tgl_jatuh_tempo),DATE(NOW())) AS hari_jatuh_tempo,
                                        IFNULL(e.`status`,'NULL') AS kode_status_bayar
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

                                WHERE a.`status` = 'AKT'
                                      AND c.user_pemilik_id = $user_id
                                      AND IFNULL(d.status_bayar,0) = 0
                                      AND TIMESTAMPDIFF(DAY, DATE(a.tgl_jatuh_tempo),DATE(NOW())) > -7");
        return view('pemilik.tagihan.list', ['data' => $penyewa]);
    }

    public function rumahKosPenyewa($id)
    {
        $month = date('m');

        $penyewa = DB::select(" SELECT a.id,a.nama_kamar,DATE_FORMAT(DATE(a.tgl_sewa),'%d-%m-%Y') AS tgl_sewa,
                                       DATE_FORMAT(DATE(a.tgl_jatuh_tempo),'%d-%m-%Y') AS tgl_jatuh_tempo ,a.bulan_sewa,a.harga AS harga_total,
                                       b.nama AS nama_penyewa
                                FROM sewa a
                                LEFT JOIN users b ON a.user_penyewa_id = b.id
                                WHERE a.`status` = 'AKT' AND SHA(a.kos_id) = '$id'");

        return view('pemilik.penyewa.list', ['data' => $penyewa, 'kos_id' => $id]);
    }

    public function nonaktifkanSewa($id)
    {
        $sewa         = Sewa::whereRaw("sha1(id) = ?", [$id])->first();
        $sewa->status = 'NAK';
        $sewa->save();

        return redirect(URL::to('/pemilik/rumah-kos-penyewa/' . sha1($sewa->kos_id)));
    }

    public function tagihanLunas($id)
    {
        $sewa = Sewa::whereRaw("sha1(id) = ?", [$id])->first();

        // BayarSewa::create([
        //     'sewa_id'           => $sewa->id,
        //     'tgl_jatuh_tempo'   => $sewa->tgl_jatuh_tempo,
        //     'tgl_bayar'         => date('Y-m-d H:i:s'),
        //     'status'            => 'BBV',
        //     'diverifikasi_oleh' => 'PEM',
        // ]);

        BayarSewa::updateOrCreate(
            [
                'sewa_id'         => $sewa->id,
                'tgl_jatuh_tempo' => $sewa->tgl_jatuh_tempo,
            ],
            [
                'tgl_bayar'         => date('Y-m-d H:i:s'),
                'status'            => 'BBV',
                'diverifikasi_oleh' => 'PEM',
            ]);

        $sewa->tgl_jatuh_tempo = Carbon::createFromFormat('Y-m-d H:i:s', $sewa->tgl_jatuh_tempo)->addMonth($sewa->bulan_sewa);
        $sewa->status          = 'AKT';
        $sewa->save();
        //$bayar                    = BayarSewa::where('id','=',$sewa->id)->first();
        // $sewa->status            = '02';
        // $sewa->diverifikasi_oleh = 'pemilik';
        // $sewa->save();

        $user_id = Session::get('user_id');
        //$tanggal = date('Y-m-d H:i:s');
        HelpersFunction::simpanNotifikasi($user_id, "Pembayaran untuk " . $sewa->kos->nama . "::" . $sewa->nama_kamar . " atas nama " . $sewa->penyewa->nama . " Dilakukan secara langsung");
        HelpersFunction::simpanNotifikasi($sewa->penyewa->id, "Pembayaran untuk " . $sewa->kos->nama . "::" . $sewa->nama_kamar . " atas nama " . $sewa->penyewa->nama . " Dilakukan secara langsung");

        return redirect(URL::to('/pemilik/tagihan-sewa'));
    }

    public function rumahKos()
    {

        $user_id = Session::get('user_id');

        $kos = DB::select(" SELECT a.id,
                                   a.nama,
                                   a.alamat,
                                   b.nama AS kecamatan,
                                   c.nama AS kelurahan,
                                   a.tipe,
                                   a.fasilitas,
                                   a.deskripsi,
                                   a.harga_sewa,
                                   a.lng,
                                   a.lat,
                                   a.foto,
                                   IFNULL(ROUND(AVG(d.rating_nilai),0),0) AS rating,
                                   IFNULL(e.kmr_terisi,0) AS kmr_terisi,
		                           (a.jml_kamar - IFNULL(e.kmr_terisi,0)) AS kmr_tersisa
                            FROM kos a
                            LEFT JOIN wilayah b ON SUBSTRING(a.wilayah,1,8) = b.kode
                            LEFT JOIN wilayah c ON a.wilayah = c.kode
                            LEFT JOIN sewa d ON a.id = d.kos_id
                            LEFT JOIN (SELECT kos_id,COUNT(id) AS kmr_terisi
                                       FROM sewa WHERE `status` = 'AKT'
                                       GROUP BY kos_id) e ON a.id = e.kos_id
                            WHERE a.user_pemilik_id = $user_id
                            GROUP BY a.id");

        return view('pemilik.kos.list', ['data' => $kos]);
    }

    public function profile(Request $request)
    {

        $user_id = Session::get('user_id');

        switch ($request->method()) {
            case 'POST':

                $this->validate($request, [
                    'nama'      => 'required',
                    'telp'      => 'required',
                    'ktp_nomor' => 'required',
                    'foto'      => 'file|image|mimes:jpeg,png,jpg|max:1028',
                    'ktp_foto'  => 'file|image|mimes:jpeg,png,jpg|max:1028',
                ]);

                $user                = User::where('id', '=', $user_id)->first();
                $user->telp          = $request->telp;
                $user->ktp_nomor     = $request->ktp_nomor;
                $user->nama          = $request->nama;
                $user->rekening_bank = $request->nama_bank . '|' . $request->nomor_rekening . '|' . $request->rekening_atas_nama;

                if ($request->hasfile('foto')) {
                    $file      = $request->file('foto');
                    $extension = $file->getClientOriginalExtension();
                    $filename  = 'foto-' . time() . '.' . $extension;
                    $file->move('uploads/', $filename);
                    $user->foto = $filename;
                }

                if ($request->hasfile('ktp_foto')) {
                    $file      = $request->file('ktp_foto');
                    $extension = $file->getClientOriginalExtension();
                    $filename  = 'ktp-' . time() . '.' . $extension;
                    $file->move('uploads/', $filename);
                    $user->ktp_foto = $filename;
                }

                $user->save();

                return redirect(URL::to('/pemilik/profile'));

                break;
            case 'GET':

                $user = User::where('id', '=', $user_id)->first();
                $rekening = explode('|',$user->rekening_bank);

                if(count($rekening) < 0){
                    $rekening = array("","","");
                }

                return view('pemilik.profile', ['data' => $user,'rek_info' => $rekening]);

                break;

            default:
                # code...
                break;
        }

    }

}
