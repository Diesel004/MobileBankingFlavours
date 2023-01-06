package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.trustbank.Model.GetUserProfileModal;
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

import java.util.ArrayList;
import java.util.List;


public class StopChequeRequestActivity extends AppCompatActivity implements AlertDialogOkListener, View.OnClickListener {

    private TrustMethods trustMethods;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private List<String> accountList;
    private AlertDialogOkListener alertDialogOkListener = this;
    private Spinner selectAccSpinner;
    private LinearLayout linearChequInqueryId;
    private Button btnSave;
    private CoordinatorLayout coordinatorLayout;
    private EditText etChequeNoId, etRemarkId;


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
                        TrustMethods.naviagteToSplashScreen(StopChequeRequestActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(StopChequeRequestActivity.this, false);
        setContentView(R.layout.activity_stop_cheque_request);
        init();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }

    private void init() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        trustMethods = new TrustMethods(StopChequeRequestActivity.this);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        selectAccSpinner = findViewById(R.id.selectAccSpinnerId);
        linearChequInqueryId = findViewById(R.id.linearChequInqueryId);
        etChequeNoId = findViewById(R.id.etChequeNoId);
        etRemarkId = findViewById(R.id.etRemarkId);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        accNumberSpinner();
    }

    private void accNumberSpinner() {
        try {
            accountsArrayList = trustMethods.getArrayList(StopChequeRequestActivity.this, "AccountListPref");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (accountsArrayList != null && accountsArrayList.size() > 0) {
            accountList = new ArrayList<>();
            accountList.add(0, "Select Account Number");
            for (int i = 0; i < accountsArrayList.size(); i++) {
                GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                if (TrustMethods.isAccountTypeValid(getUserProfileModal.getActType())) {
                    String accNo = getUserProfileModal.getAccNo();
                    String accTypeCode = getUserProfileModal.getAcTypeCode();
                    accountList.add(accNo + " - " + accTypeCode);
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(StopChequeRequestActivity.this, android.R.layout.simple_spinner_item, accountList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectAccSpinner.setAdapter(adapter);

            selectAccSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        linearChequInqueryId.setVisibility(View.GONE);
                        etChequeNoId.setText("");
                        etRemarkId.setText("");
                    } else {
                        linearChequInqueryId.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

    }

    @Override
    public void onDialogOk(int resultCode) {
        switch (resultCode) {
            case 0:
                Intent intentLogin = new Intent(getApplicationContext(), LockActivity.class);
                intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogin);
                trustMethods.activityCloseAnimation();
                break;

            case 1:
                selectAccSpinner.setSelection(0);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                TrustMethods.hideSoftKeyboard(StopChequeRequestActivity.this);
                if (selectAccSpinner.getSelectedItemPosition() == 0) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayout);
                } else if (TextUtils.isEmpty(etChequeNoId.getText().toString().trim())) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_invalid_cheque_no), coordinatorLayout);
                } else if (TextUtils.isEmpty(etRemarkId.getText().toString().trim())) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_enter_remark), coordinatorLayout);
                } else {
                    String accNo = TrustMethods.getValidAccountNo(selectAccSpinner.getSelectedItem().toString());
                    String chequeNo = etChequeNoId.getText().toString();
                    String remark = etRemarkId.getText().toString();
                    if (NetworkUtil.getConnectivityStatus(StopChequeRequestActivity.this)) {
                        new StopChequeRequestAsyncTask(StopChequeRequestActivity.this, accNo.trim(), chequeNo, remark).execute();
                    } else {
                        AlertDialogMethod.alertDialogOk(StopChequeRequestActivity.this, getResources().getString(R.string.error_check_internet), "", getResources().getString(R.string.btn_ok),
                                3, false, alertDialogOkListener);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        TrustMethods.showBackButtonAlert(StopChequeRequestActivity.this);
    }

    @SuppressLint("StaticFieldLeak")
    private class StopChequeRequestAsyncTask extends AsyncTask<Void, Void, String> {

        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String result;
        String accNo, chequeNo, remark;
        String actionName = "STOP_CHEQUE";
        private String errorCode;
        private String chequeStatus = "";


        public StopChequeRequestAsyncTask(Context ctx, String accNo, String chequeNo, String remark) {
            this.accNo = accNo;
            this.chequeNo = chequeNo;
            this.remark = remark;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StopChequeRequestActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                String url = TrustURL.MobileNoVerifyUrl();
                String jsonString = "{\"profile_id\":\"" + AppConstants.getProfileID() + "\", \"ac_no\":\"" + accNo + "\", \"chq_no\":\"" + chequeNo + "\", \"remarks\":\"" + remark + "\"}";
                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);

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
                    JSONObject responseObject = jsonResponse.getJSONObject("response");
                    if (responseObject.has("error")) {
                        error = responseObject.getString("error");
                        return error;
                    }

                    response = "Cheque stop successfully.";

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
                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(StopChequeRequestActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    }
                    if (!TextUtils.isEmpty(error) && TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(StopChequeRequestActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(StopChequeRequestActivity.this, getResources().getString(R.string.app_name), this.error, getResources().getString(R.string.btn_ok),
                                1, false, alertDialogOkListener);
                    }

                } else {
                    AlertDialogMethod.alertDialogOk(StopChequeRequestActivity.this, getResources().getString(R.string.app_name), this.response, getResources().getString(R.string.btn_ok),
                            1, false, alertDialogOkListener);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}