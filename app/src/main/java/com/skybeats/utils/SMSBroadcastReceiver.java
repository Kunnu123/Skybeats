package com.skybeats.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

/**
 * Created by Chintan on 08-11-2017.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {

    private OTPReceiveListener otpReceiver;

    public void initOTPListener(OTPReceiveListener otpReceiver) {
        this.otpReceiver = otpReceiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            switch (status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    if (otpReceiver != null && message != null) {
                        message = message.substring(0, message.length() - 11).trim();
                        String otp = message.substring(message.length() - 4, message.length());
                        otpReceiver.onOTPReceived(otp);
                    }
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server for SMS authenticity.
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    if (otpReceiver != null) {
                        otpReceiver.onOTPTimeOut();
                    }
                    break;

            }
        }
    }

    public interface OTPReceiveListener {

        public void onOTPReceived(String otp);

        public void onOTPTimeOut();
    }
}
