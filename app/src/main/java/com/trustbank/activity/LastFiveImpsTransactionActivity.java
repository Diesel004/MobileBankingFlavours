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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.Model.LastFiveImpsTransactionModel;
import com.trustbank.R;
import com.trustbank.adapter.LastFiveTransactionAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LastFiveImpsTransactionActivity extends AppCompatActivity implements AlertDialogOkListener {
    private String TAG = MiniStatementActivity.class.getSimpleName();
    private TrustMethods method;
    private Spinner selectAccSpinner;
    private CardView cardFiveImpsTrans;
    private RecyclerView recyclerLastFiveTrans;
    private CoordinatorLayout coordinatorLayout;
    ArrayList<GetUserProfileModal> accountsArrayList;
    List<String> accountList;
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
                        TrustMethods.naviagteToSplashScreen(LastFiveImpsTransactionActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(LastFiveImpsTransactionActivity.this, false);
        setContentView(R.layout.activity_last_five_imps_transaction);
        inIt();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        try {
            if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
                bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void inIt() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        method = new TrustMethods(LastFiveImpsTransactionActivity.this);
        selectAccSpinner = findViewById(R.id.selectAccSpinnerId);
        cardFiveImpsTrans = findViewById(R.id.cardFiveImpsTransId);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerLastFiveTrans = findViewById(R.id.recyclerLastFiveTransId);
        recyclerLastFiveTrans.setLayoutManager(linearLayoutManager);

//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerLastFiveTrans.setLayoutManager(mLayoutManager);
        accountNoSpinner();
        setAccSpinner();
    }

    private void accountNoSpinner() {
        try {
            accountsArrayList = method.getArrayList(LastFiveImpsTransactionActivity.this, "AccountListPref");

            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                accountList = new ArrayList<>();
                accountList.add(0, "Select Account Number");
                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    if (TrustMethods.isAccountTypeValidRDLN(getUserProfileModal.getActType())) {
                        String accNo = getUserProfileModal.getAccNo();
                        String accTypeCode = getUserProfileModal.getAcTypeCode();
                        accountList.add(accNo + " - " + accTypeCode);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(LastFiveImpsTransactionActivity.this, android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectAccSpinner.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setAccSpinner() {
        selectAccSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position != 0) {
                    String selectedAccNo = TrustMethods.getValidAccountNo((String) adapterView.getItemAtPosition(position));

                    if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(LastFiveImpsTransactionActivity.this)) {
                        if (NetworkUtil.getConnectivityStatus(LastFiveImpsTransactionActivity.this)) {
                            new ImpsLastFiveTransAsyncTask(LastFiveImpsTransactionActivity.this, selectedAccNo.trim()).execute();
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                        }
                    } else {
                        TrustMethods.displaySimErrorDialog(LastFiveImpsTransactionActivity.this);
                    }
                } else {
                    cardFiveImpsTrans.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onDialogOk(int resultCode) {
        Intent intentLogin = new Intent(getApplicationContext(), LockActivity.class);
        intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentLogin);
        method.activityCloseAnimation();
    }


    @SuppressLint("StaticFieldLeak")
    private class ImpsLastFiveTransAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse;
        ProgressDialog pDialog;
        String mAccNo;
        ArrayList<LastFiveImpsTransactionModel> lastFiveImpsTransactionList;
        String resp;
        String result;
        private String errorCode;

        public ImpsLastFiveTransAsyncTask(Context ctx, String accNo) {
            this.ctx = ctx;
            this.mAccNo = accNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LastFiveImpsTransactionActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
             //   String jsonString = "{\"filter\":[\"stan\"]}";
                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(LastFiveImpsTransactionActivity.this, jsonString, TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {
                    TMessage requestXmlMsg = msgDto.GetImpsLastFiveTransactionDto(TMessageUtil.GetLocalTxnDtTime(),
                            generateStanRRNModel.getStan(), AppConstants.getUSERMOBILENUMBER(), mAccNo,
                            AppConstants.INSTITUTION_ID,generateStanRRNModel.getChannel_ref_no()); //TMessageUtil.MSG_INSTITUTION_ID);
                    Log.d("msg.GetXml()" + "" + ":", requestXmlMsg.GetXml());

                    String base64EncodedRequestJson = Base64.encodeToString(requestXmlMsg.GetXml().getBytes(), Base64.NO_WRAP);

                    String jsonStringRequest = "{\"data\":\"" + base64EncodedRequestJson + "\"}";
                    TrustMethods.LogMessage("", "base64EncodedRequestJson : " + jsonStringRequest);

                    if (!url.equals("")) {
                        TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWitAuthHeader(url, jsonStringRequest, AppConstants.getAuth_token());
                        TrustMethods.LogMessage(TAG, "Frm Enquiry details response-->" + result);
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
                        resp = jsonResponse.has("response") ? jsonResponse.getString("response") : "NA";

                        String responseValue = TrustMethods.decodeBase64(resp);
                        Log.d("responseValue", responseValue);

                        ResponseEntity resParse = TMessage.ParseMessage(responseValue);
                        lastFiveImpsTransactionList = new ArrayList<>();

                        TMessage responseMsg = (TMessage) resParse.response;

                        if (responseMsg.ActCode.Value.equals("000")) {
                            for (int i = 0; i < responseMsg.Record.RecordList.size(); i++) {
                                Log.d("Record # ", String.valueOf(i));
                                Log.d("TAG", responseMsg.Record.RecordList.get(i).TranDateTime + " " + responseMsg.Record.RecordList.get(i).TransRefNo + " " + responseMsg.Record.RecordList.get(i).TranAmount);

                                LastFiveImpsTransactionModel lastFiveImpsTransactionModel = new LastFiveImpsTransactionModel();
                                lastFiveImpsTransactionModel.setDate(responseMsg.Record.RecordList.get(i).TranDateTime);
                                lastFiveImpsTransactionModel.setAmount(responseMsg.Record.RecordList.get(i).TranAmount);
                                lastFiveImpsTransactionModel.setTransactionType(responseMsg.Record.RecordList.get(i).TranIndicator);
                                lastFiveImpsTransactionModel.setBeneficiery(responseMsg.Record.RecordList.get(i).BeneName);
                                lastFiveImpsTransactionModel.setRefNo(responseMsg.Record.RecordList.get(i).TransRefNo);
                                lastFiveImpsTransactionList.add(lastFiveImpsTransactionModel);
                            }
                        } else {
                            error = "Act Code: " + responseMsg.ActCode.Value + ": " + responseMsg.ActCodeDesc.Value;
                        }
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
            return resp;


        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (!this.error.equals("")) {
                cardFiveImpsTrans.setVisibility(View.GONE);
                if (TrustMethods.isSessionExpired(errorCode)) {
                    AlertDialogMethod.alertDialogOk(LastFiveImpsTransactionActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                } else if (error.trim().equalsIgnoreCase("Act Code: 124: Transaction Request has been rejected as Transaction not found")) {
                    TrustMethods.showMessage(getApplicationContext(), "Transaction not found");
                } else {
                    TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                }

            } else {
                if (lastFiveImpsTransactionList != null && lastFiveImpsTransactionList.size() != 0) {
                    cardFiveImpsTrans.setVisibility(View.VISIBLE);
                    LastFiveTransactionAdapter lastFiveTransactionAdapter = new LastFiveTransactionAdapter(LastFiveImpsTransactionActivity.this, lastFiveImpsTransactionList);
                    recyclerLastFiveTrans.setAdapter(lastFiveTransactionAdapter);
                  //  recyclerLastFiveTrans.setLayoutManager(new LinearLayoutManager(LastFiveImpsTransactionActivity.this));
                    recyclerLastFiveTrans.setHasFixedSize(true);

                } else {
                    cardFiveImpsTrans.setVisibility(View.GONE);
                }
            }
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
        TrustMethods.showBackButtonAlert(LastFiveImpsTransactionActivity.this);
    }
}
