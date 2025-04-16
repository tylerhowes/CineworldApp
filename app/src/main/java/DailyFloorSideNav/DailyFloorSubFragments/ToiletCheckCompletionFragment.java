package DailyFloorSideNav.DailyFloorSubFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cineworldapp.R;
import com.example.cineworldapp.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class ToiletCheckCompletionFragment extends Fragment {

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private Button completeCheckButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_toilet_check_completion, container, false);

        viewPager = view.findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.add(ToiletCheckCompletionSubFragment.newInstance("Mens"), "Mens");
        viewPagerAdapter.add(ToiletCheckCompletionSubFragment.newInstance("Womens"), "Womens");
        viewPagerAdapter.add(ToiletCheckCompletionSubFragment.newInstance("Disabled"), "Disabled");

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        completeCheckButton = view.findViewById(R.id.completeCheckButton);
        completeCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ToiletCheckCompletionFragment", "Complete Check Button Clicked");
                String mensData = getSubFragEditTextData(0);
                String womensData = getSubFragEditTextData(1);
                String disabledData = getSubFragEditTextData(2);

                if(!mensData.isEmpty() && !womensData.isEmpty() && !disabledData.isEmpty()){
                    Bundle result = new Bundle();
                    result.putString("mensData", mensData);
                    result.putString("womensData", womensData);
                    result.putString("disabledData", disabledData);
                    getParentFragmentManager().setFragmentResult("CheckCompleteKey", result);
                    getActivity().getSupportFragmentManager().popBackStack();
                    // Send data back to previous activity
                }
                else{
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private String getSubFragEditTextData(int position){
        return ((ToiletCheckCompletionSubFragment) viewPagerAdapter.getRegisteredFragment(position)).getEditTextData();
    }

}