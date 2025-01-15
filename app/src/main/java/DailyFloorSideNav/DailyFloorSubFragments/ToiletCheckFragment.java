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

import com.example.cineworldapp.R;
import com.example.cineworldapp.ToiletCheckCardAdapter;
import com.example.cineworldapp.ToiletCheckDataModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class ToiletCheckFragment extends Fragment {

    TextView timerText;
    CountDownTimer countDownTimer;

    ArrayList<ToiletCheckDataModel> toiletCheckDataList = new ArrayList<>();
    ToiletCheckCardAdapter adapter;


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



        Gson gson = new Gson();
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE);
        String json = sharedPrefs.getString("toiletCheckDataListJson", "");
        Type type = new TypeToken<ArrayList<ToiletCheckDataModel>>(){}.getType();
        if(savedInstanceState != null){
            toiletCheckDataList = savedInstanceState.getParcelableArrayList("toiletCheckDataList");
        } else {
            toiletCheckDataList = gson.fromJson(json, type);
            if(toiletCheckDataList == null){
                toiletCheckDataList = new ArrayList<>();
            }
        }






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

                toiletCheckDataList.add(new ToiletCheckDataModel(""+ (toiletCheckDataList.size()+1), actualTime, "TH"));
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

        Log.d("ToiletCheckFragment", "On Pause Called");
        SharedPreferences preferences = getActivity().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE);
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

        Gson gson = new Gson();
        String json = gson.toJson(toiletCheckDataList);
        editor.putString("toiletCheckDataListJson", json);

        editor.apply();
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