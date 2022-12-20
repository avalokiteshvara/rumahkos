package com.example.rumahkos.model;

import com.google.gson.annotations.SerializedName;

public class TagihanModel {

    @SerializedName("id")
    private int id;

    @SerializedName("nama_kamar")
    private String nama_kamar;

    @SerializedName("tgl_sewa")
    private String tgl_sewa;

    @SerializedName("tgl_jatuh_tempo")
    private String tgl_jatuh_tempo;

    @SerializedName("bulan_sewa")
    private String bulan_sewa;

    @SerializedName("harga_total")
    private Double harga_total;

    @SerializedName("nama_penyewa")
    private String nama_penyewa;

    @SerializedName("status")
    private String status;

    @SerializedName("status_bayar")
    private String status_bayar;

    @SerializedName("nama_kos")
    private String nama_kos;

    @SerializedName("hari_jatuh_tempo")
    private int hari_jatuh_tempo;
    @SerializedName("kode_status_bayar")
    private String kode_status_bayar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama_kamar() {
        return nama_kamar;
    }

    public void setNama_kamar(String nama_kamar) {
        this.nama_kamar = nama_kamar;
    }

    public String getTgl_sewa() {
        return tgl_sewa;
    }

    public void setTgl_sewa(String tgl_sewa) {
        this.tgl_sewa = tgl_sewa;
    }

    public String getTgl_jatuh_tempo() {
        return tgl_jatuh_tempo;
    }

    public void setTgl_jatuh_tempo(String tgl_jatuh_tempo) {
        this.tgl_jatuh_tempo = tgl_jatuh_tempo;
    }

    public String getBulan_sewa() {
        return bulan_sewa;
    }

    public void setBulan_sewa(String bulan_sewa) {
        this.bulan_sewa = bulan_sewa;
    }

    public Double getHarga_total() {
        return harga_total;
    }

    public void setHarga_total(Double harga_total) {
        this.harga_total = harga_total;
    }

    public String getNama_penyewa() {
        return nama_penyewa;
    }

    public void setNama_penyewa(String nama_penyewa) {
        this.nama_penyewa = nama_penyewa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_bayar() {
        return status_bayar;
    }

    public void setStatus_bayar(String status_bayar) {
        this.status_bayar = status_bayar;
    }

    public String getNama_kos() {
        return nama_kos;
    }

    public void setNama_kos(String nama_kos) {
        this.nama_kos = nama_kos;
    }

    public int getHari_jatuh_tempo() {
        return hari_jatuh_tempo;
    }

    public void setHari_jatuh_tempo(int hari_jatuh_tempo) {
        this.hari_jatuh_tempo = hari_jatuh_tempo;
    }

    public String getKode_status_bayar() {
        return kode_status_bayar;
    }

    public void setKode_status_bayar(String kode_status_bayar) {
        this.kode_status_bayar = kode_status_bayar;
    }


}
