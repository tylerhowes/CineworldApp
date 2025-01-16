package com.example.cineworldapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomerAssistanceCardAdapter extends RecyclerView.Adapter<CustomerAssistanceCardAdapter.ViewHolder>{

    private final Context context;
    private final ArrayList<CustomerAssistanceDataModel> customerAssistanceDataModelArrayList;

    public CustomerAssistanceCardAdapter(Context context, ArrayList<CustomerAssistanceDataModel> customerAssistanceDataModelArrayList) {
        this.context = context;
        this.customerAssistanceDataModelArrayList = customerAssistanceDataModelArrayList;
    }

    @NonNull
    @Override
    public CustomerAssistanceCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_customer_assistance_card, parent, false);
        return new CustomerAssistanceCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAssistanceCardAdapter.ViewHolder holder, int position) {
        CustomerAssistanceDataModel customerAssistanceDataModel = customerAssistanceDataModelArrayList.get(position);
        holder.name.setText(customerAssistanceDataModel.getCustomerName()) ;
        holder.screen.setText(customerAssistanceDataModel.getScreen());
        holder.seat.setText(customerAssistanceDataModel.getSeatNumber()) ;
        holder.start.setText(customerAssistanceDataModel.getStartTime());
        holder.finish.setText(customerAssistanceDataModel.getFinishTime()) ;
        holder.assistanceRequired.setText(customerAssistanceDataModel.getAssistanceRequired());
    }

    @Override
    public int getItemCount() {
        return customerAssistanceDataModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView screen;
        TextView seat;
        TextView start;
        TextView finish;
        TextView assistanceRequired;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.customerName);
            screen = itemView.findViewById(R.id.screenNumber);
            seat = itemView.findViewById(R.id.seatNumber);
            start = itemView.findViewById(R.id.startTime);
            finish = itemView.findViewById(R.id.finishTime);
            assistanceRequired = itemView.findViewById(R.id.assistanceRequired);
        }
    }
}
