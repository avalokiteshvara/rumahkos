package com.example.rumahkos.model;

import com.google.gson.annotations.SerializedName;

//https://stackoverflow.com/questions/18656102/spinner-with-key-value-pair
public class WilayahModel {

    @SerializedName("kode")
    private String kode;
    @SerializedName("nama")
    private String nama;

    public WilayahModel(String kode, String nama) {
        this.kode = kode;
        this.nama = nama;
    }


    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    //to display object as a string in spinner
    @Override
    public String toString() {
        return nama;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WilayahModel) {
            WilayahModel c = (WilayahModel) obj;
            return c.getKode().equals(kode);
        }

        return false;
    }

}
