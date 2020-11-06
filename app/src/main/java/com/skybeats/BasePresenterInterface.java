package com.skybeats;

import android.content.Context;
import android.view.View;

public interface BasePresenterInterface {
    void sendRequest(Context context, View view, int reqCode);
}
