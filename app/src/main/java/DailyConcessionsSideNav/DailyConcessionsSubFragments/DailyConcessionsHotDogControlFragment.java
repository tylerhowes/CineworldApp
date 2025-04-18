package DailyConcessionsSideNav.DailyConcessionsSubFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cineworldapp.R;
import com.example.cineworldapp.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;


public class DailyConcessionsHotDogControlFragment extends Fragment {

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_concessions_hot_dog_control, container, false);

        viewPager = view.findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.add(new HotDogCookingFragment(), "Cooking");
        viewPagerAdapter.add(new HotDogStorageFragment(), "Storage");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);


        // Inflate the layout for this fragment
        return view;
    }
}