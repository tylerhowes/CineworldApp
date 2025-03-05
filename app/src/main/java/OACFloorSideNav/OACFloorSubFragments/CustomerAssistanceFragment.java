package OACFloorSideNav.OACFloorSubFragments;

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

import com.example.cineworldapp.CustomerAssistanceCardAdapter;
import com.example.cineworldapp.CustomerAssistanceDataModel;
import com.example.cineworldapp.R;
import com.example.cineworldapp.ToiletCheckDataModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import DailyFloorSideNav.DailyFloorSubFragments.ToiletCheckCompletionFragment;


public class CustomerAssistanceFragment extends Fragment {

    Button addLogButton;

    ArrayList<CustomerAssistanceDataModel> customerAssistanceDataModelArrayList;
    CustomerAssistanceCardAdapter adapter;
    RecyclerView customerAssistanceRV;

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
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("customerAssistanceFragPrefs", Context.MODE_PRIVATE);
        String json = sharedPrefs.getString("customerAssistanceDataJson", "");
        Type type = new TypeToken<ArrayList<CustomerAssistanceDataModel>>(){}.getType();
        if(savedInstanceState != null){
            customerAssistanceDataModelArrayList = savedInstanceState.getParcelableArrayList("customerAssistanceArrayList");
        } else {
            customerAssistanceDataModelArrayList = gson.fromJson(json, type);
            if(customerAssistanceDataModelArrayList == null){
                customerAssistanceDataModelArrayList = new ArrayList<>();
            }
        }

        customerAssistanceRV = view.findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        adapter = new CustomerAssistanceCardAdapter(getContext(), customerAssistanceDataModelArrayList);
        customerAssistanceRV.setLayoutManager(linearLayoutManager);
        customerAssistanceRV.setAdapter(adapter);

        addLogButton = view.findViewById(R.id.addLogButton);
        addLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                final FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                fragTransaction.add(R.id.oacFloor, new CustomerAssistanceCompletionFragment());
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();

            }
        });



        getActivity().getSupportFragmentManager().setFragmentResultListener("CustomerAssistanceCheckCompleteKey", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                Log.d("CustomerAssistance", "Customer Assistance received value");

                String customerName= result.getString("customerName");
                String screenNumber = result.getString("screenNumber");
                String seatNumber= result.getString("seatNumber");
                String startTime= result.getString("startTime");
                String finishTime= result.getString("finishTime");
                String assistanceRequired= result.getString("assistanceRequired");

                customerAssistanceDataModelArrayList.add(new CustomerAssistanceDataModel(customerName, screenNumber, seatNumber, startTime, finishTime, assistanceRequired));

                adapter.notifyItemInserted(customerAssistanceDataModelArrayList.size() - 1);



            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("CustomerAssistance", "On Pause Called");
        SharedPreferences preferences = getActivity().getSharedPreferences("customerAssistanceFragPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(customerAssistanceDataModelArrayList);
        editor.putString("customerAssistanceDataJson", json);

        editor.apply();
    }
}