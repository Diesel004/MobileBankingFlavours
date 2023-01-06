package com.trustbank.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.trustbank.Model.BbpsComplaintTypeModel;
import com.trustbank.Model.BbpsTransactionRequestModel;
import com.trustbank.Model.FundTransferSubModel;
import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.tcpconnection.tdto.MessageDtoBuilder;
import com.trustbank.tcpconnection.tmessage.TMessage;
import com.trustbank.tcpconnection.tmessage.TMessageUtil;
import com.trustbank.tcpconnection.util.ResponseEntity;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterComplaintsActivity extends AppCompatActivity implements AlertDialogOkListener {

    @BindView(R.id.etRefeneranceNoId)
    EditText etReferenceNoId;

    @BindView(R.id.etRemarks)
    EditText etRemarks;

    @BindView(R.id.btnRegisterComaplintsId)
    Button btnRegisterComplaints;

    @BindView(R.id.spinnerDispositionId)
    Spinner spinnerDispositionId;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    AlertDialogOkListener alertDialogOkListener = this;
    private TrustMethods trustMethods;

    private List<BbpsComplaintTypeModel> bbpsComplaintTypeModel;
    private BbpsTransactionRequestModel bbpsTransactionRequestModel;
    private ArrayList<String> dispositionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            if (savedInstanceState != null) {
                Object currentPID = String.valueOf(android.os.Process.myPid());
                // Check current PID with old PID
                if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                    // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                    TrustMethods.naviagteToSplashScreen(RegisterComplaintsActivity.this);
                }
            }
        }
        SetTheme.changeToTheme(RegisterComplaintsActivity.this, false);
        setContentView(R.layout.activity_register_complaints);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            bbpsComplaintTypeModel = (List<BbpsComplaintTypeModel>) getIntent().getSerializableExtra("dispositionList");
            bbpsTransactionRequestModel = (BbpsTransactionRequestModel) getIntent().getSerializableExtra("bbpsTransactionModel");
            trustMethods = new TrustMethods(this);

            if (bbpsComplaintTypeModel != null && bbpsComplaintTypeModel.size() > 0) {
                dispositionList = new ArrayList<>();
                dispositionList.add(0, "Select Complaint Type");
                for (int i = 0; i < bbpsComplaintTypeModel.size(); i++) {
                    BbpsComplaintTypeModel getUserProfileModal = bbpsComplaintTypeModel.get(i);
                    dispositionList.add(getUserProfileModal.getDisposition());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterComplaintsActivity.this, android.R.layout.simple_spinner_item, dispositionList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDispositionId.setAdapter(adapter);
            }


            btnRegisterComplaints.setOnClickListener(view -> {

                String strRefeNo = etReferenceNoId.getText().toString();
                String strRemarks = etRemarks.getText().toString();
                String type = spinnerDispositionId.getSelectedItem().toString();

           /*     if (TextUtils.isEmpty(strRefeNo)) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_reference_id), coordinatorLayout);
                } else */
                if (type.equalsIgnoreCase("Select Complaint Type")) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_complaint_type_id), coordinatorLayout);
                } else if (TextUtils.isEmpty(strRemarks)) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_description_id), coordinatorLayout);
                } else {
                    registerComplaintsCall(type, strRemarks, bbpsTransactionRequestModel);
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerComplaintsCall(String complaintType, String strRemarks,
                                        BbpsTransactionRequestModel bbpsTransactionRequestModel) {


        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(RegisterComplaintsActivity.this)) {
            if (NetworkUtil.getConnectivityStatus(RegisterComplaintsActivity.this)) {
                new ComplaintRegisterAsyncTask(RegisterComplaintsActivity.this,
                        complaintType, strRemarks,  bbpsTransactionRequestModel).execute();
            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
            }
        } else {
            TrustMethods.displaySimErrorDialog(RegisterComplaintsActivity.this);
        }

    }

    @Override
    public void onDialogOk(int resultCode) {

        try {
            switch (resultCode) {

                case 0:
                    Intent intent = new Intent(RegisterComplaintsActivity.this, LockActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    trustMethods.activityCloseAnimation();
                    break;

                case 1:
                    finish();
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //check validation api call.
    @SuppressLint("StaticFieldLeak")
    private class ComplaintRegisterAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse;
        ProgressDialog pDialog;
        String response;
        String result;
        private String errorCode;
        String remarks;
        String complaintType;
        private TMessage msg;
        private BbpsTransactionRequestModel bbpsTransactionRequestModel;

        public ComplaintRegisterAsyncTask(Context ctx, String complaintType, String remarks,
                                          BbpsTransactionRequestModel bbpsTransactionRequestModel) {
            this.ctx = ctx;
            this.complaintType = complaintType;
            this.remarks = remarks;
            this.bbpsTransactionRequestModel = bbpsTransactionRequestModel;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterComplaintsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {

            try {
                //String jsonString = "{\"filter\":[\"stan\"]}";
                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(RegisterComplaintsActivity.this, jsonString,
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

                    JSONObject mainJsonObject = new JSONObject();
                    JSONObject billerjsonObj = new JSONObject();
                    billerjsonObj.put("disposition", complaintType);
                    mainJsonObject.put("complainReq", billerjsonObj);

                    String base64Data = Base64.encodeToString(mainJsonObject.toString().getBytes(), Base64.NO_WRAP);
                    msg = msgDto.RegsitserComplinatsDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                            AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no(), bbpsTransactionRequestModel.getRrn(),
                            base64Data, AppConstants.getUSERMOBILENUMBER(), bbpsTransactionRequestModel.getBbpsBillerId(), bbpsTransactionRequestModel.getRemAcno(),
                            bbpsTransactionRequestModel.getRemName(), bbpsTransactionRequestModel.getAmount(), remarks);
                    Log.d("msg.GetXml():", msg.GetXml());


                    String base64EncodedRequestJson = Base64.encodeToString(msg.GetXml().getBytes(), Base64.NO_WRAP);
                    String jsonStringRequest = "{\"data\":\"" + base64EncodedRequestJson + "\"}";
                    TrustMethods.LogMessage("", "base64EncodedRequestJson : " + jsonStringRequest);

                    if (!url.equals("")) {
                        //   TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWitAuthHeader(url, jsonStringRequest, AppConstants.getAuth_token());
                        // TrustMethods.LogMessage(TAG, "Frm Enquiry details response-->" + response);
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
                            finalResponse = responseMsg.ActCodeDesc.Value;
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
                TrustMethods.hideSoftKeyboard(RegisterComplaintsActivity.this);
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(RegisterComplaintsActivity.this, getResources().getString(R.string.error_session_expire),
                                "",
                                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(RegisterComplaintsActivity.this, "Status!!!",
                                this.error,
                                getResources().getString(R.string.btn_ok), -1, false, alertDialogOkListener);
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }

                } else {
                    AlertDialogMethod.alertDialogOk(RegisterComplaintsActivity.this, finalResponse,
                            "Complaint Register successfully for the Amount " + bbpsTransactionRequestModel.getAmount() + ".",
                            getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
}