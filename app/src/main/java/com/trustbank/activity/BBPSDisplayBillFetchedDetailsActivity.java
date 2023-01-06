package com.trustbank.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
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

import com.trustbank.Model.BBPSBillerResponseModel;
import com.trustbank.Model.BBPSTitlevalueModel;
import com.trustbank.Model.FundTransferSubModel;
import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.adapter.BBPDisplayBillPayerDetailsAdapter;
import com.trustbank.fragment.BBPSCustomAmountFragmentDialog;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.interfaces.BBPSClickEvenAmountInterface;
import com.trustbank.tcpconnection.tdto.MessageDtoBuilder;
import com.trustbank.tcpconnection.tmessage.TMessage;
import com.trustbank.tcpconnection.tmessage.TMessageUtil;
import com.trustbank.tcpconnection.util.ResponseEntity;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;


public class BBPSDisplayBillFetchedDetailsActivity extends AppCompatActivity implements BBPSClickEvenAmountInterface, AlertDialogOkListener {

    LinearLayout linearAmountId;
    RecyclerView recyclerCustomePropertyID;
    TextView txtBillAmountId, titleBillerNameId;
    Spinner spinnerFrmAct;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private TrustMethods trustMethods;
    private String accountNo, remitterAccName, billAmount;
    private Button btnPayBillsId, btCancelId;
    private EditText etRemarksId;

    CoordinatorLayout coordinatorLayoutId;
    private String billerName, billerId, refId;
    private JSONObject billDetailsJsonObject;

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
                        TrustMethods.naviagteToSplashScreen(BBPSDisplayBillFetchedDetailsActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(BBPSDisplayBillFetchedDetailsActivity.this, false);
        setContentView(R.layout.activity_b_b_p_s_display_bill_fetched_details);

        initcomponent();
    }

    private void initcomponent() {

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            trustMethods = new TrustMethods(BBPSDisplayBillFetchedDetailsActivity.this);
            titleBillerNameId = findViewById(R.id.titleBillerNameId);
            coordinatorLayoutId = findViewById(R.id.coordinatorLayoutId);
            linearAmountId = findViewById(R.id.linearAmountId);
            recyclerCustomePropertyID = findViewById(R.id.recyclerCustomePropertyID);
            txtBillAmountId = findViewById(R.id.txtBillAmountId);
            spinnerFrmAct = findViewById(R.id.spinnerFrmAct);
            etRemarksId = findViewById(R.id.etRemarksId);

            btnPayBillsId = findViewById(R.id.btnPayBillsId);

            Intent intent = getIntent();
            billerName = intent.getStringExtra("billerName");
            billerId = intent.getStringExtra("billerId");
            refId = intent.getStringExtra("refId");
            String billDetails = intent.getStringExtra("billDetailsJsonObject");
            billDetailsJsonObject = new JSONObject(billDetails);

            HashMap<String, Object> billDetailsMap = (HashMap<String, Object>) intent.getExtras().get("billDetailsMap");
            HashMap<String, Object> additionalInfoMap = (HashMap<String, Object>) intent.getExtras().get("additionalInfoMap");
            BBPSBillerResponseModel bbpsBillerResponseModel = (BBPSBillerResponseModel) intent.getSerializableExtra("BBPSBillerResponseModel");

            List<BBPSTitlevalueModel> bbpsTitlevalueModelList = new ArrayList<>();
            if (billDetailsMap != null && billDetailsMap.size() != 0) {
                for (String key : billDetailsMap.keySet()) {
                    BBPSTitlevalueModel bbpsTitlevalueModel = new BBPSTitlevalueModel();
                    String value = (String) billDetailsMap.get(key);
                    bbpsTitlevalueModel.setTitle(key);
                    bbpsTitlevalueModel.setValue(value);
                    bbpsTitlevalueModelList.add(bbpsTitlevalueModel);
                }
            }

            if (additionalInfoMap != null && additionalInfoMap.size() != 0) {
                for (String key : additionalInfoMap.keySet()) {
                    BBPSTitlevalueModel bbpsTitlevalueModel = new BBPSTitlevalueModel();
                    String value = (String) additionalInfoMap.get(key);
                    bbpsTitlevalueModel.setTitle(key);
                    bbpsTitlevalueModel.setValue(value);
                    bbpsTitlevalueModelList.add(bbpsTitlevalueModel);
                }
            }


            String consumerName = Objects.requireNonNull(bbpsBillerResponseModel).getCustomerName();
            String dueDate = bbpsBillerResponseModel.getDueDate();

            String billDate = bbpsBillerResponseModel.getBillDate();
            String billNumber = bbpsBillerResponseModel.getBillNumber();

            billAmount = bbpsBillerResponseModel.getAmount();

            if (!TextUtils.isEmpty(consumerName)) {
                BBPSTitlevalueModel bbpsTitlevalueModel = new BBPSTitlevalueModel();
                bbpsTitlevalueModel.setTitle("Customer Name");
                bbpsTitlevalueModel.setValue(consumerName);
                bbpsTitlevalueModelList.add(bbpsTitlevalueModel);
            }

            if (!TextUtils.isEmpty(dueDate)) {
                BBPSTitlevalueModel bbpsTitlevalueModel = new BBPSTitlevalueModel();
                bbpsTitlevalueModel.setTitle("Due Date");
                bbpsTitlevalueModel.setValue(dueDate);
                bbpsTitlevalueModelList.add(bbpsTitlevalueModel);
            }


            if (!TextUtils.isEmpty(billDate)) {
                BBPSTitlevalueModel bbpsTitlevalueModel = new BBPSTitlevalueModel();
                bbpsTitlevalueModel.setTitle("Bill Date");
                bbpsTitlevalueModel.setValue(billDate);
                bbpsTitlevalueModelList.add(bbpsTitlevalueModel);
            }

            if (!TextUtils.isEmpty(billDate)) {
                BBPSTitlevalueModel bbpsTitlevalueModel = new BBPSTitlevalueModel();
                bbpsTitlevalueModel.setTitle("Bill Number");
                bbpsTitlevalueModel.setValue(billNumber);
                bbpsTitlevalueModelList.add(bbpsTitlevalueModel);
            }

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerCustomePropertyID.setLayoutManager(mLayoutManager);

            BBPDisplayBillPayerDetailsAdapter bbpsSearchBillerAdapter = new BBPDisplayBillPayerDetailsAdapter(BBPSDisplayBillFetchedDetailsActivity.this,
                    bbpsTitlevalueModelList);
            recyclerCustomePropertyID.setAdapter(bbpsSearchBillerAdapter);
            txtBillAmountId.setText("\u20B9" + " " + billAmount);
            titleBillerNameId.setText(billerName);
//            billAmount = amount;
            setAccountNoSpinner();

            linearAmountId.setOnClickListener(view -> {
                try {
                    HashMap<String, Object> bbpsTagsMap = bbpsBillerResponseModel.getBillTagsMap();
                    if (bbpsTagsMap != null && bbpsTagsMap.size() != 0) {
                        List<BBPSTitlevalueModel> bbpsTitlevalueModelsList = new ArrayList<>();
                        BBPSTitlevalueModel bbpsTitlevalueModel = new BBPSTitlevalueModel();
                        bbpsTitlevalueModel.setTitle("Due Date");
                        bbpsTitlevalueModel.setValue(billAmount);
                        bbpsTitlevalueModel.setEnable(true);
                        bbpsTitlevalueModelsList.add(bbpsTitlevalueModel);

                        for (String key : bbpsTagsMap.keySet()) {
                            String value = (String) bbpsTagsMap.get(key);
                            BBPSTitlevalueModel bbpsTitlevalueModel1 = new BBPSTitlevalueModel();
                            bbpsTitlevalueModel1.setTitle(key);
                            bbpsTitlevalueModel1.setValue(value);
                            bbpsTitlevalueModelsList.add(bbpsTitlevalueModel1);
                        }

                        FragmentManager manager = getSupportFragmentManager();
                        DialogFragment newFragment = BBPSCustomAmountFragmentDialog.newInstance(bbpsTitlevalueModelsList, this);
                        newFragment.show(manager, "dialog");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            btnPayBillsId.setOnClickListener(view -> {

                if (spinnerFrmAct.getSelectedItem().equals("Select Account Number")) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayoutId);
                } else {
                    Log.e("account", accountNo);
                    Log.e("amt", billAmount);

                    String remark = etRemarksId.getText().toString();
                    createSaveJonObject(billerId, billerName, billAmount, accountNo,
                            remitterAccName, remark, refId, billNumber);

                }
            });

            btCancelId.setOnClickListener(view -> {
                finish();
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSaveJonObject(String billerId, String billerName, String billAmount,
                                     String accountNo, String remitterAccName, String remark, String refId,
                                     String billNumber) {

        try {

            JSONObject mainJsonObject = new JSONObject();
            JSONObject amountJsonObject = new JSONObject();
            amountJsonObject.put("amount", billAmount);
            mainJsonObject.put("amountDetails", amountJsonObject);
            mainJsonObject.put("billDetails", billDetailsJsonObject); //get from previus page such as "billDetails": {"billerId": "CESU00000ODI01","customerParams": [{"name": "Consumer Id","value": "80003948751"}]},
            JSONObject paymentDetailsJsonObject = new JSONObject();

            mainJsonObject.put("paymentDetails", paymentDetailsJsonObject);
            mainJsonObject.put("refId", refId);

            List<FundTransferSubModel> fundTransferSubModalList = new ArrayList<>();
            FundTransferSubModel fundTransferSubModel = new FundTransferSubModel();
            fundTransferSubModel.setAccNo(accountNo);
            fundTransferSubModel.setRemitterAccName(remitterAccName);
            fundTransferSubModel.setAmt(billAmount);
            fundTransferSubModel.setRemark(remark);
            fundTransferSubModel.setBillerId(billerId);
            fundTransferSubModel.setBillerName(billerName);
            fundTransferSubModel.setRefId(refId);
            fundTransferSubModel.setBillNumber(billNumber);
            fundTransferSubModalList.add(fundTransferSubModel);

            Intent intent = new Intent(BBPSDisplayBillFetchedDetailsActivity.this, OtpVerificationActivity.class);
            intent.putExtra("checkTransferType", "billPayTransactions");
            intent.putExtra("fundTransferDataList", (Serializable) fundTransferSubModalList);
            intent.putExtra("billPayObject", mainJsonObject.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            trustMethods.activityOpenAnimation();




        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setAccountNoSpinner() {
        try {
            accountsArrayList = trustMethods.getArrayList(BBPSDisplayBillFetchedDetailsActivity.this, "AccountListPref");

            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                List<String> accountList = new ArrayList<>();
                accountList.add(0, "Select Account Number");
                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    if (TrustMethods.isAccountTypeIsImpsRegValid(getUserProfileModal.getActType(), getUserProfileModal.getIs_imps_reg())) {
                        String accNo = getUserProfileModal.getAccNo();
                        String accTypeCode = getUserProfileModal.getAcTypeCode();
                        accountList.add(accNo + " - " + accTypeCode);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(BBPSDisplayBillFetchedDetailsActivity.this, android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFrmAct.setAdapter(adapter);

                spinnerFrmAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


    @Override
    public void getSelectedAmount(String amount) {
        billAmount = amount;
        txtBillAmountId.setText("\u20B9" + " " + amount);
    }

/*
    @SuppressLint("StaticFieldLeak")
    private class BillPaymentAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "", responseError = "", status = "";
        private Context ctx;
        private String response;
        private String result;
        private ProgressDialog pDialog;
        private String data;
        //        String action = "send_to_switch";
        private String finalResponse;
        private String errorCode, errorDetails;

        HashMap<String, Object> billDetailsMap = new HashMap<>();
        HashMap<String, Object> additionalInfoMap = new HashMap<>();
        BBPSBillerResponseModel bbpsBillerResponseModel;
        private String accountNo, remitterAccName, billAmount, remark, billerId, billerName, billNumber;
        private String doubtfullResponse;


        public BillPaymentAsyncTask(Context ctx, String data, String accountNo,
                                    String remitterAccName,
                                    String billAmount, String remark,
                                    String billerId, String billerName, String billNumber) {
            this.ctx = ctx;
            this.data = data;
            this.accountNo = accountNo;
            this.remitterAccName = remitterAccName;
            this.billAmount = billAmount;
            this.remark = remark;
            this.billerId = billerId;
            this.billerName = billerName;
            this.billNumber = billNumber;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BBPSDisplayBillFetchedDetailsActivity.this);
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
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(BBPSDisplayBillFetchedDetailsActivity.this, jsonString, TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {
                    String base64Data = Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);

                    TMessage msg = msgDto.BillPaymentRequest(TMessageUtil.GetLocalTxnDtTime(),
                            generateStanRRNModel.getStan(),
                            generateStanRRNModel.getChannel_ref_no(), base64Data, accountNo,
                            AppConstants.getUSERMOBILENUMBER(), remitterAccName, billAmount, remark, billerId,
                            billerName, billNumber); //TMessageUtil.MSG_INSTITUTION_ID);

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

                        ResponseEntity resParse = TMessage.ParseMessage(responseValue);
                        TMessage responseMsg = (TMessage) resParse.response;
                        if (responseMsg.ActCode.Value.equals("000") || responseMsg.ActCode.Value.equals("0")) {
                            finalResponse = responseMsg.TransRefNo.Value;
                        } else if (responseMsg.ActCode.Value.equals(AppConstants.DOUBTFULL_TRANSACTION) || responseMsg.ActCode.Value.equals(AppConstants.DOUBTFULL)) {
                            doubtfullResponse = responseMsg.ActCodeDesc.Value;
                        } else {
                            error = "Act Code: " + responseMsg.ActCode.Value + ": " + responseMsg.ActCodeDesc.Value;
                        }

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
                if (!TextUtils.isEmpty(error) || !TextUtils.isEmpty(errorCode) || !TextUtils.isEmpty(responseError)) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(BBPSDisplayBillFetchedDetailsActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(BBPSDisplayBillFetchedDetailsActivity.this, status, error, getResources().getString(R.string.btn_ok), -1, false, alertDialogOkListener);

                    }

                } else {

                    if (!TextUtils.isEmpty(finalResponse)) {
                        String message = "Your " + billerName + " of Bill Number " + billNumber + " of ₹ " + billAmount +
                                " has been Paid Successfully." + "(Txn Ref No." + finalResponse + ")";
                        // String message = "Bill Payment of Rs. " + billAmount + " for Bill No. " + billNumber + " of Biller Name " + billerName + " Paid Successfully. (Txn Ref No." + finalResponse + ")";
                        AlertDialogMethod.alertDialogOk(BBPSDisplayBillFetchedDetailsActivity.this, getResources().getString(R.string.msg_transaction_success), message, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    } else if (!TextUtils.isEmpty(doubtfullResponse)) {
                        AlertDialogMethod.alertDialogOk(BBPSDisplayBillFetchedDetailsActivity.this, "Bill Payment!!!", doubtfullResponse, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 0) {
            Intent intent = new Intent(getApplicationContext(), LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            trustMethods.activityCloseAnimation();
        } else if (resultCode == 1) {
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            trustMethods.activityCloseAnimation();

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
        TrustMethods.showBackButtonAlert(BBPSDisplayBillFetchedDetailsActivity.this);
    }
}