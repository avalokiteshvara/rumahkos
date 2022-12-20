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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.squareup.picasso.Picasso;

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
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class FormProfileFragment extends Fragment {

    public final int REQUEST_CAMERA_FOTO = 0;
    public final int REQUEST_CAMERA_KTP = 1;
    public final int OPEN_SETTING = 2;

    Bitmap bitmap, decoded_foto, decoded_ktp;
    int bitmap_size = 60; // range 1 - 100
    String mCurrentPhotoPath, mCurrentKtpPath;

    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;
    @BindView(R.id.profile_imgViewFoto)
    ImageView profileImgViewFoto;
    @BindView(R.id.profile_imgViewCamera)
    ImageView profileImgViewCamera;
    @BindView(R.id.profile_etNama)
    EditText profileEtNama;
    @BindView(R.id.profile_etAlamat)
    EditText profileEtAlamat;
    @BindView(R.id.profile_etTelp)
    EditText profileEtTelp;
    @BindView(R.id.profile_etEmail)
    EditText profileEtEmail;
    @BindView(R.id.profile_etNamaBank)
    EditText profileEtNamaBank;
    @BindView(R.id.profile_etNoRekening)
    EditText profileEtNoRekening;
    @BindView(R.id.profile_etNamaPemilikRekening)
    EditText profileEtNamaPemilikRekening;
    @BindView(R.id.profile_btnFotoKtp)
    Button profileBtnFotoKtp;
    @BindView(R.id.profile_imgViewKtp)
    ImageView profileImgViewKtp;
    @BindView(R.id.profile_btnKembali)
    Button profileBtnKembali;
    @BindView(R.id.profile_btnSimpan)
    Button profileBtnSimpan;
    @BindView(R.id.profile_etKtpNomor)
    EditText profileEtKtpNomor;



    @Override
    //Pressed return button
    public void onResume() {
        super.onResume();
        requireView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {

            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                ProfileFragment mf = new ProfileFragment();
                FragmentTransaction ft = requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_host_fragment, mf, FormProfileFragment.class.getSimpleName())
                    .addToBackStack(null);
                ft.commit();

                return true;
            }
            return false;
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_form_profile, container, false);
        ButterKnife.bind(this, root);

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);

        if (spManager.getLevel().equals("penyewa")) {
            profileEtNamaBank.setVisibility(View.GONE);
            profileEtNoRekening.setVisibility(View.GONE);
            profileEtNamaPemilikRekening.setVisibility(View.GONE);
        }

        profileEtNama.setText(spManager.getNama());
        profileEtAlamat.setText(spManager.getAlamat());
        profileEtTelp.setText(spManager.getTelp());
        profileEtEmail.setText(spManager.getEmail());

        String[] rekening = spManager.getRekening().split(Pattern.quote("|"));

        Log.i("GET REKENING", spManager.getRekening());


        profileEtNamaBank.setText(rekening[0]);
        profileEtNoRekening.setText(rekening[1]);
        profileEtNamaPemilikRekening.setText(rekening[2]);

        Picasso.get().invalidate(UtilsApi.BASE_URL + "uploads/" + spManager.getFoto());
        Picasso.get().load(UtilsApi.BASE_URL + "uploads/" + spManager.getFoto()).into(profileImgViewFoto);

        Log.e("FOTO", spManager.getFoto());

        Picasso.get().invalidate(UtilsApi.BASE_URL + "uploads/" + spManager.getFotoKtp());
        Picasso.get().load(UtilsApi.BASE_URL + "uploads/" + spManager.getFotoKtp()).into(profileImgViewKtp);


//        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
//        toolbar.setTitle("Booking Kamar Kos");

        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();
        if (floatingActionButton != null) {
            floatingActionButton.hide();
            floatingActionButton.setOnClickListener(view -> {
            });
        }

        //tampilkan search filter
        ((MainActivity) getActivity()).setmStateActionFilter(false);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        return root;
    }

    @OnClick(R.id.profile_imgViewCamera)
    public void onProfileImgViewCameraClicked() {

        pickImage("FOTO");

    }

    private File createImageFile(String jenis) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = jenis + "_JPEG_" + timeStamp + "_";
        File storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",   /* suffix */
            storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        if (jenis.equals("FOTO")) {
            mCurrentPhotoPath = image.getAbsolutePath();
        } else {
            mCurrentKtpPath = image.getAbsolutePath();
        }

        return image;
    }

    private void pickImage(String jenis) {
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
                            f = createImageFile(jenis);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mContext, "com.example.rumahkos.fileprovider", Objects.requireNonNull(f)));
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivityForResult(takePictureIntent, (jenis.equals("FOTO") ? REQUEST_CAMERA_FOTO : REQUEST_CAMERA_KTP));

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

    private void setToImageView(String jenis, Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);

        if (jenis.equals("FOTO")) {
            decoded_foto = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
            profileImgViewFoto.setImageBitmap(decoded_foto);
        } else {
            decoded_ktp = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
            profileImgViewKtp.setImageBitmap(decoded_ktp);
        }


        // imageView.setImageBitmap(decoded);

    }

    private String getStringImage(String jenis, @NotNull Bitmap bmp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        if (jenis.equals("FOTO")) {
            decoded_foto = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
        } else {
            decoded_ktp = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
        }


        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA_FOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    bitmap = null;
                    bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    setToImageView("FOTO", getResizedBitmap(bitmap, 512));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_CAMERA_KTP) {
            if (resultCode == RESULT_OK) {
                try {
                    bitmap = null;
                    bitmap = BitmapFactory.decodeFile(mCurrentKtpPath);
                    setToImageView("KTP", getResizedBitmap(bitmap, 512));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
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

    @OnClick(R.id.profile_btnFotoKtp)
    public void onProfileBtnFotoKtpClicked() {

        pickImage("KTP");
    }

    @OnClick(R.id.profile_btnKembali)
    public void onProfileBtnKembaliClicked() {

        ProfileFragment fragment = new ProfileFragment();

        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.nav_host_fragment, fragment, ProfileFragment.class.getSimpleName())
            .addToBackStack(null)
            .commit();
    }

    @OnClick(R.id.profile_btnSimpan)
    public void onProfileBtnSimpanClicked() {

        new AlertDialog.Builder(this.mContext)
            .setTitle("Kirim Data")
            .setMessage("Apakah anda yakin ingin mengirimkan data ini?")
            .setPositiveButton("Kirim", (dialog, which) -> {
                ProgressDialog loading;
                loading = ProgressDialog.show(mContext, null, "Menyimpan data profile...", true, false);

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + spManager.getAccessToken());
                headers.put("Accept", "application/json");

                mBaseApiService.updateDataProfile(headers,
                    profileEtNama.getText().toString(),
                    profileEtAlamat.getText().toString(),
                    profileEtTelp.getText().toString(),
                    profileEtKtpNomor.getText().toString(),
                    profileEtNamaBank.getText().toString(),
                    profileEtNoRekening.getText().toString(),
                    profileEtNamaPemilikRekening.getText().toString(),
                    (this.decoded_foto != null ? getStringImage("FOTO", decoded_foto) : "NO_IMAGE"),
                    (this.decoded_ktp != null ? getStringImage("KTP", decoded_ktp) : "NO_IMAGE")
                ).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            try {

                                JSONObject jsonObject = new JSONObject(response.body().string());
                                String error_message = jsonObject.getString("error_msg");
                                String foto = jsonObject.getString("foto");
                                String ktp_foto = jsonObject.getString("ktp_foto");

                                spManager.saveString(SPManager.FOTO,foto);
                                spManager.saveString(SPManager.KTP_FOTO,ktp_foto);

                                Toasty.success(mContext, error_message, Toasty.LENGTH_LONG).show();

                                ProfileFragment fragment = new ProfileFragment();

                                getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.nav_host_fragment, fragment, ProfileFragment.class.getSimpleName())
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


            }).setNegativeButton("Batal", null).show();
    }
}
