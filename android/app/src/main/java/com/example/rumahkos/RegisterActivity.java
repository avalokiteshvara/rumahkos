package com.example.rumahkos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.text_register)
    TextView textRegister;
    @BindView(R.id.register_etEmail)
    EditText registerEtEmail;
    @BindView(R.id.register_etNama)
    EditText registerEtNama;
    @BindView(R.id.register_etPassword)
    EditText registerEtPassword;
    @BindView(R.id.register_etTelp)
    EditText registerEtTelp;
    @BindView(R.id.register_btnRegisterPemilik)
    Button registerBtnRegister;
    @BindView(R.id.textLoginHere)
    TextView textLoginHere;
    @BindView(R.id.progressBar)
    ProgressBar loading;
    @BindView(R.id.register_etBankNama)
    EditText registerEtBankNama;

    @BindView(R.id.register_etBankNomor)
    EditText registerEtBankNomor;

    @BindView(R.id.register_etBankAtasNama)
    EditText registerEtBankAtasNama;


    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;
    @BindView(R.id.register_etPasswordConfirmation)
    EditText registerEtPasswordConfirmation;
    @BindView(R.id.register_btnRegisterPenyewa)
    Button registerBtnRegisterPenyewa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mContext = this;
        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(this);

        loading.setVisibility(View.GONE);
    }

    @OnClick(R.id.register_btnRegisterPemilik)
    public void onRegisterBtnRegisterPemilikClicked() {

        String email = registerEtEmail.getText().toString().trim();
        String nama = registerEtNama.getText().toString().trim();
        String password = registerEtPassword.getText().toString().trim();
        String passwordConf = registerEtPasswordConfirmation.getText().toString().trim();
        String telp = registerEtTelp.getText().toString().trim();
        String bankNama = registerEtBankNama.getText().toString().trim();
        String bankNomor = registerEtBankNomor.getText().toString().trim();
        String bankAtasNama = registerEtBankAtasNama.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            registerEtEmail.setError("Email is Required.");
            return;
        }

        if (TextUtils.isEmpty(nama)) {
            registerEtNama.setError("Nama dibutuhkan.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            registerEtPassword.setError("Password dibutuhkan.");
            return;
        }

        if (TextUtils.isEmpty(passwordConf)) {
            registerEtPasswordConfirmation.setError("Konfirmasi dibutuhkan.");
            return;
        }

        if(TextUtils.isEmpty(bankNama)){
            registerEtBankNama.setError("Nama Bank dibutuhkan");
            return;
        }

        if(TextUtils.isEmpty(bankNomor)){
            registerEtBankNomor.setError("Nomor Rekening dibutuhkan");
            return;
        }

        if(TextUtils.isEmpty(bankAtasNama)){
            registerEtBankAtasNama.setError("Rekening Atas Nama dibutuhkan");
            return;
        }


        loading.setVisibility(View.VISIBLE);

        Log.e("REGISTER", "Mulai login");
        mBaseApiService.register(email, nama, password, passwordConf, telp, "pemilik",bankNama,bankNomor,bankAtasNama)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        loading.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());

                            Log.i("REGISTER", "JSONObject :" + jsonObject.toString());

                            if (jsonObject.getString("error").equals("false")) {

                                Toasty.success(mContext, "Silahkan periksa email anda", Toasty.LENGTH_LONG).show();

                                startActivity(new Intent(mContext, LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();

                            } else {
                                String error_message = jsonObject.getString("error_msg");
                                Toasty.error(mContext, error_message, Toasty.LENGTH_LONG).show();
                                Log.i("REGISTER", "REGISTER GAGAL : " + error_message);
                            }
                        } catch (JSONException | IOException e) {
                            Log.i("REGISTER", "REGISTER GAGAL " + e.getMessage());
                        }
                    } else {
                        loading.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                    loading.setVisibility(View.GONE);
                }
            });

    }

    @OnClick(R.id.textLoginHere)
    public void onTextLoginHereClicked() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    @OnClick(R.id.register_btnRegisterPenyewa)
    public void onRegisterBtnRegisterPenyewaClicked() {


        String email = registerEtEmail.getText().toString().trim();
        String nama = registerEtNama.getText().toString().trim();
        String password = registerEtPassword.getText().toString().trim();
        String passwordConf = registerEtPasswordConfirmation.getText().toString().trim();
        String telp = registerEtTelp.getText().toString().trim();

        String bankNama = registerEtBankNama.getText().toString().trim();
        String bankNomor = registerEtBankNomor.getText().toString().trim();
        String bankAtasNama = registerEtBankAtasNama.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            registerEtEmail.setError("Email diperlukan.");
            return;
        }

        if (TextUtils.isEmpty(nama)) {
            registerEtNama.setError("Nama diperlukan.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            registerEtPassword.setError("Password diperlukan.");
            return;
        }

        if (TextUtils.isEmpty(passwordConf)) {
            registerEtPasswordConfirmation.setError("Password konfirmasi diperlukan.");
            return;
        }



        loading.setVisibility(View.VISIBLE);

        Log.e("REGISTER", "Mulai login");
        mBaseApiService.register(email, nama, password, passwordConf, telp, "penyewa",bankNama,bankNomor,bankAtasNama)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        loading.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());

                            Log.i("REGISTER", "JSONObject :" + jsonObject.toString());

                            if (jsonObject.getString("error").equals("false")) {

                                Toasty.error(mContext, "Silahkan periksa email anda", Toasty.LENGTH_LONG).show();

                                startActivity(new Intent(mContext, LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();

                            } else {
                                String error_message = jsonObject.getString("error_msg");
                                Toasty.error(mContext, error_message, Toasty.LENGTH_LONG).show();
                                Log.i("REGISTER", "REGISTER GAGAL : " + error_message);
                            }
                        } catch (JSONException | IOException e) {
                            Log.i("REGISTER", "REGISTER GAGAL " + e.getMessage());
                        }
                    } else {
                        loading.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("debug", "onFailure: ERROR > " + t.toString());
                    loading.setVisibility(View.GONE);
                }
            });

    }
}
