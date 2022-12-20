<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class BayarSewa extends Model
{
    use HasFactory;

    protected $table = "bayar_sewa";

    protected $fillable = [
        'sewa_id',
        'tgl_jatuh_tempo',        
        'rekening_bank',        
        'foto_pembayaran',        
        'tgl_bayar',
        'status',
        'diverifikasi_oleh',
    ];
}
