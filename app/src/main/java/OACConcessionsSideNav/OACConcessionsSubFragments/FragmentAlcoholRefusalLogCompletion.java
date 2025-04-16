package OACConcessionsSideNav.OACConcessionsSubFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cineworldapp.R;

//THIS IS WHERE I LEFT OFF
public class FragmentAlcoholRefusalLogCompletion extends Fragment {

    EditText nameDescriptionET;
    EditText productET;
    EditText timeET;
    EditText reasonET;

    Button completeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alcohol_refusal_log_completion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameDescriptionET = view.findViewById(R.id.customerNameDescription);
        productET = view.findViewById(R.id.product);
        reasonET = view.findViewById(R.id.reasonsForRefusal);

        completeButton = view.findViewById(R.id.completeButton);
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameDescription = nameDescriptionET.getText().toString();
                String product = productET.getText().toString();
                String reason = reasonET.getText().toString();
                if(nameDescription.isEmpty() && product.isEmpty() && reason.isEmpty()){
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    Bundle result = new Bundle();
                    result.putString("customerNameDescription", nameDescription);
                    result.putString("product", product);
                    result.putString("reason", reason);
                    Log.d("AlcoholRefusalCompletion", "Refusal Log value sent value");
                    getParentFragmentManager().setFragmentResult("AlcoholRefusalCompleteKey", result);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }
}