<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class MutasiDana extends Model
{
    use HasFactory;

    protected $table = "mutasi_dana";

    protected $fillable = [
        'user_id',
        'rekening_bank',
        'nominal',
        'status',
        'keterangan'
    ];    
}
