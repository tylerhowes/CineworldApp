package DailyConcessionsSideNav.DailyConcessionsSubFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.cineworldapp.FreezerCheckModel;
import com.example.cineworldapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DailyConcessionsFreezerChecksFragment extends Fragment {

    ArrayList<String> freezerCheckList = new ArrayList<>();
    ArrayList<String> checkTimes = new ArrayList<>();

    String userInitials = "";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkTimes.add("Open Temp Check");
        checkTimes.add("Visual Check");
        checkTimes.add("PM Temp Check");
        checkTimes.add("Close Visual Check");

        freezerCheckList.add("Chest Freezer 1");
        freezerCheckList.add("Chest Freezer 2");
        freezerCheckList.add("Chest Freezer 3");
        freezerCheckList.add("Upright Freezer 1");
        freezerCheckList.add("Upright Freezer 2");
        freezerCheckList.add("Upright Fridge 1");
        freezerCheckList.add("Upright Fridge 2");
        freezerCheckList.add("Baskins Counter Fridge");
        freezerCheckList.add("Left Dipping Cabinet");
        freezerCheckList.add("Right Dipping Cabinet");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_concessions_freezer_checks, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String UID = auth.getCurrentUser().getUid();
        db.collection("users").document(UID).get().addOnSuccessListener(documentSnapshot -> {
            userInitials = documentSnapshot.getString("initials");
            Log.d("HotDogCookingFragment", "User Initials loaded: " + userInitials);
        });

        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        CardView openFreezerCheckCard = view.findViewById(R.id.openFreezerCheckCard);
        CardView visualFreezerCheckCard = view.findViewById(R.id.visualFreezerCheckCard);
        CardView pmVisualFreezerCheckCard = view.findViewById(R.id.pmVisualFreezerCheckCard);
        CardView closeFreezerCheckCard = view.findViewById(R.id.closeFreezerCheckCard);

        checkFreezerCheckStatus(currentDate, openFreezerCheckCard, visualFreezerCheckCard, pmVisualFreezerCheckCard, closeFreezerCheckCard);

        Context context = getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View freezerCheckPopupView = inflater.inflate(R.layout.layout_freezer_check_popup, null);

        int width = (int) (300 * getResources().getDisplayMetrics().density + 0.5f);
        int height = (int) (600 * getResources().getDisplayMetrics().density + 0.5f);
        boolean focusable = true;

        openFreezerCheckCard.findViewById(R.id.openCheckButton).setOnClickListener(v -> {
            final PopupWindow freezerCheckPopupWindow = new PopupWindow(freezerCheckPopupView, width, height, focusable);
            freezerCheckPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            freezerCheckPopupView.findViewById(R.id.cancelButton).setOnClickListener(view1 -> freezerCheckPopupWindow.dismiss());

            freezerCheckPopupView.findViewById(R.id.confirmButton).setOnClickListener(view1 -> {
                Map<String, String> freezerTemps = getFreezerTempsFromUI(freezerCheckPopupView);

                for (Map.Entry<String, String> entry : freezerTemps.entrySet()) {
                    String freezerName = entry.getKey();
                    String openTemp = entry.getValue();

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("freezerName", freezerName);
                    updateMap.put("freezerOpenTemp", openTemp);
                    updateMap.put("freezerOpenStaffInitials", userInitials);

                    db.collection("Documents")
                            .document(currentDate)
                            .collection("Daily Concessions")
                            .document("Freezer Checks")
                            .collection("logs")
                            .document(freezerName)
                            .set(updateMap, com.google.firebase.firestore.SetOptions.merge());
                }
                openFreezerCheckCard.findViewById(R.id.openCheckButton).setBackgroundColor(getResources().getColor(R.color.green));
                freezerCheckPopupWindow.dismiss();
            });
        });

        closeFreezerCheckCard.findViewById(R.id.closeCheckButton).setOnClickListener(v -> {
            final PopupWindow freezerCheckPopupWindow = new PopupWindow(freezerCheckPopupView, width, height, focusable);
            freezerCheckPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            freezerCheckPopupView.findViewById(R.id.cancelButton).setOnClickListener(view1 -> freezerCheckPopupWindow.dismiss());

            freezerCheckPopupView.findViewById(R.id.confirmButton).setOnClickListener(view1 -> {
                Map<String, String> freezerTemps = getFreezerTempsFromUI(freezerCheckPopupView);

                for (Map.Entry<String, String> entry : freezerTemps.entrySet()) {
                    String freezerName = entry.getKey();
                    String closeTemp = entry.getValue();

                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("freezerName", freezerName);
                    updateMap.put("freezerCloseTemp", closeTemp);
                    updateMap.put("freezerCloseStaffInitials", userInitials);

                    db.collection("Documents")
                            .document(currentDate)
                            .collection("Daily Concessions")
                            .document("Freezer Checks")
                            .collection("logs")
                            .document(freezerName)
                            .set(updateMap, com.google.firebase.firestore.SetOptions.merge());
                }

                closeFreezerCheckCard.findViewById(R.id.closeCheckButton).setBackgroundColor(getResources().getColor(R.color.green));
                freezerCheckPopupWindow.dismiss();
            });
        });

        visualFreezerCheckCard.findViewById(R.id.visualCheckButton).setOnClickListener(v -> {
            for (String freezerName : freezerCheckList) {
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("freezerName", freezerName);
                updateMap.put("freezerVisualCheck", userInitials);

                db.collection("Documents")
                        .document(currentDate)
                        .collection("Daily Concessions")
                        .document("Freezer Checks")
                        .collection("logs")
                        .document(freezerName)
                        .set(updateMap, com.google.firebase.firestore.SetOptions.merge());
            }

            visualFreezerCheckCard.findViewById(R.id.visualCheckButton).setBackgroundColor(getResources().getColor(R.color.green));
        });

        pmVisualFreezerCheckCard.findViewById(R.id.pmVisualCheckButton).setOnClickListener(v -> {
            for (String freezerName : freezerCheckList) {
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("freezerName", freezerName);
                updateMap.put("freezerPMVisualCheck", userInitials);

                db.collection("Documents")
                        .document(currentDate)
                        .collection("Daily Concessions")
                        .document("Freezer Checks")
                        .collection("logs")
                        .document(freezerName)
                        .set(updateMap, com.google.firebase.firestore.SetOptions.merge());
            }

            pmVisualFreezerCheckCard.findViewById(R.id.pmVisualCheckButton).setBackgroundColor(getResources().getColor(R.color.green));
        });

        // Default data fetch
        for (String freezer : freezerCheckList) {
            for (String time : checkTimes) {

                db.collection("Documents")
                        .document(currentDate)
                        .collection("Daily Concessions")
                        .document("Freezer Checks")
                        .collection("logs")
                        .document(freezer)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                FreezerCheckModel model = new FreezerCheckModel(
                                        documentSnapshot.getString("freezerName"),
                                        documentSnapshot.getString("targetTemp"),
                                        documentSnapshot.getString("freezerOpenTemp"),
                                        documentSnapshot.getString("freezerCloseTemp"),
                                        documentSnapshot.getString("freezerVisualCheck"),
                                        documentSnapshot.getString("freezerPMVisualCheck"),
                                        documentSnapshot.getString("freezerOpenStaffInitials") != null ? documentSnapshot.getString("freezerOpenStaffInitials") : "...",
                                        documentSnapshot.getString("freezerCloseStaffInitials") != null ? documentSnapshot.getString("freezerCloseStaffInitials") : "..."
                                );
                            } else {
                                Map<String, Object> defaultData = new HashMap<>();
                                defaultData.put("freezerName", freezer);

                                if (freezer.contains("Freezer")) {
                                    defaultData.put("targetTemp", "-20");
                                } else if (freezer.contains("Fridge")) {
                                    defaultData.put("targetTemp", "3");
                                } else if (freezer.contains("Cabinet")) {
                                    defaultData.put("targetTemp", "-15");
                                }

                                defaultData.put("freezerOpenTemp", "...");
                                defaultData.put("freezerCloseTemp", "...");
                                defaultData.put("freezerVisualCheck", "...");
                                defaultData.put("freezerPMVisualCheck", "...");
                                defaultData.put("freezerOpenStaffInitials", "...");
                                defaultData.put("freezerCloseStaffInitials", "...");

                                db.collection("Documents")
                                        .document(currentDate)
                                        .collection("Daily Concessions")
                                        .document("Freezer Checks")
                                        .collection("logs")
                                        .document(freezer)
                                        .set(defaultData);
                            }
                        });
            }
        }
    }


    private void checkFreezerCheckStatus(String currentDate, CardView openFreezerCheckCard,
                                         CardView visualFreezerCheckCard, CardView pmVisualFreezerCheckCard,
                                         CardView closeFreezerCheckCard) {
        // Loop over the freezer list
        for (String freezerName : freezerCheckList) {
            db.collection("Documents")
                    .document(currentDate)
                    .collection("Daily Concessions")
                    .document("Freezer Checks")
                    .collection("logs")
                    .document(freezerName)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Check if the "freezerOpenStaffInitials" field is not null (for open check)
                            String openStaffInitials = documentSnapshot.getString("freezerOpenStaffInitials");
                            if (openStaffInitials != null && !openStaffInitials.isEmpty()) {
                                openFreezerCheckCard.findViewById(R.id.openCheckButton)
                                        .setBackgroundColor(getResources().getColor(R.color.green));
                            }

                            // Check if the "freezerVisualCheck" field is not null (for visual check)
                            String visualCheck = documentSnapshot.getString("freezerVisualCheck");
                            if (visualCheck != null && !visualCheck.isEmpty()) {
                                visualFreezerCheckCard.findViewById(R.id.visualCheckButton)
                                        .setBackgroundColor(getResources().getColor(R.color.green));
                            }

                            // Check if the "freezerPMVisualCheck" field is not null (for PM visual check)
                            String pmVisualCheck = documentSnapshot.getString("freezerPMVisualCheck");
                            if (pmVisualCheck != null && !pmVisualCheck.isEmpty()) {
                                pmVisualFreezerCheckCard.findViewById(R.id.pmVisualCheckButton)
                                        .setBackgroundColor(getResources().getColor(R.color.green));
                            }

                            // Check if the "freezerCloseStaffInitials" field is not null (for close check)
                            String closeStaffInitials = documentSnapshot.getString("freezerCloseStaffInitials");
                            if (closeStaffInitials != null && !closeStaffInitials.isEmpty()) {
                                closeFreezerCheckCard.findViewById(R.id.closeCheckButton)
                                        .setBackgroundColor(getResources().getColor(R.color.green));
                            }
                        }
                    });
        }
    }


    private Map<String, String> getFreezerTempsFromUI(View popupView) {
        Map<String, String> temps = new HashMap<>();
        temps.put("Chest Freezer 1", ((EditText) popupView.findViewById(R.id.chestFreezer1Temp)).getText().toString());
        temps.put("Chest Freezer 2", ((EditText) popupView.findViewById(R.id.chestFreezer2Temp)).getText().toString());
        temps.put("Chest Freezer 3", ((EditText) popupView.findViewById(R.id.chestFreezer3Temp)).getText().toString());
        temps.put("Upright Freezer 1", ((EditText) popupView.findViewById(R.id.uprightFreezer1Temp)).getText().toString());
        temps.put("Upright Freezer 2", ((EditText) popupView.findViewById(R.id.uprightFreezer2Temp)).getText().toString());
        temps.put("Upright Fridge 1", ((EditText) popupView.findViewById(R.id.uprightFridge1Temp)).getText().toString());
        temps.put("Upright Fridge 2", ((EditText) popupView.findViewById(R.id.uprightFridge2Temp)).getText().toString());
        temps.put("Baskins Counter Fridge", ((EditText) popupView.findViewById(R.id.baskinsCounterTemp)).getText().toString());
        temps.put("Left Dipping Cabinet", ((EditText) popupView.findViewById(R.id.dippingCabinetLeftTemp)).getText().toString());
        temps.put("Right Dipping Cabinet", ((EditText) popupView.findViewById(R.id.dippingCabinetRightTemp)).getText().toString());
        return temps;
    }
}
