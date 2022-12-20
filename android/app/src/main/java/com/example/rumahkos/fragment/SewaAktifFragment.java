package com.example.rumahkos.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
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
import com.example.rumahkos.WebAppInterface;
import com.example.rumahkos.adapter.SewaAdapter;
import com.example.rumahkos.model.SewaModel;
import com.example.rumahkos.modellist.SewaModelList;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SewaAktifFragment extends Fragment {

    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;
    ProgressDialog loading;

    private SewaAdapter adapter;
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

                HomeFragment mf = new HomeFragment();
                FragmentTransaction ft = requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_host_fragment, mf, SewaAktifFragment.class.getSimpleName())
                    .addToBackStack(null);
                ft.commit();

                return true;
            }
            return false;
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sewa_list, container, false);

        swipe = root.findViewById(R.id.sewa_swipeContainer);
//        TextView verifikasi_msg = root.findViewById(R.id.sewa_verifikasi_msg);

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);

//        if(spManager.getStatusVerifikasi().equals("AKT")){
//            verifikasi_msg.setVisibility(View.GONE);
//        }

//        androidx.appcompat.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
//        toolbar.setTitle("Data Sewa Aktif");

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

        mBaseApiService.getSewa(headers).enqueue(new Callback<SewaModelList>() {
            @Override
            public void onResponse(@NonNull Call<SewaModelList> call, @NonNull Response<SewaModelList> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
//                    generateRecyclerList(Objects.requireNonNull(response.body()).getArrayList());

                    if(response.body().getArrayList().isEmpty()){
                        TextView empty_msg = requireView().findViewById(R.id.sewa_aktif_list_empty_msg);

                        empty_msg.setVisibility(View.VISIBLE);
                    }else{
                        generateRecyclerList(Objects.requireNonNull(response.body()).getArrayList());
                    }


                } else {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<SewaModelList> call, Throwable t) {
                Toasty.error(mContext, "Ada kesalahan! :: " + t.getMessage(), Toast.LENGTH_LONG, true).show();
                loading.dismiss();
            }
        });


    }

    private void generateRecyclerList(ArrayList<SewaModel> modelList) {

        recyclerView = requireView().findViewById(R.id.sewa_recyclerList);
        adapter = new SewaAdapter(modelList);

        adapter.onBindCallBack = (jenis, viewHolder, position) -> {

            int idSewa = modelList.get(position).getId();

            if ("btnStopSewaOnClick".equals(jenis)) {

                if(spManager.getLevel().equals("penyewa")){

                    new AlertDialog.Builder(this.mContext)
                        .setTitle("Berhenti Sewa")
                        .setMessage("Apakah anda yakin ingin mengajukan berhenti sewa?")
                        .setPositiveButton("Kirim", (dialog, which) -> {
                            ProgressDialog loading;
                            loading = ProgressDialog.show(mContext, null, "Mengirimkan permintaan...", true, false);

                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Authorization", "Bearer " + spManager.getAccessToken());
                            headers.put("Accept", "application/json");

                            mBaseApiService.permintaanBerhentiSewa(
                                headers,
                                idSewa
                            ).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        loading.dismiss();
                                        try {

                                            JSONObject jsonObject = new JSONObject(response.body().string());
                                            String error_message = jsonObject.getString("error_msg");

                                            Toasty.success(mContext, error_message, Toasty.LENGTH_LONG).show();

                                            loadData();

                                        } catch (IOException | JSONException e) {
                                            Log.i("ERROR", e.getMessage());
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

                }else{
                    new AlertDialog.Builder(this.mContext)
                        .setTitle("Setujui permintaan berhenti Sewa")
                        .setMessage("Apakah anda yakin ingin menyetujui permohonan ini?")
                        .setPositiveButton("Kirim", (dialog, which) -> {
                            ProgressDialog loading;
                            loading = ProgressDialog.show(mContext, null, "Mengirimkan status permohonan...", true, false);

                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Authorization", "Bearer " + spManager.getAccessToken());
                            headers.put("Accept", "application/json");

                            mBaseApiService.menyetujuiBerhentiSewa(
                                headers,
                                idSewa
                            ).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        loading.dismiss();
                                        try {

                                            JSONObject jsonObject = new JSONObject(response.body().string());
                                            String error_message = jsonObject.getString("error_msg");

                                            Toasty.success(mContext, error_message, Toasty.LENGTH_LONG).show();

                                            loadData();

                                        } catch (IOException | JSONException e) {
                                            Log.i("ERROR", e.getMessage());
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


            }else if("btnQrCodeOnClick".equals(jenis)){
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                LayoutInflater inflater = getLayoutInflater();
                View root = inflater.inflate(R.layout.fragment_qrcode, null);
                dialog.setView(root);
                dialog.setCancelable(true);
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setTitle("QRCode");

                ImageView qrcode = root.findViewById(R.id.imViewQrcode);
                Picasso.get().invalidate("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + idSewa);
                Picasso.get().load("https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + idSewa).into(qrcode);

                dialog.setNegativeButton("TUTUP", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();

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
