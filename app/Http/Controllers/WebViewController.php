<?php

namespace App\Http\Controllers;

use App\Models\MutasiDana;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\URL;

class WebViewController extends Controller
{

    public function getLocation($lat, $lng)
    {
        return view('webview.maps', ['lat' => $lat, 'lng' => $lng]);
    }

    public function getQrcode($id)
    {

        return view('webview.qrcode', ['kos_id' => $id]);
    }

    public function riwayatSewa($user_id, $user_level)
    {

        if ($user_level === 'pemilik') {
            $sewa = DB::select("SELECT a.id,c.id AS kos_id,
                                   c.nama AS nama_kos,
                                   a.nama_kamar,
                                   DATE_FORMAT(a.tgl_sewa,'%d-%m-%Y') AS tgl_sewa,
                                   DATE_FORMAT(a.tgl_keluar,'%d-%m-%Y') AS tgl_keluar,
                                   a.bulan_sewa,
                                   a.harga AS harga_total,
                                   b.nama AS nama_penyewa,
                                   a.`status`,
                                   c.foto,
                                   IFNULL(c.alamat,'-') AS alamat,
                                   c.deskripsi,
                                   a.rating_komentar,
                                   IFNULL(a.rating_nilai,0) AS rating_nilai,
                                   b.nama AS nama_penyewa
                            FROM sewa a
                            LEFT JOIN users b ON a.user_penyewa_id = b.id
                            LEFT JOIN kos c ON a.kos_id = c.id

                            WHERE (a.`status` = 'NAK') AND (c.user_pemilik_id = $user_id OR a.user_penyewa_id = $user_id)");

            return view('webview.riwayatsewa_pemilik', ['data' => $sewa]);
        } else {
            $sewa = DB::select("SELECT a.id,c.id AS kos_id,
                                   c.nama AS nama_kos,
                                   a.nama_kamar,
                                   DATE_FORMAT(a.tgl_sewa,'%d-%m-%Y') AS tgl_sewa,
                                   DATE_FORMAT(a.tgl_keluar,'%d-%m-%Y') AS tgl_keluar,
                                   a.bulan_sewa,
                                   a.harga AS harga_total,
                                   b.nama AS nama_penyewa,
                                   a.`status`,
                                   c.foto,
                                   IFNULL(c.alamat,'-') AS alamat,
                                   c.deskripsi,
                                   a.rating_komentar,
                                   IFNULL(a.rating_nilai,0) AS rating_nilai,
                                   b.nama AS nama_penyewa
                            FROM sewa a
                            LEFT JOIN users b ON a.user_penyewa_id = b.id
                            LEFT JOIN kos c ON a.kos_id = c.id

                            WHERE (a.`status` = 'NAK') AND (c.user_pemilik_id = $user_id OR a.user_penyewa_id = $user_id)");

            return view('webview.riwayatsewa_penyewa', ['data' => $sewa]);
        }

    }

    public function mutasiDana($user_id)
    {
        $mutasi       = MutasiDana::where('user_id', '=', $user_id)->orderBy('updated_at', 'desc')->get();
        $mutasi_masuk = MutasiDana::where('user_id', '=', $user_id)
            ->where('status', '=', 'IN')
            ->get()->sum('nominal');

        $mutasi_keluar = MutasiDana::where('user_id', '=', $user_id)
            ->where('status', '=', 'OUT')
            ->get()->sum('nominal');

        return view('webview.mutasidana.list', ['data' => $mutasi, 'dana_tersisa' => $mutasi_masuk - $mutasi_keluar, 'user_id' => $user_id]);

    }

    public function permohonanPenarikanDana($user_id, Request $request)
    {
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

                return redirect(URL::to('/webview/mutasi-dana/' . $user_id));

                break;
            case 'GET':

                return view('webview.mutasidana.tambah', ['user_id' => $user_id]);

                break;

            default:
                # code...
                break;
        }
    }
}
