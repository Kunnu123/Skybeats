package com.skybeats.dialogs;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDialog;
import androidx.databinding.DataBindingUtil;

import com.skybeats.R;
import com.skybeats.databinding.NoInternetConnectionDialogBinding;


public class NoInternetDialog extends AppCompatDialog implements View.OnClickListener {
    private final Activity context;
    private final View.OnClickListener onClickListener;

    NoInternetConnectionDialogBinding mBinding;

    public NoInternetDialog(Activity context, View.OnClickListener onClickListener) {
        super(context);
        this.context = context;

        this.onClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.no_internet_connection_dialog, null, false);
        setContentView(mBinding.getRoot());
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.92);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        this.getWindow().setAttributes(lp);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mBinding.tvRetry.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBinding.tvRetry.getId()) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
            dismiss();
        }
    }
}
