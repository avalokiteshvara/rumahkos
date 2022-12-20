package com.example.rumahkos.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.BaseApiService;
import com.example.rumahkos.util.api.UtilsApi;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileFragment extends Fragment {

    Context mContext;
    BaseApiService mBaseApiService;
    SPManager spManager;

    @BindView(R.id.btn_edit_profil)
    Button btnEditProfil;
    Button btn_tagihan ;
    @BindView(R.id.btn_tagihan)
    Button btnTagihan;
    @BindView(R.id.btn_riwayat_sewa)
    Button btnRiwayatSewa;
    @BindView(R.id.btn_logout)
    Button btnLogout;
    @BindView(R.id.btn_mutasi_dana)
    Button btnMutasiDana;
    @BindView(R.id.image_profil)
    ImageView profileImgViewFoto;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    //Pressed return button
    public void onResume() {
        super.onResume();
        requireView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {

            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                HomeFragment mf = new HomeFragment();
                FragmentTransaction ft = requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_host_fragment, mf, ProfileFragment.class.getSimpleName())
                    .addToBackStack(null);
                ft.commit();

                return true;
            }
            return false;
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this

        ActionBar actionBar = getActivity().getActionBar();
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, root);

        spManager = new SPManager(mContext);

//        btnTagihan = view.findViewById(R.id.btn_tagihan);
//        btnEditProfil = view.findViewById(R.id.btn_edit_profil);
//        btnRiwayatSewa = view.findViewById(R.id.btn_riwayat_sewa);
//        btnMutasiDana = view.findViewById(R.id.btn_mutasi_dana);
//        btnLogout = view.findViewById(R.id.btn_logout);

        if(spManager.getLevel().equals("penyewa")){
            btnMutasiDana.setVisibility(View.GONE);
        }


        Picasso.get().invalidate(UtilsApi.BASE_URL + "uploads/" + spManager.getFoto());
        Picasso.get().load(UtilsApi.BASE_URL + "uploads/" + spManager.getFoto()).into(profileImgViewFoto);


        btnMutasiDana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MutasiDanaFragment();
                getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
            }
        });


        btnEditProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FormProfileFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


        btnTagihan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new TagihanListFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnRiwayatSewa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new RiwayatKosFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new SignOutFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return root;
    }
}
