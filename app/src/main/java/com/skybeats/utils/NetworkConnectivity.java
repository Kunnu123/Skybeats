package com.skybeats.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.skybeats.BaseViewInterface;
import com.skybeats.dialogs.NoInternetDialog;


public class NetworkConnectivity {

    Context context;

    public NetworkConnectivity(Context context) {
        super();
        this.context = context;
    }

    /**
     * Check whether the device is connected, and if so, whether the connection
     * is wifi or mobile (it could be something else).
     */
    public boolean isNetworkAvailable() {
        boolean isConnected = false;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();

        if (connMgr != null) {
            //Tlog.i("getActiveNetworkInfo : " + connMgr.getActiveNetworkInfo());
            //Tlog.i("getAllNetworkInfo : " + connMgr.getAllNetworkInfo());
        }

        if (activeInfo != null
                && (activeInfo.isConnected() || activeInfo.isConnectedOrConnecting())) {

            //SLog.i("isConnected : " + activeInfo.isConnected());
            //SLog.i("isConnectedOrConnecting : "+ activeInfo.isConnectedOrConnecting());
            //Tlog.i("isAvailable : " + activeInfo.isAvailable());
            //Tlog.i("isFailover : " + activeInfo.isFailover());
            //Tlog.i("isRoaming : " + activeInfo.isRoaming());
            //Tlog.i("isRoaming : " + activeInfo.isRoaming());
            //Tlog.i("getType : " + activeInfo);

            switch (activeInfo.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    //SLog.i("TYPE_MOBILE : "+ ConnectivityManager.TYPE_MOBILE);
                    isConnected = true;
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    //SLog.i("TYPE_WIFI : " + ConnectivityManager.TYPE_WIFI);
                    isConnected = true;
                    break;
                case ConnectivityManager.TYPE_WIMAX:
                    //SLog.i("TYPE_WIMAX : " + ConnectivityManager.TYPE_WIMAX);
                    isConnected = true;
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    //SLog.i("TYPE_ETHERNET : "+ ConnectivityManager.TYPE_ETHERNET);
                    isConnected = true;
                    break;

                default:
                    isConnected = false;
                    break;
            }
        }
        Logger.d("" + "isConnected : " + isConnected);
        return isConnected;
    }

    public void errorMessage(Context context, View view, String error, BaseViewInterface viewInterface, int reqCode) {
       /* Snackbar snackbar = Snackbar
                .make(view, error, Snackbar.LENGTH_LONG)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewInterface.retry(reqCode);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.setTextColor(Color.WHITE);
        snackbar.setDuration(15000);
        snackbar.show();*/

        new NoInternetDialog((Activity) context, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewInterface.retry(reqCode);

            }
        }).show();
    }

    public void errorMessage(Context context, BaseViewInterface viewInterface, int reqCode) {

        new NoInternetDialog((Activity) context, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewInterface.retry(reqCode);

            }
        }).show();
    }
}