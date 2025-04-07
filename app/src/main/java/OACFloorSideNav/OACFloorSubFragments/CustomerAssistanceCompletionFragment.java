package OACFloorSideNav.OACFloorSubFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cineworldapp.R;


public class CustomerAssistanceCompletionFragment extends Fragment {
    EditText nameET;
    Spinner dropdown;
    EditText seatET;
    EditText startET;
    EditText finishET;
    EditText assistanceET;

    Button completeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_assistance_completion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameET = view.findViewById(R.id.customerName);
        seatET = view.findViewById(R.id.seat);
        startET = view.findViewById(R.id.startTime);
        finishET = view.findViewById(R.id.finishTime);
        assistanceET = view.findViewById(R.id.assistanceRequired);

        dropdown = view.findViewById(R.id.screenNumber);
        String[] screens = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, screens);
        dropdown.setAdapter(adapter);

        completeButton = view.findViewById(R.id.completeButton);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEmpty(nameET) && !isEmpty(seatET) && !isEmpty(startET) && !isEmpty(finishET) && !isEmpty(assistanceET)){
                    String customerName = nameET.getText().toString();
                    String screenNumber = dropdown.getSelectedItem().toString();
                    String seatNumber = seatET.getText().toString();
                    String startTime = startET.getText().toString();
                    String finishTime = finishET.getText().toString();
                    String assistanceRequired = assistanceET.getText().toString();

                    Bundle result = new Bundle();
                    result.putString("customerName", customerName);
                    result.putString("screenNumber", screenNumber);
                    result.putString("seatNumber", seatNumber);
                    result.putString("startTime", startTime);
                    result.putString("finishTime", finishTime);
                    result.putString("assistanceRequired", assistanceRequired);
                    Log.d("CustomerAssistance", "Customer Assistance sent value");
                    getParentFragmentManager().setFragmentResult("CustomerAssistanceCheckCompleteKey", result);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                else{
                    Toast.makeText(getContext(), "Please Fill All Fields", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }
}