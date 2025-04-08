package com.example.cineworldapp;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ScreenCardAdapter extends RecyclerView.Adapter<ScreenCardAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<ScreenCardModel> screenCardModelArrayList;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userInitials;

    public ScreenCardAdapter(Context context, ArrayList<ScreenCardModel> screenCardModelArrayList) {
        this.context = context;
        this.screenCardModelArrayList = screenCardModelArrayList;
    }

    @NonNull
    @Override
    public ScreenCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_screen_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScreenCardAdapter.ViewHolder holder, int position) {
        ScreenCardModel screenCardModel = screenCardModelArrayList.get(position);
        holder.screen.setText(screenCardModel.getScreen());
        holder.title.setText(" "+screenCardModel.getTitle());
        holder.startTime.setText(screenCardModel.getStartTime());
        holder.featureTime.setText(screenCardModel.getFeatureTime());
        holder.finishTime.setText(screenCardModel.getFinishTime());



        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UID = auth.getCurrentUser().getUid();

        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials =  documentSnapshot.getString("initials");
                });


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date date = Calendar.getInstance().getTime();
        String currentDate = dateFormat.format(date);
        db.collection("Documents")
                .document(currentDate)
                .collection("Daily Floor")
                .document("Anti Piracy Checks")
                .collection("Screen " + screenCardModel.getScreen())
                .document(screenCardModel.getTitle() + " " + screenCardModel.getStartTime())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String path = "Documents/" + currentDate +
                                "/Anti Piracy Checks/Screen " + screenCardModel.getScreen() +
                                "/Showings/" + screenCardModel.getTitle() + " " + screenCardModel.getStartTime();

                        Log.d("ScreenCardAdapter", "Document path: " + path);
                        Log.d("ScreenCardAdapter", "document has been found");
                        if(task.isSuccessful()){
                            Log.d("ScreenCardAdapter", "Task was succesful");
                            DocumentSnapshot snapshot = task.getResult();
                            if(snapshot != null && snapshot.exists()){
                                Log.d("ScreenCardAdapter", "Snapshot Exists");
                                ScreenCardModel model = snapshot.toObject(ScreenCardModel.class);
                                if(model != null){
                                    Log.d("ScreenCardAdapter", "Model is not empty");
                                    ArrayList<ScreenCheck> checks = model.getChecks();
                                    // Do something with checks here
                                    Log.d("ScreenCardAdapter", "Size of checks: " + checks.size());
                                    for (int i =0; i < checks.size(); i++){
                                        ScreenCheck check = checks.get(i);
                                        if (check.getTime() != null && !check.getTime().isEmpty()) {
                                            holder.checkButtons.get(i).setBackgroundColor(context.getResources().getColor(R.color.green));
                                            holder.checkInitials.get(i).setText(model.getChecks().get(i).getStaffInitials());
                                        }
                                    }
                                    Log.d("Firestore", "Checks: " + checks.toString());
                                }
                            }
                        }
                    }
                });


        holder.checkButtons.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.layout_screen_check_popup, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                popupView.findViewById(R.id.completeCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.checkButtons.get(0).setBackgroundColor(context.getResources().getColor(R.color.green));
                        popupWindow.dismiss();
                        holder.checkInitials.get(0).setText(userInitials);



                        //Add code to save completed check in firebase
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        Date date = Calendar.getInstance().getTime();
                        String currentTime = timeFormat.format(date);
                        String currentDate = dateFormat.format(date);

                        Log.d("ScreenCardAdapter", "The current time of check is " + currentTime.toString());
                        //screenCardModel.setChecksCompleteTimes(checksCompleteTimes);

                        CheckBox nightVisionCheckBox = popupView.findViewById(R.id.checkboxNightVision);
                        CheckBox onScreenPerfectionCheckBox = popupView.findViewById(R.id.checkboxScreenPerfection);

                        ScreenCheck check = new ScreenCheck(currentTime.toString(), nightVisionCheckBox.isChecked(), onScreenPerfectionCheckBox.isChecked(), userInitials);
                        screenCardModel.getChecks().set(0, check);
                        db.collection("Documents")
                                .document(currentDate)
                                .collection("Daily Floor")
                                .document("Anti Piracy Checks")
                                .collection("Screen " + screenCardModel.getScreen())
                                .document(screenCardModel.getTitle() + " " + screenCardModel.getStartTime())
                                .set(screenCardModel);
                    }
                });

                popupView.findViewById(R.id.cancelCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do something
                        popupWindow.dismiss();
                    }
                });
            }
        });

        holder.checkButtons.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.layout_screen_check_popup, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                popupView.findViewById(R.id.completeCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.checkButtons.get(1).setBackgroundColor(context.getResources().getColor(R.color.green));
                        popupWindow.dismiss();
                        holder.checkInitials.get(1).setText(userInitials);
                        //Add code to save completed check in firebase
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        Date date = Calendar.getInstance().getTime();
                        String currentTime = timeFormat.format(date);
                        String currentDate = dateFormat.format(date);

                        CheckBox nightVisionCheckBox = popupView.findViewById(R.id.checkboxNightVision);
                        CheckBox onScreenPerfectionCheckBox = popupView.findViewById(R.id.checkboxScreenPerfection);

                        ScreenCheck check = new ScreenCheck(currentTime.toString(), nightVisionCheckBox.isChecked(), onScreenPerfectionCheckBox.isChecked(), userInitials);
                        screenCardModel.getChecks().set(1, check);
                        db.collection("Documents")
                                .document(currentDate)
                                .collection("Daily Floor")
                                .document("Anti Piracy Checks")
                                .collection("Screen " + screenCardModel.getScreen())
                                .document(screenCardModel.getTitle() + " " + screenCardModel.getStartTime())
                                .set(screenCardModel);
                    }
                });

                popupView.findViewById(R.id.cancelCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do something
                        popupWindow.dismiss();
                    }
                });
            }
        });

        holder.checkButtons.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.layout_screen_check_popup, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                popupView.findViewById(R.id.completeCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.checkButtons.get(2).setBackgroundColor(context.getResources().getColor(R.color.green));
                        popupWindow.dismiss();
                        holder.checkInitials.get(2).setText(userInitials);

                        //Add code to save completed check in firebase
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        Date date = Calendar.getInstance().getTime();
                        String currentTime = timeFormat.format(date);
                        String currentDate = dateFormat.format(date);
                        //screenCardModel.setChecksCompleteTimes(checksCompleteTimes);

                        CheckBox nightVisionCheckBox = popupView.findViewById(R.id.checkboxNightVision);
                        CheckBox onScreenPerfectionCheckBox = popupView.findViewById(R.id.checkboxScreenPerfection);

                        ScreenCheck check = new ScreenCheck(currentTime.toString(), nightVisionCheckBox.isChecked(), onScreenPerfectionCheckBox.isChecked(), userInitials);
                        screenCardModel.getChecks().set(2, check);
                        db.collection("Documents")
                                .document(currentDate)
                                .collection("Daily Floor")
                                .document("Anti Piracy Checks")
                                .collection("Screen " + screenCardModel.getScreen())
                                .document(screenCardModel.getTitle() + " " + screenCardModel.getStartTime())
                                .set(screenCardModel);}
                });

                popupView.findViewById(R.id.cancelCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do something
                        popupWindow.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return screenCardModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView screen;
        private final TextView title;
        private final TextView startTime;
        private final TextView featureTime;
        private final TextView finishTime;

        private final ArrayList<ScreenCheck> checks;
        private final List<Button> checkButtons;
        private final List<TextView> checkInitials;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkButtons = new ArrayList<>();
            checkInitials = new ArrayList<>();
            checks = new ArrayList<>(3);

            screen = itemView.findViewById(R.id.screenNumber);
            title = itemView.findViewById(R.id.filmTitle);
            startTime = itemView.findViewById(R.id.startTime);
            featureTime = itemView.findViewById(R.id.featureTime);
            finishTime = itemView.findViewById(R.id.endTime);
            checkButtons.add(itemView.findViewById(R.id.check1));
            checkButtons.add(itemView.findViewById(R.id.check2));
            checkButtons.add(itemView.findViewById(R.id.check3));
            checkInitials.add(itemView.findViewById(R.id.check1Initials));
            checkInitials.add(itemView.findViewById(R.id.check2Initials));
            checkInitials.add(itemView.findViewById(R.id.check3Initials));


        }
    }
}
