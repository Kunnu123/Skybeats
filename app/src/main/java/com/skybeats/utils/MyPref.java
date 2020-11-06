package com.skybeats.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


import com.google.gson.Gson;
import com.skybeats.retrofit.model.UserModel;


import static android.content.Context.MODE_PRIVATE;

public class MyPref {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public MyPref(Context context) {
        preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setUserData(UserModel userData) {
        editor.putString(Keys.UserData.name(), new Gson().toJson(userData));
        editor.commit();
    }


    public UserModel getUserData() {
        String userData = preferences.getString(Keys.UserData.name(), "");
        if (TextUtils.isEmpty(userData))
            return new UserModel();
        UserModel user = new Gson().fromJson(userData, UserModel.class);
        if (user == null) return new UserModel();
        return user;
    }


    public void setData(Keys keys, boolean isData) {
        editor.putBoolean(keys.name(), isData);
        editor.commit();
    }

    public void setData(Keys keys, String isData) {
        editor.putString(keys.name(), isData);
        editor.commit();
    }

    public void clearPrefs() {
        editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public String getData(Keys keys) {
        return preferences.getString(keys.name(), "");
    }

    public boolean getData(Keys keys, boolean defaults) {
        return preferences.getBoolean(keys.name(), defaults);
    }

    public enum Keys {
        UserData, userId, token, isLogin
    }
}