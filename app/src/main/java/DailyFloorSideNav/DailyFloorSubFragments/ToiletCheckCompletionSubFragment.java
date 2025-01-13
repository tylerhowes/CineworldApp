package DailyFloorSideNav.DailyFloorSubFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.cineworldapp.R;


public class ToiletCheckCompletionSubFragment extends Fragment {

    private static final String checkType = "checkType";
    private EditText editText;

    public static ToiletCheckCompletionSubFragment newInstance(String type) {
        ToiletCheckCompletionSubFragment fragment = new ToiletCheckCompletionSubFragment();
        Bundle args = new Bundle();
        args.putString(checkType, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Retrieve the type argument
        String type = getArguments() != null ? getArguments().getString(checkType) : "Default";

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_toilet_check_completion_sub, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editText = getView().findViewById(R.id.editText);
    }

    public String getEditTextData(){
        return editText.getText().toString();
    }
}