package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.interfaces.OtpDetailsListener;
import com.trustbank.receiver.MySMSBroadcastReceiver;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.SharePreferenceUtils;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.trustbank.util.AppConstants.OTP_MESSAGE;
import static com.trustbank.util.AppConstants.isAutoReadOTPReadOnlyRegisterMobile;


public class RegisterClientId extends AppCompatActivity implements AlertDialogOkListener, OtpDetailsListener {

    private String TAG = RegisterClientId.class.getSimpleName();
    private TrustMethods method;
    private Button btnSendSms;
    private EditText etClientId;
    private LinearLayout otpLayout, mobileNoLayout;
    private EditText edVerifyOTP;
    private TextView tvTimer;
    public ProgressDialog pDialog;
    private CoordinatorLayout coordinatorLayout;
    private SharePreferenceUtils sharePreferenceUtils;
    private int time = 180;
    private AlertDialogOkListener alertDialogOkListener = this;
    private ProgressDialog otpDialog;
    private MySMSBroadcastReceiver smsBroadcastReceiver;
    private static final int RESOLVE_HINT = 1;  // Set to an unused request code
    public static final int SMS_CONSENT_REQUEST = 2;
    private String clientId;
    private OtpDetailsListener otpDetailsListener = this;
    boolean isReceiverRegister;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
                if (savedInstanceState != null) {
                    Object currentPID = String.valueOf(android.os.Process.myPid());
                    // Check current PID with old PID
                    if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                        // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                        TrustMethods.naviagteToSplashScreen(RegisterClientId.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(RegisterClientId.this, false);
        setContentView(R.layout.activity_register_client_id);

        initcomponent();
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }


    @SuppressLint("WrongConstant")
    @TargetApi(Build.VERSION_CODES.O)
    private void initcomponent() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        method = new TrustMethods(RegisterClientId.this);
        sharePreferenceUtils = new SharePreferenceUtils(getApplicationContext());
        etClientId = findViewById(R.id.etClientId);
        btnSendSms = findViewById(R.id.btnSendSms);
        otpLayout = findViewById(R.id.otpLayout);
        mobileNoLayout = findViewById(R.id.mobileNoLayout);
        edVerifyOTP = findViewById(R.id.edVerifyOTP);
        tvTimer = findViewById(R.id.tvTimer);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        otpLayout.setVisibility(View.GONE);
        mobileNoLayout.setVisibility(View.VISIBLE);

        registerBroadcast();
        registerEvent();
    }


    private void registerEvent() {
        try {
            btnSendSms.setOnClickListener(view -> {
                TrustMethods.hideSoftKeyboard(RegisterClientId.this);

                if (isAutoReadOTPReadOnlyRegisterMobile) {
                    edVerifyOTP.setClickable(false);
                    edVerifyOTP.setEnabled(false); //non editable
                }

                if (!TextUtils.isEmpty(etClientId.getText().toString().trim())) {
                    if (NetworkUtil.getConnectivityStatus(RegisterClientId.this)) {
                        new VerifyClientIdAsyncTask(RegisterClientId.this, etClientId.getText().toString().trim()).execute();
                    } else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                    }
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_client_id), coordinatorLayout);
                }
            });

            edVerifyOTP.setOnClickListener(v -> edVerifyOTP.setText(""));

            edVerifyOTP.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (Objects.requireNonNull(edVerifyOTP.getText()).toString().length() == 6) {

                            if (NetworkUtil.getConnectivityStatus(RegisterClientId.this)) {
                                new VerifyOtpAsyncTask(RegisterClientId.this, edVerifyOTP.getText().toString().trim(), clientId).execute();
                            } else {
                                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            tvTimer.setOnClickListener(v -> {
                try {
                    if (tvTimer.getText().equals("Resend Registration Code")) {
                        edVerifyOTP.setText("");
                        if (!TextUtils.isEmpty(etClientId.getText().toString().trim())) {
                            if (NetworkUtil.getConnectivityStatus(RegisterClientId.this)) {
                                new VerifyClientIdAsyncTask(RegisterClientId.this, etClientId.getText().toString().trim()).execute();
                            } else {
                                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                            }
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_client_id), coordinatorLayout);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setTimer(TextView tvTimer) {
        try {
            tvTimer.setText("");
            int autoReadTimeOutInMillisec = AppConstants.getAutoReadOtpTimeout();
            time = autoReadTimeOutInMillisec / 1000;
            new CountDownTimer(autoReadTimeOutInMillisec, 1000) {

                public void onTick(long millisUntilFinished) {
                    tvTimer.setText(checkDigit(time) + "s");
                    time--;
                }

                public void onFinish() {
                    tvTimer.setText("Resend Registration Code");
                    edVerifyOTP.setEnabled(true);
                    edVerifyOTP.setClickable(true);
//                autoReadProgressBar.setVisibility(View.GONE);
                    dismissProgressDialog();
                    if (smsBroadcastReceiver != null) {
                        if (isReceiverRegister) {
                            unregisterReceiver(smsBroadcastReceiver);
                            isReceiverRegister = false;
                        }

                    }
                    AppConstants.setServerOtp("");
                }

            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }


    @SuppressLint("StaticFieldLeak")
    private class VerifyClientIdAsyncTask extends AsyncTask<Void, Integer, String> {
        private String error = "";
        private Context ctx;
        private String response;
        private String actionName = "GET_CUSTOMER_INFO";
        private String result;
        private Handler handler;
        private String errorCode;

        public VerifyClientIdAsyncTask(Context ctx, String mClientId) {
            this.ctx = ctx;
            clientId = mClientId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterClientId.this);
            pDialog.setMax(45);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();
                String jsonString = "{\"custid\":\"" + clientId + "\"}";
                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonString, actionName, AppConstants.getAuth_token());
                }
                if (result == null || result.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResponse = (new JSONObject(result));
                if (jsonResponse.has("error")) {
                    error = jsonResponse.getString("error");
                    return error;
                }
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";

                if (responseCode.equals("1")) {
                    JSONObject jsonObject = jsonResponse.getJSONObject("response");
                    JSONArray jsonArray = jsonObject.getJSONArray("Table");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    String cust = jsonObject1.getString("customer_name");
                    AppConstants.setANOTHERCUSTOMERNAME(cust);
                    response = "Client id successfully verified.";
                } else {
                    errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
                    error = jsonResponse.has("error_message") ? jsonResponse.getString("error_message") : "NA";
                }
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
                pDialog.dismiss();
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(RegisterClientId.this, "Session Expired!", "",
                                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                } else {
                    if (response != null) {
                        new GenerateOtpAsyncTask(getApplicationContext(), clientId).execute();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    @SuppressLint("StaticFieldLeak")
    private class GenerateOtpAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "";
        private Context ctx;
        private String response;
        private ProgressDialog pDialog;
        private String actionName = "GENERATE_OTP_FOR_MBANK_REG";
        private String purposeCode = "MBANK_REG";
        private String result;
        private String clientId;
        private String mobileNumber;
        private String sms;

        public GenerateOtpAsyncTask(Context ctx, String clientId) {
            this.ctx = ctx;
            this.clientId = clientId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(RegisterClientId.this);
            pDialog.setMax(45);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.GenerateOtpFundTransferUrl();
                String jsonString = "{\"for\":\"" + AppConstants.getUSERMOBILENUMBER() + "\",\"custid\":\"" + clientId + "\",\"purpose_code\":\"" + purposeCode + "\"}";
                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);

                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonString, actionName, "");
                }
                if (result == null || result.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResponse = (new JSONObject(result));
                if (jsonResponse.has("error")) {
                    error = jsonResponse.getString("error");
                    return error;
                }
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";

                if (responseCode.equals("1")) {
                    JSONObject responseJsonObject = jsonResponse.getJSONObject("response");
                    if (responseJsonObject.has("error")) {
                        error = responseJsonObject.getString("error");
                        return error;
                    }
                    JSONObject otpJsonObject = responseJsonObject.getJSONObject("otp");
                    if (otpJsonObject.has("error")) {
                        error = otpJsonObject.getString("error");
                        return error;
                    }
                    sms = otpJsonObject.has("sms") ? otpJsonObject.getString("sms") : "";
                    mobileNumber = otpJsonObject.has("mobile_number") ? otpJsonObject.getString("mobile_number") : "";
                    response = otpJsonObject.has("otp_sent_msg") ? otpJsonObject.getString("otp_sent_msg") : "";

                } else {
                    String errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
                    error = jsonResponse.has("error_message") ? jsonResponse.getString("error_message") : "NA";
                }

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

                if (response != null) { //TODO
                    AlertDialogMethod.alertDialogOk(RegisterClientId.this, "", response, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    startSMSRetriever();
                } else {
                    AppConstants.setServerError(error);
                    TrustMethods.showSnackBarMessage(error, coordinatorLayout);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class VerifyOtpAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "";
        private Context ctx;
        private String response;
        private ProgressDialog pDialog;
        private String actionName = "VERIFY_OTP_FOR_REG";
        private String purposeCode = "MBANK_REG";
        private String result;
        private String mOtp;
        private String mClientId;

        public VerifyOtpAsyncTask(Context ctx, String otp, String mClientId) {
            this.ctx = ctx;
            this.mOtp = otp;
            this.mClientId = mClientId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterClientId.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();
                String jsonString = "{\"mobile_number\":\"" + AppConstants.getUSERMOBILENUMBER() + "\",\"otp\":\"" + mOtp + "\",\"purpose_code\":\"" + purposeCode + "\"}";
                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);
                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonString, actionName, "");
                }
                if (result == null || result.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResponse = (new JSONObject(result));
                if (jsonResponse.has("error")) {
                    error = jsonResponse.getString("error");
                    return error;
                }
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";
                if (responseCode.equals("1")) {
                    //response = jsonResponse.has("response") ? jsonResponse.getString("response") : "NA";
                    response = "success";
                } else {
                    String errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
                    error = jsonResponse.has("error_message") ? jsonResponse.getString("error_message") : "NA";
                    return error;
                }
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
                    if (error.equalsIgnoreCase("auth token expired.")) {
                        AlertDialogMethod.alertDialogOk(RegisterClientId.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                } else {
                    if (response != null && response.equals("success")) {
                        openMPinActivationActivity(mOtp, mClientId);
                    } else {
                        TrustMethods.showSnackBarMessage(error, coordinatorLayout);
                    }
                }
            } catch (Exception e) {
                TrustMethods.message(RegisterClientId.this, e.getMessage());
            }
        }
    }

    private void openMPinActivationActivity(String mOtp, String mClientId) {
        try {
            if (!TextUtils.isEmpty(mOtp)) {
                edVerifyOTP.setText("");
                etClientId.setText("");
                AppConstants.setServerOtp(mOtp);
                AppConstants.setANOTHERCLIENTID(mClientId);

                Intent moveToProfile = new Intent(RegisterClientId.this, ActivateProfileInfo.class);
                moveToProfile.putExtra("operationType", "activateClientMpin");
                moveToProfile.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(moveToProfile);
                method.activityOpenAnimation();
                otpLayout.setVisibility(View.GONE);
                mobileNoLayout.setVisibility(View.VISIBLE);

            } else {
                TrustMethods.showSnackBarMessage("Otp is incorrect please enter correct Otp and try again.", coordinatorLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (smsBroadcastReceiver != null) {
                if (isReceiverRegister) {
                    unregisterReceiver(smsBroadcastReceiver);
                    isReceiverRegister = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (Objects.requireNonNull(edVerifyOTP.getText()).toString().equals("") && Objects.requireNonNull(etClientId.getText().toString()).equals("") && AppConstants.getServerOtp().equals("")) {
                otpLayout.setVisibility(View.GONE);
                mobileNoLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogOk(int resultCode) {
        try {
            if (resultCode == 0) {
                Intent intentLogin = new Intent(getApplicationContext(), LockActivity.class);
                intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogin);
                method.activityCloseAnimation();
            } else if (resultCode == 1) {
                otpLayout.setVisibility(View.VISIBLE);
                mobileNoLayout.setVisibility(View.GONE);
                setTimer(tvTimer);
                if (isAutoReadOTPReadOnlyRegisterMobile) {
                    showProgressDialog();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(RegisterClientId.this);
    }


    private void showProgressDialog() {
        try {
            otpDialog = new ProgressDialog(RegisterClientId.this);
            otpDialog.setMax(45);
            otpDialog.setMessage(getResources().getString(R.string.loading_wait_auto_read));
            Objects.requireNonNull(otpDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            otpDialog.getWindow().setGravity(Gravity.BOTTOM);
            otpDialog.setIndeterminate(false);
            otpDialog.setCancelable(false);
            otpDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dismissProgressDialog() {
        try {
            if (otpDialog != null) {
                if (otpDialog.isShowing()) {
                    otpDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onOTPReceived(Intent intent, int requestCode) {
        try {
            if (isAutoReadOTPReadOnlyRegisterMobile) {
                if (requestCode == SMS_CONSENT_REQUEST) {
                    // Get SMS message content
                    String otp_message = intent.getStringExtra(OTP_MESSAGE);

                    if (!TextUtils.isEmpty(otp_message)) {
                        String[] otpMessage = otp_message.split(" ");
                        String code = otpMessage[1];
                        edVerifyOTP.setText(code);
                        dismissProgressDialog();
                    }
                    if (smsBroadcastReceiver != null) {
                        if (isReceiverRegister) {
                            unregisterReceiver(smsBroadcastReceiver);
                            isReceiverRegister = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOTPTimeOut() {
        try {
            if (isAutoReadOTPReadOnlyRegisterMobile) {
                if (smsBroadcastReceiver != null) {
                    if (isReceiverRegister) {
                        unregisterReceiver(smsBroadcastReceiver);
                        isReceiverRegister = false;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerBroadcast() {
        try {
            //        if (isAutoReadOTPEnabled) {
            if (isAutoReadOTPReadOnlyRegisterMobile) {
                smsBroadcastReceiver = new MySMSBroadcastReceiver();
                smsBroadcastReceiver.initOTPListener(otpDetailsListener);
                IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
                registerReceiver(smsBroadcastReceiver, intentFilter);
                isReceiverRegister = true;
            }
//        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSMSRetriever() {
        try {
            //        if (isAutoReadOTPEnabled) {
            if (isAutoReadOTPReadOnlyRegisterMobile) {
                SmsRetrieverClient client = SmsRetriever.getClient(RegisterClientId.this);
                Task<Void> task = client.startSmsRetriever();

                task.addOnSuccessListener(aVoid -> {
                    // Successfully started retriever, expect broadcast intent
                    // ...
                    if (task.isSuccessful() || task.isComplete()) {
                    }
                });
                task.addOnFailureListener(e -> Toast.makeText(RegisterClientId.this, "Failed OTP Retrieved", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}