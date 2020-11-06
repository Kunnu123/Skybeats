package com.skybeats.retrofit;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;


import com.skybeats.BaseViewInterface;
import com.skybeats.LoginActivity;
import com.skybeats.utils.AppClass;
import com.skybeats.utils.Logger;

import java.net.ConnectException;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

public class DisposableCallback<T> extends DisposableObserver<T> {
    private Context context;
    private int reqCode;
    private BaseViewInterface onApiCallListerner;


    public DisposableCallback(Context context, int reqCode, BaseViewInterface onApiCallListerner) {
        this.context = context;
        this.reqCode = reqCode;
        this.onApiCallListerner = onApiCallListerner;


    }

    @Override
    public void onNext(Object t) {
        if (((AppCompatActivity) context).isFinishing()) {
            return;
        }
        hideProgress();
        onApiCallListerner.onSuccess(t, reqCode, 200);
    }

    @Override
    public void onError(Throwable e) {
        if (((AppCompatActivity) context).isFinishing()) {
            return;
        }
        Logger.d(e.getMessage());
        hideProgress();
        if (e instanceof UndeliverableException) {
            onApiCallListerner.onError(e.getMessage(), reqCode, e.hashCode());
        } else if (e instanceof ConnectException) {
            AppClass.networkConnectivity.errorMessage(context, onApiCallListerner, reqCode);
        } else if (e instanceof HttpException) {
            onApiCallListerner.onError(e.getMessage(), reqCode, ((HttpException) e).code());
            if (((HttpException) e).code() == 401) {
                context.startActivity(new Intent(context, LoginActivity.class));
                ((AppCompatActivity) context).finishAffinity();
                return;
            }
        }


    }

    @Override
    public void onComplete() {
        hideProgress();
    }

    private void hideProgress() {

        this.onApiCallListerner.hideProgress(onApiCallListerner.getProgressbar());

    }
}
