package com.example.cineworldapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;




public class FloorEquipmentSanitisationAdapter extends RecyclerView.Adapter<FloorEquipmentSanitisationAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<FloorEquipmentSanitisationModel> floorEquipmentSanitisationList;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String userInitials;

    public FloorEquipmentSanitisationAdapter(Context context, ArrayList<FloorEquipmentSanitisationModel> floorEquipmentSanitisationList) {
        this.context = context;
        this.floorEquipmentSanitisationList = floorEquipmentSanitisationList;
    }

    @NonNull
    @Override
    public FloorEquipmentSanitisationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_floor_sanitisations_card, parent, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UID = auth.getCurrentUser().getUid();

        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials =  documentSnapshot.getString("initials");
                });

        return new FloorEquipmentSanitisationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FloorEquipmentSanitisationAdapter.ViewHolder holder, int position) {

        FloorEquipmentSanitisationModel floorEquipmentSanitisationModel = floorEquipmentSanitisationList.get(position);

        holder.timeDue.setText(floorEquipmentSanitisationModel.getSanitisedTime());
        holder.areasToSanitise.setText(floorEquipmentSanitisationModel.getAreasToSanitise());
        holder.completeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 holder.staffInitials.setText(userInitials);
                 holder.completeCheck.setBackgroundColor(context.getResources().getColor(R.color.green));
            }
        });
    }

    @Override
    public int getItemCount() {
        return floorEquipmentSanitisationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView timeDue;
        TextView staffInitials;
        TextView areasToSanitise;
        Button completeCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeDue = itemView.findViewById(R.id.timeDue);
            areasToSanitise = itemView.findViewById(R.id.areas);
            staffInitials = itemView.findViewById(R.id.staffInitials);
            completeCheck = itemView.findViewById(R.id.completeCheckButton);

        }
    }
}
