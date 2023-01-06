package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.DecimalDigitsInputFilter;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PPSRequestActivity extends AppCompatActivity implements AlertDialogOkListener, View.OnClickListener {

    private TrustMethods trustMethods;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private List<String> accountList;
    private AlertDialogOkListener alertDialogOkListener = this;
    private Spinner selectAccSpinner;
    private LinearLayout formLayoutId;
    private Button btnSave;
    private CoordinatorLayout coordinatorLayout;
    private EditText etChequeDateId, etAmountId, etPayeeNameId, etChequeNod;


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
                        TrustMethods.naviagteToSplashScreen(PPSRequestActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(PPSRequestActivity.this, false);
        setContentView(R.layout.activity_pps_request);
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

        trustMethods = new TrustMethods(PPSRequestActivity.this);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        selectAccSpinner = findViewById(R.id.spinnerFrmAct);
        formLayoutId = findViewById(R.id.formLayoutId);
        etChequeNod = findViewById(R.id.etChequeNod);
        etChequeDateId = findViewById(R.id.etChequeDateId);
        etAmountId = findViewById(R.id.etAmountId);
        etPayeeNameId = findViewById(R.id.etPayeeNameId);
        btnSave = findViewById(R.id.btnSaveId);
        btnSave.setOnClickListener(this);
        etChequeDateId.setOnClickListener(this);
        accNumberSpinner();
        etAmountId.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
        etAmountId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String s1 = s.toString();
                    if (s1.length() == 1 && s1.equalsIgnoreCase("0")) {
                        etAmountId.setText("");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void accNumberSpinner() {
        try {
            accountsArrayList = trustMethods.getArrayList(PPSRequestActivity.this, "AccountListPref");

            accountList = new ArrayList<>();
            accountList.add(0, "Select Account Number");
            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    if (TrustMethods.isAccountTypeValid(getUserProfileModal.getActType())) {
                        String accNo = getUserProfileModal.getAccNo();
                        String accTypeCode = getUserProfileModal.getAcTypeCode();
                        accountList.add(accNo + " - " + accTypeCode);
                    }
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(PPSRequestActivity.this, android.R.layout.simple_spinner_item, accountList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectAccSpinner.setAdapter(adapter);

            selectAccSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        formLayoutId.setVisibility(View.GONE);
                        etChequeDateId.setText("");
                        etAmountId.setText("");
                        etPayeeNameId.setText("");
                        etChequeNod.setText("");
                    } else {
                        formLayoutId.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
            case R.id.btnSaveId:
                TrustMethods.hideSoftKeyboard(PPSRequestActivity.this);
                if (selectAccSpinner.getSelectedItemPosition() == 0) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayout);
                } else if (TextUtils.isEmpty(etChequeNod.getText().toString().trim())) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_invalid_cheque_no), coordinatorLayout);
                } else if (TextUtils.isEmpty(etChequeDateId.getText().toString().trim())) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_invalid_cheque_date), coordinatorLayout);
                } else if (TextUtils.isEmpty(etAmountId.getText().toString().trim())) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_amt), coordinatorLayout);
                } else if (TextUtils.isEmpty(etPayeeNameId.getText().toString().trim())) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_enter_payee_name), coordinatorLayout);
                } else if (TrustMethods.isAmoutLessThanZero(etAmountId.getText().toString().trim())) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_valid_amt), coordinatorLayout);
                } else {
                    String mAccNo = TrustMethods.getValidAccountNo(selectAccSpinner.getSelectedItem().toString());
                    String mChequeNo = etChequeNod.getText().toString();
                    String mAmount = etAmountId.getText().toString();
                    String mDate = etChequeDateId.getText().toString();
                    String mPayeeName = etPayeeNameId.getText().toString();


                    if (NetworkUtil.getConnectivityStatus(PPSRequestActivity.this)) {
                        new PPSRequestAsyncTask(PPSRequestActivity.this, mAccNo, mChequeNo,
                                mAmount, mDate, mPayeeName).execute();
                    } else {
                        AlertDialogMethod.alertDialogOk(PPSRequestActivity.this, getResources().getString(R.string.error_check_internet), "", getResources().getString(R.string.btn_ok),
                                3, false, alertDialogOkListener);
                    }
                }
                break;

            case R.id.etChequeDateId:
                trustMethods.datePicker(PPSRequestActivity.this, etChequeDateId);
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
        TrustMethods.showBackButtonAlert(PPSRequestActivity.this);
    }

    @SuppressLint("StaticFieldLeak")
    private class PPSRequestAsyncTask extends AsyncTask<Void, Void, String> {

        private String error = "";
        private Context ctx;
        private String response;
        private ProgressDialog pDialog;
        private String result;
        private String accNo, chequeNo, mAmount, date, payeeName;
        private String actionName = "PPS_SAVE_REQUEST";
        private String errorCode;
        private String chequeStatus = "";


        public PPSRequestAsyncTask(Context ctx, String accNo, String chequeNo, String mAmount, String date, String payeeName) {
            this.accNo = accNo;
            this.chequeNo = chequeNo;
            this.mAmount = mAmount;
            this.date = date;
            this.payeeName = payeeName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PPSRequestActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                String url = TrustURL.MobileNoVerifyUrl();
                String jsonString = "{\"account_number\":\"" + accNo + "\", \"cheque_no\":\"" + chequeNo + "\", \"cheque_date\":\"" + date + "\", \"amount\":\"" + mAmount + "\", \"payee_name\":\"" + payeeName + "\"}";
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

                    JSONArray jsonArray = responseObject.getJSONArray("account");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    response = jsonObject.getString("msg");


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
                        AlertDialogMethod.alertDialogOk(PPSRequestActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    }
                    if (!TextUtils.isEmpty(error) && TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(PPSRequestActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(PPSRequestActivity.this, getResources().getString(R.string.lbl_pps_request), this.error, getResources().getString(R.string.btn_ok),
                                2, false, alertDialogOkListener);
                    }

                } else {
                    AlertDialogMethod.alertDialogOk(PPSRequestActivity.this, getResources().getString(R.string.lbl_pps_request), this.response, getResources().getString(R.string.btn_ok),
                            1, false, alertDialogOkListener);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}