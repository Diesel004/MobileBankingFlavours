package com.trustbank.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.trustbank.activity.LockActivity;
import com.trustbank.activity.WelcomeScreen;

import java.util.HashSet;
import java.util.Set;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "BankApp";
    private static final String IS_LOGIN = "IsLoggedIn";
   // public static final String KEY_MOBILE_NO = "key_mobile_no"; //for new version ,PDCC need this key.
    public static final String KEY_MOBILE_NO = "key_mpin"; //only for sadhana.
    public static final String KEY_CLIENT_ID = "key_client_id";
    public static final String KEY_MOBILE_NO_OLD = "key_mobile_no"; //for new version ,PDCC need this key(Make dynamically).
    public static final String KEY_CLIENT_ID_LIST = "key_client_id_list";
    Activity activity;

    public SessionManager(Context context) {
        this._context = context;
        this.activity = (Activity) context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    //      Create login session
    public void createLoginSession(String mobile, String clientId) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_MOBILE_NO, mobile);
        editor.putString(KEY_CLIENT_ID, clientId);
        editor.commit();
    }

    public String getMobileNUmber(String keyMobile,String keyMobileOld) {
        if (!TextUtils.isEmpty(pref.getString(keyMobile, ""))){
            return pref.getString(keyMobile, "");
        }else {
            return pref.getString(keyMobileOld, "");
        }

    }

    public String getClientId() {
        return pref.getString(KEY_CLIENT_ID, "");
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status

        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
//            Intent i = new Intent(_context, Welcome.class);
            Intent i = new Intent(_context, WelcomeScreen.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            if (!TrustMethods.isSimAvailable(_context)) {
                i.putExtra(AppConstants.SIM_ERROR_MSG, AppConstants.SIM_NOT_EXISTS);
            }
            _context.startActivity(i);
            activity.finish();
        } else {

            Intent i = new Intent(_context, LockActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (!TrustMethods.isSimAvailable(_context)) {
                i.putExtra(AppConstants.SIM_ERROR_MSG, AppConstants.SIM_NOT_EXISTS);
            }
            _context.startActivity(i);

        }

    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        Intent i = new Intent(_context, LockActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void storeclientList(Set<String> mySet) {
        //    Set<String> mySet = new HashSet<>();
        String json = new Gson().toJson(mySet);
        editor.putStringSet(KEY_CLIENT_ID_LIST, mySet);
        editor.commit();
    }

    public Set<String> getClientListIds() {
        //  return pref.getStringSet(KEY_CLIENT_ID_LIST, new HashSet<String>());
        return new HashSet<String>(pref.getStringSet(KEY_CLIENT_ID_LIST, new HashSet<String>()));
    }

}
