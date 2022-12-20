package com.example.rumahkos.util.api;

import android.app.DownloadManager;

import com.example.rumahkos.modellist.KosModelList;
import com.example.rumahkos.modellist.SewaModelList;
import com.example.rumahkos.modellist.TagihanModelList;
import com.example.rumahkos.modellist.WilayahModelList;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BaseApiService {
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> login(
        @HeaderMap Map<String, String> headers,
        @Field("email") String email,
        @Field("password") String password
    );

    @FormUrlEncoded
    @POST("update-data-kos")
    Call<ResponseBody> updateDataKos(
        @HeaderMap Map<String, String> headers,
        @Field("id") int id,
        @Field("nama") String nama,
        @Field("alamat") String alamat,
        @Field("wilayah") String wilayah,
        @Field("telp") String telp,
        @Field("tipe") String tipe,
        @Field("fasilitas") String fasilitas,
        @Field("deskripsi") String deskripsi,
        @Field("jml_kamar") String jml_kamar,
        @Field("harga_sewa") String harga_sewa,
        @Field("foto") String foto,
        @Field("lat") String lat,
        @Field("lng") String lng
    );

    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> register(
        @Field("email") String email,
        @Field("nama") String nama,
        @Field("password") String password,
        @Field("password_confirmation") String password_confirmation,
        @Field("telp") String telp,
        @Field("level") String level,
        @Field("nama_bank") String nama_bank,
        @Field("nomor_rekening") String nomor_rekening,
        @Field("rekening_atas_nama") String rekening_atas_nama
    );

    @FormUrlEncoded
    @POST("send-firebase-token")
    Call<ResponseBody> sendFirebaseToken(
        @HeaderMap Map<String, String> headers,
        @Field("token_id") String token_id
    );

    @FormUrlEncoded
    @POST("update-lokasi")
    Call<ResponseBody> updateLokasi(
        @HeaderMap Map<String, String> headers,
        @Field("kos_id") int kos_id,
        @Field("lat") String lat,
        @Field("lng") String lng
    );


    @FormUrlEncoded
    @POST("booking")
    Call<ResponseBody> booking(
        @HeaderMap Map<String, String> headers,
        @Field("kos_id") int kos_id,
        @Field("bulan_sewa") int bulan_sewa
    );


    @FormUrlEncoded
    @POST("reset-password")
    Call<ResponseBody> resetPassword(
        @Field("email") String email
    );

    @GET("get-kos-list")
    Call<KosModelList> getKosList(
        @HeaderMap Map<String, String> headers,
        @Query("filter") String filter
    );

    @GET("load-detail-kos")
    Call<ResponseBody> loadDetailKos(
        @HeaderMap Map<String, String> headers,
        @Query("kos_id") int kos_id
    );

    @GET("get-kecamatan")
    Call<WilayahModelList> getKecamatan(
        @HeaderMap Map<String, String> headers
    );

    @GET("get-kelurahan")
    Call<WilayahModelList> getKelurahan(
        @HeaderMap Map<String, String> headers,
        @Query("kec_kode") String kec_kode
    );

    @GET("get-tagihan")
    Call<TagihanModelList> getTagihan(
        @HeaderMap Map<String, String> headers
    );

    @FormUrlEncoded
    @POST("bayar")
    Call<ResponseBody> bayar(
        @HeaderMap Map<String, String> headers,
        @Field("id") int id,
        @Field("nama_kamar") String nama_kamar,
        @Field("bulan_sewa") String bulan_sewa,
        @Field("harga") String harga,
        @Field("foto_pembayaran") String foto_pembayaran
    );


    @FormUrlEncoded
    @POST("update-sewa")
    Call<ResponseBody> updateSewa(
        @HeaderMap Map<String, String> headers,
        @Field("id") int id,
        @Field("nama_kamar") String nama_kamar,
        @Field("bulan_sewa") String bulan_sewa,
        @Field("harga") String harga
    );

    @GET("get-sewa")
    Call<SewaModelList> getSewa(
        @HeaderMap Map<String, String> headers
    );

    @FormUrlEncoded
    @POST("check-active-sewa")
    Call<ResponseBody> CheckActiveSewa(
        @HeaderMap Map<String, String> headers,
        @Field("sewa_id") String sewa_id
    );

    /*
    *  mBaseApiService.updateDataProfile(headers,
                    profileEtNama.getText().toString(),
                    profileEtAlamat.getText().toString(),
                    profileEtTelp.getText().toString(),
                    profileEtNamaBank.getText().toString(),
                    profileEtNoRekening.getText().toString(),
                    profileEtNamaPemilikRekening.getText().toString(),
                    (this.decoded_foto != null ? getStringImage("FOTO",decoded_foto) : "NO_IMAGE"),
                    (this.decoded_ktp != null ? getStringImage("KTP",decoded_ktp) : "NO_IMAGE")
    *
    * */
    Call<ResponseBody> updateDataProfile(HashMap<String, String> headers, String toString, String toString1, String toString2, String toString3, String toString4, String toString5, String foto, String ktp);

    @FormUrlEncoded
    @POST("update-data-profile")
    Call<ResponseBody> updateDataProfile(
        @HeaderMap Map<String, String> headers,
        @Field("nama") String nama,
        @Field("alamat") String alamat,
        @Field("telp") String telp,
        @Field("ktp_nomor") String ktp_nomor,
        @Field("nama_bank") String nama_bank,
        @Field("no_rekening") String no_rekening,
        @Field("nama_pemilik_rekening") String nama_pemilik_rekening,
        @Field("foto") String foto,
        @Field("ktp_foto") String ktp_foto
    );

    @FormUrlEncoded
    @POST("permintaan-berhenti-sewa")
    Call<ResponseBody> permintaanBerhentiSewa(
        @HeaderMap Map<String, String> headers,
        @Field("sewa_id") int sewa_id
    );

    @FormUrlEncoded
    @POST("menyetujui-berhenti-sewa")
    Call<ResponseBody> menyetujuiBerhentiSewa(
        @HeaderMap Map<String, String> headers,
        @Field("sewa_id") int sewa_id
    );

    @FormUrlEncoded
    @POST("update-rating")
    Call<ResponseBody> updateRating(
        @HeaderMap Map<String, String> headers,
        @Field("sewa_id") int id,
        @Field("rating_nilai") float rating_nilai,
        @Field("rating_komentar") String rating_komentar
    );


    @GET("get-fasilitas-list")
    Call<ResponseBody> getFasilitasList(
        @HeaderMap Map<String, String> headers,
        @Query("fasilitas_list") String set_fasilitas
    );
}
