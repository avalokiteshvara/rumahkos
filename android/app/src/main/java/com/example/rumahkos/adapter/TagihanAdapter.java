package com.example.rumahkos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rumahkos.R;
import com.example.rumahkos.bindcallback.OnTagihanBindCallBack;
import com.example.rumahkos.model.TagihanModel;
import com.example.rumahkos.util.api.UtilsApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TagihanAdapter extends RecyclerView.Adapter<TagihanAdapter.TagihanViewHolder> {
    public OnTagihanBindCallBack onBindCallBack;
    private ArrayList<TagihanModel> arrayList;

    public TagihanAdapter(ArrayList<TagihanModel> dataList) {
        this.arrayList = dataList;
    }

    @NonNull
    @Override
    public TagihanAdapter.TagihanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_tagihan, parent, false);
        return new TagihanAdapter.TagihanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagihanAdapter.TagihanViewHolder holder, int position) {

       /*
       *  TextView tvNamaKos;
        TextView tvNamaKamar;
        TextView tvNamaPenyewa;
        TextView tvTglJatuhTempo;
        TextView tvJmlBulanTagih;
        TextView tvNominalBayar;
        TextView tvKodeStatusBayar;

        Button btnBayar;
       * */

        holder.tvNamaKos.setText(arrayList.get(position).getNama_kos());
        holder.tvNamaKamar.setText(arrayList.get(position).getNama_kamar());
        holder.tvNamaPenyewa.setText(arrayList.get(position).getNama_penyewa());

        String tglJatuhTempo = arrayList.get(position).getTgl_jatuh_tempo();
        int hari_jatuh_tempo = arrayList.get(position).getHari_jatuh_tempo();

        String status_hari_jatuh_tempo = hari_jatuh_tempo > 0 ? "+" : "-";

        holder.tvTglJatuhTempo.setText(String.format(Locale.US, "%s ( %s %d hari )", tglJatuhTempo, status_hari_jatuh_tempo, hari_jatuh_tempo));
        holder.tvJmlBulanTagih.setText(String.format("%s Bulan", arrayList.get(position).getBulan_sewa()));

        String totalBayar = UtilsApi.formatRupiah(arrayList.get(position).getHarga_total());
        holder.tvNominalBayar.setText(totalBayar);


        Map<String, String> kodeStatusBayar = new HashMap<String, String>();
        kodeStatusBayar.put("PND", "Menunggu pembayaran");
        kodeStatusBayar.put("BBU", "Menunggu verifikasi pembayaran");
        kodeStatusBayar.put("BBV", "Pembayaran telah tervalidasi");
        kodeStatusBayar.put("BBI", "Bukti bayar tidak valid! Upload kembali");
        kodeStatusBayar.put("NTS", "Nominal bayar tidak sama! Upload kembali");

        String statusBayar = arrayList.get(position).getKode_status_bayar();
        holder.tvKodeStatusBayar.setText(kodeStatusBayar.get(statusBayar));

        holder.btnBayar.setOnClickListener(view -> {
            if (onBindCallBack != null) {
                onBindCallBack.OnTagihanViewBind("btnBayarOnClick", holder, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class TagihanViewHolder extends RecyclerView.ViewHolder {

        TextView tvNamaKos;
        TextView tvNamaKamar;
        TextView tvNamaPenyewa;
        TextView tvTglJatuhTempo;
        TextView tvJmlBulanTagih;
        TextView tvNominalBayar;
        TextView tvKodeStatusBayar;

        Button btnBayar;


        public TagihanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaKos = itemView.findViewById(R.id.tagihan_tvNamaKos);
            tvNamaKamar = itemView.findViewById(R.id.tagihan_tvNamaKamar);
            tvNamaPenyewa = itemView.findViewById(R.id.tagihan_tvNamaPenyewa);
            tvTglJatuhTempo = itemView.findViewById(R.id.tagihan_tvTglJatuhTempo);
            tvJmlBulanTagih = itemView.findViewById(R.id.tagihan_tvJmlBulanTagih);
            tvNominalBayar = itemView.findViewById(R.id.tagihan_tvNominalBayar);
            tvKodeStatusBayar = itemView.findViewById(R.id.tagihan_tvKodeStatusBayar);

            btnBayar = itemView.findViewById(R.id.tagihan_btnBayar);
        }
    }

}
