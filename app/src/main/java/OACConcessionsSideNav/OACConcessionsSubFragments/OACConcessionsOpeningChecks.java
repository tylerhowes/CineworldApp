package OACConcessionsSideNav.OACConcessionsSubFragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.cineworldapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OACConcessionsOpeningChecks extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String userInitials;
    String teamLeaderPasscode;
    TextView teamLeaderInitalsTV;
    String currentDate;

    int[] checkboxIds = {
            R.id.checkboxBIBDatesChecked,
            R.id.checkboxHotColdWaterAvailable,
            R.id.checkboxLightsIlluminated,
            R.id.checkboxEquipmentReady,
            R.id.checkboxElectricalSwitches,
            R.id.checkboxTillsReady,
            R.id.checkboxNachosReady,
            R.id.checkboxCondimentsAvailable,
            R.id.checkboxIceBinsFilled,
            R.id.checkboxCupsLidsStrawsNapkins,
            R.id.checkboxConfectioneryStocked,
            R.id.checkboxBainMarieReady,
            R.id.checkboxBottledDrinksStocked
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        currentDate = sdf.format(new Date());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String UID = auth.getCurrentUser().getUid();
        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials = documentSnapshot.getString("initials");
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oac_concessions_opening_checks, container, false);

        DocumentReference checksRef = db.collection("Documents").document(currentDate)
                .collection("OAC Concessions").document("Opening Checks");

        // Initialize Firestore document if it doesn't exist
        checksRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Map<String, Object> initMap = new HashMap<>();
                for (int checkboxID : checkboxIds) {
                    String checkboxName = getResources().getResourceEntryName(checkboxID);
                    initMap.put(checkboxName, false);
                    initMap.put(checkboxName + "Initials", "...");
                }
                checksRef.set(initMap);
            }
        });

        // Load and manage each checkbox + initials
        for (int checkboxID : checkboxIds) {
            CheckBox checkBox = view.findViewById(checkboxID);
            String checkboxName = getResources().getResourceEntryName(checkboxID);
            String initialsName = checkboxName + "Initials";

            int textViewID = getResources().getIdentifier(initialsName, "id", getActivity().getPackageName());
            TextView initialsTV = view.findViewById(textViewID);

            checksRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains(checkboxName)) {
                    boolean isChecked = documentSnapshot.getBoolean(checkboxName);
                    String initials = documentSnapshot.getString(initialsName);
                    checkBox.setChecked(isChecked);
                    initialsTV.setText(initials);
                }
            });

            checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                String initialsValue = isChecked ? userInitials : "...";
                initialsTV.setText(initialsValue);

                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put(checkboxName, isChecked);
                updateMap.put(initialsName, initialsValue);
                checksRef.update(updateMap);
            });
        }

        // Team leader sign off
        teamLeaderInitalsTV = view.findViewById(R.id.teamLeaderInitials);
        Button teamLeaderSignOff = view.findViewById(R.id.buttonTeamLeaderSignOff);
        teamLeaderSignOff.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Team Leader Passcode");

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                teamLeaderPasscode = input.getText().toString();
                db.collection("users").whereEqualTo("loginCode", teamLeaderPasscode).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if ("teamLeader".equals(doc.getString("role"))) {
                                    teamLeaderInitalsTV.setText(doc.getString("initials"));
                                } else {
                                    Toast.makeText(getActivity(), "Invalid Team Leader Passcode", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        return view;
    }
}
