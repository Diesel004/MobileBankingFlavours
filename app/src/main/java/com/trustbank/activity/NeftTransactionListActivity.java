package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.trustbank.Model.NeftEnquiryModel;
import com.trustbank.R;
import com.trustbank.adapter.NeftTransactionAdapter;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NeftTransactionListActivity extends AppCompatActivity implements AlertDialogOkListener {

    private String TAG = NeftEnquiryActivity.class.getSimpleName();

    private CoordinatorLayout layoutNEFT;
    private RecyclerView rvNeftEnquiryList;
    private List<NeftEnquiryModel> neftEnquiryList = new ArrayList<>();
    private AlertDialogOkListener alertDialogOkListener = this;

    private String fromDate = "";
    private String toDate = "";

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
                        TrustMethods.naviagteToSplashScreen(NeftTransactionListActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(NeftTransactionListActivity.this, false);
        setContentView(R.layout.activity_neft_transaction_list);

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

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            layoutNEFT = findViewById(R.id.layoutNEFT);
            rvNeftEnquiryList = findViewById(R.id.rvNeftEnquiryList);

            String enquiryCode = getIntent().getStringExtra("EnquiryCode");
            assert enquiryCode != null;
            if (enquiryCode.equals("1")) {
                fromDate = TrustMethods.formatDate(getIntent().getStringExtra("FromDate"), "dd/MM/yyyy", "yyyy-MM-dd");
                toDate = TrustMethods.formatDate(getIntent().getStringExtra("ToDate"), "dd/MM/yyyy", "yyyy-MM-dd");
            }
            String transactionId = getIntent().getStringExtra("TransactionId");

            neftEnquiryList.clear();
            if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(NeftTransactionListActivity.this)) {
                if (NetworkUtil.getConnectivityStatus(NeftTransactionListActivity.this)) {
                    new GetNeftEnquiryAsyncTask(NeftTransactionListActivity.this, enquiryCode, fromDate, toDate, transactionId).execute();
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), layoutNEFT);
                }
            } else {
                TrustMethods.displaySimErrorDialog(NeftTransactionListActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetNeftEnquiryAsyncTask extends AsyncTask<Void, Void, String> {

        private String error = "";
        private Context ctx;
        private String response;
        private ProgressDialog pDialog;
        private String result;
        private String enquiryType;
        private String fromDate;
        private String toDate;
        private String transactionID;
        private String actionName = "NEFT_ENQUIRY";
        private String errorCode;

        public GetNeftEnquiryAsyncTask(Context ctx, String enquiryType, String fromDate, String toDate, String transactionID) {
            this.ctx = ctx;
            this.enquiryType = enquiryType;
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.transactionID = transactionID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NeftTransactionListActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.GetNeftEnquiryUrl(enquiryType, fromDate, toDate, transactionID, AppConstants.getProfileID());
                if (!url.equals("")) {
                    result = HttpClientWrapper.getResponseGET(url, actionName, AppConstants.getAuth_token());
                }
//                 result = "{\"response_code\":1,\"response\":{\"data\":{\"transfers\":[{\"TransactionID\":\"210411700721\",\"Amount\":\"1.00\",\"Tran_Date\":\"10-02-2021\",\"Debit_Account\":\"250000200000129\",\"Ben_Name\":\"test neft\",\"To_AccountNo\":\"208001700002323\",\"IFSC\":\"IBKL0000204\",\"Status\":0,\"Remarks\":null,\"utr\":\"\",\"resp_utr\":true}]}},\"error_code\":null,\"error_message\":null}";
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
                    JSONObject responseObject = jsonResponse.getJSONObject("response");
                    if (responseObject.has("error")) {
                        error = responseObject.getString("error");
                        return error;
                    }

                    JSONObject dataObject = responseObject.getJSONObject("data");
                    if (dataObject.has("error")) {
                        error = dataObject.getString("error");
                        return error;
                    }

                    JSONArray transfersJsonArray = dataObject.getJSONArray("transfers");
                    if (transfersJsonArray.length() == 0) {
                        error = "NEFT Enquiry not found";
                        return error;
                    }

                    for (int i = 0; i < transfersJsonArray.length(); i++) {

                        neftEnquiryList.add(new NeftEnquiryModel(
                                transfersJsonArray.getJSONObject(i).has("TransactionID") ? transfersJsonArray.getJSONObject(i).getString("TransactionID") : "",
                                transfersJsonArray.getJSONObject(i).has("Amount") ? transfersJsonArray.getJSONObject(i).getString("Amount") : "",
                                transfersJsonArray.getJSONObject(i).has("Tran_Date") ? transfersJsonArray.getJSONObject(i).getString("Tran_Date") : "",
                                transfersJsonArray.getJSONObject(i).has("Debit_Account") ? transfersJsonArray.getJSONObject(i).getString("Debit_Account") : "",
                                transfersJsonArray.getJSONObject(i).has("Ben_Name") ? transfersJsonArray.getJSONObject(i).getString("Ben_Name") : "",
                                transfersJsonArray.getJSONObject(i).has("To_AccountNo") ? transfersJsonArray.getJSONObject(i).getString("To_AccountNo") : "",
                                transfersJsonArray.getJSONObject(i).has("IFSC") ? transfersJsonArray.getJSONObject(i).getString("IFSC") : "",
                                transfersJsonArray.getJSONObject(i).has("Status") ? transfersJsonArray.getJSONObject(i).getString("Status") : "",
                                transfersJsonArray.getJSONObject(i).has("Remarks") ? transfersJsonArray.getJSONObject(i).getString("Remarks") : "",
                                transfersJsonArray.getJSONObject(i).has("Error") ? transfersJsonArray.getJSONObject(i).getString("Error") : "",
                                transfersJsonArray.getJSONObject(i).has("utr") ? transfersJsonArray.getJSONObject(i).getString("utr") : "",
                                transfersJsonArray.getJSONObject(i).has("resp_utr") ? transfersJsonArray.getJSONObject(i).getString("resp_utr") : ""
                        ));
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
                    TrustMethods.showSnackBarMessage(this.error, layoutNEFT);
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(NeftTransactionListActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(NeftTransactionListActivity.this, this.error, "", getResources().getString(R.string.btn_ok),
                                1, false, alertDialogOkListener);
                    }
                } else {
                    rvNeftEnquiryList.setAdapter(new NeftTransactionAdapter(NeftTransactionListActivity.this, neftEnquiryList));
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
                Intent intent = new Intent(NeftTransactionListActivity.this, LockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case 1:
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
        TrustMethods.showBackButtonAlert(NeftTransactionListActivity.this);
    }
}