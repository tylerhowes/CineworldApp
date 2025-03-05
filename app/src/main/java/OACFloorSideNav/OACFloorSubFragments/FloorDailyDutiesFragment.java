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

import com.example.cineworldapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    Map<Integer, Boolean> checkboxStates;
    Map<Integer, String> initialsStates;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", getActivity().MODE_PRIVATE);

        checkboxStates = new HashMap<>();
        initialsStates = new HashMap<>();
        for(int checkboxID : checkboxIds) {

            String checkboxName = getResources().getResourceEntryName(checkboxID);
            String textViewName = checkboxName + "Initials";

            int textViewID = getResources().getIdentifier(textViewName, "id", getActivity().getPackageName());

            boolean isChecked = sharedPreferences.getBoolean(checkboxName, false);
            checkboxStates.put(checkboxID, isChecked);

            initialsStates.put(textViewID, sharedPreferences.getString(textViewName, "..."));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UID = auth.getCurrentUser().getUid();

        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials =  documentSnapshot.getString("initials");
                });

        View view = inflater.inflate(R.layout.fragment_oac_floor_daily_duties, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        for (int id : checkboxIds) {
            CheckBox checkBox = view.findViewById(id);
            if (checkBox != null) {

                String checkboxName = getResources().getResourceEntryName(id);
                String textViewName = checkboxName + "Initials";

                int textViewID = getResources().getIdentifier(textViewName, "id", getActivity().getPackageName());
                TextView textView = view.findViewById(textViewID);

                boolean isChecked = checkboxStates.getOrDefault(id,false);
                String initials = initialsStates.getOrDefault(textViewID, "...");
                textView.setText(initials);
                checkBox.setChecked(isChecked);

                checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                    if(b) {
                        textView.setText(userInitials);
                    } else {
                        textView.setText("...");
                    }
                    editor.putBoolean(checkboxName, b);
                    editor.putString(textViewName, textView.getText().toString());
                    editor.apply();
                });
            }
        }

        teamLeaderInitalsTV = view.findViewById(R.id.teamLeaderInitials);
        Button teamLeaderSignOff = view.findViewById(R.id.buttonTeamLeaderSignOff);
        teamLeaderSignOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Team Leader Passcode");


                // Set up the input
                final EditText input = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        teamLeaderPasscode = input.getText().toString();

                        db.collection("users").whereEqualTo("loginCode", teamLeaderPasscode).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                    if(doc.get("role").toString().equals("teamLeader")) {
                                        String docID = doc.getId();
                                        teamLeaderInitalsTV.setText(doc.get("initials").toString());
                                    }else {
                                        Toast.makeText(getActivity(), "Invalid Team Leader Passcode", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}