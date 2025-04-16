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

import com.example.cineworldapp.ConcessionsTillSanitisationAdapter;
import com.example.cineworldapp.ConcessionsTillSanitisationModel;
import com.example.cineworldapp.FloorEquipmentSanitisationAdapter;
import com.example.cineworldapp.FloorEquipmentSanitisationModel;
import com.example.cineworldapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DailyConcessionsTillSanitisationFragment extends Fragment {


    private ArrayList<String> checkTimeList;
    private String areasToSanitise;

    private ArrayList<ConcessionsTillSanitisationModel> concessionsTillSanitisationModelList;
    private ConcessionsTillSanitisationAdapter concessionsTillSanitisationAdapter;

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

        areasToSanitise = "All Tills";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_concessions_till_sanitisation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        timerText = view.findViewById(R.id.timer);
        ResumeCountDown();

        concessionsTillSanitisationModelList = new ArrayList<>();
        concessionsTillSanitisationAdapter = new ConcessionsTillSanitisationAdapter(getContext(), concessionsTillSanitisationModelList, this);

        RecyclerView recyclerView = view.findViewById(R.id.tillSanitisationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(concessionsTillSanitisationAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        for (String time : checkTimeList) {

            db.collection("Documents")
                    .document(currentDate)
                    .collection("Daily Concessions")
                    .document("Till Sanitisation")
                    .collection("Logs")
                    .document(time)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Load from Firestore
                            boolean isSanitised = documentSnapshot.getBoolean("isSanitised") != null && documentSnapshot.getBoolean("isSanitised");
                            String initials = documentSnapshot.getString("staffInitials");
                            String timeCompleted = documentSnapshot.getString("timeCompleted");

                            ConcessionsTillSanitisationModel model = new ConcessionsTillSanitisationModel(
                                    isSanitised,
                                    initials != null ? initials : "...",
                                    time,
                                    areasToSanitise,
                                    timeCompleted
                            );
                            concessionsTillSanitisationModelList.add(model);
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
                                    .collection("Daily Concessions")
                                    .document("Till Sanitisation")
                                    .collection("Logs")
                                    .document(time)
                                    .set(defaultData);

                            // Add default model to list
                            concessionsTillSanitisationModelList.add(new ConcessionsTillSanitisationModel(false, "...", time, areasToSanitise, null));
                        }

                        concessionsTillSanitisationAdapter.notifyDataSetChanged(); // Refresh the list once item is added
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