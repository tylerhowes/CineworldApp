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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FloorClosingChecksFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String userInitials;
    TextView teamLeaderInitalsTV;
    String teamLeaderPasscode;

    int[] checkboxIds = {
            R.id.checkboxSluiceRoom,
            R.id.checkboxFireExits,
            R.id.checkboxBins,
            R.id.checkboxNoCustomers,
            R.id.checkboxRadiosReturned,
            R.id.checkboxCleanSreens,
            R.id.checkboxFoyerTidy,
            R.id.checkboxCleaningTrolley,
            R.id.checkboxLightsOff,
            R.id.checkboxTraps,
            R.id.checkboxLostProperty,
            R.id.checkboxLockerRooms,
            R.id.checkboxStaffRoom
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oac_floor_closing_checks, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UID = auth.getCurrentUser().getUid();

        teamLeaderInitalsTV = view.findViewById(R.id.teamLeaderInitials);

        String todayDate = getTodayDate();
        DocumentReference closingChecksRef = db
                .collection("Documents")
                .document(todayDate)
                .collection("OAC Floor")
                .document("Closing Checks");

        // Load user's initials
        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials = documentSnapshot.getString("initials");

                    // Load existing checkbox states from Firestore
                    closingChecksRef.get().addOnSuccessListener(snapshot -> {
                        if (!snapshot.exists()) {
                            Map<String, Object> defaultData = new HashMap<>();
                            for (int id : checkboxIds) {
                                String checkboxName = getResources().getResourceEntryName(id);
                                String textViewName = checkboxName + "Initials";
                                defaultData.put(checkboxName, false);
                                defaultData.put(textViewName, "...");
                            }
                            defaultData.put("teamLeaderInitials", "...");
                            closingChecksRef.set(defaultData);
                        }

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
                                    closingChecksRef.set(updates, SetOptions.merge());
                                });
                            }
                        }

                        // Load TL initials if present
                        String teamLeaderInitials = snapshot.getString("teamLeaderInitials");
                        if (teamLeaderInitials != null) {
                            teamLeaderInitalsTV.setText(teamLeaderInitials);
                        }
                    });
                });

        // Team Leader Sign Off
        Button teamLeaderSignOff = view.findViewById(R.id.buttonTeamLeaderSignOff);
        teamLeaderSignOff.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Team Leader Passcode");

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                teamLeaderPasscode = input.getText().toString();
                db.collection("users").whereEqualTo("loginCode", teamLeaderPasscode).get()
                        .addOnSuccessListener((QuerySnapshot queryDocumentSnapshots) -> {
                            boolean found = false;
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if ("teamLeader".equals(doc.getString("role"))) {
                                    found = true;
                                    String initials = doc.getString("initials");
                                    teamLeaderInitalsTV.setText(initials);

                                    // Save to Firestore
                                    Map<String, Object> update = new HashMap<>();
                                    update.put("teamLeaderInitials", initials);
                                    closingChecksRef.set(update, SetOptions.merge());
                                    break;
                                }
                            }
                            if (!found) {
                                Toast.makeText(getActivity(), "Invalid Team Leader Passcode", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        return view;
    }

    private String getTodayDate() {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
    }
}