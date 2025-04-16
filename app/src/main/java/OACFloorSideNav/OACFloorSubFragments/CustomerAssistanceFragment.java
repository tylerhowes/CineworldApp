package OACFloorSideNav.OACFloorSubFragments;

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

import com.example.cineworldapp.CustomerAssistanceCardAdapter;
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

public class CustomerAssistanceFragment extends Fragment {

    Button addLogButton;

    ArrayList<CustomerAssistanceDataModel> customerAssistanceDataModelArrayList;
    CustomerAssistanceCardAdapter adapter;
    RecyclerView customerAssistanceRV;

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

        customerAssistanceDataModelArrayList = new ArrayList<>();
        customerAssistanceRV = view.findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        adapter = new CustomerAssistanceCardAdapter(getContext(), customerAssistanceDataModelArrayList);
        customerAssistanceRV.setLayoutManager(linearLayoutManager);
        customerAssistanceRV.setAdapter(adapter);

        addLogButton = view.findViewById(R.id.addLogButton);
        addLogButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragTransaction = fragmentManager.beginTransaction();
            fragTransaction.add(R.id.oacFloor, new CustomerAssistanceCompletionFragment());
            fragTransaction.addToBackStack(null);
            fragTransaction.commit();
        });

        loadCustomerAssistanceData();

        getActivity().getSupportFragmentManager().setFragmentResultListener("CustomerAssistanceCheckCompleteKey", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d("CustomerAssistance", "Customer Assistance received value");

                String customerName = result.getString("customerName");
                String screenNumber = result.getString("screenNumber");
                String seatNumber = result.getString("seatNumber");
                String startTime = result.getString("startTime");
                String finishTime = result.getString("finishTime");
                String assistanceRequired = result.getString("assistanceRequired");

                CustomerAssistanceDataModel model = new CustomerAssistanceDataModel(
                        customerName,
                        screenNumber,
                        seatNumber,
                        startTime,
                        finishTime,
                        assistanceRequired,
                        currentStaffInitials
                );

                addCustomerAssistanceToFirestore(model);
            }
        });
    }

    private void loadCustomerAssistanceData() {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        CollectionReference logsRef = db.collection("Documents")
                .document(currentDate)
                .collection("OAC Floor")
                .document("Customer Assistance Logs")
                .collection("Logs");

        logsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            customerAssistanceDataModelArrayList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                CustomerAssistanceDataModel model = new CustomerAssistanceDataModel(
                        doc.getString("customerName"),
                        doc.getString("screen"),
                        doc.getString("seatNumber"),
                        doc.getString("startTime"),
                        doc.getString("finishTime"),
                        doc.getString("assistanceRequired"),
                        doc.getString("staffInitials")
                );
                customerAssistanceDataModelArrayList.add(model);
            }
            adapter.notifyDataSetChanged();
            Log.d("CustomerAssistance", "Data loaded from Firestore.");
        }).addOnFailureListener(e -> {
            Log.e("CustomerAssistance", "Error loading data from Firestore", e);
        });
    }

    private void addCustomerAssistanceToFirestore(CustomerAssistanceDataModel model) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("customerName", model.getCustomerName());
        dataMap.put("screen", model.getScreen());
        dataMap.put("seatNumber", model.getSeatNumber());
        dataMap.put("startTime", model.getStartTime());
        dataMap.put("finishTime", model.getFinishTime());
        dataMap.put("assistanceRequired", model.getAssistanceRequired());
        dataMap.put("staffInitials", model.getStaffInitials());

        CollectionReference logsRef = db.collection("Documents")
                .document(currentDate)
                .collection("OAC Floor")
                .document("Customer Assistance Logs")
                .collection("Logs");

        logsRef.add(dataMap)
                .addOnSuccessListener(documentReference -> {
                    customerAssistanceDataModelArrayList.add(model);
                    adapter.notifyItemInserted(customerAssistanceDataModelArrayList.size() - 1);
                    Log.d("CustomerAssistance", "Data added to Firestore.");
                })
                .addOnFailureListener(e -> {
                    Log.e("CustomerAssistance", "Error writing document", e);
                });
    }
}
