<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Session;

class Penyewa
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle(Request $request, Closure $next)
    {
        //return $next($request);
        $user_level = Session::get('user_level');

        if($user_level !== 'penyewa'){
            return redirect('/')->with('alert-danger','Anda sudah logout');
        }else{
            return $next($request);
        }
    }
}
