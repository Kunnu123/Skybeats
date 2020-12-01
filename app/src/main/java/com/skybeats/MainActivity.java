package com.skybeats;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.skybeats.databinding.ActivityMainBinding;
import com.skybeats.retrofit.WebAPI;
import com.skybeats.retrofit.model.BaseModel;
import com.skybeats.retrofit.model.BroadCastModel;
import com.skybeats.retrofit.model.GiftModel;
import com.skybeats.retrofit.model.UserModel;
import com.skybeats.rtc.media.RtcTokenBuilder;
import com.skybeats.ui.ProfileFragment;
import com.skybeats.ui.SearchFragment;
import com.skybeats.ui.dashboard.DashboardFragment;
import com.skybeats.ui.home.HomeFragment;
import com.skybeats.ui.notifications.NotificationsFragment;
import com.skybeats.utils.AppClass;
import com.skybeats.utils.ImageUtils;

import io.agora.rtc.RtcChannel;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.RtcEngineConfig;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    boolean isHome = true;
    boolean isSearch = false;
    boolean isNotification = false;
    boolean isProfile = false;
    private static final int PERMISSION_REQ_CODE_AUDIANCE = 123;
    private static final int PERMISSION_REQ_CODE = 1 << 4;
    private String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    boolean isAPICAll = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.maincontainer, new DashboardFragment(MainActivity.this));
        transaction.commit();

        AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.GET_USER_DETAILS)
                .addBodyParameter(WebAPI.USER_ID, getMyPref().getUserData().getUser_id())
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(UserModel.class, new ParsedRequestListener<UserModel>() {
                    @Override
                    public void onResponse(UserModel response) {
                        if (response.isStatus().equalsIgnoreCase("200")) {
                            getMyPref().setUserData(response.getData());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.getResponse();
                    }
                });


        binding.ivHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isHome = true;
                isSearch = false;
                isNotification = false;
                isProfile = false;
//                binding.ivHome.setBackgroundColor(getResources().getColor(R.color.white));
//                binding.ivSearch.setBackgroundColor(getResources().getColor(R.color.black));
//                binding.ivnoti.setBackgroundColor(getResources().getColor(R.color.black));
//                binding.ivprofile.setBackgroundColor(getResources().getColor(R.color.black));
                final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.maincontainer, new DashboardFragment(MainActivity.this));
                transaction.commit();
            }
        });

        binding.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isHome = false;
                isSearch = true;
                isNotification = false;
                isProfile = false;
//                binding.ivSearch.setBackgroundColor(getResources().getColor(R.color.white));
//                binding.ivHome.setBackgroundColor(getResources().getColor(R.color.black));
//                binding.ivnoti.setBackgroundColor(getResources().getColor(R.color.black));
//                binding.ivprofile.setBackgroundColor(getResources().getColor(R.color.black));
                final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.maincontainer, new SearchFragment());
                transaction.commit();
            }
        });

        binding.ivnoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isHome = false;
                isSearch = false;
                isNotification = true;
                isProfile = false;
                binding.line.setVisibility(View.GONE);
                binding.llBottom.setVisibility(View.GONE);
                binding.liveVideo.setVisibility(View.GONE);
//                binding.ivnoti.setBackgroundColor(getResources().getColor(R.color.white));
//                binding.ivHome.setBackgroundColor(getResources().getColor(R.color.black));
//                binding.ivSearch.setBackgroundColor(getResources().getColor(R.color.black));
//                binding.ivprofile.setBackgroundColor(getResources().getColor(R.color.black));
                final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.maincontainer, new NotificationsFragment());
                transaction.commit();
            }
        });

        ImageUtils.loadImage(MainActivity.this, getMyPref().getUserData().getProfile_image(), R.drawable.avatar, binding.ivprofile);
        binding.txtUser.setText(getMyPref().getUserData().getUser_name());
        binding.txtSkyId.setText(getMyPref().getUserData().getUser_id());

        binding.llProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isHome = false;
                isSearch = false;
                isNotification = false;
                isProfile = true;
                binding.line.setVisibility(View.GONE);
                binding.llBottom.setVisibility(View.GONE);
                binding.liveVideo.setVisibility(View.GONE);
//                binding.ivprofile.setBackgroundColor(getResources().getColor(R.color.white));
//                binding.ivHome.setBackgroundColor(getResources().getColor(R.color.black));
//                binding.ivSearch.setBackgroundColor(getResources().getColor(R.color.black));
//                binding.ivnoti.setBackgroundColor(getResources().getColor(R.color.black));
                final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.maincontainer, new ProfileFragment());
                transaction.commit();
            }
        });

        binding.liveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage(getResources().getString(R.string.ter_message));
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                checkPermission();
                            }
                        });

                alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });


//        UserModel userModel=getMyPref().getUserData();
//        if (userModel!=null){
//            binding.tvName.setText(userModel.getUser_name());
//            binding.tvMobile.setText(userModel.getMobile_no());
//            ImageUtils.loadImage(MainActivity.this, userModel.getProfile_image(), R.drawable.avatar, binding.imgProfile);
//        }
    }

    private void checkPermission() {
        boolean granted = true;
        for (String per : PERMISSIONS) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }

        if (granted) {
            gotoLiveActivity(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
        } else {
            requestPermissions();
        }
    }

    private void gotoLiveActivity(int role) {

        String appId = "dc5e59c822f940eb98283b57cef08f55";
        String appCertificate = "ff4a4835e1494786ac7428e784ef22a9";
        String channelName = getMyPref().getUserData().getUser_id();
        String userAccount = "";
        int uid = 0;
        int expirationTimeInSeconds = 84000000;

        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String result = token.buildTokenWithUserAccount(appId, appCertificate,
                channelName, userAccount, RtcTokenBuilder.Role.Role_Publisher, timestamp);
        System.out.println(result);
        AppClass.agoraToken = result;

        result = token.buildTokenWithUid(appId, appCertificate,
                channelName, uid, RtcTokenBuilder.Role.Role_Publisher, timestamp);
        System.out.println(result);
        AppClass.agoraToken = result;
        config().setChannelName(getMyPref().getUserData().getUser_id());

        Intent intent = new Intent(getIntent());
        intent.putExtra(Constants.KEY_CLIENT_ROLE, role);
        intent.setClass(getApplicationContext(), LiveActivity.class);
        startActivity(intent);

//        final Dialog dialog = new Dialog(MainActivity.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.setContentView(R.layout.custo_gi);
//
//        String appId = "dc5e59c822f940eb98283b57cef08f55";
//        String appCertificate = "ff4a4835e1494786ac7428e784ef22a9";
//        String channelName = getMyPref().getUserData().getUser_id();
//        String userAccount = "";
//        int uid = 0;
//        int expirationTimeInSeconds = 84000000;
//
//        RtcTokenBuilder token = new RtcTokenBuilder();
//        int timestamp = (int)(System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
//        String result = token.buildTokenWithUserAccount(appId, appCertificate,
//                channelName, userAccount, RtcTokenBuilder.Role.Role_Publisher, timestamp);
//        System.out.println(result);
//        AppClass.agoraToken = result;
//
//        result = token.buildTokenWithUid(appId, appCertificate,
//                channelName, uid, RtcTokenBuilder.Role.Role_Publisher, timestamp);
//        System.out.println(result);
//        AppClass.agoraToken = result;
//        config().setChannelName(getMyPref().getUserData().getUser_id());
//
//
//        if (AppClass.networkConnectivity.isNetworkAvailable()) {
//            AndroidNetworking.post(WebAPI.BASE_URL + WebAPI.ADD_USER_DETAILS)
//                    .addBodyParameter(WebAPI.USER_ID, getMyPref().getUserData().getUser_id())
//                    .addBodyParameter(WebAPI.SKYBEAT_ID, getMyPref().getUserData().getUser_id())
//                    .addBodyParameter(WebAPI.TOKEN, result)
//                    .addBodyParameter(WebAPI.CHANNEL_NAME, getMyPref().getUserData().getUser_id())
//                    .setTag(this)
//                    .setPriority(Priority.HIGH)
//                    .build()
//                    .getAsObject(BaseModel.class, new ParsedRequestListener<BaseModel>() {
//                        @Override
//                        public void onResponse(BaseModel response) {
//                            if (response.isStatus().equalsIgnoreCase("200")) {
//                                isAPICAll = true;
//                            }
//                        }
//
//                        @Override
//                        public void onError(ANError anError) {
//                        }
//                    });
//
//        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                dialog.dismiss();
//                if (isAPICAll){
//                    Intent intent = new Intent(getIntent());
//                    intent.putExtra(Constants.KEY_CLIENT_ROLE, role);
//                    intent.setClass(getApplicationContext(), LiveActivity.class);
//                    startActivity(intent);
//                }else {
//                    Toast.makeText(MainActivity.this, "Please try again", Toast.LENGTH_LONG).show();
//                }
//            }
//        },4000);
//        dialog.show();


    }

    private void gotoAudiance(int role) {
        config().setChannelName(getMyPref().getUserData().getUser_id());
        Intent intent = new Intent(getIntent());
        intent.putExtra(Constants.KEY_CLIENT_ROLE, role);
        intent.setClass(getApplicationContext(), JoinLiveActivity.class);
        startActivity(intent);
    }


    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(
                this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = (result == PackageManager.PERMISSION_GRANTED);
                if (!granted) break;
            }

            if (granted) {
                gotoLiveActivity(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
            } else {
                toastNeedPermissions();
            }
        }
        if (requestCode == PERMISSION_REQ_CODE_AUDIANCE) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = (result == PackageManager.PERMISSION_GRANTED);
                if (!granted) break;
            }

            if (granted) {
                gotoAudiance(io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE);
            } else {
                toastNeedPermissions();
            }
        }
    }

    private void toastNeedPermissions() {
        Toast.makeText(this, R.string.need_necessary_permissions, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (isHome){
            super.onBackPressed();
        }else {
            binding.line.setVisibility(View.VISIBLE);
            binding.llBottom.setVisibility(View.VISIBLE);
            binding.liveVideo.setVisibility(View.VISIBLE);
            isHome = true;
            isSearch = false;
            isNotification = false;
            isProfile = false;
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.maincontainer, new DashboardFragment(MainActivity.this));
            transaction.commit();
        }
    }
}