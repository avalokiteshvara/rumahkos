package com.example.rumahkos.util.api;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class UtilsApi {

    public static final String BASE_URL = "https://webhomekos.hostingerapp.com/";
//    public static final String BASE_URL = "http://192.168.8.101/rumahkos/public/";
    public static final String BASE_URL_API = BASE_URL + "api/";
    public static final String BASE_URL_WEBVIEW = BASE_URL + "webview/";

    // Mendeklarasikan Interface BaseApiService
    public static BaseApiService getAPIService() {
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }

    public static String formatRupiah(double nominal){
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);

        return kursIndonesia.format(nominal);
    }
}
