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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class DailyConcessionsEquipmentSanitisationFragment extends Fragment {


    private ArrayList<String> checkTimeList;
    private String areasToSanitise;
    private String closeAreasToSanitise;

    private ArrayList<ConcessionsEquipmentSanitisationModel> concessionsEquipmentSanitisationModelList;
    private ConcessionsEquipmentSanitisationAdapter concessionsEquipmentSanitisationAdapter;

    private TextView timerText;
    private CountDownTimer countDownTimer;
    private String emptyString = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkTimeList = new ArrayList<>();
        checkTimeList.add("Open");
        checkTimeList.add("12:00:00");
        checkTimeList.add("16:00:00");
        checkTimeList.add("20:00:00");
        checkTimeList.add("Close");

        areasToSanitise = "Hot Tongs, Jalapeno Scoops, Ice Scoops, Popcorn Scoops, Ice Cream Scoops, Pick n Mix Tongs, Ice Dollies";
        closeAreasToSanitise = "Concessions Counter, BR Counter, Concessions Door Handle, Pick n Mix Hopper Lids, Nachos Prep table";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_concessions_equipment_sanitisation, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        long currentTime = System.currentTimeMillis();
        String currentTimeString = format.format(currentTime);



        concessionsEquipmentSanitisationModelList = new ArrayList<>();
        concessionsEquipmentSanitisationModelList.add(new ConcessionsEquipmentSanitisationModel(false, "...", checkTimeList.get(0), areasToSanitise, emptyString));
        concessionsEquipmentSanitisationModelList.add(new ConcessionsEquipmentSanitisationModel(false, "...", checkTimeList.get(1), areasToSanitise, emptyString));
        concessionsEquipmentSanitisationModelList.add(new ConcessionsEquipmentSanitisationModel(false, "...", checkTimeList.get(2), areasToSanitise, emptyString));
        concessionsEquipmentSanitisationModelList.add(new ConcessionsEquipmentSanitisationModel(false, "...", checkTimeList.get(3), areasToSanitise, emptyString));
        concessionsEquipmentSanitisationModelList.add(new ConcessionsEquipmentSanitisationModel(false, "...", checkTimeList.get(4), areasToSanitise, closeAreasToSanitise));


        concessionsEquipmentSanitisationAdapter = new ConcessionsEquipmentSanitisationAdapter(getContext(), concessionsEquipmentSanitisationModelList);
        concessionsEquipmentSanitisationAdapter.notifyDataSetChanged();
        RecyclerView recyclerView = view.findViewById(R.id.equipmentSanitisationRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(concessionsEquipmentSanitisationAdapter);
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