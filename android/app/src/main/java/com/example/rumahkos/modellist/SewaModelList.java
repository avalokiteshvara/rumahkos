package com.example.rumahkos.modellist;

import com.example.rumahkos.model.SewaModel;
import com.example.rumahkos.model.TagihanModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SewaModelList {

    @SerializedName("dataList")
    private ArrayList<SewaModel> arrayList;

    public ArrayList<SewaModel> getArrayList() {
        return arrayList;    }

    public void setArraylList(ArrayList<SewaModel> arraylList) {
        this.arrayList = arraylList;
    }

}
