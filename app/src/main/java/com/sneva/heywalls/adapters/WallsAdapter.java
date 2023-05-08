package com.sneva.heywalls.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.sneva.heywalls.R;
import com.sneva.heywalls.SetActivity;
import com.sneva.heywalls.databinding.ItemWallsBinding;
import com.sneva.heywalls.databinding.LayoutAdBinding;
import com.sneva.heywalls.models.Wallpapers;
import com.sneva.heywalls.utlis.Constants;

import java.util.List;
import java.util.Objects;

public class WallsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_VIEW = 0;
    private static final int AD_VIEW = 1;
    private static final int ITEM_FEED_COUNT = 4;
    public Context context;
    public List<Wallpapers> features;

    public WallsAdapter(Context context, List<Wallpapers> features) {
        this.context = context;
        this.features = features;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == ITEM_VIEW) {
            View view = layoutInflater.inflate(R.layout.item_walls, parent, false);
            return new ViewHolder(view);
        } else if (viewType == AD_VIEW) {
            View view = layoutInflater.inflate(R.layout.layout_ad, parent, false);
            return new AdViewHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == ITEM_VIEW) {
            int pos = position - Math.round(position / ITEM_FEED_COUNT);
            Wallpapers fe = features.get(pos);
            ((ViewHolder) holder).setData(fe);
            ((ViewHolder) holder).itemView.setOnClickListener(view -> {
                try {
                    if (context != null) {
                        Intent intent = new Intent(context, SetActivity.class);
                        intent.putExtra("url", fe.getImage());
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else if (holder.getItemViewType() == AD_VIEW) {
            ((WallsAdapter.AdViewHolder) holder).bindAdData();
        }
    }

    @Override
    public int getItemCount() {
        if (features.size() > 0) {
            return features.size() + Math.round(features.size() / ITEM_FEED_COUNT);
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if ((position + 1) % ITEM_FEED_COUNT == 0) {
            return AD_VIEW;
        }
        return ITEM_VIEW;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemWallsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemWallsBinding.bind(itemView);
        }

        public void setData(Wallpapers featured) {
            Glide.with(context).load(featured.getImage()).placeholder(R.drawable.profile).into(binding.image);
        }
    }

    private void populateNativeADView(NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        ((MaterialTextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        Objects.requireNonNull(adView.getMediaView()).setMediaContent(nativeAd.getMediaContent());

        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((MaterialTextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((MaterialButton) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((MaterialTextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }
        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((MaterialTextView) adView.getStoreView()).setText(nativeAd.getStore());
        }
        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView())).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else {
            ((MaterialTextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {

        LayoutAdBinding binding;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutAdBinding.bind(itemView);
        }

        private void bindAdData() {
            AdLoader.Builder builder = new AdLoader.Builder(itemView.getContext(), Constants.NATIVE)
                    .forNativeAd(nativeAd -> {
                        NativeAdView nativeAdView = (NativeAdView) ((Activity)itemView.getContext()).getLayoutInflater().inflate(R.layout.item_native_ad, null);
                        populateNativeADView(nativeAd, nativeAdView);
                        binding.adLayout.removeAllViews();
                        binding.adLayout.addView(nativeAdView);
                    });
            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Toast.makeText(itemView.getContext(), loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }
}
