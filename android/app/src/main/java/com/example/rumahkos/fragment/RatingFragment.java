package com.example.rumahkos.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingFragment extends Fragment {
    @BindView(R.id.penilaian)
    RatingBar penilaian;
    @BindView(R.id.taKomentarRating)
    EditText taKomentarRating;
    @BindView(R.id.submit)
    Button submit;

    private Context mContext;
    private BaseApiService mBaseApiService;
    private ProgressDialog loading;
    private float rating;
    SPManager spManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_form_rating, container, false);
        ButterKnife.bind(this, root);

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);

//        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
//        toolbar.setTitle("Rating pelayanan");

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

        rating = requireArguments().getFloat("rating_nilai");
        penilaian.setRating(rating);

        String komentar = getArguments().getString("rating_komentar");
        byte[] decodedBytes = Base64.getDecoder().decode(komentar);
        taKomentarRating.setText(new String(decodedBytes));

        return root;
    }


    private void updateRating() {
        loading = ProgressDialog.show(mContext, null, "Mengirimkan penilaian, Mohon tunggu...", true, false);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + spManager.getAccessToken());
        headers.put("Accept", "application/json");

        mBaseApiService.updateRating(
            headers,
            getArguments().getInt("sewa_id"),
            penilaian.getRating(),
            taKomentarRating.getText().toString()
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("error").equals("false")) {

                            Toast.makeText(mContext, "Terimakasih atas penilaian anda", Toast.LENGTH_SHORT).show();

                            RiwayatKosFragment mf = new RiwayatKosFragment();
                            FragmentTransaction ft = requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.nav_host_fragment, mf, RatingFragment.class.getSimpleName())
                                .addToBackStack(null);
                            ft.commit();

                        } else {
                            String error_message = jsonObject.getString("error_msg");
                            Toasty.error(mContext, error_message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("debug", "onFailure: ERROR > " + t.toString());
                loading.dismiss();
            }
        });
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {

        if (rating > 0.0) {

            new AlertDialog.Builder(mContext)
                .setTitle("Merubah penilaian")
                .setMessage("Apakah anda ingin merubah penilaian anda?")
                .setPositiveButton("UBAH", (dialog, which) -> {
                    updateRating();
                }).setNegativeButton("Batal", null).show();


        } else {
            updateRating();
        }
    }

}
