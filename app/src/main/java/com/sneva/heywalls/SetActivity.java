package com.sneva.heywalls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.sneva.easyprefs.EasyPrefs;
import com.sneva.heywalls.databinding.ActivitySetBinding;
import com.sneva.heywalls.utlis.AdRequestBuilder;
import com.sneva.heywalls.utlis.Constants;

import java.io.IOException;

public class SetActivity extends AppCompatActivity {

    ActivitySetBinding binding;
    InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AdRequestBuilder.initialize(SetActivity.this);
        AdRequestBuilder.loadBanner(SetActivity.this, binding.adView);
        loadInterstitialAd();

        WallpaperManager m = WallpaperManager.getInstance(this);

        binding.back.setOnClickListener(v -> finish());

        Glide.with(SetActivity.this).asBitmap()
                .apply(new RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL)
                ).load(getIntent().getStringExtra("url")).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        binding.wallpaper.setImageBitmap(resource);
                        binding.set.setOnClickListener(v -> {
                            if (interstitial != null) {
                                interstitial.show(SetActivity.this);
                                try {
                                    m.setBitmap(resource);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    m.setBitmap(resource);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

        binding.centerCrop.setOnClickListener(v -> binding.wallpaper.setScaleType(ImageView.ScaleType.CENTER_CROP));

        binding.fitCenter.setOnClickListener(v -> binding.wallpaper.setScaleType(ImageView.ScaleType.FIT_CENTER));
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, Constants.INTERSTITIAL, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                interstitial = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        });
    }
}