package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.trustbank.Model.TransactionModel;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TransactionLimitSubmitActivity extends AppCompatActivity implements AlertDialogOkListener {

    private SeekBar seekBar;
    private EditText enterTransLimitEdit;
    private TrustMethods method;
    private String formType;
    private int limit;
    private Button btnSendTransactionLimitId;
    private CoordinatorLayout coordinatorLayout;
    AlertDialogOkListener alertDialogOkListener = this;

    private List<TransactionModel> transactionLimitModelList;
    private RadioGroup transactionRG;
    private TextView textTransactionId;
    private RadioButton transactionPerDayRadioBtn;
    private String accountNo;

    String trf_type;
    String limitType;

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
                        TrustMethods.naviagteToSplashScreen(TransactionLimitSubmitActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(TransactionLimitSubmitActivity.this, false);
        setContentView(R.layout.activity_transaction_limit_submit);

        init();
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }

    private void init() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            method = new TrustMethods(TransactionLimitSubmitActivity.this);
            transactionPerDayRadioBtn = findViewById(R.id.transactionPerDayRadioBtn);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            transactionRG = findViewById(R.id.transactionRGId);
            textTransactionId = findViewById(R.id.textTransactionId);

            enterTransLimitEdit = findViewById(R.id.enterTransLimitEditId);
            btnSendTransactionLimitId = findViewById(R.id.btnSendTransactionLimitId);

            Intent intent = getIntent();
            if (intent.hasExtra("transactionLimit")) {
                transactionLimitModelList = (List<TransactionModel>) intent.getSerializableExtra("transactionLimit");
                accountNo = intent.getStringExtra("accountNo");
                getSupportActionBar().setTitle(transactionLimitModelList.get(0).getTrfTypeText());
                textTransactionId.setText("Transaction Limit Per Day.");
                for (TransactionModel transactionModel1 : transactionLimitModelList) {
                    if (transactionModel1.getLimitType() == 1) {
                        enterTransLimitEdit.setText(transactionModel1.getLimit_value());
                        trf_type = String.valueOf(transactionModel1.getTrfType());
                        limitType = String.valueOf(transactionModel1.getLimitType());
                    }
                }
                transactionPerDayRadioBtn.setChecked(true);

            }


            transactionRG.setOnCheckedChangeListener((group, checkedId) -> {
                RadioButton rb = findViewById(checkedId);
                if (rb.getText().toString().equalsIgnoreCase("Transaction Limit Per Day.")) {
                    textTransactionId.setText("Transaction Limit Per Day.");
                    for (TransactionModel transactionModel1 : transactionLimitModelList) {
                        if (transactionModel1.getLimitType() == 1) {
                            enterTransLimitEdit.setText(transactionModel1.getLimit_value());
                            trf_type = String.valueOf(transactionModel1.getTrfType());
                            limitType = String.valueOf(transactionModel1.getLimitType());
                        }
                    }
                } else if (rb.getText().toString().equalsIgnoreCase("Limit Per Transaction.")) {
                    for (TransactionModel transactionModel1 : transactionLimitModelList) {
                        if (transactionModel1.getLimitType() == 3) {
                            enterTransLimitEdit.setText(transactionModel1.getLimit_value());
                            trf_type = String.valueOf(transactionModel1.getTrfType());
                            limitType = String.valueOf(transactionModel1.getLimitType());
                        }
                    }
                    textTransactionId.setText("Limit Per Transaction.");
                } else if (rb.getText().toString().equalsIgnoreCase("No.Of Transaction Per Day.")) {
                    textTransactionId.setText("No.Of Transaction Per Day.");
                    for (TransactionModel transactionModel1 : transactionLimitModelList) {
                        if (transactionModel1.getLimitType() == 2) {
                            enterTransLimitEdit.setText(transactionModel1.getLimit_value());
                            trf_type = String.valueOf(transactionModel1.getTrfType());
                            limitType = String.valueOf(transactionModel1.getLimitType());
                        }
                    }
                }
            });

            btnSendTransactionLimitId.setOnClickListener(v -> {
                TrustMethods.hideSoftKeyboard(TransactionLimitSubmitActivity.this);
                if (TextUtils.isEmpty(enterTransLimitEdit.getText().toString())) {
                    TrustMethods.showSnackBarMessage("Please enter Value", coordinatorLayout);
                } else if (TrustMethods.isAmoutLessThanZero(enterTransLimitEdit.getText().toString().trim())) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_valid_amt_limit), coordinatorLayout);
                } else {

                    String limitPerDay = enterTransLimitEdit.getText().toString();
                    checkAmountValidation(limitPerDay,accountNo);
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkAmountValidation(String amount, String accountNo) {
        if (NetworkUtil.getConnectivityStatus(TransactionLimitSubmitActivity.this)) {
            new IMPSAddValidationAsyncTask(TransactionLimitSubmitActivity.this, accountNo, amount).execute();
        } else {
            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
        }
    }

    //check validation api call.
    @SuppressLint("StaticFieldLeak")
    private class IMPSAddValidationAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse;
        ProgressDialog pDialog;
        String response;
        String actionName = "MBANK_LIMIT_VALIDATE";
        String result;
        private String errorCode;
        private String accountNo;
        private String otpActionName;
        private String amount;


        public IMPSAddValidationAsyncTask(Context ctx, String accountNo, String amount) {
            this.ctx = ctx;
            this.accountNo = accountNo;
            this.amount = amount;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TransactionLimitSubmitActivity.this);
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
                jsonObject.put("acno", accountNo);
                jsonObject.put("trf_type", trf_type);
                jsonObject.put("limit_type", limitType);
                jsonObject.put("limit_value", amount);

                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonObject.toString(), actionName, AppConstants.getAuth_token());

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
                        AlertDialogMethod.alertDialogOk(TransactionLimitSubmitActivity.this, getResources().getString(R.string.error_session_expire),
                                "",
                                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }

                } else {
                    //response..
                   if (NetworkUtil.getConnectivityStatus(TransactionLimitSubmitActivity.this)) {

                        Intent intentPin = new Intent(TransactionLimitSubmitActivity.this, OtpVerificationActivity.class);
                        intentPin.putExtra("checkTransferType", "setTransactionLimit");
                        intentPin.putExtra("limitPerDay", amount);
                        intentPin.putExtra("trf_type", trf_type);
                        intentPin.putExtra("limitType", limitType);
                        intentPin.putExtra("accountNo", accountNo);
                        intentPin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentPin);
                        method.activityOpenAnimation();
                    } else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
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
        TrustMethods.showBackButtonAlert(TransactionLimitSubmitActivity.this);
    }

    @Override
    public void onDialogOk(int resultCode) {
        switch (resultCode) {
            case 0:
                Intent intentLogin = new Intent(TransactionLimitSubmitActivity.this, LockActivity.class);
                intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogin);
                method.activityCloseAnimation();
                break;

            case 1:
                Intent intent = new Intent();
                intent.putExtra("accNo", accountNo);
                setResult(1, intent);
                finish();
                break;
        }
    }
}