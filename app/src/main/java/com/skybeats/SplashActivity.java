package com.skybeats;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.skybeats.utils.AppSignatureHelper;
import com.skybeats.utils.MyPref;

import java.util.ArrayList;

public class SplashActivity extends BaseActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppSignatureHelper appSignature = new AppSignatureHelper(this);
        ArrayList<String> hasKeys = appSignature.getAppSignatures();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getMyPref().getData(MyPref.Keys.isLogin, false)){
                    startActivity(new Intent(context, MainActivity.class));
                    finishAffinity();
                }else {
                    startActivity(new Intent(context, LoginActivity.class));
                    finishAffinity();
                }

            }
        }, 2000);
    }
}