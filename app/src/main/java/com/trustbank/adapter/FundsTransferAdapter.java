package com.trustbank.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.Module.ImageTextMenuModel;
import com.trustbank.R;
import com.trustbank.activity.CreateQRUPIActivity;
import com.trustbank.activity.GetQRUPIActivity;
import com.trustbank.activity.IMPSTransferToAccount;
import com.trustbank.activity.IMPSTransferToMobile;
import com.trustbank.activity.LockActivity;
import com.trustbank.activity.ManageBeneficiaryActivity;
import com.trustbank.activity.MenuActivity;
import com.trustbank.activity.NEFTTransferToAccount;
import com.trustbank.activity.SelfTransferToAccountActivity;
import com.trustbank.activity.UPICollectActivity;
import com.trustbank.activity.UPIToUPITransactions;
import com.trustbank.activity.WithinBankActivity;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FundsTransferAdapter extends RecyclerView.Adapter<FundsTransferAdapter.ViewHolder> implements AlertDialogOkListener {
    private static final String TAG = FundsTransferAdapter.class.getSimpleName();
    private Activity mActivity;
    private TrustMethods trustMethods;
    private ImageTextMenuModel[] accountItemsData;
    private ArrayList<BeneficiaryModal> beneficiaryList;
    private AlertDialogOkListener alertDialogOkListener = this;
    public CoordinatorLayout dataLayout;

    public FundsTransferAdapter(FragmentActivity activity, ImageTextMenuModel[] accountItemsData, ArrayList<BeneficiaryModal> beneficiaryList) {
        this.mActivity = activity;
        this.accountItemsData = accountItemsData;
        this.beneficiaryList = beneficiaryList;
        trustMethods = new TrustMethods(mActivity);

        if ((!TextUtils.isEmpty(AppConstants.getPlay_store_validate())) && AppConstants.getPlay_store_validate().equalsIgnoreCase("1")) {
            //
        } else if (AppConstants.getUSERMOBILENUMBER().equalsIgnoreCase(AppConstants.getPlayStoreDemoUserMobile()) &&
                AppConstants.getCLIENTID().equalsIgnoreCase(AppConstants.getPlayStoreDemoPasswordClientid())) {

        } else {
           /* if (TrustMethods.isSimAvailable(mActivity.getApplicationContext()) && TrustMethods.isSimVerified(activity)) {
                if (NetworkUtil.getConnectivityStatus(mActivity)) {
                    if (!TextUtils.isEmpty(AppConstants.getAuth_token())) {
                        new GetBanaficiaryAsyncTask(mActivity, AppConstants.getUSERMOBILENUMBER()).execute();
                    } else {
                        AlertDialogMethod.alertDialogOk(mActivity, mActivity.getResources().getString(R.string.error_session_expire),
                                "", mActivity.getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    }
                } else {
                    TrustMethods.showSnackBarMessage(mActivity.getResources().getString(R.string.error_check_internet), dataLayout);
                }
            } else {
                TrustMethods.displaySimErrorDialog(activity);
            }*/
        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_accounts, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.accountMenuName.setText(accountItemsData[position].getItemName());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView accountMenuName;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            dataLayout = itemLayoutView.findViewById(R.id.dataLayoutId);
            accountMenuName = itemLayoutView.findViewById(R.id.accountMenuNameId);

        }

        @Override
        public void onClick(View view) {
            switch (accountItemsData[getAdapterPosition()].getItemName().trim()) {

                case "Within Bank Transfer":
                    Intent intentWithinBank = new Intent(view.getContext(), WithinBankActivity.class);
                    intentWithinBank.putExtra("beneficiaryList", beneficiaryList);
                    intentWithinBank.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentWithinBank);
                    trustMethods.activityOpenAnimation();
                    break;

                case "IMPS Transfer To Mobile Phone":
                    Intent intentImpsToMobile = new Intent(view.getContext(), IMPSTransferToMobile.class);
                    intentImpsToMobile.putExtra("beneficiaryList", beneficiaryList);
                    intentImpsToMobile.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentImpsToMobile);
                    trustMethods.activityOpenAnimation();
                    break;

                case "IMPS Transfer To Account":
                    Intent intentImpsToAccountNo = new Intent(view.getContext(), IMPSTransferToAccount.class);
                    intentImpsToAccountNo.putExtra("beneficiaryList", beneficiaryList);
                    intentImpsToAccountNo.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentImpsToAccountNo);
                    trustMethods.activityOpenAnimation();
                    break;

                case "NEFT Transfer To Account":
                    Intent intentNEFTTransferToAccount = new Intent(view.getContext(), NEFTTransferToAccount.class);
                    intentNEFTTransferToAccount.putExtra("beneficiaryList", beneficiaryList);
                    intentNEFTTransferToAccount.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentNEFTTransferToAccount);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Manage Beneficiaries":
                    Intent intentBeneficiaries = new Intent(view.getContext(), ManageBeneficiaryActivity.class);
                    intentBeneficiaries.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentBeneficiaries);
                    trustMethods.activityOpenAnimation();
                    mActivity.finish();
                    break;
                case "Self Transfer To Account":
                    Intent intentSelfTransferToAccount = new Intent(view.getContext(), SelfTransferToAccountActivity.class);
                    intentSelfTransferToAccount.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentSelfTransferToAccount);
                    trustMethods.activityOpenAnimation();
                    break;

                case "UPI":
                    Intent intentUPITransactions = new Intent(view.getContext(), UPIToUPITransactions.class);
                    intentUPITransactions.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentUPITransactions.putExtra("beneficiaryList", beneficiaryList);
                    view.getContext().startActivity(intentUPITransactions);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Collect Money":
                    Intent intentUPICollectMoneyTransactions = new Intent(view.getContext(), UPICollectActivity.class);
                    intentUPICollectMoneyTransactions.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentUPICollectMoneyTransactions.putExtra("beneficiaryList", beneficiaryList);
                    view.getContext().startActivity(intentUPICollectMoneyTransactions);
                    trustMethods.activityOpenAnimation();
                    break;
                case "Create New QR Code":
                    Intent intentUPIGenearteBarcode = new Intent(view.getContext(), CreateQRUPIActivity.class);
                    intentUPIGenearteBarcode.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentUPIGenearteBarcode.putExtra("beneficiaryList", beneficiaryList);
                    view.getContext().startActivity(intentUPIGenearteBarcode);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Display QR Code":
                    Intent intentUPIRetrieveBarcode = new Intent(view.getContext(), GetQRUPIActivity.class);
                    intentUPIRetrieveBarcode.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentUPIRetrieveBarcode.putExtra("beneficiaryList", beneficiaryList);
                    view.getContext().startActivity(intentUPIRetrieveBarcode);
                    trustMethods.activityOpenAnimation();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return accountItemsData.length;
    }

    @SuppressLint("StaticFieldLeak")
    public class GetBanaficiaryAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        String mobileNumber;
        String result;
        ProgressDialog pDialog;
        String actionName = "LIST_BENEFICIARY";
        private String errorCode;

        public GetBanaficiaryAsyncTask(Context ctx, String mobileNumber) {
            this.ctx = ctx;
            this.mobileNumber = mobileNumber;
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

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();

                String jsonString = "{\"mobile_number\":\"" + mobileNumber + "\"}";
                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);

                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonString, actionName, AppConstants.getAuth_token());
                }
                //  AppConstants.setAuth_token("");
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
                    JSONArray jsonArray = responseJsonObject.getJSONArray("ben_list");
                    beneficiaryList = new ArrayList<>();

                    beneficiaryList.clear();

                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject dataJsonObject = jsonArray.getJSONObject(i);
                            String benId = dataJsonObject.has("benid") ? dataJsonObject.getString("benid") : "NA";
                            String benType = dataJsonObject.has("ben_type") ? dataJsonObject.getString("ben_type") : "NA";
                            String benName = dataJsonObject.has("ben_nickname") ? dataJsonObject.getString("ben_nickname") : "NA";
                            String benAccName = dataJsonObject.has("ben_ac_name") ? dataJsonObject.getString("ben_ac_name") : "NA";
                            String benAccNo = dataJsonObject.has("ben_ac_no") ? dataJsonObject.getString("ben_ac_no") : "NA";
                            String benIfscCode = dataJsonObject.has("ben_ifsc") ? dataJsonObject.getString("ben_ifsc") : "NA";
                            String benMobNo = dataJsonObject.has("ben_mobile_number") ? dataJsonObject.getString("ben_mobile_number") : "NA";
                            String benMmid = dataJsonObject.has("ben_mmid") ? dataJsonObject.getString("ben_mmid") : "NA";
                            String benUpiId = dataJsonObject.has("ben_upi_id") ? dataJsonObject.getString("ben_upi_id") : "NA";

                            BeneficiaryModal beneficiaryModal = new BeneficiaryModal();
                            beneficiaryModal.setBenId(benId);
                            beneficiaryModal.setBenType(benType);
                            beneficiaryModal.setBenNickname(benName);
                            beneficiaryModal.setBanAccName(benAccName);
                            beneficiaryModal.setBenAccNo(benAccNo);
                            beneficiaryModal.setBenIfscCode(benIfscCode);
                            beneficiaryModal.setBenMobNo(benMobNo);
                            beneficiaryModal.setBenMmid(benMmid);
                            beneficiaryModal.setBenUpiId(benUpiId);

                            beneficiaryList.add(beneficiaryModal);
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
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(mActivity, mActivity.getResources().getString(R.string.error_session_expire),
                                "", mActivity.getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(mActivity, this.error, "", mActivity.getResources().getString(R.string.btn_ok),
                                1, false, alertDialogOkListener);
                    }
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
                Intent intent = new Intent(mActivity, LockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mActivity.startActivity(intent);
                trustMethods.activityCloseAnimation();
                break;
            case 1:
                Intent intentMenu = new Intent(mActivity, MenuActivity.class);
                intentMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mActivity.startActivity(intentMenu);
                trustMethods.activityCloseAnimation();
                break;
        }
    }
}
