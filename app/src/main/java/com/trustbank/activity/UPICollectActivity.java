package com.trustbank.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.Model.FundTransferSubModel;
import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.fragment.UPIBenListFragmentDialog;
import com.trustbank.interfaces.AlertDialogListener;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.interfaces.UPISelectBenInterface;
import com.trustbank.tcpconnection.tdto.MessageDtoBuilder;
import com.trustbank.tcpconnection.tmessage.TMessage;
import com.trustbank.tcpconnection.tmessage.TMessageUtil;
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

public class UPICollectActivity extends AppCompatActivity implements View.OnClickListener,
        AlertDialogListener, AlertDialogOkListener, UPISelectBenInterface {

    private TrustMethods trustMethods;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private Spinner spinnerFromAccountId;
    private EditText etUPIIDId, etAmount, etRemarksId, etCustomerNameId;
    private Button btnRequestId, btnGenerateBarcodeId;
    private CoordinatorLayout coordinatorLayout;
    private AlertDialogListener alertDialogListener = this;
    private AlertDialogOkListener alertDialogOkListener = this;
    TextView textVerifyUPITransId;
    private ImageView imgSelectUPIId;
    private ArrayList<BeneficiaryModal> beneficiaryArrayList;
    private String accountNo;
    private String remitterAccName;
    List<FundTransferSubModel> fundTransferSubModalList = new ArrayList<>();
    //    private LinearLayout bottom_linear_layout_id;
//
//    private BottomSheetBehavior sheetBehavior;
    private int BARCODE_READER_REQUEST_CODE = 1;


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
                        TrustMethods.naviagteToSplashScreen(UPICollectActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(UPICollectActivity.this, false);
        setContentView(R.layout.activity_upicollect);

        initcomponent();
    }


    private void initcomponent() {

        try {

            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            trustMethods = new TrustMethods(UPICollectActivity.this);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            spinnerFromAccountId = findViewById(R.id.spinnerFromAccountId);
            etAmount = findViewById(R.id.etAmountId);
            etRemarksId = findViewById(R.id.etRemarksId);
            btnRequestId = findViewById(R.id.btnRequestId);
            btnGenerateBarcodeId = findViewById(R.id.btnGenerateBarcodeId);
            etUPIIDId = findViewById(R.id.etUPIIDId);
            textVerifyUPITransId = findViewById(R.id.textVerifyUPITransId);
            etCustomerNameId = findViewById(R.id.etCustomerNameId);
            imgSelectUPIId = findViewById(R.id.imgSelectUPIId);


            btnRequestId.setOnClickListener(this);
            textVerifyUPITransId.setOnClickListener(this);
            imgSelectUPIId.setOnClickListener(this);
            btnGenerateBarcodeId.setOnClickListener(this);

            setAccountNoSpinner();
            textVerifyUPITransId.setVisibility(View.GONE);

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
            accountsArrayList = trustMethods.getArrayList(UPICollectActivity.this, "AccountListPref");

            if (getIntent().getExtras() != null) {
                beneficiaryArrayList = (ArrayList<BeneficiaryModal>) getIntent().getSerializableExtra("beneficiaryList");

            }

            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                List<String> accountList = new ArrayList<>();
                accountList.add(0, "Select Account Number");
                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    if (TrustMethods.isAccountTypeIsImpsRegValid(getUserProfileModal.getActType(),
                            getUserProfileModal.getIs_imps_reg())) {

                        String accNo = getUserProfileModal.getAccNo();
                        String accTypeCode = getUserProfileModal.getAcTypeCode();
                        String name = getUserProfileModal.getName();
                        accountList.add(accNo + " - " + accTypeCode);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(UPICollectActivity.this, android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFromAccountId.setAdapter(adapter);


                spinnerFromAccountId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                        if (pos != 0) {
                            accountNo = TrustMethods.getValidAccountNo((String) adapterView.getItemAtPosition(pos));
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


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRequestId:

                if (isValidate()) {
                    String amt = etAmount.getText().toString().trim();
                    String remark = etRemarksId.getText().toString().trim();
                    String upiId = etUPIIDId.getText().toString();
                    String payeeCustName = etCustomerNameId.getText().toString();


                    FundTransferSubModel fundTransferSubModel = new FundTransferSubModel();
                    fundTransferSubModel.setAccNo(accountNo);
                    fundTransferSubModel.setRemitterAccName(remitterAccName);
                    fundTransferSubModel.setBenAccNo("");
                    fundTransferSubModel.setBenIfscCode("");
                    fundTransferSubModel.setAmt(amt);
                    fundTransferSubModel.setRemark(remark);
                    fundTransferSubModel.setBenAccName(payeeCustName);
                    fundTransferSubModel.setUpiId(upiId);
                    fundTransferSubModalList.add(fundTransferSubModel);

                    AlertDialogMethod.alertDialog(UPICollectActivity.this, getResources().getString(R.string.msg_confirm_collect_req), getResources().getString(R.string.msg_sure_collect_req) + " " + amt, getResources().getString(R.string.btn_ok), "Cancel", 0, true, alertDialogListener);

                }

                break;

            case R.id.btnGenerateBarcodeId:
                if (isValidateDataBr()) {

                    AlertDialogMethod.alertDialog(UPICollectActivity.this, getResources().getString(R.string.msg_confirm_collect_req), getResources().getString(R.string.msg_create_barcode_req) + " " + etAmount.getText().toString().trim(), getResources().getString(R.string.btn_ok), "Cancel", 1, true, alertDialogListener);

                }

                break;

            case R.id.textVerifyUPITransId:
                //TODO Verify ben here.
                break;

            case R.id.imgSelectUPIId:  //TODO  select UPI Id From fragment dialogs..
                if (beneficiaryArrayList != null && beneficiaryArrayList.size() != 0) {

                    ArrayList<BeneficiaryModal> beneficiaryUPiArrayList = new ArrayList<>();

                    for (BeneficiaryModal beneficiaryModal : beneficiaryArrayList) {
                        if (beneficiaryModal.getBenType().equalsIgnoreCase("4")) {
                            beneficiaryUPiArrayList.add(beneficiaryModal);
                        }
                    }
                    if (beneficiaryUPiArrayList != null && beneficiaryUPiArrayList.size() != 0) {
                        FragmentManager manager = getSupportFragmentManager();
                        DialogFragment newFragment = UPIBenListFragmentDialog.newInstance(beneficiaryUPiArrayList, this);
                        newFragment.show(manager, "dialog");
                    }

                }
                break;
        }
    }


    @Override
    public void onDialogOk(int resultCode) {
        try {
            if (resultCode == 0) {
                Intent intent = new Intent(UPICollectActivity.this, UPICollectSuccessActivity.class);
                intent.putExtra("checkTransferType", "upiToUpiTransaction");
                intent.putExtra("fundTransferDataList", (Serializable) fundTransferSubModalList);
                intent.putExtra("beneficiaryList", beneficiaryArrayList);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                trustMethods.activityCloseAnimation();
            } else if (resultCode == 1) {
                String amt = etAmount.getText().toString().trim();
                String remark = etRemarksId.getText().toString().trim();
                String upiId = etUPIIDId.getText().toString();
                String payeeCustName = etCustomerNameId.getText().toString();


                FundTransferSubModel fundTransferSubModel = new FundTransferSubModel();
                fundTransferSubModel.setAccNo(accountNo);
                fundTransferSubModel.setRemitterAccName(remitterAccName);
                fundTransferSubModel.setBenAccNo("");
                fundTransferSubModel.setBenIfscCode("");
                fundTransferSubModel.setAmt(amt);
                fundTransferSubModel.setRemark(remark);
                fundTransferSubModel.setBenAccName(payeeCustName);
                fundTransferSubModel.setUpiId(upiId);
                fundTransferSubModalList.add(fundTransferSubModel);
                TrustMethods.hideSoftKeyboard(UPICollectActivity.this);

                if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(UPICollectActivity.this)) {
                    if (NetworkUtil.getConnectivityStatus(UPICollectActivity.this)) {
                        new FetchUPIUrlForBarcodeAsyncTask(UPICollectActivity.this, "0.00", accountNo).execute();
                    } else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                    }
                } else {
                    TrustMethods.displaySimErrorDialog(UPICollectActivity.this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

  /*  private void   callasyncktask( List<FundTransferSubModel> fundTransferSubModalList){
        try{
            if (NetworkUtil.getConnectivityStatus(UPICollectActivity.this)) {
                new UPICollectActivity.RequestCollectAsyncTask(UPICollectActivity.this, fundTransferSubModalList).execute();
            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }*/

    public boolean isValidate() {
        if (spinnerFromAccountId.getSelectedItem().equals("Select Account Number")) {
            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayout);
            return false;
        } else if (TextUtils.isEmpty(etUPIIDId.getText().toString().trim())) {
            TrustMethods.showSnackBarMessage("Please Enter Payee's UPI Id", coordinatorLayout);
            return false;
        } else if (!TrustMethods.validateUPI(etUPIIDId.getText().toString().trim())) {
            TrustMethods.showSnackBarMessage("Invalid UPI Id, Please enter valid UPI id.", coordinatorLayout);
            return false;
        } else if (TextUtils.isEmpty(etCustomerNameId.getText().toString())) {
            TrustMethods.showSnackBarMessage("Enter Customer Name", coordinatorLayout);
            return false;
        } else if (TextUtils.isEmpty(etAmount.getText().toString())) {
            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_amt), coordinatorLayout);
            return false;
        } else if (TrustMethods.isAmoutLessThanZero(etAmount.getText().toString().trim())) {
            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_valid_amt), coordinatorLayout);
            return false;
        } else {
            return true;
        }

    }

    public boolean isValidateDataBr() {
        if (spinnerFromAccountId.getSelectedItem().equals("Select Account Number")) {
            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayout);
            return false;
        } /*else if (TextUtils.isEmpty(etUPIIDId.getText().toString().trim())) {
            TrustMethods.showSnackBarMessage("Please Enter Payee's UPI Id", coordinatorLayout);
            return false;
        } else if (!TrustMethods.validateUPI(etUPIIDId.getText().toString().trim())) {
            TrustMethods.showSnackBarMessage("Invalid UPI Id, Please enter valid UPI id.", coordinatorLayout);
            return false;
        } else if (TextUtils.isEmpty(etCustomerNameId.getText().toString())) {
            TrustMethods.showSnackBarMessage("Enter Customer Name", coordinatorLayout);
            return false;
        } else if (TextUtils.isEmpty(etAmount.getText().toString())) {
            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_amt), coordinatorLayout);
            return false;
        } else if (TrustMethods.isAmoutLessThanZero(etAmount.getText().toString().trim())) {
            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_valid_amt), coordinatorLayout);
            return false;
        } */ else {
            return true;
        }

    }

    @Override
    public void onDialogCancel(int resultCode) {
        try {
            if (resultCode == 0) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




   /* // api call.
    @SuppressLint("StaticFieldLeak")
    private class RequestCollectAsyncTask extends AsyncTask<Void, Void, String> {
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

        public void IMPSAddValidationAsyncTask(Context ctx,
                                               List<FundTransferSubModel> fundTransferSubModalList) {
            this.ctx = ctx;
            this.accountNo = accountNo;
            this.amount = amount;
            this.fundTransferSubModalList = fundTransferSubModalList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UPICollectActivity.this);
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
                jsonObject.put("trf_type", 3);
                jsonObject.put("ben_ifsc", fundTransferSubModalList.get(0).getBenIfscCode());
                jsonObject.put("ben_ac_no", fundTransferSubModalList.get(0).getBenAccNo());
                jsonObject.put("amount", fundTransferSubModalList.get(0).getAmt());

                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonObject.toString(),
                            actionName, AppConstants.getAuth_token());

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
                        AlertDialogMethod.alertDialogOk(UPICollectActivity.this, getResources().getString(R.string.error_session_expire),
                                "",
                                getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }

                } else {
                    //response..
                    Intent intent = new Intent(UPICollectActivity.this, UPICollectSuccessActivity.class);
                   // intent.putExtra("checkTransferType", "impsToAccount");
                    intent.putExtra("fundTransferDataList", (Serializable) fundTransferSubModalList);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    trustMethods.activityOpenAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upi_scan_menu, menu);
        return true;
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } /*else if (item.getItemId() == R.id.action_scan) {
            Intent intent = new Intent(UPICollectActivity.this, ScannedBarcodeActivity.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(UPICollectActivity.this);
    }

    @Override
    public void selectUPIVPABenName(String upiBenVpaName, String custName,String benName) {
        etUPIIDId.setText(upiBenVpaName);
        etCustomerNameId.setText(custName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == BARCODE_READER_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                String upi_id = data.getStringExtra("upiid");
                String upi_name = data.getStringExtra("upi_name");
                if (upi_id != null) {

                    etUPIIDId.setText(upi_id);
                    etCustomerNameId.setText(upi_name);

                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_barcode_captured, Toast.LENGTH_SHORT).show();
                }
            }

        } else
            super.onActivityResult(requestCode, resultCode, data);

    }

    @SuppressLint("StaticFieldLeak")
    private class FetchUPIUrlForBarcodeAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        String result;
        ProgressDialog pDialog;
        String mAccNo, mAmount;
        //        String action = "send_to_switch";
        String finalResponse;
        private String errorCode;

        public FetchUPIUrlForBarcodeAsyncTask(Context ctx, String amount, String accNo) {
            this.ctx = ctx;
            this.mAccNo = accNo;
            this.mAmount = amount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UPICollectActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                //  String jsonString = "{\"filter\":[\"stan\"]}";
                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(UPICollectActivity.this, jsonString, TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {
                    TMessage msg = msgDto.upiCollectMoneyRequest(mAmount, generateStanRRNModel.getChannel_ref_no(),
                            AppConstants.getUSERMOBILENUMBER(),
                            mAccNo); //TMessageUtil.MSG_INSTITUTION_ID);

                    // <XML><MessageType>1200</MessageType><ProcCode>111011</ProcCode><OriginatingChannel>Mobile</OriginatingChannel><LocalTxnDtTime>20211208125622</LocalTxnDtTime><Stan>003164</Stan><RemitterMobNo>8208552893</RemitterMobNo><RemitterAccNo>002103760021576</RemitterAccNo><RemitterName>test</RemitterName><InstitutionID>406</InstitutionID><ChannelRefNo>MOB20211208125256287</ChannelRefNo></XML>

                    Log.d("msg.GetXml()" + "" + ":", msg.GetXml());

                    String base64EncodedRequestJson = Base64.encodeToString(msg.GetXml().getBytes(), Base64.NO_WRAP);

                    String jsonStringRequest = "{\"data\":\"" + base64EncodedRequestJson + "\"}";
                    TrustMethods.LogMessage("", "base64EncodedRequestJson : " + jsonStringRequest);

                    if (!url.equals("")) {

                        result = HttpClientWrapper.postWitAuthHeader(url, jsonStringRequest, AppConstants.getAuth_token());

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
                        response = jsonResponse.has("response") ? jsonResponse.getString("response") : "NA";

                        String responseValue = TrustMethods.decodeBase64(response);
                        Log.d("responseValue", responseValue);

                        if (!TextUtils.isEmpty(responseValue)) {
                            response = responseValue;
                        } else {
                            error = "No Record Found";
                        }

                      /*  ResponseEntity resParse = TMessage.ParseMessage(responseValue);
                        TMessage responseMsg = (TMessage) resParse.response;

                        if (responseMsg.ActCode.Value.equals("000")) {
                            finalResponse = responseMsg.Message.Value;
                        } else {
                            error = "Act Code: " + responseMsg.ActCode.Value + ": " + responseMsg.ActCodeDesc.Value;
                        }*/
                        onProgressUpdate();
                    } else {
                        errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
                        error = jsonResponse.has("error_message") ? jsonResponse.getString("error_message") : "NA";
                    }
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

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                if (!this.error.equals("")) {
                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(UPICollectActivity.this,
                                getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                    //   cardBalMsg.setVisibility(View.GONE);
                } else {
                    if (response != null) {
                        Intent pinActivationIntent = new Intent(UPICollectActivity.this, UpiQRDisplayActivity.class);
                        pinActivationIntent.putExtra("isUpiSelfBarcodeGenerator", true);
                        pinActivationIntent.putExtra("upiUrl", response);
                        pinActivationIntent.putExtra("accName", remitterAccName);
                        pinActivationIntent.putExtra("accNumber", accountNo);
                        startActivity(pinActivationIntent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}