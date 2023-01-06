package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Objects;

public class IMPSTransferToMobile extends AppCompatActivity implements View.OnClickListener, AlertDialogOkListener {
    private TextView txtToBenName, txtToMobNo, txtToMmid;
    private EditText etMobileNo;
    private EditText etMMID;
    private EditText etAmount;
    private EditText etRemarks;
    private Spinner spinnerFrmAct;
    private Spinner spinnerToBeneficery;
    private LinearLayout setFundTransferLayout;
    private LinearLayout formLayoutFundTransfer;
    private CoordinatorLayout coordinatorLayout;
    private TrustMethods trustMethods;
    private String beneficiaryNickName;
    private String accountNo, remitterAccName;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private ArrayList<BeneficiaryModal> beneficiaryArrayList;
    private ArrayList<BeneficiaryModal> beneficiaryModalsSortedData;
    private AlertDialogOkListener alertDialogOkListener = this;
    private String benNickName;

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
                        TrustMethods.naviagteToSplashScreen(IMPSTransferToMobile.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(IMPSTransferToMobile.this, false);
        setContentView(R.layout.activity_impstransfer_to_mobile);
        inIt();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        try {
            if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
                bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inIt() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            trustMethods = new TrustMethods(IMPSTransferToMobile.this);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            spinnerFrmAct = findViewById(R.id.spinnerFrmAct);
            spinnerToBeneficery = findViewById(R.id.spinnerToBeneficery);
            etMobileNo = findViewById(R.id.etMobileNoId);
            etMMID = findViewById(R.id.etMMID);
            etAmount = findViewById(R.id.etAmountId);
            etRemarks = findViewById(R.id.etRemarks);
            Button btnWithBankNext = findViewById(R.id.btnWithBankNextId);
            setFundTransferLayout = findViewById(R.id.setFundTransferLayoutId);
            formLayoutFundTransfer = findViewById(R.id.formLayoutFundTransferId);
            txtToBenName = findViewById(R.id.txtToBenNameId);
            txtToMobNo = findViewById(R.id.txtToMobNoId);
            txtToMmid = findViewById(R.id.txtToMmidId);
            btnWithBankNext.setOnClickListener(this);
            setAccountNoSpinner();
            setBeneficiarySpinner();
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAccountNoSpinner() {
        try {
            accountsArrayList = trustMethods.getArrayList(IMPSTransferToMobile.this, "AccountListPref");

            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                List<String> accountList = new ArrayList<>();
                accountList.add(0, "Select Account Number");

                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    if (getPackageName().equals("com.trustbank.janataajarambank")) {
                        if (TrustMethods.isAccountTypeIsImpsRegValidAjara(getUserProfileModal.getActType(),
                                getUserProfileModal.getIs_imps_reg())) {
                            String accNo = getUserProfileModal.getAccNo();
                            String accTypeCode = getUserProfileModal.getAcTypeCode();
                            accountList.add(accNo + " - " + accTypeCode);
                        }
                    } else {
                        if (TrustMethods.isAccountTypeIsImpsRegValid(getUserProfileModal.getActType(),
                                getUserProfileModal.getIs_imps_reg())) {
                            String accNo = getUserProfileModal.getAccNo();
                            String accTypeCode = getUserProfileModal.getAcTypeCode();
                            accountList.add(accNo + " - " + accTypeCode);
                        }
                    }

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(IMPSTransferToMobile.this, android.R.layout.simple_spinner_item, accountList);
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
            List<String> beneficiaryList = new ArrayList<>();
            beneficiaryList.add(0, "Select Beneficiary");

            beneficiaryModalsSortedData = new ArrayList<>();
            if (beneficiaryArrayList != null && beneficiaryArrayList.size() > 0) {
                for (int i = 0; i < beneficiaryArrayList.size(); i++) {
                    BeneficiaryModal beneficiaryModal = beneficiaryArrayList.get(i);

                    if (beneficiaryModal.getBenType().equals("3")) {
                        BeneficiaryModal beneficiaryModal1 = new BeneficiaryModal();
                        beneficiaryModal1.setBenNickname(beneficiaryModal.getBenNickname());
                        beneficiaryModal1.setBenMobNo(beneficiaryModal.getBenMobNo());
                        beneficiaryModal1.setBenMmid(beneficiaryModal.getBenMmid());
                        beneficiaryModalsSortedData.add(beneficiaryModal1);
                        beneficiaryList.add(beneficiaryModal.getBenNickname());
                    }
                }
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(IMPSTransferToMobile.this, android.R.layout.simple_spinner_item, beneficiaryList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerToBeneficery.setAdapter(adapter);

            if (!TextUtils.isEmpty(beneficiaryNickName)) {
                formLayoutFundTransfer.setVisibility(View.GONE);
                setFundTransferLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < beneficiaryModalsSortedData.size(); i++) {
                    if (beneficiaryNickName.equalsIgnoreCase(beneficiaryModalsSortedData.get(i).getBenNickname())) {
                        txtToBenName.setText(beneficiaryModalsSortedData.get(i).getBenNickname());
                        txtToMobNo.setText(beneficiaryModalsSortedData.get(i).getBenMobNo());
                        txtToMmid.setText(beneficiaryModalsSortedData.get(i).getBenMmid());
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
        try {
            spinnerFrmAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                    if (pos != 0) {
                        accountNo = TrustMethods.getValidAccountNo((String) adapterView.getItemAtPosition(pos));
                        if (accountsArrayList != null && accountsArrayList.size() > 0) {
                            for (int i = 0; i < accountsArrayList.size(); i++) {
                                GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);

                                if (getUserProfileModal.getAccNo().equalsIgnoreCase(accountNo)) {
                                    remitterAccName = getUserProfileModal.getName();
                                    Log.d("remitterAccName", remitterAccName);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            spinnerToBeneficery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    if (position != 0) {
                        String strMobileNo = beneficiaryModalsSortedData.get(position - 1).getBenMobNo();
                        String strMMID = beneficiaryModalsSortedData.get(position - 1).getBenMmid();
                        benNickName = beneficiaryModalsSortedData.get(position - 1).getBenNickname();
                        etMobileNo.setText(strMobileNo);
                        etMMID.setText(strMMID);
                        etMobileNo.setClickable(false);
                        etMMID.setClickable(false);
                    } else {
                        etMobileNo.setClickable(true);
                        etMMID.setClickable(true);
                        etMobileNo.setText("");
                        etMMID.setText("");
                        benNickName = "";
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnWithBankNextId:
                try {
                    TrustMethods.hideSoftKeyboard(IMPSTransferToMobile.this);
                    if (!TextUtils.isEmpty(beneficiaryNickName)) {
                        if (spinnerFrmAct.getSelectedItem().equals("Select Account Number")) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayout);
                        } else if (spinnerToBeneficery.getSelectedItem().equals("Select Beneficiary")) {
                            TrustMethods.showSnackBarMessage("Please Select Beneficiary", coordinatorLayout);
                        } else if (TextUtils.isEmpty(etAmount.getText().toString())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_amt), coordinatorLayout);
                        } else if (TrustMethods.isAmoutLessThanZero(etAmount.getText().toString().trim())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_valid_amt), coordinatorLayout);
                        }else if (TextUtils.isEmpty(etRemarks.getText().toString().trim())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_enter_remark), coordinatorLayout);
                        }  else {
                            String mobNo = txtToMobNo.getText().toString().trim();
                            String mmid = txtToMmid.getText().toString().trim();
                            String amt = etAmount.getText().toString().trim();
                            String remark = etRemarks.getText().toString().trim();

                            List<FundTransferSubModel> fundTransferSubModalList = new ArrayList<>();
                            FundTransferSubModel fundTransferSubModel = new FundTransferSubModel();
                            fundTransferSubModel.setAccNo(accountNo);
                            fundTransferSubModel.setRemitterAccName(remitterAccName);
                            fundTransferSubModel.setBenMobNo(mobNo);
                            fundTransferSubModel.setBenMmid(mmid);
                            fundTransferSubModel.setAmt(amt);
                            fundTransferSubModel.setRemark(remark);
                            fundTransferSubModel.setBenAccName(benNickName);
                            fundTransferSubModalList.add(fundTransferSubModel);

                            Intent intent = new Intent(view.getContext(), OtpVerificationActivity.class);
                            intent.putExtra("checkTransferType", "impsToMobile");
                            intent.putExtra("fundTransferDataList", (Serializable) fundTransferSubModalList);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            view.getContext().startActivity(intent);
                            trustMethods.activityOpenAnimation();
                        }
                    } else {
                        if (spinnerFrmAct.getSelectedItem().equals("Select Account Number")) {
                            //TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayout);
                            AlertDialogMethod.alertDialogOk(IMPSTransferToMobile.this," ",
                                    getResources().getString(R.string.error_select_acc_debit_no),getResources().getString(R.string.btn_ok), 70, false, alertDialogOkListener);
                        } else if (TextUtils.isEmpty(etMobileNo.getText().toString())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_mob_no), coordinatorLayout);
                        } else if (TextUtils.isEmpty(etMMID.getText().toString())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_enter_mmid), coordinatorLayout);
                        } else if (TextUtils.isEmpty(etAmount.getText().toString())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_amt), coordinatorLayout);
                        } else if (TrustMethods.isAmoutLessThanZero(etAmount.getText().toString().trim())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_valid_amt), coordinatorLayout);
                        } else if (TextUtils.isEmpty(etRemarks.getText().toString().trim())) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_enter_remark), coordinatorLayout);
                        } else {
                            String mobNo = etMobileNo.getText().toString().trim();
                            String mmid = etMMID.getText().toString().trim();
                            String amt = etAmount.getText().toString().trim();
                            String remark = etRemarks.getText().toString().trim();

                            List<FundTransferSubModel> fundTransferSubModalList = new ArrayList<>();
                            FundTransferSubModel fundTransferSubModel = new FundTransferSubModel();
                            fundTransferSubModel.setAccNo(accountNo);
                            fundTransferSubModel.setRemitterAccName(remitterAccName);
                            fundTransferSubModel.setBenMobNo(mobNo);
                            fundTransferSubModel.setBenMmid(mmid);
                            fundTransferSubModel.setAmt(amt);
                            fundTransferSubModel.setRemark(remark);
                            fundTransferSubModel.setBenAccName(benNickName);
                            fundTransferSubModalList.add(fundTransferSubModel);

                            Intent intent = new Intent(view.getContext(), OtpVerificationActivity.class);
                            intent.putExtra("checkTransferType", "impsToMobile");
                            intent.putExtra("fundTransferDataList", (Serializable) fundTransferSubModalList);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            view.getContext().startActivity(intent);
                            trustMethods.activityOpenAnimation();
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
        TrustMethods.showBackButtonAlert(IMPSTransferToMobile.this);
    }

    private void checkAmountValidation(String amount, String accountNo, List<FundTransferSubModel> fundTransferSubModalList) {
        if (NetworkUtil.getConnectivityStatus(Objects.requireNonNull(IMPSTransferToMobile.this))) {
            new IMPSAddValidationAsyncTask(IMPSTransferToMobile.this, accountNo, amount, fundTransferSubModalList).execute();
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
        private String otpActionName;
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
            pDialog = new ProgressDialog(IMPSTransferToMobile.this);
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
                jsonObject.put("ben_ac_no", accountNo);
                jsonObject.put("ben_ac_no", accountNo);

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
                        AlertDialogMethod.alertDialogOk(IMPSTransferToMobile.this, getResources().getString(R.string.error_session_expire),
                                "",
                                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(IMPSTransferToMobile.this," ",
                                this.error,
                                getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }

                } else {
                    //response..
                    Intent intent = new Intent(IMPSTransferToMobile.this, OtpVerificationActivity.class);
                    intent.putExtra("checkTransferType", "impsToMobile");
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
                    Intent intent = new Intent(IMPSTransferToMobile.this, LockActivity.class);
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