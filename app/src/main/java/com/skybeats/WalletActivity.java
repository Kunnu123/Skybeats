package com.skybeats;

import android.os.Bundle;
import android.view.View;

import com.skybeats.databinding.ActivityWalletBinding;
import com.skybeats.ui.DiamondFragment;
import com.skybeats.ui.SearchFragment;
import com.skybeats.ui.home.HomeFragment;

public class WalletActivity extends BaseActivity {
    ActivityWalletBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TabAdapter viewPagerAdapter = new TabAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new DiamondFragment(), "Diamonds");
        viewPagerAdapter.addFragment(new SearchFragment(), "Beans");
//        viewPagerAdapter.addFragment(new SearchFragment(), "Moments");
//        viewPagerAdapter.addFragment(new SearchFragment(), "Live");
//        viewPagerAdapter.addFragment(new SearchFragment(), "Multi-Guest");
        binding.vpWallet.setAdapter(viewPagerAdapter);
        binding.vpWallet.setCurrentItem(0);
        binding.tabWallet.setupWithViewPager(binding.vpWallet);

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}