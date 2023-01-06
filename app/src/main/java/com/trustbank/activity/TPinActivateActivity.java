package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SessionManager;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public class TPinActivateActivity extends AppCompatActivity implements AlertDialogListener {

    private TrustMethods method;
    private EditText etSecurityCode;
    private EditText etTPin;
    private EditText etConfirmMTIN;
    private Button btnActivation;
    private CoordinatorLayout coordinatorLayoutPin;
    private SessionManager session;
    private String TAG = PinActivation.class.getSimpleName();
    private AlertDialogListener alertDialogListener = this;
    private String mSecurityCode, mSecurityCodeHInt, operationType;
    private String mClientId;

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
                        TrustMethods.naviagteToSplashScreen(TPinActivateActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(TPinActivateActivity.this, true);
        setContentView(R.layout.activity_verify_t_p_i_n);
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
        Intent intent = getIntent();
        if (intent.hasExtra("mSecurityCode")) {
            mSecurityCode = intent.getStringExtra("mSecurityCode");
            operationType = intent.getStringExtra("operationType");
            if (operationType.equalsIgnoreCase("activateMpin")) {
                mClientId = AppConstants.getCLIENTID();
            } else {
                mClientId = AppConstants.getANOTHERCLIENTID();
            }
        }
        method = new TrustMethods(TPinActivateActivity.this);
        session = new SessionManager(TPinActivateActivity.this);
        etSecurityCode = findViewById(R.id.etSecurityCodeId);
        etTPin = findViewById(R.id.etTPinId);
        etConfirmMTIN = findViewById(R.id.etConfrimTPINId);
        btnActivation = findViewById(R.id.btnActivationDone);
        coordinatorLayoutPin = findViewById(R.id.coordinatorLayoutPin);

        registerEvent();
    }

    private void registerEvent() {
        btnActivation.setOnClickListener(view -> {

            TrustMethods.hideSoftKeyboard(TPinActivateActivity.this);
            if (TextUtils.isEmpty(etTPin.getText().toString())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.hint_tpin), coordinatorLayoutPin);
            } else if (TextUtils.isEmpty(etConfirmMTIN.getText().toString())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_confirm_tpin), coordinatorLayoutPin);
            } else if (!etTPin.getText().toString().equals(etConfirmMTIN.getText().toString())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_mismatch_tpin), coordinatorLayoutPin);
            } else if (etTPin.getText().toString().length() != 4 || etConfirmMTIN.getText().toString().length() != 4) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_tpin_lenght), coordinatorLayoutPin);
            } else {
                if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(TPinActivateActivity.this)) {
                    String strTPIN = etTPin.getText().toString();
                    String strConfirmPin = etConfirmMTIN.getText().toString();
                    if (NetworkUtil.getConnectivityStatus(TPinActivateActivity.this)) {
                        new TPinActivationAsyncTask(TPinActivateActivity.this, strTPIN, strConfirmPin, AppConstants.getUSERMOBILENUMBER()).execute();
                    } else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayoutPin);
                    }
                } else {
                    TrustMethods.displaySimErrorDialog(TPinActivateActivity.this);
                }
            }
        });
    }


    public void clearAllFields() {
        etTPin.setText("");
        etConfirmMTIN.setText("");
    }

    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 0) {
            Intent intent = new Intent(TPinActivateActivity.this, LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            method.activityOpenAnimation();
        } else if (resultCode == 1) {
            Intent intent = new Intent(TPinActivateActivity.this, VerifyMobileNumber.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            method.activityCloseAnimation();
        } else if (resultCode == 3) {
            Intent intent = new Intent(TPinActivateActivity.this, RegisterClientId.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            method.activityCloseAnimation();
            finish();
        }

    }

    @Override
    public void onDialogCancel(int resultCode) {

    }


    private class TPinActivationAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String cnfmTpin;
        String tpin;
        String mMobileNo;
        String result;
        String purposeCode = "MBANK_REG";

        public TPinActivationAsyncTask(Context ctx, String tpin, String cnfmTpin, String mobileNo) {
            this.ctx = ctx;
            this.tpin = tpin;
            this.cnfmTpin = cnfmTpin;
            this.mMobileNo = mobileNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TPinActivateActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.GeneratePinUrl();

                String jsonString = "{\"pin_type\":\"" + "tpin" + "\",\"pin\":\"" + tpin + "\",\"pin_confirmation\":\"" + cnfmTpin + "\", \"mobile_number\":\"" + mMobileNo + "\", \"security_code\":\"" + mSecurityCode + "\", \"otp\":\"" + AppConstants.getServerOtp() + "\", \"custid\":\"" + mClientId + "\", \"otp_purpose_code\":\"" + purposeCode + "\"}";

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
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";

                if (responseCode.equals("1")) {
                    response = "TPin Successfully Generated.";
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

                if (!this.error.equals("")) {
                    clearAllFields();
                    TrustMethods.showSnackBarMessage(this.error, coordinatorLayoutPin);
                } else {
                    if (response != null) {
                        String clientId = "";
                        if (operationType.equalsIgnoreCase("activateClientMpin")) {
                            clientId = AppConstants.getANOTHERCLIENTID();
                            Set<String> mySetClientList = session.getClientListIds();
                            if (mySetClientList != null){
                                mySetClientList.add(clientId);
                            }else {
                                mySetClientList = new HashSet<>();
                                mySetClientList.add(clientId);
                            }
                            session.storeclientList(mySetClientList);
                        } else {
                            clientId = AppConstants.getCLIENTID();
                            Set<String> mySet = new HashSet<>();
                            mySet.add(clientId);
                            session.storeclientList(mySet);
                        }
                        session.createLoginSession(AppConstants.getUSERMOBILENUMBER(), clientId);
                        clearAllFields();
                        AppConstants.setANOTHERCLIENTID("");
                        AlertDialogMethod.alertDialog(TPinActivateActivity.this, "", response, getResources().getString(R.string.btn_ok), "", 0, false, alertDialogListener);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (operationType.equalsIgnoreCase("activateMpin")) {
            String message = getResources().getString(R.string.alert_back_message);
            AlertDialogMethod.alertDialog(TPinActivateActivity.this, getResources().getString(R.string.alert_back_title), message, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), 1, false, alertDialogListener);
        } else if (operationType.equalsIgnoreCase("activateClientMpin")) {
            String message = getResources().getString(R.string.alert_back_message);
            AlertDialogMethod.alertDialog(TPinActivateActivity.this, getResources().getString(R.string.alert_back_title), message, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), 3, false, alertDialogListener);
        } else {
            String message = getResources().getString(R.string.msg_back_button);
            AlertDialogMethod.alertDialog(TPinActivateActivity.this, getResources().getString(R.string.app_name), message, getResources().getString(R.string.btn_ok), "", 2, false, alertDialogListener);

        }
    }
}