package com.example.rumahkos;

import android.content.Context;
import android.content.DialogInterface;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.btnSignIn)
    Button btnSignIn;
    @BindView(R.id.progressBar)
    ProgressBar loading;

    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;
    @BindView(R.id.textRegister)
    TextView textRegister;
    @BindView(R.id.textForgetPassword)
    TextView textForgetPassword;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mContext = this;
        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(this);

        loading.setVisibility(View.GONE);

        textForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Masukkan email anda untuk reset password");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        startResetPassword(mail);
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                passwordResetDialog.create().show();

            }
        });


        if (Boolean.TRUE.equals(spManager.is_user_logged_in())) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    @OnClick(R.id.btnSignIn)
    public void requestLogin() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("email diperlukan.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("password diperlukan.");
            return;
        }

        loading.setVisibility(View.VISIBLE);

        Log.e("LOGIN", "Mulai login");

        HashMap<String, String> headers = new HashMap<String, String>();
        /*
        * header('Access-Control-Allow-Origin: *');
          header('Access-Control-Allow-Methods: POST, GET, PUT, OPTIONS, PATCH, DELETE');
          header('Access-Control-Allow-Credentials: true');
          header('Access-Control-Allow-Headers: Authorization, Content-Type, x-xsrf-token, x_csrftoken, Cache-Control, X-Requested-With');
        * */

        headers.put("Accept", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods","POST, GET, PUT, OPTIONS, PATCH, DELETE");
        headers.put("Access-Control-Allow-Credentials","true");
        headers.put("Access-Control-Allow-Headers","Authorization, Content-Type, x-xsrf-token, x_csrftoken, Cache-Control, X-Requested-With");

        mBaseApiService.login(headers,email, password)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        loading.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());

                            Log.i("LOGIN", "JSONObject :" + jsonObject.toString());

                            if (jsonObject.getString("error").equals("false")) {

                                int user_id = jsonObject.getJSONObject("user").getInt("id");
                                String nama = jsonObject.getJSONObject("user").getString("nama");
                                String email = jsonObject.getJSONObject("user").getString("email");
                                String telp = jsonObject.getJSONObject("user").getString("telp");
                                String alamat = jsonObject.getJSONObject("user").getString("alamat");
                                String foto = jsonObject.getJSONObject("user").getString("foto");
                                String ktp_foto = jsonObject.getJSONObject("user").getString("ktp_foto");
                                String ktp_nomor = jsonObject.getJSONObject("user").getString("ktp_nomor");
                                String level = jsonObject.getJSONObject("user").getString("level");
                                String token = jsonObject.getJSONObject("user").getString("token");
                                String rekening = jsonObject.getJSONObject("user").getString("rekening");
                                String rekening_admin = jsonObject.getJSONObject("user").getString("rekening_admin");
                                String verifikasi = jsonObject.getJSONObject("user").getString("verifikasi");

                                spManager.saveInt(SPManager.USER_ID,user_id);
                                spManager.saveBoolean(SPManager.LOGIN_STATUS, true);
                                spManager.saveString(SPManager.NAMA, nama);
                                spManager.saveString(SPManager.EMAIL, email);
                                spManager.saveString(SPManager.TELP, telp);
                                spManager.saveString(SPManager.ALAMAT,alamat);
                                spManager.saveString(SPManager.FOTO, foto);
                                spManager.saveString(SPManager.KTP_FOTO, ktp_foto);
                                spManager.saveString(SPManager.KTP_NOMOR, ktp_nomor);
                                spManager.saveString(SPManager.LEVEL, level);
                                spManager.saveString(SPManager.ACCESS_TOKEN, token);
                                spManager.saveString(SPManager.REKENING,rekening);
                                spManager.saveString(SPManager.REKENING_ADMIN, rekening_admin);
                                spManager.saveString(SPManager.STATUS_VERIFIKASI,verifikasi);


                                //set default filter search
                                spManager.defaultSearchFilter();

                                startActivity(new Intent(mContext, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();

                            } else {
                                String error_message = jsonObject.getString("error_msg");
                                Toasty.error(mContext, error_message, Toasty.LENGTH_LONG).show();
                                Log.i("LOGIN", "Login GAGAL : " + error_message);
                            }
                        } catch (JSONException | IOException e) {
                            Log.i("LOGIN", "Login GAGAL " + e.getMessage());
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

    @OnClick(R.id.textRegister)
    public void onTextRegisterClicked() {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

    }

//    @OnClick(R.id.textForgetPassword)
//    public void onTextForgetPasswordClicked() {
//
//        Log.e("On Forget","Clicked");
//
//        final EditText resetMail = new EditText();
//        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(getApplicationContext());
//        passwordResetDialog.setTitle("Reset Password ?");
//        passwordResetDialog.setMessage("Masukkan email anda untuk reset password");
//        passwordResetDialog.setView(resetMail);
//
//        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // extract the email and send reset link
//                String mail = resetMail.getText().toString();
//                startResetPassword(mail);
//            }
//        });
//
//        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // close the dialog
//            }
//        });
//
//    }

    private void startResetPassword(String mail) {
        loading.setVisibility(View.VISIBLE);

        Log.e("RESET", "Mulai reset");
        mBaseApiService.resetPassword(mail)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        loading.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());

                            Log.i("LOGIN", "JSONObject :" + jsonObject.toString());

                            if (jsonObject.getString("error").equals("false")) {

                                String error_message = jsonObject.getString("error_msg");
                                Toasty.success(mContext, error_message, Toasty.LENGTH_LONG).show();

                            } else {
                                String error_message = jsonObject.getString("error_msg");
                                Toasty.error(mContext, error_message, Toasty.LENGTH_LONG).show();
                                Log.i("LOGIN", "Login GAGAL : " + error_message);
                            }
                        } catch (JSONException | IOException e) {
                            Log.i("LOGIN", "Login GAGAL " + e.getMessage());
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

//    @OnClick(R.id.textForgetPassword)
//    public void onViewClicked() {
//    }

    @Override
    public void onBackPressed() {

    }

}
