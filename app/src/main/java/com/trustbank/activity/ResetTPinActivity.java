package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONException;
import org.json.JSONObject;


public class ResetTPinActivity extends AppCompatActivity implements AlertDialogOkListener {

    private EditText etSecurityTpin, etnewPinId, etConfrimPINId;
    private Button btnActivation;
    private CoordinatorLayout coordinatorLayoutPin;
    private TrustMethods trustMethods;
    private TextView textSecurityHintId;
    private AlertDialogOkListener alertDialogOkListener = this;


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
                        TrustMethods.naviagteToSplashScreen(ResetTPinActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(ResetTPinActivity.this, false);
        setContentView(R.layout.activity_reset_t_pin);

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        trustMethods = new TrustMethods(ResetTPinActivity.this);
        etSecurityTpin = findViewById(R.id.etSecurityTpin);
        etnewPinId = findViewById(R.id.etnewPinId);
        etConfrimPINId = findViewById(R.id.etConfrimPINId);
        btnActivation = findViewById(R.id.btnActivationDone);
        coordinatorLayoutPin = findViewById(R.id.coordinatorLayoutPin);
        textSecurityHintId = findViewById(R.id.textSecurityHintId);

        textSecurityHintId.setText(AppConstants.getSecurityCodeHint());

        etSecurityTpin.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    etSecurityTpin.setText(s);
                    etSecurityTpin.setSelection(etSecurityTpin.length()); //fix reverse texting
                }
            }
        });

        btnActivation.setOnClickListener(view -> {

            TrustMethods.hideSoftKeyboard(ResetTPinActivity.this);
            if (TextUtils.isEmpty(etSecurityTpin.getText().toString())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_security_code), coordinatorLayoutPin);
            } else if (TextUtils.isEmpty(etnewPinId.getText().toString())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.hint_tpin), coordinatorLayoutPin);
            } else if (TextUtils.isEmpty(etConfrimPINId.getText().toString())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_confirm_tpin), coordinatorLayoutPin);
            } else if (!etnewPinId.getText().toString().equals(etConfrimPINId.getText().toString())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_mismatch_tpin), coordinatorLayoutPin);
            } else if (etnewPinId.getText().toString().length() != 4 || etConfrimPINId.getText().toString().length() != 4) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_tpin_lenght), coordinatorLayoutPin);
            } else {
                if (TrustMethods.isSimAvailable(getApplicationContext()) &&
                        TrustMethods.isSimVerified(ResetTPinActivity.this)) {
                    String strSecurityPin = etSecurityTpin.getText().toString();
                    String strPIN = etnewPinId.getText().toString();
                    String strConfirmPin = etConfrimPINId.getText().toString();
                    if (NetworkUtil.getConnectivityStatus(ResetTPinActivity.this)) {

                        new SecurityCodeAddValidationAsyncTask(this, strSecurityPin, AppConstants.getCLIENTID(), strPIN, strConfirmPin).execute();

                    } else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayoutPin);
                    }
                } else {
                    TrustMethods.displaySimErrorDialog(ResetTPinActivity.this);
                }
            }
        });
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
        TrustMethods.showBackButtonAlert(ResetTPinActivity.this);
    }

    @Override
    public void onDialogOk(int resultCode) {
        try {
            if (resultCode == 0) {
                Intent intent = new Intent(ResetTPinActivity.this, LockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                trustMethods.activityCloseAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        public SecurityCodeAddValidationAsyncTask(Context ctx, String securityCode, String clientId, String pin,
                                                  String confrmPin) {
            this.ctx = ctx;
            this.securityCode = securityCode;
            this.clientId = clientId;
            this.pin = pin;
            this.confrmPin = confrmPin;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ResetTPinActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {

            try {
                String url = TrustURL.getURLForFundTransferOwnAndNeft();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("for", AppConstants.getUSERMOBILENUMBER());
                jsonObject.put("clientid", clientId);
                jsonObject.put("security_code", securityCode);


                if (!url.equals("")) {
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
                        AlertDialogMethod.alertDialogOk(ResetTPinActivity.this, getResources().getString(R.string.error_session_expire),
                                "",
                                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayoutPin);
                    }

                } else {

                    Intent intent = new Intent(ResetTPinActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("checkTransferType", "ResetTPIn");
                    intent.putExtra("strSecurityPin", securityCode);
                    intent.putExtra("strPIN", pin);
                    intent.putExtra("strConfirmPin", confrmPin);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    trustMethods.activityOpenAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}