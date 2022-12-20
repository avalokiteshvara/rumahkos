<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Kos extends Model
{
    use HasFactory;

    protected $table = "kos";

    protected $fillable = [      
        'user_pemilik_id',
        'nama',
        'alamat',
        'wilayah',
        'telp',
        'tipe',
        'fasilitas',
        'deskripsi',
        'jml_kamar',
        'harga_sewa',        
        'lng',
        'lat',
        'foto'
    ];

    public function pemilik(){
    	return $this->belongsTo('App\User','user_pemilik_id','id');
    }
}
