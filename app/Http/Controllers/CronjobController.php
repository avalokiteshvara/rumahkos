<?php

namespace App\Http\Controllers;

use App\Models\Notifikasi;
use Illuminate\Support\Facades\DB;
use App\Models\Sewa;

class CronjobController extends Controller
{

    public function __construct()
    {
        date_default_timezone_set('Asia/Jakarta');
    }

    

    public function kirimNotifikasi($fcm_token, $title, $message, $id = null, $action = null)
    {

        $url    = "https://fcm.googleapis.com/fcm/send";
        $header = [
            'Authorization: key=AAAAnuA85cU:APA91bFxPHYgIXlhDMX0NDKQ94yC9eC3KecOGH_aod_-0sL4iAgbA7sK7kdfexjSNQRrAGRippWxDuoLWl6ym0oBr5ZDeoyBL1jKzr-zsbUAoJGj7Za8Ra3mjcT_TRl-ljePLWaVVMVr',
            'Content-Type: application/json',
        ];

        $notification = [
            'title' => $title,
            'body'  => $message,
            'notification_type' =>  'Test'
        ];
        $extraNotificationData = ["message" => $notification, "id" => $id, 'action' => $action];

        $fcmNotification = [
            'to'           => $fcm_token,
            'notification' => $notification,
            'data'         => $extraNotificationData,
        ];

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 60);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fcmNotification));
        curl_setopt($ch, CURLOPT_HTTPHEADER, $header);

        $result = curl_exec($ch);
        curl_close($ch);

        return $result;
    }

    

    /**
     * cronjob untuk
     *
     *
     *
     */


     //tiap jam 6.00 wib
    public function cek_jatuh_tempo(){
        $jatuh_tempo = DB::select("SELECT a.id,b.firebase_token,
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

                                    WHERE (a.`status` = 'AKT')      
                                        AND IFNULL(d.status_bayar,0) = 0
                                        AND TIMESTAMPDIFF(DAY, DATE(a.tgl_jatuh_tempo),DATE(NOW())) > -3");

        foreach ($jatuh_tempo as $j) {

            $pesan = "";
            if($j->hari_jatuh_tempo < 0){
                $pesan = "Pembayaran kamar kos anda jatuh tempo dalam " . $j->hari_jatuh_tempo;
            }elseif($j->hari_jatuh_tempo == 0){
                $pesan = "Pembayaran kamar kos anda jatuh tempo dalam hari ini";
            }else{
                $pesan = "Pembayaran kamar kos anda lewat " . $j->hari_jatuh_tempo . ' hari dari jatuh tempo';
            }

            Notifikasi::create([
                'firebase_token' => $j->firebase_token,
                'pesan'          => $pesan,
                'status'         => 'PND'
            ]);


        }                                
    }

    //setiap 1 menit
    public function start()
    {
        
        //non aktifkan data booking jika lebih dari 60 menit tidak berganti status
        $booking = DB::select(" SELECT a.id
                                FROM sewa a
                                LEFT JOIN bayar_sewa b ON a.id = b.sewa_id AND a.tgl_jatuh_tempo = b.tgl_jatuh_tempo
                                WHERE (a.`status` = 'BOK'
                                    AND (TIMESTAMPDIFF(MINUTE,a.tgl_jatuh_tempo, NOW()) > 60))
                                    OR b.`status` IN ('PND','BBI')
                                GROUP BY a.id   ");

        foreach ($booking as $b) {
            $sewa         = Sewa::where('id', $b->id)->first();
            $sewa->status = 'NAK';
            $sewa->save();
        }

        //TODO::kirim notifikasi

        $notifikasi = DB::select("SELECT id,firebase_token,pesan FROM notifikasi WHERE `status` = 'PND' LIMIT 5");
        foreach ($notifikasi as $n) {
            $this->kirimNotifikasi($n->firebase_token, 'Notifikasi dari rumahkos.com', $n->pesan);
            $n         = Notifikasi::where('id', $n->id)->first();
            $n->status = 'SND';
            $n->save();
        }

    }
}
