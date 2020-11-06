package com.skybeats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.skybeats.databinding.ActivityLoginBinding;
import com.skybeats.retrofit.ApiCallInterface;
import com.skybeats.retrofit.model.BaseModel;
import com.skybeats.user.UserPresenter;
import com.skybeats.user.UserPresenterInterface;
import com.skybeats.user.UserViewInterface;

import java.util.HashMap;

import okhttp3.RequestBody;

public class LoginActivity extends BaseActivity implements View.OnClickListener, UserViewInterface {
    ActivityLoginBinding binding;
    UserPresenterInterface presenterInterface;
    private boolean isExist = false;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

    }

    private void init() {
        presenterInterface = new UserPresenter(this);
        binding.tvRegister.setOnClickListener(this);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.montserrat_regular);
        binding.ccp.setTypeFace(typeface);
        binding.ccp.registerCarrierNumberEditText(binding.edtNumber);
        binding.ccp.setPhoneNumberValidityChangeListener(isValidNumber -> {
            // your code
            if (isValidNumber) {
                binding.edtNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.green_tick, 0);
            } else {
                binding.edtNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register:
                presenterInterface.sendRequest(context, binding.getRoot(), ApiCallInterface.CHECK_USER_EXIST);

                break;
        }
    }

    @Override
    public boolean validate(int reqCode) {
        if (!binding.ccp.isValidFullNumber()) {
            errorMessage(binding.getRoot(), "Please enter valid mobile number");
            return false;
        }
        return binding.ccp.isValidFullNumber();
    }

    @Override
    public HashMap<String, Object> getParameters(int reqCode) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile_no", binding.ccp.getFullNumber());
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

        if (requestCode == ApiCallInterface.CHECK_USER_EXIST) {
            BaseModel baseModel = (BaseModel) success;
            isExist = baseModel.isStatus().equals("200");

            presenterInterface.sendRequest(context, binding.getRoot(), ApiCallInterface.SEND_OTP);
        }
        if (requestCode == ApiCallInterface.SEND_OTP) {
            BaseModel baseModel = (BaseModel) success;
            if (baseModel.isStatus().equals("200")) {
                Toast.makeText(context, baseModel.getResponse_msg(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,OtpActivity.class);
                intent.putExtra("mobile_no", binding.ccp.getFullNumber());
                intent.putExtra("isExist", isExist);
                intent.putExtra("country_name", binding.ccp.getSelectedCountryEnglishName());
                startActivity(intent);
            }

        }
    }
}