package com.example.rumahkos.bindcallback;

import com.example.rumahkos.adapter.KosAdapter;
import com.example.rumahkos.adapter.TagihanAdapter;

public interface OnTagihanBindCallBack {
    void OnTagihanViewBind(String jenis, TagihanAdapter.TagihanViewHolder viewHolder, int position);
}
