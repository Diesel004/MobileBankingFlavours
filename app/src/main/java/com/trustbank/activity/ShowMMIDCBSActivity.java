package com.trustbank.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
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

import static com.trustbank.tcpconnection.Constants.FUND_REQUEST_DELETE;
import static com.trustbank.tcpconnection.Constants.MMID_REQUEST_SHOW;


public class ShowMMIDCBSActivity extends AppCompatActivity implements AlertDialogOkListener, AlertDialogListener {
    private String TAG = ShowMMIDCBSActivity.class.getSimpleName();
    private TrustMethods method;
    private Spinner selectAccSpinner;
    private CardView cardMmidMsg;
    private TextView txtShowMmid;
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private List<String> accountList;
    private AlertDialogOkListener alertDialogOkListener = this;
    private AlertDialogListener alertDialogListener = this;
    private Button btnDeleteMMID;
    private String mmid = "";


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
                        TrustMethods.naviagteToSplashScreen(ShowMMIDCBSActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(ShowMMIDCBSActivity.this, false);
        setContentView(R.layout.activity_show_cbs_mmid);

        inIt();
        listener();
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

            method = new TrustMethods(ShowMMIDCBSActivity.this);
            selectAccSpinner = findViewById(R.id.selectAccSpinnerId);
            cardMmidMsg = findViewById(R.id.cardMmidMsgId);
            txtShowMmid = findViewById(R.id.txtShowMmidId);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            btnDeleteMMID = findViewById(R.id.btnDeleteMMID);

            accountNoSpinner();
            setAccSpinner();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void listener() {
        btnDeleteMMID.setOnClickListener(v -> AlertDialogMethod.alertDialog(ShowMMIDCBSActivity.this, getResources().getString(R.string.app_name), "Are you sure for delete MMID?", "Yes", "No", 1, true, alertDialogListener));
    }


    private void accountNoSpinner() {
        try {
            accountsArrayList = method.getArrayList(ShowMMIDCBSActivity.this, "AccountListPref");
            //Log.d("TAG", accountsArrayList.toString());

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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ShowMMIDCBSActivity.this, android.R.layout.simple_spinner_item,
                        accountList);
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
                    String accNo = TrustMethods.getValidAccountNo((String) adapterView.getItemAtPosition(position));
                    if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(ShowMMIDCBSActivity.this)) {
                        if (NetworkUtil.getConnectivityStatus(ShowMMIDCBSActivity.this)) {
                            new ShowMMIDAsyncTask(ShowMMIDCBSActivity.this, accNo, MMID_REQUEST_SHOW).execute();
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                        }
                    } else {
                        TrustMethods.displaySimErrorDialog(ShowMMIDCBSActivity.this);
                    }
                } else {
                    cardMmidMsg.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 0) {
            Intent intentLogin = new Intent(getApplicationContext(), LockActivity.class);
            intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentLogin);
            method.activityCloseAnimation();
        } else if (resultCode == 1) {
            if (!selectAccSpinner.getSelectedItem().toString().equals("Select Account Number")) {
                if (!mmid.equals("")) {
                    if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
                        String accountNo = TrustMethods.getValidAccountNo(selectAccSpinner.getSelectedItem().toString().trim());
                        Intent intent = new Intent(ShowMMIDCBSActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("checkTransferType", "deleteMMIDCBS");
                        intent.putExtra("mmid", mmid);
                        intent.putExtra("mmidRequestType", FUND_REQUEST_DELETE);
                        intent.putExtra("accNo", accountNo);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        TrustMethods.displaySimErrorDialog(this);
                    }
                }
            }
        } else if (resultCode == 2) {
            selectAccSpinner.setSelection(0);
        }
    }

    @Override
    public void onDialogCancel(int resultCode) {
    }

    private class ShowMMIDAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse;
        String transRefNo;
        String responseAccountNo;
        ProgressDialog pDialog;
        String mAccNo;
        String mmidRequestType;
        String action = "SHOW_MMID";
        String result;
        private String errorCode;

        public ShowMMIDAsyncTask(Context ctx, String accNo, String mmidRequestType) {
            this.ctx = ctx;
            this.mAccNo = accNo;
            this.mmidRequestType = mmidRequestType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowMMIDCBSActivity.this);
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

                String jsonStringRequest = "{\"acc_no\":\"" + mAccNo + "\"}";

                if (!url.equals("")) {
                    TrustMethods.LogMessage(TAG, "URL:-" + url);

                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonStringRequest, action, AppConstants.getAuth_token());
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
                    JSONObject responseObject = jsonResponse.has("response") ? jsonResponse.getJSONObject("response") : null;

                    if (responseObject != null) {
                        JSONArray jsonArray = responseObject.has("Table") ? responseObject.getJSONArray("Table") : null;
                        if (jsonArray != null) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            finalResponse = jsonObject.getString("MMID");
                        } else {
                            error = "Data not found.";
                            return error;
                        }
                    } else {
                        error = "Record not found";
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
            return finalResponse;
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (!this.error.equals("")) {
                cardMmidMsg.setVisibility(View.GONE);
                if (TrustMethods.isSessionExpired(errorCode)) {
                    AlertDialogMethod.alertDialogOk(ShowMMIDCBSActivity.this, "Session Expired!", "",
                            getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                } else {
                    TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                }
            } else {

                if (this.mmidRequestType.equals(MMID_REQUEST_SHOW)) {

                    if (finalResponse != null) {
                        mmid = finalResponse;
                        String sourceString = getResources().getString(R.string.txt_show_mmid_msg_first) + " " + "<b>" + finalResponse + "</b> ";
                        txtShowMmid.setText(Html.fromHtml(sourceString));
                        cardMmidMsg.setVisibility(View.VISIBLE);
                    } else {
                        cardMmidMsg.setVisibility(View.GONE);
                    }

                } else if (this.mmidRequestType.equals(FUND_REQUEST_DELETE)) {

                    if (finalResponse != null) {
                        String sourceString = "MMID - " + mmid + " " + getResources().getString(R.string.txt_delete_mmid_msg_first) + responseAccountNo + ".\nReference ID is " + transRefNo + ".";
                        cardMmidMsg.setVisibility(View.GONE);
                        AlertDialogMethod.alertDialogOk(ShowMMIDCBSActivity.this, "MMID Deleted!", sourceString,
                                getResources().getString(R.string.btn_ok), 2, false, alertDialogOkListener);
                    }

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
        TrustMethods.showBackButtonAlert(ShowMMIDCBSActivity.this);
    }
}
