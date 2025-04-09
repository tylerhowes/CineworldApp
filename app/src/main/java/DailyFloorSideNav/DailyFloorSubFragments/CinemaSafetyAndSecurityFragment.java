package DailyFloorSideNav.DailyFloorSubFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cineworldapp.CinemaSafetyAndSecurityAdapter;
import com.example.cineworldapp.CinemaSafetyAndSecurityModel;
import com.example.cineworldapp.FloorEquipmentSanitisationModel;
import com.example.cineworldapp.R;
import com.example.cineworldapp.ToiletCheckCardAdapter;
import com.example.cineworldapp.ToiletCheckDataModel;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CinemaSafetyAndSecurityFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userInitials;

    String openingCriteria =
            "• All fire exits are operational and free from obstruction\n" +
            "• All running men and emergency lighting are illuminated\n" +
            "• All fire extinguishers are present with Pin and Seal in place\n" +
            "• All house lights are working in all screens (note any issues found in comments)\n" +
            "• All Games machines are turned on and visually checked\n" +
            "• Auditorium is free from hazards\n" +
            "• Cleanliness is up to required standard";

    String midCriteria =
            "• All fire exits are operational and free from obstruction\n" +
            "• All running men and emergency lighting are illuminated\n" +
            "• All fire extinguishers are present with Pin and Seal in place (note any issues found)";

    String closeCriteria =
            "• Auditorium is clear of all customers\n" +
            "• Litter pick-up has been completed and all bins emptied\n" +
            "• All fire exits are secure\n" +
            "• All screen and foyer lighting is turned off\n" +
            "• All games machines are switched off";


    String[] checkTimes = {"Open", "Mid", "Close"};
    String areasToCheck = "Foyer and Corridors";
    String screensToCheck = "1, 2, 3, 4, 5, 6, 7";

    ArrayList<CinemaSafetyAndSecurityModel> cinemaSafetyAndSecurityModelArrayList = new ArrayList<>();
    CinemaSafetyAndSecurityAdapter adapter ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_floor_cinema_safety_and_security, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UID = auth.getCurrentUser().getUid();
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        db.collection("users").document(UID).get()
                .addOnSuccessListener(userDocumentSnapshot -> {
                    userInitials =  userDocumentSnapshot.getString("initials");

                    RecyclerView recyclerView = getView().findViewById(R.id.recyclerViewSafetyAndSecurity);
                    adapter = new CinemaSafetyAndSecurityAdapter(getContext(), cinemaSafetyAndSecurityModelArrayList, userInitials);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(adapter);

                    for(String s : checkTimes){
                        String timeOfCheck = s;

                        db.collection("Documents")
                                .document(currentDate)
                                .collection("Daily Floor")
                                .document("Safety and Security")
                                .collection("Logs")
                                .document(timeOfCheck)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Load from Firestore
                                        String areas = documentSnapshot.getString("areas");
                                        String screens = documentSnapshot.getString("screens");
                                        String staffInitials = documentSnapshot.getString("staffInitials");
                                        String timeCompleted = documentSnapshot.getString("timeCompleted");
                                        CinemaSafetyAndSecurityModel model = new CinemaSafetyAndSecurityModel(
                                                timeOfCheck,
                                                areas != null ? areas : "...",
                                                screens != null ? screens : "...",
                                                staffInitials != null ? staffInitials : "...",
                                                timeCompleted
                                        );
                                        cinemaSafetyAndSecurityModelArrayList.add(model);

                                    } else {

                                        Map<String, Object> defaultData = new HashMap<>();
                                        defaultData.put("timeOfCheck", timeOfCheck);
                                        defaultData.put("areas", areasToCheck);
                                        defaultData.put("screens", screensToCheck);
                                        defaultData.put("staffInitials", "...");
                                        defaultData.put("timeCompleted", "...");
                                        // Create a default document in Firestore

                                        db.collection("Documents")
                                                .document(currentDate)
                                                .collection("Daily Floor")
                                                .document("Safety and Security")
                                                .collection("Logs")
                                                .document(timeOfCheck)
                                                .set(defaultData);

                                        // Add default model to list
                                        CinemaSafetyAndSecurityModel model = new CinemaSafetyAndSecurityModel(
                                                timeOfCheck,
                                                areasToCheck,
                                                screensToCheck,
                                                "...",
                                                "..."
                                        );
                                        cinemaSafetyAndSecurityModelArrayList.add(model);
                                    }
                                    adapter.notifyDataSetChanged();
                                });
                    }



                });
    }
}