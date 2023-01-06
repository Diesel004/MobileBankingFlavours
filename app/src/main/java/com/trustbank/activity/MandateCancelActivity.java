package com.trustbank.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

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
import android.widget.Spinner;
import android.widget.Switch;

import com.trustbank.Model.AccountDetailsModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.Model.MandateDateModel;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
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

public class MandateCancelActivity extends AppCompatActivity implements View.OnClickListener, AlertDialogOkListener {

    private Spinner spinnerMandateRequestId, selectAccSpinner;
    private EditText etDebatorBankNameId, etDebatorMICRCodeId, etDebatorLegalAccountNumberId,
            etDebatorAccountHolderNameId, etAmountId, etMaxAmountId, etStartDateId, etEndDateId,
            etDebtorCustomerRefNumberId, etFrequencyId,etdebtorAcctTypeId;

    private Button btnSubmitCanclMnadateId;
    private TrustMethods trustMethods;
    private CardView cardViewParentId, cardMandateDetailsId, cardViewMandateRequestId;
    private AlertDialogOkListener alertDialogOkListener = this;
    private CoordinatorLayout coordinatorLayout;

    private ArrayList<GetUserProfileModal> accountsArrayList;
    private List<String> accountList;
    private List<String> mandateUMRNList;
    private TrustMethods method;

    private MandateDateModel mandateDetailsModel = null;
    private String accNo = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            if (savedInstanceState != null) {
                Object currentPID = String.valueOf(android.os.Process.myPid());
                // Check current PID with old PID
                if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                    // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                    TrustMethods.naviagteToSplashScreen(MandateCancelActivity.this);
                }
            }
        }
        SetTheme.changeToTheme(MandateCancelActivity.this, false);
        setContentView(R.layout.activity_mandate_cancel);

        init();
    }

    private void init() {
        try {

            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            method = new TrustMethods(MandateCancelActivity.this);

            trustMethods = new TrustMethods(MandateCancelActivity.this);


            coordinatorLayout = findViewById(R.id.coordinatorLayout);

            selectAccSpinner = findViewById(R.id.selectAccSpinnerId);

            cardViewParentId = findViewById(R.id.cardViewParentId);


            cardViewMandateRequestId = findViewById(R.id.cardViewMandateRequestId);
            cardViewMandateRequestId.setVisibility(View.GONE);

            spinnerMandateRequestId = findViewById(R.id.spinnerMandateRequestId);

            cardMandateDetailsId = findViewById(R.id.cardMandateDetailsId);
            cardMandateDetailsId.setVisibility(View.GONE);

            etDebatorBankNameId = findViewById(R.id.etDebatorBankNameId);
            etDebatorMICRCodeId = findViewById(R.id.etDebatorMICRCodeId);
            etDebatorLegalAccountNumberId = findViewById(R.id.etDebatorLegalAccountNumberId);
            etDebatorAccountHolderNameId = findViewById(R.id.etDebatorAccountHolderNameId);
            etFrequencyId = findViewById(R.id.etFrequencyId);

            etAmountId = findViewById(R.id.etAmountId);
            etMaxAmountId = findViewById(R.id.etMaxAmountId);
            etStartDateId = findViewById(R.id.etStartDateId);
            etEndDateId = findViewById(R.id.etEndDateId);
            etdebtorAcctTypeId = findViewById(R.id.etdebtorAcctTypeId);
            etDebtorCustomerRefNumberId = findViewById(R.id.etDebtorCustomerRefNumberId);

            btnSubmitCanclMnadateId = findViewById(R.id.btnSubmitCanclMnadateId);
            btnSubmitCanclMnadateId.setOnClickListener(this);
            accNumberSpinner();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void accNumberSpinner() {
        try {
            accountsArrayList = method.getArrayList(MandateCancelActivity.this, "AccountListPref");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (accountsArrayList != null && accountsArrayList.size() > 0) {
            accountList = new ArrayList<>();
            accountList.add(0, "Select Account Number");
            for (int i = 0; i < accountsArrayList.size(); i++) {
                GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                String accNo = getUserProfileModal.getAccNo();
                String accTypeCode = getUserProfileModal.getAcTypeCode();
                accountList.add(accNo + " - " + accTypeCode);

            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MandateCancelActivity.this, android.R.layout.simple_spinner_item, accountList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectAccSpinner.setAdapter(adapter);
        }

        selectAccSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    if (position != 0) {
                        String selectedAccNo = (String) adapterView.getItemAtPosition(position);
                        if (selectedAccNo.contains("-")) {
                            String[] accounts = selectedAccNo.split("-");
                            accNo = accounts[0];
                        } else {
                            accNo = selectedAccNo;
                        }

                        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(MandateCancelActivity.this)) {
                            if (NetworkUtil.getConnectivityStatus(MandateCancelActivity.this)) {
                                new GetMandateAccountAsyncTask(MandateCancelActivity.this, accNo).execute();
                            } else {
                                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                            }
                        } else {
                            TrustMethods.displaySimErrorDialog(MandateCancelActivity.this);
                        }

                    } else {
                        cardViewMandateRequestId.setVisibility(View.GONE);
                        cardMandateDetailsId.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSubmitCanclMnadateId) {

            if (mandateDetailsModel != null){

                String ecsMandateId = mandateDetailsModel.getGlobalEcsMandateId();
                String umrnNo = mandateDetailsModel.getUMRN();


                if (!TextUtils.isEmpty(ecsMandateId) && !TextUtils.isEmpty(accNo)) {
                    Intent intent = new Intent(MandateCancelActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("checkTransferType", "mandateCancelRequest");
                    intent.putExtra("accountNo", accNo.trim());
                    intent.putExtra("umrnNo", umrnNo);
                    intent.putExtra("ecsMandateId", ecsMandateId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    TrustMethods.showSnackBarMessage("Account No or Card No cannot be empty.", coordinatorLayout);
                }
            }

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
            default:
                break;
        }
    }

    //TODO Get Mandate Accounts List as per acc no.
    private class GetMandateAccountAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String actionName = "GET_MANDATE_DETAILS";
        String mAccNo;
        String result;
        ArrayList<MandateDateModel> accountDateModelArrayList = new ArrayList<>();
        private String errorCode;

        public GetMandateAccountAsyncTask(Context ctx, String accNo) {
            this.ctx = ctx;
            this.mAccNo = accNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MandateCancelActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.getMandateDetails(mAccNo);

                if (!url.equals("")) {
                    result = HttpClientWrapper.getResponseGET(url, actionName, AppConstants.getAuth_token());
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
                    JSONArray mandateJsonArray = jsonResponse.getJSONObject("response").getJSONArray("mandate_list");

                    for (int i = 0; i < mandateJsonArray.length(); i++) {

                        MandateDateModel mandateDateModel = new MandateDateModel();

                        JSONObject mandateJSONObject = mandateJsonArray.getJSONObject(i);
                        String debitorBankName = mandateJSONObject.has("BankName") ? mandateJSONObject.getString("BankName") : "";
                        String mandateType = mandateJSONObject.has("Mandate Type") ? mandateJSONObject.getString("Mandate Type") : "";
                        String globalEcsMandateId = mandateJSONObject.has("GlobalEcsMandateId") ? mandateJSONObject.getString("GlobalEcsMandateId") : "";
                        String accountId = mandateJSONObject.has("AccountId") ? mandateJSONObject.getString("AccountId") : "";
                        String orgElementId = mandateJSONObject.has("OrgElementId") ? mandateJSONObject.getString("OrgElementId") : "";
                        String accountTypeId = mandateJSONObject.has("AccountTypeId") ? mandateJSONObject.getString("AccountTypeId") : "";
                        String mICRCodeId = mandateJSONObject.has("MICRCodeId") ? mandateJSONObject.getString("MICRCodeId") : "";
                        String fromDate = mandateJSONObject.has("FromDate") ? mandateJSONObject.getString("FromDate") : "";
                        String toDate = mandateJSONObject.has("ToDate") ? mandateJSONObject.getString("ToDate") : "";
                        String ecsAmount = mandateJSONObject.has("EcsAmount") ? mandateJSONObject.getString("EcsAmount") : "";
                        String bankListId = mandateJSONObject.has("BankListId") ? mandateJSONObject.getString("BankListId") : "";
                        String branchListId = mandateJSONObject.has("BranchListId") ? mandateJSONObject.getString("BranchListId") : "";
                        String ecsCategoryCodeId = mandateJSONObject.has("EcsCategoryCodeId") ? mandateJSONObject.getString("EcsCategoryCodeId") : "";
                        String frequency = mandateJSONObject.has("Frequency") ? mandateJSONObject.getString("Frequency") : "";
                        String sequenceType = mandateJSONObject.has("SequenceType") ? mandateJSONObject.getString("SequenceType") : "";
                        String ecsMaxAmount = mandateJSONObject.has("ECSMaxAmount") ? mandateJSONObject.getString("ECSMaxAmount") : "";
                        String consumerRefNo = mandateJSONObject.has("ConsumerRefNo") ? mandateJSONObject.getString("ConsumerRefNo") : "";
                        String schemeRefNo = mandateJSONObject.has("SchemeRefNo") ? mandateJSONObject.getString("SchemeRefNo") : "";
                        String debtTelephone = mandateJSONObject.has("DebtTelephone") ? mandateJSONObject.getString("DebtTelephone") : "";
                        String debtTelephoneCCode = mandateJSONObject.has("DebtTelephoneCCode") ? mandateJSONObject.getString("DebtTelephoneCCode") : "";
                        String debtMobile = mandateJSONObject.has("DebtMobile") ? mandateJSONObject.getString("DebtMobile") : "";
                        String email = mandateJSONObject.has("Email") ? mandateJSONObject.getString("Email") : "";
                        String customerAddInfo = mandateJSONObject.has("CustomerAddInfo") ? mandateJSONObject.getString("CustomerAddInfo") : "";
                        String UMRN = mandateJSONObject.has("UMRN") ? mandateJSONObject.getString("UMRN") : "";
                        String debtBankId = mandateJSONObject.has("DebtBankId") ? mandateJSONObject.getString("DebtBankId") : "";
                        String accountHeadId = mandateJSONObject.has("AccountHeadId") ? mandateJSONObject.getString("AccountHeadId") : "";
                        String debtorAccountNo = mandateJSONObject.has("Debtor Account No") ? mandateJSONObject.getString("Debtor Account No") : "";
                        String debtAccountTypeId = mandateJSONObject.has("DebtAccountTypeId") ? mandateJSONObject.getString("DebtAccountTypeId") : "";
                        String destinationAccountHolderName = mandateJSONObject.has("Destination Account Holder Name") ? mandateJSONObject.getString("Destination Account Holder Name") : "";
                        String serviceProUtilityCode = mandateJSONObject.has("ServiceProUtilityCode") ? mandateJSONObject.getString("ServiceProUtilityCode") : "";
                        String creditorAccountType = mandateJSONObject.has("Creditor Account Type") ? mandateJSONObject.getString("Creditor Account Type") : "";
                        String creditorAccountNo = mandateJSONObject.has("Creditor Account No") ? mandateJSONObject.getString("Creditor Account No") : "";
                        String creditorBankName = mandateJSONObject.has("Creditor Bank Name") ? mandateJSONObject.getString("Creditor Bank Name") : "";
                        String debtBankMICRCode = mandateJSONObject.has("DebtBankMICRCode") ? mandateJSONObject.getString("DebtBankMICRCode") : "";


                        mandateDateModel.setMandateType(mandateType);
                        mandateDateModel.setGlobalEcsMandateId(globalEcsMandateId);
                        mandateDateModel.setAccountId(accountId);
                        mandateDateModel.setOrgElementId(orgElementId);
                        mandateDateModel.setAccountTypeId(accountTypeId);
                        mandateDateModel.setmICRCodeId(mICRCodeId);
                        mandateDateModel.setFromDate(fromDate);
                        mandateDateModel.setToDate(toDate);
                        mandateDateModel.setEcsAmount(ecsAmount);
                        mandateDateModel.setBankListId(bankListId);
                        mandateDateModel.setBranchListId(branchListId);
                        mandateDateModel.setEcsCategoryCodeId(ecsCategoryCodeId);
                        mandateDateModel.setFrequency(frequency);
                        mandateDateModel.setSequenceType(sequenceType);
                        mandateDateModel.setEcsMaxAmount(ecsMaxAmount);
                        mandateDateModel.setConsumerRefNo(consumerRefNo);
                        mandateDateModel.setSchemeRefNo(schemeRefNo);
                        mandateDateModel.setDebtTelephone(debtTelephone);
                        mandateDateModel.setDebtTelephoneCCode(debtTelephoneCCode);
                        mandateDateModel.setDebtMobile(debtMobile);
                        mandateDateModel.setEmail(email);
                        mandateDateModel.setCustomerAddInfo(customerAddInfo);
                        mandateDateModel.setUMRN(UMRN);
                        mandateDateModel.setDebtBankId(debtBankId);
                        mandateDateModel.setAccountHeadId(accountHeadId);
                        mandateDateModel.setDebtAccountNo(debtorAccountNo);
                        mandateDateModel.setDebtAccountTypeId(debtAccountTypeId);
                        mandateDateModel.setAccountHolderName(destinationAccountHolderName);
                        mandateDateModel.setServiceProUtilityCode(serviceProUtilityCode);
                        mandateDateModel.setCreditorAccountType(creditorAccountType);
                        mandateDateModel.setCreditorAccountNo(creditorAccountNo);
                        mandateDateModel.setCreditorBankName(creditorBankName);
                        mandateDateModel.setDebtBankMICRCode(debtBankMICRCode);
                        mandateDateModel.setBankName(debitorBankName);

                        accountDateModelArrayList.add(mandateDateModel);

                    }

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
                    cardViewMandateRequestId.setVisibility(View.GONE);
                    cardMandateDetailsId.setVisibility(View.GONE);
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(MandateCancelActivity.this,
                                getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok),
                                0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                } else {
                    if (accountDateModelArrayList != null && accountDateModelArrayList.size() != 0) {
                        cardViewMandateRequestId.setVisibility(View.VISIBLE);
                        setMandateUMRNNoSpinner(accountDateModelArrayList);
                    } else {
                        cardViewMandateRequestId.setVisibility(View.GONE);
                        cardMandateDetailsId.setVisibility(View.GONE);
                        TrustMethods.showSnackBarMessage("No record found", coordinatorLayout);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setMandateUMRNNoSpinner(ArrayList<MandateDateModel> accountDateModelArrayList) {

        try {

            if (accountDateModelArrayList != null && accountDateModelArrayList.size() > 0) {
                mandateUMRNList = new ArrayList<>();
                mandateUMRNList.add(0, "Select UMRN Number");
                for (int i = 0; i < accountDateModelArrayList.size(); i++) {
                    MandateDateModel mandateDetailsModel = accountDateModelArrayList.get(i);
                    String umRNNo = mandateDetailsModel.getUMRN();
                    mandateUMRNList.add(umRNNo);

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MandateCancelActivity.this, android.R.layout.simple_spinner_item, mandateUMRNList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMandateRequestId.setAdapter(adapter);


                spinnerMandateRequestId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        try {
                            if (position != 0) {
                                String umrnNumber = (String) adapterView.getItemAtPosition(position);
                                for(MandateDateModel mandateDateModel : accountDateModelArrayList){
                                    if (umrnNumber.equalsIgnoreCase(mandateDateModel.getUMRN())){
                                        mandateDetailsModel = mandateDateModel;
                                        break;
                                    }
                                }
                                if (mandateDetailsModel != null){
                                    displayMandateDetailData(mandateDetailsModel);
                                }

                            } else {
                                cardMandateDetailsId.setVisibility(View.GONE);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayMandateDetailData(MandateDateModel mandateDetailsModel) {
        try {
            cardMandateDetailsId.setVisibility(View.VISIBLE);
            etDebatorBankNameId.setText(mandateDetailsModel.getBankName());
            etDebatorMICRCodeId.setText(mandateDetailsModel.getDebtBankMICRCode());
            etDebatorLegalAccountNumberId.setText(mandateDetailsModel.getDebtAccountNo());
            etDebatorAccountHolderNameId.setText(mandateDetailsModel.getAccountHolderName());
            etFrequencyId.setText(TrustMethods.getFrequency(mandateDetailsModel.getFrequency()));
            etAmountId.setText(mandateDetailsModel.getEcsAmount());
            etMaxAmountId.setText(mandateDetailsModel.getEcsMaxAmount());
            etStartDateId.setText(mandateDetailsModel.getFromDate());
            etEndDateId.setText(mandateDetailsModel.getToDate());
            etdebtorAcctTypeId.setText(mandateDetailsModel.getDebtAccountTypeId());
            etDebtorCustomerRefNumberId.setText(mandateDetailsModel.getConsumerRefNo());
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(MandateCancelActivity.this);
    }
}