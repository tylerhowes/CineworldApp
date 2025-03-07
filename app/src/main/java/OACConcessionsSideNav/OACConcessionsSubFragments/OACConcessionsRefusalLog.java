package OACConcessionsSideNav.OACConcessionsSubFragments;

import android.content.Context;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cineworldapp.AlcoholRefusalLogCardAdapter;
import com.example.cineworldapp.AlcoholRefusalLogDataModel;
import com.example.cineworldapp.FragmentAlcoholRefusalLogCompletion;
import com.example.cineworldapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class OACConcessionsRefusalLog extends Fragment {

    Button addLogButton;

    ArrayList<AlcoholRefusalLogDataModel> alcoholRefusalLogDataModelArrayList;
    AlcoholRefusalLogCardAdapter adapter;
    RecyclerView alcoholRefusalLogRV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_oac_floor_customer_assistance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Gson gson = new Gson();
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("alcoholRefusalFragPrefs", Context.MODE_PRIVATE);
        String json = sharedPrefs.getString("alcoholRefusalDataJson", "");
        Type type = new TypeToken<ArrayList<AlcoholRefusalLogDataModel>>(){}.getType();
        if(savedInstanceState != null){
            alcoholRefusalLogDataModelArrayList = savedInstanceState.getParcelableArrayList("alcoholRefusalDataJson");
        } else {
            alcoholRefusalLogDataModelArrayList = gson.fromJson(json, type);
            if(alcoholRefusalLogDataModelArrayList == null){
                alcoholRefusalLogDataModelArrayList = new ArrayList<>();
            }
        }

        alcoholRefusalLogRV = view.findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        adapter = new AlcoholRefusalLogCardAdapter(getContext(), alcoholRefusalLogDataModelArrayList);
        alcoholRefusalLogRV.setLayoutManager(linearLayoutManager);
        alcoholRefusalLogRV.setAdapter(adapter);

        addLogButton = view.findViewById(R.id.addLogButton);
        addLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                final FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                fragTransaction.add(R.id.oacConcessions, new FragmentAlcoholRefusalLogCompletion());
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();

            }
        });



        getActivity().getSupportFragmentManager().setFragmentResultListener("AlcoholRefusalCompleteKey", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                Log.d("OACConcessionsRefusalLog", "Alcohol Refusal received result");

                String customerNameDescription= result.getString("customerNameDescription");
                String product = result.getString("product");
                String reason= result.getString("reason");

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String time = timeFormat.format(new Date());

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String date = format.format(new Date());

                alcoholRefusalLogDataModelArrayList.add(new AlcoholRefusalLogDataModel( customerNameDescription, product, time, date, reason));

                adapter.notifyItemInserted(alcoholRefusalLogDataModelArrayList.size() - 1);

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("OACConcessionsRefusalLog", "On Pause Called");
        SharedPreferences preferences = getActivity().getSharedPreferences("alcoholRefusalFragPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(alcoholRefusalLogDataModelArrayList);
        editor.putString("alcoholRefusalDataJson", json);

        editor.apply();
    }
}