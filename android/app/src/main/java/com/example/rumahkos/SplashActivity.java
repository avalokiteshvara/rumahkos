package com.example.rumahkos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;


public class SplashActivity extends AppCompatActivity {

    SPManager sharedPrefManager;
    BaseApiService mBaseApiService;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mBaseApiService = UtilsApi.getAPIService();
        sharedPrefManager = new SPManager(this);

        int SPLASH_TIME_OUT = 2;
        new Handler().postDelayed(() -> {

            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);

    }
}
