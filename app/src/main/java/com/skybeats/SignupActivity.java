package com.skybeats;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.skybeats.databinding.ActivitySignupBinding;
import com.skybeats.retrofit.ApiCallInterface;
import com.skybeats.retrofit.model.BaseModel;
import com.skybeats.retrofit.model.UserModel;
import com.skybeats.user.UserPresenter;
import com.skybeats.user.UserPresenterInterface;
import com.skybeats.user.UserViewInterface;
import com.skybeats.utils.BottomSheetGetImageVideoFragment;
import com.skybeats.utils.ImageUtils;
import com.skybeats.utils.KeyBoardUtils;
import com.skybeats.utils.MyPref;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SignupActivity extends BaseActivity implements View.OnClickListener, UserViewInterface {
    UserPresenterInterface presenterInterface;
    String country_name;
    String mobile_no;
    ProgressDialog progressDialog;
    ActivitySignupBinding binding;
    BottomSheetGetImageVideoFragment bottomSheetGetImageVideoFragment;
    String profile_photo = "";
    String birthdate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mobile_no = getIntent().getStringExtra("mobile_no");
        country_name = getIntent().getStringExtra("country_name");
        presenterInterface = new UserPresenter(this);
        binding.toolbar.imgBack.setOnClickListener(this);
        binding.tvNext.setOnClickListener(this);
        binding.imgProfile.setOnClickListener(this);
        binding.edtDob.setOnClickListener(this);
        binding.toolbar.tvTitle.setText("Sign Up");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tv_next:

                presenterInterface.sendRequest(context, binding.getRoot(), ApiCallInterface.USER_SIGNUP);

                break;
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.img_profile:
                openGallery();
                break;
            case R.id.edt_dob:
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                DatePickerDialog pickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                birthdate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                binding.edtDob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                pickerDialog.show();
                break;
        }
    }

    @Override
    public boolean validate(int reqCode) {
        if (TextUtils.isEmpty(profile_photo)) {
            errorMessage(binding.getRoot(), "Please  select profile photo");
            return false;
        } else if (TextUtils.isEmpty(binding.edtUsername.getText().toString().trim())) {
            errorMessage(binding.getRoot(), "Please enter username");
            return false;
        } else if (TextUtils.isEmpty(birthdate)) {
            errorMessage(binding.getRoot(), "Please select birthdate");
            return false;
        } else if (TextUtils.isEmpty(binding.edtPassword.getText().toString().trim())) {
            errorMessage(binding.getRoot(), "Please enter password");
            return false;
        }
        return true;
    }

    @Override
    public HashMap<String, Object> getParameters(int reqCode) {
        return null;
    }

    @Override
    public HashMap<String, RequestBody> getRequestBody(int reqCode) {
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("user_name", toRequestBody(binding.edtUsername.getText().toString()));
        map.put("gender", toRequestBody(binding.rbGroup.getCheckedRadioButtonId() == R.id.rb_male ? "M" : "F"));
        map.put("country", toRequestBody(country_name));
        map.put("dob", toRequestBody(birthdate));
        map.put("mobile_no", toRequestBody(mobile_no));
        map.put("password", toRequestBody(binding.edtPassword.getText().toString()));
        map.put("is_new_user", toRequestBody("1"));
      /*  if (!profile_photo.equalsIgnoreCase(""))
            map.put("profile_image", toRequestBody(new File(profile_photo)));*/

        return map;
    }

    @Override
    public String getProfileImage(int reqCode) {
        return profile_photo;
    }

    private RequestBody toRequestBody(File file) {
        return RequestBody.create(MediaType.parse("image/*"), file);
    }

    public RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    @Override
    public void retry(int pos) {
        presenterInterface.sendRequest(context, binding.getRoot(), ApiCallInterface.USER_SIGNUP);
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
        if (requestCode == ApiCallInterface.USER_SIGNUP) {
            UserModel baseModel = (UserModel) success;
            if (baseModel.isStatus().equals("200")) {
                Toast.makeText(context, baseModel.getResponse_msg(), Toast.LENGTH_SHORT).show();
                getMyPref().setUserData(baseModel.getData());
                getMyPref().setData(MyPref.Keys.isLogin, true);
                startActivity(new Intent(context, MainActivity.class));
//                startActivity(new Intent(context, HomeActivity.class));
                finishAffinity();
            }

        }
    }

    private void openGallery() {

        bottomSheetGetImageVideoFragment = new BottomSheetGetImageVideoFragment(SignupActivity.this, BottomSheetGetImageVideoFragment.GET_IMAGE, new BottomSheetGetImageVideoFragment.OnActivityResult() {
            @Override
            public void onSuccessResult(String resultant_file_path, Bitmap thumbnail_bitmap) {
                Log.e("Selected Image", resultant_file_path);
                profile_photo = resultant_file_path;
                ImageUtils.loadImage(SignupActivity.this, resultant_file_path, R.drawable.avatar, binding.imgProfile);
            }

            @Override
            public void onFailResult(String reason) {

            }
        });
        bottomSheetGetImageVideoFragment.show(getSupportFragmentManager(), "");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            bottomSheetGetImageVideoFragment.onActivityResult(requestCode, resultCode, data);
        }


    }
}