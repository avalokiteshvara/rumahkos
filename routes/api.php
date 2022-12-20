<?php

use App\Http\Controllers\ApiController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
 */

Route::post('/login', [ApiController::class, 'login']);
Route::post('/register', [ApiController::class, 'register']);
Route::post('/reset-password', [ApiController::class, 'resetPassword']);

Route::group(['middleware' => 'auth:api'], function () {
    Route::post('/send-firebase-token', [ApiController::class, 'sendFirebaseToken']);
    Route::get('/get-kos-list', [ApiController::class, 'getKosList']);
    Route::get('/load-detail-kos', [ApiController::class, 'loadDetailKos']);
    Route::post('/update-data-kos', [ApiController::class, 'updateDataKos']);
    Route::get('/get-kecamatan', [ApiController::class, 'getKecamatan']);
    Route::get('/get-kelurahan', [ApiController::class, 'getKelurahan']);

    Route::post('/booking', [ApiController::class, 'booking']);
    Route::get('/get-tagihan', [ApiController::class, 'getTagihan']);
    Route::get('/get-sewa', [ApiController::class, 'getSewa']);

    Route::post('/permintaan-berhenti-sewa',[ApiController::class,'berhentiSewa']);
    Route::post('/menyetujui-berhenti-sewa ',[ApiController::class,'setujuiBerhentiSewa']);

    Route::post('/update-lokasi', [ApiController::class, 'updateLokasi']);
    Route::post('/check-active-sewa',[ApiController::class,'checkActiveSewa']);

    Route::post('/update-rating',[ApiController::class,'updateRating']);

    Route::get('/get-fasilitas-list', [ApiController::class, 'getFasilitasList']);

    Route::post('/bayar', [ApiController::class, 'bayar']);
    Route::post('/update-sewa', [ApiController::class, 'updateSewa']);

    Route::post('/update-data-profile',[ApiController::class,'updateDataProfile']);
});

// Route::group(['middleware' => 'auth:api'], function(){

//     Route::get('/profile',[UserController::class,'profile']);
//     Route::post('/profile',[UserController::class,'profileUpdate']);

//     Route::get('/get-kecamatan',[WilayahController::class,'kecamatan']);
//     Route::get('/get-kelurahan/{kec}',[WilayahController::class,'kelurahan']);

//     Route::post('/kos-store',[KosController::class,'store']);
//     Route::post('/kos-update/{id}',[KosController::class,'update']);
//     Route::post('/kos',[KosController::class,'index']);
//     Route::get('/kos-destroy/{kos_id}',[KosController::class,'destroy']);

//     Route::get('/kos-search',[KosController::class,'search']);

// });

// Route::middleware('auth:api')->get('/user', function (Request $request) {
//     return $request->user();
// });
