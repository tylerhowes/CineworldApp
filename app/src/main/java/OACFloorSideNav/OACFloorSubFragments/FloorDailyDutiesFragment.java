package OACFloorSideNav.OACFloorSubFragments;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cineworldapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class FloorDailyDutiesFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String userInitials;
    TextView teamLeaderInitalsTV;
    String teamLeaderPasscode;

    int[] checkboxIds = {
            R.id.checkboxToiletsStocked,
            R.id.checkboxPosterCases,
            R.id.checkboxPOSClean,
            R.id.checkboxScreenNumbers,
            R.id.checkboxWoodenLedge,
            R.id.checkboxDropboxClean,
            R.id.checkboxGlassDoors,
            R.id.checkboxATMGiftCards,
            R.id.checkboxBoosterSeats,
            R.id.checkboxSkirtingClean,
            R.id.checkboxFireExtinguishers,
            R.id.checkboxRunningManLights,
            R.id.checkboxPickups,
            R.id.checkboxWallsAirvents,
            R.id.checkboxPillars,
            R.id.checkboxDoorFrames,
            R.id.checkboxScreenDoors,
            R.id.checkboxCorridorBins,
            R.id.checkbox1And2Corridor,
            R.id.checkboxTVClean,
            R.id.checkboxWetFloorSign,
            R.id.checkboxStockRooms,
            R.id.checkboxStockRoomShelving,
            R.id.checkboxSluiceRoomClean,
            R.id.checkboxCineworldLogo,
            R.id.checkboxUnlimitedGlass,
            R.id.checkboxBoxOffice
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_oac_floor_daily_duties, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UID = auth.getCurrentUser().getUid();

        String todayDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());

        DocumentReference dutiesRef = db
                .collection("Documents")
                .document(todayDate)
                .collection("Daily Floor")
                .document("Daily Duties");

        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials = documentSnapshot.getString("initials");

                    dutiesRef.get().addOnSuccessListener(snapshot -> {
                        if (!snapshot.exists()) {
                            Map<String, Object> defaultData = new HashMap<>();
                            for (int id : checkboxIds) {
                                String checkboxName = getResources().getResourceEntryName(id);
                                String textViewName = checkboxName + "Initials";
                                defaultData.put(checkboxName, false);
                                defaultData.put(textViewName, "...");
                            }
                            defaultData.put("teamLeaderInitials", "...");
                            dutiesRef.set(defaultData); // Create document with defaults
                        }

                        // Now load UI with snapshot (even if just created)
                        for (int id : checkboxIds) {
                            CheckBox checkBox = view.findViewById(id);
                            String checkboxName = getResources().getResourceEntryName(id);
                            String textViewName = checkboxName + "Initials";
                            int textViewID = getResources().getIdentifier(textViewName, "id", getActivity().getPackageName());
                            TextView textView = view.findViewById(textViewID);

                            if (checkBox != null && textView != null) {
                                boolean isChecked = snapshot.getBoolean(checkboxName) != null && snapshot.getBoolean(checkboxName);
                                String initials = snapshot.getString(textViewName);
                                if (initials == null) initials = "...";

                                checkBox.setChecked(isChecked);
                                textView.setText(initials);

                                checkBox.setOnCheckedChangeListener((compoundButton, checked) -> {
                                    textView.setText(checked ? userInitials : "...");
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put(checkboxName, checked);
                                    updates.put(textViewName, textView.getText().toString());
                                    dutiesRef.set(updates, SetOptions.merge());
                                });
                            }
                        }
                    });

                });

        teamLeaderInitalsTV = view.findViewById(R.id.teamLeaderInitials);
        Button teamLeaderSignOff = view.findViewById(R.id.buttonTeamLeaderSignOff);

        teamLeaderSignOff.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Team Leader Passcode");

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                teamLeaderPasscode = input.getText().toString();

                db.collection("users").whereEqualTo("loginCode", teamLeaderPasscode)
                        .get()
                        .addOnSuccessListener((QuerySnapshot queryDocumentSnapshots) -> {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if ("teamLeader".equals(doc.getString("role"))) {
                                    String initials = doc.getString("initials");
                                    teamLeaderInitalsTV.setText(initials);
                                    Map<String, Object> update = new HashMap<>();
                                    update.put("teamLeaderInitials", initials);
                                    dutiesRef.set(update, SetOptions.merge());
                                    return;
                                }
                            }
                            Toast.makeText(getActivity(), "Invalid Team Leader Passcode", Toast.LENGTH_SHORT).show();
                        });
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        return view;
    }
}
