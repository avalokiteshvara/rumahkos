package com.example.rumahkos.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rumahkos.MainActivity;
import com.example.rumahkos.R;
import com.example.rumahkos.util.SPManager;
import com.example.rumahkos.util.api.UtilsApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MutasiDanaFragment extends Fragment {

    Context mContext;
    SwipeRefreshLayout swipe;
    SPManager sharedPrefManager;

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

                ProfileFragment mf = new ProfileFragment();
                FragmentTransaction ft = requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_host_fragment, mf, MutasiDanaFragment.class.getSimpleName())
                    .addToBackStack(null);
                ft.commit();

                return true;
            }
            return false;
        });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mutasidana, container, false);
        swipe = root.findViewById(R.id.mutasidana_swipeContainer);


        FloatingActionButton floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();
        if (floatingActionButton != null) {
            floatingActionButton.hide();

//            if(spManager.getLevel().equals("pemilik")){
//
//                floatingActionButton.setOnClickListener(view -> {
//
//                    Bundle bundle = new Bundle();
//                    // bundle.putInt("put something key", someting value);
//                    KosListFragment fragment = new KosListFragment();
//                    fragment.setArguments(bundle);
//                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
//
//                    activity.getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.nav_host_fragment, fragment, KosListFragment.class.getSimpleName())
//                        .addToBackStack(null)
//                        .commit();
//                });
//            }else{
//                floatingActionButton.hide();
//            }

//            floatingActionButton.setOnClickListener(view -> {
//
//                Bundle bundle = new Bundle();
//                // bundle.putInt("put something key", someting value);
//                KosListFragment fragment = new KosListFragment();
//                fragment.setArguments(bundle);
//                AppCompatActivity activity = (AppCompatActivity) view.getContext();
//
//                activity.getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.nav_host_fragment, fragment, KosListFragment.class.getSimpleName())
//                    .addToBackStack(null)
//                    .commit();
//            });


        }

        swipe.setOnRefreshListener(() -> loadWeb(root));

        sharedPrefManager = new SPManager(mContext);

        loadWeb(root);

        //tampilkan search filter
        ((MainActivity) getActivity()).setmStateActionFilter(false);
        ((MainActivity) getActivity()).invalidateOptionsMenu();

        return root;
    }

    public void loadWeb(View root) {
        WebView mWebView = root.findViewById(R.id.mutasidana_webview);
        mWebView.loadUrl(UtilsApi.BASE_URL_WEBVIEW + "mutasi-dana/" + sharedPrefManager.getUserId() );

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        swipe.setRefreshing(true);
        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                mWebView.loadUrl("file:///android_asset/error.html");
            }

            public void onPageFinished(WebView view, String url) {
                //ketika loading selesai, ison loading akan hilang
                swipe.setRefreshing(false);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //loading akan jalan lagi ketika masuk link lain
                // dan akan berhenti saat loading selesai
                if (100 == mWebView.getProgress()) {
                    swipe.setRefreshing(false);
                } else {
                    swipe.setRefreshing(true);
                }
            }
        });
    }


}
