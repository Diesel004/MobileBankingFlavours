package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.Model.TransactionLimitModel;
import com.trustbank.Model.TransactionModel;
import com.trustbank.R;
import com.trustbank.adapter.TransactionLimitAdapter;
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


public class TransactionLimitActivity extends AppCompatActivity implements AlertDialogOkListener {

    private TrustMethods trustMethods;

    ArrayList<TransactionModel> transactionModels, transactionModelsNeft, transactionModelsImps;
    private RecyclerView recyclerTransactionLimit;
    private RecyclerView.LayoutManager mLayoutManager;

    AlertDialogOkListener alertDialogOkListener = this;
    ArrayList<GetUserProfileModal> accountsArrayList;
    private Spinner spinnerFrmAct;
    private ArrayList<String> accountList;
    private LinearLayout linearTransactionMenu;

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
                        TrustMethods.naviagteToSplashScreen(TransactionLimitActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(TransactionLimitActivity.this, false);
        setContentView(R.layout.activity_transaction_limit);

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
            trustMethods = new TrustMethods(TransactionLimitActivity.this);
            recyclerTransactionLimit = findViewById(R.id.recyclerTransactionLimitId);
            linearTransactionMenu = findViewById(R.id.linearTransactionMenuId);
            mLayoutManager = new LinearLayoutManager(getApplicationContext());
            spinnerFrmAct = findViewById(R.id.spinnerFrmActId);
            accNumberSpinner();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void accNumberSpinner() {
        try {
            accountsArrayList = trustMethods.getArrayList(TransactionLimitActivity.this, "AccountListPref");
            // Log.d("TAG", accountsArrayList.toString());

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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(TransactionLimitActivity.this, android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFrmAct.setAdapter(adapter);
            }

            spinnerFrmAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    if (position != 0) {
                        String selectedAccNo = (String) adapterView.getItemAtPosition(position);
                        String accNo = "";
                        if (selectedAccNo.contains("-")) {
                            String[] accounts = selectedAccNo.split("-");
                            accNo = accounts[0];
                        } else {
                            accNo = selectedAccNo;
                        }
                        if (TrustMethods.isSimAvailable(TransactionLimitActivity.this) && TrustMethods.isSimVerified(TransactionLimitActivity.this)) {
                            if (NetworkUtil.getConnectivityStatus(TransactionLimitActivity.this)) {
                                new GetTransactionLimitAsyncTask(TransactionLimitActivity.this, accNo).execute();
                            } else {
                                TrustMethods.message(TransactionLimitActivity.this, getResources().getString(R.string.error_check_internet));
                            }
                        } else {
                            TrustMethods.displaySimErrorDialog(TransactionLimitActivity.this);
                        }

                    } else {
                        linearTransactionMenu.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
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
        TrustMethods.showBackButtonAlert(TransactionLimitActivity.this);
    }

    @Override
    public void onDialogOk(int resultCode) {
        switch (resultCode) {
            case 0:
                Intent intent = new Intent(TransactionLimitActivity.this, LockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                trustMethods.activityCloseAnimation();
                break;
            case 1:
                Intent intentMenu = new Intent(TransactionLimitActivity.this, MenuActivity.class);
                intentMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentMenu);
                trustMethods.activityCloseAnimation();
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetTransactionLimitAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        String result;
        String accountNo;
        ProgressDialog pDialog;
        String actionName = "ACCOUNT_LIMIT_GET";
        private String errorCode;
        ArrayList<TransactionLimitModel> transactionLimitModels;


        public GetTransactionLimitAsyncTask(Context ctx, String accountNo) {
            this.ctx = ctx;
            this.accountNo = accountNo;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TransactionLimitActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                String url = TrustURL.MobileNoVerifyUrl() + "?ac_no=" + accountNo;

                if (!url.equals("")) {
                    result = HttpClientWrapper.getResponseGET(url, actionName, AppConstants.getAuth_token());
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
                    JSONObject responseJsonObject = jsonResponse.getJSONObject("response");
                    if (responseJsonObject.has("error")) {
                        error = responseJsonObject.getString("error");
                        return error;
                    }


                    transactionLimitModels = new ArrayList<>();
                    transactionModels = new ArrayList<>();
                    transactionModelsNeft = new ArrayList<>();
                    transactionModelsImps = new ArrayList<>();

                    JSONArray jsonArray = responseJsonObject.getJSONArray("own_bank_limit");
                    JSONArray neftLimitArray = responseJsonObject.getJSONArray("neft_limit");
                    JSONArray impsLimitArray = responseJsonObject.has("imps_limit") ? responseJsonObject.getJSONArray("imps_limit") : null;


                    if (jsonArray.length() > 0) {
                        String trfTypeText = "";
                        TransactionLimitModel transactionLimitModel = new TransactionLimitModel();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                            TransactionModel transactionModel = new TransactionModel();
                            int trfType = dataJsonObject.has("trf_type") ? dataJsonObject.getInt("trf_type") : 0;
                            trfTypeText = dataJsonObject.has("trf_type_text") ? dataJsonObject.getString("trf_type_text") : "";
                            int limitType = dataJsonObject.has("limit_type") ? dataJsonObject.getInt("limit_type") : 0;
                            String limitTypeText = dataJsonObject.has("limit_type_text") ? dataJsonObject.getString("limit_type_text") : "";
                            String limit_value = dataJsonObject.has("limit_value") ? dataJsonObject.getString("limit_value") : "";
                            transactionModel.setTrfType(trfType);
                            transactionModel.setTrfTypeText(trfTypeText);
                            transactionModel.setLimitType(limitType);
                            transactionModel.setLimitTypeText(limitTypeText);
                            transactionModel.setLimit_value(limit_value);
                            transactionModels.add(transactionModel);

                        }
                        transactionLimitModel.setName(trfTypeText);
                        transactionLimitModel.setTransactionList(transactionModels);
                        transactionLimitModels.add(transactionLimitModel);
                    }

                    if (neftLimitArray.length() > 0) {
                        String trfTypeText = "";
                        TransactionLimitModel transactionLimitModelNeft = new TransactionLimitModel();
                        for (int i = 0; i < neftLimitArray.length(); i++) {
                            JSONObject dataJsonObject = neftLimitArray.getJSONObject(i);
                            TransactionModel transactionModel = new TransactionModel();
                            int trfType = dataJsonObject.has("trf_type") ? dataJsonObject.getInt("trf_type") : 0;
                            trfTypeText = dataJsonObject.has("trf_type_text") ? dataJsonObject.getString("trf_type_text") : "";
                            int limitType = dataJsonObject.has("limit_type") ? dataJsonObject.getInt("limit_type") : 0;
                            String limitTypeText = dataJsonObject.has("limit_type_text") ? dataJsonObject.getString("limit_type_text") : "";
                            String limit_value = dataJsonObject.has("limit_value") ? dataJsonObject.getString("limit_value") : "";
                            transactionModel.setTrfType(trfType);
                            transactionModel.setTrfTypeText(trfTypeText);
                            transactionModel.setLimitType(limitType);
                            transactionModel.setLimitTypeText(limitTypeText);
                            transactionModel.setLimit_value(limit_value);
                            transactionModelsNeft.add(transactionModel);

                        }
                        transactionLimitModelNeft.setName(trfTypeText);
                        transactionLimitModelNeft.setTransactionList(transactionModelsNeft);
                        transactionLimitModels.add(transactionLimitModelNeft);
                    }

                    if (impsLimitArray != null){
                        if (impsLimitArray.length() > 0) {
                            String trfTypeText = "";
                            TransactionLimitModel transactionLimitModelImps = new TransactionLimitModel();
                            for (int i = 0; i < impsLimitArray.length(); i++) {
                                JSONObject dataJsonObject = impsLimitArray.getJSONObject(i);
                                TransactionModel transactionModel = new TransactionModel();
                                int trfType = dataJsonObject.has("trf_type") ? dataJsonObject.getInt("trf_type") : 0;
                                trfTypeText = dataJsonObject.has("trf_type_text") ? dataJsonObject.getString("trf_type_text") : "";
                                int limitType = dataJsonObject.has("limit_type") ? dataJsonObject.getInt("limit_type") : 0;
                                String limitTypeText = dataJsonObject.has("limit_type_text") ? dataJsonObject.getString("limit_type_text") : "";
                                String limit_value = dataJsonObject.has("limit_value") ? dataJsonObject.getString("limit_value") : "";
                                transactionModel.setTrfType(trfType);
                                transactionModel.setTrfTypeText(trfTypeText);
                                transactionModel.setLimitType(limitType);
                                transactionModel.setLimitTypeText(limitTypeText);
                                transactionModel.setLimit_value(limit_value);
                                transactionModelsImps.add(transactionModel);
                            }
                            transactionLimitModelImps.setName(trfTypeText);
                            transactionLimitModelImps.setTransactionList(transactionModelsImps);
                            transactionLimitModels.add(transactionLimitModelImps);
                        }
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
                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(TransactionLimitActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else if (!TextUtils.isEmpty(error) && TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(TransactionLimitActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(TransactionLimitActivity.this, this.error, "", getResources().getString(R.string.btn_ok),
                                1, false, alertDialogOkListener);
                    }
                } else {
                    linearTransactionMenu.setVisibility(View.VISIBLE);
                    recyclerTransactionLimit.setLayoutManager(mLayoutManager);
                    TransactionLimitAdapter transactionAdapter = new TransactionLimitAdapter(TransactionLimitActivity.this,
                            transactionLimitModels, accountNo);
                    recyclerTransactionLimit.setAdapter(transactionAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1) {
                if (data != null && data.hasExtra("accNo")) {
                    String accNo = data.getStringExtra("accNo");
                    if (TrustMethods.isSimAvailable(TransactionLimitActivity.this) && TrustMethods.isSimVerified(TransactionLimitActivity.this)) {
                        if (NetworkUtil.getConnectivityStatus(TransactionLimitActivity.this)) {
                            new GetTransactionLimitAsyncTask(TransactionLimitActivity.this, accNo).execute();
                        } else {
                            TrustMethods.message(TransactionLimitActivity.this, getResources().getString(R.string.error_check_internet));
                        }
                    } else {
                        TrustMethods.displaySimErrorDialog(TransactionLimitActivity.this);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}