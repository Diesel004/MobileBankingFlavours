package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trustbank.Model.PPSRequestModel;
import com.trustbank.R;
import com.trustbank.adapter.PPSRequestEnquiryAdapter;
import com.trustbank.interfaces.AlertDialogListener;
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


public class FrmPPSServiceRequestEnquiryDetails extends AppCompatActivity implements AlertDialogOkListener, AlertDialogListener {

    private String TAG = FrmPPSServiceRequestEnquiryDetails.class.getSimpleName();
    private TrustMethods method;
    private CardView cardMiniStatement;
    private AlertDialogOkListener alertDialogOkListener = this;
    private RecyclerView recyclerPPSRequestId;
    private CoordinatorLayout coordinatorLayout;

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
                        TrustMethods.naviagteToSplashScreen(FrmPPSServiceRequestEnquiryDetails.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(FrmPPSServiceRequestEnquiryDetails.this, false);
        setContentView(R.layout.activity_frm_pps_request_enquiry_details);
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
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            method = new TrustMethods(FrmPPSServiceRequestEnquiryDetails.this);

            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            cardMiniStatement = findViewById(R.id.cardMiniStatementId);
            recyclerPPSRequestId = findViewById(R.id.recyclerPPSRequestId);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerPPSRequestId.setLayoutManager(mLayoutManager);


            if (getIntent().getExtras() != null) {
                Intent i = getIntent();
                String accountNo = i.getStringExtra("accountNo");

                String dateFrom = TrustMethods.formatDate(getIntent().getStringExtra("fromDate"), "dd/MM/yyyy", "yyyy-MM-dd");
                String dateTo = TrustMethods.formatDate(getIntent().getStringExtra("toDate"), "dd/MM/yyyy", "yyyy-MM-dd");

                if (NetworkUtil.getConnectivityStatus(FrmPPSServiceRequestEnquiryDetails.this)) {
                    new PPSServiceRequestDetails(FrmPPSServiceRequestEnquiryDetails.this, accountNo, dateFrom, dateTo).execute();
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class PPSServiceRequestDetails extends AsyncTask<Void, Void, String> {
        String error = "";

        Context ctx;
        ProgressDialog pDialog;
        String mAccNo, fromDate, toDate;
        String response;
        String action = "PPS_GET_REQUEST";
        private String errorCode;
        private List<PPSRequestModel> ppsRequestModelList = null;

        public PPSServiceRequestDetails(Context ctx, String accNo, String fromDate, String toDate) {
            this.ctx = ctx;
            this.mAccNo = accNo;
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FrmPPSServiceRequestEnquiryDetails.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {

                String url = TrustURL.MobileNoVerifyUrl();

                String jsonString = "{\"account_number\":\"" + mAccNo + "\",\"from_date\":\"" + fromDate + "\"," + "\"to_date\":\"" + toDate + "\"}";  //TODO
                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);


                if (!url.equals("")) {
                    response = HttpClientWrapper.postWithActionAuthToken(url, jsonString, action, AppConstants.getAuth_token());
                }
                if (response == null || response.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResponse = (new JSONObject(response));
                if (jsonResponse.has("error")) {
                    error = jsonResponse.getString("error");
                    return error;
                }
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";

                if (responseCode.equals("1")) {

                    JSONObject responseJsonObject = jsonResponse.has("response") ? jsonResponse.getJSONObject("response") : null;

                    if (responseJsonObject != null) {
                        ppsRequestModelList = new ArrayList<>();
                        JSONArray jsonArrayTable = responseJsonObject.has("account") ? responseJsonObject.getJSONArray("account") : null;
                        if (jsonArrayTable != null && !jsonArrayTable.isNull(0)) {
                            for (int i = 0; i < jsonArrayTable.length(); i++) {
                                JSONObject jsonObject = jsonArrayTable.getJSONObject(i);
                                PPSRequestModel ppsRequestModel = new PPSRequestModel();
                                String cheque_no = jsonObject.has("cheque_no") ? jsonObject.getString("cheque_no") : "";
                                String req_date = jsonObject.has("req_date") ? jsonObject.getString("req_date") : "";
                                String cheque_date = jsonObject.has("cheque_date") ? jsonObject.getString("cheque_date") : "";
                                String amount = jsonObject.has("amount") ? jsonObject.getString("amount") : "";
                                String payee_name = jsonObject.has("payee_name") ? jsonObject.getString("payee_name") : "";
                                String status = jsonObject.has("status") ? jsonObject.getString("status") : "";
                                String channel = jsonObject.has("channel") ? jsonObject.getString("channel") : "";
                                ppsRequestModel.setChequeNo(cheque_no);
                                ppsRequestModel.setReqDate(req_date);
                                ppsRequestModel.setChequeDate(cheque_date);
                                ppsRequestModel.setAmount(amount);
                                ppsRequestModel.setPayeeName(payee_name);
                                ppsRequestModel.setStatus(status);
                                ppsRequestModel.setChannel(channel);
                                ppsRequestModelList.add(ppsRequestModel);
                            }

                        } else {
                            error = "Data not found";
                            return error;
                        }
                    } else {
                        error = "Data not found";
                        return error;
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
                        AlertDialogMethod.alertDialogOk(FrmPPSServiceRequestEnquiryDetails.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                    cardMiniStatement.setVisibility(View.GONE);

                } else {

                    if (ppsRequestModelList != null && ppsRequestModelList.size() != 0) {
                        cardMiniStatement.setVisibility(View.VISIBLE);
                        PPSRequestEnquiryAdapter lastFiveTransactionCBSAdapter = new PPSRequestEnquiryAdapter(FrmPPSServiceRequestEnquiryDetails.this, ppsRequestModelList);
                        recyclerPPSRequestId.setAdapter(lastFiveTransactionCBSAdapter);
                    } else {
                        cardMiniStatement.setVisibility(View.GONE);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
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
                method.activityCloseAnimation();
                break;

            default:
                break;
        }
    }

    @Override
    public void onDialogCancel(int resultCode) {
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
        TrustMethods.showBackButtonAlert(FrmPPSServiceRequestEnquiryDetails.this);
    }
}