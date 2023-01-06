package com.trustbank.activity;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.trustbank.Model.BlockDebitReason;
import com.trustbank.Model.DebitCardModels;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DebitCardPinGenerationActivity extends AppCompatActivity implements AlertDialogOkListener {

    private TrustMethods method;
    private Spinner spinnerFrmAct, spinnerDebitCardId, spinnerReason;
    private TextView txtCustName, txtCustMobNo, txtCustEmail;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private List<String> accountList;
    private CardView debitCardView, submitCardViewId, reasonTypeCardViewId, reasonOtherTypeCardViewId;
    private CoordinatorLayout debitCardBlockCLId;
    private AlertDialogOkListener alertDialogOkListener = this;
    private Button btnSubmitCardDetails;
    private EditText reasonOtherEtId, etnewPinId, etConfrimPINId, etRemarksId;
    private DebitCardModels debitCardModels;
    private BlockDebitReason blockDebitReason;
    private TextInputLayout etnewPinTextInput, etConfirmPinTextInput;
    private LinearLayout pinLLId;


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
                        TrustMethods.naviagteToSplashScreen(DebitCardPinGenerationActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SetTheme.changeToTheme(DebitCardPinGenerationActivity.this, false);
        setContentView(R.layout.activity_debit_card_pin_gneration);
        inIt();
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

    private void inIt() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        method = new TrustMethods(DebitCardPinGenerationActivity.this);
        debitCardBlockCLId = findViewById(R.id.debitCardBlockCLId);
        spinnerFrmAct = findViewById(R.id.spinnerFrmActId);
        spinnerDebitCardId = findViewById(R.id.spinnerDebitCardId);
        spinnerReason = findViewById(R.id.spinnerReasonId);
        debitCardView = findViewById(R.id.debitCardViewId);
        submitCardViewId = findViewById(R.id.submitCardViewId);
        reasonTypeCardViewId = findViewById(R.id.reasonTypeViewId);
        reasonOtherTypeCardViewId = findViewById(R.id.reasonOtherTypeCardViewId);
        btnSubmitCardDetails = findViewById(R.id.btnSubmitCardDetails);
        reasonOtherEtId = findViewById(R.id.reasonOtherEtId);

        etnewPinTextInput = findViewById(R.id.etnewPinTextInput);
        etnewPinId = findViewById(R.id.etnewPinId);
        etConfirmPinTextInput = findViewById(R.id.etConfirmPinTextInput);
        etConfrimPINId = findViewById(R.id.etConfrimPINId);
        etRemarksId = findViewById(R.id.etRemarksId);
        pinLLId = findViewById(R.id.pinLLId);
        pinLLId.setVisibility(View.GONE);

        accNumberSpinner();
    }

    private void accNumberSpinner() {
        try {
            accountsArrayList = method.getArrayList(DebitCardPinGenerationActivity.this, "AccountListPref");


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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(DebitCardPinGenerationActivity.this, android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFrmAct.setAdapter(adapter);
            }

            spinnerFrmAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    try {
                        if (position != 0) {
                            String selectedAccNo = (String) adapterView.getItemAtPosition(position);
                            String accNo = "";
                            if (selectedAccNo.contains("-")) {
                                String[] accounts = selectedAccNo.split("-");
                                accNo = accounts[0];
                            } else {
                                accNo = selectedAccNo;
                            }

                            if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(DebitCardPinGenerationActivity.this)) {
                                if (NetworkUtil.getConnectivityStatus(DebitCardPinGenerationActivity.this)) {
                                    new LoadDebitCardDetailsAsyncTask(DebitCardPinGenerationActivity.this, accNo).execute();
                                } else {
                                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), debitCardBlockCLId);
                                }
                            } else {
                                TrustMethods.displaySimErrorDialog(DebitCardPinGenerationActivity.this);
                            }

                        } else {
                            debitCardView.setVisibility(View.GONE);
                            submitCardViewId.setVisibility(View.GONE);
                            reasonTypeCardViewId.setVisibility(View.GONE);
                            reasonOtherTypeCardViewId.setVisibility(View.GONE);

                            pinLLId.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
    public void onDialogOk(int resultCode) {
        if (resultCode == 0) {
            Intent intentLogin = new Intent(getApplicationContext(), LockActivity.class);
            intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentLogin);
            method.activityCloseAnimation();
        }

    }


    @SuppressLint("StaticFieldLeak")
    private class LoadDebitCardDetailsAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "";
        private Context ctx;
        private String response;
        private ProgressDialog pDialog;
        private String actionName = "DEBIT_CARDS_GET";
        private String mAccNo;
        private String result;
        private ArrayList<DebitCardModels> debitCardDetailsModelArrayList = new ArrayList<>();
        private List<BlockDebitReason> blockDebitReasonList = new ArrayList<>();
        private String errorCode;

        public LoadDebitCardDetailsAsyncTask(Context ctx, String accNo) {
            this.ctx = ctx;
            this.mAccNo = accNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DebitCardPinGenerationActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.getAccountDetails(mAccNo);

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
                    JSONArray accountJsonArray = jsonResponse.getJSONObject("response").getJSONArray("card_details");


                    DebitCardModels debitCardModel = new DebitCardModels();
                    debitCardModel.setDebitCardNo("");
                    debitCardModel.setStatus("");
                    debitCardModel.setDebitCardNoForDisplay("Select Debit Card");
                    debitCardDetailsModelArrayList.add(0, debitCardModel);

                    for (int i = 0; i < accountJsonArray.length(); i++) {
                        JSONObject cardDeatils = accountJsonArray.getJSONObject(i);
                        String cardNo = cardDeatils.has("card_no") ? cardDeatils.getString("card_no") : "";
                        String cardStatus = cardDeatils.has("card_status") ? cardDeatils.getString("card_status") : "";
                        String maskedCardNo = cardDeatils.has("masked_card_no") ? cardDeatils.getString("masked_card_no") : "";
                        DebitCardModels debitCardModels = new DebitCardModels();
                        debitCardModels.setDebitCardNo(cardNo);
                        debitCardModels.setStatus(cardStatus);
                        debitCardModels.setDebitCardNoForDisplay(maskedCardNo + " - " + cardStatus);
                        debitCardDetailsModelArrayList.add(debitCardModels);
                    }

                    /*JSONArray requestModeJsonArray = jsonResponse.getJSONObject("response").getJSONArray("request_mode");

                    BlockDebitReason blockDebitReason1 = new BlockDebitReason();
                    blockDebitReason1.setModeid("");
                    blockDebitReason1.setModeText("Select Reason");
                    blockDebitReasonList.add(0, blockDebitReason1);

                    for (int i = 0; i < requestModeJsonArray.length(); i++) {
                        JSONObject jsonObject = requestModeJsonArray.getJSONObject(i);
                        String modeId = jsonObject.has("modeid") ? jsonObject.getString("modeid") : "";
                        String modeText = jsonObject.has("modeText") ? jsonObject.getString("modeText") : "";
                        BlockDebitReason blockDebitReason = new BlockDebitReason();
                        blockDebitReason.setModeid(modeId);
                        blockDebitReason.setModeText(modeText);
                        blockDebitReasonList.add(blockDebitReason);
                    }*/

                    BlockDebitReason blockDebitReason0 = new BlockDebitReason();
                    blockDebitReason0.setModeid("0");
                    blockDebitReason0.setModeText("Select Reason");
                    blockDebitReasonList.add(blockDebitReason0);

                    BlockDebitReason blockDebitReason = new BlockDebitReason();
                    blockDebitReason.setModeid("1");
                    blockDebitReason.setModeText("Pin not Received");
                    blockDebitReasonList.add(blockDebitReason);

                    BlockDebitReason blockDebitReason1 = new BlockDebitReason();
                    blockDebitReason1.setModeid("2");
                    blockDebitReason1.setModeText("Pin Forgotten");
                    blockDebitReasonList.add(blockDebitReason1);


                    BlockDebitReason blockDebitReason2 = new BlockDebitReason();
                    blockDebitReason2.setModeid("3");
                    blockDebitReason2.setModeText("PIN illegible");
                    blockDebitReasonList.add(blockDebitReason2);


                    BlockDebitReason blockDebitReason3 = new BlockDebitReason();
                    blockDebitReason3.setModeid("4");
                    blockDebitReason3.setModeText("REPIN for Welcome KIT card");
                    blockDebitReasonList.add(blockDebitReason3);

                    BlockDebitReason blockDebitReason4 = new BlockDebitReason();
                    blockDebitReason4.setModeid("6");
                    blockDebitReason4.setModeText("Other");
                    blockDebitReasonList.add(blockDebitReason4);

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
                if (debitCardDetailsModelArrayList != null && debitCardDetailsModelArrayList.size() != 0) {
                    debitCardView.setVisibility(View.VISIBLE);
                    displayDebitCardDetailsDropdown(debitCardDetailsModelArrayList, mAccNo, blockDebitReasonList);
                } else {
                    pinLLId.setVisibility(View.GONE);

                    debitCardView.setVisibility(View.GONE);
                    reasonTypeCardViewId.setVisibility(View.GONE);
                    spinnerReason.setSelection(0);
                    reasonOtherTypeCardViewId.setVisibility(View.GONE);
                    if (!this.error.equals("")) {
                        if (TrustMethods.isSessionExpired(errorCode)) {
                            AlertDialogMethod.alertDialogOk(DebitCardPinGenerationActivity.this,
                                    getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok),
                                    0, false, alertDialogOkListener);
                        } else {
                            TrustMethods.showSnackBarMessage(this.error, debitCardBlockCLId);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void displayDebitCardDetailsDropdown(ArrayList<DebitCardModels> debitCardDetailsModelArrayList,
                                                 String accountNo, List<BlockDebitReason> blockDebitReasonList) {

        try {

            if (debitCardDetailsModelArrayList != null && debitCardDetailsModelArrayList.size() > 0) {

                ArrayAdapter<DebitCardModels> adapter = new ArrayAdapter<>(DebitCardPinGenerationActivity.this, android.R.layout.simple_spinner_item, debitCardDetailsModelArrayList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDebitCardId.setAdapter(adapter);

                spinnerDebitCardId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            if (position != 0) {
                                reasonTypeCardViewId.setVisibility(View.VISIBLE);
                                debitCardModels = (DebitCardModels) parent.getSelectedItem();
                                String status = debitCardModels.getStatus();
                                if (status.equalsIgnoreCase("Blocked")) {
                                    btnSubmitCardDetails.setBackgroundColor(getResources().getColor(R.color.silverColor));
                                    btnSubmitCardDetails.setClickable(false);
                                    reasonTypeCardViewId.setVisibility(View.GONE);
                                    spinnerReason.setSelection(0);
                                    reasonOtherTypeCardViewId.setVisibility(View.GONE);
                                    submitCardViewId.setVisibility(View.GONE);
                                    pinLLId.setVisibility(View.GONE);
                                } else {
                                    btnSubmitCardDetails.setBackgroundColor(TrustMethods.getColorPrimary(DebitCardPinGenerationActivity.this));
                                    btnSubmitCardDetails.setClickable(true);
                                    reasonTypeCardViewId.setVisibility(View.VISIBLE);
                                    submitCardViewId.setVisibility(View.VISIBLE);
                                    pinLLId.setVisibility(View.VISIBLE);
                                    spinnerReason.setSelection(0);
                                    reasonOtherTypeCardViewId.setVisibility(View.GONE);
                                }
                            } else {
                                reasonOtherTypeCardViewId.setVisibility(View.GONE);
                                submitCardViewId.setVisibility(View.GONE);
                                reasonTypeCardViewId.setVisibility(View.GONE);
                                pinLLId.setVisibility(View.GONE);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                displayREasonDropdown(blockDebitReasonList);

                btnSubmitCardDetails.setOnClickListener(v -> {

                    String cardNo = debitCardModels.getDebitCardNo();
                    String modeid = blockDebitReason.getModeid();
                    String otherReason = "";
                    String pin = etnewPinId.getText().toString();
                    String confirmPin = etConfrimPINId.getText().toString();
                    String remarks = etRemarksId.getText().toString();

                    if (reasonOtherTypeCardViewId.getVisibility() == View.VISIBLE) {
                        if (TextUtils.isEmpty(reasonOtherEtId.getText().toString())) {
                            TrustMethods.showSnackBarMessage("Please enter reason", debitCardBlockCLId);
                            return;
                        } else {
                            otherReason = reasonOtherEtId.getText().toString();
                        }
                    } else if (TextUtils.isEmpty(pin)) {
                        TrustMethods.showSnackBarMessage("Please enter new pin", debitCardBlockCLId);
                        return;
                    } else if (TextUtils.isEmpty(confirmPin)) {
                        TrustMethods.showSnackBarMessage("Please enter confirm pin", debitCardBlockCLId);
                        return;
                    } else if (!pin.equals(confirmPin)) {
                        TrustMethods.showSnackBarMessage("Pin and Confirm pin should be Same.", debitCardBlockCLId);
                        return;
                    }


                    if (!TextUtils.isEmpty(cardNo) && !TextUtils.isEmpty(accountNo)) {
                        Intent intent = new Intent(DebitCardPinGenerationActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("checkTransferType", "debitCardPinGeneration");
                        intent.putExtra("accountNo", accountNo);
                        intent.putExtra("cardNo", cardNo);
                        intent.putExtra("modeid", modeid);
                        intent.putExtra("otherReason", otherReason);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        TrustMethods.showSnackBarMessage("Account No or Card No cannot be empty.", debitCardBlockCLId);
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void displayREasonDropdown(List<BlockDebitReason> reasonList) {
        ArrayAdapter<BlockDebitReason> adapter = new ArrayAdapter<>(DebitCardPinGenerationActivity.this, android.R.layout.simple_spinner_item, reasonList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReason.setAdapter(adapter);

        spinnerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    submitCardViewId.setVisibility(View.VISIBLE);
                    blockDebitReason = (BlockDebitReason) parent.getSelectedItem();
                    String modeId = blockDebitReason.getModeid();
                    if (modeId.trim().equalsIgnoreCase("6")) {
                        reasonOtherTypeCardViewId.setVisibility(View.VISIBLE);
                    } else {
                        reasonOtherTypeCardViewId.setVisibility(View.GONE);
                    }
                } else {
                    submitCardViewId.setVisibility(View.GONE);
                    reasonOtherTypeCardViewId.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        TrustMethods.showBackButtonAlert(DebitCardPinGenerationActivity.this);
    }


}
