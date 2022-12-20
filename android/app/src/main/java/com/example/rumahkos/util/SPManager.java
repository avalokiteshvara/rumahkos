package com.example.rumahkos.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPManager {

    public static final String USER_ID = "USER_ID";
    public static final String LOGIN_STATUS = "LOGIN_STATUS";
    public static final String NAMA = "NAMA";
    public static final String TELP = "TELP";
    public static final String ALAMAT = "ALAMAT";
    public static final String FOTO = "FOTO";
    public static final String KTP_FOTO = "KTP_FOTO";
    public static final String KTP_NOMOR = "KTP_NOMOR";
    public static final String LEVEL = "LEVEL";
    public static final String EMAIL = "EMAIL";
    public static final String REKENING = "REKENING";
    public static final String REKENING_ADMIN = "REKENING_ADMIN";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String STATUS_VERIFIKASI = "STATUS_VERIFIKASI";
    public static final String KOS_SEARCH_FILTER = "KOS_SEARCH_FILTER";

    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    public SPManager(Context context) {
        pref = context.getSharedPreferences("KAMARKOS_APP", Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public String getKosSearchFilter() {
        return pref.getString(KOS_SEARCH_FILTER, "");
    }

    public String getTelp() {
        return pref.getString(TELP, "");
    }


    public String getLatitude() {
        return pref.getString(LATITUDE, "");
    }
    public String getLongitude() {
        return pref.getString(LONGITUDE, "");
    }

    public String getAlamat() {
        return pref.getString(ALAMAT, "");
    }

    public String getFoto() {
        return pref.getString(FOTO, "");
    }

    public String getFotoKtp() {
        return pref.getString(KTP_FOTO, "");
    }

    public String getKtpNomor() {
        return pref.getString(KTP_NOMOR, "");
    }

    public String getLevel() {
        return pref.getString(LEVEL, "");
    }

    public Boolean is_user_logged_in() {
        return pref.getBoolean(LOGIN_STATUS, false);
    }

    public String getNama() {
        return pref.getString(NAMA, "");
    }

    public String getEmail() {
        return pref.getString(EMAIL, "");
    }

    public String getRekening(){return pref.getString(REKENING,"");}

    public String getRekeningAdmin(){return pref.getString(REKENING_ADMIN,"");}

    public String getAccessToken() {
        return pref.getString(ACCESS_TOKEN, "");
    }

    public int getUserId() {
        return pref.getInt(USER_ID, 0);
    }

    public String getStatusVerifikasi(){return  pref.getString(STATUS_VERIFIKASI,"");}

    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void defaultSearchFilter() {
        //kecamatan|kelurahan|tipe|harga_max
        this.saveString(KOS_SEARCH_FILTER, "00.00.00|00.00.00.0000|All|All");
    }
}
