<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Routing\Redirector;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Session;
use Illuminate\Support\Facades\URL;
use Illuminate\Support\Str;
use Illuminate\Validation\Rule;
use Illuminate\Support\Facades\DB;
use Mail;
use Validator;

class WebController extends Controller
{
    public function test_email(){
        
        $to_email = 'triasfahrudin@gmail.com';
        $to_name  = 'trias';
        $data     = array('body' => 'selamat datang di rumahkos.com, untuk mengaktifkan akun anda, silahkan klik <a href="' . URL::to('/aktivasi-akun/' . md5($to_email)) . '">disini</a>');
       

        Mail::send('email.registration', $data, function ($message) use ($to_name, $to_email) {
            $message->subject('Registrasi di rumahkos.com');
            $message->to($to_email, $to_name);
            $message->from('donotreply@rumahkos.com', 'Rumahkos.com');
        });

    }


    public function index(Request $request, Redirector $redirect)
    {

    //     $sewa_det = DB::select("SELECT b.firebase_token AS token_penyewa,
    //                         d.firebase_token AS token_pemilik,
    //                         c.nama AS nama_kos,
    //                         a.nama_kamar AS nama_kamar
    //                 FROM sewa a
    //                 LEFT JOIN users b ON a.user_penyewa_id = b.id
    //                 LEFT JOIN kos c ON a.kos_id = c.id
    //                 LEFT JOIN users d ON c.user_pemilik_id = d.id
    //                 WHERE a.id = 10");

    //    //echo  $token_penyewa = $sewa_det->first()->token_penyewa;
    //    var_dump($sewa_det);
    //   echo  $sewa_det[0]->token_penyewa;

        switch ($request->method()) {
            case 'POST':

                if (Auth::attempt(['email' => request('email'), 'password' => request('password')])) {
                    //apakah email tersebut ada atau tidak
                    $user = Auth::user();

                    if ($user->status === 'NAK') {
                        //return $redirect->to('/')->with('alert-danger', 'Akun belum aktif. Cek email untuk aktivasi')->send();
                        return redirect('/')->with('alert-danger', 'Akun belum aktif. Cek email untuk aktivasi');
                    } else {
                        $request->session()->put('user_id', $user->id);
                        $request->session()->put('user_nama', $user->nama);
                        $request->session()->put('user_email', $user->email);
                        $request->session()->put('user_level', $user->level);
                        return redirect(URL::to($user->level));
                    }

                } else {
                    return redirect('/')->with('alert-danger', 'Password atau Email salah !');
                }

                break;

            case 'GET':

                return view('web.login');
                break;

            default:
                # code...
                break;
        }

    }

    public function lupaPassword(Request $request, Redirector $redirect)
    {

        switch ($request->method()) {
            case 'POST':
                $this->validate($request, [
                    'email' => 'required|email',
                ]);

                $email = $request->email;

                $user = User::where('email', $email)->first();

                if ($user->count() > 0) {

                    $str_random        = Str::random(10);
                    $user->reset_token = $str_random;

                    $user->save();

                    $to_email = $request->email;
                    $to_name  = $request->nama;

                    $data = array('body' => 'Anda telah meminta reset password di rumahkos.com, klik <a href="' . URL::to('/reset-password/' . $str_random) . '">disini</a> untuk melanjutkan');
                    Mail::send('email.reset_password', $data, function ($message) use ($to_name, $to_email) {
                        $message->subject('Reset Password @rumahkos.com');
                        $message->to($to_email, $to_name);
                        $message->from('donotreply@rumahkos.com', 'Rumahkos.com');
                    });

                    return redirect('/')->with('alert-danger', 'Periksa email anda');

                } else {
                    return redirect('/')->with('alert-danger', 'Email anda tidak ditemukan');
                }

                break;

            case 'GET':

                return view('web.forgot_password');
                break;

            default:
                # code...
                break;
        }

    }

    public function resetPassword($token, Request $request, Redirector $redirect)
    {

        switch ($request->method()) {
            case 'POST':
                $this->validate($request, [
                    'password' => 'required|confirmed',
                ]);

                $user = User::where('reset_token', $token);

                if ($user->count() > 0) {

                    $u              = $user->first();
                    $str_random     = Str::random(10);
                    $u->reset_token = $str_random;
                    $u->password    = Hash::make($request->password);

                    $u->save();

                    return redirect('/')->with('alert-success', 'Password berhasil diubah');

                } else {
                    return redirect('/')->with('alert-danger', 'Token tidak ditemukan');
                }

                break;

            case 'GET':

                return view('web.reset_password');
                break;

            default:
                # code...
                break;
        }

    }

    public function aktivasiAkun($token)
    {
        $user = User::whereRaw("md5(email) = ?", [$token])->first();

        if ($user->count() > 0) {

            $user->status = 'AKT';
            $user->save();

            return redirect('/')->with('alert-success', 'Akun berhasil diaktifkan');

        } else {
            return redirect('/')->with('alert-danger', 'Token tidak ditemukan');
        }

    }

    public function buatAkunBaru(Request $request, Redirector $redirect)
    {

        switch ($request->method()) {
            case 'POST':
                $this->validate($request, [
                    'nama'     => 'required',
                    'email'    => 'required|email|unique:users,email',
                    'password' => 'required|confirmed',
                    'level'    => Rule::in(['pemilik', 'penyewa']), // option1 or option2 values

                ]);

                User::create([
                    'nama'     => $request->nama,
                    'email'    => $request->email,
                    'password' => Hash::make($request->password),
                    'level'    => $request->level,
                ]);

                $to_email = $request->email;
                $to_name  = $request->nama;

                $data = array('body' => 'selamat datang di rumahkos.com, untuk mengaktifkan akun anda, silahkan klik <a href="' . URL::to('/aktivasi-akun/' . md5($to_email)) . '">disini</a>');
                Mail::send('email.registration', $data, function ($message) use ($to_name, $to_email) {
                    $message->subject('Registrasi di rumahkos.com');
                    $message->to($to_email, $to_name);
                    $message->from('donotreply@rumahkos.com', 'Rumahkos.com');
                });

                return redirect('/')->with('alert-success', 'Akun berhasil dibuat. Cek email untuk aktivasi');

                // $user = User::where('reset_token', $token)->first();

                // if ($user->count() > 0) {

                //     $str_random        = Str::random(10);
                //     $user->reset_token = $str_random;
                //     $user->password    = Hash::make($request->password);

                //     $user->save();

                //     return redirect('/')->with('alert-success', 'Password berhasil diubah');

                // } else {
                //     return redirect('/')->with('alert-danger', 'Token tidak ditemukan');
                // }

                break;

            case 'GET':

                return view('web.register');
                break;

            default:
                # code...
                break;
        }

    }

    public function logout()
    {
        Session::flush();
        return redirect('/')->with('alert-danger', 'Anda sudah logout');
    }
}
