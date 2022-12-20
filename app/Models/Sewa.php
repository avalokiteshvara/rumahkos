<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Sewa extends Model
{
    use HasFactory;

    protected $table = "sewa";

    protected $fillable = [
        'user_penyewa_id',
        'kos_id',
        'nama_kamar',
        'tgl_sewa',
        'harga',
        'bulan_sewa',
        'tgl_jatuh_tempo',
        'status',
        'rating_nilai',
        'rating_komentar',       
    ];

    public function kos(){
        return $this->belongsTo('App\Models\Kos','kos_id');
    }

    public function penyewa(){
        return $this->belongsTo('App\Models\User','user_penyewa_id');
    }
}
