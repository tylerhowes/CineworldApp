package DailyFloorSideNav;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import DailyFloorSideNav.DailyFloorSubFragments.AntiPiracyFragment;
import DailyFloorSideNav.DailyFloorSubFragments.CinemaSafetyAndSecurityFragment;
import DailyFloorSideNav.DailyFloorSubFragments.FloorEquipmentSanitisationFragment;
import DailyFloorSideNav.DailyFloorSubFragments.ToiletCheckFragment;
import OACFloorSideNav.OACFloorSubFragments.FloorClosingChecksFragment;
import OACFloorSideNav.OACFloorSubFragments.CustomerAssistanceFragment;
import OACFloorSideNav.OACFloorSubFragments.FloorDailyDutiesFragment;
import OACFloorSideNav.OACFloorSubFragments.FloorOpeningChecksFragment;
import com.example.cineworldapp.R;
import com.example.cineworldapp.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;


public class DailyFloorFragment extends Fragment {

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_floor, container, false);

        viewPager = view.findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.add(new AntiPiracyFragment(), "Anti-Piracy Checks");
        viewPagerAdapter.add(new ToiletCheckFragment(), "Toilet Checks");
        viewPagerAdapter.add(new FloorEquipmentSanitisationFragment(), "Equipment Sanitisation");
        viewPagerAdapter.add(new CinemaSafetyAndSecurityFragment(), "Safety and Security Checks");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}