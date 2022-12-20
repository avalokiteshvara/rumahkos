package com.example.rumahkos.bindcallback;

import com.example.rumahkos.adapter.KosAdapter;

public interface OnKosBindCallBack {
    void OnKosViewBind(String jenis, KosAdapter.KosViewHolder viewHolder, int position);

}
