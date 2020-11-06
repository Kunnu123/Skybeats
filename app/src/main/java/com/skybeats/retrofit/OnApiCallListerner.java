package com.skybeats.retrofit;

/**
 * Created by Sachin on 25/4/19.
 * Prismetric Technology, Gandhinagar, Gujarat
 */
public interface OnApiCallListerner {
    public void onSuccess(int requestCode, int resultCode, Object message);

    public void onError(int requestCode, int resultCode, String message);
}
