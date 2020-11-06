package com.skybeats.retrofit;


import com.skybeats.retrofit.model.BaseModel;
import com.skybeats.retrofit.model.UserModel;

import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;


public interface ApiCallInterface {
    int TOKEN_EXPIRED = 401;
    int SEND_OTP = 1;
    int CHECK_USER_EXIST = 2;
    int VERIFY_OTP = 3;
    int USER_SIGNUP = 4;
    int LOGIN = 5;


    @POST(WebAPI.SENDOTP)
    @FormUrlEncoded
    Observable<BaseModel> sendOtp(@FieldMap HashMap<String, Object> map);

    @POST(WebAPI.CHECK_USER_EXIST)
    @FormUrlEncoded
    Observable<BaseModel> checkUserExist(@FieldMap HashMap<String, Object> map);

    @POST(WebAPI.VERIFYOTP)
    @FormUrlEncoded
    Observable<BaseModel> verifyOTP(@FieldMap HashMap<String, Object> map);

    @POST(WebAPI.LOGIN)
    @FormUrlEncoded
    Observable<UserModel> login(@FieldMap HashMap<String, Object> map);

    @POST(WebAPI.USER_SIGNUP)
    @Multipart
    Observable<UserModel> signUp(@PartMap HashMap<String, RequestBody> map, @Part MultipartBody.Part file);


}
