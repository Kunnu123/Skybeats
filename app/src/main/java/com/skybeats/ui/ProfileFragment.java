package com.skybeats.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.skybeats.DiamondActivity;
import com.skybeats.R;
import com.skybeats.SplashActivity;
import com.skybeats.WalletActivity;
import com.skybeats.retrofit.model.UserModel;
import com.skybeats.utils.ImageUtils;
import com.skybeats.utils.MyPref;

public class ProfileFragment extends Fragment {


    private MyPref myPref;
    TextView tv_name, tv_mobile, txtDiamonds, txtLogOut,tv_skyid;
    ImageView img_profile,ivEdit,ivCheck;
    LinearLayout llWallets, llSettings,llDisplay,llEdit,txtSettings,txtWallets;
    EditText edt_name;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        tv_name = root.findViewById(R.id.tv_name);
        tv_skyid = root.findViewById(R.id.tv_skyid);
        tv_mobile = root.findViewById(R.id.tv_mobile);
        img_profile = root.findViewById(R.id.img_profile);
        txtWallets = root.findViewById(R.id.txtWallets);
        llWallets = root.findViewById(R.id.llWallets);
        txtLogOut = root.findViewById(R.id.txtLogOut);
        txtDiamonds = root.findViewById(R.id.txtDiamonds);
        llSettings = root.findViewById(R.id.llSettings);
        txtSettings = root.findViewById(R.id.txtSettings);
        llDisplay = root.findViewById(R.id.llDisplay);
        llEdit = root.findViewById(R.id.llEdit);
        ivEdit = root.findViewById(R.id.ivEdit);
        ivCheck = root.findViewById(R.id.ivCheck);
        edt_name = root.findViewById(R.id.edt_name);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (myPref == null) {
            myPref = new MyPref(getActivity());
        }
        UserModel userModel=myPref.getUserData();
        if (userModel!=null){
            tv_name.setText(userModel.getUser_name());
            tv_mobile.setText(userModel.getMobile_no());
            tv_skyid.setText(userModel.getUser_id());
            ImageUtils.loadImage(getActivity(), userModel.getProfile_image(), R.drawable.avatar, img_profile);
        }



        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDisplay.setVisibility(View.GONE);
                llEdit.setVisibility(View.VISIBLE);
                edt_name.requestFocus();
                edt_name.setText(tv_name.getText());
            }
        });

        ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userModel.setUser_name(edt_name.getText().toString());
                myPref.setUserData(userModel);
                tv_name.setText(userModel.getUser_name());
                llEdit.setVisibility(View.GONE);
                llDisplay.setVisibility(View.VISIBLE);
                hideKeyboard(edt_name);
            }
        });

        txtWallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newIntent = new Intent(getActivity(), WalletActivity.class);
                startActivity(newIntent);

//                if (llWallets.getVisibility() == View.VISIBLE){
//                    llWallets.setVisibility(View.GONE);
////                    llWallets.animate()
////                            .alpha(0.0f)
////                            .setDuration(500)
////                            .setListener(new AnimatorListenerAdapter() {
////                                @Override
////                                public void onAnimationEnd(Animator animation) {
////                                    super.onAnimationEnd(animation);
////                                    llWallets.setVisibility(View.GONE);
////                                }
////                            });
//                }else {
//                    llWallets.setVisibility(View.VISIBLE);
//                    if(llSettings.getVisibility() == View.VISIBLE){
//                        llSettings.setVisibility(View.GONE);
//                    }
////                    llWallets.animate()
////                            .alpha(1.0f)
////                            .setDuration(500)
////                            .setListener(new AnimatorListenerAdapter() {
////                                @Override
////                                public void onAnimationEnd(Animator animation) {
////                                    super.onAnimationEnd(animation);
////                                    llWallets.setVisibility(View.VISIBLE);
////                                }
////                            });
//                }
            }
        });

        txtSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llSettings.getVisibility() == View.VISIBLE){
                    llSettings.setVisibility(View.GONE);
                }else {
                    if(llWallets.getVisibility() == View.VISIBLE){
                        llWallets.setVisibility(View.GONE);
                    }
                    llSettings.setVisibility(View.VISIBLE);
                }
            }
        });

        txtLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure want to Log Out ?");
                        alertDialogBuilder.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        myPref.clearPrefs();
                                        Intent i = new Intent(getActivity(), SplashActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        txtDiamonds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(getActivity(), DiamondActivity.class);
                startActivity(newIntent);
            }
        });
    }

    public void hideKeyboard(EditText edt_name) {
        // Check if no view has focus:
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(edt_name.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}