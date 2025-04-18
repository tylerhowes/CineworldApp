package DailyConcessionsSideNav.DailyConcessionsSubFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.cineworldapp.ConcessionsHotdogBatchAdapter;
import com.example.cineworldapp.ConcessionsHotdogStorageAdapter;
import com.example.cineworldapp.FloorEquipmentSanitisationAdapter;
import com.example.cineworldapp.FloorEquipmentSanitisationModel;
import com.example.cineworldapp.HotdogStorageModel;
import com.example.cineworldapp.R;
import com.example.cineworldapp.ScreenCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class HotDogStorageFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private ArrayList<String> checkTimeList;

    private ArrayList<HotdogStorageModel> hotdogStorageModelList;
    private ConcessionsHotdogStorageAdapter concessionsHotdogStorageAdapter;

    private String userInitials;

    public HotDogStorageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkTimeList = new ArrayList<>();
        checkTimeList.add("12:00");
        checkTimeList.add("14:00");
        checkTimeList.add("16:00");
        checkTimeList.add("18:00");
        checkTimeList.add("20:00");
        checkTimeList.add("22:00");
        checkTimeList.add("00:00");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hot_dog_storage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        hotdogStorageModelList = new ArrayList<>();

        String UID = auth.getCurrentUser().getUid();

        db.collection("users").document(UID).get()
                .addOnSuccessListener(userDocumentSnapshot -> {
                    userInitials = userDocumentSnapshot.getString("initials");

                    Log.d("HotDogCookingFragment", "User Initials loaded: " + userInitials);
                    concessionsHotdogStorageAdapter = new ConcessionsHotdogStorageAdapter(getContext(), hotdogStorageModelList, userInitials);

                    RecyclerView recyclerView = view.findViewById(R.id.hotdogStorageRecyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(concessionsHotdogStorageAdapter);

                    for (String time : checkTimeList) {

                        db.collection("Documents")
                                .document(currentDate)
                                .collection("Daily Concessions")
                                .document("Hot Dog Control")
                                .collection("Storage")
                                .document(time)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Load from Firestore
                                        String unit1Top = documentSnapshot.getString("unit1TopTemp");
                                        String unit1Bottom = documentSnapshot.getString("unit1BottomTemp");
                                        String unit2Top = documentSnapshot.getString("unit2TopTemp");
                                        String unit2Bottom = documentSnapshot.getString("unit2BottomTemp");
                                        String staffInitials = documentSnapshot.getString("staffInitials");
                                        String teamLeaderInitials = documentSnapshot.getString("teamLeaderInitials");
                                        Boolean checkComplete = documentSnapshot.getBoolean("checkComplete");
                                        String timeCompeleted = documentSnapshot.getString("timeCompleted");

                                        HotdogStorageModel model = new HotdogStorageModel(
                                                time,
                                                unit1Top,
                                                unit1Bottom,
                                                unit2Top,
                                                unit2Bottom,
                                                staffInitials,
                                                teamLeaderInitials,
                                                checkComplete,
                                                timeCompeleted
                                        );
                                        hotdogStorageModelList.add(model);
                                    } else {
                                        // Create a default document in Firestore
                                        Map<String, Object> defaultData = new HashMap<>();
                                        defaultData.put("unit1TopTemp", "...");
                                        defaultData.put("unit1BottomTemp", "...");
                                        defaultData.put("unit2TopTemp", "...");
                                        defaultData.put("unit2BottomTemp", "...");
                                        defaultData.put("staffInitials", "...");
                                        defaultData.put("teamLeaderInitials", "...");
                                        defaultData.put("checkComplete", false);
                                        defaultData.put("timeDue", time);
                                        defaultData.put("timeCompleted", "...");
                                        

                                        db.collection("Documents")
                                                .document(currentDate)
                                                .collection("Daily Concessions")
                                                .document("Hot Dog Control")
                                                .collection("Storage")
                                                .document(time)
                                                .set(defaultData);

                                        // Add default model to list
                                        hotdogStorageModelList.add(new HotdogStorageModel(
                                                time,
                                                "...",
                                                "...",
                                                "...",
                                                "...",
                                                "...",
                                                "...",
                                                false,
                                                null
                                        ));
                                    }

                                    concessionsHotdogStorageAdapter.notifyDataSetChanged(); // Refresh the list once item is added
                                });
                    }
                });


    }
}