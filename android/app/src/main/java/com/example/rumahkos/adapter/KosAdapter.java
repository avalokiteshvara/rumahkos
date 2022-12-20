package com.example.rumahkos.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rumahkos.R;
import com.example.rumahkos.model.KosModel;
import com.example.rumahkos.bindcallback.OnKosBindCallBack;
import com.example.rumahkos.util.api.UtilsApi;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class KosAdapter extends RecyclerView.Adapter<KosAdapter.KosViewHolder> {
    public OnKosBindCallBack onBindCallBack;
    private ArrayList<KosModel> arrayList;

    public KosAdapter(ArrayList<KosModel> dataList) {
        this.arrayList = dataList;
    }

    @NonNull
    @Override
    public KosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_kos, parent, false);
        return new KosAdapter.KosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KosViewHolder holder, int position) {

        String[] foto = arrayList.get(position).getFoto().split(",");

        Picasso.get().invalidate(UtilsApi.BASE_URL + "uploads/" +  foto[0]);
        Picasso.get().load(UtilsApi.BASE_URL + "uploads/" + foto[0]).into(holder.imgViewFoto);

        Map<String, String> mapJenisKos = new HashMap<String, String>();
        mapJenisKos.put("PTR","KOS PUTRA");
        mapJenisKos.put("PUT","KOS PUTRI");
        mapJenisKos.put("CMP","KOS CAMPUR");

        holder.rbRating.setRating(Float.parseFloat(arrayList.get(position).getRating()));
        holder.tvHarga.setText(UtilsApi.formatRupiah(Double.parseDouble(arrayList.get(position).getHarga_sewa())));
        holder.tvJenis.setText(mapJenisKos.get(arrayList.get(position).getTipe()));
        holder.tvNama.setText(arrayList.get(position).getNama());

        holder.tvAlamat.setText(String.format(Locale.US,"%s ,%s - %s",arrayList.get(position).getAlamat(), arrayList.get(position).getKelurahan(),arrayList.get(position).getKecamatan()));
        //set visible gone untuk deskripsi dan fasilitas agar tidak terlalu banyak info awalnya
        holder.tvDeskripsi.setText(arrayList.get(position).getDeskripsi());
        holder.tvFasilitas.setText(arrayList.get(position).getFasilitas());
        holder.tvDeskripsi.setVisibility(View.GONE);
        holder.tvFasilitas.setVisibility(View.GONE);

        holder.tvSisaKamar.setText(String.format(Locale.US, "%s Kamar tersisa",arrayList.get(position).getKmr_tersisa()));

        holder.btnMaps.setOnClickListener(view -> {
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + arrayList.get(position).getLat() + "," + arrayList.get(position).getLng() );
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            view.getContext().startActivity(mapIntent);
        });


        holder.btnDetail.setOnClickListener(view -> {
            if (onBindCallBack != null) {
                onBindCallBack.OnKosViewBind("btnDetailOnClick", holder, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class KosViewHolder extends RecyclerView.ViewHolder {
        String lat,lng;

        ImageView imgViewFoto;
        RatingBar rbRating;
        TextView tvJenis;
        TextView tvHarga;
        TextView tvNama;
        TextView tvAlamat;
        TextView tvDeskripsi;
        TextView tvFasilitas;
        TextView tvSisaKamar;
        Button btnMaps;
        Button btnDetail;


        public KosViewHolder(@NonNull View itemView) {
            super(itemView);

            imgViewFoto = itemView.findViewById(R.id.kos_foto);
            rbRating = itemView.findViewById(R.id.kos_rating);
            tvJenis = itemView.findViewById(R.id.kos_tvJenis);
            tvHarga = itemView.findViewById(R.id.kos_tvHarga);
            tvNama = itemView.findViewById(R.id.kos_tvNama);
            tvAlamat = itemView.findViewById(R.id.kos_tvAlamat);
            tvDeskripsi = itemView.findViewById(R.id.kos_tvDeskripsi);
            tvFasilitas = itemView.findViewById(R.id.kos_tvFasilitas);
            tvSisaKamar = itemView.findViewById(R.id.kos_tvSisaKamar);
            btnMaps = itemView.findViewById(R.id.kos_btnMaps);
            btnDetail = itemView.findViewById(R.id.kos_btnDetail);
        }
    }
}
