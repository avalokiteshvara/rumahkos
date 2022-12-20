package com.example.rumahkos.modellist;

import com.example.rumahkos.model.KosModel;
import com.example.rumahkos.model.WilayahModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WilayahModelList {
    @SerializedName("dataList")
    private ArrayList<WilayahModel> arrayList;

    public ArrayList<WilayahModel> getArrayList() {
        return arrayList;    }

    public void setArraylList(ArrayList<WilayahModel> arraylList) {
        this.arrayList = arraylList;
    }
}
