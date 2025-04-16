package com.example.cineworldapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import DailyConcessionsSideNav.DailyConcessionsSubFragments.DailyConcessionsEquipmentSanitisationFragment;
import DailyConcessionsSideNav.DailyConcessionsSubFragments.DailyConcessionsTillSanitisationFragment;

public class ConcessionsEquipmentSanitisationAdapter extends RecyclerView.Adapter<ConcessionsEquipmentSanitisationAdapter.ViewHolder>{

    private final Context context;
    private final ArrayList<ConcessionsEquipmentSanitisationModel> concessionsEquipmentSanitisationModelList;
    private final DailyConcessionsEquipmentSanitisationFragment fragment;


    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String userInitials;

    public ConcessionsEquipmentSanitisationAdapter(Context context, ArrayList<ConcessionsEquipmentSanitisationModel> concessionsEquipmentSanitisationModelList, DailyConcessionsEquipmentSanitisationFragment fragment) {
        this.context = context;
        this.concessionsEquipmentSanitisationModelList = concessionsEquipmentSanitisationModelList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ConcessionsEquipmentSanitisationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_floor_sanitisations_card, parent, false);

        //Move this and pass into constructor!!!!!!!!!!!!!!!
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UID = auth.getCurrentUser().getUid();

        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials =  documentSnapshot.getString("initials");
                });

        return new ConcessionsEquipmentSanitisationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConcessionsEquipmentSanitisationAdapter.ViewHolder holder, int position) {

        ConcessionsEquipmentSanitisationModel concessionsEquipmentSanitisationModel = concessionsEquipmentSanitisationModelList.get(position);

        // Show initials if already filled
        String initials = concessionsEquipmentSanitisationModel.getStaffInitials();
        if (initials != null && !initials.equals("...")) {
            holder.staffInitials.setText(initials);
        }

        // Change button color if already completed
        if (concessionsEquipmentSanitisationModel.getIsSanitised()) {
            holder.completeCheck.setBackgroundColor(context.getResources().getColor(R.color.green));
        }

        holder.timeDue.setText(concessionsEquipmentSanitisationModel.getSanitisedTime());
        String areasToSanitise = concessionsEquipmentSanitisationModel.getAreasToSanitise();
        holder.areasToSanitise.setText(areasToSanitise);

        holder.completeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.staffInitials.setText(userInitials);
                holder.completeCheck.setBackgroundColor(context.getResources().getColor(R.color.green));
                fragment.StartCountDownTimer();

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date date = Calendar.getInstance().getTime();
                String currentTime = timeFormat.format(date);

                concessionsEquipmentSanitisationModel.setStaffInitials(userInitials);
                concessionsEquipmentSanitisationModel.setTimeCompleted(currentTime);
                concessionsEquipmentSanitisationModel.setSanitised(true);

                SaveLogToFirestore(concessionsEquipmentSanitisationModel);}
        });
    }

    @Override
    public int getItemCount() {
        return concessionsEquipmentSanitisationModelList.size();
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

    private void SaveLogToFirestore(ConcessionsEquipmentSanitisationModel model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        // Prepare the data to be saved as a map
        // Create a map of log data to be stored in Firestore
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("isSanitised", model.getIsSanitised());
        dataMap.put("staffInitials", model.getStaffInitials());
        dataMap.put("timeDue", model.getTimeDue());
        dataMap.put("areasToSanitise", model.getAreasToSanitise());
        dataMap.put("timeCompleted", model.getTimeCompleted());


        // Firestore path to store the log
        db.collection("Documents")
                .document(currentDate)
                .collection("Daily Concessions")
                .document("Equipment Sanitisation")
                .collection("Logs")
                .document(model.getTimeDue())
                .set(dataMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("ConcessionsTillSanitisation", "Log added to Firestore.");
                })
                .addOnFailureListener(e -> {
                    Log.e("ConcessionsTillSanitisation", "Error writing log to Firestore", e);
                });
    }
}