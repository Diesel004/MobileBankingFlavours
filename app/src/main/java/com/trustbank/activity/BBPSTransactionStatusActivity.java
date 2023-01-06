package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.trustbank.Model.BbpsComplaintTypeModel;
import com.trustbank.Model.BbpsTransactionRequestModel;
import com.trustbank.Model.IMPSTransactionRequestModel;
import com.trustbank.R;
import com.trustbank.adapter.BbpsTransactionRequestEnquiryAdapter;

import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.interfaces.SessionErrorMessageListener;
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

public class BBPSTransactionStatusActivity extends AppCompatActivity implements View.OnClickListener, AlertDialogOkListener, SessionErrorMessageListener {

    private static final String TAG = ImpsCheckTransactionRequest.class.getSimpleName();
    private TextView editTextDateFrom;
    private Button buttonDisplay;
    private TrustMethods trustMethods;
    public static String dateFrom;
    private CardView cardViewIMPSTransactionDetails;
    private RecyclerView recyclerIMPSTransRequestId;
    private AlertDialogOkListener alertDialogOkListener = this;
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
                        TrustMethods.naviagteToSplashScreen(BBPSTransactionStatusActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(BBPSTransactionStatusActivity.this, false);
        setContentView(R.layout.activity_bbps_check_transaction_request);
        init();
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

    private void init() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        trustMethods = new TrustMethods(BBPSTransactionStatusActivity.this);
        trustMethods.activityOpenAnimation();
        editTextDateFrom = findViewById(R.id.editTextFromDate);
        buttonDisplay = findViewById(R.id.buttonDisplay);

        cardViewIMPSTransactionDetails = findViewById(R.id.cardViewIMPSTransactionDetails);
        recyclerIMPSTransRequestId = findViewById(R.id.recyclerIMPSTransRequestId);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerIMPSTransRequestId.setLayoutManager(mLayoutManager);

        editTextDateFrom.setOnClickListener(this);
        buttonDisplay.setOnClickListener(this);
        setDate();
    }


    private void setDate() {
        try {
            editTextDateFrom.setText(TrustMethods.getMonthYear());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.editTextFromDate:
                    trustMethods.datePicker(BBPSTransactionStatusActivity.this, editTextDateFrom);
                    break;

                case R.id.buttonDisplay:
                    dateFrom = editTextDateFrom.getText().toString();


                    if (dateFrom.trim().length() == 0) {
                        trustMethods.message(getApplicationContext(), "Please select from date");
                        return;
                    }
                    String date = TrustMethods.convertdmyintoymd(dateFrom);
                    if (NetworkUtil.getConnectivityStatus(BBPSTransactionStatusActivity.this)) {
                        new ImpsTransactionRequestDetails(BBPSTransactionStatusActivity.this, date).execute();
                    } else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                    }

                    break;


            }
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
        TrustMethods.showBackButtonAlert(BBPSTransactionStatusActivity.this);
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

    @Override
    public void onSessionError(String errorMessage) {
        AlertDialogMethod.alertDialogOk(BBPSTransactionStatusActivity.this, getResources().getString(R.string.error_session_expire),
                "",
                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
    }


    @SuppressLint("StaticFieldLeak")
    private class ImpsTransactionRequestDetails extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        ProgressDialog pDialog;
        String fromDate;
        String response;
        String action = "BILL_PAY_TRANSACTION_DETAILS";
        private String errorCode;
        private List<BbpsTransactionRequestModel> impsTransactionRequestModelList = null;
        private List<BbpsComplaintTypeModel> dispositionRequestModelList = null;

        public ImpsTransactionRequestDetails(Context ctx, String fromDate) {
            this.ctx = ctx;

            this.fromDate = fromDate;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BBPSTransactionStatusActivity.this);
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


                String jsonString = "{\"date\":\"" + fromDate + "\",\"profile_id\":\"" + AppConstants.getProfileID() + "\"}";  //TODO
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
                        JSONArray jsonArrayTable = responseJsonObject.has("disposition_request") ? responseJsonObject.getJSONArray("disposition_request") : null;
                        JSONArray jsonArrayBillPay = responseJsonObject.has("bill_pay_ft_request") ? responseJsonObject.getJSONArray("bill_pay_ft_request") : null;
                        impsTransactionRequestModelList = new ArrayList<>();

                        if (jsonArrayBillPay != null && !jsonArrayBillPay.isNull(0)) {
                            for (int i = 0; i < jsonArrayBillPay.length(); i++) {
                                BbpsTransactionRequestModel impsTransactionRequestModel = new BbpsTransactionRequestModel();
                                JSONObject jsonObject = jsonArrayBillPay.getJSONObject(i);
                                String logTime = jsonObject.has("log_time") ? jsonObject.getString("log_time") : "";
                                String switch_req_type = jsonObject.has("switch_req_type") ? jsonObject.getString("switch_req_type") : "";
                                String switch_res_code = jsonObject.has("switch_res_code") ? jsonObject.getString("switch_res_code") : "";
                                String rrn = jsonObject.has("rrn") ? jsonObject.getString("rrn") : "";
                                String channel_ref_no = jsonObject.has("channel_ref_no") ? jsonObject.getString("channel_ref_no") : "";
                                String amount = jsonObject.has("amount") ? jsonObject.getString("amount") : "";
                                String ben_mobile = jsonObject.has("ben_mobile") ? jsonObject.getString("ben_mobile") : "";
                                String ben_acno = jsonObject.has("ben_acno") ? jsonObject.getString("ben_acno") : "";
                                String ben_ifsc = jsonObject.has("ben_ifsc") ? jsonObject.getString("ben_ifsc") : "";
                                String ben_mmid = jsonObject.has("ben_mmid") ? jsonObject.getString("ben_mmid") : "";
                                String ben_name = jsonObject.has("ben_name") ? jsonObject.getString("ben_name") : "";
                                String rem_mobile = jsonObject.has("rem_mobile") ? jsonObject.getString("rem_mobile") : "";
                                String rem_acno = jsonObject.has("rem_acno") ? jsonObject.getString("rem_acno") : "";
                                String rem_name = jsonObject.has("rem_name") ? jsonObject.getString("rem_name") : "";
                                String ben_Upi_id = jsonObject.has("ben_upi_id") ? jsonObject.getString("ben_upi_id") : "";
                                String bbps_biller_id = jsonObject.has("bbps_biller_id") ? jsonObject.getString("bbps_biller_id") : "";

                                impsTransactionRequestModel.setLogTime(logTime);
                                impsTransactionRequestModel.setSwitchReqType(switch_req_type);
                                impsTransactionRequestModel.setSwitchResCode(switch_res_code);
                                impsTransactionRequestModel.setRrn(rrn);
                                impsTransactionRequestModel.setChannelRefNo(channel_ref_no);
                                impsTransactionRequestModel.setAmount(amount);
                                impsTransactionRequestModel.setBenMobile(ben_mobile);
                                impsTransactionRequestModel.setBenAcno(ben_acno);
                                impsTransactionRequestModel.setBenIfsc(ben_ifsc);
                                impsTransactionRequestModel.setBenMmid(ben_mmid);
                                impsTransactionRequestModel.setBenName(ben_name);
                                impsTransactionRequestModel.setRemMobile(rem_mobile);
                                impsTransactionRequestModel.setRemAcno(rem_acno);
                                impsTransactionRequestModel.setRemName(rem_name);
                                impsTransactionRequestModel.setBenUpiId(ben_Upi_id);
                                impsTransactionRequestModel.setBbpsBillerId(bbps_biller_id);
                                impsTransactionRequestModelList.add(impsTransactionRequestModel);
                            }

                        } else {
                            error = "Data not found";
                            return error;
                        }
                        dispositionRequestModelList = new ArrayList<>();
                        if (jsonArrayTable != null && !jsonArrayTable.isNull(0)) {
                            for (int i = 0; i < jsonArrayTable.length(); i++) {
                                BbpsComplaintTypeModel impsTransactionRequestModel = new BbpsComplaintTypeModel();
                                JSONObject jsonObject = jsonArrayTable.getJSONObject(i);
                                String complaint_type = jsonObject.has("Complain_type") ? jsonObject.getString("Complain_type") : "";
                                String disposition = jsonObject.has("disposition") ? jsonObject.getString("disposition") : "";
                                impsTransactionRequestModel.setComplaint_type(complaint_type);
                                impsTransactionRequestModel.setDisposition(disposition);
                                dispositionRequestModelList.add(impsTransactionRequestModel);

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
                    cardViewIMPSTransactionDetails.setVisibility(View.GONE);
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(BBPSTransactionStatusActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }


                } else {
                    cardViewIMPSTransactionDetails.setVisibility(View.VISIBLE);
                    if (impsTransactionRequestModelList != null && impsTransactionRequestModelList.size() != 0) {
                        BbpsTransactionRequestEnquiryAdapter lastFiveTransactionCBSAdapter = new BbpsTransactionRequestEnquiryAdapter(
                                BBPSTransactionStatusActivity.this, impsTransactionRequestModelList,
                                BBPSTransactionStatusActivity.this,dispositionRequestModelList);
                        recyclerIMPSTransRequestId.setAdapter(lastFiveTransactionCBSAdapter);
                    } else {
                        cardViewIMPSTransactionDetails.setVisibility(View.GONE);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
