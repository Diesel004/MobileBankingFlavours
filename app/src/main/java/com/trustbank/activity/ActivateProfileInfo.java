package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.trustbank.R;
import com.trustbank.fragment.PrivacyPolicyFragmentDialog;
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
import butterknife.ButterKnife;

public class ActivateProfileInfo extends AppCompatActivity implements AlertDialogListener {
    private String TAG = ActivateProfileInfo.class.getSimpleName();
    private TrustMethods method;
    private CheckBox checkProfile;
    private Button btnProfileNextId;
    private EditText etFullName;
    private EditText etEmailId;
    private CoordinatorLayout coordinatorLayout;
    private AlertDialogListener alertDialogListener = this;
    private String operationType;
    private String mClientId;
    private TextView termsAndCondId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            if (savedInstanceState != null) {
                Object currentPID = String.valueOf(android.os.Process.myPid());
                // Check current PID with old PID
                if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                    // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                    TrustMethods.naviagteToSplashScreen(ActivateProfileInfo.this);
                }
            }
        }
        SetTheme.changeToTheme(ActivateProfileInfo.this, false);
        setContentView(R.layout.activity_activate_profile_info);
        ButterKnife.bind(this);
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

        Intent intent = getIntent();
        if (intent.hasExtra("operationType")) {
            operationType = intent.getStringExtra("operationType");
            if (operationType.equalsIgnoreCase("activateMpin")) {
                mClientId = AppConstants.getCLIENTID();
            } else {
                mClientId = AppConstants.getANOTHERCLIENTID();
            }
        }

        method = new TrustMethods(ActivateProfileInfo.this);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        etFullName = findViewById(R.id.etFullNameId);
        etEmailId = findViewById(R.id.etEmailIdId);
        checkProfile = findViewById(R.id.chechProfileId);
        termsAndCondId = findViewById(R.id.termsAndCondId);
        btnProfileNextId = findViewById(R.id.btnProfileNextId);
        if (!TextUtils.isEmpty(AppConstants.getANOTHERCUSTOMERNAME())) {
            etFullName.setText(AppConstants.getANOTHERCUSTOMERNAME());
        }
        registerEvent();
    }

    private void registerEvent() {
        btnProfileNextId.setOnClickListener(view -> {
            TrustMethods.hideSoftKeyboard(ActivateProfileInfo.this);
            if (TextUtils.isEmpty(etFullName.getText().toString())) {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.msg_enter_name), coordinatorLayout);
            } else {
                String fullName = etFullName.getText().toString().trim();
                String emailId = etEmailId.getText().toString().trim();
                if (!TextUtils.isEmpty(emailId)) {
                    boolean isValidEmail = TrustMethods.validateEmailAddress(emailId);
                    if (!isValidEmail) {
                        TrustMethods.showSnackBarMessage("Please enter valid email id", coordinatorLayout);
                        return;
                    }
                }
                if (checkProfile.isChecked()) {
                    if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(ActivateProfileInfo.this)) {
                        if (NetworkUtil.getConnectivityStatus(ActivateProfileInfo.this)) {
                            if (!TextUtils.isEmpty(AppConstants.getServerOtp())) {
                                new CreateProfileAsyncTask(ActivateProfileInfo.this, fullName, emailId, AppConstants.getUSERMOBILENUMBER()).execute();
                            } else {
                                TrustMethods.showSnackBarMessage("Entered Otp is invalid, please enter correct Otp", coordinatorLayout);
                            }
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                        }
                    } else {
                        TrustMethods.displaySimErrorDialog(ActivateProfileInfo.this);
                    }
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.msg_terms_conditions), coordinatorLayout);
                }

            }
        });


        termsAndCondId.setOnClickListener(v -> {
//            if (getPackageName().equals("com.trustbank.pucbmbank") || getPackageName().equalsIgnoreCase("com.trustbank.pdccbank")) {
                FragmentManager manager = getSupportFragmentManager();
                DialogFragment newFragment = PrivacyPolicyFragmentDialog.newInstance();
                newFragment.show(manager, "dialog");
//            }

        });
    }

    @SuppressLint("StaticFieldLeak")
    private class CreateProfileAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String mMobileNo;
        String mName;
        String mEmailId;
        String actionName = "CREATE_PROFILE";
        String result;

        public CreateProfileAsyncTask(Context ctx, String name, String emailId, String mobileNo) {
            this.ctx = ctx;
            this.mName = name;
            this.mEmailId = emailId;
            this.mMobileNo = mobileNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ActivateProfileInfo.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();

                String jsonString = "{\"name\":\"" + mName + "\",\"email\":\"" + mEmailId + "\", \"mobile_number\":\"" + mMobileNo + "\", \"otp\":\"" + AppConstants.getServerOtp() + "\", \"custid\":\"" + mClientId + "\"}";
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
                    response = "User Successfully Registered.";
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
                    TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                } else {
                    if (response != null) {
                        clearAllFields();
                        Intent intent = new Intent(ActivateProfileInfo.this, PinActivation.class);
                        intent.putExtra("operationType", operationType);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        method.activityOpenAnimation();
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearAllFields() {
        etFullName.setText("");
        etEmailId.setText("");
        checkProfile.setChecked(false);
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
    public void onBackPressed() {
        String message = getResources().getString(R.string.alert_back_message);
        AlertDialogMethod.alertDialog(ActivateProfileInfo.this, getResources().getString(R.string.alert_back_title),
                message, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no),
                1, false, alertDialogListener);
    }

    @Override
    public void onDialogOk(int resultCode) {
        if (operationType.equalsIgnoreCase("activateClientMpin")) {
            Intent intent = new Intent(ActivateProfileInfo.this, RegisterClientId.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            method.activityCloseAnimation();
        } else {
            Intent intent = new Intent(ActivateProfileInfo.this, VerifyMobileNumber.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            method.activityCloseAnimation();
        }

    }

    @Override
    public void onDialogCancel(int resultCode) {

    }
}
