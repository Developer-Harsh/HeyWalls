package com.sneva.heywalls.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sneva.heywalls.R;
import com.sneva.heywalls.SetActivity;
import com.sneva.heywalls.databinding.ItemStoriesBinding;
import com.sneva.heywalls.models.Featured;

import java.util.List;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.ViewHolder> {

    public Context context;
    public List<Featured> features;

    public FeaturedAdapter(Context context, List<Featured> features) {
        this.context = context;
        this.features = features;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stories, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Featured fe = features.get(position);

        holder.itemView.setOnClickListener(view -> {
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

        holder.setData(fe);
    }

    @Override
    public int getItemCount() {
        return features.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemStoriesBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStoriesBinding.bind(itemView);
        }

        public void setData(Featured featured) {
            Glide.with(context).load(featured.getImage()).placeholder(R.drawable.profile).into(binding.image);
        }
    }
}
