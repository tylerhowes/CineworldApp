package DailyFloorSideNav.DailyFloorSubFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.cineworldapp.R;
import com.example.cineworldapp.ToiletCheckCardAdapter;
import com.example.cineworldapp.ToiletCheckDataModel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;


public class ToiletCheckFragment extends Fragment {

    TextView timerText;
    CountDownTimer countDownTimer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_daily_floor_toilet_check, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = getView().findViewById(R.id.toiletCheckRecyclerView);

        ArrayList<ToiletCheckDataModel> toiletCheckDataList = new ArrayList<>();

        toiletCheckDataList.add(new ToiletCheckDataModel("1", "12:00", "AB"));
        toiletCheckDataList.add(new ToiletCheckDataModel("2", "12:30", "CD"));
        toiletCheckDataList.add(new ToiletCheckDataModel("3", "13:00", "EF"));

        ToiletCheckCardAdapter adapter = new ToiletCheckCardAdapter(getContext(), toiletCheckDataList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        Button newCheck = getView().findViewById(R.id.newCheckButton);
        newCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countDownTimer != null){
                    countDownTimer.cancel();
                }
                countDownTimer= new CountDownTimer(10000, 1000){
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
        });

        timerText = getView().findViewById(R.id.timer);

        SharedPreferences preferences = requireActivity().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE);

        long savedTime = preferences.getLong("timeLeft", 0);
        long timePaused = preferences.getLong("timePaused", 0);
        long timeSincePause = System.currentTimeMillis() - timePaused;

        long timeLeft = savedTime - timeSincePause;

        if(timeLeft > 0){
            if(countDownTimer != null){
                countDownTimer.cancel();
            }
            countDownTimer = new CountDownTimer(timeLeft ,1000){
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

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences preferences = getActivity().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String timeString = timerText.getText().toString();
        String[] timeComponent = timeString.split(":");

        int hour = Integer.parseInt(timeComponent[0]);
        int min = Integer.parseInt(timeComponent[1]);
        int sec = Integer.parseInt(timeComponent[2]);

        long millis = (hour * 3600000) + (min * 60000) + (sec * 1000);

        editor.putLong("timeLeft", millis);
        editor.putLong("timePaused", System.currentTimeMillis());
        editor.apply();
    }

}