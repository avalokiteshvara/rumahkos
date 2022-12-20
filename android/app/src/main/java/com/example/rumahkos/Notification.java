package com.example.rumahkos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.UtilsApi;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Notification extends Activity {
    Context mContext;
    SPManager spManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        spManager = new SPManager(this);

        if (getIntent().hasExtra("msg")) {

            String isi_pesan = getIntent().getExtras().getString("msg");

            if (isi_pesan.contains("ACTION::LOGOUT|REASON::VERIFICATION_ACCEPTED")) {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Notifikasi")
                    .setContentText("Verifikasi akun anda diterima")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {

                        spManager.saveString(SPManager.STATUS_VERIFIKASI,"Y");
                        spManager.saveBoolean(SPManager.LOGIN_STATUS,true);
                        Intent intent = new Intent(Notification.this, LoginActivity.class);
                        startActivity(intent);

                        sDialog.dismissWithAnimation();
                        finish();
                    })
                    .show();
            } else {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Notifikasi")
                    .setContentText(isi_pesan)
                    .setConfirmText("OK")
                    .setConfirmClickListener(sDialog -> {

                        Intent intent = new Intent(Notification.this, MainActivity.class);
                        intent.putExtra("redirect", "goto-notifikasi");
                        startActivity(intent);

                        sDialog.dismissWithAnimation();
                        finish();


                    })
                    .show();
            }


        }

    }
}
