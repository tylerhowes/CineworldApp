package DailyConcessionsSideNav.DailyConcessionsSubFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cineworldapp.ConcessionsEquipmentSanitisationAdapter;
import com.example.cineworldapp.ConcessionsEquipmentSanitisationModel;
import com.example.cineworldapp.ConcessionsTillSanitisationAdapter;
import com.example.cineworldapp.ConcessionsTillSanitisationModel;
import com.example.cineworldapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class DailyConcessionsEquipmentSanitisationFragment extends Fragment {


    private ArrayList<String> checkTimeList;
    private String openAreasToSanitise;
    private String closeAreasToSanitise;

    private ArrayList<ConcessionsEquipmentSanitisationModel> concessionsEquipmentSanitisationModelList;
    private ConcessionsEquipmentSanitisationAdapter concessionsEquipmentSanitisationAdapter;

    private TextView timerText;
    private CountDownTimer countDownTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkTimeList = new ArrayList<>();
        checkTimeList.add("Open");
        checkTimeList.add("12:00:00");
        checkTimeList.add("16:00:00");
        checkTimeList.add("20:00:00");
        checkTimeList.add("Close");

        openAreasToSanitise = "Hot Tongs, Jalapeno Scoops, Ice Scoops, Popcorn Scoops, Ice Cream Scoops, Pick n Mix Tongs, Ice Dollies";
        closeAreasToSanitise = ", Concessions Counter, BR Counter, Concessions Door Handle, Pick n Mix Hopper Lids, Nachos Prep table";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_concessions_equipment_sanitisation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timerText = view.findViewById(R.id.timer);
        ResumeCountDown();

        concessionsEquipmentSanitisationModelList = new ArrayList<>();
        concessionsEquipmentSanitisationAdapter = new ConcessionsEquipmentSanitisationAdapter(getContext(), concessionsEquipmentSanitisationModelList, this);

        RecyclerView recyclerView = view.findViewById(R.id.equipmentSanitisationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(concessionsEquipmentSanitisationAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        for (String time : checkTimeList) {
            String areasToSanitise = "";
            if(time.equals("Close")){
                areasToSanitise = openAreasToSanitise + closeAreasToSanitise;
            }
            else{
                areasToSanitise = openAreasToSanitise;
            }

            String finalAreasToSanitise = areasToSanitise;
            db.collection("Documents")
                    .document(currentDate)
                    .collection("Daily Concessions")
                    .document("Equipment Sanitisation")
                    .collection("Logs")
                    .document(time)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Load from Firestore
                            boolean isSanitised = documentSnapshot.getBoolean("isSanitised") != null && documentSnapshot.getBoolean("isSanitised");
                            String initials = documentSnapshot.getString("staffInitials");
                            String timeCompleted = documentSnapshot.getString("timeCompleted");

                            ConcessionsEquipmentSanitisationModel model = new ConcessionsEquipmentSanitisationModel(
                                    isSanitised,
                                    initials != null ? initials : "...",
                                    time,
                                    finalAreasToSanitise,
                                    timeCompleted
                            );
                            concessionsEquipmentSanitisationModelList.add(model);
                        } else {
                            // Create a default document in Firestore
                            Map<String, Object> defaultData = new HashMap<>();
                            defaultData.put("isSanitised", false);
                            defaultData.put("staffInitials", "...");
                            defaultData.put("timeDue", time);
                            defaultData.put("areasToSanitise", finalAreasToSanitise);
                            defaultData.put("timeCompleted", null);

                            db.collection("Documents")
                                    .document(currentDate)
                                    .collection("Daily Concessions")
                                    .document("Equipment Sanitisation")
                                    .collection("Logs")
                                    .document(time)
                                    .set(defaultData);

                            // Add default model to list
                            concessionsEquipmentSanitisationModelList.add(new ConcessionsEquipmentSanitisationModel(false, "...", time, finalAreasToSanitise, null));
                        }

                        concessionsEquipmentSanitisationAdapter.notifyDataSetChanged(); // Refresh the list once item is added
                    });
        }
    }


    public void StartCountDownTimer(){
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        countDownTimer= new CountDownTimer(100000, 1000){
            public void onTick(long millisUntilFinished){
                NumberFormat format = new DecimalFormat("00");
                timerText.setText("" + millisUntilFinished / 1000);
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                timerText.setText(format.format(hour) + ":" + format.format(min) + ":" + format.format(sec));
            }
            public void onFinish(){
                timerText.setText("CHECK DUE");
            }
        }.start();
    }


    public void ResumeCountDown(){
        SharedPreferences preferences = requireActivity().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE);
        long savedTime = preferences.getLong("timeLeft", 0);
        long timePaused = preferences.getLong("timePaused", 0);
        long timeSincePause = System.currentTimeMillis() - timePaused;

        long timeLeft = savedTime - timeSincePause;

        if(timeLeft > 0){
            countDownTimer = new CountDownTimer(timeLeft ,3600000 ){
                public void onTick(long millisUntilFinished){
                    NumberFormat format = new DecimalFormat("00");
                    timerText.setText("" + millisUntilFinished / 1000);
                    long hour = (millisUntilFinished / 3600000) % 24;
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    timerText.setText(format.format(hour) + ":" + format.format(min) + ":" + format.format(sec));
                }
                public void onFinish(){
                    timerText.setText("CHECK DUE");
                }
            }.start();
        }else{
            timerText.setText("00:00:00");
        }
    }
}