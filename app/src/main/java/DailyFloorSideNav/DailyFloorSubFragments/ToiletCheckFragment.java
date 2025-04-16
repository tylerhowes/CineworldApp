package DailyFloorSideNav.DailyFloorSubFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cineworldapp.AlcoholRefusalLogDataModel;
import com.example.cineworldapp.R;
import com.example.cineworldapp.ToiletCheckCardAdapter;
import com.example.cineworldapp.ToiletCheckDataModel;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ToiletCheckFragment extends Fragment {

    TextView timerText;
    CountDownTimer countDownTimer;

    ArrayList<ToiletCheckDataModel> toiletCheckDataList = new ArrayList<>();
    ToiletCheckCardAdapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("toiletCheckDataList", toiletCheckDataList);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_floor_toilet_check, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoadToiletChecks();

        Log.d("ToiletCheckFragment", "On View Created Called");
        Log.d("ToiletCheckFragment", "Size of Check Array in onCreateView: " + toiletCheckDataList.size());

        timerText = getView().findViewById(R.id.timer);
        ResumeCountDown();

        getActivity().getSupportFragmentManager().setFragmentResultListener("CheckCompleteKey", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                Log.d("ToiletCheckFragment", "Fragment Result Received");

                String mensData = result.getString("mensData");
                String womensData = result.getString("womensData");
                String disabledData = result.getString("disabledData");
                Log.d("ToiletCheckFragment", "Mens: " + mensData + " Womens: " + womensData + " Disabled: " + disabledData);

                long currentTime = System.currentTimeMillis();
                SimpleDateFormat format= new SimpleDateFormat("HH:mm");
                String actualTime = format.format(currentTime);

                ToiletCheckDataModel model = new ToiletCheckDataModel(""+ (toiletCheckDataList.size()+1), actualTime, "TH", mensData, womensData, disabledData);

                SaveLogToFirestore(model);
                toiletCheckDataList.add(model);
                Log.d("ToiletCheckFragment", "Size of Array in onFrag result: " + toiletCheckDataList.size());

                adapter.notifyItemInserted(toiletCheckDataList.size() - 1);

                //StartTimer
                StartCountDownTimer();
            }
        });

        RecyclerView recyclerView = getView().findViewById(R.id.toiletCheckRecyclerView);
        adapter = new ToiletCheckCardAdapter(getContext(), toiletCheckDataList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        Button newCheck = getView().findViewById(R.id.newCheckButton);
        newCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                final FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                fragTransaction.add(R.id.dailyFloorFragment, new ToiletCheckCompletionFragment());
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        if (countDownTimer != null) {
            countDownTimer.cancel(); // Stop the timer
        }

        Log.d("ToiletCheckFragment", "On Pause Called");
        SharedPreferences preferences = getActivity().getSharedPreferences("toiletTimerPrefs", Context.MODE_PRIVATE);
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
        ResumeCountDown();
    }

    public void StartCountDownTimer(){
        if(countDownTimer != null){
            countDownTimer.cancel();
        }
        countDownTimer= new CountDownTimer(3600000, 1000){
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
        SharedPreferences preferences = requireActivity().getSharedPreferences("toiletTimerPrefs", Context.MODE_PRIVATE);
        long savedTime = preferences.getLong("timeLeft", 0);
        long timePaused = preferences.getLong("timePaused", 0);

        if (savedTime > 0) {
            long elapsedTime = System.currentTimeMillis() - timePaused;
            long timeLeft = savedTime - elapsedTime;

            if (timeLeft > 0) {
                countDownTimer = new CountDownTimer(timeLeft, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        NumberFormat format = new DecimalFormat("00");
                        long hours = (millisUntilFinished / 3600000) % 24;
                        long minutes = (millisUntilFinished / 60000) % 60;
                        long seconds = (millisUntilFinished / 1000) % 60;

                        timerText.setText(format.format(hours) + ":" + format.format(minutes) + ":" + format.format(seconds));
                    }
                    @Override
                    public void onFinish() {
                        timerText.setText("CHECK DUE");
                    }
                }.start();
            } else {
                timerText.setText("CHECK DUE");
            }
        }
    }

    private void LoadToiletChecks() {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        CollectionReference logsRef = db.collection("Documents")
                .document(currentDate)
                .collection("Daily Floor")
                .document("Toilet Checks")
                .collection("Logs");

        logsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            toiletCheckDataList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                ToiletCheckDataModel model = new ToiletCheckDataModel(
                        doc.getString("toiletCheckNumber"),
                        doc.getString("toiletCheckTime"),
                        doc.getString("staffInitials"),
                        doc.getString("mensCorrectiveActions"),
                        doc.getString("womensCorrectiveActions"),
                        doc.getString("disabledCorrectiveActions")
                );
                toiletCheckDataList.add(model);
            }
            adapter.notifyDataSetChanged();
            Log.d("ToiletCheckFragment", "Data loaded from Firestore.");
        }).addOnFailureListener(e -> {
            Log.e("ToiletCheckFragment", "Error loading data from Firestore", e);
        });
    }

    private void SaveLogToFirestore(ToiletCheckDataModel model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        // Prepare the data to be saved as a map
        // Create a map of log data to be stored in Firestore
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("toiletCheckNumber", model.getToiletCheckNumber());
        dataMap.put("toiletCheckTime", model.getToiletCheckTime());
        dataMap.put("staffInitials", model.getStaffInitials());
        dataMap.put("mensCorrectiveActions", model.getMensCorrectiveActions());
        dataMap.put("womensCorrectiveActions", model.getWomensCorrectiveActions());
        dataMap.put("disabledCorrectiveActions", model.getDisabledCorrectiveActions());

        // Firestore path to store the log
        db.collection("Documents")
                .document(currentDate)
                .collection("Daily Floor")
                .document("Toilet Checks")
                .collection("Logs")
                .add(dataMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("AlcoholRefusalLog", "Log added to Firestore.");
                })
                .addOnFailureListener(e -> {
                    Log.e("AlcoholRefusalLog", "Error writing log to Firestore", e);
                });
    }
}