package com.sneva.heywalls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.sneva.heywalls.databinding.ActivityWebviewBinding;
import com.sneva.heywalls.utlis.AdRequestBuilder;

public class WebViewActivity extends AppCompatActivity {

    ActivityWebviewBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AdRequestBuilder.initialize(WebViewActivity.this);
        AdRequestBuilder.loadBanner(WebViewActivity.this, binding.adView);

        binding.back.setOnClickListener(v -> finish());

        binding.webview.setWebViewClient(new MyBrowser());
        binding.webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    binding.progress.setVisibility(View.GONE);
                } else {
                    binding.progress.setVisibility(View.VISIBLE);
                }
            }
        });
        binding.webview.getSettings().setLoadsImagesAutomatically(true);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        binding.webview.loadUrl(getIntent().getStringExtra("url"));
    }

    private static class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}