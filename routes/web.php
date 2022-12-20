<?php

use App\Http\Controllers\AdminController;
use App\Http\Controllers\CronjobController;
use App\Http\Controllers\PemilikController;
use App\Http\Controllers\PenyewaController;
use App\Http\Controllers\WebController;
use App\Http\Controllers\WebViewController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
 */

//Route::post('/login', [AuthController::class, 'login']);

Route::group(['prefix' => 'webview'], function () {
    Route::get('/get-location/{lat}/{lng}', [WebViewController::class,'getLocation']);
    Route::get('/get-qrcode/{id}',[WebViewController::class,'getQrcode']);
    Route::get('/mutasi-dana/{user_id}', [WebViewController::class, 'mutasiDana']);
    Route::get('/riwayat-sewa/{user_id}/{user_level}',[WebViewController::class,'riwayatSewa']);
    Route::match(array('GET', 'POST'), '/permohonan-penarikan-dana/{user_id}',[WebViewController::class,'permohonanPenarikanDana']);
});


#
Route::match(array('GET', 'POST'), '/', [WebController::class, 'index']); #login
Route::get('/logout', [WebController::class, 'logout']);

Route::get('/test_email',[WebController::class,'test_email']);


Route::match(array('GET', 'POST'), '/lupa-password', [WebController::class, 'lupaPassword']);
Route::match(array('GET', 'POST'), '/reset-password/{token}', [WebController::class, 'resetPassword']);
Route::match(array('GET', 'POST'), '/buat-akun-baru', [WebController::class, 'buatAkunBaru']);
Route::get('/aktivasi-akun/{token}', [WebController::class, 'aktivasiAkun']);

#
Route::group(['prefix' => 'pemilik'], function () {
    Route::get('/', [PemilikController::class, 'index']);
    #
    Route::get('/rumah-kos', [PemilikController::class, 'rumahKos']);
    Route::match(array('GET', 'POST'), '/rumah-kos-tambah', [PemilikController::class, 'rumahKosTambah']);
    Route::match(array('GET', 'POST'), '/rumah-kos-edit/{id}', [PemilikController::class, 'rumahKosEdit']);
    Route::get('/rumah-kos-penyewa/{id}', [PemilikController::class, 'rumahKosPenyewa']);
    Route::get('/nonaktifkan-sewa/{id}', [PemilikController::class, 'nonaktifkanSewa']);
    Route::get('/rumah-kos-hapus/{id}', [PemilikController::class, 'rumahKosHapus']);
    #
    Route::get('/tagihan-sewa', [PemilikController::class, 'tagihanSewa']);
    Route::get('/tagihan-lunas/{id}', [PemilikController::class, 'tagihanLunas']);
    #
    Route::get('/mutasi-dana', [PemilikController::class, 'mutasiDana']);
    Route::match(array('GET', 'POST'), '/permohonan-penarikan-dana', [PemilikController::class, 'permohonanPenarikanDana']);
    #
    Route::get('/get-kelurahan', [PemilikController::class, 'getKelurahan']);
    Route::post('/update-lokasi', [PemilikController::class, 'updateLokasi']);
    #
    Route::match(array('GET', 'POST'), '/penyewa-tambah/{id}', [PemilikController::class, 'penyewaTambah']);
    #
    Route::get('/riwayat-tagihan-sewa', [PemilikController::class, 'riwayatTagihanSewa']);
    #
    Route::match(array('GET', 'POST'), '/profile', [PemilikController::class, 'profile']);
    #
    Route::get('/delete-image/{kos_id}/{image_name}',[PemilikController::class,'deleteImage']);
});

#
Route::group(['prefix' => 'penyewa'], function () {
    Route::get('/', [PenyewaController::class, 'index']);
    #
    Route::get('/cari-rumah-kos', [PenyewaController::class, 'cariRumahKos']);
    #
    Route::match(array('GET', 'POST'), '/form-bayar/{id}', [PenyewaController::class, 'formBayar']);
    #
    Route::get('/tagihan-sewa', [PenyewaController::class, 'tagihanSewa']);
    Route::get('/riwayat-tagihan-sewa', [PenyewaController::class, 'riwayatTagihanSewa']);
    #
    Route::match(array('GET', 'POST'), '/booking/{id}', [PenyewaController::class, 'booking']);
    Route::match(array('GET', 'POST'), '/profile', [PenyewaController::class, 'profile']);
    #test
    Route::get('/test',[PenyewaController::class,'test']);
});

#
Route::group(['prefix' => 'admin'], function () {
    Route::get('/', [AdminController::class, 'index']);
    #
    Route::get('/data-pemilik-kos', [AdminController::class, 'dataPemilikKos']);
    Route::get('/data-penyewa-kos', [AdminController::class, 'dataPenyewaKos']);
    Route::get('/data-rumah-kos', [AdminController::class, 'dataRumahKos']);
    #
    Route::get('/validasi-pembayaran-sewa', [AdminController::class, 'validasiPembayaranSewa']);
    Route::get('/mutasi-dana', [AdminController::class, 'mutasiDana']);

    Route::get('/permohonan-mutasi-diproses/{id}', [AdminController::class, 'mutasiDiproses']);
    Route::get('/bukti-bayar-valid/{id}', [AdminController::class, 'buktiBayarValid']);
    Route::get('/bukti-bayar-invalid/{id}', [AdminController::class, 'buktiBayarInvalid']);
    #
    Route::get('/pemilik', [AdminController::class, 'pemilik']);

    #
    Route::get('/verifikasi-diterima/{id}', [AdminController::class, 'verifikasiDiterima']);
    Route::get('/verifikasi-ditolak/{id}', [AdminController::class, 'verifikasiDitolak']);
    // Route::match(array('GET', 'POST'), '/pemilik-tambah', [AdminController::class, 'pemilikTambah']);
    // Route::match(array('GET', 'POST'), '/pemilik-edit/{id}', [AdminController::class, 'pemilikEdit']);
    // Route::get('/pemilik-hapus/{id}', [AdminController::class, 'pemilikHapus']);

    #
    Route::get('/penyewa', [AdminController::class, 'penyewa']);
    // Route::match(array('GET', 'POST'), '/penyewa-tambah', [AdminController::class, 'penyewaTambah']);
    // Route::match(array('GET', 'POST'), '/penyewa-edit/{id}', [AdminController::class, 'penyewaEdit']);
    // Route::get('/penyewa-hapus/{id}', [AdminController::class, 'penyewaHapus']);

    #
    // Route::get('/setting', [AdminController::class, 'setting']);
    // Route::post('/setting', [AdminController::class], 'setting');

    #pending (bukti bayar sudah upload) -> validasi -> GAGAL|BERHASIL -> selesai
    // Route::get('/transaksi-status/{status}', [AdminController::class, 'transaksi']);
    // Route::get('/transaksi-set-status/{id}/{status}', [AdminController::class, 'transaksi']);
    Route::match(array('GET', 'POST'), '/profile', [AdminController::class, 'profile']);
});

#dilakukan 1 kali dalam sehari
Route::get('/cronjob-start', [CronjobController::class, 'start']);
Route::get('/cek-jatuh-tempo',[CronjobController::class,'cek_jatuh_tempo']);
// Route::get('/test', [CronjobController::class, 'test']);
