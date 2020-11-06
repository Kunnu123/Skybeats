package com.skybeats;

import android.view.View;
import android.widget.ProgressBar;

public interface BaseViewInterface {
     void retry(int pos);

    View getProgressbar();

    void showProgress(View progressBar);

    void hideProgress(View progressBar);

    void onError(String errorMsg, int requestCode, int resultCode);

    void onSuccess(Object success, int requestCode, int resultCode);

}
