package com.example.rumahkos.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class FormBayarFragment extends Fragment {


    public final int REQUEST_CAMERA = 0;
    public final int OPEN_SETTING = 1;
    Bitmap bitmap, decoded;
    int bitmap_size = 60; // range 1 - 100
    String mCurrentPhotoPath;

    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;
    int id;
    String nama_kos;
    String nama_kamar;
    String nama_penyewa;
    String tgl_jatuh_tempo;
    String bulan_sewa;
    Double nominal;
    @BindView(R.id.bayar_tvRumahKos)
    TextView bayarTvRumahKos;
    @BindView(R.id.bayar_etNamaKamar)
    EditText bayarEtNamaKamar;
    @BindView(R.id.bayar_tvNamaPenyewa)
    TextView bayarTvNamaPenyewa;
    @BindView(R.id.bayar_tvTanggalJatuhTempo)
    TextView bayarTvTanggalJatuhTempo;
    @BindView(R.id.bayar_etJmlBulanTagih)
    EditText bayarEtJmlBulanTagih;
    @BindView(R.id.bayar_etNominal)
    EditText bayarEtNominal;
    @BindView(R.id.bayar_btnUploadBukti)
    Button bayarBtnUploadBukti;
    @BindView(R.id.bayar_imgViewBukti)
    ImageView bayarImgViewBukti;
    @BindView(R.id.bayar_btnStopSewa)
    Button bayarBtnStopSewa;
    @BindView(R.id.bayar_btnBayar)
    Button bayarBtnBayar;

    /*
    *   bundle.putInt("kos_id", modelList.get(position).getId());
        bundle.putString("nama_kos",modelList.get(position).getNama_kos());
        bundle.putString("nama_kamar",modelList.get(position).getNama_kamar());
        bundle.putString("nama_penyewa",modelList.get(position).getNama_penyewa());

        String tglJatuhTempo = modelList.get(position).getTgl_jatuh_tempo();
        int hari_jatuh_tempo = modelList.get(position).getHari_jatuh_tempo();

        String status_hari_jatuh_tempo = hari_jatuh_tempo > 0 ? "+":"-";
        bundle.putString("tgl_jatuh_tempo",String.format(Locale.US,"%s ( %s %d hari )",tglJatuhTempo,status_hari_jatuh_tempo,hari_jatuh_tempo));

        bundle.putString("bulan_sewa",modelList.get(position).getBulan_sewa());
        bundle.putDouble("nominal",modelList.get(position).getHarga_total());
    *
    *
    * */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void EnableDisableEditText(boolean isEnabled, EditText editText) {
        editText.setFocusable(isEnabled);
        editText.setFocusableInTouchMode(isEnabled) ;
        editText.setClickable(isEnabled);
        editText.setLongClickable(isEnabled);
        editText.setCursorVisible(isEnabled) ;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_form_bayar, container, false);

        ButterKnife.bind(this, root);

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);

        Bundle arguments = getArguments();
        if (arguments == null)
            Toast.makeText(getActivity(), "Arguments is NULL", Toast.LENGTH_LONG).show();
        else {
            id = getArguments().getInt("id", 0);
            nama_kos = getArguments().getString("nama_kos", "");
            nama_kamar = getArguments().getString("nama_kamar", "");
            nama_penyewa = getArguments().getString("nama_penyewa", "");

            tgl_jatuh_tempo = getArguments().getString("tgl_jatuh_tempo", "");
            bulan_sewa = getArguments().getString("bulan_sewa", "");
            nominal = getArguments().getDouble("nominal", 0);

        }

        bayarTvRumahKos.setText(nama_kos);
        bayarEtNamaKamar.setText(nama_kamar);
        bayarTvNamaPenyewa.setText(nama_penyewa);

        bayarTvTanggalJatuhTempo.setText(tgl_jatuh_tempo);
        bayarEtJmlBulanTagih.setText(bulan_sewa);
        bayarEtNominal.setText(String.format(Locale.US, "%.0f",nominal));

        if(spManager.getLevel().equals("penyewa")){

            EnableDisableEditText(false,bayarEtJmlBulanTagih);
            EnableDisableEditText(false,bayarEtNominal);
            EnableDisableEditText(false,bayarEtNamaKamar);
        }

//        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
//        toolbar.setTitle("Booking Kamar Kos");

        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();
        if (floatingActionButton != null) {
            floatingActionButton.hide();

//            if(spManager.getLevel().equals("pemilik")){
//
//                floatingActionButton.setOnClickListener(view -> {
//
//                    Bundle bundle = new Bundle();
//                    // bundle.putInt("put something key", someting value);
//                    KosListFragment fragment = new KosListFragment();
//                    fragment.setArguments(bundle);
//                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
//
//                    activity.getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.nav_host_fragment, fragment, KosListFragment.class.getSimpleName())
//                        .addToBackStack(null)
//                        .commit();
//                });
//            }else{
//                floatingActionButton.hide();
//            }

//            floatingActionButton.setOnClickListener(view -> {
//
//                Bundle bundle = new Bundle();
//                // bundle.putInt("put something key", someting value);
//                KosListFragment fragment = new KosListFragment();
//                fragment.setArguments(bundle);
//                AppCompatActivity activity = (AppCompatActivity) view.getContext();
//
//                activity.getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.nav_host_fragment, fragment, KosListFragment.class.getSimpleName())
//                    .addToBackStack(null)
//                    .commit();
//            });


        }

        return root;
    }


    @OnClick(R.id.bayar_btnUploadBukti)
    public void onBayarBtnUploadBuktiClicked() {
        //Toasty.error(mContext, "Belum Diimplementasikan !", Toasty.LENGTH_LONG).show();
        pickImage();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        //sdcard/Android/data/com.example.dolibarr/files/Pictures/....
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",   /* suffix */
            storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        Log.e("mCurrentPhotoPath", mCurrentPhotoPath);
        return image;
    }

    private void pickImage() {

        Dexter.withActivity(getActivity())
            .withPermissions(
                Manifest.permission.CAMERA)
            .withListener(new MultiplePermissionsListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        //Toast.makeText(mContext, "LOCATION ACCESS GRANTED", Toast.LENGTH_SHORT).show();
                        File f = null;
                        try {
                            f = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mContext, "com.example.rumahkos.fileprovider", Objects.requireNonNull(f)));
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(takePictureIntent, REQUEST_CAMERA);

                    }

                    if (report.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog();
                    }
                }


                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).
            withErrorListener(error -> Toast.makeText(mContext, String.format("Some Error! %s", error.toString()), Toast.LENGTH_SHORT).show())
            .onSameThread()
            .check();


    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, OPEN_SETTING);
    }

    // Handle results of enable GPS Dialog
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    bitmap = null;
                    bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    setToImageView(getResizedBitmap(bitmap, 512));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public Bitmap getResizedBitmap(@NotNull Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        // imageView.setImageBitmap(decoded);
        bayarImgViewBukti.setImageBitmap(decoded);
    }


    @OnClick(R.id.bayar_btnStopSewa)
    public void onBayarBtnStopSewaClicked() {
        Toasty.error(mContext, "Belum Diimplementasikan !", Toasty.LENGTH_LONG).show();
    }

    private String getStringImage(@NotNull Bitmap bmp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @OnClick(R.id.bayar_btnBayar)
    public void onBayarBtnBayarClicked() {

        ProgressDialog loading;

        loading = ProgressDialog.show(mContext, null, "Mengirimkan data...", true, false);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + spManager.getAccessToken());
        headers.put("Accept", "application/json");

        mBaseApiService.bayar(
            headers,
            this.id,
            bayarEtNamaKamar.getText().toString(),
            bayarEtJmlBulanTagih.getText().toString(),
            bayarEtNominal.getText().toString(),
            (this.decoded != null ? getStringImage(decoded) : "NO_IMAGE")
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String error_message = jsonObject.getString("error_msg");
                        Toasty.success(mContext, error_message, Toasty.LENGTH_LONG).show();

                        TagihanListFragment fragment = new TagihanListFragment();

                        getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, fragment, TagihanListFragment.class.getSimpleName())
                            .addToBackStack(null)
                            .commit();

                    } catch (JSONException | IOException e) {
                        Log.i("LOGIN", "Login GAGAL " + e.getMessage());
                    }

                } else {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("debug", "onFailure: ERROR > " + t.toString());
                loading.dismiss();
            }
        });

    }
}
