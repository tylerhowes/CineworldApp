package DailyFloorSideNav.DailyFloorSubFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cineworldapp.FloorEquipmentSanitisationAdapter;
import com.example.cineworldapp.FloorEquipmentSanitisationModel;
import com.example.cineworldapp.R;
import com.example.cineworldapp.ToiletCheckDataModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FloorEquipmentSanitisationFragment extends Fragment {

    private ArrayList<String> checkTimeList;
    private String areasToSanitise;

    private ArrayList<FloorEquipmentSanitisationModel> floorEquipmentSanitisationList;
    private FloorEquipmentSanitisationAdapter floorEquipmentSanitisationAdapter;

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

        areasToSanitise = "ATM1, ATM2, ATM3";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.fragment_daily_floor_equipment_sanitisation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timerText = view.findViewById(R.id.timer);
        ResumeCountDown();

        floorEquipmentSanitisationList = new ArrayList<>();
        floorEquipmentSanitisationAdapter = new FloorEquipmentSanitisationAdapter(getContext(), floorEquipmentSanitisationList, this);

        RecyclerView recyclerView = view.findViewById(R.id.equipmentSanitisationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(floorEquipmentSanitisationAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        for (String time : checkTimeList) {

            db.collection("Documents")
                    .document(currentDate)
                    .collection("Daily Floor")
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

                            FloorEquipmentSanitisationModel model = new FloorEquipmentSanitisationModel(
                                    isSanitised,
                                    initials != null ? initials : "...",
                                    time,
                                    areasToSanitise,
                                    timeCompleted
                            );
                            floorEquipmentSanitisationList.add(model);
                        } else {
                            // Create a default document in Firestore
                            Map<String, Object> defaultData = new HashMap<>();
                            defaultData.put("isSanitised", false);
                            defaultData.put("staffInitials", "...");
                            defaultData.put("timeDue", time);
                            defaultData.put("areasToSanitise", areasToSanitise);
                            defaultData.put("timeCompleted", null);

                            db.collection("Documents")
                                    .document(currentDate)
                                    .collection("Daily Floor")
                                    .document("Equipment Sanitisation")
                                    .collection("Logs")
                                    .document(time)
                                    .set(defaultData);

                            // Add default model to list
                            floorEquipmentSanitisationList.add(new FloorEquipmentSanitisationModel(false, "...", time, areasToSanitise, null));
                        }

                        floorEquipmentSanitisationAdapter.notifyDataSetChanged(); // Refresh the list once item is added
                    });
        }
    }


    public void StartCountDownTimer(){
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        countDownTimer= new CountDownTimer(14400000, 1000){
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

    @Override
    public void onPause() {
        super.onPause();

        if (countDownTimer != null) {
            countDownTimer.cancel(); // Stop the timer
        }

        Log.d("FloorEquipmentSanitisationFragment", "On Pause Called");
        SharedPreferences preferences = getActivity().getSharedPreferences("saniTimePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String timeString = timerText.getText().toString();
        if(timeString.equals("CHECK DUE")){
            timeString = "00:00:00";
        }
        String[] timeComponent = timeString.split(":");

        int hour = Integer.parseInt(timeComponent[0]);
        int min = Integer.parseInt(timeComponent[1]);
        int sec = Integer.parseInt(timeComponent[2]);

        long millis = (hour * 3600000) + (min * 60000) + (sec * 1000);

        editor.putLong("timeLeft", millis);
        editor.putLong("timePaused", System.currentTimeMillis());

        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void ResumeCountDown(){
        if (timerText == null) {
            Log.e("ResumeCountDown", "Timer text is null, skipping resume");
            return;
        }

        SharedPreferences preferences = requireActivity().getSharedPreferences("saniTimePrefs", Context.MODE_PRIVATE);
        long savedTime = preferences.getLong("timeLeft", 0);
        long timePaused = preferences.getLong("timePaused", 0);
        long timeSincePause = System.currentTimeMillis() - timePaused;

        long timeLeft = savedTime - timeSincePause;

        if(timeLeft > 0){
            countDownTimer = new CountDownTimer(timeLeft , 1000){
                public void onTick(long millisUntilFinished){
                    NumberFormat format = new DecimalFormat("00");
                    long hour = (millisUntilFinished / 3600000) % 24;
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    timerText.setText(format.format(hour) + ":" + format.format(min) + ":" + format.format(sec));
                }
                public void onFinish(){
                    timerText.setText("CHECK DUE");
                }
            }.start();
        } else {
            timerText.setText("00:00:00");
        }
    }
}