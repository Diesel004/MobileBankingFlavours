package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.trustbank.R;
import com.trustbank.util.AppConstants;
import com.trustbank.util.AskPermissions;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SessionManager;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONException;
import org.json.JSONObject;

import static com.trustbank.util.MBank.loadAppLogo;

public class SplashScreenActivity extends AppCompatActivity {

    SessionManager session;
    private AskPermissions askPermissions;
    private ImageView ivAppLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTheme.changeToTheme(SplashScreenActivity.this, true);
        setContentView(R.layout.content_splash);

        ivAppLogo = findViewById(R.id.ivAppLogo);
        loadAppLogo(ivAppLogo);
        // Session class instance
        session = new SessionManager(SplashScreenActivity.this);

        if (NetworkUtil.getConnectivityStatus(SplashScreenActivity.this)) {
            new LoadHintsMessageAsyncTask(SplashScreenActivity.this).execute();
        } else {
            TrustMethods.message(SplashScreenActivity.this, getResources().getString(R.string.error_check_internet));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @SuppressLint("StaticFieldLeak")
    private class LoadHintsMessageAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String securityHint,play_store_validate,play_store_mobile,play_store_clientid;
        int autoReadOtpTimeout;
        private String errorCode = "";
        private String bankName;

        public LoadHintsMessageAsyncTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SplashScreenActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.SecurityHintUrl();
                if (!url.equals("")) {
                    response = HttpClientWrapper.getResponseWithoutAuth(url);
                }
                if (response == null || response.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResult = (new JSONObject(response));
                if (jsonResult.has("error")) {
                    error = jsonResult.getString("error");
                    return error;
                }

                JSONObject messageObject = (new JSONObject(jsonResult.getString("message")));
                if (messageObject.has("error")) {
                    error = messageObject.getString("error");
                    return error;
                }
                securityHint = messageObject.has("security_code_hint") ? messageObject.getString("security_code_hint") : "NA";
                play_store_validate = messageObject.has("play_store") ? messageObject.getString("play_store") : "0";
                play_store_mobile = messageObject.has("play_store_mobile") ? messageObject.getString("play_store_mobile") : "";
                play_store_clientid = messageObject.has("play_store_clientid") ? messageObject.getString("play_store_clientid") : "";
                autoReadOtpTimeout = messageObject.has("otp_auto_read_timeout_seconds") ? messageObject.getInt("otp_auto_read_timeout_seconds") : 120000;


            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return error;
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!this.error.equals("")) {
                    TrustMethods.message(SplashScreenActivity.this, error);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        askPermissions = new AskPermissions(SplashScreenActivity.this, session);
                        if (askPermissions.checkAndRequestPermissions()) {
                            threadCallSession();
                        }
                    } else {
                        threadCallSession();
                    }
                    AppConstants.setSecurityCodeHint(securityHint);
                    AppConstants.setAutoReadOtpTimeout(autoReadOtpTimeout);
                    AppConstants.setPlay_store_validate(play_store_validate);

                    AppConstants.setPlayStoreDemoUserMobile(play_store_mobile);
                    AppConstants.setPlayStoreDemoPasswordClientid(play_store_clientid);



                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void threadCallSession() {
        Thread background;
        background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 2.8 seconds
                    sleep(5000);
                    session.checkLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        /// start thread
        background.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        askPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
