package com.example.cineworldapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ToiletCheckCardAdapter extends RecyclerView.Adapter<ToiletCheckCardAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<ToiletCheckDataModel> toiletCheckDataModels;

    public ToiletCheckCardAdapter(Context context, ArrayList<ToiletCheckDataModel> toiletCheckDataModels) {
        this.context = context;
        this.toiletCheckDataModels = toiletCheckDataModels;
    }

    @NonNull
    @Override
    public ToiletCheckCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_toilet_check_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToiletCheckCardAdapter.ViewHolder holder, int position) {
        ToiletCheckDataModel toiletCheckDataModel = toiletCheckDataModels.get(position);
        holder.toiletCheckNumber.setText(toiletCheckDataModel.getToiletCheckNumber());
        holder.toiletCheckTime.setText(toiletCheckDataModel.getToiletCheckTime());
        holder.staffInitials.setText(toiletCheckDataModel.getStaffInitials());
    }

    @Override
    public int getItemCount() {
        return toiletCheckDataModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView toiletCheckNumber;
        TextView toiletCheckTime;
        TextView staffInitials;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            toiletCheckNumber = itemView.findViewById(R.id.checkNumber);
            toiletCheckTime = itemView.findViewById(R.id.timeCompleted);
            staffInitials = itemView.findViewById(R.id.staffInitials);
        }
    }
}
