package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;

import com.trustbank.Model.BeneficiaryModal;
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


public class WithinBankActivity extends AppCompatActivity implements View.OnClickListener, AlertDialogOkListener {

    private Spinner spinnerFrmAct;
    private Spinner spinnerToBeneficery;
    private EditText etAccountName;
    private EditText etAccountNo;
    private EditText etAmount;
    private EditText etRemarks;
    private CoordinatorLayout coordinatorLayout;
    private TrustMethods trustMethods;
    ArrayList<GetUserProfileModal> accountsArrayList;
    List<String> accountList;
    List<String> beneficiaryList;
    ArrayList<BeneficiaryModal> beneficiaryArrayList;
    private String beneficiaryNickName;
    private LinearLayout setFundTransferLayout;
    private LinearLayout formLayoutFundTransfer;
    private TextView txtToBenName, txtToAccNo, txtToAccName;
    ArrayList<BeneficiaryModal> beneficiaryModalsSortedData;

    private String benId = "";
    private String accountNo;
    private String toBeneficiaryName;
    private String remitterAccName;
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
                        TrustMethods.naviagteToSplashScreen(WithinBankActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(WithinBankActivity.this, false);
        setContentView(R.layout.activity_within_bank);

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
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            trustMethods = new TrustMethods(WithinBankActivity.this);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            spinnerFrmAct = findViewById(R.id.spinnerFrmAct);
            spinnerToBeneficery = findViewById(R.id.spinnerToBeneficery);
            etAccountName = findViewById(R.id.etAccountName);
            etAccountNo = findViewById(R.id.etAccountNo);
            etAmount = findViewById(R.id.etAmount);
            etRemarks = findViewById(R.id.etRemarks);
            Button btnWithBankNext = findViewById(R.id.btnWithBankNextId);
            setFundTransferLayout = findViewById(R.id.setFundTransferLayoutId);
            formLayoutFundTransfer = findViewById(R.id.formLayoutFundTransferId);
            txtToBenName = findViewById(R.id.txtToBenNameId);
            txtToAccNo = findViewById(R.id.txtToAccNoId);
            txtToAccName = findViewById(R.id.txtToAccNameId);

            btnWithBankNext.setOnClickListener(this);

         /*   if (getPackageName().equalsIgnoreCase("com.trustbank.shivajibank") ||
                    getPackageName().equalsIgnoreCase("com.trustbank.vmucbbank")){
                TrustMethods.setEditTextMaxLength(18,etAccountNo);
            }else {
                TrustMethods.setEditTextMaxLength(15,etAccountNo);
            }*/

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

            setAccountNoSpinner();
            setBeneficiarySpinner();
            getSpinnerData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAccountNoSpinner() {
        try {
            accountsArrayList = trustMethods.getArrayList(WithinBankActivity.this, "AccountListPref");

            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                accountList = new ArrayList<>();
                accountList.add(0, "Select Account Number");
                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    if (getPackageName().equals("com.trustbank.janataajarambank")) {
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(WithinBankActivity.this, android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFrmAct.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBeneficiarySpinner() {

        try {
            if (getIntent().getExtras() != null) {
                beneficiaryArrayList = (ArrayList<BeneficiaryModal>) getIntent().getSerializableExtra("beneficiaryList");
                beneficiaryNickName = getIntent().getStringExtra("beneficiaryNickName");
            }
            beneficiaryList = new ArrayList<>();
//        beneficiaryList.add(0, "New Beneficiary");
            beneficiaryList.add(0, "Select Beneficiary");

            beneficiaryModalsSortedData = new ArrayList<>();
            if (beneficiaryArrayList != null && beneficiaryArrayList.size() > 0) {
                for (int i = 0; i < beneficiaryArrayList.size(); i++) {
                    BeneficiaryModal beneficiaryModal = beneficiaryArrayList.get(i);

                    if (beneficiaryModal.getBenType().equals("1")) {
                        BeneficiaryModal beneficiaryModal1 = new BeneficiaryModal();
                        beneficiaryModal1.setBenId(beneficiaryModal.getBenId());
                        beneficiaryModal1.setBenNickname(beneficiaryModal.getBenNickname());
                        beneficiaryModal1.setBanAccName(beneficiaryModal.getBanAccName());
                        beneficiaryModal1.setBenAccNo(beneficiaryModal.getBenAccNo());
                        beneficiaryModalsSortedData.add(beneficiaryModal1);
                        beneficiaryList.add(beneficiaryModal.getBenNickname());
                    }
                }
            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_benef_not_added), coordinatorLayout);
                setFundTransferLayout.setVisibility(View.GONE);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(WithinBankActivity.this, android.R.layout.simple_spinner_item, beneficiaryList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerToBeneficery.setAdapter(adapter);

            if (!TextUtils.isEmpty(beneficiaryNickName)) {
                formLayoutFundTransfer.setVisibility(View.GONE);
                setFundTransferLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < beneficiaryModalsSortedData.size(); i++) {
                    if (beneficiaryNickName.equalsIgnoreCase(beneficiaryModalsSortedData.get(i).getBenNickname())) {
                        benId = beneficiaryModalsSortedData.get(i).getBenId(); //Added for trnasfer fund direct from manage beneficiary
                        txtToBenName.setText(beneficiaryModalsSortedData.get(i).getBenNickname());
                        txtToAccName.setText(beneficiaryModalsSortedData.get(i).getBanAccName());
                        txtToAccNo.setText(beneficiaryModalsSortedData.get(i).getBenAccNo());
                    }
                }
            } else {
                formLayoutFundTransfer.setVisibility(View.VISIBLE);
                setFundTransferLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSpinnerData() {
        spinnerFrmAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position != 0) {
                        accountNo = TrustMethods.getValidAccountNo((String) parent.getItemAtPosition(position));
                        //Get Profile Info List to get remitter name for the selected account number
                        if (accountsArrayList != null && accountsArrayList.size() > 0) {
                            for (int i = 0; i < accountsArrayList.size(); i++) {
                                GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                                if (getUserProfileModal.getAccNo().equalsIgnoreCase(accountNo.trim())) {
                                    remitterAccName = getUserProfileModal.getName();
                                    Log.d("remitterAccName", remitterAccName);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerToBeneficery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (position != 0) {
                        toBeneficiaryName = (String) parent.getItemAtPosition(position);
                        benId = beneficiaryModalsSortedData.get(position - 1).getBenId();
                        String strAcctName = beneficiaryModalsSortedData.get(position - 1).getBanAccName();
                        String strAcctNumber = beneficiaryModalsSortedData.get(position - 1).getBenAccNo();
                        TrustMethods.LogMessage("strAcctNumber", strAcctNumber);
                        etAccountName.setText(strAcctName);
                        etAccountNo.setText(strAcctNumber);
                        etAccountName.setClickable(false);
                        etAccountNo.setClickable(false);

                    } else {
                        etAccountNo.setText("");
                        etAccountName.setText("");
                        etAccountName.setClickable(true);
                        etAccountNo.setClickable(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWithBankNextId:
                try {
                    TrustMethods.hideSoftKeyboard(WithinBankActivity.this);

                    if (!TextUtils.isEmpty(beneficiaryNickName)) {
                        if (spinnerFrmAct.getSelectedItem().equals("Select Account Number")) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayout);
                        } else if (TextUtils.isEmpty(etAmount.getText().toString())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_amt), coordinatorLayout);
                        } else if (TrustMethods.isAmoutLessThanZero(etAmount.getText().toString().trim())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_valid_amt), coordinatorLayout);
                        } else {
                            String benToName = txtToBenName.getText().toString().trim();
                            String benAccName = txtToAccName.getText().toString().trim();
                            String benAccNo = txtToAccNo.getText().toString().trim();
                            String amt = etAmount.getText().toString().trim();
                            String remark = etRemarks.getText().toString().trim();

                            List<FundTransferSubModel> fundTransferSubModalList = new ArrayList<>();
                            FundTransferSubModel fundTransferSubModel = new FundTransferSubModel();
                            fundTransferSubModel.setAccNo(accountNo);
                            fundTransferSubModel.setBenId(benId);
                            fundTransferSubModel.setToBenName(benToName);
                            fundTransferSubModel.setBenAccName(benAccName);
                            fundTransferSubModel.setBenAccNo(benAccNo);
                            fundTransferSubModel.setRemitterAccName(remitterAccName);
                            fundTransferSubModel.setAmt(amt);
                            fundTransferSubModel.setRemark(remark);
                            fundTransferSubModalList.add(fundTransferSubModel);

                            checkAmountValidation(amt, accountNo, fundTransferSubModalList);
                        }
                    } else {
                        if (spinnerFrmAct.getSelectedItem().equals("Select Account Number")) {
                            //TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayout);
                            AlertDialogMethod.alertDialogOk(WithinBankActivity.this," ", getResources().getString(R.string.error_select_acc_debit_no),getResources().getString(R.string.btn_ok), 70, false, alertDialogOkListener);
                        } else if (spinnerToBeneficery.getSelectedItem().equals("Select Beneficiary")) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_to_benf), coordinatorLayout);
                        } else if (TextUtils.isEmpty(etAccountName.getText().toString())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_enter_ben_acc_nm), coordinatorLayout);
                        } else if (TextUtils.isEmpty(etAccountNo.getText().toString())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_enter_ben_acc_no), coordinatorLayout);
                        } else if (TextUtils.isEmpty(etAmount.getText().toString())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_amt), coordinatorLayout);
                        } else if (TrustMethods.isAmoutLessThanZero(etAmount.getText().toString().trim())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_valid_amt), coordinatorLayout);
                        }  else {
                            String benAccName = etAccountName.getText().toString().trim();
                            String benAccNo = etAccountNo.getText().toString().trim();
                            String amt = etAmount.getText().toString().trim();
                            String remark = etRemarks.getText().toString().trim();

                            List<FundTransferSubModel> fundTransferSubModalList = new ArrayList<>();
                            FundTransferSubModel fundTransferSubModel = new FundTransferSubModel();
                            fundTransferSubModel.setAccNo(accountNo);
                            fundTransferSubModel.setToBenName(toBeneficiaryName);
                            fundTransferSubModel.setRemitterAccName(remitterAccName);
                            fundTransferSubModel.setBenId(benId);
                            fundTransferSubModel.setBenAccName(benAccName);
                            fundTransferSubModel.setBenAccNo(benAccNo);
                            fundTransferSubModel.setAmt(amt);
                            fundTransferSubModel.setRemark(remark);
                            fundTransferSubModalList.add(fundTransferSubModel);

                            checkAmountValidation(amt, accountNo, fundTransferSubModalList);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }

    private void checkAmountValidation(String amount, String accountNo, List<FundTransferSubModel> fundTransferSubModalList) {
        if (NetworkUtil.getConnectivityStatus(WithinBankActivity.this)) {
            new WithinBankAddValidationAsyncTask(WithinBankActivity.this, accountNo, amount, fundTransferSubModalList).execute();
        } else {
            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                trustMethods.activityCloseAnimation();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(WithinBankActivity.this);
    }

    //check validation api call.
    @SuppressLint("StaticFieldLeak")
    private class WithinBankAddValidationAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse;
        ProgressDialog pDialog;
        String response;
        String actionName = "VALIDATE_FT";
        String result;
        private String errorCode;
        private String accountNo;
        private String otpActionName;
        private String amount;
        private List<FundTransferSubModel> fundTransferSubModalList;

        public WithinBankAddValidationAsyncTask(Context ctx, String accountNo, String amount,
                                                List<FundTransferSubModel> fundTransferSubModalList) {
            this.ctx = ctx;
            this.otpActionName = actionName;
            this.accountNo = accountNo;
            this.amount = amount;
            this.fundTransferSubModalList = fundTransferSubModalList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WithinBankActivity.this);
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
                jsonObject.put("ben_id", fundTransferSubModalList.get(0).getBenId());
                jsonObject.put("ben_ifsc", "");
                jsonObject.put("ben_ac_no", fundTransferSubModalList.get(0).getBenAccNo());
                jsonObject.put("amount", fundTransferSubModalList.get(0).getAmt());

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
                        AlertDialogMethod.alertDialogOk(WithinBankActivity.this, getResources().getString(R.string.error_session_expire),
                                "",
                                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(WithinBankActivity.this, " ",
                                this.error,
                                getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }

                } else {
                    //response..
                    Intent intent = new Intent(WithinBankActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("checkTransferType", "withinBank");
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
            switch (resultCode) {

                case 0:
                    Intent intent = new Intent(WithinBankActivity.this, LockActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    trustMethods.activityCloseAnimation();
                    break;

                case 55:

                    break;

                case 70:


                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
