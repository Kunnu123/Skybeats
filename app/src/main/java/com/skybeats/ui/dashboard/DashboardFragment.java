package com.skybeats.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.skybeats.MainActivity;
import com.skybeats.R;
import com.skybeats.TabAdapter;
import com.skybeats.ui.SearchFragment;
import com.skybeats.ui.home.HomeFragment;

public class DashboardFragment extends Fragment {

    TabLayout tabHome;
    ViewPager vpHome;
    MainActivity mainActivity;

    public DashboardFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        tabHome = root.findViewById(R.id.tabHome);
        vpHome = root.findViewById(R.id.vpHome);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabAdapter viewPagerAdapter = new TabAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new HomeFragment(mainActivity), "Following");
        viewPagerAdapter.addFragment(new SearchFragment(), "Nearby");
        viewPagerAdapter.addFragment(new SearchFragment(), "Moments");
        viewPagerAdapter.addFragment(new SearchFragment(), "Live");
        viewPagerAdapter.addFragment(new SearchFragment(), "Multi-Guest");
        vpHome.setAdapter(viewPagerAdapter);
        vpHome.setCurrentItem(0);
        tabHome.setupWithViewPager(vpHome);

    }
}