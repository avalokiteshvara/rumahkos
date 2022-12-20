<?php

namespace App\Http\Controllers;

use App\Helpers\HelpersFunction;
use App\Models\BayarSewa;
use App\Models\Kos;
use App\Models\MutasiDana;
use App\Models\Sewa;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\URL;
use Illuminate\Support\Facades\Session;

class AdminController extends Controller
{

    public function __construct()
    {
        $this->middleware('admin');
        date_default_timezone_set('Asia/Jakarta');
    }

    public function index()
    {   
        $aset = DB::select("SELECT CONCAT(IFNULL(lng,'-') ,'|',IFNULL(lat,'-')) AS lokasi,nama
                            FROM kos");
        $aset_location = "";
        $i             = 1;
        foreach ($aset as $a) {
            if ($a->lokasi !== '-|-') {

                $arr = explode('|', $a->lokasi);

                $aset_location .= "[";
                $aset_location .= "'" . $a->nama . "',";
                $aset_location .= $arr[1] . ",";
                $aset_location .= $arr[0] . ",";
                $aset_location .= $i;
                $aset_location .= "],";
            }
        }

        return view('admin.index', ['aset_location' => $aset_location]);
    }

    public function dataPemilikKos()
    {
        $user = User::where('level', '=', 'pemilik')->get();

        return view('admin.pemilik.list', ['data' => $user]);
    }

    public function dataPenyewaKos()
    {
        $user = User::where('level', '=', 'penyewa')->get();

        return view('admin.penyewa.list', ['data' => $user]);
    }

    public function dataRumahKos()
    {
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

                            GROUP BY a.id");

        return view('admin.kos.list', ['data' => $kos]);
    }

    public function mutasiDana()
    {
        $mutasi = DB::select("
                SELECT a.id,a.created_at,
                       a.user_id, a.rekening_bank,a.nominal,
                       b.trans_in,
                       c.trans_out,
                       (SUM(b.trans_in) - SUM(c.trans_out)) AS saldo,
                       a.keterangan
                FROM mutasi_dana a

                LEFT JOIN (SELECT SUM(nominal) AS trans_in,user_id
                            FROM mutasi_dana
                            WHERE `status` = 'IN'
                            GROUP BY user_id)  b ON a.user_id = b.user_id

                LEFT JOIN (SELECT SUM(nominal) AS trans_out,user_id
                            FROM mutasi_dana
                            WHERE `status` = 'OUT'
                            GROUP BY user_id)  c ON a.user_id = c.user_id

                WHERE a.`status` = 'PND'
                GROUP BY a.user_id,b.user_id,c.user_id");

        return view('admin.validasi.mutasi_dana_list', ['data' => $mutasi]);
    }

    public function mutasiDiproses($id)
    {
        $mutasi         = MutasiDana::whereRaw("sha1(id) = ?",[$id])->first();
        $mutasi->status = 'OUT';
        $mutasi->save();

        $tanggal = date('Y-m-d H:i:s');
        HelpersFunction::simpanNotifikasi($mutasi->user_id, "Permohonan penarikan dana disetujui. Proses dilakukan max 1x24 jam dari waktu disetujui");

        return redirect(URL::to('/admin/mutasi-dana'));
    }

    public function buktiBayarValid($id)
    {

        $bayar = BayarSewa::whereRaw("sha1(id) = ?",[$id])->first();
        $sewa = Sewa::where('id', '=', $bayar->sewa_id)->first();       

        $sewa->tgl_jatuh_tempo = Carbon::createFromFormat('Y-m-d H:i:s', $sewa->tgl_jatuh_tempo)->addMonth($sewa->bulan_sewa);
        $sewa->status          = 'AKT';
        $sewa->save();

        $bayar->diverifikasi_oleh = 'ADM';
        $bayar->status            = 'BBV';
        $bayar->save();

        $kos = Kos::where('id', '=', $sewa->kos_id)->first();

        MutasiDana::create([
            'user_id'    => $kos->user_pemilik_id,
            'nominal'    => $sewa->harga,
            'status'     => 'IN',
            'keterangan' => 'Penerimaan dari Pembayaran Kos ' .  $sewa->kos->nama . '::' .  $sewa->nama_kamar ." atas nama " . $sewa->penyewa->nama
        ]);

        $tanggal = date('Y-m-d H:i:s');
        HelpersFunction::simpanNotifikasi($sewa->user_penyewa_id, "Bukti pembayaran untuk kamar : $sewa->nama_kamar BERHASIL divalidasi");

        return redirect(URL::to('/admin/validasi-pembayaran-sewa'));

    }


    public function verifikasiDiterima($id)
    {
        $user = User::where('id', '=', $id)->first();
        $user->verifikasi = 'Y';
        $user->save();
        
        HelpersFunction::simpanNotifikasi($user->id, "ACTION::LOGOUT|REASON::VERIFICATION_ACCEPTED");

        if($user->level === 'pemilik'){
            return redirect(URL::to('/admin/data-pemilik-kos'));
        }else{
            return redirect(URL::to('/admin/data-penyewa-kos'));
        }
    }

    public function verifikasiDitolak($id)
    {
        $user = User::where('id', '=', $id)->first();
        $user->verifikasi = 'N';
        $user->save();
        
        HelpersFunction::simpanNotifikasi($user->id, "Akun anda GAGAL diverifikasi !\nSilahkan mengunggah kembali foto diri dan foto KTP");

        if($user->level === 'pemilik'){
            return redirect(URL::to('/admin/data-pemilik-kos'));
        }else{
            return redirect(URL::to('/admin/data-penyewa-kos'));
        }
    }


    public function buktiBayarInvalid($id)
    {

        $bayar = BayarSewa::whereRaw("sha1(id) = ?",[$id])->first();
        $sewa = Sewa::where('id', '=', $bayar->sewa_id)->first();  
      
        $bayar->status            = 'BBI';
        $bayar->save();

        $tanggal = date('Y-m-d H:i:s');
        HelpersFunction::simpanNotifikasi($sewa->user_penyewa_id, "Bukti pembayaran untuk kamar : $sewa->nama_kamar GAGAL divalidasi");

        return redirect(URL::to('/admin/validasi-pembayaran-sewa'));

    }

    public function profile(Request $request){

        $user_id = Session::get('user_id');

        switch ($request->method()) {
            case 'POST':

                $this->validate($request, [
                    'nama'       => 'required',                   
                    'telp'       => 'required',                  
                ]);
                
                $user = User::where('id','=',$user_id)->first();
                $user->telp = $request->telp;    
                $user->save();                

                return redirect(URL::to('/admin/profile'));

                break;
            case 'GET':

               
                $user = User::where('id','=',$user_id)->first();
        
                return view('admin.profile', ['data' => $user]);
               
                break;

            default:
                # code...
                break;
        }


       
    }

    public function validasiPembayaranSewa()
    {
        //BayarSewa::where('')
        $bayar = DB::select("
                        SELECT a.id,a.tgl_jatuh_tempo,a.rekening_bank,
                               a.foto_pembayaran,a.tgl_bayar,b.harga
                        FROM bayar_sewa a
                        LEFT JOIN sewa b ON a.sewa_id = b.id
                        WHERE a.`status` = 'BBU'");

        return view('admin.validasi.bayar_sewa_list', ['data' => $bayar]);
    }
}
