package com.skybeats.retrofit.model;

import android.text.TextUtils;

import java.io.Serializable;


public class BaseModel implements Serializable {
    private String status;
    private String message;

    public String isStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse_msg() {
        if (!TextUtils.isEmpty(message))
            return message;
        return "Something Wrong";
    }

    public void setResponse_msg(String response_msg) {
        this.message = response_msg;
    }

}
