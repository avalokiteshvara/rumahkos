package com.example.rumahkos.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //hide search filter
        ((MainActivity) getActivity()).setmStateActionFilter(false);
        ((MainActivity) getActivity()).invalidateOptionsMenu();


        return root;
    }
}
