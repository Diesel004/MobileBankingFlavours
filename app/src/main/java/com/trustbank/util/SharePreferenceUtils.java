package com.trustbank.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Trust on 05-12-2015.
 */
public class SharePreferenceUtils {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharePreferenceUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(AppConstants.PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putInteger(String key, int value) {
        editor.putInt(key, value).commit();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public int getInt(String key, int value) {
        return sharedPreferences.getInt(key, value);
    }

    public void clearValue(String key) {
        try {
            sharedPreferences.edit().remove(key).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
