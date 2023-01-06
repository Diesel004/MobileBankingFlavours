package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
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
import com.trustbank.util.JetPackSecurePreference;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.SharePreferenceUtils;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Objects;

import static com.trustbank.util.AppConstants.OTP_MESSAGE;
import static com.trustbank.util.AppConstants.isAutoReadOTPReadOnlyRegisterMobile;

public class VerifyMobileNumber extends AppCompatActivity implements AlertDialogOkListener, OtpDetailsListener {

    private String TAG = VerifyMobileNumber.class.getSimpleName();
    private TrustMethods method;
    private Button btnSendSms;
    private EditText edtMobileNo, etClientId;
    private LinearLayout otpLayout, mobileNoLayout;
    private EditText edVerifyOTP;
    private TextView tvTimer;
    private TextView txtSimSlotCondn;
    private TextView txtTitleId;
    public ProgressDialog pDialog;
    private CoordinatorLayout coordinatorLayout;
    private SharePreferenceUtils sharePreferenceUtils;
    private int time = 180;
    private AlertDialogOkListener alertDialogOkListener = this;
    private ProgressDialog otpDialog;
    private MySMSBroadcastReceiver smsBroadcastReceiver;
    private OtpDetailsListener otpDetailsListener = this;
    private static final int RESOLVE_HINT = 1;  // Set to an unused request code
    public static final int SMS_CONSENT_REQUEST = 2;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTheme.changeToTheme(VerifyMobileNumber.this, true);
        setContentView(R.layout.activity_verify_mobile_number);

        requestHint();
        initcomponent();
       // JetPackSecurePreference.storeSecretKey(this);
    }

    private void requestHint() {
//        if (isAutoReadOTPEnabled) {
        if (isAutoReadOTPReadOnlyRegisterMobile) {
            try {
                HintRequest hintRequest = new HintRequest.Builder().setPhoneNumberIdentifierSupported(true).build();
                PendingIntent intent = Credentials.getClient(this).getHintPickerIntent(hintRequest);
                startIntentSenderForResult(intent.getIntentSender(), RESOLVE_HINT, null, 0, 0, 0, new Bundle());

            } catch (IntentSender.SendIntentException e) {
                Toast.makeText(method, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        }
    }

    @SuppressLint("WrongConstant")
    @TargetApi(Build.VERSION_CODES.O)
    private void initcomponent() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        method = new TrustMethods(VerifyMobileNumber.this);
        sharePreferenceUtils = new SharePreferenceUtils(getApplicationContext());
        edtMobileNo = findViewById(R.id.edtMobileNoId);
        btnSendSms = findViewById(R.id.btnSendSms);
        otpLayout = findViewById(R.id.otpLayout);
        mobileNoLayout = findViewById(R.id.mobileNoLayout);
        etClientId = findViewById(R.id.etClientId);
        edVerifyOTP = findViewById(R.id.edVerifyOTP);
        tvTimer = findViewById(R.id.tvTimer);
        txtSimSlotCondn = findViewById(R.id.txtSimSlotCondnId);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        txtTitleId = findViewById(R.id.txtTitleId);
//        autoReadProgressBar = findViewById(R.id.autoReadProgressBarId);

        otpLayout.setVisibility(View.GONE);
        mobileNoLayout.setVisibility(View.VISIBLE);
        txtTitleId.setText(getResources().getString(R.string.txt_verify_mob_no));
        if (isAutoReadOTPReadOnlyRegisterMobile) {
            edVerifyOTP.setClickable(false);
            edVerifyOTP.setEnabled(false); //non editable
        }
        registerEvent();
        registerBroadcast();
    }

    public void registerBroadcast() {
//        if (isAutoReadOTPEnabled) {
        if (isAutoReadOTPReadOnlyRegisterMobile) {
            smsBroadcastReceiver = new MySMSBroadcastReceiver();
            smsBroadcastReceiver.initOTPListener(otpDetailsListener);
            IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
            registerReceiver(smsBroadcastReceiver, intentFilter);
        }
//        }
    }

    private void registerEvent() {
        btnSendSms.setOnClickListener(view -> {

            TrustMethods.hideSoftKeyboard(VerifyMobileNumber.this);


            if (!TextUtils.isEmpty(etClientId.getText().toString().trim())) {
                if (!TextUtils.isEmpty(edtMobileNo.getText().toString().trim())) {
                    String mobileNo = edtMobileNo.getText().toString().trim();
                    if (mobileNo.length() == 10) {
                        if (NetworkUtil.getConnectivityStatus(VerifyMobileNumber.this)) {

                            String mClientId = etClientId.getText().toString().trim();
                            new VerifyMobileNumberAsyncTask(VerifyMobileNumber.this, mobileNo, mClientId).execute();
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                        }

                    } else {
                        TrustMethods.showSnackBarMessage("Please enter 10 digit mobile number.", coordinatorLayout);
                    }
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_mobile_no), coordinatorLayout);
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
                if (Objects.requireNonNull(edVerifyOTP.getText()).toString().length() == 6) {
                    //openProfileActivity();//Comment
                    //Uncomment Below Code For Verify OTP
                    if (NetworkUtil.getConnectivityStatus(VerifyMobileNumber.this)) {
                        new VerifyOtpAsyncTask(VerifyMobileNumber.this, edVerifyOTP.getText().toString().trim()).execute();
                    } else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                    }
                }
            }
        });

        tvTimer.setOnClickListener(v -> {
            if (tvTimer.getText().equals("Resend Registration Code")) {
                registerBroadcast();
                edVerifyOTP.setText("");
                if (!TextUtils.isEmpty(edtMobileNo.getText().toString().trim())) {
                    if (!TextUtils.isEmpty(etClientId.getText().toString().trim())) {
                        if (NetworkUtil.getConnectivityStatus(VerifyMobileNumber.this)) {
                            String mobileNo = edtMobileNo.getText().toString().trim();
                            String clientId = etClientId.getText().toString().trim();
                            new VerifyMobileNumberAsyncTask(VerifyMobileNumber.this, mobileNo, clientId).execute();
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                        }
                    } else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_client_id), coordinatorLayout);
                    }
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_mobile_no), coordinatorLayout);
                }

            }
        });

    }

    private void setTimer(TextView tvTimer) {
        tvTimer.setText("");
        int autoReadTimeOutInMillisec = AppConstants.getAutoReadOtpTimeout();
        time = autoReadTimeOutInMillisec / 1000;
        new CountDownTimer(autoReadTimeOutInMillisec, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimer.setText(checkDigit(time) + "s");
                time--;
            }

            public void onFinish() {
                try {
                    tvTimer.setText("Resend Registration Code");
                    edVerifyOTP.setEnabled(true);
                    edVerifyOTP.setClickable(true);
//                autoReadProgressBar.setVisibility(View.GONE);
                    dismissProgressDialog();
                    if (smsBroadcastReceiver != null) {
                        unregisterReceiver(smsBroadcastReceiver);
                    }

                    AppConstants.setServerOtp("");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    @SuppressLint("StaticFieldLeak")
    private class VerifyMobileNumberAsyncTask extends AsyncTask<Void, Integer, String> {
        String error = "";
        Context ctx;
        String response;
        String mMobileNo, mClientId;
        String actionName = "VERIFY_MOBILE_NUMBER";
        String result;
        private Handler handler;

        public VerifyMobileNumberAsyncTask(Context ctx, String mobileNo, String mClientId) {
            this.ctx = ctx;
            this.mMobileNo = mobileNo;
            this.mClientId = mClientId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerifyMobileNumber.this);
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

                String jsonString = "{\"mobile_number\":\"" + mMobileNo + "\",\"custid\":\"" + mClientId + "\"}";
                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);

                if (!url.equals("")) {
                    TrustMethods.LogMessage(TAG, "URL:-" + url);
                    result = HttpClientWrapper.post(url, jsonString, actionName);
                    TrustMethods.LogMessage(TAG, "Frm Enquiry details response-->" + response);
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
                    response = "Mobile Number Successfully Verified.";
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

            pDialog.dismiss();
            if (mMobileNo.equalsIgnoreCase(AppConstants.getPlayStoreDemoUserMobile()) &&
                    mClientId.equalsIgnoreCase(AppConstants.getPlayStoreDemoPasswordClientid())) {

                AppConstants.setCLIENTID(mClientId);
                AppConstants.setUSERMOBILENUMBER(mMobileNo);
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                method.activityOpenAnimation();

            } else {
                if (!this.error.equals("")) {
                    TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                } else {
                    // check only for play store authenticate app.
                    if ((!TextUtils.isEmpty(AppConstants.getPlay_store_validate())) &&
                            AppConstants.getPlay_store_validate().equalsIgnoreCase("1")) {
                        AppConstants.setCLIENTID(mClientId);
                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        method.activityOpenAnimation();
                    } else {
                        if (response != null) {
                            new GenerateOtpAsyncTask(getApplicationContext(), mMobileNo, mClientId).execute();
                        }
                    }

                }
            }

        }
    }


    @SuppressLint("StaticFieldLeak")
    private class GenerateOtpAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String actionName = "GENERATE_OTP_FOR_MBANK_REG";
        String purposeCode = "MBANK_REG";
        String result;
        String mMobileNo, mClientId;
        private String mobileNumber;
        private String sms;

        public GenerateOtpAsyncTask(Context ctx, String mobileNo, String mClientId) {
            this.ctx = ctx;
            this.mMobileNo = mobileNo;
            this.mClientId = mClientId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(VerifyMobileNumber.this);
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
                String jsonString = "{\"for\":\"" + mMobileNo + "\",\"custid\":\"" + mClientId + "\",\"purpose_code\":\"" + purposeCode + "\"}";
//                String jsonString = "{\"for\":\"" + mMobileNo + "\",\"purpose_code\":\"" + purposeCode + "\"}";
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

                if (response != null) {

                    AppConstants.setUSERMOBILENUMBER(mMobileNo);
                    AppConstants.setCLIENTID(mClientId);
                    startSMSRetriever();
                    AlertDialogMethod.alertDialogOk(VerifyMobileNumber.this, "", response, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);

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
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String actionName = "VERIFY_OTP_FOR_REG";
        String purposeCode = "MBANK_REG";
        String result;
        String mOtp;

        public VerifyOtpAsyncTask(Context ctx, String otp) {
            this.ctx = ctx;
            this.mOtp = otp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VerifyMobileNumber.this);
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
                        AlertDialogMethod.alertDialogOk(VerifyMobileNumber.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                } else {
                    if (response != null && response.equals("success")) {
                        openProfileActivity(mOtp);
                    } else {
                        TrustMethods.showSnackBarMessage(error, coordinatorLayout);
                    }
                }
            } catch (Exception e) {
                TrustMethods.message(VerifyMobileNumber.this, e.getMessage());
            }
        }
    }

    private void openProfileActivity(String mOtp) {
        if (!TextUtils.isEmpty(mOtp)) {
            edVerifyOTP.setText("");
            edtMobileNo.setText("");
            etClientId.setText("");
            AppConstants.setServerOtp(mOtp);
            Intent moveToProfile = new Intent(VerifyMobileNumber.this, ActivateProfileInfo.class);
            moveToProfile.putExtra("operationType", "activateMpin");
            moveToProfile.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(moveToProfile);
            method.activityOpenAnimation();
        } else {
            TrustMethods.showSnackBarMessage("Otp is incorrect please enter correct Otp and try again.", coordinatorLayout);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (smsBroadcastReceiver != null) {
                unregisterReceiver(smsBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
//                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Objects.requireNonNull(edVerifyOTP.getText()).toString().equals("") && Objects.requireNonNull(edtMobileNo.getText().toString()).equals("") && AppConstants.getServerOtp().equals("")) {
            otpLayout.setVisibility(View.GONE);
            mobileNoLayout.setVisibility(View.VISIBLE);
            txtTitleId.setText(getResources().getString(R.string.txt_verify_mob_no));
        }
    }

    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 0) {
            Intent intentLogin = new Intent(getApplicationContext(), VerifyMobileNumber.class);
            intentLogin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentLogin);
            method.activityCloseAnimation();
        } else if (resultCode == 1) {
            //startSMSRetriever();
            otpLayout.setVisibility(View.VISIBLE);
            txtTitleId.setText(getResources().getString(R.string.txt_registration));
            mobileNoLayout.setVisibility(View.GONE);
            setTimer(tvTimer);
            if (isAutoReadOTPReadOnlyRegisterMobile) {
                showProgressDialog();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (isAutoReadOTPEnabled) {
        if (isAutoReadOTPReadOnlyRegisterMobile) {
            if (requestCode == RESOLVE_HINT) {
                if (resultCode == RESULT_OK) {
                    Credential credential = Objects.requireNonNull(data).getParcelableExtra(Credential.EXTRA_KEY);
                    edtMobileNo.setText(credential.getId().substring(3).trim());
                    //startSMSRetriever();
                }
            }
        }
    }

    @Override
    public void onOTPReceived(Intent intent, int requestCode) {
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
                    unregisterReceiver(smsBroadcastReceiver);
                }// send one time code to the server
            }
        }
    }

    @Override
    public void onOTPTimeOut() {
//        if (isAutoReadOTPEnabled) {
        if (isAutoReadOTPReadOnlyRegisterMobile) {
            if (smsBroadcastReceiver != null) {
                unregisterReceiver(smsBroadcastReceiver);
            }
        }
    }

    private void startSMSRetriever() {

//        if (isAutoReadOTPEnabled) {
        if (isAutoReadOTPReadOnlyRegisterMobile) {

            SmsRetrieverClient client = SmsRetriever.getClient(VerifyMobileNumber.this);
            Task<Void> task = client.startSmsRetriever();

            task.addOnSuccessListener(aVoid -> {
                if (task.isSuccessful() || task.isComplete()) {

                }
            });
            task.addOnFailureListener(e -> Toast.makeText(VerifyMobileNumber.this, "Failed OTP Retrieved", Toast.LENGTH_SHORT).show());
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showProgressDialog() {
        otpDialog = new ProgressDialog(VerifyMobileNumber.this);
        otpDialog.setMax(45);
        otpDialog.setMessage(getResources().getString(R.string.loading_wait_auto_read));
        Objects.requireNonNull(otpDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        otpDialog.getWindow().setGravity(Gravity.BOTTOM);
        otpDialog.setIndeterminate(false);
        otpDialog.setCancelable(false);
        otpDialog.show();
    }

    private void dismissProgressDialog() {
        if (otpDialog != null) {
            if (otpDialog.isShowing()) {
                otpDialog.dismiss();
            }
        }

    }
}