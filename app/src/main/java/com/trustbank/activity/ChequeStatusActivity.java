package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.trustbank.Model.GetUserProfileModal;
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

import java.util.ArrayList;
import java.util.List;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class ChequeStatusActivity extends AppCompatActivity implements AlertDialogOkListener, View.OnClickListener {

    TrustMethods trustMethods;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    List<String> accountList;
    AlertDialogOkListener alertDialogOkListener = this;
    private Spinner selectAccSpinner;
    private CardView cardChequStatusResult;
    private LinearLayout linearChequInqueryId;
    private Button btnSave;
    private CoordinatorLayout coordinatorLayout;
    private EditText etChequeNoId;
    private TextView chequeStatusId;

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
                        TrustMethods.naviagteToSplashScreen(ChequeStatusActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(ChequeStatusActivity.this, false);
        setContentView(R.layout.activity_cheque_status);

        init();
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

    private void init() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        trustMethods = new TrustMethods(ChequeStatusActivity.this);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        selectAccSpinner = findViewById(R.id.selectAccSpinnerId);
        linearChequInqueryId = findViewById(R.id.linearChequInqueryId);
        etChequeNoId = findViewById(R.id.etChequeNoId);
        chequeStatusId = findViewById(R.id.chequeStatusId);
        cardChequStatusResult = findViewById(R.id.cardChequStatusResult);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        accNumberSpinner();
    }

    private void accNumberSpinner() {
        try {
            accountsArrayList = trustMethods.getArrayList(ChequeStatusActivity.this, "AccountListPref");

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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ChequeStatusActivity.this, android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectAccSpinner.setAdapter(adapter);

                selectAccSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            linearChequInqueryId.setVisibility(View.GONE);
                            cardChequStatusResult.setVisibility(View.GONE);
                            etChequeNoId.setText("");
                        } else {
                            linearChequInqueryId.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDialogOk(int resultCode) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                try {
                    TrustMethods.hideSoftKeyboard(ChequeStatusActivity.this);
                    if (selectAccSpinner.getSelectedItemPosition() == 0) {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayout);
                    } else if (TextUtils.isEmpty(etChequeNoId.getText().toString().trim())) {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_invalid_cheque_no), coordinatorLayout);
                    } else {
                        TrustMethods.hideSoftKeyboard(ChequeStatusActivity.this);
                        String accNo = TrustMethods.getValidAccountNo(selectAccSpinner.getSelectedItem().toString());
                        String chequeNo = TrustMethods.getValidAccountNo(etChequeNoId.getText().toString());

                        if (NetworkUtil.getConnectivityStatus(ChequeStatusActivity.this)) {
                            new ChequeRequestAsyncTask(ChequeStatusActivity.this, accNo.trim(), chequeNo).execute();
                        } else {
                            AlertDialogMethod.alertDialogOk(ChequeStatusActivity.this, getResources().getString(R.string.error_check_internet), "", getResources().getString(R.string.btn_ok),
                                    3, false, alertDialogOkListener);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ChequeRequestAsyncTask extends AsyncTask<Void, Void, String> {

        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String result;
        String accNo, chequeNo;
        String actionName = "GET_LIST";
        private String errorCode;
        private String chequeStatus = "";


        public ChequeRequestAsyncTask(Context ctx, String accNo, String chequeNo) {
            this.accNo = accNo;
            this.chequeNo = chequeNo;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ChequeStatusActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.CheckChequeBookRequestUrl(accNo, chequeNo);

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
                    JSONObject responseObject = jsonResponse.getJSONObject("response");
                    if (responseObject.has("error")) {
                        error = responseObject.getString("error");
                        return error;
                    }

                    JSONObject misJsonObject = responseObject.has("misc") ? responseObject.getJSONObject("misc") : null;
                    if (misJsonObject != null) {
                        String data = misJsonObject.has("p_out_data") ? misJsonObject.getString("p_out_data") : "";
                        XmlToJson xmlToJson = new XmlToJson.Builder(data).build();
                        JSONObject jsonObject = xmlToJson.toJson();
                        if (jsonObject != null) {
                            JSONObject dataJsonObject = jsonObject.has("data") ? jsonObject.getJSONObject("data") : null;
                            if (dataJsonObject != null) {
                                int chequeStatusCode = dataJsonObject.has("cheque_status") ? dataJsonObject.getInt("cheque_status") : -1;
                                chequeStatus = dataJsonObject.has("cheque_status_text") ? dataJsonObject.getString("cheque_status_text") : "";
                            }
                        } else {
                            error = "Data Not Available";
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
                    cardChequStatusResult.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(ChequeStatusActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    }
                    if (!TextUtils.isEmpty(error) && TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(ChequeStatusActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(ChequeStatusActivity.this, this.error, "", getResources().getString(R.string.btn_ok),
                                1, false, alertDialogOkListener);
                    }

                } else {
                    cardChequStatusResult.setVisibility(View.VISIBLE);
                    chequeStatusId.setText("Cheque Status : " + chequeStatus);

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
        TrustMethods.showBackButtonAlert(ChequeStatusActivity.this);
    }
}