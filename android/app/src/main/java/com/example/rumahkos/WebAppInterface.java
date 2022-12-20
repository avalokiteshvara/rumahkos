package com.example.rumahkos;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.example.rumahkos.fragment.KosListFragment;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebAppInterface {
    Context mContext;
    int kos_id;
    BaseApiService mBaseApiService;
    SPManager spManager;

    /** Instantiate the interface and set the context */
    public WebAppInterface(Context c,int kos_id) {
        mContext = c;
        this.kos_id = kos_id;

        mBaseApiService = UtilsApi.getAPIService();
        spManager = new SPManager(mContext);
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String lat,String lng) {
        //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();

        if(this.kos_id > 0){
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + spManager.getAccessToken());
            headers.put("Accept", "application/json");

            spManager.saveString(SPManager.LATITUDE,lat);
            spManager.saveString(SPManager.LONGITUDE,lng);

            mBaseApiService.updateLokasi(
                headers, this.kos_id, lat, lng
            ).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        //TODO
                    } else {
                        //TODO
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toasty.error(mContext, "ERROR:" + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("debug", "onFailure: ERROR > " + t.toString());

                }
            });
        }else{
            spManager.saveString(SPManager.LATITUDE,lat);
            spManager.saveString(SPManager.LONGITUDE,lng);
        }
    }
}
