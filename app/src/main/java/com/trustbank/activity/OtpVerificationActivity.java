package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.trustbank.Model.AccountDetailsModel;
import com.trustbank.Model.BBPSBillerResponseModel;
import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.Model.FundTransferSubModel;
import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.Model.IMPSTransactionRequestModel;
import com.trustbank.R;
import com.trustbank.fragment.ImpsTransactionResultFragment;
import com.trustbank.interfaces.AlertDialogListener;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.tcpconnection.tdto.MessageDtoBuilder;
import com.trustbank.tcpconnection.tmessage.TMessage;
import com.trustbank.tcpconnection.tmessage.TMessageUtil;
import com.trustbank.tcpconnection.util.ResponseEntity;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SessionManager;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;
import com.trustbank.util.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.trustbank.tcpconnection.Constants.FUND_MSG_DELETE;
import static com.trustbank.tcpconnection.Constants.FUND_REQUEST_DELETE;
import static com.trustbank.tcpconnection.Constants.MMID_MSG_DELETE;
import static com.trustbank.tcpconnection.Constants.MMID_REQUEST_SHOW;

public class OtpVerificationActivity extends AppCompatActivity implements View.OnClickListener,
        AlertDialogOkListener, AlertDialogListener {

    private String mSecurityCode;
    private String TAG = BalanceEnquiryActivity.class.getSimpleName();
    private TrustMethods method;
    private EditText etFundTransferOtp, etFundTransferMpin;
    private TextView txtResendOtp;
    private CoordinatorLayout coordinatorLayout;
    private TextView txtCounter;
    private String accountType;
    private ArrayList<FundTransferSubModel> fundTransferModalDataList;
    private AlertDialogOkListener alertDialogOkListener = this;
    private AlertDialogListener alertDialogListener = this;
    private String bMobileNo, bNickname, bAccName, bAccNo, bIfscCode, bBenfMobNo, bMmid, bBen_type, upi_id;
    private boolean bWithinChecked;
    private String mmid;
    private String mmidRequestType;
    private String strSecurityPin, strPIN, strConfirmPin;
    private String limitPerDay, trf_type, limitType, accountNo, umrnNo, ecsMandateId;
    private String mClientId;
    private String debitcardNo, debitCardMode, otherReasonDebitCard;
    private String benAcNameUpi, upiid;


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
                        TrustMethods.naviagteToSplashScreen(OtpVerificationActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(OtpVerificationActivity.this, false);
        setContentView(R.layout.activity_fund_transfer_otp);
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

            method = new TrustMethods(OtpVerificationActivity.this);
            TextInputLayout etTpinId = findViewById(R.id.etTpinId);
            etFundTransferOtp = findViewById(R.id.etFundTransferOtpId);
            etFundTransferMpin = findViewById(R.id.etFundTransferMpinId);
            txtResendOtp = findViewById(R.id.txtResendOtpId);
            txtCounter = findViewById(R.id.txtCounterId);
            Button btnWithBankNext = findViewById(R.id.btnWithBankNextId);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            btnWithBankNext.setOnClickListener(this);
            txtResendOtp.setOnClickListener(this);

            accountType = getIntent().getStringExtra("checkTransferType");
            if (Objects.requireNonNull(accountType).equalsIgnoreCase("addBeneficiary")) {

                bMobileNo = getIntent().getStringExtra("mobileNo");
                bNickname = getIntent().getStringExtra("nickname");
                bAccName = getIntent().getStringExtra("accName");
                bAccNo = getIntent().getStringExtra("accNo");
                bIfscCode = getIntent().getStringExtra("ifscCode");
                bBenfMobNo = getIntent().getStringExtra("benfMobNo");
                bMmid = getIntent().getStringExtra("mmid");
                bWithinChecked = getIntent().getBooleanExtra("withinChecked", false);
                bBen_type = getIntent().getStringExtra("ben_type");
                upi_id = getIntent().getStringExtra("upi_id");

                String actionName = "GENERATE_OTP_FOR_MBANK_REG";
                getSupportActionBar().setTitle("Add Beneficiary");
                generateOtp(actionName);


            } else if (accountType.equalsIgnoreCase("deleteMMID")) {

                mmid = getIntent().getStringExtra("mmid");
                mmidRequestType = getIntent().getStringExtra("mmidRequestType");
                bAccNo = getIntent().getStringExtra("accNo");
                String actionName = "GENERATE_OTP_FOR_MBANK_REG";
                getSupportActionBar().setTitle("Delete MMID");
                generateOtp(actionName);

            } else if (accountType.equalsIgnoreCase("deleteMMIDCBS")) {

                mmid = getIntent().getStringExtra("mmid");
                mmidRequestType = getIntent().getStringExtra("mmidRequestType");
                bAccNo = getIntent().getStringExtra("accNo");
                String actionName = "GENERATE_OTP_FOR_MBANK_REG";
                getSupportActionBar().setTitle("Delete MMID");
                generateOtp(actionName);

            } else if (accountType.equalsIgnoreCase("ResetTPIn")) {
                etTpinId.setVisibility(View.GONE);
                strSecurityPin = getIntent().getStringExtra("strSecurityPin");
                strPIN = getIntent().getStringExtra("strPIN");
                strConfirmPin = getIntent().getStringExtra("strConfirmPin");
                String actionName = "GENERATE_OTP_FOR_MBANK_REG";
                getSupportActionBar().setTitle("Reset TPIN");
                generateOtp(actionName);

            } else if (accountType.equalsIgnoreCase("MPinActivation")) {
                etTpinId.setVisibility(View.GONE);
                strSecurityPin = getIntent().getStringExtra("strSecurityPin");
                strPIN = getIntent().getStringExtra("strPIN");
                strConfirmPin = getIntent().getStringExtra("strConfirmPin");
                mClientId = getIntent().getStringExtra("mClientId");
                String actionName = "GENERATE_OTP_FOR_MBANK_REG";
                getSupportActionBar().setTitle("MPin Activation");
                generateOtp(actionName);

            } else if (accountType.equalsIgnoreCase("setTransactionLimit")) {
                limitPerDay = getIntent().getStringExtra("limitPerDay");
                trf_type = getIntent().getStringExtra("trf_type");
                limitType = getIntent().getStringExtra("limitType");
                accountNo = getIntent().getStringExtra("accountNo");

                String actionName = "GENERATE_OTP_FOR_MBANK_REG";
                getSupportActionBar().setTitle("Set Transaction Limit");
                generateOtp(actionName);

            } else if (accountType.equalsIgnoreCase("blockDebitCard")) {

                debitcardNo = getIntent().getStringExtra("cardNo");
                accountNo = getIntent().getStringExtra("accountNo");
                debitCardMode = getIntent().getStringExtra("modeid");
                otherReasonDebitCard = getIntent().getStringExtra("otherReason");
                String actionName = "GENERATE_OTP_FOR_MBANK_REG";
                getSupportActionBar().setTitle("Block Debit Card");
                generateOtp(actionName);

            } else if (accountType.equalsIgnoreCase("mandateCancelRequest")) {

                umrnNo = getIntent().getStringExtra("umrnNo");
                accountNo = getIntent().getStringExtra("accountNo");
                ecsMandateId = getIntent().getStringExtra("ecsMandateId");
                String actionName = "GENERATE_OTP_FOR_MBANK_REG";
                getSupportActionBar().setTitle("Cancel Mandate Request");
                generateOtp(actionName);

            } else {
                if (accountType.equalsIgnoreCase("billPayTransactions")) {
                    getSupportActionBar().setTitle("Bill Pay");
                }
                fundTransferModalDataList = (ArrayList<FundTransferSubModel>) getIntent().getSerializableExtra("fundTransferDataList");
                String actionName = "GENERATE_OTP_FOR_FT";
                generateOtp(actionName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateOtp(String actionName) {
        try {
            if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
                if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                    new GenerateOtpAsyncTask(OtpVerificationActivity.this, actionName).execute();
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                }
            } else {
                TrustMethods.displaySimErrorDialog(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnWithBankNextId:
                try {
                    TrustMethods.hideSoftKeyboard(OtpVerificationActivity.this);

                    if (accountType.equalsIgnoreCase("ResetTPIn")) {
                        String otp = etFundTransferOtp.getText().toString().trim();
                        if (otp.isEmpty()) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_otp), coordinatorLayout);
                        } else {
                            if (getIntent().getExtras() != null) {
                                if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
                                    if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                        new ResetTPinAsyncTask(strSecurityPin, strPIN, strConfirmPin, otp).execute();
                                    } else {
                                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                    }

                                }
                            }
                        }

                    } else if (accountType.equalsIgnoreCase("MPinActivation")) {
                        String otp = etFundTransferOtp.getText().toString().trim();
                        if (otp.isEmpty()) {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_otp), coordinatorLayout);
                        } else {
                            if (getIntent().getExtras() != null) {
                                if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {

                                    if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                        new PinActivationAsyncTask(this, strSecurityPin, strPIN, strConfirmPin, otp).execute();
                                    } else {
                                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                    }

                                }
                            }
                        }

                    } else {
                        if (Validation.validationFundTransferOtp(coordinatorLayout, OtpVerificationActivity.this, etFundTransferOtp, etFundTransferMpin)) {
                            String mpin = etFundTransferMpin.getText().toString().trim();
                            String otp = etFundTransferOtp.getText().toString().trim();

                            if (getIntent().getExtras() != null) {
                                if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {

                                    if (accountType.equals("impsToAccount") || accountType.equals("impsToMobile")) {
                                        if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                            new FundTransferAsyncTask(OtpVerificationActivity.this, accountType, fundTransferModalDataList, mpin, otp).execute();
                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }
                                    } else if (accountType.equals("neftToAccount")) {
                                        String is_neft_send_to_switch = AppConstants.getMnu_neft_trans_switch_transaction();
                                        if (is_neft_send_to_switch.equalsIgnoreCase("1")) {
                                            if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                                new FundNEFTSWithSwitchTransferAsyncTask(OtpVerificationActivity.this, accountType,
                                                        fundTransferModalDataList, mpin, otp).execute();
                                            } else {
                                                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                            }
                                        } else {
                                            if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                                new FundTransferNEFTAsyncTask(OtpVerificationActivity.this, accountType,
                                                        fundTransferModalDataList, mpin, otp).execute();
                                            } else {
                                                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                            }
                                        }

                                    } else if (accountType.equals("upiToUpiTransaction")) {
                                        if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                            new FundUPISWithSwitchTransferAsyncTask(OtpVerificationActivity.this, accountType,
                                                    fundTransferModalDataList, mpin, otp).execute();
                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }

                                    } else if (accountType.equals("billPayTransactions")) {
                                        if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {

                                            String data = getIntent().getStringExtra("billPayObject");
                                            new BillPaymentAsyncTask(OtpVerificationActivity.this,
                                                    data, fundTransferModalDataList.get(0).getAccNo(), fundTransferModalDataList.get(0).getRemitterAccName(), fundTransferModalDataList.get(0).getAmt(),
                                                    fundTransferModalDataList.get(0).getRemark(), fundTransferModalDataList.get(0).getBillerId(),
                                                    fundTransferModalDataList.get(0).getBillerName(), fundTransferModalDataList.get(0).getBillNumber()
                                                    , mpin, otp).execute();


                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }

                                    } else if (accountType.equals("withinBank")) {
                                        if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                            new FundTransferWithinBankAsyncTask(OtpVerificationActivity.this, accountType,
                                                    fundTransferModalDataList, mpin, otp).execute();
                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }
                                    } else if (accountType.equals("SelfTransferToAccount")) {
                                        if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                            new FundTransferToSelfAccountAsyncTask(OtpVerificationActivity.this, accountType, fundTransferModalDataList, mpin, otp).execute();
                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }
                                    } else if (accountType.equalsIgnoreCase("addBeneficiary")) {

                                        if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                            addBeneficiary(bMobileNo, bNickname, bAccName, bAccNo, bIfscCode,
                                                    bBenfMobNo, bMmid, bWithinChecked, bBen_type, otp, mpin, upi_id);
                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }
                                    } else if (accountType.equalsIgnoreCase("deleteMMID")) {

                                        if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                            deleteMMID(mmid, mmidRequestType, bAccNo, otp, mpin);
                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }
                                    } else if (accountType.equalsIgnoreCase("deleteMMIDCBS")) {
                                        if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                            deleteMMIDCBS(mmid, mmidRequestType, bAccNo, otp, mpin);
                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }
                                    } else if (accountType.equalsIgnoreCase("setTransactionLimit")) {/**/

                                        if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                            new TransactionLimitSubmit(limitPerDay, trf_type, limitType, accountNo, otp, mpin).execute();
                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }
                                    } else if (accountType.equalsIgnoreCase("blockDebitCard")) {

                                        if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                            new DebitCardBlock(accountNo, debitcardNo, otp, mpin).execute();
                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }
                                    } else if (accountType.equalsIgnoreCase("mandateCancelRequest")) {

                                        if (NetworkUtil.getConnectivityStatus(OtpVerificationActivity.this)) {
                                            new GetMandateAccountDetailsAsyncTask(accountNo, umrnNo, ecsMandateId,otp,mpin).execute();
                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }
                                    }

                                } else {
                                    TrustMethods.displaySimErrorDialog(this);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.txtResendOtpId:
                try {
                    String actionName;
                    if (accountType.equalsIgnoreCase("addBeneficiary") || accountType.equalsIgnoreCase("deleteMMID")
                            || accountType.equalsIgnoreCase("ResetTPIn") || accountType.equalsIgnoreCase("MPinActivation")
                            || accountType.equalsIgnoreCase("setTransactionLimit") || accountType.equalsIgnoreCase("blockDebitCard")) {
                        actionName = "GENERATE_OTP_FOR_MBANK_REG";
                    } else {
                        actionName = "GENERATE_OTP_FOR_FT";
                    }
                    generateOtp(actionName);
                    clearFields();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void deleteMMID(String mmid, String mmidRequestType, String bAccNo, String otp, String tpin) {
        new DeleteMMIDAsyncTask(OtpVerificationActivity.this, bAccNo, mmidRequestType, mmid, otp, tpin).execute();
    }

    private void deleteMMIDCBS(String mmid, String mmidRequestType, String bAccNo, String otp, String tpin) {
        new DeleteMMIDCBSAsyncTask(OtpVerificationActivity.this, bAccNo, mmidRequestType, mmid, otp, tpin).execute();
    }

    private void addBeneficiary(String mobileNo, String nickname, String accName, String accNo,

                                String ifscCode, String benfMobNo, String mmid, boolean withinChecked, String ben_type,
                                String otp, String mpin, String upi_id) {


        new AddBeneficiaryAsyncTask(OtpVerificationActivity.this
                , mobileNo
                , nickname
                , accName
                , accNo
                , ifscCode
                , benfMobNo
                , mmid
                , withinChecked
                , ben_type, otp, mpin, upi_id).execute();

    }

    private void clearFields() {
        etFundTransferMpin.setText("");
        etFundTransferOtp.setText("");
    }

    @SuppressLint("StaticFieldLeak")
    private class GenerateOtpAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String otp_sent_msg;
        String sms;
        ProgressDialog pDialog;
        String actionName;
        String purposeCode = "";
        String result;
        private String errorCode;
        private String customerId = AppConstants.getCLIENTID();

        public GenerateOtpAsyncTask(Context ctx, String actionName) {
            this.ctx = ctx;
            this.actionName = actionName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

               /* if (accountType.equals("impsToAccount") || accountType.equals("impsToMobile")
                        || accountType.equals("upiToUpiTransaction") ||
                        accountType.equals("billPayTransactions")) {
                    purposeCode = "MBANK_IMPS";
                }*/
                if (accountType.equals("impsToAccount") || accountType.equals("impsToMobile")) {
                    purposeCode = "MBANK_IMPS";
                } else if (accountType.equals("billPayTransactions")) {
                    purposeCode = "MBANK_BILL_PAY";
                } else if (accountType.equals("upiToUpiTransaction")) {
                    purposeCode = "MBANK_UPI";
                } else if (accountType.equals("neftToAccount")) {
                    purposeCode = "MBANK_FT_NEFT_RTGS";
                } else if (accountType.equals("withinBank")) {
                    purposeCode = "MBANK_FT_OWN_BANK";
                } else if (accountType.equalsIgnoreCase("addBeneficiary")) {
                    purposeCode = "MBANK_BEN";
                } else if (accountType.equalsIgnoreCase("deleteMMID")) {
                    purposeCode = "MBANK_DEL_MMID";
                } else if (accountType.equalsIgnoreCase("deleteMMIDCBS")) {
                    purposeCode = "MBANK_DEL_MMID";
                } else if (accountType.equalsIgnoreCase("ResetTPIn")) {
                    purposeCode = "RESET_TPIN";
                } else if (accountType.equalsIgnoreCase("MPinActivation")) {
                    purposeCode = "FRGT_MPIN";
                    customerId = mClientId;
                } else if (accountType.equalsIgnoreCase("SelfTransferToAccount")) {
                    purposeCode = "MBANK_FT_SELF";
                } else if (accountType.equalsIgnoreCase("setTransactionLimit")) {
                    purposeCode = "MBANK_CHANGE_LIMIT";
                } else if (accountType.equalsIgnoreCase("blockDebitCard")) {
                    purposeCode = "BLOCK_DEBIT";
                } else if (accountType.equalsIgnoreCase("mandateCancelRequest")) {
                    purposeCode = "CANCEL_MANDATE_REQUEST";
                }

                String url = TrustURL.GenerateOtpFundTransferUrl();
                String jsonString = "{\"for\":\"" + AppConstants.getUSERMOBILENUMBER() + "\",\"custid\":\"" + customerId + "\",\"purpose_code\":\"" + purposeCode + "\"}";

                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonString, actionName, AppConstants.getAuth_token());
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
                    JSONObject otpJsonObject = responseJsonObject.getJSONObject("otp");
                    if (otpJsonObject.has("error")) {
                        error = otpJsonObject.getString("error");
                        return error;
                    }
                    sms = otpJsonObject.has("sms") ? otpJsonObject.getString("sms") : "NA";
                    String mobile_number = otpJsonObject.has("mobile_number") ? otpJsonObject.getString("mobile_number") : "NA";
                    otp_sent_msg = otpJsonObject.has("otp_sent_msg") ? otpJsonObject.getString("otp_sent_msg") : "NA";


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
            return otp_sent_msg;
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
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else if (!TextUtils.isEmpty(error) && TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                } else {
                    if (otp_sent_msg != null) {
                        TrustMethods.showSnackBarMessage(this.otp_sent_msg, coordinatorLayout);
                        Log.d("OTP", sms);
                        txtCounter.setVisibility(View.VISIBLE);
                        txtResendOtp.setTextColor(getResources().getColor(R.color.colorLightGray));
                        txtResendOtp.setEnabled(false);

                        new CountDownTimer(60000, 1000) {
                            @SuppressLint("SetTextI18n")
                            public void onTick(long millisUntilFinished) {
                                txtCounter.setText(getResources().getString(R.string.msg_resend_otp_enable)
                                        + " " + millisUntilFinished / 1000 + " " + "seconds");
                            }

                            public void onFinish() {
                                txtResendOtp.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                txtCounter.setVisibility(View.GONE);
                                txtResendOtp.setEnabled(true);
                            }
                        }.start();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class FundTransferAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse, doubtfullResponse;
        ProgressDialog pDialog;
        String mAccountType;
        String mpin;
        String otp;
        ArrayList<FundTransferSubModel> mFundTransferModalDataList;
        private TMessage msg;
        String response;
        String result;
        private String errorCode;
        GenerateStanRRNModel generateStanRRNModel;

        public FundTransferAsyncTask(Context ctx, String accountType,
                                     ArrayList<FundTransferSubModel> fundTransferModalDataList,
                                     String mpin, String otp) {
            this.ctx = ctx;
            this.mAccountType = accountType;
            this.mpin = mpin;
            this.otp = otp;
            this.mFundTransferModalDataList = fundTransferModalDataList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                // String jsonString = "{\"filter\":[\"stan\"]}";
                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                generateStanRRNModel = TMessageUtil.GetNextStanRrn(OtpVerificationActivity.this, jsonString,
                        TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {
                    if (mAccountType.equalsIgnoreCase("impsToMobile")) {
                        msg = msgDto.GetImpsTransferToMobileDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                                AppConstants.getUSERMOBILENUMBER(), mFundTransferModalDataList.get(0).getAccNo(),
                                mFundTransferModalDataList.get(0).getRemitterAccName(), mFundTransferModalDataList.get(0).getBenMobNo(),
                                mFundTransferModalDataList.get(0).getBenMmid()/*mMmid*/, mFundTransferModalDataList.get(0).getAmt(),
                                mFundTransferModalDataList.get(0).getRemark(), AppConstants.INSTITUTION_ID,
                                generateStanRRNModel.getChannel_ref_no(), mFundTransferModalDataList.get(0).getBenAccName());
                        Log.d("msg.GetXml():", msg.GetXml());

                    } else if (mAccountType.equalsIgnoreCase("impsToAccount")) {
                        msg = msgDto.GetImpsTransferToAccountNoDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                                AppConstants.getUSERMOBILENUMBER(), mFundTransferModalDataList.get(0).getAccNo(), mFundTransferModalDataList.get(0).getRemitterAccName(),
                                mFundTransferModalDataList.get(0).getBenAccNo(), mFundTransferModalDataList.get(0).getBenIfscCode(),
                                mFundTransferModalDataList.get(0).getAmt(), mFundTransferModalDataList.get(0).getRemark(),
                                AppConstants.INSTITUTION_ID, mFundTransferModalDataList.get(0).getBenAccName(), generateStanRRNModel.getChannel_ref_no());
                        Log.d("msg.GetXml():", msg.GetXml());
                    }

                    String base64EncodedRequestJson = Base64.encodeToString(msg.GetXml().getBytes(),
                            Base64.NO_WRAP);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("otp", otp);
                    jsonObject.put("tpin", mpin); //TODO
                    jsonObject.put("data", base64EncodedRequestJson);

                    if (!url.equals("")) {
                        TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWitInstituteAuthHeader(url, jsonObject.toString()
                                /*jsonStringRequest*/, AppConstants.getAuth_token(), AppConstants.INSTITUTION_ID);
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
                            finalResponse = responseMsg.TransRefNo.Value;
                        } else if (responseMsg.ActCode.Value.equals(AppConstants.DOUBTFULL_TRANSACTION)) {
                            doubtfullResponse = responseMsg.ActCodeDesc.Value;
                        } else {
                            error = "Act Code: " + responseMsg.ActCode.Value + ": " + responseMsg.ActCodeDesc.Value;
                        }

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

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        alertDialogOk(OtpVerificationActivity.this, "ERROR!!!", this.error, "Ok");
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                    clearFields();
                } else {

                    String checkImpsTransStatusFundTransfer = "0";
                    // String checkImpsTransStatusFundTransfer = AppConstants.getCheckImpsTransStatusFundTransfer();
                    if (checkImpsTransStatusFundTransfer.equals("0")) {
                        if (!TextUtils.isEmpty(finalResponse)) {
                            if (mAccountType.equalsIgnoreCase("impsToMobile")) {
                                AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.msg_transaction_success), "Your Account No " + mFundTransferModalDataList.get(0).getAccNo() + " is debited for Rs." + mFundTransferModalDataList.get(0).getAmt() + " & Credited to Mobile No " + mFundTransferModalDataList.get(0).getBenMobNo() + ".(Txn Ref No." + finalResponse + ")", getResources().getString(R.string.btn_ok), -1, false, alertDialogOkListener);

                            } else if (mAccountType.equalsIgnoreCase("impsToAccount")) {
                                AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.msg_transaction_success), "Your Account No " + mFundTransferModalDataList.get(0).getAccNo() + " is debited for Rs." + mFundTransferModalDataList.get(0).getAmt() + " & Credited to Account No " + mFundTransferModalDataList.get(0).getBenAccNo() + ".(Txn Ref No." + finalResponse + ")", getResources().getString(R.string.btn_ok), -1, false, alertDialogOkListener);
                            }
                        } else if (!TextUtils.isEmpty(doubtfullResponse)) {
                            AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "IMPS Transaction", doubtfullResponse, getResources().getString(R.string.btn_ok), -1, false, alertDialogOkListener);
                        }
                    } else {

                        //TODO in future if required after successfull transaction we should have to open this page.
                        if (mAccountType.equalsIgnoreCase("impsToAccount")) {

                            IMPSTransactionRequestModel impsTransactionRequestModel = new IMPSTransactionRequestModel();
                            impsTransactionRequestModel.setRemMobile(AppConstants.getUSERMOBILENUMBER());
                            impsTransactionRequestModel.setRemAcno(mFundTransferModalDataList.get(0).getToAccNo());
                            impsTransactionRequestModel.setRrn(finalResponse);
                            impsTransactionRequestModel.setChannelRefNo(generateStanRRNModel.getChannel_ref_no());
                            impsTransactionRequestModel.setSwitchReqType("8");
                          /*  alertDialogOk(OtpVerificationActivity.this,getResources().getString(R.string.msg_transaction_success),"Your Account No " + mFundTransferModalDataList.get(0).getAccNo() + " is debited for Rs." + mFundTransferModalDataList.get(0).getAmt() + " & Credited to Account No " + mFundTransferModalDataList.get(0).getBenAccNo() + ".(Txn Ref No." + finalResponse + ")",
                                    "OK",impsTransactionRequestModel);*/

                        } else if (mAccountType.equalsIgnoreCase("impsToMobile")) {

                            IMPSTransactionRequestModel impsTransactionRequestModel = new IMPSTransactionRequestModel();
                            impsTransactionRequestModel.setRemMobile(AppConstants.getUSERMOBILENUMBER());
                            impsTransactionRequestModel.setRemAcno(mFundTransferModalDataList.get(0).getToAccNo());
                            impsTransactionRequestModel.setRrn(finalResponse);
                            impsTransactionRequestModel.setChannelRefNo(generateStanRRNModel.getChannel_ref_no());
                            impsTransactionRequestModel.setSwitchReqType("4");

                      /*      alertDialogOk(OtpVerificationActivity.this,getResources().getString(R.string.msg_transaction_success),"Your Account No " + mFundTransferModalDataList.get(0).getAccNo() + " is debited for Rs." + mFundTransferModalDataList.get(0).getAmt() + " & Credited to Mobile No " + mFundTransferModalDataList.get(0).getBenMobNo() + ".(Txn Ref No." + finalResponse + ")",
                                    "OK",impsTransactionRequestModel);*/
                            //  new VerifyIMPSTransactionAsyncTask(OtpVerificationActivity.this, impsTransactionRequestModel).execute();
                        }

                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FundTransferNEFTAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "";
        private Context ctx;
        private String finalResponse, utrNO;
        private ProgressDialog pDialog;
        private String mAccountType;
        private String mpin;
        private String otp;
        private GenerateStanRRNModel generateStanRRNModel;
        private ArrayList<FundTransferSubModel> mFundTransferModalDataList;
        private TMessage msg;
        private String response;
        private String actionName = "FUND_TRANSFER_RTGS_NSFT";
        private String result;
        private String errorCode;

        public FundTransferNEFTAsyncTask(Context ctx, String accountType, ArrayList<FundTransferSubModel> fundTransferModalDataList, String mpin, String otp) {
            this.ctx = ctx;
            this.mAccountType = accountType;
            this.mpin = mpin;
            this.otp = otp;
            this.mFundTransferModalDataList = fundTransferModalDataList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                //   String jsonString = "{\"filter\":[\"rrn_cbs\"]}";
                String jsonString = "{\"filter\":[\"rrn_cbs\",\"channel_ref_no\"]}";
                generateStanRRNModel = TMessageUtil.GetNextStanRrn(OtpVerificationActivity.this, jsonString, TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.getURLForFundTransferOwnAndNeft();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tpin", mpin);  //TODO
                    jsonObject.put("otp", otp);
                    jsonObject.put("rrn", generateStanRRNModel.getCbs_rrn());
                    jsonObject.put("utr", "");
                    jsonObject.put("ben_id", mFundTransferModalDataList.get(0).getBenId());
                    jsonObject.put("ben_ifsc", mFundTransferModalDataList.get(0).getBenIfscCode());
                    jsonObject.put("ben_ac_no", mFundTransferModalDataList.get(0).getBenAccNo());
                    jsonObject.put("rem_ac_no", mFundTransferModalDataList.get(0).getAccNo());
                    jsonObject.put("amount", mFundTransferModalDataList.get(0).getAmt());
                    jsonObject.put("remarks", mFundTransferModalDataList.get(0).getRemark());

                    if (!url.equals("")) {
                        TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWithActionAuthToken(url, jsonObject.toString(), actionName, AppConstants.getAuth_token());
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
                        finalResponse = jsonResponse.has("response") ? jsonResponse.getString("response") : "NA";
                        JSONObject responseJSONObject = jsonResponse.has("response") ? jsonResponse.getJSONObject("response") : null;
                        if (responseJSONObject != null) {
                            JSONObject miscJSONObject = responseJSONObject.has("misc") ? responseJSONObject.getJSONObject("misc") : null;
                            if (miscJSONObject != null) {
                                utrNO = miscJSONObject.has("p_out_utrno") ? miscJSONObject.getString("p_out_utrno") : "";
                            }
                        }

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

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                    clearFields();
                } else {
                    if (finalResponse != null) {
                        if (mAccountType.equalsIgnoreCase("neftToAccount")) {
                            String message;
                            if (!TextUtils.isEmpty(utrNO)) {
                                message = "NEFT transfer of Rs." + mFundTransferModalDataList.get(0).getAmt()
                                        + " to Account No. " + mFundTransferModalDataList.get(0).getAccNo()
                                        + " is processed successfully. \nTxn Ref No: " + generateStanRRNModel.getCbs_rrn() + "." + "\nUTR No. : "
                                        + utrNO;
                            } else {
                                message = "NEFT transfer of Rs." + mFundTransferModalDataList.get(0).getAmt() +
                                        " to Account No. " + mFundTransferModalDataList.get(0).getAccNo()
                                        + " will be effected subject to realisation. \nTxn Ref No: " + generateStanRRNModel.getCbs_rrn();
                            }
                            AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "", message, getResources().getString(R.string.btn_ok), -1, false, alertDialogOkListener);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FundTransferToSelfAccountAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse;
        ProgressDialog pDialog;
        String mAccountType;
        String tpin;
        String otp;
        GenerateStanRRNModel generateStanRRNModel;
        ArrayList<FundTransferSubModel> mFundTransferModalDataList;
        private TMessage msg;
        String response;
        String actionName = "FUND_TRANSFER_OWN_BANK";
        //        String action = "send_to_switch";
        String result;
        private String errorCode;

        public FundTransferToSelfAccountAsyncTask(Context ctx, String accountType, ArrayList<FundTransferSubModel> fundTransferModalDataList, String tpin, String otp) {
            this.ctx = ctx;
            this.mAccountType = accountType;
            this.tpin = tpin;
            this.otp = otp;
            this.mFundTransferModalDataList = fundTransferModalDataList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                //   String jsonString = "{\"filter\":[\"rrn_cbs\"]}";
                String jsonString = "{\"filter\":[\"rrn_cbs\",\"channel_ref_no\"]}";
                generateStanRRNModel = TMessageUtil.GetNextStanRrn(OtpVerificationActivity.this, jsonString, TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.getURLForFundTransferOwnAndNeft();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tpin", tpin); //TODO
                    jsonObject.put("otp", otp);
                    jsonObject.put("rrn", generateStanRRNModel.getCbs_rrn());
                    jsonObject.put("ben_id", "");
                    jsonObject.put("ben_ac_no", mFundTransferModalDataList.get(0).getToAccNo());
                    jsonObject.put("rem_ac_no", mFundTransferModalDataList.get(0).getAccNo());
                    jsonObject.put("amount", mFundTransferModalDataList.get(0).getAmt());
                    jsonObject.put("remarks", mFundTransferModalDataList.get(0).getRemark());
                    jsonObject.put("is_self", "1");


                    if (!url.equals("")) {
                        TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWithActionAuthToken(url, jsonObject.toString(), actionName, AppConstants.getAuth_token());
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
                        finalResponse = jsonResponse.has("response") ? jsonResponse.getString("response") : "NA";
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

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                      //  TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                    clearFields();
                } else {
                    if (finalResponse != null) {

                        String message = "Your Account No " + mFundTransferModalDataList.get(0).getAccNo() + " is debited for Rs." + mFundTransferModalDataList.get(0).getAmt() + " & Credited to Account No " + mFundTransferModalDataList.get(0).getToAccNo() + ".(Txn Ref No." + generateStanRRNModel.getCbs_rrn() + ")";
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.msg_transaction_success), message, getResources().getString(R.string.btn_ok), -1, false, alertDialogOkListener);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class FundTransferWithinBankAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse;
        ProgressDialog pDialog;
        String mAccountType;
        String mpin;
        String otp;
        GenerateStanRRNModel generateStanRRNModel;
        ArrayList<FundTransferSubModel> mFundTransferModalDataList;
        private TMessage msg;
        String response;
        String actionName = "FUND_TRANSFER_OWN_BANK";
        String result;
        private String errorCode;

        public FundTransferWithinBankAsyncTask(Context ctx, String accountType,
                                               ArrayList<FundTransferSubModel> fundTransferModalDataList, String mpin, String otp) {
            this.ctx = ctx;
            this.mAccountType = accountType;
            this.mpin = mpin;
            this.otp = otp;
            this.mFundTransferModalDataList = fundTransferModalDataList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                //String jsonString = "{\"filter\":[\"rrn_cbs\"]}";
                String jsonString = "{\"filter\":[\"rrn_cbs\",\"channel_ref_no\"]}";
                generateStanRRNModel = TMessageUtil.GetNextStanRrn(OtpVerificationActivity.this,
                        jsonString, TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.getURLForFundTransferOwnAndNeft();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tpin", mpin); //TODO
                    jsonObject.put("otp", otp);
                    jsonObject.put("rrn", generateStanRRNModel.getCbs_rrn());
                    jsonObject.put("ben_id", mFundTransferModalDataList.get(0).getBenId());
                    jsonObject.put("ben_ac_no", mFundTransferModalDataList.get(0).getBenAccNo());
                    jsonObject.put("rem_ac_no", mFundTransferModalDataList.get(0).getAccNo());
                    jsonObject.put("amount", mFundTransferModalDataList.get(0).getAmt());
                    jsonObject.put("remarks", mFundTransferModalDataList.get(0).getRemark());
                    jsonObject.put("is_self", "0");

                    if (!url.equals("")) {
                        TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWithActionAuthToken(url, jsonObject.toString(),
                                actionName, AppConstants.getAuth_token());
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
                        finalResponse = jsonResponse.has("response") ? jsonResponse.getString("response") : "NA";

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

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this,
                                getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //rustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                    clearFields();
                } else {
                    if (finalResponse != null) {
                        if (mAccountType.equalsIgnoreCase("withinBank")) {
                            AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.msg_transaction_success), "Your Account No " + mFundTransferModalDataList.get(0).getAccNo() + " is debited for Rs." + mFundTransferModalDataList.get(0).getAmt() + " & Credited to Account No " + mFundTransferModalDataList.get(0).getBenAccNo() + ".(Txn Ref No." + generateStanRRNModel.getCbs_rrn() /*finalResponse*/ + ")", getResources().getString(R.string.btn_ok), -1, false, alertDialogOkListener);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDialogOk(int resultCode) {
        try {
            switch (resultCode) {
                case -1:
                    Intent intent = new Intent(getApplicationContext(), FundsTransferMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    method.activityCloseAnimation();
                    clearFields();
                    break;
                case 55:
                    clearFields();
                    break;
                case 0:
                    Intent intentLogin = new Intent(getApplicationContext(), LockActivity.class);
                    intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentLogin);
                    method.activityCloseAnimation();
                    clearFields();
                    break;

                case 1:
                    Intent fundTransferMenuIntent = new Intent(getApplicationContext(), MenuActivity.class);
                    fundTransferMenuIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(fundTransferMenuIntent);
                    method.activityOpenAnimation();
                    clearFields();
                    break;
                case 2:
                    Intent mmidIntent = new Intent(getApplicationContext(), MenuActivity.class);
                    mmidIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mmidIntent);
                    method.activityOpenAnimation();
                    clearFields();
                    break;

                case 3:
                    Intent intentGenerateTpin = new Intent(OtpVerificationActivity.this, TPinActivateActivity.class);
                    intentGenerateTpin.putExtra("mSecurityCode", mSecurityCode);
                    intentGenerateTpin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentGenerateTpin);
                    method.activityOpenAnimation();
                    finish();
                    clearFields();
                    break;

                case 4: //after UPI Transaction success.
                    try {
                        upiid = fundTransferModalDataList.get(0).getUpiId();
                        boolean isUpiExist = false;
                        if (getIntent().getExtras() != null) {
                            ArrayList<BeneficiaryModal> beneficiaryArrayList = (ArrayList<BeneficiaryModal>) getIntent().getSerializableExtra("beneficiaryList");

                            for (BeneficiaryModal beneficiaryModal : beneficiaryArrayList) {
                                if (beneficiaryModal.getBenType().equalsIgnoreCase("4") &&
                                        beneficiaryModal.getBenUpiId().equalsIgnoreCase(upiid)) {
                                    isUpiExist = true;
                                    break;
                                }
                            }
                        }
                        if (isUpiExist) {
                            Intent intent1 = new Intent(getApplicationContext(), FundsTransferMenu.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent1);
                            method.activityCloseAnimation();
                            clearFields();
                        } else {
                            AlertDialogMethod.alertDialog(OtpVerificationActivity.this, "Fund Transfer!!!", "Do you wish to save this as Contact ?", getResources().getString(R.string.btn_ok), getResources().getString(R.string.btn_cancel), 5, true, alertDialogListener);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent intent1 = new Intent(getApplicationContext(), FundsTransferMenu.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        method.activityCloseAnimation();
                        clearFields();
                    }

                    break;

                case 5: //add upi benficiery from here.
                    //TODO add ben for UPI
                    try{
                        bNickname = fundTransferModalDataList.get(0).getToBenName();
                        benAcNameUpi = fundTransferModalDataList.get(0).getBenAccName();
                        if (TextUtils.isEmpty(bNickname)){
                            bNickname = benAcNameUpi;
                        }
                        upiid = fundTransferModalDataList.get(0).getUpiId();
                        new AddUPIBeneficiaryAsyncTask(OtpVerificationActivity.this
                                , AppConstants.getUSERMOBILENUMBER()
                                , bNickname
                                , benAcNameUpi
                                , ""
                                , ""
                                , ""
                                , ""
                                , false
                                , "4", "", "", upiid).execute();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDialogCancel(int resultCode) {

        if (resultCode == 5) {
            Intent intent1 = new Intent(getApplicationContext(), FundsTransferMenu.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent1);
            method.activityCloseAnimation();
            clearFields();
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


    @SuppressLint("StaticFieldLeak")
    private class AddBeneficiaryAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "";
        private Context ctx;
        private String response, benActivateTimeInMins;
        private ProgressDialog pDialog;
        private String mMobileNo;
        private String mNickname;
        private String mAccName;
        private String mAccNo;
        private String mIfscCode;
        private String mMmid;
        private String mBenfMobNo;
        private String ben_type;
        private String actionName = "ADD_BENEFICIARY";
        private String result;
        private String otp;
        private String mpin;
        private String upi_id;
        private Boolean mWithinChecked;
        private String errorCode;

        public AddBeneficiaryAsyncTask(Context ctx, String mobileNo, String nickname, String accName, String accNo, String ifscCode, String benfMobNo, String mmid,
                                       boolean withinChecked, String ben_type, String otp, String mpin, String upi_id) {
            this.ctx = ctx;
            this.mMobileNo = mobileNo;
            this.mNickname = nickname;
            this.mAccName = accName;
            this.mAccNo = accNo;
            this.mIfscCode = ifscCode;
            this.mBenfMobNo = benfMobNo;
            this.mMmid = mmid;
            this.mWithinChecked = withinChecked;
            this.ben_type = ben_type;
            this.otp = otp;
            this.mpin = mpin;
            this.upi_id = upi_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();

                String jsonString = "{\"mobile_number\":\"" + mMobileNo + "\",\"ben_nickname\":\"" + mNickname + "\"," + "\"ben_ac_name\":\"" + mAccName + "\"," + "\"ben_type\":\"" + ben_type + "\",\"ben_ac_no\":\"" + mAccNo + "\",\"ben_ifsc\":\"" + mIfscCode + "\"," + "\"ben_mobile_number\":\"" + mBenfMobNo + "\",\"ben_mmid\":\"" + mMmid + "\",\"otp\":\"" + otp + "\",\"tpin\":\"" + mpin + "\",\"ben_upi_id\":\"" + upi_id + "\"}";

                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);


                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonString, actionName, AppConstants.getAuth_token());
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
                        error = jsonResponse.getString("error");
                        return error;
                    }

                    JSONArray responseArray = responseObject.getJSONArray("Table");
                    if (responseArray.length() > 0) {
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject dataObject = responseArray.getJSONObject(i);
                            response = dataObject.has("ben_id") ? dataObject.getString("ben_id") : "NA";
                            benActivateTimeInMins = dataObject.has("ben_activate_after_mins") ? dataObject.getString("ben_activate_after_mins") : "NA";
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
                if (response != null) {
                    String message = "Beneficiary is successfully added as " + mNickname + ". " + "It will be activated after " + benActivateTimeInMins + " Minutes.";
                    AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "", message, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                }
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "Session Expired!", "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class DeleteMMIDAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "";
        private Context ctx;
        private String finalResponse;
        private String transRefNo;
        private String responseAccountNo;
        private ProgressDialog pDialog;
        private String mAccNo;
        private String mmidRequestType;
        private String response;
        private String result;
        private String mmid;
        private String otp;
        private String tpin;
        private String errorCode;

        public DeleteMMIDAsyncTask(Context ctx, String accNo, String mmidRequestType, String mmid, String otp, String tpin) {
            this.ctx = ctx;
            this.mAccNo = accNo;
            this.mmid = mmid;
            this.otp = otp;
            this.tpin = tpin;
            this.mmidRequestType = mmidRequestType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                // String jsonString = "{\"filter\":[\"stan\"]}";
                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(OtpVerificationActivity.this,
                        jsonString, TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {
                    TMessage requestXmlMsg = null;
                    if (this.mmidRequestType.equals(MMID_REQUEST_SHOW)) {
                        requestXmlMsg = msgDto.GetGenerateRetriveMMIDDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                                AppConstants.getUSERMOBILENUMBER(), mAccNo, "", AppConstants.getUSERNAME(),
                                AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no());//TMessageUtil.MSG_INSTITUTION_ID);
                    } else if (this.mmidRequestType.equals(FUND_REQUEST_DELETE)) {
                        requestXmlMsg = msgDto.GetDeleteMMIDDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                                AppConstants.getUSERMOBILENUMBER(), mAccNo, "", AppConstants.getUSERNAME(),
                                AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no());//TMessageUtil.MSG_INSTITUTION_ID);
                    }

                    Log.d("msg.GetXml()" + "" + ":", requestXmlMsg.GetXml());

                    String base64EncodedRequestJson = Base64.encodeToString(requestXmlMsg.GetXml().getBytes(), Base64.NO_WRAP);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("otp", otp);
                    jsonObject.put("tpin", tpin); //TODO
                    jsonObject.put("data", base64EncodedRequestJson);

                    TrustMethods.LogMessage("", "base64EncodedRequestJson : " + jsonObject.toString());

                    if (!url.equals("")) {
                        TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWitAuthHeader(url, jsonObject.toString(), AppConstants.getAuth_token());
                        TrustMethods.LogMessage(TAG, "Frm Enquiry details response-->" + response);
                    }
                    if (result == null || result.equals("")) {
                        error = AppConstants.SERVER_NOT_RESPONDING;
                        return error;
                    }
                    JSONObject jsonResponse = new JSONObject(result);
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

                        if (this.mmidRequestType.equals(FUND_REQUEST_DELETE)) {

                            if (responseMsg.ActCodeDesc.Value.equals(FUND_MSG_DELETE) || responseMsg.ActCodeDesc.Value.equals(MMID_MSG_DELETE)) {
                                finalResponse = responseMsg.ActCodeDesc.Value;
                                transRefNo = responseMsg.TransRefNo.Value;
                                responseAccountNo = responseMsg.RemitterAccNo.Value;
                            } else {
                                error = "Act Code: " + responseMsg.ActCode.Value + ": " + responseMsg.ActCodeDesc.Value;
                            }

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

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "Session Expired!", "",
                                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }

                    //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                } else {
                    if (this.mmidRequestType.equals(FUND_REQUEST_DELETE)) {

                        if (finalResponse != null) {
                            String sourceString = "MMID - " + mmid + " " + getResources().getString(R.string.txt_delete_mmid_msg_first) + responseAccountNo + ".\nReference ID is " + transRefNo + ".";
                            AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "MMID Deleted!", sourceString,
                                    getResources().getString(R.string.btn_ok), 2, false, alertDialogOkListener);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DeleteMMIDCBSAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "";
        private Context ctx;
        private String responseAccountNo;
        private ProgressDialog pDialog;
        private String mAccNo;
        private String mmidRequestType;
        private String responseMessage;
        String action = "DELETE _MMID";
        private String result;
        private String mmid;
        private String otp;
        private String tpin;
        private String errorCode;

        public DeleteMMIDCBSAsyncTask(Context ctx, String accNo, String mmidRequestType, String mmid, String otp, String tpin) {
            this.ctx = ctx;
            this.mAccNo = accNo;
            this.mmid = mmid;
            this.otp = otp;
            this.tpin = tpin;
            this.mmidRequestType = mmidRequestType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
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

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("otp", otp);
                jsonObject.put("tpin", tpin); //TODO
                jsonObject.put("acc_no", mAccNo);

                if (!url.equals("")) {
                    TrustMethods.LogMessage(TAG, "URL:-" + url);
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonObject.toString(), action, AppConstants.getAuth_token());
                }
                if (result == null || result.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResponse = new JSONObject(result);
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
                            JSONObject jsonDetails = jsonArray.getJSONObject(0);
                            responseMessage = jsonDetails.has("ActCodeDesc") ? jsonDetails.getString("ActCodeDesc") : "";
                            responseAccountNo = jsonDetails.has("RemitterAccNo") ? jsonDetails.getString("RemitterAccNo") : "";
                        } else {
                            error = "Data not found.";
                            return error;
                        }
                    } else {
                        error = "Data not found.";
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
            return responseMessage;
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
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "Session Expired!", "",
                                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }

                    //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                } else {

                    if (this.mmidRequestType.equals(FUND_REQUEST_DELETE)) {
                        if (responseMessage != null) {
                            String sourceString = "MMID - " + mmid + " " + responseMessage + " " + responseAccountNo + ".";
                            AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "MMID Deleted!", sourceString,
                                    getResources().getString(R.string.btn_ok), 2, false, alertDialogOkListener);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class ResetTPinAsyncTask extends AsyncTask<Void, Void, String> {

        private String error = "";
        private String response;
        private ProgressDialog pDialog;
        private String result;
        private String otp;
        private String tpin;
        private String strConfirmPin;
        private String errorCode;
        private String action = "RESET_TPIN";
        private String purposeCode = "RESET_TPIN";
        private String strSecurityPin;


        public ResetTPinAsyncTask(String strSecurityPin, String strPIN, String strConfirmPin, String otp) {
            this.strSecurityPin = strSecurityPin;
            this.otp = otp;
            this.tpin = strPIN;
            this.strConfirmPin = strConfirmPin;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();
                String jsonString = "{\"pin_type\":\"" + "tpin" + "\",\"tpin\":\"" + tpin + "\",\"confirm_tpin\":\"" + strConfirmPin + "\", \"mobile_number\":\"" + AppConstants.getUSERMOBILENUMBER() + "\", \"security_code\":\"" + strSecurityPin + "\", \"otp\":\"" + otp + "\", \"custid\":\"" + AppConstants.getCLIENTID() + "\", \"otp_purpose_code\":\"" + purposeCode + "\"}";

                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);


                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonString, action, AppConstants.getAuth_token());

                }
                if (result == null || result.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResponse = new JSONObject(result);
                if (jsonResponse.has("error")) {
                    error = jsonResponse.getString("error");
                    return error;
                }
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";

                if (responseCode.equalsIgnoreCase("1")) {
                    response = "Reset TPin Successfully";
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
                if (response != null) {
                    AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "", response, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                }
                if (!this.error.equals("")) {
                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "Session Expired!", "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    }
                    if (TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "Session Expired!", "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PinActivationAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "";
        private Context ctx;
        private String response;
        private ProgressDialog pDialog;
        private String mCnfmMpin;
        private String mMpin;
        private String mMobileNo, otp;
        private String result;
        private String purposeCode = "FRGT_MPIN";

        public PinActivationAsyncTask(Context ctx, String securityCode, String mpin, String cnfmMpin, String otp) {
            this.ctx = ctx;
            mSecurityCode = securityCode;
            this.mMpin = mpin;
            this.otp = otp;
            this.mCnfmMpin = cnfmMpin;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.GeneratePinUrl();

                String jsonString = "{\"pin_type\":\"" + "mpin" + "\",\"pin\":\"" + mMpin + "\",\"pin_confirmation\":\"" + mCnfmMpin + "\", \"mobile_number\":\"" + AppConstants.getUSERMOBILENUMBER() + "\", \"security_code\":\"" + mSecurityCode + "\", \"otp\":\"" + otp + "\", \"custid\":\"" + mClientId + "\", \"otp_purpose_code\":\"" + purposeCode + "\"}";

                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);


                if (!url.equals("")) {
                    TrustMethods.LogMessage(TAG, "URL:-" + url);
                    result = HttpClientWrapper.postWithoutHeader(url, jsonString);
                    TrustMethods.LogMessage(TAG, "Frm Enquiry details response-->" + response);
                }
                if (result == null || result.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResponse = new JSONObject(result);
                if (jsonResponse.has("error")) {
                    error = jsonResponse.getString("error");
                    return error;
                }
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";

                if (responseCode.equals("1")) {
                    response = "Pin Successfully Activated.";
                } else {
                    String errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
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
                    if (!this.error.equals("")) {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                } else {
                    if (response != null) {
                        if (getPackageName().equals("com.trustbank.sadhnambank")) {
                            SessionManager sessionManager = new SessionManager(OtpVerificationActivity.this);
                            Set<String> mySetClientList = sessionManager.getClientListIds();
                            if (mySetClientList != null) {
                                mySetClientList.add(mClientId);
                            } else {
                                mySetClientList = new HashSet<>();
                                mySetClientList.add(mClientId);
                            }
                            sessionManager.storeclientList(mySetClientList);

                        }

                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "", response, getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    @SuppressLint("StaticFieldLeak")
    private class DebitCardBlock extends AsyncTask<Void, Integer, String> {

        private String error = "";
        private String response;
        private String transLimit, trfType, debitCardNo, accNo, otp, tpin;
        private String actionName = "BLOCK_DEBIT_CARD";
        private String result;
        private ProgressDialog pDialog;
        private String errorCode;

        public DebitCardBlock(String accountNo, String debitCardNo, String otp, String tpin) {

            this.debitCardNo = debitCardNo;
            this.accNo = accountNo;
            this.otp = otp;
            this.tpin = tpin;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMax(45);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();
                String jsonString = "{\"acc_no\":\"" + accNo + "\",\"card_no\":\"" + debitCardNo + "\",\"otp\":\"" + otp + "\",\"tpin\":\"" + tpin + "\",\"req_mode\":\"" + debitCardMode + "\",\"reason\":\"" + otherReasonDebitCard + "\"}";

                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonString, actionName, AppConstants.getAuth_token());
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
                    JSONArray accountJsonArray = jsonResponse.getJSONObject("response").getJSONArray("response_msg");
                    JSONObject jsonObject = accountJsonArray.getJSONObject(0);
                    response = jsonObject.has("msg") ? jsonObject.getString("msg") : "";
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
                pDialog.dismiss();
                if (!this.error.equals("")) {
                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    }
                    if (!TextUtils.isEmpty(error) && TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                     //   AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "Info", error, getResources().getString(R.string.btn_ok), 4, false, alertDialogOkListener);
                    }
                } else {
                    if (response != null) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "", response, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class TransactionLimitSubmit extends AsyncTask<Void, Integer, String> {

        private String error = "";
        private String response;
        private String transLimit, trfType, limitType, accNo, otp, tpin;
        private String actionName = "ACCOUNT_LIMIT_SET";
        private String result;
        private ProgressDialog pDialog;
        private String errorCode;

        public TransactionLimitSubmit(String transLimit, String trfType, String limitType, String accNo, String otp, String tpin) {

            this.transLimit = transLimit;
            this.trfType = trfType;
            this.limitType = limitType;
            this.accNo = accNo;
            this.otp = otp;
            this.tpin = tpin;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMax(45);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // https://59.95.101.93:8024/mbank.svc/api
        // {"ac_no":"002103760021576 ","trf_type":"1","limit_type":"1","limit_value":"600","otp":"123456","tpin":"0011"}
        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();
                String jsonString = "{\"ac_no\":\"" + accNo + "\",\"trf_type\":\"" + trfType + "\",\"limit_type\":\"" + limitType + "\",\"limit_value\":\"" + transLimit + "\",\"otp\":\"" + otp + "\",\"tpin\":\"" + tpin + "\"}";

                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonString, actionName, AppConstants.getAuth_token());
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

                    JSONObject responseJsonObject = jsonResponse.has("response") ? jsonResponse.getJSONObject("response") : null;
                    if (responseJsonObject != null) {
                        JSONArray dataArray = responseJsonObject.has("Table") ? responseJsonObject.getJSONArray("Table") : null;
                        if (dataArray != null) {
                            response = dataArray.getJSONObject(0).getString("limit_msg");
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
                pDialog.dismiss();
                if (!this.error.equals("")) {
                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    }
                    if (!TextUtils.isEmpty(error) && TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                } else {
                    if (response != null) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "", response, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(OtpVerificationActivity.this);
    }


    private class VerifyIMPSTransactionAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        ProgressDialog pDialog;
        String response;
        String result;
        private String errorCode;
        private IMPSTransactionRequestModel miniStatementModel;
        private TMessage msg;
        private String TranAmount, TranType, ImpsMessage, benname, BeneMobileNo, BeneMMID, TrandateTime, BeneAccNo, IFSCCode, BENUPIID;

        public VerifyIMPSTransactionAsyncTask(Context ctx, IMPSTransactionRequestModel miniStatementModel) {
            this.ctx = ctx;
            this.miniStatementModel = miniStatementModel;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {

            try {

                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(OtpVerificationActivity.this,
                        jsonString, TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {

                  /*  msg = msgDto.CheckIMPSFTTransactionsDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                            miniStatementModel.getRemMobile(), miniStatementModel.getRemAcno(),
                            "", "",
                            ""*//*mMmid*//*,
                            AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no(),
                            miniStatementModel.getRrn(), miniStatementModel.getChannelRefNo()); //miniStatementModel.getChannelRefNo()
                    Log.d("msg.GetXml():", msg.GetXml());*/

                    if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("12")) { //12--UPI
                        msg = msgDto.CheckUPIFTTransactionsDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                                miniStatementModel.getRemMobile(), miniStatementModel.getRemAcno(),
                                "", "",
                                ""/*mMmid*/,
                                AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no(),
                                miniStatementModel.getRrn(), miniStatementModel.getChannelRefNo()); //miniStatementModel.getChannelRefNo()
                        Log.d("msg.GetXml():", msg.GetXml());
                    } else if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("4") || miniStatementModel.getSwitchReqType().equalsIgnoreCase("8")) { //4--p2p, 8--p2a
                        msg = msgDto.CheckIMPSFTTransactionsDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                                miniStatementModel.getRemMobile(), miniStatementModel.getRemAcno(),
                                "", "",
                                ""/*mMmid*/,
                                AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no(),
                                miniStatementModel.getRrn(), miniStatementModel.getChannelRefNo()); //miniStatementModel.getChannelRefNo()
                        Log.d("msg.GetXml():", msg.GetXml());
                    } else if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("13")) { //13--For NEFT
                        msg = msgDto.CheckNEFTFTTransactionsDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                                miniStatementModel.getRemMobile(), miniStatementModel.getRemAcno(),
                                "", "",
                                ""/*mMmid*/,
                                AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no(),
                                miniStatementModel.getRrn(), miniStatementModel.getChannelRefNo()); //miniStatementModel.getChannelRefNo()
                        Log.d("msg.GetXml():", msg.GetXml());
                    } else if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("19")) { //19--For Bill PAy


                        JSONObject billerjsonObj = new JSONObject();
                        billerjsonObj.put("billerId", miniStatementModel.getBbpsBillerId());

                        String base64Data = Base64.encodeToString(billerjsonObj.toString().getBytes(), Base64.NO_WRAP);

                        msg = msgDto.CheckBillPayementStatusDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                                miniStatementModel.getRemMobile(), miniStatementModel.getRemAcno(),
                                "", "",
                                ""/*mMmid*/,
                                AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no(),
                                miniStatementModel.getRrn(), miniStatementModel.getChannelRefNo(), base64Data); //miniStatementModel.getChannelRefNo()
                        Log.d("msg.GetXml():", msg.GetXml());
                    }


                    String base64EncodedRequestJson = Base64.encodeToString(msg.GetXml().getBytes(), Base64.NO_WRAP);

                    String jsonStringRequest = "{\"data\":\"" + base64EncodedRequestJson + "\"}";
                    TrustMethods.LogMessage("", "base64EncodedRequestJson : " + jsonStringRequest);

                    if (!url.equals("")) {
                        result = HttpClientWrapper.postWitInstituteAuthHeader(url, jsonStringRequest, AppConstants.getAuth_token(), AppConstants.INSTITUTION_ID);
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

                        if (responseMsg.ActCode.Value.equals("0") || responseMsg.ActCode.Value.equals("000") ||
                                responseMsg.ActCode.Value.equals("0000")) {

                            TranAmount = responseMsg.TranAmount.Value;
                            TranType = responseMsg.TranType.Value;
                            ImpsMessage = responseMsg.ImpsMessage.Value;
                            benname = responseMsg.BeneName.Value;
                            BeneMobileNo = responseMsg.BeneMobileNo.Value;
                            BeneMMID = responseMsg.BeneMMID.Value;
                            BeneAccNo = responseMsg.BeneAccNo.Value;
                            IFSCCode = responseMsg.IFSCCode.Value;
                            TrandateTime = responseMsg.TrandateTime.Value;
                            BENUPIID = responseMsg.BENEUPIID.Value;
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

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                if (!this.error.equals("")) {
                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else if (!TextUtils.isEmpty(error) && TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }

                } else {

                    IMPSTransactionRequestModel resultImpsTransactionModel = new IMPSTransactionRequestModel();
                    if (TextUtils.isEmpty(benname)) {
                        resultImpsTransactionModel.setBenName(miniStatementModel.getBenName());
                    } else {
                        resultImpsTransactionModel.setBenName(benname);
                    }

                    if (TextUtils.isEmpty(TranAmount)) {
                        resultImpsTransactionModel.setAmount(miniStatementModel.getAmount());
                    } else {
                        resultImpsTransactionModel.setAmount(TranAmount);
                    }

                    if (TextUtils.isEmpty(TranType)) {
                        resultImpsTransactionModel.setTranType(miniStatementModel.getTranType());
                    } else {
                        resultImpsTransactionModel.setTranType(TranType);
                    }

                    if (TextUtils.isEmpty(ImpsMessage)) {
                        resultImpsTransactionModel.setImpsResultMessage("Transaction Successful.");
                    } else {
                        resultImpsTransactionModel.setImpsResultMessage(ImpsMessage);
                    }

                    if (TextUtils.isEmpty(BeneMobileNo)) {
                        resultImpsTransactionModel.setBenMobile(miniStatementModel.getBenMobile());
                    } else {
                        resultImpsTransactionModel.setBenMobile(BeneMobileNo);
                    }

                    if (TextUtils.isEmpty(BeneMMID)) {
                        resultImpsTransactionModel.setBenMmid(miniStatementModel.getBenMmid());
                    } else {
                        resultImpsTransactionModel.setBenMmid(BeneMMID);
                    }

                    if (TextUtils.isEmpty(TrandateTime)) {
                        resultImpsTransactionModel.setTransDateTime(miniStatementModel.getLogTime());
                    } else {
                        resultImpsTransactionModel.setTransDateTime(TrandateTime);
                    }

                    if (TextUtils.isEmpty(BeneAccNo)) {
                        resultImpsTransactionModel.setBenAcno(miniStatementModel.getBenAcno());
                    } else {
                        resultImpsTransactionModel.setBenAcno(BeneAccNo);
                    }
                    if (TextUtils.isEmpty(IFSCCode)) {
                        resultImpsTransactionModel.setBenIfsc(miniStatementModel.getBenIfsc());
                    } else {
                        resultImpsTransactionModel.setBenIfsc(IFSCCode);
                    }
                    if (TextUtils.isEmpty(BENUPIID)) {
                        resultImpsTransactionModel.setBenUpiId(miniStatementModel.getBenUpiId());
                    } else {
                        resultImpsTransactionModel.setBenUpiId(BENUPIID);
                    }

                    resultImpsTransactionModel.setRemName(miniStatementModel.getRemName());
                    resultImpsTransactionModel.setRemMobile(miniStatementModel.getRemMobile());
                    resultImpsTransactionModel.setRemAcno(miniStatementModel.getRemAcno());
                    resultImpsTransactionModel.setRrn(miniStatementModel.getRrn());
                    resultImpsTransactionModel.setSwitchReqType(miniStatementModel.getSwitchReqType());

                    FragmentManager manager = getSupportFragmentManager();
                    DialogFragment newFragment = ImpsTransactionResultFragment.newInstance(resultImpsTransactionModel);
                    newFragment.show(manager, "dialog");


                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(OtpVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class FundNEFTSWithSwitchTransferAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse, doubtfullResponse;
        ProgressDialog pDialog;
        String mAccountType;
        String mpin;
        String otp;
        ArrayList<FundTransferSubModel> mFundTransferModalDataList;
        private TMessage msg;
        String response;
        String result;
        private String errorCode;
        GenerateStanRRNModel generateStanRRNModel;

        public FundNEFTSWithSwitchTransferAsyncTask(Context ctx, String accountType,
                                                    ArrayList<FundTransferSubModel> fundTransferModalDataList,
                                                    String mpin, String otp) {
            this.ctx = ctx;
            this.mAccountType = accountType;
            this.mpin = mpin;
            this.otp = otp;
            this.mFundTransferModalDataList = fundTransferModalDataList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                // String jsonString = "{\"filter\":[\"stan\"]}";
                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                generateStanRRNModel = TMessageUtil.GetNextStanRrn(OtpVerificationActivity.this, jsonString,
                        TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {

                    msg = msgDto.GetRTGSTransferToAccountNoDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                            AppConstants.getUSERMOBILENUMBER(), mFundTransferModalDataList.get(0).getAccNo(), mFundTransferModalDataList.get(0).getRemitterAccName(),
                            mFundTransferModalDataList.get(0).getBenAccNo(), mFundTransferModalDataList.get(0).getBenIfscCode(),
                            mFundTransferModalDataList.get(0).getAmt(), mFundTransferModalDataList.get(0).getRemark(),
                            AppConstants.INSTITUTION_ID, mFundTransferModalDataList.get(0).getBenAccName(), generateStanRRNModel.getChannel_ref_no());

                    String base64EncodedRequestJson = Base64.encodeToString(msg.GetXml().getBytes(), Base64.NO_WRAP);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("otp", otp);
                    jsonObject.put("tpin", mpin); //TODO
                    jsonObject.put("data", base64EncodedRequestJson);

                    if (!url.equals("")) {
                        TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWitInstituteAuthHeader(url, jsonObject.toString()
                                /*jsonStringRequest*/, AppConstants.getAuth_token(), AppConstants.INSTITUTION_ID);
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
                            finalResponse = responseMsg.TransRefNo.Value;
                        } else if (responseMsg.ActCode.Value.equals(AppConstants.DOUBTFULL_TRANSACTION)) {
                            doubtfullResponse = responseMsg.ActCodeDesc.Value;
                        } else {
                            error = "Act Code: " + responseMsg.ActCode.Value + ": " + responseMsg.ActCodeDesc.Value;
                        }
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

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this," ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                    clearFields();
                } else {

                    String isImpsTransactionCheckStatus = "0";

                    /*   if (isImpsTransactionCheckStatus.equals("0")) {*/

                    if (!TextUtils.isEmpty(finalResponse)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.msg_transaction_success), "Your Account No " + mFundTransferModalDataList.get(0).getAccNo() + " is debited for Rs." + mFundTransferModalDataList.get(0).getAmt() + " & Credited to Account No " + mFundTransferModalDataList.get(0).getBenAccNo() + ".(Txn Ref No." + finalResponse + ")", getResources().getString(R.string.btn_ok), -1, false, alertDialogOkListener);
                    } else if (!TextUtils.isEmpty(doubtfullResponse)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "NEFT Transaction", doubtfullResponse, getResources().getString(R.string.btn_ok), -1, false, alertDialogOkListener);
                    }

                   /* } else {

                        //TODO in future after successfull transaction we should have to open this page.
                        IMPSTransactionRequestModel impsTransactionRequestModel = new IMPSTransactionRequestModel();
                        impsTransactionRequestModel.setRemMobile(AppConstants.getUSERMOBILENUMBER());
                        impsTransactionRequestModel.setRemAcno(mFundTransferModalDataList.get(0).getToAccNo());
                        impsTransactionRequestModel.setRrn(finalResponse);
                        impsTransactionRequestModel.setChannelRefNo(generateStanRRNModel.getChannel_ref_no());

                        new VerifyIMPSTransactionAsyncTask(OtpVerificationActivity.this, impsTransactionRequestModel).execute();
                    }*/


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class FundUPISWithSwitchTransferAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse, doubtfullResponse;
        ProgressDialog pDialog;
        String mAccountType;
        String mpin;
        String otp;
        ArrayList<FundTransferSubModel> mFundTransferModalDataList;
        private TMessage msg;
        String response;
        String result;
        private String errorCode;
        GenerateStanRRNModel generateStanRRNModel;
        TMessage responseMsg;

        public FundUPISWithSwitchTransferAsyncTask(Context ctx, String accountType,
                                                   ArrayList<FundTransferSubModel> fundTransferModalDataList,
                                                   String mpin, String otp) {
            this.ctx = ctx;
            this.mAccountType = accountType;
            this.mpin = mpin;
            this.otp = otp;
            this.mFundTransferModalDataList = fundTransferModalDataList;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                // String jsonString = "{\"filter\":[\"stan\"]}";
                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                generateStanRRNModel = TMessageUtil.GetNextStanRrn(OtpVerificationActivity.this, jsonString,
                        TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    if (generateStanRRNModel.getError().equalsIgnoreCase("Old auth token.")) {
                        errorCode = "9004";

                    }
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;


                } else {
                    msg = msgDto.GetUPITransferTransactionsDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                            AppConstants.getUSERMOBILENUMBER(), mFundTransferModalDataList.get(0).getAccNo(), mFundTransferModalDataList.get(0).getRemitterAccName(),
                            mFundTransferModalDataList.get(0).getBenAccNo(), mFundTransferModalDataList.get(0).getBenIfscCode(),
                            mFundTransferModalDataList.get(0).getAmt(), mFundTransferModalDataList.get(0).getRemark(),
                            AppConstants.INSTITUTION_ID, mFundTransferModalDataList.get(0).getBenAccName(), generateStanRRNModel.getChannel_ref_no(), mFundTransferModalDataList.get(0).getUpiId());

                    String base64EncodedRequestJson = Base64.encodeToString(msg.GetXml().getBytes(), Base64.NO_WRAP);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("otp", otp);
                    jsonObject.put("tpin", mpin); //TODO
                    jsonObject.put("data", base64EncodedRequestJson);

                    if (!url.equals("")) {
                        TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWitInstituteAuthHeader(url, jsonObject.toString()
                                /*jsonStringRequest*/, AppConstants.getAuth_token(), AppConstants.INSTITUTION_ID);
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
                        responseMsg = (TMessage) resParse.response;

                        if (responseMsg.ActCode.Value.equals("000") || responseMsg.ActCode.Value.equals("0")) {
                            finalResponse = responseMsg.TransRefNo.Value;
                        } else if (responseMsg.ActCode.Value.equals(AppConstants.DOUBTFULL_TRANSACTION) || responseMsg.ActCode.Value.equals(AppConstants.DOUBTFULL)) {
                            doubtfullResponse = responseMsg.ActCodeDesc.Value;
                        } else {
                            error = "Act Code: " + responseMsg.ActCode.Value + ": " + responseMsg.ActCodeDesc.Value;
                        }
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

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                // Toast.makeText(getApplicationContext(),"acc Code "+ responseMsg.ActCode.Value,Toast.LENGTH_SHORT).show();
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this," ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                    clearFields();
                } else {

                    String isImpsTransactionCheckStatus = "0";

                    /* if (isImpsTransactionCheckStatus.equals("0")) {*/
                    if (!TextUtils.isEmpty(finalResponse)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.msg_transaction_success), "Your Account No " + mFundTransferModalDataList.get(0).getAccNo() + " is debited for Rs." + mFundTransferModalDataList.get(0).getAmt() + " & Credited to UPI Id " + mFundTransferModalDataList.get(0).getUpiId() + ".(Txn Ref No." + finalResponse + ")", getResources().getString(R.string.btn_ok), 4, false, alertDialogOkListener);
                    } else if (!TextUtils.isEmpty(doubtfullResponse)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "UPI Transaction", doubtfullResponse, getResources().getString(R.string.btn_ok), -1, false, alertDialogOkListener);
                    }
                   /* } else {
                        //TODO in future after successfull transaction we should have to open this page.
                        IMPSTransactionRequestModel impsTransactionRequestModel = new IMPSTransactionRequestModel();
                        impsTransactionRequestModel.setRemMobile(AppConstants.getUSERMOBILENUMBER());
                        impsTransactionRequestModel.setRemAcno(mFundTransferModalDataList.get(0).getToAccNo());
                        impsTransactionRequestModel.setRrn(finalResponse);
                        impsTransactionRequestModel.setChannelRefNo(generateStanRRNModel.getChannel_ref_no());

                        new VerifyIMPSTransactionAsyncTask(OtpVerificationActivity.this, impsTransactionRequestModel).execute();
                    }*/

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AddUPIBeneficiaryAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "";
        private Context ctx;
        private String response, benActivateTimeInMins;
        private ProgressDialog pDialog;
        private String mMobileNo;
        private String mNickname;
        private String mAccName;
        private String mAccNo;
        private String mIfscCode;
        private String mMmid;
        private String mBenfMobNo;
        private String ben_type;
        private String actionName = "ADD_UPI_BENEFICIARY";
        private String result;
        private String otp;
        private String mpin;
        private String upi_id;
        private Boolean mWithinChecked;
        private String errorCode;

        public AddUPIBeneficiaryAsyncTask(Context ctx, String mobileNo, String nickname, String accName, String accNo, String ifscCode, String benfMobNo, String mmid,
                                          boolean withinChecked, String ben_type, String otp, String mpin, String upi_id) {
            this.ctx = ctx;
            this.mMobileNo = mobileNo;
            this.mNickname = nickname;
            this.mAccName = accName;
            this.mAccNo = accNo;
            this.mIfscCode = ifscCode;
            this.mBenfMobNo = benfMobNo;
            this.mMmid = mmid;
            this.mWithinChecked = withinChecked;
            this.ben_type = ben_type;
            this.otp = otp;
            this.mpin = mpin;
            this.upi_id = upi_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();

                String jsonString = "{\"mobile_number\":\"" + mMobileNo + "\",\"ben_nickname\":\"" + mNickname + "\"," + "\"ben_ac_name\":\"" + mAccName + "\"," + "\"ben_type\":\"" + ben_type + "\"," + "\"ben_upi_id\":\"" + upi_id + "\"}";
                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);

                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonString, actionName, AppConstants.getAuth_token());
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
                        error = jsonResponse.getString("error");
                        return error;
                    }

                    JSONArray responseArray = responseObject.getJSONArray("Table");
                    if (responseArray.length() > 0) {
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject dataObject = responseArray.getJSONObject(i);
                            response = dataObject.has("ben_id") ? dataObject.getString("ben_id") : "NA";
                            // benActivateTimeInMins = dataObject.has("ben_activate_after_mins") ? dataObject.getString("ben_activate_after_mins") : "NA";
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
                if (response != null) {
                    String message = "Beneficiary is successfully added as " + mNickname + ". ";
                    AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "", message, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                }
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "Session Expired!", "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                       Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this," ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class BillPaymentAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "", responseError = "", status = "";
        private Context ctx;
        private String response;
        private String result;
        private ProgressDialog pDialog;
        private String data;
        //        String action = "send_to_switch";
        private String finalResponse;
        private String errorCode, errorDetails;

        HashMap<String, Object> billDetailsMap = new HashMap<>();
        HashMap<String, Object> additionalInfoMap = new HashMap<>();
        BBPSBillerResponseModel bbpsBillerResponseModel;
        private String accountNo, remitterAccName, billAmount, remark, billerId, billerName, billNumber;
        private String doubtfullResponse, tpin, otp;


        public BillPaymentAsyncTask(Context ctx, String data, String accountNo,
                                    String remitterAccName,
                                    String billAmount, String remark,
                                    String billerId, String billerName, String billNumber,
                                    String tpin, String otp) {
            this.ctx = ctx;
            this.data = data;
            this.accountNo = accountNo;
            this.remitterAccName = remitterAccName;
            this.billAmount = billAmount;
            this.remark = remark;
            this.billerId = billerId;
            this.billerName = billerName;
            this.billNumber = billNumber;
            this.tpin = tpin;
            this.otp = otp;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
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
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(OtpVerificationActivity.this, jsonString, TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {
                    String base64Data = Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);

                    TMessage msg = msgDto.BillPaymentRequest(TMessageUtil.GetLocalTxnDtTime(),
                            generateStanRRNModel.getStan(),
                            generateStanRRNModel.getChannel_ref_no(), base64Data, accountNo,
                            AppConstants.getUSERMOBILENUMBER(), remitterAccName, billAmount, remark, billerId,
                            billerName, billNumber); //TMessageUtil.MSG_INSTITUTION_ID);

                    Log.d("msg.GetXml()" + "" + ":", msg.GetXml());

                    String base64EncodedRequestJson = Base64.encodeToString(msg.GetXml().getBytes(), Base64.NO_WRAP);

                    //   String jsonStringRequest = "{\"data\":\"" + base64EncodedRequestJson + "\"}";

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("otp", otp);
                    jsonObject.put("tpin", tpin); //TODO
                    jsonObject.put("data", base64EncodedRequestJson);

                    TrustMethods.LogMessage("", "base64EncodedRequestJson : " + jsonObject.toString());

                    if (!url.equals("")) {

                        result = HttpClientWrapper.postWitAuthHeader(url, jsonObject.toString(), AppConstants.getAuth_token());

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
                        if (responseMsg.ActCode.Value.equals("000") || responseMsg.ActCode.Value.equals("0")) {
                            finalResponse = responseMsg.TransRefNo.Value;
                        } else if (responseMsg.ActCode.Value.equals(AppConstants.DOUBTFULL_TRANSACTION) || responseMsg.ActCode.Value.equals(AppConstants.DOUBTFULL)) {
                            doubtfullResponse = responseMsg.ActCodeDesc.Value;
                        } else {
                            error = "Act Code: " + responseMsg.ActCode.Value + ": " + responseMsg.ActCodeDesc.Value;
                        }

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
                if (!TextUtils.isEmpty(error) || !TextUtils.isEmpty(errorCode) || !TextUtils.isEmpty(responseError)) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, status, error, getResources().getString(R.string.btn_ok), 10, false, alertDialogOkListener);

                    }

                } else {

                    if (!TextUtils.isEmpty(finalResponse)) {
                        String message = "Your " + billerName + " of Bill Number " + billNumber + " of ₹ " + billAmount +
                                " has been Paid Successfully." + "(Txn Ref No." + finalResponse + ")";
                        // String message = "Bill Payment of Rs. " + billAmount + " for Bill No. " + billNumber + " of Biller Name " + billerName + " Paid Successfully. (Txn Ref No." + finalResponse + ")";
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this,
                                getResources().getString(R.string.msg_transaction_success),
                                message, getResources().getString(R.string.btn_ok), 1,
                                false, alertDialogOkListener);
                    } else if (!TextUtils.isEmpty(doubtfullResponse)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "Bill Payment!!!", doubtfullResponse, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void alertDialogOk(Context context, String title, String message,
                              String button) {

        try {
//            final AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.customDialogue);
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setCancelable(true);
            alert.setTitle(title);
            alert.setMessage(message);
            alert.setPositiveButton(button,
                    (dialog, arg1) -> {
                    });

            final AlertDialog alertDialog = alert.create();
            Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.dialogTheme;
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                alertDialog.dismiss();

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO Fetch Mandate deatils as per account id.
    private class GetMandateAccountDetailsAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        String response;
        ProgressDialog pDialog;
        String actionName = "SAVE_CANCEL_MANDATE_REQUEST";
        String mAccNo, umrnNo, ecsMandateId,otp,tpin;
        String result;
        private String errorCode;

        public GetMandateAccountDetailsAsyncTask(String accNo, String umrnNo, String ecsMandateId,String otp,String tpin) {
            this.mAccNo = accNo;
            this.umrnNo = umrnNo;
            this.ecsMandateId = ecsMandateId;
            this.otp = otp;
            this.tpin = tpin;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OtpVerificationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();
                String jsonString = "{\"acc_no\":\"" + mAccNo + "\",\"umrn_no\":\"" + umrnNo + "\",\"otp\":\"" + otp + "\",\"tpin\":\"" + tpin + "\",\"ecs_mandate_id\":\"" + ecsMandateId + "\"}";

                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonString, actionName, AppConstants.getAuth_token());
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
                    JSONArray accountJsonArray = jsonResponse.getJSONObject("response").getJSONArray("response_msg");
                    JSONObject jsonObject = accountJsonArray.getJSONObject(0);
                    response = jsonObject.has("msg") ? jsonObject.getString("msg") : "";

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
                pDialog.dismiss();
                if (!this.error.equals("")) {
                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    }
                    if (!TextUtils.isEmpty(error) && TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, " ", this.error, getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);

                    }
                } else {
                    if (response != null) {
                        AlertDialogMethod.alertDialogOk(OtpVerificationActivity.this, "", response, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}