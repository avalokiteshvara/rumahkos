package com.example.rumahkos.bindcallback;

import com.example.rumahkos.adapter.SewaAdapter;


public interface OnSewaBindCallBack {
    void OnSewaViewBind(String jenis, SewaAdapter.SewaViewHolder viewHolder, int position);
}
