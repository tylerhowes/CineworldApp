package OACFloorSideNav.OACFloorSubFragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.cineworldapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class FloorClosingChecksFragment extends Fragment {



    FirebaseAuth auth;
    FirebaseFirestore db;
    String userInitials;

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
            R.id.checkboxLostProperty
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

        View view = inflater.inflate(R.layout.fragment_oac_floor_closing_checks, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String UID = auth.getCurrentUser().getUid();

        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials =  documentSnapshot.getString("initials");
                });

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
        // Inflate the layout for this fragment
        return view;
    }
}