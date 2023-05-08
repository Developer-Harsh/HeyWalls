package com.sneva.heywalls.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sneva.heywalls.R;
import com.sneva.heywalls.databinding.ItemTabBinding;
import com.sneva.heywalls.listener.TabsSelector;
import com.sneva.heywalls.models.Tabs;

import java.util.List;

public class TabsAdapter extends RecyclerView.Adapter<TabsAdapter.ViewHolder> {

    public Context context;
    public List<Tabs> tabs;
    public TabsSelector selector;
    public int index;

    public TabsAdapter(Context context, List<Tabs> tabs, TabsSelector selector) {
        this.context = context;
        this.tabs = tabs;
        this.selector = selector;
    }

    @NonNull
    @Override
    public TabsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tab, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull TabsAdapter.ViewHolder holder, int position) {
        Tabs tab = tabs.get(position);
        holder.binding.text1.setText(tab.getId());

        new Handler().postDelayed(() -> {
            if (index == holder.getAdapterPosition()) {
                holder.binding.text1.setBackgroundResource(R.drawable.selected_tab);
                holder.binding.text1.setTextColor(Color.parseColor("#FF0339"));
            } else {
                holder.binding.text1.setBackgroundResource(R.drawable.unselected_tab);
                holder.binding.text1.setTextColor(Color.parseColor("#A6ADB5"));
            }
        }, 1000);

        holder.binding.text1.setOnClickListener(view -> {
            index = holder.getAdapterPosition();
            notifyDataSetChanged();
            selector.clicked(holder.binding.text1.getText().toString(), holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return tabs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemTabBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTabBinding.bind(itemView);
        }
    }
}
