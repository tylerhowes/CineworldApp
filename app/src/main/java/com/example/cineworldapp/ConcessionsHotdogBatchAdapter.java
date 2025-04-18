package com.example.cineworldapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ConcessionsHotdogBatchAdapter extends RecyclerView.Adapter<ConcessionsHotdogBatchAdapter.ViewHolder>{

    private final Context context;
    private final String userInitials;

    private final ArrayList<HotdogBatchModel> hotdogBatchModelList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ConcessionsHotdogBatchAdapter(Context context, ArrayList<HotdogBatchModel> hotdogBatchModelList, String userInitials) {
        this.context = context;
        this.hotdogBatchModelList = hotdogBatchModelList;
        this.userInitials = userInitials;
    }

    @NonNull
    @Override
    public ConcessionsHotdogBatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hotdog_cooking_batch_card, parent, false);

        return new ConcessionsHotdogBatchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConcessionsHotdogBatchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        HotdogBatchModel hotdogBatchModel = hotdogBatchModelList.get(position);

        holder.timeStarted.setText(hotdogBatchModel.getTimeStarted());
        holder.batchNumber.setText(hotdogBatchModel.getBatchNumber());
        holder.regularQuantity.setText(hotdogBatchModel.getRegularQuantity());
        holder.largeQuantity.setText(hotdogBatchModel.getLargeQuantity());
        holder.veggieQuantity.setText(hotdogBatchModel.getVeggieQuantity());

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bagBatchPopupView = inflater.inflate(R.layout.bag_batch_popup_layout, null);
        final PopupWindow bagBatchPopupWindow = new PopupWindow(bagBatchPopupView, width, height, focusable);

        holder.bagBatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bagBatchPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                bagBatchPopupView.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GET WHAT BATCH IT IS AND UPDATE FIREBASE TO COMPLETE COOKING
                        bagBatchPopupWindow.dismiss();

                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        String currentTime = timeFormat.format(date);
                        String currentDate = dateFormat.format(date);

                        Log.d("HotDogCookingFragment", "Batch bagged at: " + currentTime.toString());


                        String batchNumber = hotdogBatchModel.getBatchNumber();

                        EditText regularTemp = bagBatchPopupView.findViewById(R.id.regularTempET);
                        EditText largeTemp = bagBatchPopupView.findViewById(R.id.largeTempET);
                        EditText veggieTemp = bagBatchPopupView.findViewById(R.id.veggieTempET);

                        Log.d("HotDogCookingFragment", "Batch Number: " + batchNumber);

                        db.collection("Documents")
                                .document(currentDate)
                                .collection("Daily Concessions")
                                .document("Hot Dog Control")
                                .collection("Cooking")
                                .document("Batch" + batchNumber)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                                            Log.d("HotDogCookingFragment", "Task was successful and document exists");
                                            HotdogBatchModel batchModel = task.getResult().toObject(HotdogBatchModel.class);
                                            if (batchModel != null) {
                                                batchModel.setTimeBagged(currentTime);
                                                batchModel.setStatus("Bagged");
                                                batchModel.setRegularTemperatureReached(regularTemp.getText().toString());
                                                batchModel.setLargeTemperatureReached(largeTemp.getText().toString());
                                                batchModel.setVeggieTemperatureReached(veggieTemp.getText().toString());
                                                batchModel.setStaffInitialsBagged(userInitials);
                                                db.collection("Documents")
                                                        .document(currentDate)
                                                        .collection("Daily Concessions")
                                                        .document("Hot Dog Control")
                                                        .collection("Cooking")
                                                        .document("Batch" + batchNumber)
                                                        .set(batchModel);
                                            } else {
                                                Log.e("HotDogCookingFragment", "batchModel is null after toObject()");
                                            }
                                        } else {
                                            Log.e("HotDogCookingFragment", "Failed to get document or document does not exist");
                                        }
                                        // Remove the bagged batch from the list and notify the adapter
                                        hotdogBatchModelList.remove(position);
                                        notifyItemRemoved(position);
                                    }
                                });
                    }
                });

                bagBatchPopupView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bagBatchPopupWindow.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotdogBatchModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView timeStarted;
        TextView batchNumber;
        TextView regularQuantity;
        TextView largeQuantity;
        TextView veggieQuantity;
        Button bagBatchButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeStarted = itemView.findViewById(R.id.timeStarted);
            batchNumber = itemView.findViewById(R.id.batchNumber);
            regularQuantity = itemView.findViewById(R.id.regularQuantity);
            largeQuantity = itemView.findViewById(R.id.largeQuantity);
            veggieQuantity = itemView.findViewById(R.id.veggieQuantity);

            bagBatchButton = itemView.findViewById(R.id.bagBatchButton);
        }
    }
}
