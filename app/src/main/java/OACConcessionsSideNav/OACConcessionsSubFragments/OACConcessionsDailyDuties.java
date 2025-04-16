package OACConcessionsSideNav.OACConcessionsSubFragments;

import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
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

public class OACConcessionsDailyDuties extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String userInitials;
    TextView teamLeaderInitalsTV;
    String teamLeaderPasscode;

    int[] checkboxIds = {
            R.id.checkboxSanitisePhoneLightSwitches,
            R.id.checkboxRotateStockRooms,
            R.id.checkboxRotateFridgesSweetRack,
            R.id.checkboxCheckCupboardStock,
            R.id.checkboxSugarStirrerHolderWiped,
            R.id.checkboxLavazzaSyrupPumpsCleaned,
            R.id.checkboxCocoaSprinklerCleaned,
            R.id.checkboxBackCounterEquipmentDusted,
            R.id.checkboxExtraMunchboxesMade,
            R.id.checkboxPullOutFridgesFreezers,
            R.id.checkboxSoapDispensersDeepCleaned,
            R.id.checkboxSweetRacksWiped,
            R.id.checkboxMoveSweepSweetRacks,
            R.id.checkboxTillsDeepCleaned,
            R.id.checkboxCoffeeMachineCleaned,
            R.id.checkboxItemsUnderSinkTidied,
            R.id.checkboxDustHighLevelPOS,
            R.id.checkboxCupboardsUnderTillsCleaned,
            R.id.checkboxFrontSinksCleaned,
            R.id.checkboxGlassPolished,
            R.id.checkboxSyrupRackingWiped,
            R.id.checkboxCupboardsCleanedSanitised,
            R.id.checkboxLidPopcornHoldersCleaned,
            R.id.checkboxFridgesEmptiedCleaned,
            R.id.checkboxIceMachineCleaned,
            R.id.checkboxIceBinsDeepCleaned,
            R.id.checkboxKickPlatesWiped,
            R.id.checkboxStorageFreezersDefrosted
    };

    Map<Integer, Boolean> checkboxStates = new HashMap<>();
    Map<Integer, String> initialsStates = new HashMap<>();

    String currentDate;

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
        View view = inflater.inflate(R.layout.fragment_oac_concessions_daily_duties, container, false);

        DocumentReference dutiesRef = db.collection("Documents").document(currentDate)
                .collection("OAC Concessions").document("Daily Duties");

        // Check if Firestore document exists and initialize if not
        dutiesRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Map<String, Object> initMap = new HashMap<>();
                for (int checkboxID : checkboxIds) {
                    String checkboxName = getResources().getResourceEntryName(checkboxID);
                    initMap.put(checkboxName, false);
                    initMap.put(checkboxName + "Initials", "...");
                }
                dutiesRef.set(initMap);
            }
        });

        for (int checkboxID : checkboxIds) {
            CheckBox checkBox = view.findViewById(checkboxID);
            String checkboxName = getResources().getResourceEntryName(checkboxID);
            String initialsName = checkboxName + "Initials";
            int textViewID = getResources().getIdentifier(initialsName, "id", getActivity().getPackageName());
            TextView initialsTV = view.findViewById(textViewID);

            dutiesRef.get().addOnSuccessListener(documentSnapshot -> {
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
                dutiesRef.update(updateMap);
            });
        }

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
                                if ("teamLeader".equals(doc.get("role"))) {
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
