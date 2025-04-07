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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


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

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        long currentTime = System.currentTimeMillis();
        String currentTimeString = format.format(currentTime);



        concessionsTillSanitisationModelList = new ArrayList<>();
        concessionsTillSanitisationModelList.add(new ConcessionsTillSanitisationModel(false, "...", checkTimeList.get(0), areasToSanitise));
        concessionsTillSanitisationModelList.add(new ConcessionsTillSanitisationModel(false, "...", checkTimeList.get(1), areasToSanitise));
        concessionsTillSanitisationModelList.add(new ConcessionsTillSanitisationModel(false, "...", checkTimeList.get(2), areasToSanitise));
        concessionsTillSanitisationModelList.add(new ConcessionsTillSanitisationModel(false, "...", checkTimeList.get(3), areasToSanitise));
        concessionsTillSanitisationModelList.add(new ConcessionsTillSanitisationModel(false, "...", checkTimeList.get(4), areasToSanitise));


        concessionsTillSanitisationAdapter = new ConcessionsTillSanitisationAdapter(getContext(), concessionsTillSanitisationModelList);
        concessionsTillSanitisationAdapter.notifyDataSetChanged();
        RecyclerView recyclerView = view.findViewById(R.id.tillSanitisationRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(concessionsTillSanitisationAdapter);
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