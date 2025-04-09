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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CinemaSafetyAndSecurityAdapter extends RecyclerView.Adapter<CinemaSafetyAndSecurityAdapter.ViewHolder> {

    private final ArrayList<CinemaSafetyAndSecurityModel> cinemaSafetyAndSecurityModelList;
    private final Context context;
    private String userInitials;

    public CinemaSafetyAndSecurityAdapter(Context context, ArrayList<CinemaSafetyAndSecurityModel> cinemaSafetyAndSecurityModelList, String userInitials) {
        this.context = context;
        this.cinemaSafetyAndSecurityModelList = cinemaSafetyAndSecurityModelList;
        this.userInitials = userInitials;
    }

    @NonNull
    @Override
    public CinemaSafetyAndSecurityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_daily_floor_safety_and_security_card, parent, false);


        return new CinemaSafetyAndSecurityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CinemaSafetyAndSecurityAdapter.ViewHolder holder, int position) {
        CinemaSafetyAndSecurityModel cinemaSafetyAndSecurityModel = cinemaSafetyAndSecurityModelList.get(position);
        holder.timeOfCheck.setText(cinemaSafetyAndSecurityModel.getTimeOfCheck());
        holder.areas.setText(cinemaSafetyAndSecurityModel.getAreas());
        holder.screens.setText(cinemaSafetyAndSecurityModel.getScreens());
        holder.staffInitials.setText(cinemaSafetyAndSecurityModel.getStaffInitials());
        holder.timeCompleted.setText(cinemaSafetyAndSecurityModel.getTimeCompleted());

        // Show initials if already filled
        String initials = cinemaSafetyAndSecurityModel.getStaffInitials();
        if (initials != null && !initials.equals("...")) {
            holder.staffInitials.setText(initials);
        }

        // Change button color if already completed
        if (cinemaSafetyAndSecurityModel.getTimeCompleted() != null && !cinemaSafetyAndSecurityModel.getTimeCompleted().equals("...")) {
            holder.completeCheckButton.setBackgroundColor(context.getResources().getColor(R.color.green));
        }


        holder.completeCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date date = Calendar.getInstance().getTime();
                String currentTime = timeFormat.format(date);

                holder.completeCheckButton.setBackgroundColor(context.getResources().getColor(R.color.green));
                holder.timeCompleted.setText(currentTime);
                holder.staffInitials.setText(userInitials);
                cinemaSafetyAndSecurityModel.setStaffInitials(userInitials);
                cinemaSafetyAndSecurityModel.setTimeCompleted(currentTime);
                SaveLogToFirestore(cinemaSafetyAndSecurityModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cinemaSafetyAndSecurityModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeOfCheck;
        TextView areas;
        TextView screens;
        TextView staffInitials;
        TextView timeCompleted;

        Button completeCheckButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeOfCheck = itemView.findViewById(R.id.timeOfCheck);
            areas = itemView.findViewById(R.id.areasToCheck);
            screens = itemView.findViewById(R.id.screensToCheck);
            staffInitials = itemView.findViewById(R.id.staffInitials);
            timeCompleted = itemView.findViewById(R.id.timeCompleted);
            completeCheckButton = itemView.findViewById(R.id.completeCheckButton);
        }
    }

    private void SaveLogToFirestore(CinemaSafetyAndSecurityModel model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        // Prepare the data to be saved as a map
        // Create a map of log data to be stored in Firestore
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("timeOfCheck", model.getTimeOfCheck());
        dataMap.put("areas", model.getAreas());
        dataMap.put("screens", model.getScreens());
        dataMap.put("staffInitials", model.getStaffInitials());
        dataMap.put("timeCompleted", model.getTimeCompleted());


        // Firestore path to store the log
        db.collection("Documents")
                .document(currentDate)
                .collection("Daily Floor")
                .document("Safety and Security")
                .collection("Logs")
                .document(model.getTimeOfCheck())
                .set(dataMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("AlcoholRefusalLog", "Log added to Firestore.");
                })
                .addOnFailureListener(e -> {
                    Log.e("AlcoholRefusalLog", "Error writing log to Firestore", e);
                });
    }
}
