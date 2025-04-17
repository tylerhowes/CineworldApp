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

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ConcessionsHotdogStorageAdapter extends RecyclerView.Adapter<ConcessionsHotdogStorageAdapter.ViewHolder> {

    private final Context context;
    private final String userInitials;
    private final ArrayList<HotdogStorageModel> hotdogStorageModelList;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ConcessionsHotdogStorageAdapter(Context context, ArrayList<HotdogStorageModel> hotdogStorageModelList, String userInitials) {
        this.context = context;
        this.hotdogStorageModelList = hotdogStorageModelList;
        this.userInitials = userInitials;
    }

    @NonNull
    @Override
    public ConcessionsHotdogStorageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_hotdog_storage_card, parent, false);
        return new ConcessionsHotdogStorageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConcessionsHotdogStorageAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        HotdogStorageModel hotdogStorageModel = hotdogStorageModelList.get(position);

        holder.timeDue.setText(hotdogStorageModel.getTimeDue());
        holder.unit1TopTemp.setText(hotdogStorageModel.getUnit1TopTemp());
        holder.unit1BottomTemp.setText(hotdogStorageModel.getUnit1BottomTemp());
        holder.unit2TopTemp.setText(hotdogStorageModel.getUnit2TopTemp());
        holder.unit2BottomTemp.setText(hotdogStorageModel.getUnit2BottomTemp());
        holder.staffInitials.setText(hotdogStorageModel.getStaffInitials());

        String initials = hotdogStorageModel.getStaffInitials();
        if (initials != null && !initials.equals("...")) {
            holder.staffInitials.setText(initials);
        }

        if (hotdogStorageModel.getCheckComplete()) {
            holder.completeCheckButton.setBackgroundColor(context.getResources().getColor(R.color.green));
        }

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View hotdogStoragePopupView = inflater.inflate(R.layout.layout_hotdog_storage_popup, null);
        final PopupWindow hotdogStoragePopupWindow = new PopupWindow(hotdogStoragePopupView, width, height, focusable);

        holder.completeCheckButton.setOnClickListener(v -> {
            hotdogStoragePopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            hotdogStoragePopupView.findViewById(R.id.cancelButton).setOnClickListener(cancelView -> {
                hotdogStoragePopupWindow.dismiss();
            });

            hotdogStoragePopupView.findViewById(R.id.confirmButton).setOnClickListener(confirmView -> {
                hotdogStoragePopupWindow.dismiss();

                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String currentTime = timeFormat.format(date);
                String currentDate = dateFormat.format(date);

                Log.d("HotDogCookingFragment", "Batch bagged at: " + currentTime);

                EditText unit1Top = hotdogStoragePopupView.findViewById(R.id.unit1Top);
                EditText unit1Bottom = hotdogStoragePopupView.findViewById(R.id.unit1Bottom);
                EditText unit2Top = hotdogStoragePopupView.findViewById(R.id.unit2Top);
                EditText unit2Bottom = hotdogStoragePopupView.findViewById(R.id.unit2Bottom);

                holder.completeCheckButton.setBackgroundColor(context.getResources().getColor(R.color.green));
                holder.staffInitials.setText(userInitials);
                holder.unit1TopTemp.setText(unit1Top.getText().toString());
                holder.unit1BottomTemp.setText(unit1Bottom.getText().toString());
                holder.unit2TopTemp.setText(unit2Top.getText().toString());
                holder.unit2BottomTemp.setText(unit2Bottom.getText().toString());

                hotdogStorageModel.setUnit1TopTemp(unit1Top.getText().toString());
                hotdogStorageModel.setUnit1BottomTemp(unit1Bottom.getText().toString());
                hotdogStorageModel.setUnit2TopTemp(unit2Top.getText().toString());
                hotdogStorageModel.setUnit2BottomTemp(unit2Bottom.getText().toString());
                hotdogStorageModel.setCheckComplete(true);
                hotdogStorageModel.setStaffInitials(userInitials);
                hotdogStorageModel.setTimeCompleted(currentTime);

                db.collection("Documents")
                        .document(currentDate)
                        .collection("Daily Concessions")
                        .document("Hot Dog Control")
                        .collection("Storage")
                        .document(hotdogStorageModel.getTimeDue())
                        .set(hotdogStorageModel)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("HotdogStorageAdapter", "Document successfully updated!");
                            notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Log.w("HotdogStorageAdapter", "Error updating document", e);
                        });
            });
        });
    }

    @Override
    public int getItemCount() {
        return hotdogStorageModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeDue;
        TextView unit1TopTemp;
        TextView unit1BottomTemp;
        TextView unit2TopTemp;
        TextView unit2BottomTemp;
        TextView staffInitials;
        Button completeCheckButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeDue = itemView.findViewById(R.id.timeDue);
            unit1TopTemp = itemView.findViewById(R.id.unit1Top);
            unit1BottomTemp = itemView.findViewById(R.id.unit1Bottom);
            unit2TopTemp = itemView.findViewById(R.id.unit2Top);
            unit2BottomTemp = itemView.findViewById(R.id.unit2Bottom);
            staffInitials = itemView.findViewById(R.id.staffInitials);
            completeCheckButton = itemView.findViewById(R.id.completeCheckButton);
        }
    }
}
