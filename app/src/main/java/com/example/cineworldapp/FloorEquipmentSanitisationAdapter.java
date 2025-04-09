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

import DailyFloorSideNav.DailyFloorSubFragments.FloorEquipmentSanitisationFragment;

public class FloorEquipmentSanitisationAdapter extends RecyclerView.Adapter<FloorEquipmentSanitisationAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<FloorEquipmentSanitisationModel> floorEquipmentSanitisationList;
    private final FloorEquipmentSanitisationFragment fragment;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String userInitials;

    public FloorEquipmentSanitisationAdapter(Context context, ArrayList<FloorEquipmentSanitisationModel> floorEquipmentSanitisationList, FloorEquipmentSanitisationFragment fragment) {
        this.context = context;
        this.floorEquipmentSanitisationList = floorEquipmentSanitisationList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public FloorEquipmentSanitisationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_floor_sanitisations_card, parent, false);

        ////Move this and pass into constructor!!!!!!!!!!!!!!!
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

        // Show initials if already filled
        String initials = floorEquipmentSanitisationModel.getStaffInitials();
        if (initials != null && !initials.equals("...")) {
            holder.staffInitials.setText(initials);
        }

        // Change button color if already completed
        if (floorEquipmentSanitisationModel.getIsSanitised()) {
            holder.completeCheck.setBackgroundColor(context.getResources().getColor(R.color.green));
        }

        SaveLogToFirestore(floorEquipmentSanitisationModel);

        holder.completeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 holder.staffInitials.setText(userInitials);
                 holder.completeCheck.setBackgroundColor(context.getResources().getColor(R.color.green));
                 fragment.StartCountDownTimer();

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date date = Calendar.getInstance().getTime();
                String currentTime = timeFormat.format(date);
                floorEquipmentSanitisationModel.setStaffInitials(userInitials);
                floorEquipmentSanitisationModel.setTimeCompleted(currentTime);
                floorEquipmentSanitisationModel.setIsSanitised(true);

                SaveLogToFirestore(floorEquipmentSanitisationModel);
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

    private void SaveLogToFirestore(FloorEquipmentSanitisationModel model) {
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
                .collection("Daily Floor")
                .document("Equipment Sanitisation")
                .collection("Logs")
                .document(model.getTimeDue())
                .set(dataMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FloorEquipmentSanitisation", "Log added to Firestore.");
                })
                .addOnFailureListener(e -> {
                    Log.e("FloorEquipmentSanitisation", "Error writing log to Firestore", e);
                });
    }
}
