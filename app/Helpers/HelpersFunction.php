<?php

namespace App\Helpers;

use App\Models\Notifikasi;
use App\Models\User;

class HelpersFunction
{

    public function rupiah($angka)
    {

        $hasil_rupiah = "Rp " . number_format($angka, 2, ',', '.');
        return $hasil_rupiah;

    }

    public static function simpanNotifikasi($user_id, $pesan)
    {
        $user = User::where('id','=',$user_id)->first();

        $tanggal = date('d-m-Y H:i:s');

        Notifikasi::create([
            'firebase_token' => $user->firebase_token,
            'pesan'          => '[' . $tanggal . ']::'. $pesan,
            'status'         => 'PND'
        ]);
    }

    public static function test(){
        echo "test from helper";
    }

}
