package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.R;
import com.trustbank.adapter.BeneficiaryAdapter;
import com.trustbank.interfaces.AddBeneficiaryFragmentListener;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.interfaces.GetBeneficiaryListInterface;
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
import java.util.Locale;

public class ManageBeneficiaryActivity extends AppCompatActivity implements View.OnClickListener, GetBeneficiaryListInterface, AlertDialogOkListener, AddBeneficiaryFragmentListener {

    private CoordinatorLayout coordinatorLayout;
    private TrustMethods method;
    private RecyclerView receyclerBeneficieryList;
    private EditText searchBenf;
    private BeneficiaryAdapter beneficiaryAdapter;
    AlertDialogOkListener alertDialogOkListener = this;


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
                        TrustMethods.naviagteToSplashScreen(ManageBeneficiaryActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(ManageBeneficiaryActivity.this, false);
        setContentView(R.layout.activity_manage_beneficiary);
        inIt();
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
    private void inIt() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        method = new TrustMethods(ManageBeneficiaryActivity.this);
        FloatingActionButton addBeneficiary = findViewById(R.id.addBeneficiaryId);
        searchBenf = findViewById(R.id.searchBenfId);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        receyclerBeneficieryList = findViewById(R.id.receyclerBeneficieryList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        receyclerBeneficieryList.setLayoutManager(mLayoutManager);

        addBeneficiary.setOnClickListener(this);
        setBeneficiaryList();
        searchFunctionality();
    }

    private void searchFunctionality() {
        searchBenf.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (beneficiaryAdapter != null) {
                    beneficiaryAdapter.filter(searchBenf.getText().toString().toLowerCase(Locale.getDefault()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
    }

    public void setBeneficiaryList() {
        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
            if (NetworkUtil.getConnectivityStatus(ManageBeneficiaryActivity.this)) {
                new GetBanaficiaryAsyncTask(ManageBeneficiaryActivity.this, AppConstants.getUSERMOBILENUMBER()).execute();
            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
            }
        } else {
            TrustMethods.displaySimErrorDialog(this);
        }
    }

    //Interface method to call Remove Beneficiary API from adapter......
    @Override
    public void GetBeneficiaryClick(String benfId) {
        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
            if (NetworkUtil.getConnectivityStatus(getApplicationContext())) {
                new RemoveBeneficiaryAsyncTask(ManageBeneficiaryActivity.this, benfId).execute();
            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
            }
        } else {
            TrustMethods.displaySimErrorDialog(this);
        }
    }

    @Override
    public void onDialogOk(int resultCode) {
        switch (resultCode) {
            case 0:
                setBeneficiaryList();
                break;
            case 1:
                Intent intent = new Intent(getApplicationContext(), LockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                method.activityCloseAnimation();
                break;
            case 2:
                Intent fundIntent = new Intent(getApplicationContext(), FundsTransferMenu.class);
                fundIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(fundIntent);
                method.activityCloseAnimation();
                break;
        }
    }

    //Interface method to call Add Beneficiary API from Dialog Fragment......
    @Override
    public void addBenficiaryClick() {
        setBeneficiaryList();
    }

    @SuppressLint("StaticFieldLeak")
    public class GetBanaficiaryAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        String result;
        String mobileNumber;
        ProgressDialog pDialog;
        String actionName = "LIST_BENEFICIARY";
        ArrayList<BeneficiaryModal> beneficiaryList;
        private String errorCode;

        public GetBanaficiaryAsyncTask(Context ctx, String mobileNumber) {
            this.ctx = ctx;
            this.mobileNumber = mobileNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ManageBeneficiaryActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
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
                            String upi_id = dataJsonObject.has("ben_upi_id") ? dataJsonObject.getString("ben_upi_id") : "NA";

                            BeneficiaryModal beneficiaryModal = new BeneficiaryModal();
                            beneficiaryModal.setBenId(benId);
                            beneficiaryModal.setBenType(benType);
                            beneficiaryModal.setBenNickname(benName);
                            beneficiaryModal.setBanAccName(benAccName);
                            beneficiaryModal.setBenAccNo(benAccNo);
                            beneficiaryModal.setBenIfscCode(benIfscCode);
                            beneficiaryModal.setBenMobNo(benMobNo);
                            beneficiaryModal.setBenMmid(benMmid);
                            beneficiaryModal.setBenUpiId(upi_id);

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
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            try {
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(ManageBeneficiaryActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(ManageBeneficiaryActivity.this, this.error, "", getResources().getString(R.string.btn_ok), 2, false, alertDialogOkListener);
                    }
                } else {
                    if (beneficiaryList != null && beneficiaryList.size() != 0) {
                        beneficiaryAdapter = new BeneficiaryAdapter(ManageBeneficiaryActivity.this, beneficiaryList);
                        receyclerBeneficieryList.setAdapter(beneficiaryAdapter);
                        beneficiaryAdapter.notifyDataSetChanged();
                    } else {
                        if (beneficiaryAdapter != null) {
                            receyclerBeneficieryList.setAdapter(null);
                            beneficiaryAdapter.notifyDataSetChanged();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @SuppressLint("StaticFieldLeak")
    private class RemoveBeneficiaryAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String mBenfId;
        String actionName = "REMOVE_BENEFICIARY";
        String result;
        private String errorCode;

        public RemoveBeneficiaryAsyncTask(Context ctx, String benfId) {
            this.ctx = ctx;
            this.mBenfId = benfId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ManageBeneficiaryActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();

                String jsonString = "{\"ben_id\":\"" + mBenfId + "\"}";
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
                    response = getResources().getString(R.string.error_benef_remove);

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
                        AlertDialogMethod.alertDialogOk(ManageBeneficiaryActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                } else {
                    if (response != null) {
                        AlertDialogMethod.alertDialogOk(ManageBeneficiaryActivity.this, "", getResources().getString(R.string.error_benef_remove), getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addBeneficiaryId:
                Intent intent = new Intent(getApplicationContext(), BeneficiaryTypeListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                method.activityOpenAnimation();

                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent fundIntent = new Intent(getApplicationContext(), FundsTransferMenu.class);
                fundIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(fundIntent);
                method.activityCloseAnimation();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(ManageBeneficiaryActivity.this);
    }
}