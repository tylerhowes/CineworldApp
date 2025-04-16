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

import DailyConcessionsSideNav.DailyConcessionsSubFragments.DailyConcessionsTillSanitisationFragment;

public class ConcessionsTillSanitisationAdapter extends RecyclerView.Adapter<ConcessionsTillSanitisationAdapter.ViewHolder>{

    private final Context context;
    private final ArrayList<ConcessionsTillSanitisationModel> concessionsTillSanitisationModelArrayList;
    private final DailyConcessionsTillSanitisationFragment fragment;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String userInitials;

    public ConcessionsTillSanitisationAdapter(Context context, ArrayList<ConcessionsTillSanitisationModel> concessionsTillSanitisationModelArrayList, DailyConcessionsTillSanitisationFragment fragment) {
        this.context = context;
        this.concessionsTillSanitisationModelArrayList = concessionsTillSanitisationModelArrayList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ConcessionsTillSanitisationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_floor_sanitisations_card, parent, false);

        //Move this and pass into constructor!!!!!!!!!!!!!!!
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UID = auth.getCurrentUser().getUid();

        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials =  documentSnapshot.getString("initials");
                });

        return new ConcessionsTillSanitisationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConcessionsTillSanitisationAdapter.ViewHolder holder, int position) {

        ConcessionsTillSanitisationModel concessionsTillSanitisationModel = concessionsTillSanitisationModelArrayList.get(position);

        // Show initials if already filled
        String initials = concessionsTillSanitisationModel.getStaffInitials();
        if (initials != null && !initials.equals("...")) {
            holder.staffInitials.setText(initials);
        }

        // Change button color if already completed
        if (concessionsTillSanitisationModel.getIsSanitised()) {
            holder.completeCheck.setBackgroundColor(context.getResources().getColor(R.color.green));
        }

        holder.timeDue.setText(concessionsTillSanitisationModel.getTimeDue());
        holder.areasToSanitise.setText(concessionsTillSanitisationModel.getAreasToSanitise());
        holder.completeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.staffInitials.setText(userInitials);
                holder.completeCheck.setBackgroundColor(context.getResources().getColor(R.color.green));
                fragment.StartCountDownTimer();

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date date = Calendar.getInstance().getTime();
                String currentTime = timeFormat.format(date);

                concessionsTillSanitisationModel.setStaffInitials(userInitials);
                concessionsTillSanitisationModel.setTimeCompleted(currentTime);
                concessionsTillSanitisationModel.setSanitised(true);

                SaveLogToFirestore(concessionsTillSanitisationModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return concessionsTillSanitisationModelArrayList.size();
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

    private void SaveLogToFirestore(ConcessionsTillSanitisationModel model) {
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
                .document("Till Sanitisation")
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

