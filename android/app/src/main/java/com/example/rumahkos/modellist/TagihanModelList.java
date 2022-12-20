package com.example.rumahkos.modellist;

import com.example.rumahkos.model.KosModel;
import com.example.rumahkos.model.TagihanModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TagihanModelList {

    @SerializedName("dataList")
    private ArrayList<TagihanModel> arrayList;

    public ArrayList<TagihanModel> getArrayList() {
        return arrayList;    }

    public void setArraylList(ArrayList<TagihanModel> arraylList) {
        this.arrayList = arraylList;
    }

}
