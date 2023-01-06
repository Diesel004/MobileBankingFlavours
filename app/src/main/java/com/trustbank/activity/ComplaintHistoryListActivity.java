package com.trustbank.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.trustbank.Model.BbpsTransactionRequestModel;
import com.trustbank.Model.ComplaintsListModel;
import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.Model.Transaction_list_Model;
import com.trustbank.R;
import com.trustbank.adapter.Adapter_Complaint_list;
import com.trustbank.interfaces.AlertDialogOkListener;
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

import java.util.ArrayList;
import java.util.List;

public class ComplaintHistoryListActivity extends AppCompatActivity implements View.OnClickListener, AlertDialogOkListener {

    private RecyclerView recyclerView;
    private List<Transaction_list_Model> transactionlistmodelArrayList = null;
    private Adapter_Complaint_list adapter1;
    CoordinatorLayout accDetCoordinatorLayoutId;
    private AlertDialogOkListener alertDialogOkListener = this;
    private String fromDate = "";
    private String toDate = "";
    private TrustMethods trustMethods;
    private String remmittterAccNo;
    private String remmitterName;


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
                        TrustMethods.naviagteToSplashScreen(ComplaintHistoryListActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(ComplaintHistoryListActivity.this, false);
        setContentView(R.layout.activity_complaint_history_list);

        Init();
    }

    private void Init() {

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            trustMethods = new TrustMethods(this);
            recyclerView = findViewById(R.id.recyclerview);
            accDetCoordinatorLayoutId = findViewById(R.id.accDetCoordinatorLayoutId);
            fromDate = TrustMethods.formatDate(getIntent().getStringExtra("FromDate"), "dd/MM/yyyy", "yyyy-MM-dd");
            toDate = TrustMethods.formatDate(getIntent().getStringExtra("ToDate"), "dd/MM/yyyy", "yyyy-MM-dd");


            if (NetworkUtil.getConnectivityStatus(ComplaintHistoryListActivity.this)) {
                new CompleteRegTransactionListAsyncTask(this, fromDate, toDate).execute();
            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), accDetCoordinatorLayoutId);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public void onDialogOk(int resultCode) {
        switch (resultCode) {
            case 0:
                Intent intent = new Intent(ComplaintHistoryListActivity.this, LockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
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
        TrustMethods.showBackButtonAlert(ComplaintHistoryListActivity.this);
    }

    @SuppressLint("StaticFieldLeak")
    private class CompleteRegTransactionListAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        ProgressDialog pDialog;
        String response, finalResponse;
        String result;
        private String errorCode;
        private TMessage msg;
        List<ComplaintsListModel> complaintsListModelList;
        private String toDate, fromDate;
        private String TranAmount, TranType, ImpsMessage, benname, BeneMobileNo, BeneMMID, TrandateTime, BeneAccNo, IFSCCode;

        public CompleteRegTransactionListAsyncTask(Context ctx, String fromDate, String toDate) {
            this.ctx = ctx;
            this.fromDate = fromDate;
            this.toDate = toDate;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ComplaintHistoryListActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {

            try {

                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(ComplaintHistoryListActivity.this, jsonString,
                        TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    if (generateStanRRNModel.getError().equalsIgnoreCase("Old auth token.")) {
                        errorCode = "9004";

                    }
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {

                    getIMPSremitterAccountsDetails();
                    JSONObject mainJsonObject = new JSONObject();
                    JSONObject billerjsonObj = new JSONObject();
                    billerjsonObj.put("fromDate", fromDate);
                    billerjsonObj.put("toDate", toDate);
                    mainJsonObject.put("complainReq", billerjsonObj);

                    String base64Data = Base64.encodeToString(mainJsonObject.toString().getBytes(), Base64.NO_WRAP);

                    //YB311012SNKCKJXMYC6N
                    msg = msgDto.ShowHistroyComplaintsRegDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                            AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no(), "", base64Data, AppConstants.getUSERMOBILENUMBER(),
                            "", remmittterAccNo,
                            remmitterName, "",
                            ""/*mMmid*/); //miniStatementModel.getChannelRefNo()
                    Log.d("msg.GetXml():", msg.GetXml());


                    String base64EncodedRequestJson = Base64.encodeToString(msg.GetXml().getBytes(), Base64.NO_WRAP);

                    String jsonStringRequest = "{\"data\":\"" + base64EncodedRequestJson + "\"}";
                    TrustMethods.LogMessage("", "base64EncodedRequestJson : " + jsonStringRequest);

                    if (!url.equals("")) {
                        result = HttpClientWrapper.postWitInstituteAuthHeader(url, jsonStringRequest, AppConstants.getAuth_token(), AppConstants.INSTITUTION_ID);
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

                        if (responseMsg.ActCode.Value.equals("000")) {
                            String respDecoded = responseMsg.BBPS_RESPONSE_DATA.Value;
                            finalResponse = TrustMethods.decodeBase64(respDecoded);

                            JSONObject jsonObject = new JSONObject(finalResponse);
                            String respCode = jsonObject.has("respCode") ? jsonObject.getString("respCode") : "";
                            String status = jsonObject.has("status") ? jsonObject.getString("status") : "";

                            if (status.equalsIgnoreCase("SUCCESS")) {

                                JSONObject jsonObjectRes = jsonObject.has("response") ? jsonObject.getJSONObject("response") : null;
                                if (jsonObjectRes != null) {

                                    JSONObject txnStatusComplainRespObject = jsonObjectRes.has("txnStatusComplainResp") ? jsonObjectRes.getJSONObject("txnStatusComplainResp") : null;
                                    if (txnStatusComplainRespObject != null) {

                                        String code = txnStatusComplainRespObject.has("responseCode") ? txnStatusComplainRespObject.getString("responseCode") : "";
                                        if (code.equalsIgnoreCase("000")) {

                                            JSONArray txnListJsonArray = txnStatusComplainRespObject.has("txnList") ? txnStatusComplainRespObject.getJSONArray("txnList") : null;
                                            if (txnListJsonArray != null && txnListJsonArray.length() > 0) {

                                                complaintsListModelList = new ArrayList<>();

                                                for (int i = 0; i < txnListJsonArray.length(); i++) {
                                                    ComplaintsListModel complaintsListModel = new ComplaintsListModel();
                                                    JSONObject jsonObjectArray = txnListJsonArray.getJSONObject(i);
                                                    String txnReferenceId = jsonObjectArray.has("txnReferenceId") ? jsonObjectArray.getString("txnReferenceId") : "";
                                                    String amount = jsonObjectArray.has("amount") ? jsonObjectArray.getString("amount") : "";
                                                    String txnDate = jsonObjectArray.has("txnDate") ? jsonObjectArray.getString("txnDate") : "";
                                                    String agentId = jsonObjectArray.has("agentId") ? jsonObjectArray.getString("agentId") : "";
                                                    String billerId = jsonObjectArray.has("billerId") ? jsonObjectArray.getString("billerId") : "";
                                                    String txnStatus = jsonObjectArray.has("txnStatus") ? jsonObjectArray.getString("txnStatus") : "";
                                                    complaintsListModel.setTxnReferenceId(txnReferenceId);
                                                    complaintsListModel.setAmount(amount);
                                                    complaintsListModel.setTxnDate(txnDate);
                                                    complaintsListModel.setAgentId(agentId);
                                                    complaintsListModel.setBillerId(billerId);
                                                    complaintsListModel.setTxnStatus(txnStatus);
                                                    complaintsListModelList.add(complaintsListModel);
                                                }

                                            } else {
                                                error = "NO Record found";
                                            }
                                        } else {
                                            error = "NO Record found";
                                        }
                                    }
                                }
                            }


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

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(ComplaintHistoryListActivity.this, "Session Expired!", "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    }
                } else {

                    if (complaintsListModelList != null && complaintsListModelList.size() != 0) {
                        adapter1 = new Adapter_Complaint_list(ComplaintHistoryListActivity.this, complaintsListModelList);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(ComplaintHistoryListActivity.this, 1, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter1);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Data Found!!!", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ComplaintHistoryListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getIMPSremitterAccountsDetails() {
        ArrayList<GetUserProfileModal> accountsArrayList = trustMethods.getArrayList(ComplaintHistoryListActivity.this, "AccountListPref");
        for (int i = 0; i < accountsArrayList.size(); i++) {
            GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
            if (TrustMethods.isAccountTypeIsImpsRegValid(getUserProfileModal.getActType(), getUserProfileModal.getIs_imps_reg())) {
                remmittterAccNo = getUserProfileModal.getAccNo();
                remmitterName = getUserProfileModal.getName();
                break;
            }
        }
    }
}