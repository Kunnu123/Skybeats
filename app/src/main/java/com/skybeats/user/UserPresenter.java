package com.skybeats.user;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.skybeats.retrofit.ApiCallInterface;
import com.skybeats.retrofit.DisposableCallback;
import com.skybeats.utils.AppClass;


public class UserPresenter implements UserPresenterInterface {

    UserViewInterface mvi;
    private String TAG = "UserPresenter";

    public UserPresenter(UserViewInterface mvi) {
        this.mvi = mvi;
    }

    @Override
    public void sendRequest(Context context, View view, int reqCode) {


        if (AppClass.networkConnectivity.isNetworkAvailable()) {

            if (mvi.validate(reqCode)) {

                this.mvi.showProgress(mvi.getProgressbar());

                if (reqCode == ApiCallInterface.USER_SIGNUP) {
                    ((AppClass) ((AppCompatActivity) context).getApplication()).getApiTask().sendRequestforMultipart(mvi.getRequestBody(reqCode), mvi.getProfileImage(reqCode),reqCode, new DisposableCallback(context, reqCode, mvi));
                } else {

                    ((AppClass) ((AppCompatActivity) context).getApplication()).getApiTask().sendRequest(mvi.getParameters(reqCode), reqCode, new DisposableCallback(context, reqCode, mvi));
                }
            }

        } else {
            AppClass.networkConnectivity.errorMessage(context, view, "No internet connection", mvi, reqCode);
        }
    }

}
