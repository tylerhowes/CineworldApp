package DailyFloorSideNav.DailyFloorSubFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.cineworldapp.R;
import com.example.cineworldapp.ScreenCardAdapter;
import com.example.cineworldapp.ScreenCardModel;
import com.example.cineworldapp.ScreenCheck;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class AntiPiracyFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_floor_anti_piracy, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.screenRecycleView);

        ArrayList<ScreenCheck> checks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            checks.add(new ScreenCheck());
        }

        ArrayList<ScreenCardModel> screenCardModelArrayList = new ArrayList<>();
        screenCardModelArrayList.add(new ScreenCardModel("1", "The Matrix", "10:00", "10:30", "12:30",checks));
        screenCardModelArrayList.add(new ScreenCardModel("2", "The Matrix 2", "10:00", "10:30", "12:30", checks));
        screenCardModelArrayList.add(new ScreenCardModel("3", "The Matrix 3", "10:00", "10:30", "12:30", checks));

        ScreenCardAdapter screenCardAdapter = new ScreenCardAdapter(getContext(), screenCardModelArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(screenCardAdapter);

    }
}