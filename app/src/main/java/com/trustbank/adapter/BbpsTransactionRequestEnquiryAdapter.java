package com.trustbank.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trustbank.Model.BbpsComplaintTypeModel;
import com.trustbank.Model.BbpsTransactionRequestModel;
import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.Model.IMPSTransactionRequestModel;
import com.trustbank.R;
import com.trustbank.activity.OtpVerificationActivity;
import com.trustbank.activity.RegisterComplaintsActivity;
import com.trustbank.activity.WithinBankActivity;
import com.trustbank.fragment.ImpsTransactionResultFragment;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.interfaces.SessionErrorMessageListener;
import com.trustbank.tcpconnection.tdto.MessageDtoBuilder;
import com.trustbank.tcpconnection.tmessage.TMessage;
import com.trustbank.tcpconnection.tmessage.TMessageUtil;
import com.trustbank.tcpconnection.util.ResponseEntity;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


public class BbpsTransactionRequestEnquiryAdapter extends
        RecyclerView.Adapter<BbpsTransactionRequestEnquiryAdapter.ViewHolder> {

    private Activity mActivity;
    private List<BbpsTransactionRequestModel> bbpsTransactionRequestModelList;
    //  private List<BbpsTransactionRequestModel> bbpsdispositionModelList;
    private SessionErrorMessageListener sessionErrorMessageListener;
    private List<BbpsComplaintTypeModel> dispositionRequestModelList;


    public BbpsTransactionRequestEnquiryAdapter(Activity activity,
                                                List<BbpsTransactionRequestModel> bbpsTransactionRequestModelList,
                                                SessionErrorMessageListener sessionErrorMessageListener,
                                                List<BbpsComplaintTypeModel> dispositionRequestModelList) {
        this.mActivity = activity;
        this.bbpsTransactionRequestModelList = bbpsTransactionRequestModelList;
        this.sessionErrorMessageListener = sessionErrorMessageListener;
        this.dispositionRequestModelList = dispositionRequestModelList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bbps_ft_request_enquiry, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            BbpsTransactionRequestModel miniStatementModel = bbpsTransactionRequestModelList.get(position);
            viewHolder.txtLogTimeId.setText(miniStatementModel.getLogTime());
            viewHolder.txtRrnId.setText(miniStatementModel.getRrn());
            viewHolder.txtBenAcnoId.setText(miniStatementModel.getBenAcno());
            viewHolder.txtBenIfscId.setText(miniStatementModel.getBenIfsc());
            viewHolder.txtBenMmidId.setText(miniStatementModel.getBenMmid());
            viewHolder.txtBenNameId.setText(miniStatementModel.getBenName());
            viewHolder.txtRemAcnoId.setText(miniStatementModel.getRemAcno());
            viewHolder.txtRemNameId.setText(miniStatementModel.getRemName());
            viewHolder.txtChannel_ref_noId.setText(miniStatementModel.getChannelRefNo());
            viewHolder.txtAmountId.setText("\u20B9" + " " + miniStatementModel.getAmount());
            viewHolder.txtBenMobNumberId.setText(miniStatementModel.getBenMobile());
            viewHolder.txtBillerId.setText(miniStatementModel.getBbpsBillerId());


            if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("4")) { //p2p
                viewHolder.txtTransTypeId.setText("IMPS-MMID");
            } else if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("8")) { //p2a
                viewHolder.txtTransTypeId.setText("IMPS-A/C");
            } else if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("12")) { //UPI
                viewHolder.txtTransTypeId.setText("UPI");
            } else if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("13")) { //NEFT
                viewHolder.txtTransTypeId.setText("NEFT");
            } else if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("19")) { //Bill
                viewHolder.txtTransTypeId.setText("Bill-Pay");
            }

            if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("8")
                    || miniStatementModel.getSwitchReqType().equalsIgnoreCase("13")) {
                viewHolder.linearBenMMIDId.setVisibility(View.GONE);
                viewHolder.linearBenUPIId.setVisibility(View.GONE);
                viewHolder.linearBenMobileNo.setVisibility(View.GONE);
                viewHolder.linearBenIFSCId.setVisibility(View.VISIBLE);
                viewHolder.linearBenAccNoId.setVisibility(View.VISIBLE);
                //  viewHolder.btnComplaintEnquiry.setVisibility(View.GONE);
            } else if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("12")) {
                viewHolder.linearBenUPIId.setVisibility(View.VISIBLE);
                viewHolder.linearBenIFSCId.setVisibility(View.GONE);
                viewHolder.linearBenAccNoId.setVisibility(View.GONE);
                viewHolder.linearBenMMIDId.setVisibility(View.GONE);
                viewHolder.linearBenMobileNo.setVisibility(View.GONE);
                // viewHolder.btnComplaintEnquiry.setVisibility(View.GONE);
            } else if (miniStatementModel.getSwitchReqType().equalsIgnoreCase("19")) {
                viewHolder.linearBenUPIId.setVisibility(View.GONE);
                viewHolder.linearBenIFSCId.setVisibility(View.GONE);
                viewHolder.linearBenAccNoId.setVisibility(View.GONE);
                viewHolder.linearBenMMIDId.setVisibility(View.GONE);
                viewHolder.linearBenMobileNo.setVisibility(View.GONE);
                //  viewHolder.benNameLinearId.setVisibility(View.GONE);
                viewHolder.linearBillerId.setVisibility(View.VISIBLE);
                // viewHolder.btnComplaintEnquiry.setVisibility(View.VISIBLE);
            } else {
                viewHolder.linearBenMMIDId.setVisibility(View.VISIBLE);
                viewHolder.linearBenMobileNo.setVisibility(View.VISIBLE);
                viewHolder.linearBenIFSCId.setVisibility(View.GONE);
                viewHolder.linearBenUPIId.setVisibility(View.GONE);
                viewHolder.linearBenAccNoId.setVisibility(View.GONE);
                // viewHolder.btnComplaintEnquiry.setVisibility(View.GONE);
            }

            viewHolder.btnRegBBPSEnquiry.setOnClickListener(view -> {
                if (NetworkUtil.getConnectivityStatus(Objects.requireNonNull(mActivity))) {
                    new VerifyIMPSTransactionAsyncTask(mActivity, miniStatementModel).execute();
                } else {
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.error_check_internet), Toast.LENGTH_SHORT).show();

                }
            });

            viewHolder.btnRegComplaint.setOnClickListener(view -> {
                Intent intent = new Intent(mActivity, RegisterComplaintsActivity.class);
                intent.putExtra("dispositionList", (Serializable) dispositionRequestModelList);
                intent.putExtra("bbpsTransactionModel", miniStatementModel);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(intent);

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout linearBenUPIId, benNameLinearId, linearBillerId;// implements View.OnClickListener {

        public TextView txtLogTimeId, txtRrnId, txtBenAcnoId, txtBenIfscId, txtBenMmidId, txtBenNameId,
                txtRemAcnoId, txtRemNameId, txtChannel_ref_noId, txtAmountId, txtBenMobNumberId, txtBillerId,
                btnComplaintEnquiry, txtTransTypeId;
        public Button btnRegBBPSEnquiry, btnRegComplaint;
        private LinearLayout linearBenMMIDId, linearBenIFSCId, linearBenAccNoId, linearBenMobileNo;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtLogTimeId = itemLayoutView.findViewById(R.id.txtLogTimeId);
            txtRrnId = itemLayoutView.findViewById(R.id.txtRrnId);
            txtChannel_ref_noId = itemLayoutView.findViewById(R.id.txtChannel_ref_noId);
            txtBenAcnoId = itemLayoutView.findViewById(R.id.txtBenAcnoId);
            txtBenIfscId = itemLayoutView.findViewById(R.id.txtBenIfscId);
            txtBenMmidId = itemLayoutView.findViewById(R.id.txtBenMmidId);
            txtBenNameId = itemLayoutView.findViewById(R.id.txtBenNameId);
            txtRemAcnoId = itemLayoutView.findViewById(R.id.txtRemAcnoId);
            txtRemNameId = itemLayoutView.findViewById(R.id.txtRemNameId);
            btnRegBBPSEnquiry = itemLayoutView.findViewById(R.id.btnRegBBPSEnquiry);
            txtBenMobNumberId = itemLayoutView.findViewById(R.id.txtBenMobNumberId);

            txtAmountId = itemLayoutView.findViewById(R.id.txtAmountId);
            linearBenMMIDId = itemLayoutView.findViewById(R.id.linearBenMMIDId);
            linearBenIFSCId = itemLayoutView.findViewById(R.id.linearBenIFSCId);
            linearBenAccNoId = itemLayoutView.findViewById(R.id.linearBenAccNoId);
            linearBenMobileNo = itemLayoutView.findViewById(R.id.linearBenMobileNo);

            linearBenUPIId = itemLayoutView.findViewById(R.id.linearBenUPIId);

            benNameLinearId = itemLayoutView.findViewById(R.id.benNameLinearId);
            linearBillerId = itemLayoutView.findViewById(R.id.linearBillerId);
            txtBillerId = itemLayoutView.findViewById(R.id.txtBillerId);
            btnRegComplaint = itemLayoutView.findViewById(R.id.btnRegComplaint);
            txtTransTypeId = itemLayoutView.findViewById(R.id.txtTransTypeId);

        }
    }

    @Override
    public int getItemCount() {
        return bbpsTransactionRequestModelList.size();
    }


    //check validation api call.
    @SuppressLint("StaticFieldLeak")
    private class VerifyIMPSTransactionAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        ProgressDialog pDialog;
        String response, finalResponse;
        String result;
        private String errorCode;
        private BbpsTransactionRequestModel miniStatementModel;
        private TMessage msg;
        private String TranAmount, TranType, ImpsMessage, benname, BeneMobileNo, BeneMMID, TrandateTime, BeneAccNo, IFSCCode;

        public VerifyIMPSTransactionAsyncTask(Context ctx, BbpsTransactionRequestModel miniStatementModel) {
            this.ctx = ctx;
            this.miniStatementModel = miniStatementModel;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(mActivity);
            pDialog.setMessage(mActivity.getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {

            try {

                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(mActivity, jsonString,
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


                    String a = "{}";
                    String base64Data = Base64.encodeToString(a.toString().getBytes(), Base64.NO_WRAP);

                    //YB311012SNKCKJXMYC6N
                    msg = msgDto.CheckComplaintsStatusDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                            AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no(), miniStatementModel.getRrn(), base64Data, miniStatementModel.getRemMobile(),
                            miniStatementModel.getBbpsBillerId(), miniStatementModel.getRemAcno(),
                            miniStatementModel.getRemName(), miniStatementModel.getAmount(),
                            ""/*mMmid*/); //miniStatementModel.getChannelRefNo()
                    Log.d("msg.GetXml():", msg.GetXml());


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

                        if (responseMsg.ActCode.Value.equals("000")) {
                            finalResponse = responseMsg.ActCodeDesc.Value;

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
                    if (TrustMethods.isSessionExpired(errorCode)) {

                        sessionErrorMessageListener.onSessionError(mActivity.getResources().getString(R.string.error_session_expire));
                    } else {
                        Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                    }

                } else {

                    alertDialogOk(mActivity, "Status!!!", finalResponse);

                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void alertDialogOk(Context context, String title, String message) {

        try {
//            final AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.customDialogue);
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setCancelable(true);
            alert.setTitle(title);
            alert.setMessage(message);
            alert.setPositiveButton("OK",
                    (dialog, arg1) -> {
                    });

            final AlertDialog alertDialog = alert.create();
            Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.dialogTheme;
            alertDialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
