package com.example.rumahkos.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class FormBookingFragment extends Fragment {


    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;
    int kos_id;
    String namaKos;
    Double hargaSewa;
    @BindView(R.id.booking_etNamaKos)
    EditText bookingEtNamaKos;
    @BindView(R.id.booking_etHargaPerBulan)
    EditText bookingEtHargaPerBulan;
    @BindView(R.id.booking_etJmlBulan)
    EditText bookingEtJmlBulan;
    @BindView(R.id.booking_etHargaTotal)
    EditText bookingEtHargaTotal;
    @BindView(R.id.booking_btnBatal)
    Button bookingBtnBatal;
    @BindView(R.id.booking_btnBooking)
    Button bookingBtnBooking;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_form_booking, container, false);

        ButterKnife.bind(this, root);

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);

        Bundle arguments = getArguments();
        if (arguments == null)
            Toast.makeText(getActivity(), "Arguments is NULL", Toast.LENGTH_LONG).show();
        else {
            kos_id = getArguments().getInt("kos_id", 0);
            hargaSewa = getArguments().getDouble("harga_sewa", 0);
            namaKos = getArguments().getString("nama_kos", "");

        }

        Log.e("harga_sewa",hargaSewa.toString());

        bookingEtNamaKos.setText(namaKos);
        bookingEtHargaPerBulan.setText(UtilsApi.formatRupiah(hargaSewa));

        bookingEtJmlBulan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int befora, int count) {
                if(charSequence.length() != 0){
                    if(bookingEtJmlBulan.getText().toString().equals("")){
                        bookingEtJmlBulan.setText("0");
                    }
                    double total_bayar = hargaSewa * Double.parseDouble(bookingEtJmlBulan.getText().toString());
                    bookingEtHargaTotal.setText(String.format("%s", UtilsApi.formatRupiah(total_bayar)));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bookingEtJmlBulan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean has_focus) {
                if(!has_focus){
                    if(bookingEtJmlBulan.getText().toString().equals("")){
                        bookingEtJmlBulan.setText("0");
                    }

                    Double total_bayar = hargaSewa * Double.parseDouble(bookingEtJmlBulan.getText().toString());
                    bookingEtHargaTotal.setText(String.format("%s", UtilsApi.formatRupiah(total_bayar)));
                }
            }
        });

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
//
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
//
//
        }

        return root;

    }

    @OnClick(R.id.booking_btnBatal)
    public void onBookingBtnBatalClicked() {

        KosListFragment fragment = new KosListFragment();
        AppCompatActivity activity = (AppCompatActivity) getView().getContext();

        activity.getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.nav_host_fragment, fragment, KosListFragment.class.getSimpleName())
            .addToBackStack(null)
            .commit();

    }

    @OnClick(R.id.booking_btnBooking)
    public void onBookingBtnBookingClicked() {

        String etJmlBulan = bookingEtJmlBulan.getText().toString().trim();

        if(TextUtils.isEmpty(etJmlBulan)){
            bookingEtJmlBulan.setError("Jumlah Booking Bulan diperlukan");
            return;
        }

        ProgressDialog loading;

        loading = ProgressDialog.show(mContext, null, "Mengirimkan data...", true, false);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + spManager.getAccessToken());
        headers.put("Accept", "application/json");

        mBaseApiService.booking(headers,
            this.kos_id,
            Integer.parseInt(etJmlBulan)
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String error_message = jsonObject.getString("error_msg");
                        Toasty.success(mContext, error_message, Toasty.LENGTH_LONG).show();

                        KosListFragment fragment = new KosListFragment();

                        getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, fragment, KosListFragment.class.getSimpleName())
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
