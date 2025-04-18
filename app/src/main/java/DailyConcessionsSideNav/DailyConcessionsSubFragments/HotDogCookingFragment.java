package DailyConcessionsSideNav.DailyConcessionsSubFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.cineworldapp.ConcessionsHotdogBatchAdapter;
import com.example.cineworldapp.ConcessionsTillSanitisationAdapter;
import com.example.cineworldapp.HotdogBatchModel;
import com.example.cineworldapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class HotDogCookingFragment extends Fragment {

    Button newBatchButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    String userInitials;

    private ArrayList<HotdogBatchModel> hotdogBatchModelList;
    private ConcessionsHotdogBatchAdapter concessionsHotdogBatchAdapter;


    public HotDogCookingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hot_dog_cooking, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        hotdogBatchModelList = new ArrayList<>();

        String UID = auth.getCurrentUser().getUid();
        db.collection("users").document(UID).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userInitials = documentSnapshot.getString("initials");

                    Log.d("HotDogCookingFragment", "User Initials loaded: " + userInitials);
                    concessionsHotdogBatchAdapter = new ConcessionsHotdogBatchAdapter(getContext(), hotdogBatchModelList, userInitials);

                    RecyclerView recyclerView = view.findViewById(R.id.hotdogBatchRecyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(concessionsHotdogBatchAdapter);

                    LoadData();
                });


        Context context = getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newBatchPopupView = inflater.inflate(R.layout.new_hotdog_batch_popup_layout, null);


        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;


        newBatchButton = view.findViewById(R.id.NewBatchButton);
        newBatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click here
                final PopupWindow newBatchPopupWindow = new PopupWindow(newBatchPopupView, width, height, focusable);
                newBatchPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                newBatchPopupView.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newBatchPopupWindow.dismiss();
                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        String currentTime = timeFormat.format(date);
                        String currentDate = dateFormat.format(date);

                        Log.d("HotDogCookingFragment", "New batch put on at: " + currentTime.toString());
                        //screenCardModel.setChecksCompleteTimes(checksCompleteTimes);

                        EditText regularET = newBatchPopupView.findViewById(R.id.regularQuantityET);
                        EditText largeET = newBatchPopupView.findViewById(R.id.largeQuantityET);
                        EditText veggieET = newBatchPopupView.findViewById(R.id.veggieQuantityET);

                        HotdogBatchModel batchModel = new HotdogBatchModel();

                        CollectionReference collectionRef = db.collection("Documents")
                                .document(currentDate)
                                .collection("Daily Concessions")
                                .document("Hot Dog Control")
                                .collection("Cooking");


                        collectionRef.count().get(AggregateSource.SERVER).addOnSuccessListener(count -> {
                            int batchNumber = (int) count.getCount() + 1;
                            batchModel.setBatchNumber(batchNumber + "");
                            Log.d("HotDogCookingFragment", "Batch Number: " + batchModel.getBatchNumber());

                            // Set other fields
                            batchModel.setTimeStarted(currentTime);
                            batchModel.setRegularQuantity(regularET.getText().toString());
                            batchModel.setLargeQuantity(largeET.getText().toString());
                            batchModel.setVeggieQuantity(veggieET.getText().toString());
                            batchModel.setStatus("Cooking");

                            Log.d("HotDogCookingFragment", "User Initials when starting batch: " + userInitials);
                            batchModel.setStaffInitialsStarted(userInitials);

                            db.collection("Documents")
                                    .document(currentDate)
                                    .collection("Daily Concessions")
                                    .document("Hot Dog Control")
                                    .collection("Cooking")
                                    .document("Batch" + batchNumber)
                                    .set(batchModel);

                            hotdogBatchModelList.add(batchModel);
                            concessionsHotdogBatchAdapter.notifyDataSetChanged();
                        });
                    }
                });
                newBatchPopupView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newBatchPopupWindow.dismiss();
                    }
                });
            }
        });


    }

    public void LoadData(){

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(date);

        db.collection("Documents")
                .document(currentDate)
                .collection("Daily Concessions")
                .document("Hot Dog Control")
                .collection("Cooking")
                .whereEqualTo("status", "Cooking")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for(QueryDocumentSnapshot snapshot : task.getResult()){
                            HotdogBatchModel batchModel = new HotdogBatchModel();
                            batchModel = snapshot.toObject(HotdogBatchModel.class);
                            hotdogBatchModelList.add(batchModel);
                        }
                        concessionsHotdogBatchAdapter.notifyDataSetChanged();
                    }
                });
    }
}