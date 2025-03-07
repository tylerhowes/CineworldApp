package com.example.cineworldapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlcoholRefusalLogCardAdapter extends RecyclerView.Adapter<AlcoholRefusalLogCardAdapter.ViewHolder>{

    private final ArrayList<AlcoholRefusalLogDataModel> alcoholRefusalLogDataModelArrayList;
    private final Context context;
    public AlcoholRefusalLogCardAdapter(Context context, ArrayList<AlcoholRefusalLogDataModel> alcoholRefusalLogDataModelArrayList) {
        this.context = context;
        this.alcoholRefusalLogDataModelArrayList = alcoholRefusalLogDataModelArrayList;
    }

    @NonNull
    @Override
    public AlcoholRefusalLogCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_refusal_log_card, parent, false);
        return new AlcoholRefusalLogCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlcoholRefusalLogCardAdapter.ViewHolder holder, int position) {
        AlcoholRefusalLogDataModel alcoholRefusalLogDataModel = alcoholRefusalLogDataModelArrayList.get(position);
        holder.nameOrDescription.setText(alcoholRefusalLogDataModel.getNameOrDescription());
        holder.product.setText(alcoholRefusalLogDataModel.getProduct());
        holder.time.setText(alcoholRefusalLogDataModel.getTime());
        holder.date.setText(alcoholRefusalLogDataModel.getDate());
        holder.reason.setText(alcoholRefusalLogDataModel.getReason());
    }

    @Override
    public int getItemCount() {
        return alcoholRefusalLogDataModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameOrDescription;
        TextView product;
        TextView time;
        TextView date;
        TextView reason;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameOrDescription = itemView.findViewById(R.id.customerNameDescription);
            product = itemView.findViewById(R.id.product);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            reason = itemView.findViewById(R.id.reasonsForRefusal);
        }
    }
}
