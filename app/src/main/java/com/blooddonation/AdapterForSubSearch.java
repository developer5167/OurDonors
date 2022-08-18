package com.blooddonation;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterForSubSearch extends RecyclerView.Adapter<AdapterForSubSearch.ViewHolder> {
    private final Context context;
    List<String> items;
    private int clickedPosition = -1;

    public AdapterForSubSearch(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.subsearch_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(items.get(position));
        holder.root.setOnClickListener(v -> {
            ((User_Activity)context).searchData(items.get(position));
            clickedPosition = holder.getBindingAdapterPosition();
            notifyDataSetChanged();
        });
        if (clickedPosition == position) {
            holder.root.setBackgroundResource(R.drawable.searching_back_selected);
            holder.textView.setTextColor(Color.BLACK);
        } else {
            holder.root.setBackgroundResource(R.drawable.searching_back);
            holder.textView.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final RelativeLayout root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item);
            root = itemView.findViewById(R.id.root);
        }
    }
}
