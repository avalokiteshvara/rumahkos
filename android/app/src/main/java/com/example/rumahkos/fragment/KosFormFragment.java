package com.example.rumahkos.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.WebAppInterface;
import com.example.rumahkos.adapter.SliderAdapter;
import com.example.rumahkos.model.SliderImageModel;
import com.example.rumahkos.model.WilayahModel;
import com.example.rumahkos.modellist.WilayahModelList;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class KosFormFragment extends Fragment {

    public final int REQUEST_CAMERA = 0;
    public final int OPEN_SETTING = 1;

    Context mContext;
    BaseApiService mBaseApiService;
    //ProgressDialog loading;
    SPManager spManager;
    Bitmap bitmap, decoded;
    int bitmap_size = 60; // range 1 - 100
    String mCurrentPhotoPath;

    //slider
//    SliderView sliderView;
    private SliderAdapter adapter;


    @BindView(R.id.formkos_imgViewFoto)
    SliderView sliderView;
    @BindView(R.id.formkos_imgViewCamera)
    ImageView imgViewCamera;
    @BindView(R.id.formkos_etNama)
    EditText etNama;
    @BindView(R.id.formkos_etJmlKamar)
    EditText etJmlKamar;
    @BindView(R.id.formkos_etAlamat)
    EditText etAlamat;
    @BindView(R.id.formkos_spKecamatan)
    Spinner spKecamatan;
    @BindView(R.id.formkos_spKelurahan)
    Spinner spKelurahan;
    @BindView(R.id.formkos_etTelepon)
    EditText etTelepon;
    @BindView(R.id.formkos_spJenisKos)
    Spinner spJenisKos;
    @BindView(R.id.formkos_etDeskripsi)
    EditText etDeskripsi;
    @BindView(R.id.formkos_etFasilitas)
    EditText etFasilitas;
    @BindView(R.id.formkos_etHargaSewa)
    EditText etHargaSewa;
    @BindView(R.id.formkos_btnKembali)
    Button btnKembali;
    @BindView(R.id.formkos_btnSimpan)
    Button btnSimpan;
    @BindView(R.id.input_group)
    LinearLayout inputGroup;
    @BindView(R.id.formkos_btnLokasi)
    Button formkosBtnLokasi;
    @BindView(R.id.formkos_verifikasi_msg)
    TextView tvVerifikasiMsg;
    @BindView(R.id.txt_lat_lng)
    TextView txtLatLng;



    private int kos_id;
    private String lat;
    private String lng;

    private String wilayah_kos;
    private String selected_wilayah;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    //Pressed return button
    public void onResume() {
        super.onResume();
        requireView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {

            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                HomeFragment mf = new HomeFragment();
                FragmentTransaction ft = requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_host_fragment, mf, KosFormFragment.class.getSimpleName())
                    .addToBackStack(null);
                ft.commit();

                return true;
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_form_kos, container, false);
        ButterKnife.bind(this, root);

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);


        //slider

        adapter = new SliderAdapter(mContext);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();

        if(spManager.getStatusVerifikasi().equals("Y") || spManager.getLevel().equals("penyewa")){
            tvVerifikasiMsg.setVisibility(View.GONE);
        }

        Bundle arguments = getArguments();
        if (arguments == null)
            Toast.makeText(getActivity(), "Arguments is NULL", Toast.LENGTH_LONG).show();
        else {
            kos_id = getArguments().getInt("kos_id", 0);
            lat = getArguments().getString("lat","");
            lng = getArguments().getString("lng","");

            txtLatLng.setText(String.format("{%s|%s}", lat, lng));
        }


//        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
//        toolbar.setTitle("Data Rumah Kos");

//        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bar);
//        if (KosFormFragment.this != null){
//            bottomAppBar.performHide();
//        }

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

            //tampilkan search filter
            ((MainActivity) getActivity()).setmStateActionFilter(false);
            ((MainActivity) getActivity()).invalidateOptionsMenu();

        }

        if (kos_id != 0) {
            loadData();
        } else {

            new getKecamatan("00.00.00").execute();
            KosFormFragment.this.wilayah_kos = "00.00.00";

            //Picasso.get().invalidate(UtilsApi.BASE_URL + "uploads/" + "house_default.jpg");
            //Picasso.get().load(UtilsApi.BASE_URL + "uploads/" + "house_default.jpg").into(imgViewFoto);

            this.addNewItem(UtilsApi.BASE_URL + "uploads/" + "house_default.jpg");

        }


        //

        //arrKecamatan = new ArrayList<>();
        //arrKecamatan.add(new WilayahModel("0","Pilih Kecamatan"));

        spKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                WilayahModel kecamatan = (WilayahModel) adapterView.getSelectedItem();
                //Toast.makeText(context, "Country ID: "+country.getId()+",  Country Name : "+country.getName(), Toast.LENGTH_SHORT).show();
                new getKelurahan(kecamatan.getKode(), KosFormFragment.this.wilayah_kos).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spKelurahan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                WilayahModel wilayahModel = (WilayahModel) adapterView.getSelectedItem();
                selected_wilayah = wilayahModel.getKode();
                Log.i("SELECTED WILAYAH", selected_wilayah);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        etFasilitas.setOnTouchListener((view, motionEvent) -> {
            if (etFasilitas.hasFocus()) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_SCROLL:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                }
            }
            return false;
        });

        return root;
    }


    public void addNewItem(String url) {
        SliderImageModel sliderItem = new SliderImageModel();
        sliderItem.setDescription("Gambar");
        sliderItem.setImageUrl(url);
        adapter.addItem(sliderItem);
    }

    @OnClick(R.id.formkos_btnLokasi)
    public void formkos_btnLokasiClicked() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        LayoutInflater inflater = getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_map, null);
        dialog.setView(root);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Detail Rumah KOS");

        SwipeRefreshLayout swipe;
        swipe = root.findViewById(R.id.map_swipeContainer);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //loadWeb(root, swipe);
                //swipe.setRefreshing(false);
            }
        });

        loadWebViewLokasi(root, swipe);
        swipe.setEnabled(false);

        dialog.setNegativeButton("TUTUP", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebViewLokasi(View root, SwipeRefreshLayout swipe) {

        WebView mWebView = root.findViewById(R.id.map_webview);
        mWebView.addJavascriptInterface(new WebAppInterface(mContext, this.kos_id), "Android");
        mWebView.loadUrl(UtilsApi.BASE_URL_WEBVIEW + "get-location/" + this.lat + "/" + this.lng);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        swipe.setRefreshing(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                mWebView.loadUrl("file:///android_asset/error.html");
            }

            public void onPageFinished(WebView view, String url) {
                //ketika loading selesai, ison loading akan hilang
                swipe.setRefreshing(false);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //loading akan jalan lagi ketika masuk link lain
                // dan akan berhenti saat loading selesai
                if (100 == mWebView.getProgress()) {
                    swipe.setRefreshing(false);
                } else {
                    swipe.setRefreshing(true);
                }
            }
        });

    }

    @OnClick(R.id.formkos_imgViewCamera)
    public void formkos_imgViewCameraClicked() {
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

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        // imageView.setImageBitmap(decoded);
        //imgViewFoto.setImageBitmap(decoded); : TODO
    }

    private String getStringImage(@NotNull Bitmap bmp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
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

    private void selectValue(Spinner spinner, Object value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void loadData() {

        ProgressDialog loading;

        loading = ProgressDialog.show(mContext, null, "Mengambil data kos...", true, false);

        Log.e("LOGIN", "Mulai login");

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + spManager.getAccessToken());
        headers.put("Accept", "application/json");

        mBaseApiService.loadDetailKos(headers, this.kos_id)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        loading.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());

                            Log.i("LOGIN", "JSONObject :" + jsonObject.toString());

                            if (jsonObject.getString("error").equals("false")) {

                                String nama = jsonObject.getJSONObject("kos").getString("nama");
                                String alamat = jsonObject.getJSONObject("kos").getString("alamat");
                                String telp = jsonObject.getJSONObject("kos").getString("telp");
                                String foto = jsonObject.getJSONObject("kos").getString("foto");
                                String tipe = jsonObject.getJSONObject("kos").getString("tipe");
                                String fasilitas = jsonObject.getJSONObject("kos").getString("fasilitas");
                                String deskripsi = jsonObject.getJSONObject("kos").getString("deskripsi");
                                String jml_kamar = jsonObject.getJSONObject("kos").getString("jml_kamar");
                                String harga_sewa = jsonObject.getJSONObject("kos").getString("harga_sewa");
                                String tipe_kos = jsonObject.getJSONObject("kos").getString("tipe");

                                if (tipe_kos.equals("PTR")) {
                                    selectValue(spJenisKos, "KOS PUTRA");
                                } else if (tipe_kos.equals("PUT")) {
                                    selectValue(spJenisKos, "KOS PUTRI");
                                } else {
                                    selectValue(spJenisKos, "KOS CAMPUR");
                                }

//                                String[] arrTipeKos = {"KOS PUTRA","KOS PUTRI","KOS CAMPUR"};

                                //set wilayah
                                String wilayah = jsonObject.getJSONObject("kos").getString("wilayah");
                                new getKecamatan(wilayah).execute();
                                KosFormFragment.this.wilayah_kos = wilayah;

                                //Picasso.get().invalidate(UtilsApi.BASE_URL + "uploads/" + foto);
                                //Picasso.get().load(UtilsApi.BASE_URL + "uploads/" + foto).into(imgViewFoto);

                                String[] img = foto.split(",");
                                for (int i = 0; i < img.length; i++) {
                                    KosFormFragment.this.addNewItem(UtilsApi.BASE_URL + "uploads/" + img[i]);
                                }

                                etNama.setText(nama);
                                etAlamat.setText(alamat);
                                etTelepon.setText(telp);
                                etFasilitas.setText(fasilitas);
                                etDeskripsi.setText(deskripsi);
                                etJmlKamar.setText(jml_kamar);
                                etHargaSewa.setText(harga_sewa);

                                if (spManager.getLevel().equals("penyewa")) {
                                    btnSimpan.setVisibility(View.GONE);
                                }


                            } else {
                                String error_message = jsonObject.getString("error_msg");
                                Toasty.error(mContext, error_message, Toasty.LENGTH_LONG).show();
                                Log.i("LOGIN", "Login GAGAL : " + error_message);
                            }
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

    @OnClick(R.id.formkos_btnKembali)
    public void onFormkosBtnKembaliClicked() {

        KosListFragment fragment = new KosListFragment();

        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.nav_host_fragment, fragment, KosListFragment.class.getSimpleName())
            .addToBackStack(null)
            .commit();
    }

    @OnClick(R.id.formkos_btnSimpan)
    public void onFormkosBtnSimpanClicked() {

        new AlertDialog.Builder(this.mContext)
            .setTitle("Kirim Data")
            .setMessage("Apakah anda yakin ingin mengirimkan data ini?")
            .setPositiveButton("Kirim", (dialog, which) -> {

                ProgressDialog loading;

                loading = ProgressDialog.show(mContext, null, "Menyimpan data kos...", true, false);

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + spManager.getAccessToken());
                headers.put("Accept", "application/json");

                mBaseApiService.updateDataKos(headers,
                    this.kos_id,
                    etNama.getText().toString(),
                    etAlamat.getText().toString(),
                    selected_wilayah,
                    etTelepon.getText().toString(),
                    spJenisKos.getSelectedItem().toString(),
                    etFasilitas.getText().toString(),
                    etDeskripsi.getText().toString(),
                    etJmlKamar.getText().toString(),
                    etHargaSewa.getText().toString(),
                    (this.decoded != null ? getStringImage(decoded) : "NO_IMAGE"),
                    spManager.getLatitude(),
                    spManager.getLongitude()
                ).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            loading.dismiss();
                            try {

                                JSONObject jsonObject = new JSONObject(response.body().string());

                                if (jsonObject.getString("error").equals("false")) {
                                    String error_message = jsonObject.getString("error_msg");
                                    Toasty.success(mContext, error_message, Toasty.LENGTH_LONG).show();

                                    KosListFragment fragment = new KosListFragment();

                                    getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.nav_host_fragment, fragment, KosListFragment.class.getSimpleName())
                                        .addToBackStack(null)
                                        .commit();

                                } else {
                                    String error_message = jsonObject.getString("error_msg");
                                    Toasty.error(mContext, error_message, Toasty.LENGTH_LONG).show();

                                }

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

    private class getKelurahan extends AsyncTask<Void, Void, Void> {

        private String kode_kecamatan;
        private String wilayah;
        //private ProgressDialog loading;

        getKelurahan(String kode_kecamatan, String wilayah) {
            this.kode_kecamatan = kode_kecamatan;
            this.wilayah = wilayah;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //loading = ProgressDialog.show(mContext, null, "Mengambil data kelurahan...", true, false);
        }

        @Override
        protected Void doInBackground(Void... voids) {


            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + spManager.getAccessToken());
            headers.put("Accept", "application/json");

            mBaseApiService.getKelurahan(headers, this.kode_kecamatan)
                .enqueue(new Callback<WilayahModelList>() {
                    @Override
                    public void onResponse(@NotNull Call<WilayahModelList> call, @NotNull Response<WilayahModelList> response) {
                        if (response.isSuccessful()) {
                            //loading.dismiss();

                            ArrayList<WilayahModel> arrkelurahan = Objects.requireNonNull(response.body()).getArrayList();

                            ArrayAdapter<WilayahModel> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, arrkelurahan);
                            spKelurahan.setAdapter(adapter);
                            //Log.e("GET POSITION",Integer.toString(adapter.getPosition(new WilayahModel("35.10.25","Blimbingsari")) ));
                            spKelurahan.setSelection(adapter.getPosition(new WilayahModel(wilayah, "")));


                        } else {
                            //loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<WilayahModelList> call, Throwable t) {
                        Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        //loading.dismiss();
                    }
                });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

    private class getKecamatan extends AsyncTask<Void, Void, Void> {

        //private ProgressDialog loading;
        private String wilayah;

        getKecamatan(String wilayah) {
            this.wilayah = wilayah;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //spKelurahan.getSelectedView().setEnabled(false);
            spKelurahan.setEnabled(false);
            spKelurahan.setAlpha(0.5f);
//            spKelurahan.setBackgroundColor(Color.GRAY);
            //loading = ProgressDialog.show(mContext, null, "Mengambil data kecamatan...", true, false);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + spManager.getAccessToken());
            headers.put("Accept", "application/json");

            mBaseApiService.getKecamatan(headers)
                .enqueue(new Callback<WilayahModelList>() {
                    @Override
                    public void onResponse(@NotNull Call<WilayahModelList> call, @NotNull Response<WilayahModelList> response) {
                        if (response.isSuccessful()) {
                            //loading.dismiss();
                            //JSONObject jsonObject = new JSONObject(response.body().getArrayList().toString());
                            ArrayList<WilayahModel> arrKecamatan = Objects.requireNonNull(response.body()).getArrayList();

                            ArrayAdapter<WilayahModel> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, arrKecamatan);
                            spKecamatan.setAdapter(adapter);
                            //Log.e("GET POSITION",Integer.toString(adapter.getPosition(new WilayahModel("35.10.25","Blimbingsari")) ));
                            spKecamatan.setSelection(adapter.getPosition(new WilayahModel(wilayah.substring(0, 8), "")));

                        } else {
                            // loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<WilayahModelList> call, Throwable t) {
                        Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        //loading.dismiss();
                    }
                });

            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //spKelurahan.getSelectedView().setEnabled(true);
            spKelurahan.setEnabled(true);
            spKelurahan.setAlpha(1.0f);
        }
    }


}
