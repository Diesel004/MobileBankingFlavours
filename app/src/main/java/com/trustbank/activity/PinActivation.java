package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONException;
import org.json.JSONObject;


public class PinActivation extends AppCompatActivity implements AlertDialogListener {

    private TrustMethods method;
    private EditText etSecurityCode;
    private EditText etMPin;
    private EditText etConfrimMPIN;
    private EditText etClientId;
    private Button btnActivation;
    private CoordinatorLayout coordinatorLayoutPin;
    private String TAG = PinActivation.class.getSimpleName();
    private AlertDialogListener alertDialogListener = this;
    private TextInputLayout textInputMpinId;
    private String mSecurityCode;
    private String operationType;
    private TextView textSecurityHintId;
    private String mClientId;
    private TextInputLayout textInputCustId;


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
                        TrustMethods.naviagteToSplashScreen(PinActivation.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(PinActivation.this, true);
        setContentView(R.layout.activity_pin_activation);
        initcomponent();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }


    private void initcomponent() {
        try {
            method = new TrustMethods(PinActivation.this);
            TextView textTitleId = findViewById(R.id.textTitleId);
            etSecurityCode = findViewById(R.id.etSecurityCodeId);
            etMPin = findViewById(R.id.etMPinId);
            textInputMpinId = findViewById(R.id.textInputMpinId);
            etConfrimMPIN = findViewById(R.id.etConfrimMPINId);
            btnActivation = findViewById(R.id.btnActivationDone);
            coordinatorLayoutPin = findViewById(R.id.coordinatorLayoutPin);
            textSecurityHintId = findViewById(R.id.textSecurityHintId);

            textInputCustId = findViewById(R.id.textInputCustId);
            etClientId = findViewById(R.id.etClientId);

            Intent intent = getIntent();
            if (intent.hasExtra("operationType")) {
                operationType = intent.getStringExtra("operationType");

                if (!TextUtils.isEmpty(operationType) && operationType.equalsIgnoreCase("activateMpin")) {
                    mClientId = AppConstants.getCLIENTID();
                } else if (!TextUtils.isEmpty(operationType) && operationType.equalsIgnoreCase("forgotMpin")) {
                    setTheme(R.style.AppTheme);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setHomeButtonEnabled(true);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setTitle(getResources().getString(R.string.title_forget_mpin));
                    }
                    textInputCustId.setVisibility(View.VISIBLE);
                } else if (!TextUtils.isEmpty(operationType) && operationType.equalsIgnoreCase("activateClientMpin")) {
                    setTheme(R.style.AppTheme);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setHomeButtonEnabled(true);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setTitle("Activate MPin");
                    }
                    mClientId = AppConstants.getANOTHERCLIENTID();
                }
            }

            if (!TextUtils.isEmpty(operationType)) {
                if (operationType.equalsIgnoreCase("activateMpin") || operationType.equalsIgnoreCase("activateClientMpin")) {
                    textTitleId.setText(getResources().getString(R.string.txt_activate_mpin));
                } else {
                    textTitleId.setText(getResources().getString(R.string.title_forget_mpin));
                    textInputMpinId.setHint(getResources().getString(R.string.title_new_mpin));
                }
            }
            textSecurityHintId.setText(AppConstants.getSecurityCodeHint());
            etSecurityCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                }

                @Override
                public void afterTextChanged(Editable et) {
                    String s = et.toString();
                    if (!s.equals(s.toUpperCase())) {
                        s = s.toUpperCase();
                        etSecurityCode.setText(s);
                        etSecurityCode.setSelection(etSecurityCode.length()); //fix reverse texting
                    }
                }
            });

            registerEvent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerEvent() {
        btnActivation.setOnClickListener(view -> {
            TrustMethods.hideSoftKeyboard(PinActivation.this);
            if (textInputCustId.getVisibility() == View.VISIBLE && TextUtils.isEmpty(etClientId.getText().toString())) {
                TrustMethods.showSnackBarMessage("Enter customer id", coordinatorLayoutPin);
            } else if (TextUtils.isEmpty(etSecurityCode.getText().toString().trim())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_security_code), coordinatorLayoutPin);
            } else if (TextUtils.isEmpty(etMPin.getText().toString())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.hint_mpin), coordinatorLayoutPin);
            } else if (TextUtils.isEmpty(etConfrimMPIN.getText().toString())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_confirm_mpin), coordinatorLayoutPin);
            } else if (!etMPin.getText().toString().equals(etConfrimMPIN.getText().toString())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_mismatch_mpin), coordinatorLayoutPin);
            } else if (etMPin.getText().toString().length() != 4 || etConfrimMPIN.getText().toString().length() != 4) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_mpin_lenght), coordinatorLayoutPin);
            } else {
                if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(PinActivation.this)) {
                    String strSecurityCode = etSecurityCode.getText().toString().trim();
                    String strMPIN = etMPin.getText().toString().trim();
                    String strConfirmPin = etConfrimMPIN.getText().toString().trim();
                    if (operationType.equalsIgnoreCase("activateMpin")) {
                        if (NetworkUtil.getConnectivityStatus(PinActivation.this)) {
                            new PinActivationAsyncTask(PinActivation.this, strSecurityCode, strMPIN,
                                    strConfirmPin, AppConstants.getUSERMOBILENUMBER()).execute();
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayoutPin);
                        }
                    } else if (operationType.equalsIgnoreCase("activateClientMpin")) {

                        if (NetworkUtil.getConnectivityStatus(PinActivation.this)) {
                            new PinActivationAsyncTask(PinActivation.this, strSecurityCode, strMPIN, strConfirmPin,
                                    AppConstants.getUSERMOBILENUMBER()).execute();
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayoutPin);
                        }
                    } else {
                        //forgot MPIN.....
                        mClientId = etClientId.getText().toString();
                        if (NetworkUtil.getConnectivityStatus(PinActivation.this)) {
                            new SecurityCodeAddValidationAsyncTask(this, strSecurityCode, strMPIN, strConfirmPin, mClientId).execute();
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayoutPin);
                        }

                    }

                } else {
                    TrustMethods.displaySimErrorDialog(PinActivation.this);
                }
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    private class PinActivationAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String mCnfmMpin;

        String mMpin;
        String mMobileNo;
        String result;
        String purposeCode = "MBANK_REG";

        public PinActivationAsyncTask(Context ctx, String securityCode, String mpin, String cnfmMpin, String mobileNo) {
            this.ctx = ctx;
            mSecurityCode = securityCode;
            this.mMpin = mpin;
            this.mCnfmMpin = cnfmMpin;
            this.mMobileNo = mobileNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PinActivation.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.GeneratePinUrl();

                String jsonString = "{\"pin_type\":\"" + "mpin" + "\",\"pin\":\"" + mMpin + "\",\"pin_confirmation\":\"" + mCnfmMpin + "\", \"mobile_number\":\"" + mMobileNo + "\", \"security_code\":\"" + mSecurityCode + "\", \"otp\":\"" + AppConstants.getServerOtp() + "\", \"custid\":\"" + mClientId + "\", \"otp_purpose_code\":\"" + purposeCode + "\"}";

                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);


                if (!url.equals("")) {
                    TrustMethods.LogMessage(TAG, "URL:-" + url);
                    result = HttpClientWrapper.postWithoutHeader(url, jsonString);
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
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "";

                if (responseCode.equals("1")) {
                    response = "MPin Successfully Generated.";
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
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (!this.error.equals("")) {
                clearAllFields();
                TrustMethods.showSnackBarMessage(this.error, coordinatorLayoutPin);
            } else {
                if (response != null) {
                    clearAllFields();
                    AlertDialogMethod.alertDialog(PinActivation.this, "", response, getResources().getString(R.string.btn_ok), "", 0, false, alertDialogListener);
                }
            }
        }
    }

    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 0) {
            Intent intent = new Intent(PinActivation.this, TPinActivateActivity.class);
            intent.putExtra("mSecurityCode", mSecurityCode);
            intent.putExtra("operationType", operationType);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            method.activityOpenAnimation();
            finish();
        } else if (resultCode == 1) {
            Intent intent = new Intent(PinActivation.this, VerifyMobileNumber.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            method.activityCloseAnimation();
        } else if (resultCode == 3) {
            Intent intent = new Intent(PinActivation.this, RegisterClientId.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            method.activityCloseAnimation();
            finish();
        } else if (resultCode == 4) {
            Intent intent = new Intent(PinActivation.this, LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            method.activityCloseAnimation();
        }

    }

    @Override
    public void onDialogCancel(int resultCode) {

    }

    public void clearAllFields() {
        etSecurityCode.setText("");
        etMPin.setText("");
        etConfrimMPIN.setText("");
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
    public void onBackPressed() {
        if (operationType.equalsIgnoreCase("activateMpin")) {
            String message = getResources().getString(R.string.alert_back_message);
            AlertDialogMethod.alertDialog(PinActivation.this, getResources().getString(R.string.alert_back_title), message, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), 1, false, alertDialogListener);
        } else if (operationType.equalsIgnoreCase("activateClientMpin")) {
            String message = getResources().getString(R.string.alert_back_message);
            AlertDialogMethod.alertDialog(PinActivation.this, getResources().getString(R.string.alert_back_title), message, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), 3, false, alertDialogListener);
        } else if (operationType.equalsIgnoreCase("forgotMpin")) {
            finish();
        } else {
            String message = getResources().getString(R.string.msg_back_button);
            AlertDialogMethod.alertDialog(PinActivation.this, getResources().getString(R.string.app_name), message, getResources().getString(R.string.btn_ok), "", 2, false, alertDialogListener);

        }
    }


    //check validation api call.
    @SuppressLint("StaticFieldLeak")
    private class SecurityCodeAddValidationAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse;
        ProgressDialog pDialog;
        String response;
        String actionName = "MBANK_SECURITYCODE_VALIDATE";
        String result;
        private String errorCode;
        private String pin, confrmPin;
        private String securityCode;
        private String clientId;

        public SecurityCodeAddValidationAsyncTask(Context ctx, String securityCode, String pin, String confrmPin,
                                                  String clientId) {
            this.ctx = ctx;
            this.securityCode = securityCode;
            this.clientId = clientId;
            this.pin = pin;
            this.confrmPin = confrmPin;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PinActivation.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {

            try {
                String url = TrustURL.MobileNoVerifyUrl();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("for", AppConstants.getUSERMOBILENUMBER());
                jsonObject.put("clientid", clientId);
                jsonObject.put("security_code", securityCode);


                if (!url.equals("")) {
//                    result = HttpClientWrapper.post(url, jsonObject.toString(), actionName);
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonObject.toString(), actionName, AppConstants.getAuth_token());

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
                    finalResponse = jsonResponse.has("response") ? jsonResponse.getString("response") : "NA";
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
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {

                        AlertDialogMethod.alertDialog(PinActivation.this, "", getResources().getString(R.string.error_session_expire), getResources().getString(R.string.btn_ok), "", 4, false, alertDialogListener);

                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayoutPin);
                    }

                } else {
                    //response..
                    Intent intent = new Intent(PinActivation.this, OtpVerificationActivity.class);
                    intent.putExtra("checkTransferType", "MPinActivation");
                    intent.putExtra("strSecurityPin", securityCode);
                    intent.putExtra("strPIN", pin);
                    intent.putExtra("strConfirmPin", confrmPin);
                    intent.putExtra("mClientId", clientId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    method.activityOpenAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
