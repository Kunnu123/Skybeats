package com.skybeats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.skybeats.databinding.ActivityOtpBinding;
import com.skybeats.retrofit.ApiCallInterface;
import com.skybeats.retrofit.model.BaseModel;
import com.skybeats.retrofit.model.UserModel;
import com.skybeats.user.UserPresenter;
import com.skybeats.user.UserPresenterInterface;
import com.skybeats.user.UserViewInterface;
import com.skybeats.utils.KeyBoardUtils;
import com.skybeats.utils.Logger;
import com.skybeats.utils.MyPref;
import com.skybeats.utils.SMSBroadcastReceiver;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.RequestBody;

public class OtpActivity extends BaseActivity implements View.OnClickListener, UserViewInterface, SMSBroadcastReceiver.OTPReceiveListener {
    UserPresenterInterface presenterInterface;
    private boolean isExist = false;
    ActivityOtpBinding binding;
    String mobile_no;
    String country_name;
    CountDownTimer timer;
    ProgressDialog progressDialog;
    private SMSBroadcastReceiver smsBroadcast = new SMSBroadcastReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setSMSRetriver();
    }

    private void init() {
        mobile_no = getIntent().getStringExtra("mobile_no");
        country_name = getIntent().getStringExtra("country_name");
        isExist = getIntent().getBooleanExtra("isExist", false);
        presenterInterface = new UserPresenter(this);
        binding.tvRegister.setOnClickListener(this);
        binding.imgBack.setOnClickListener(this);
        binding.tvResend.setOnClickListener(this);
        startTimer();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                KeyBoardUtils.closeSoftKeyboard(OtpActivity.this);
                presenterInterface.sendRequest(context, binding.getRoot(), ApiCallInterface.VERIFY_OTP);

                break;
            case R.id.tv_resend:
                binding.etOtp.setText("");
                presenterInterface.sendRequest(context, binding.getRoot(), ApiCallInterface.SEND_OTP);

                break;
            case R.id.img_back:
                onBackPressed();
                break;
        }
    }

    private void startTimer() {
        binding.tvResend.setVisibility(View.GONE);
        binding.tvTime.setVisibility(View.VISIBLE);
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvTime.setText(getHourFromMillis(millisUntilFinished));


            }

            @Override
            public void onFinish() {
                binding.tvResend.setVisibility(View.VISIBLE);
                binding.tvTime.setVisibility(View.INVISIBLE);
            }
        };
        timer.start();
    }

    public String getHourFromMillis(long millis) {
        String hms = "0";
        try {
            hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));


        } catch (Exception e) {
            e.printStackTrace();
        }
        return hms;
    }

    @Override
    public boolean validate(int reqCode) {


        if (reqCode == ApiCallInterface.SEND_OTP) {
            return true;
        } else if (reqCode == ApiCallInterface.VERIFY_OTP) {
            if (TextUtils.isEmpty(binding.etOtp.getText().toString()) || binding.etOtp.getText().toString().length() < 4) {
                errorMessage(binding.getRoot(), "Please enter valid otp");
                return false;
            }
            return true;
        }
        return true;
    }

    @Override
    public HashMap<String, Object> getParameters(int reqCode) {


        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile_no", mobile_no);
        map.put("otp", binding.etOtp.getText().toString());
        if (reqCode == ApiCallInterface.LOGIN) {
            map.put("password", "");
            map.put("is_login_via_otp", "1");
        }


        return map;
    }

    @Override
    public HashMap<String, RequestBody> getRequestBody(int reqCode) {
        return null;
    }

    @Override
    public String getProfileImage(int reqCode) {
        return null;
    }

    @Override
    public void retry(int pos) {
        presenterInterface.sendRequest(context, binding.getRoot(), pos);
    }

    @Override
    public View getProgressbar() {
        return null;
    }

    @Override
    public void showProgress(View progressBar) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        presenterInterface = null;
    }

    @Override
    public void hideProgress(View progressBar) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onError(String errorMsg, int requestCode, int resultCode) {

    }

    @Override
    public void onSuccess(Object success, int requestCode, int resultCode) {
        if (requestCode == ApiCallInterface.SEND_OTP) {
            BaseModel baseModel = (BaseModel) success;
            if (baseModel.isStatus().equals("200")) {
                Toast.makeText(context, baseModel.getResponse_msg(), Toast.LENGTH_SHORT).show();
            }

        }
        if (requestCode == ApiCallInterface.LOGIN) {
            UserModel baseModel = (UserModel) success;
            if (baseModel.isStatus().equals("200")) {
                Toast.makeText(context, "Successfully Login", Toast.LENGTH_SHORT).show();
                getMyPref().setUserData(baseModel.getData());
                getMyPref().setData(MyPref.Keys.isLogin, true);
                startActivity(new Intent(context, MainActivity.class));
//                startActivity(new Intent(context, HomeActivity.class));
                finishAffinity();
            }

        }
        if (requestCode == ApiCallInterface.VERIFY_OTP) {
            BaseModel baseModel = (BaseModel) success;
            Toast.makeText(context, baseModel.getResponse_msg(), Toast.LENGTH_SHORT).show();
            if (baseModel.isStatus().equals("200")) {
                if (isExist) {
                    presenterInterface.sendRequest(context, binding.getRoot(), ApiCallInterface.LOGIN);
                } else {
                    startActivity(new Intent(context, SignupActivity.class).putExtra("mobile_no", mobile_no).putExtra("country_name", country_name));
                    finishAffinity();
                }

            }
        }
    }

    @Override
    public void onOTPReceived(String otp) {
        Logger.d(" otp " + otp);

        try {
            if (!TextUtils.isEmpty(otp)) {
                //   if (sender.contains("TRUSRC")) {
                if (isInteger(otp)) {

                    binding.etOtp.setText(otp);
                    KeyBoardUtils.closeSoftKeyboard(OtpActivity.this);
                    presenterInterface.sendRequest(context, binding.getRoot(), ApiCallInterface.VERIFY_OTP);

                }
                // }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOTPTimeOut() {

    }

    private void setSMSRetriver() {

        startSMSListener();
        smsBroadcast.initOTPListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);

        getApplicationContext().registerReceiver(smsBroadcast, intentFilter);
        //requestHint();
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (Exception e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    private void startSMSListener() {

        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                // ...
                Logger.e("SMS Retriever starts");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
                Logger.e("Cannot Start SMS Retriever error : " + e.getMessage());
            }
        });
    }
}