package com.example.rumahkos.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.rumahkos.LoginActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.util.SPManager;

public class SignOutFragment extends Fragment {

    private Context mContext;
    SPManager spManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        spManager = new SPManager(mContext);

        new AlertDialog.Builder(this.mContext)
            .setTitle("Keluar")
            .setMessage("Apakah anda yakin ingin keluar aplikasi?")
            .setPositiveButton("Keluar", (dialog, which) -> {

                spManager.saveBoolean(spManager.LOGIN_STATUS, false);

                Intent myIntent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(myIntent);

            }).setNegativeButton("Batal", (dialog, which) -> {


            ProfileFragment fragment = new ProfileFragment();
            AppCompatActivity activity = (AppCompatActivity) getView().getContext();

            activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment, ProfileFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();

        }).show();


        return root;

    }

}
