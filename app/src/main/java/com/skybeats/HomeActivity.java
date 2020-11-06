package com.skybeats;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.material.navigation.NavigationView;
import com.skybeats.ui.ProfileFragment;
import com.skybeats.ui.SearchFragment;
import com.skybeats.ui.dashboard.DashboardFragment;
import com.skybeats.ui.notifications.NotificationsFragment;

public class HomeActivity extends BaseActivity {
    private static final int PERMISSION_REQ_CODE = 1 << 4;
    private final static int ID_HOME = 1;
    private final static int ID_EXPLORE = 2;
    private final static int ID_MESSAGE = 3;
    private final static int ID_NOTIFICATION = 4;
    private final static int ID_ACCOUNT = 5;
    private String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    FrameLayout contentContainer;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private AppCompatImageView iv_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        contentContainer = findViewById(R.id.container);
        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        iv_profile = (AppCompatImageView) findViewById(R.id.iv_profile);


        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new DashboardFragment());
        transaction.addToBackStack(null);
        transaction.commit();

        bottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_EXPLORE, R.drawable.ic_search));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_MESSAGE, R.drawable.ic_video_camera));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION, R.drawable.ic_notification));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_account));

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                switch (item.getId()) {
                    case ID_HOME:
                        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, new DashboardFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case ID_EXPLORE:
                        final FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                        transaction1.replace(R.id.container, new SearchFragment());
                        transaction1.addToBackStack(null);
                        transaction1.commit();
                        break;
                    case ID_MESSAGE:
                        checkPermission();
                        break;
                    case ID_NOTIFICATION:
                        final FragmentTransaction transactionnoti = getSupportFragmentManager().beginTransaction();
                        transactionnoti.replace(R.id.container, new NotificationsFragment());
                        transactionnoti.addToBackStack(null);
                        transactionnoti.commit();
                        break;
                    case ID_ACCOUNT:
                        final FragmentTransaction transactionprof = getSupportFragmentManager().beginTransaction();
                        transactionprof.replace(R.id.container, new ProfileFragment());
                        transactionprof.addToBackStack(null);
                        transactionprof.commit();
                        break;
                }
//                Toast.makeText(HomeActivity.this, "clicked item : " + item.getId(), Toast.LENGTH_SHORT).show();
            }
        });


        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
//                Toast.makeText(HomeActivity.this, "showing item : " + item.getId(), Toast.LENGTH_SHORT).show();

                String name;
                switch (item.getId()) {
                    case ID_HOME:
                        name = "HOME";
                        break;
                    case ID_EXPLORE:
                        name = "EXPLORE";
                        break;
                    case ID_MESSAGE:
                        name = "MESSAGE";
                        break;
                    case ID_NOTIFICATION:
                        name = "NOTIFICATION";
                        break;
                    case ID_ACCOUNT:
                        name = "ACCOUNT";
                        break;
                    default:
                        name = "";
                }
            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                checkPermission();
//                Toast.makeText(HomeActivity.this, "reselected item : " + item.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigation.setCount(ID_NOTIFICATION, "10");

        bottomNavigation.show(ID_HOME, true);

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
    }

    private void toastNeedPermissions() {
        Toast.makeText(this, R.string.need_necessary_permissions, Toast.LENGTH_LONG).show();
    }
}