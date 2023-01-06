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

import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
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

public class BalanceEnquiryActivity extends AppCompatActivity implements AlertDialogOkListener {

    private String TAG = BalanceEnquiryActivity.class.getSimpleName();
    private TrustMethods method;
    private Spinner selectAccSpinner;
    private CardView cardBalMsg;
    private CoordinatorLayout coordinatorLayout;
    private TextView txtAvailBal;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private List<String> accountList;
    private AlertDialogOkListener alertDialogOkListener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            if (savedInstanceState != null) {
                Object currentPID = String.valueOf(android.os.Process.myPid());
                // Check current PID with old PID
                if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                    // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                    TrustMethods.naviagteToSplashScreen(BalanceEnquiryActivity.this);
                }
            }
        }
        SetTheme.changeToTheme(BalanceEnquiryActivity.this, false);
        setContentView(R.layout.activity_balance_enquiry);
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        method = new TrustMethods(BalanceEnquiryActivity.this);
        selectAccSpinner = findViewById(R.id.selectAccSpinnerId);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        cardBalMsg = findViewById(R.id.cardBalMsgId);
        txtAvailBal = findViewById(R.id.txtAvailBalId);
        accNumberSpinner();
    }

    private void accNumberSpinner() {
        try {
            accountsArrayList = method.getArrayList(BalanceEnquiryActivity.this, "AccountListPref");
          //  Log.d("TAG", accountsArrayList.toString());
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(BalanceEnquiryActivity.this, android.R.layout.simple_spinner_item, accountList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectAccSpinner.setAdapter(adapter);
        }

        selectAccSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {


                if (position != 0) {
                    String accNo = TrustMethods.getValidAccountNo((String) adapterView.getItemAtPosition(position));
                    if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(BalanceEnquiryActivity.this)) {
                        if (NetworkUtil.getConnectivityStatus(BalanceEnquiryActivity.this)) {
                            new BalanceEnquiryAsyncTask(BalanceEnquiryActivity.this, accNo).execute();
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                        }
                    } else {
                        TrustMethods.displaySimErrorDialog(BalanceEnquiryActivity.this);
                    }
                } else {
                    cardBalMsg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onDialogOk(int resultCode) {
        if(resultCode == 0) {
            Intent intent = new Intent(getApplicationContext(), LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            method.activityCloseAnimation();
        }else if(resultCode == 55){

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class BalanceEnquiryAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        String result;
        ProgressDialog pDialog;
        String mAccNo;
        //        String action = "send_to_switch";
        String finalResponse;
        private String errorCode;

        public BalanceEnquiryAsyncTask(Context ctx, String accNo) {
            this.ctx = ctx;
            this.mAccNo = accNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BalanceEnquiryActivity.this);
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
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(BalanceEnquiryActivity.this, jsonString, TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {
                    TMessage msg = msgDto.GetBalanceInquiryDto(TMessageUtil.GetLocalTxnDtTime(),
                            generateStanRRNModel.getStan(),
                            AppConstants.getUSERMOBILENUMBER(),
                            mAccNo, "", AppConstants.getUSERNAME(),
                            AppConstants.INSTITUTION_ID,generateStanRRNModel.getChannel_ref_no()); //TMessageUtil.MSG_INSTITUTION_ID);

              // <XML><MessageType>1200</MessageType><ProcCode>111011</ProcCode><OriginatingChannel>Mobile</OriginatingChannel><LocalTxnDtTime>20211208125622</LocalTxnDtTime><Stan>003164</Stan><RemitterMobNo>8208552893</RemitterMobNo><RemitterAccNo>002103760021576</RemitterAccNo><RemitterName>test</RemitterName><InstitutionID>406</InstitutionID><ChannelRefNo>MOB20211208125256287</ChannelRefNo></XML>

                    Log.d("msg.GetXml()" + "" + ":", msg.GetXml());

                    String base64EncodedRequestJson = Base64.encodeToString(msg.GetXml().getBytes(), Base64.NO_WRAP);

                    String jsonStringRequest = "{\"data\":\"" + base64EncodedRequestJson + "\"}";
                    TrustMethods.LogMessage("", "base64EncodedRequestJson : " + jsonStringRequest);

                    if (!url.equals("")) {
                        TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWitAuthHeader(url, jsonStringRequest, AppConstants.getAuth_token());
                        TrustMethods.LogMessage(TAG, "Frm Enquiry details response-->" + response);
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
                            finalResponse = responseMsg.Message.Value;
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
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(BalanceEnquiryActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(BalanceEnquiryActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                    cardBalMsg.setVisibility(View.GONE);
                } else {
                    if (response != null) {
                        cardBalMsg.setVisibility(View.VISIBLE);
                        txtAvailBal.setText(finalResponse);
                    } else {
                        cardBalMsg.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        TrustMethods.showBackButtonAlert(BalanceEnquiryActivity.this);
    }

}