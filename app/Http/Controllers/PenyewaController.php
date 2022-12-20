<?php

namespace App\Http\Controllers;

use App\Helpers\HelpersFunction;
use App\Models\BayarSewa;
use App\Models\Kos;
use App\Models\Sewa;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Session;
use Illuminate\Support\Facades\URL;

class PenyewaController extends Controller
{
    public function __construct()
    {
        $this->middleware('penyewa');
        date_default_timezone_set('Asia/Jakarta');
    }

    public function index()
    {
        return view('penyewa.index');
    }

    public function booking($id, Request $request)
    {

        switch ($request->method()) {
            case 'POST':

                $kos = Kos::whereRaw("sha1(id) = ?", [$id])->first();

                $this->validate($request, [
                    'bulan_sewa' => 'required',
                ]);

                $user_id = Session::get('user_id');
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

                // TODO: save notifikasi

                return redirect(URL::to('/penyewa/tagihan-sewa'));

                break;
            case 'GET':
                // HelperFunctions::test();

                $kos = Kos::whereRaw("sha1(id) = ?", [$id])->first();
                return view('penyewa.booking', ['kos_id' => $id, 'nominal' => $kos->harga_sewa]);

                break;

            default:
                # code...
                break;
        }

    }

    public function cariRumahKos()
    {
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
            ->groupBy('a.id')
            ->paginate(10);

        return view('penyewa.kos.list', ['kos' => $kos]);
    }


    public function test(){
        $sewa = Sewa::where('id','=',11)->first();
        echo $sewa->kos->nama;
    }

    public function formBayar($id, Request $request)
    {
        switch ($request->method()) {
            case 'POST':

                $sewa = Sewa::whereRaw("sha1(id) = ?", [$id])->first();

                $this->validate($request, [
                    'bank_nama'          => 'required',
                    'bank_rekening'      => 'required',
                    'bank_nama_pengirim' => 'required',
                    'nominal'            => 'required|numeric|min:' . $sewa->harga . '|max:' . $sewa->harga,
                    'foto_pembayaran'    => 'required|file|image|mimes:jpeg,png,jpg|max:1028',
                ]);

                if ($request->hasfile('foto_pembayaran')) {
                    $file      = $request->file('foto_pembayaran');
                    $extension = $file->getClientOriginalExtension();
                    $filename  = time() . '.' . $extension;
                    $file->move('uploads/', $filename);
                    $request->foto_pembayaran = $filename;
                }

                // BayarSewa::create([
                //     'sewa_id'         => $sewa->id,
                //     'tgl_jatuh_tempo' => $sewa->tgl_jatuh_tempo,
                //     'foto_pembayaran' => $request->foto_pembayaran,
                //     'rekening_bank'   => $request->bank_nama . '|' . $request->bank_rekening . '|' . $request->bank_nama_pengirim,
                //     'tgl_bayar'       => date('Y-m-d H:i:s'),
                //     'status'          => 'BBU',
                // ]);

                BayarSewa::updateOrCreate(
                    [
                        'sewa_id'         => $sewa->id,
                        'tgl_jatuh_tempo' => $sewa->tgl_jatuh_tempo,
                    ],
                    [
                        'foto_pembayaran' => $request->foto_pembayaran,
                        'rekening_bank'   => $request->bank_nama . '|' . $request->bank_rekening . '|' . $request->bank_nama_pengirim,
                        'tgl_bayar'       => date('Y-m-d H:i:s'),
                        'status'          => 'BBU',
                    ]);

                $user_id = Session::get('user_id');
                // $tanggal = date('Y-m-d H:i:s');
                HelpersFunction::simpanNotifikasi($sewa->kos->user_pemilik_id, "Bukti pembayaran untuk " . $sewa->kos->nama . "::" . $sewa->nama_kamar ." atas nama " . $sewa->penyewa->nama ." telah diupload ! Menunggu verifikasi dari sistem  )");
                HelpersFunction::simpanNotifikasi($user_id, "Bukti pembayaran untuk " . $sewa->kos->nama . "::" . $sewa->nama_kamar ." atas nama " . $sewa->penyewa->nama ." telah kami terima ! Menunggu verifikasi dari sistem  )");

                return redirect(URL::to('/penyewa/tagihan-sewa'));

                break;
            case 'GET':
                // HelperFunctions::test();

                $sewa    = Sewa::whereRaw("sha1(id) = ?", [$id])->first();
                $setting = DB::select("SELECT nilai FROM setting WHERE nama='bank'")[0];

                return view('penyewa.tagihan.form_bayar', [
                    'sewa_id'        => $id,
                    'nominal'        => $sewa->harga,
                    'rekening_bayar' => $setting->nilai]
                );

                break;

            default:
                # code...
                break;
        }
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
                            WHERE a.`status` = 'BBV' AND b.user_penyewa_id = $user_id");

        return view('penyewa.tagihan.riwayat', ['data' => $sewa]);
    }

    public function tagihanSewa()
    {
        // $month   = date('m');
        $user_id = Session::get('user_id');

        $sewa = DB::select("   SELECT a.id,
                                      a.nama_kamar,
                                      a.tgl_sewa,
                                      a.tgl_jatuh_tempo,
                                      a.bulan_sewa,
                                      a.harga AS harga_total,
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

                                WHERE (a.`status` = 'AKT' OR a.`status` = 'BOK')
                                    AND a.user_penyewa_id = $user_id
                                    AND IFNULL(d.status_bayar,0) = 0
                                    AND TIMESTAMPDIFF(DAY, DATE(a.tgl_jatuh_tempo),DATE(NOW())) > -7");

        return view('penyewa.tagihan.list', ['data' => $sewa]);
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
                    'foto'      => 'required|file|image|mimes:jpeg,png,jpg|max:1028',
                    'ktp_foto'  => 'required|file|image|mimes:jpeg,png,jpg|max:1028',
                ]);

                $user            = User::where('id', '=', $user_id)->first();
                $user->telp      = $request->telp;
                $user->ktp_nomor = $request->ktp_nomor;

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

                return redirect(URL::to('/penyewa/profile'));

                break;
            case 'GET':

                $user = User::where('id', '=', $user_id)->first();

                return view('penyewa.profile', ['data' => $user]);

                break;

            default:
                # code...
                break;
        }

    }

}
