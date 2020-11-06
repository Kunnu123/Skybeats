package com.skybeats.user;


import com.skybeats.BaseViewInterface;

import java.util.HashMap;

import okhttp3.RequestBody;

public interface UserViewInterface extends BaseViewInterface {
    boolean validate(int reqCode);

    HashMap<String, Object> getParameters(int reqCode);

    HashMap<String, RequestBody> getRequestBody(int reqCode);

    String getProfileImage(int reqCode);

}
