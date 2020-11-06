package com.skybeats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.skybeats.databinding.ActivityDiamondBinding;

public class DiamondActivity extends BaseActivity {
    ActivityDiamondBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiamondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(DiamondActivity.this, AddDiamondActivity.class);
                startActivity(addIntent);
            }
        });

    }

}