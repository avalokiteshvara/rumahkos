package com.example.rumahkos.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerFragment extends Fragment {

    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;
    private ImageView imageView;
    private CodeScanner codeScanner;
    private CodeScannerView codeScannerView;
    private ProgressDialog progressDialog;
    private String textQRCode;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_scanner, container, false);

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);


        imageView = root.findViewById(R.id.imContent);
        codeScannerView = root.findViewById(R.id.scannerView);
        imageView.bringToFront();
        codeScanner = new CodeScanner(mContext, codeScannerView);
        codeScanner.setDecodeCallback(result -> getActivity().runOnUiThread(() -> {
            textQRCode = result.getText();
            //new ScanQR.GetLaporan().execute();
            Log.e("QRCode", textQRCode);

            Vibrator v = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(400);
            }

            //tampilkan search filter
            ((MainActivity) getActivity()).setmStateActionFilter(false);
            ((MainActivity) getActivity()).invalidateOptionsMenu();

            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + spManager.getAccessToken());
            headers.put("Accept", "application/json");

            mBaseApiService.CheckActiveSewa(headers, textQRCode)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {

                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());

                                Log.i("LOGIN", "JSONObject :" + jsonObject.toString());

                                if (jsonObject.getString("error").equals("false")) {


                                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                    LayoutInflater inflater = getLayoutInflater();
                                    View dialogView = inflater.inflate(R.layout.fragment_hasil_scanner, null);
                                    dialog.setView(dialogView);
                                    dialog.setCancelable(true);
                                    dialog.setIcon(R.mipmap.ic_launcher);
                                    dialog.setTitle("Hasil Scanner");


                                    TextView tvNamaKos;
                                    EditText etNamaKamar;
                                    TextView tvNamaPenyewa;
                                    TextView tvTglJatuhTempo;
                                    EditText etBulanSewa;
                                    EditText etHarga;

                                    tvNamaKos = (TextView) dialogView.findViewById(R.id.scanner_tvRumahKos);
                                    etNamaKamar = (EditText) dialogView.findViewById(R.id.scanner_etNamaKamar);
                                    tvNamaPenyewa = (TextView) dialogView.findViewById(R.id.scanner_tvNamaPenyewa);
                                    tvTglJatuhTempo = (TextView) dialogView.findViewById(R.id.scanner_tvTanggalJatuhTempo);
                                    etBulanSewa = (EditText) dialogView.findViewById(R.id.scanner_etJmlBulanTagih);
                                    etHarga = (EditText) dialogView.findViewById(R.id.scanner_etNominal);

                                    String namaKos = jsonObject.getJSONObject("sewa").getString("nama_kos");
                                    String namaKamar = jsonObject.getJSONObject("sewa").getString("nama_kamar");
                                    String namaPenyewa = jsonObject.getJSONObject("sewa").getString("nama_penyewa");
                                    String tgljatuhTempo = jsonObject.getJSONObject("sewa").getString("tgl_jatuh_tempo");
                                    String bulanSewa = jsonObject.getJSONObject("sewa").getString("bulan_tagihan");
                                    String harga = jsonObject.getJSONObject("sewa").getString("nominal");

                                    tvNamaKos.setText(namaKos);
                                    etNamaKamar.setText(namaKamar);
                                    tvNamaPenyewa.setText(namaPenyewa);
                                    tvTglJatuhTempo.setText(tgljatuhTempo);
                                    etBulanSewa.setText(bulanSewa);
                                    etHarga.setText(harga);

                                    dialog.setNegativeButton("TUTUP", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {

                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            HashMap<String, String> headers = new HashMap<String, String>();
                                            headers.put("Authorization", "Bearer " + spManager.getAccessToken());
                                            headers.put("Accept", "application/json");

                                            mBaseApiService.updateSewa(
                                                headers,
                                                Integer.parseInt(textQRCode),
                                                etNamaKamar.getText().toString(),
                                                etBulanSewa.getText().toString(),
                                                etHarga.getText().toString()
                                            ).enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {

                                                        SewaAktifFragment fragment = new SewaAktifFragment();

                                                        getActivity().getSupportFragmentManager()
                                                            .beginTransaction()
                                                            .replace(R.id.nav_host_fragment, fragment, SewaAktifFragment.class.getSimpleName())
                                                            .addToBackStack(null)
                                                            .commit();


                                                    } else {

                                                        Toasty.error(mContext, "hmmm ERROR" , Toast.LENGTH_LONG).show();

                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                                                    Log.e("debug", "onFailure: ERROR > " + t.toString());

                                                }
                                            });


                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.show();

                                } else {
                                    String error_message = jsonObject.getString("error_msg");
                                    Toasty.error(mContext, error_message, Toasty.LENGTH_LONG).show();
                                    Log.i("LOGIN", "Login GAGAL : " + error_message);
                                }
                            } catch (JSONException | IOException e) {
                                Log.i("LOGIN", "Login GAGAL " + e.getMessage());
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                    }
                });


        }));
        checkCameraPermission();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkCameraPermission();
    }

    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewCompat.requestApplyInsets(view);
    }


    private void checkCameraPermission() {
        Dexter.withActivity(getActivity())
            .withPermission(Manifest.permission.CAMERA)
            .withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {
                    codeScanner.startPreview();
                }

                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {

                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                               PermissionToken token) {
                    token.continuePermissionRequest();
                }
            })
            .check();
    }

}
