package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.Model.FundTransferSubModel;
import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogListener;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.tcpconnection.tdto.MessageDtoBuilder;
import com.trustbank.tcpconnection.tmessage.TMessage;
import com.trustbank.tcpconnection.tmessage.TMessageUtil;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.MessageBodyManipulator;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateQRUPIActivity extends AppCompatActivity implements View.OnClickListener,
        AlertDialogListener, AlertDialogOkListener {

    private TrustMethods trustMethods;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private Spinner spinnerFromAccountId;
    private Button  btnGenerateBarcodeId;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout btnDisplayBarcodeLayout;
    private AlertDialogListener alertDialogListener = this;
    private AlertDialogOkListener alertDialogOkListener = this;
    TextView textVerifyUPITransId;
    private ImageView imgSelectUPIId;
    private ArrayList<BeneficiaryModal> beneficiaryArrayList;
    private String accountNo;
    private String remitterAccName;
    List<FundTransferSubModel> fundTransferSubModalList = new ArrayList<>();
    //    private LinearLayout bottom_linear_layout_id;
//
//    private BottomSheetBehavior sheetBehavior;
    private int BARCODE_READER_REQUEST_CODE = 1;


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
                        TrustMethods.naviagteToSplashScreen(CreateQRUPIActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(CreateQRUPIActivity.this, false);
        setContentView(R.layout.activity_generate_barcodeupi);

        initcomponent();
    }


    private void initcomponent() {

        try {

            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            trustMethods = new TrustMethods(CreateQRUPIActivity.this);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            spinnerFromAccountId = findViewById(R.id.spinnerFromAccountId);
            btnGenerateBarcodeId = findViewById(R.id.btnGenerateBarcodeId);

            btnDisplayBarcodeLayout=findViewById(R.id.btnDisplayBarcodeLayout);
            btnDisplayBarcodeLayout.setVisibility(View.GONE);

            btnGenerateBarcodeId.setOnClickListener(this);

            setAccountNoSpinner();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setAccountNoSpinner() {
        try {
            accountsArrayList = trustMethods.getArrayList(CreateQRUPIActivity.this, "AccountListPref");

            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                List<String> accountList = new ArrayList<>();
                accountList.add(0, "Select Account Number");
                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    if (TrustMethods.isAccountTypeQRValid(getUserProfileModal.getActType())) {

                        String accNo = getUserProfileModal.getAccNo();
                        String accTypeCode = getUserProfileModal.getAcTypeCode();
                        String name = getUserProfileModal.getName();
                        accountList.add(accNo + " - " + accTypeCode);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateQRUPIActivity.this, android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFromAccountId.setAdapter(adapter);


                spinnerFromAccountId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                        if (pos != 0) {
                            accountNo = TrustMethods.getValidAccountNo((String) adapterView.getItemAtPosition(pos));
                            //Get Profile Info List to get remitter name for the selected account number
                            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                                for (int i = 0; i < accountsArrayList.size(); i++) {
                                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                                    if (getUserProfileModal.getAccNo().equalsIgnoreCase(accountNo.trim())) {
                                        remitterAccName = getUserProfileModal.getName();
                                        Log.d("remitterAccName", remitterAccName);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGenerateBarcodeId:
                if (isValidateDataBr()) {

                    AlertDialogMethod.alertDialog(CreateQRUPIActivity.this, getResources().getString(R.string.msg_confirm_collect_req), getResources().getString(R.string.msg_create_barcode_req) , getResources().getString(R.string.btn_ok), "Cancel", 1, true, alertDialogListener);

                }

                break;

        }
    }


    @Override
    public void onDialogOk(int resultCode) {
        try {
            if (resultCode == 1) {

                TrustMethods.hideSoftKeyboard(CreateQRUPIActivity.this);

                if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(CreateQRUPIActivity.this)) {
                    if (NetworkUtil.getConnectivityStatus(CreateQRUPIActivity.this)) {
                        new FetchUPIUrlForBarcodeAsyncTask(CreateQRUPIActivity.this, "0.00", accountNo).execute();
                    } else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                    }
                } else {
                    TrustMethods.displaySimErrorDialog(CreateQRUPIActivity.this);
                }
            }else if(resultCode == 55){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean isValidateDataBr() {
        if (spinnerFromAccountId.getSelectedItem().equals("Select Account Number")) {
            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_select_acc_no), coordinatorLayout);
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onDialogCancel(int resultCode) {
        try {
            if (resultCode == 0) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(CreateQRUPIActivity.this);
    }




    @SuppressLint("StaticFieldLeak")
    private class FetchUPIUrlForBarcodeAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        String decryptresponse;
        String upiId;
        String result;
        String actionName = "GENERATE_QR";
        ProgressDialog pDialog;
        String mAccNo, mAmount;
        private String errorCode;

        public FetchUPIUrlForBarcodeAsyncTask(Context ctx, String amount, String accNo) {
            this.ctx = ctx;
            this.mAccNo = accNo;
            this.mAmount = amount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateQRUPIActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @SuppressLint("WrongThread")
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.getBarcodeDetails(mAccNo);

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
                    JSONObject qrJsonObject = jsonResponse.getJSONObject("response").getJSONObject("data");
                    response = qrJsonObject.has("qr_string") ? qrJsonObject.getString("qr_string") : "";

                    decryptresponse=TrustMethods.decodeBase64(response);

                    upiId=decryptresponse.substring(decryptresponse.indexOf("=")+1,decryptresponse.indexOf("&"));
                    decryptresponse.trim();
                    Log.e("upiId ",upiId);

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
                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(CreateQRUPIActivity.this,
                                getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(CreateQRUPIActivity.this,
                                " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                    //   cardBalMsg.setVisibility(View.GONE);
                } else {
                    if (response != null) {
                        Intent pinActivationIntent = new Intent(CreateQRUPIActivity.this, UpiQRDisplayActivity.class);
                        pinActivationIntent.putExtra("isUpiSelfBarcodeGenerator", true);
                        pinActivationIntent.putExtra("upiUrl", decryptresponse);
                        pinActivationIntent.putExtra("accName", remitterAccName);
                        pinActivationIntent.putExtra("accNumber", accountNo);
                        pinActivationIntent.putExtra("upiId", upiId);
                        startActivity(pinActivationIntent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}