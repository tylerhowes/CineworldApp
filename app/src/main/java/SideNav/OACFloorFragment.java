package SideNav;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cineworldapp.ClosingChecksFragment;
import com.example.cineworldapp.CustomerAssistanceFragment;
import com.example.cineworldapp.FloorDailyDutiesFragment;
import com.example.cineworldapp.FloorOpeningChecksFragment;
import com.example.cineworldapp.R;
import com.example.cineworldapp.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class OACFloorFragment extends Fragment {

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_oac_floor, container, false);
        viewPager = view.findViewById(R.id.pager);

        viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        viewPagerAdapter.add(new FloorDailyDutiesFragment(), "Daily Floor");
        viewPagerAdapter.add(new FloorOpeningChecksFragment(), "Opening Checks");
        viewPagerAdapter.add(new ClosingChecksFragment(), "Closing Checks");
        viewPagerAdapter.add(new CustomerAssistanceFragment(), "Customer Assistance Log");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
}