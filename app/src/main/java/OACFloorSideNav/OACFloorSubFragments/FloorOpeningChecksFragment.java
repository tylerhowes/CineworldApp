package OACFloorSideNav.OACFloorSubFragments;


import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import com.example.cineworldapp.MainActivity;
import com.example.cineworldapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FloorOpeningChecksFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseFirestore db;
    String userInitials;

    TextView teamLeaderInitalsTV;

    int[] checkboxIds = {
            R.id.checkboxOpenSafetyChecks,
            R.id.checkboxScanner,
            R.id.checkboxToiletCords,
            R.id.checkboxLightsIlluminated,
            R.id.checkboxCleanerEquipment,
            R.id.checkboxToiletsStocked,
            R.id.checkboxSluiceRoom,
            R.id.checkboxATMWorking,
            R.id.checkboxTVWorking,
            R.id.checkboxMusicPlaying,
            R.id.checkboxMaintainanceIssues,
            R.id.checkboxBinLiners
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oac_floor_opening_checks, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UID = auth.getCurrentUser().getUid();
        String todayDate = getTodayDate();

        DocumentReference openingChecksRef = db
                .collection("Documents")
                .document(todayDate)
                .collection("Daily Floor")
                .document("Opening Checks");

        teamLeaderInitalsTV = view.findViewById(R.id.teamLeaderInitials);

        // Fetch initials first so we can use it in checkbox rendering
        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials = documentSnapshot.getString("initials");

                    // Now fetch checkbox states and team leader initials
                    openingChecksRef.get().addOnSuccessListener(snapshot -> {
                        if (!snapshot.exists()) {
                            Map<String, Object> defaultData = new HashMap<>();
                            for (int id : checkboxIds) {
                                String checkboxName = getResources().getResourceEntryName(id);
                                String textViewName = checkboxName + "Initials";
                                defaultData.put(checkboxName, false);
                                defaultData.put(textViewName, "...");
                            }
                            defaultData.put("teamLeaderInitials", "...");
                            openingChecksRef.set(defaultData);
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
                                    openingChecksRef.set(updates, SetOptions.merge());
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

        Button teamLeaderSignOff = view.findViewById(R.id.buttonTeamLeaderSignOff);
        teamLeaderSignOff.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Team Leader Passcode");

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String teamLeaderPasscode = input.getText().toString();

                db.collection("users")
                        .whereEqualTo("loginCode", teamLeaderPasscode)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if ("teamLeader".equals(doc.getString("role"))) {
                                    String initials = doc.getString("initials");
                                    teamLeaderInitalsTV.setText(initials);

                                    Map<String, Object> update = new HashMap<>();
                                    update.put("teamLeaderInitials", initials);
                                    openingChecksRef.set(update, SetOptions.merge());
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

    private String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }


}