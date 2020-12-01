package com.skybeats.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.skybeats.AddDiamondActivity;
import com.skybeats.DiamondActivity;
import com.skybeats.R;
import com.skybeats.utils.MyPref;

public class DiamondFragment extends Fragment {

    AppCompatTextView txtUserPoints,btnAdd;
    private MyPref myPref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_diamond, container, false);
        txtUserPoints = root.findViewById(R.id.txtUserPoints);
        btnAdd = root.findViewById(R.id.btnAdd);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (myPref == null) {
            myPref = new MyPref(getActivity());
        }
        txtUserPoints.setText(myPref.getUserData().getUser_diamonds());
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(getActivity(), AddDiamondActivity.class);
                startActivity(addIntent);
            }
        });
    }
}