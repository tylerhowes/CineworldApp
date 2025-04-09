package OACConcessionsSideNav.OACConcessionsSubFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cineworldapp.AlcoholRefusalLogCardAdapter;
import com.example.cineworldapp.AlcoholRefusalLogDataModel;
import com.example.cineworldapp.CustomerAssistanceDataModel;
import com.example.cineworldapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OACConcessionsRefusalLog extends Fragment {

    Button addLogButton;

    ArrayList<AlcoholRefusalLogDataModel> alcoholRefusalLogDataModelArrayList;
    AlcoholRefusalLogCardAdapter adapter;
    RecyclerView alcoholRefusalLogRV;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentStaffInitials;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_oac_floor_customer_assistance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String staffID = auth.getCurrentUser().getUid();
        db.collection("users").document(staffID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot = task.getResult();
                currentStaffInitials = snapshot.getString("initials");
            }
        });

        alcoholRefusalLogDataModelArrayList = new ArrayList<>();

        alcoholRefusalLogRV = view.findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        adapter = new AlcoholRefusalLogCardAdapter(getContext(), alcoholRefusalLogDataModelArrayList);
        alcoholRefusalLogRV.setLayoutManager(linearLayoutManager);
        alcoholRefusalLogRV.setAdapter(adapter);

        addLogButton = view.findViewById(R.id.addLogButton);
        addLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                final FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
                fragTransaction.add(R.id.oacConcessions, new FragmentAlcoholRefusalLogCompletion());
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            }
        });

        LoadAlcoholRefusalLogsFirebase();

        getActivity().getSupportFragmentManager().setFragmentResultListener("AlcoholRefusalCompleteKey", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                Log.d("OACConcessionsRefusalLog", "Alcohol Refusal received result");

                String customerNameDescription = result.getString("customerNameDescription");
                String product = result.getString("product");
                String reason = result.getString("reason");

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String time = timeFormat.format(new Date());

                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                String date = format.format(new Date());

                // Create a new AlcoholRefusalLogDataModel object
                AlcoholRefusalLogDataModel logDataModel = new AlcoholRefusalLogDataModel(
                        customerNameDescription,
                        product,
                        time,
                        date,
                        reason,
                        currentStaffInitials);

                // Add the new log to the local list
                alcoholRefusalLogDataModelArrayList.add(logDataModel);

                // Notify the adapter to refresh the RecyclerView
                adapter.notifyItemInserted(alcoholRefusalLogDataModelArrayList.size() - 1);

                // Save the new log to Firestore
                saveLogToFirestore(logDataModel);
            }
        });
    }

    private void LoadAlcoholRefusalLogsFirebase() {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        CollectionReference logsRef = db.collection("Documents")
                .document(currentDate)
                .collection("OAC Concessions")
                .document("Alcohol Refusal Logs")
                .collection("Logs");

        logsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            alcoholRefusalLogDataModelArrayList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                AlcoholRefusalLogDataModel model = new AlcoholRefusalLogDataModel(
                        doc.getString("nameOrDescription"),
                        doc.getString("product"),
                        doc.getString("time"),
                        doc.getString("date"),
                        doc.getString("reason"),
                        doc.getString("staffInitials")

                );
                alcoholRefusalLogDataModelArrayList.add(model);
            }
            adapter.notifyDataSetChanged();
            Log.d("CustomerAssistance", "Data loaded from Firestore.");
        }).addOnFailureListener(e -> {
            Log.e("CustomerAssistance", "Error loading data from Firestore", e);
        });
    }

    private void saveLogToFirestore(AlcoholRefusalLogDataModel model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        // Prepare the data to be saved as a map
        // Create a map of log data to be stored in Firestore
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("nameOrDescription", model.getNameOrDescription());
        dataMap.put("product", model.getProduct());
        dataMap.put("time", model.getTime());
        dataMap.put("date", model.getDate());
        dataMap.put("reason", model.getReason());
        dataMap.put("staffInitials", model.getStaffInitials());

        // Firestore path to store the log
        db.collection("Documents")
                .document(currentDate)
                .collection("OAC Concessions")
                .document("Alcohol Refusal Logs")
                .collection("Logs")
                .add(dataMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("AlcoholRefusalLog", "Log added to Firestore.");
                })
                .addOnFailureListener(e -> {
                    Log.e("AlcoholRefusalLog", "Error writing log to Firestore", e);
                });
    }
}
