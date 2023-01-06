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
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.trustbank.Model.FundTransferSubModel;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelfTransferToAccountActivity extends AppCompatActivity implements View.OnClickListener, AlertDialogOkListener {

    private EditText etAmount;
    private EditText etRemarks;
    private Spinner spinnerFrmAct;
    private Spinner spinnerToAccount;
    private LinearLayout setFundTransferLayout;
    private LinearLayout formLayoutFundTransfer;
    private CoordinatorLayout coordinatorLayout;
    private TrustMethods trustMethods;
    private String accountNo, toAccountNo;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    AlertDialogOkListener alertDialogOkListener = this;

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
                        TrustMethods.naviagteToSplashScreen(SelfTransferToAccountActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(SelfTransferToAccountActivity.this, false);
        setContentView(R.layout.activity_self_transfer_to_account);
        inIt();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }

    private void inIt() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            trustMethods = new TrustMethods(SelfTransferToAccountActivity.this);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            spinnerFrmAct = findViewById(R.id.spinnerFrmAct);
            spinnerToAccount = findViewById(R.id.spinnerTAccount);

            etAmount = findViewById(R.id.etAmountId);
            etRemarks = findViewById(R.id.etRemarks);
            Button btnWithBankNext = findViewById(R.id.btnWithBankNextId);
            setFundTransferLayout = findViewById(R.id.setFundTransferLayoutId);
            formLayoutFundTransfer = findViewById(R.id.formLayoutFundTransferId);
            formLayoutFundTransfer.setVisibility(View.GONE);
            btnWithBankNext.setOnClickListener(this);
            setAccountNoSpinner();
            getSpinnerData();
            etAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

            etAmount.addTextChangedListener(new TextWatcher() {
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
                            etAmount.setText("");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnWithBankNextId:
                try {
                    TrustMethods.hideSoftKeyboard(SelfTransferToAccountActivity.this);
                    if (spinnerFrmAct.getSelectedItem().equals("Select Account Number")) {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayout);
                    } else if (spinnerToAccount.getSelectedItem().equals("Select To A/C Number")) {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_to_acc_no), coordinatorLayout);
                    } else if (TextUtils.isEmpty(etAmount.getText().toString())) {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_amt), coordinatorLayout);
                    }else if (TrustMethods.isAmoutLessThanZero(etAmount.getText().toString().trim())) {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_valid_amt), coordinatorLayout);
                    } else {

                        String amt = etAmount.getText().toString().trim();
                        String remark = etRemarks.getText().toString().trim();
                        String toAccNo = spinnerToAccount.getSelectedItem().toString().trim();
                        toAccountNo = TrustMethods.getValidAccountNo(toAccNo);

                        List<FundTransferSubModel> fundTransferSubModalList = new ArrayList<>();
                        FundTransferSubModel fundTransferSubModel = new FundTransferSubModel();
                        fundTransferSubModel.setAccNo(accountNo.trim());
                        fundTransferSubModel.setToAccNo(toAccountNo.trim());
                        fundTransferSubModel.setAmt(amt);
                        fundTransferSubModel.setRemark(remark);
                        fundTransferSubModalList.add(fundTransferSubModel);

                        checkAmountValidation(amt, accountNo, fundTransferSubModalList);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setAccountNoSpinner() {
        try {
            accountsArrayList = trustMethods.getArrayList(SelfTransferToAccountActivity.this, "AccountListPref");
            List<String> accountList = new ArrayList<>();
            accountList.add(0, "Select Account Number");
            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    if (getPackageName().equals("com.trustbank.shivajibank")) {
                        if (TrustMethods.isAccountTypeValidRD(getUserProfileModal.getActType())) {
                            String accNo = getUserProfileModal.getAccNo();
                            String accTypeCode = getUserProfileModal.getAcTypeCode();
                            accountList.add(accNo + " - " + accTypeCode);
                        }
                    } else if (getPackageName().equals("com.trustbank.janataajarambank")) {
                        if (TrustMethods.isAccountTypeValidAjara(getUserProfileModal.getActType())) {
                            String accNo = getUserProfileModal.getAccNo();
                            String accTypeCode = getUserProfileModal.getAcTypeCode();
                            accountList.add(accNo + " - " + accTypeCode);
                        }
                    } else {
                        if (TrustMethods.isAccountTypeValid(getUserProfileModal.getActType())) {
                            String accNo = getUserProfileModal.getAccNo();
                            String accTypeCode = getUserProfileModal.getAcTypeCode();
                            accountList.add(accNo + " - " + accTypeCode);
                        }
                    }

                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(SelfTransferToAccountActivity.this, android.R.layout.simple_spinner_item, accountList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFrmAct.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getSpinnerData() {
        try {
            spinnerFrmAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                    ArrayAdapter<String> adapter = null;
                    if (pos != 0) {
                        accountNo = TrustMethods.getValidAccountNo((String) adapterView.getItemAtPosition(pos));
                        List<String> accountList = new ArrayList<>();
                        accountList.add(0, "Select To A/C Number");
                        //Get Profile Info List to get remitter name for the selected account number
                        if (accountsArrayList != null && accountsArrayList.size() > 0) {
                            for (int i = 0; i < accountsArrayList.size(); i++) {
                                GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                                if (TrustMethods.isAccountTypeValid(getUserProfileModal.getActType())) {
                                    if (!getUserProfileModal.getAccNo().equalsIgnoreCase(accountNo)) {
                                        String accNo = getUserProfileModal.getAccNo();
                                        String accTypeCode = getUserProfileModal.getAcTypeCode();
                                        accountList.add(accNo + " - " + accTypeCode);
                                    }
                                }

                            }

                            adapter = new ArrayAdapter<>(SelfTransferToAccountActivity.this, android.R.layout.simple_spinner_item, accountList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerToAccount.setAdapter(adapter);

                            etAmount.setText("");
                            etRemarks.setText("");
                        }
                        formLayoutFundTransfer.setVisibility(View.VISIBLE);
                    } else {
                        spinnerToAccount.setAdapter(null);
                        formLayoutFundTransfer.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });


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
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(SelfTransferToAccountActivity.this);
    }


    private void checkAmountValidation(String amount, String accountNo, List<FundTransferSubModel> fundTransferSubModalList) {
        if (NetworkUtil.getConnectivityStatus(SelfTransferToAccountActivity.this)) {
            new IMPSAddValidationAsyncTask(SelfTransferToAccountActivity.this, accountNo, amount,
                    fundTransferSubModalList).execute();
        } else {
            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
        }
    }

    //check validation api call.
    @SuppressLint("StaticFieldLeak")
    private class IMPSAddValidationAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse;
        ProgressDialog pDialog;
        String response;
        String actionName = "VALIDATE_FT";
        String result;
        private String errorCode;
        private String accountNo;
        private String amount;
        private List<FundTransferSubModel> fundTransferSubModalList;

        public IMPSAddValidationAsyncTask(Context ctx, String accountNo, String amount,
                                          List<FundTransferSubModel> fundTransferSubModalList) {
            this.ctx = ctx;
            this.accountNo = accountNo;
            this.amount = amount;
            this.fundTransferSubModalList = fundTransferSubModalList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SelfTransferToAccountActivity.this);
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
                jsonObject.put("rem_ac_no", fundTransferSubModalList.get(0).getAccNo());
                jsonObject.put("trf_type", 1);
                jsonObject.put("ben_ifsc", "");
                jsonObject.put("ben_ac_no", fundTransferSubModalList.get(0).getToAccNo());
                jsonObject.put("amount", fundTransferSubModalList.get(0).getAmt());
                jsonObject.put("is_self", "1");

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
                        AlertDialogMethod.alertDialogOk(SelfTransferToAccountActivity.this, getResources().getString(R.string.error_session_expire),
                                "",
                                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }

                } else {
                    //response..
                    Intent intent = new Intent(SelfTransferToAccountActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("checkTransferType", "SelfTransferToAccount");
                    intent.putExtra("fundTransferDataList", (Serializable) fundTransferSubModalList);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    trustMethods.activityOpenAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDialogOk(int resultCode) {
        try {
            if (resultCode == 0) {
                Intent intent = new Intent(SelfTransferToAccountActivity.this, LockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                trustMethods.activityCloseAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}