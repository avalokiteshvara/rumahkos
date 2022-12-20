package com.example.rumahkos.modellist;

import com.example.rumahkos.model.KosModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class KosModelList {

    @SerializedName("dataList")
    private ArrayList<KosModel> arrayList;

    public ArrayList<KosModel> getArrayList() {
        return arrayList;    }

    public void setArraylList(ArrayList<KosModel> arraylList) {
        this.arrayList = arraylList;
    }

}
