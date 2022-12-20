package com.example.rumahkos.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.adapter.KosAdapter;
import com.example.rumahkos.adapter.SliderAdapter;
import com.example.rumahkos.model.KosModel;
import com.example.rumahkos.model.SliderImageModel;
import com.example.rumahkos.modellist.KosModelList;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KosListFragment extends Fragment {

    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;
    ProgressDialog loading;
    private SliderAdapter imgAdapter;

    private KosAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipe;

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

                KosListFragment mf = new KosListFragment();
                FragmentTransaction ft = requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_host_fragment, mf, KosListFragment.class.getSimpleName())
                    .addToBackStack(null);
                ft.commit();

                return true;
            }
            return false;
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_kos_list, container, false);
        swipe = root.findViewById(R.id.kos_swipeContainer);
        TextView verifikasi_msg = root.findViewById(R.id.kos_verifikasi_msg);

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);

        if(spManager.getStatusVerifikasi().equals("Y") || spManager.getLevel().equals("penyewa")){
            verifikasi_msg.setVisibility(View.GONE);
        }

//        androidx.appcompat.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
//        toolbar.setTitle("Data Rumah Kos");

        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();
        if (floatingActionButton != null) {
            floatingActionButton.show();

            if(spManager.getLevel().equals("pemilik")){

                floatingActionButton.setOnClickListener(view -> {

                    Bundle bundle = new Bundle();
                    // bundle.putInt("put something key", someting value);
                    KosFormFragment fragment = new KosFormFragment();
                    fragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();

                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, fragment, KosListFragment.class.getSimpleName())
                            .addToBackStack(null)
                            .commit();
                });
            }else{
                floatingActionButton.hide();
            }

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

        //recyclerView = root.findViewById(R.id.kos_recyclerList);

        swipe.setOnRefreshListener(() -> {
            swipe.setRefreshing(false);
            loadData();
        });

        loadData();

        //tampilkan search filter
        ((MainActivity) getActivity()).setmStateActionFilter(true);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        return root;
    }

    private void loadData() {

        loading = ProgressDialog.show(mContext, null, "Mengambil data ...", true, false);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + spManager.getAccessToken());
        headers.put("Accept", "application/json");

        mBaseApiService.getKosList(headers, spManager.getKosSearchFilter()).enqueue(new Callback<KosModelList>() {
            @Override
            public void onResponse(@NonNull Call<KosModelList> call, @NonNull Response<KosModelList> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();


                    if(response.body().getArrayList().isEmpty()){
                        TextView empty_msg = requireView().findViewById(R.id.kos_list_empty_msg);

                        empty_msg.setVisibility(View.VISIBLE);
                    }else{
                        generateRecyclerList(Objects.requireNonNull(response.body()).getArrayList());
                    }

                } else {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<KosModelList> call, Throwable t) {
                Toasty.error(mContext, "Ada kesalahan! :: " + t.getMessage(), Toast.LENGTH_LONG, true).show();
                loading.dismiss();
            }
        });


    }

    private void generateRecyclerList(ArrayList<KosModel> modelList) {

        recyclerView = requireView().findViewById(R.id.kos_recyclerList);
        adapter = new KosAdapter(modelList);

        adapter.onBindCallBack = (jenis, viewHolder, position) -> {

            if ("btnDetailOnClick".equals(jenis)) {

                if(spManager.getLevel().equals("penyewa")){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.fragment_kos_detail, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(true);
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setTitle("Detail Rumah KOS");

                    SliderView sliderView;
                    RatingBar rbRating;
                    TextView tvJenisKos,tvHarga,tvNama,tvAlamat,tvDeskripsi,tvFasilitas,tvKamarTersisa;
                    Button btnDetail,btnMaps;

                    sliderView = dialogView.findViewById(R.id.kos_foto);
                    rbRating = (RatingBar) dialogView.findViewById(R.id.kos_rating);
                    tvJenisKos = (TextView) dialogView.findViewById(R.id.kos_tvJenis);
                    tvHarga = (TextView) dialogView.findViewById(R.id.kos_tvHarga);
                    tvNama = (TextView) dialogView.findViewById(R.id.kos_tvNama);
                    tvAlamat = (TextView) dialogView.findViewById(R.id.kos_tvAlamat);
                    tvDeskripsi = (TextView) dialogView.findViewById(R.id.kos_tvDeskripsi);

                    tvFasilitas = (TextView) dialogView.findViewById(R.id.kos_tvFasilitas);
                    tvKamarTersisa = (TextView) dialogView.findViewById(R.id.kos_tvSisaKamar);

                    btnDetail = (Button) dialogView.findViewById(R.id.kos_btnDetail);
                    btnMaps = (Button) dialogView.findViewById(R.id.kos_btnMaps);

                    btnDetail.setVisibility(View.GONE);
                    btnMaps.setVisibility(View.GONE);


                    imgAdapter = new SliderAdapter(mContext);
                    sliderView.setSliderAdapter(imgAdapter);
                    sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                    sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                    sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                    sliderView.setIndicatorSelectedColor(Color.WHITE);
                    sliderView.setIndicatorUnselectedColor(Color.GRAY);
                    sliderView.setScrollTimeInSec(3);
                    sliderView.setAutoCycle(true);
                    sliderView.startAutoCycle();


                    Map<String, String> mapJenisKos = new HashMap<String, String>();
                    mapJenisKos.put("PTR","KOS PUTRA");
                    mapJenisKos.put("PUT","KOS PUTRI");
                    mapJenisKos.put("CMP","KOS CAMPUR");

                    //Picasso.get().invalidate(UtilsApi.BASE_URL + "uploads/" +  modelList.get(position).getFoto());
                    //Picasso.get().load(UtilsApi  .BASE_URL + "uploads/" +  modelList.get(position).getFoto()).into(sliderView);

                    String foto = modelList.get(position).getFoto();
                    String[] img = foto.split(",");
                    for (int i = 0; i < img.length; i++) {
                        //KosFormFragment.this.addNewItem(UtilsApi.BASE_URL + "uploads/" + img[i]);

                        SliderImageModel sliderItem = new SliderImageModel();
                        sliderItem.setDescription("Gambar");
                        sliderItem.setImageUrl(UtilsApi.BASE_URL + "uploads/" + img[i]);
                        imgAdapter.addItem(sliderItem);
                    }


                    rbRating.setRating(Float.parseFloat(modelList.get(position).getRating()));
                    tvHarga.setText(UtilsApi.formatRupiah(Double.parseDouble(modelList.get(position).getHarga_sewa())));
                    tvJenisKos.setText(mapJenisKos.get(modelList.get(position).getTipe()));
                    tvNama.setText(modelList.get(position).getNama());
                    tvAlamat.setText(String.format(Locale.US,"%s ,%s - %s",modelList.get(position).getAlamat(), modelList.get(position).getKelurahan(),modelList.get(position).getKecamatan()));
                    tvDeskripsi.setText(modelList.get(position).getDeskripsi());
                    //tvFasilitas.setText(modelList.get(position).getFasilitas());

                    //load fasilitas
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + spManager.getAccessToken());
                    headers.put("Accept", "application/json");

                    mBaseApiService.getFasilitasList(headers,modelList.get(position).getFasilitas()).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                loading.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());

                                    Log.i("LOGIN", "JSONObject :" + jsonObject.toString());
                                    tvFasilitas.setText(jsonObject.getString("fasilitas"));

                                } catch (JSONException | IOException e) {
                                    Log.i("LOGIN", "Login GAGAL " + e.getMessage());
                                }

                            } else {
                                loading.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toasty.error(mContext, "Ada kesalahan! :: " + t.getMessage(), Toast.LENGTH_LONG, true).show();
                            loading.dismiss();
                        }
                    });

                    //==========


                    tvKamarTersisa.setText(String.format(Locale.US, "%s Kamar tersisa",modelList.get(position).getKmr_tersisa()));

                    btnDetail.setVisibility(View.GONE);
                    btnMaps.setVisibility(View.GONE);

                    dialog.setNegativeButton("TUTUP", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            dialog.dismiss();
                        }
                    });

                    dialog.setPositiveButton("BOOKING", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("kos_id", modelList.get(position).getId());
                            bundle.putString("nama_kos",modelList.get(position).getNama());
                            bundle.putDouble("harga_sewa",Double.parseDouble(modelList.get(position).getHarga_sewa()));

                            FormBookingFragment fragment = new FormBookingFragment();
                            fragment.setArguments(bundle);
                            Log.e("param",bundle.toString());
                            AppCompatActivity activity = (AppCompatActivity) getView().getContext();

                            activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.nav_host_fragment, fragment, FormBookingFragment.class.getSimpleName())
                                .addToBackStack(null)
                                .commit();


                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("kos_id", modelList.get(position).getId());
                    bundle.putString("lat",modelList.get(position).getLat());
                    bundle.putString("lng",modelList.get(position).getLng());

                    KosFormFragment fragment = new KosFormFragment();
                    fragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) getView().getContext();

                    activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment, KosFormFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
                }


            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

        };

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}
