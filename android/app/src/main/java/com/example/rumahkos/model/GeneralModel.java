package com.example.rumahkos.model;

public class GeneralModel {

    private String kode;
    private String nama;

    public GeneralModel(String kode, String nama) {
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

    @Override
    public String toString() {
        return nama;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GeneralModel) {
            GeneralModel c = (GeneralModel) obj;
            return c.getKode().equals(kode);
        }

        return false;
    }


}
