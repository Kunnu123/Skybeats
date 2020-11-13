package com.skybeats;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.skybeats.databinding.ActivityMainBinding;
import com.skybeats.ui.ProfileFragment;
import com.skybeats.ui.SearchFragment;
import com.skybeats.ui.dashboard.DashboardFragment;
import com.skybeats.ui.home.HomeFragment;
import com.skybeats.ui.notifications.NotificationsFragment;
import com.skybeats.utils.ImageUtils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.maincontainer, new DashboardFragment(MainActivity.this));
        transaction.commit();

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
            config().setChannelName("Skybeats");
            gotoLiveActivity(io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER);
        } else {
            requestPermissions();
        }
    }

    private void gotoLiveActivity(int role) {
        Intent intent = new Intent(getIntent());
        intent.putExtra(Constants.KEY_CLIENT_ROLE, role);
        intent.setClass(getApplicationContext(), LiveActivity.class);
        startActivity(intent);
    }

    private void gotoAudiance(int role) {
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
                config().setChannelName("Skybeats");
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
                config().setChannelName("Skybeats");
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