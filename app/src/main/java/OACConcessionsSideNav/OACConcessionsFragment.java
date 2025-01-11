package OACConcessionsSideNav;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cineworldapp.R;
import com.example.cineworldapp.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import DailyFloorSideNav.DailyFloorSubFragments.AntiPiracyFragment;
import DailyFloorSideNav.DailyFloorSubFragments.CinemaSafetyAndSecurityFragment;
import DailyFloorSideNav.DailyFloorSubFragments.FloorEquipmentSanitisationFragment;
import DailyFloorSideNav.DailyFloorSubFragments.ToiletCheckFragment;
import OACConcessionsSideNav.OACConcessionsSubFragments.OACConcessionsClosingChecks;
import OACConcessionsSideNav.OACConcessionsSubFragments.OACConcessionsDailyDuties;
import OACConcessionsSideNav.OACConcessionsSubFragments.OACConcessionsOpeningChecks;
import OACConcessionsSideNav.OACConcessionsSubFragments.OACConcessionsRefusalLog;


public class OACConcessionsFragment extends Fragment {

    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_oac_concessions, container, false);

        viewPager = view.findViewById(R.id.pager);

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.add(new OACConcessionsOpeningChecks(), "Opening Checks");
        viewPagerAdapter.add(new OACConcessionsDailyDuties(), "Daily Duties");
        viewPagerAdapter.add(new OACConcessionsClosingChecks(), "Closing Checks");
        viewPagerAdapter.add(new OACConcessionsRefusalLog(), "Refusal Logs");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        // Inflate the layout for this fragment
        return view;
    }
}