package com.trustbank.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.trustbank.Model.AccountDetailsModel;
import com.trustbank.Model.BBPSCategoryModel;
import com.trustbank.Model.BBPSCoverageModel;
import com.trustbank.Model.BBPSSearchBillerModel;
import com.trustbank.Model.GenerateStanRRNModel;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BBPSBillerCategoryActivity extends AppCompatActivity implements AlertDialogOkListener {

    Spinner sp_biiler_category, sp_coveragespinner;
    Button search;
    EditText et_biller_name, et_biller_id;
    CoordinatorLayout searchbillerCoordinatorLayoutId;
    private String selectedBillerCategory;
    AlertDialogOkListener alertDialogOkListener = this;
    private TrustMethods method;
    private LinearLayout coverageLLId, linearBillerNameId, linearBillerIdId;

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
                        TrustMethods.naviagteToSplashScreen(BBPSBillerCategoryActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(BBPSBillerCategoryActivity.this, false);
        setContentView(R.layout.activity_b_b_p_s_biller_category);

        initCompnonet();

    }

    private void initCompnonet() {

        try {

            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            method = new TrustMethods(BBPSBillerCategoryActivity.this);
            sp_biiler_category = findViewById(R.id.sp_biiler_category);
            sp_coveragespinner = findViewById(R.id.sp_coveragespinner);
            searchbillerCoordinatorLayoutId = findViewById(R.id.searchbillerCoordinatorLayoutId);
            search = findViewById(R.id.btn_search);
            et_biller_name = findViewById(R.id.et_biller_name);
            linearBillerNameId = findViewById(R.id.linearBillerNameId);

            et_biller_id = findViewById(R.id.et_biller_id);
            linearBillerIdId = findViewById(R.id.linearBillerIdId);
            coverageLLId = findViewById(R.id.coverageLLId);

            coverageLLId.setVisibility(View.GONE);
            linearBillerNameId.setVisibility(View.GONE);
            linearBillerIdId.setVisibility(View.GONE);
            search.setVisibility(View.GONE);
            // search.setOnClickListener(this);


            if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(BBPSBillerCategoryActivity.this)) {
                if (NetworkUtil.getConnectivityStatus(BBPSBillerCategoryActivity.this)) {
                    new FetchBillerCategoryAsyncTask(BBPSBillerCategoryActivity.this).execute();
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), searchbillerCoordinatorLayoutId);
                }
            } else {
                TrustMethods.displaySimErrorDialog(BBPSBillerCategoryActivity.this);
            }

            search.setOnClickListener(view -> {

                String billerName = et_biller_name.getText().toString().trim();
                String billerId = et_biller_id.getText().toString().trim();
                String categrory = sp_biiler_category.getSelectedItem().toString();
                String coverage = sp_coveragespinner.getSelectedItem().toString();

                if (categrory.equalsIgnoreCase("Select Biller Category")) {
                    TrustMethods.showSnackBarMessage("Please select biller category", searchbillerCoordinatorLayoutId);
                } else if (coverage.equalsIgnoreCase("Select Coverage")) {
                    TrustMethods.showSnackBarMessage("Please select Coverage", searchbillerCoordinatorLayoutId);
                } else if (TextUtils.isEmpty(billerName)) {
                    TrustMethods.showSnackBarMessage("Please Enter biller name", searchbillerCoordinatorLayoutId);
                } else if (TextUtils.isEmpty(billerId)) {
                    TrustMethods.showSnackBarMessage("Please Enter biller id", searchbillerCoordinatorLayoutId);
                } else {

                    if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(BBPSBillerCategoryActivity.this)) {
                        if (NetworkUtil.getConnectivityStatus(BBPSBillerCategoryActivity.this)) {
                            TrustMethods.hideSoftKeyboard(this);
                            new SearchBillerCategoryAsyncTask(BBPSBillerCategoryActivity.this,
                                    coverage, categrory, billerName, billerId).execute();
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), searchbillerCoordinatorLayoutId);
                        }
                    } else {
                        TrustMethods.displaySimErrorDialog(BBPSBillerCategoryActivity.this);
                    }
                }


            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 0) {
            Intent intent = new Intent(getApplicationContext(), LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            method.activityCloseAnimation();
        }
    }

    private class FetchBillerCategoryAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String actionName = "BBPS_BILLER_CATEGORY";
        String result;
        List<BBPSCategoryModel> bbpsCategoryModelList = null;
        ArrayList<AccountDetailsModel> accountDetailsModelArrayList = new ArrayList<>();
        private String errorCode;

        public FetchBillerCategoryAsyncTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BBPSBillerCategoryActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.MobileNoVerifyUrl();

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

                    bbpsCategoryModelList = new ArrayList<>();
                    JSONObject jsonResponseObj = jsonResponse.getJSONObject("response");
                    JSONArray jsonArrayCategory = jsonResponseObj.getJSONArray("biiler_category_list");

                    for (int i = 0; i < jsonArrayCategory.length(); i++) {
                        JSONObject jsonObject = jsonArrayCategory.getJSONObject(i);
                        BBPSCategoryModel bbpsCategoryModel = new BBPSCategoryModel();
                        bbpsCategoryModel.setBillerCategory(jsonObject.getString("category"));
                        bbpsCategoryModelList.add(bbpsCategoryModel);
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
                        AlertDialogMethod.alertDialogOk(BBPSBillerCategoryActivity.this,
                                getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok),
                                0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, searchbillerCoordinatorLayoutId);
                    }
                } else {

                    if (bbpsCategoryModelList != null && bbpsCategoryModelList.size() != 0) {

                        List<String> biller_category = new ArrayList<>();
                        biller_category.add("Select Biller Category");
                        for (BBPSCategoryModel bbpsCategoryModel : bbpsCategoryModelList) {
                            biller_category.add(bbpsCategoryModel.getBillerCategory());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(BBPSBillerCategoryActivity.this, android.R.layout.simple_spinner_item, biller_category);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_biiler_category.setAdapter(adapter);
                        setSelectCtedBillerCategorySpinner(bbpsCategoryModelList);
                    } else {
                        TrustMethods.showSnackBarMessage("Category Not Found", searchbillerCoordinatorLayoutId);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setSelectCtedBillerCategorySpinner(List<BBPSCategoryModel> bbpsCategoryModelList) {
        try {
            sp_biiler_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                    try {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                        selectedBillerCategory = (String) adapterView.getItemAtPosition(pos);
                        if (selectedBillerCategory.equalsIgnoreCase("Select Biller Category")) {
                            coverageLLId.setVisibility(View.GONE);
                            linearBillerNameId.setVisibility(View.GONE);
                            linearBillerIdId.setVisibility(View.GONE);
                            search.setVisibility(View.GONE);
                            et_biller_id.setText("");
                            et_biller_name.setText("");
                        } else {
                            coverageLLId.setVisibility(View.VISIBLE);
                            linearBillerNameId.setVisibility(View.GONE);
                            linearBillerIdId.setVisibility(View.GONE);
                            search.setVisibility(View.GONE);
                            loadCovergae(selectedBillerCategory);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            sp_coveragespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                    try {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                        String coverage = (String) adapterView.getItemAtPosition(pos);
                        if (coverage.equalsIgnoreCase("Select Coverage")) {
                            linearBillerNameId.setVisibility(View.GONE);
                            linearBillerIdId.setVisibility(View.GONE);
                            search.setVisibility(View.GONE);
                            et_biller_id.setText("");
                            et_biller_name.setText("");
                        } else {
                            linearBillerNameId.setVisibility(View.VISIBLE);
                            linearBillerIdId.setVisibility(View.VISIBLE);
                            search.setVisibility(View.VISIBLE);
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

    private void loadCovergae(String selectedBillerCategory) {
        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(BBPSBillerCategoryActivity.this)) {
            if (NetworkUtil.getConnectivityStatus(BBPSBillerCategoryActivity.this)) {
                new FetchCovergaeAsyncTask(BBPSBillerCategoryActivity.this, selectedBillerCategory).execute();
            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), searchbillerCoordinatorLayoutId);
            }
        } else {
            TrustMethods.displaySimErrorDialog(BBPSBillerCategoryActivity.this);
        }
    }

    private class FetchCovergaeAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String actionName = "BBPS_BILLER_COVERAGE";
        String result;
        String category;
        List<BBPSCoverageModel> bbpsCoverageModelList = null;
        private String errorCode;

        public FetchCovergaeAsyncTask(Context ctx, String category) {
            this.ctx = ctx;
            this.category = category;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BBPSBillerCategoryActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.getCovergaeDetails(category);

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

                    bbpsCoverageModelList = new ArrayList<>();

                    JSONObject jsonResponseObj = jsonResponse.getJSONObject("response");

                    JSONArray jsonArrayCategory = jsonResponseObj.getJSONArray("biiler_coverage_list");

                    for (int i = 0; i < jsonArrayCategory.length(); i++) {
                        JSONObject jsonObject = jsonArrayCategory.getJSONObject(i);
                        BBPSCoverageModel bbpsCovergaeModel = new BBPSCoverageModel();
                        bbpsCovergaeModel.setBillerCovergae(jsonObject.getString("coverage"));
                        bbpsCoverageModelList.add(bbpsCovergaeModel);
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
                        AlertDialogMethod.alertDialogOk(BBPSBillerCategoryActivity.this,
                                getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok),
                                0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, searchbillerCoordinatorLayoutId);
                    }
                } else {

                    if (bbpsCoverageModelList != null && bbpsCoverageModelList.size() != 0) {

                        List<String> biller_coverage = new ArrayList<>();
                        biller_coverage.add("Select Coverage");
                        for (BBPSCoverageModel bbpsCoverageModel : bbpsCoverageModelList) {
                            biller_coverage.add(bbpsCoverageModel.getBillerCovergae());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(BBPSBillerCategoryActivity.this, android.R.layout.simple_spinner_item, biller_coverage);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_coveragespinner.setAdapter(adapter);
                        setSelectCtedBillerCoverageSpinner(bbpsCoverageModelList);
                    } else {
                        TrustMethods.showSnackBarMessage("Category Not Found", searchbillerCoordinatorLayoutId);
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setSelectCtedBillerCoverageSpinner(List<BBPSCoverageModel> bbpsCoverageModelList) {


    }


    private class SearchBillerCategoryAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String actionName = "BBPS_SEARCH_BILLER";
        String result;
        List<BBPSSearchBillerModel> bbpsSearchModelList = null;
        ArrayList<AccountDetailsModel> accountDetailsModelArrayList = new ArrayList<>();
        private String errorCode;
        private String coverage, category, billerName, billerId;

        public SearchBillerCategoryAsyncTask(Context ctx, String coverage, String category,
                                             String billerName, String billerId) {
            this.ctx = ctx;
            this.coverage = coverage;
            this.category = category;
            this.billerName = billerName;
            this.billerId = billerId;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BBPSBillerCategoryActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                String url = TrustURL.getSearchBillerDetails(coverage, category, billerName, billerId);

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

                    bbpsSearchModelList = new ArrayList<>();

                    JSONObject jsonResponseObj = jsonResponse.getJSONObject("response");

                    JSONArray jsonArrayCategory = jsonResponseObj.getJSONArray("biiler");

                    if (jsonArrayCategory != null && jsonArrayCategory.length() != 0) {
                        for (int i = 0; i < jsonArrayCategory.length(); i++) {

                            JSONObject jsonObject = jsonArrayCategory.getJSONObject(i);
                            BBPSSearchBillerModel bbpsCategoryModel = new BBPSSearchBillerModel();
                            String billerId = jsonObject.has("billerId") ? jsonObject.getString("billerId") : "";
                            String billerName = jsonObject.has("billerName") ? jsonObject.getString("billerName") : "";
                            String billerAliasName = jsonObject.has("billerAliasName") ? jsonObject.getString("billerAliasName") : "";
                            String billerCategoryName = jsonObject.has("billerCategoryName") ? jsonObject.getString("billerCategoryName") : "";
                            String billerMode = jsonObject.has("billerMode") ? jsonObject.getString("billerMode") : "";
                            String billerAcceptsAdhoc = jsonObject.has("billerAcceptsAdhoc") ? jsonObject.getString("billerAcceptsAdhoc") : "";
                            String parentBiller = jsonObject.has("parentBiller") ? jsonObject.getString("parentBiller") : "";
                            String parentBillerId = jsonObject.has("parentBillerId") ? jsonObject.getString("parentBillerId") : "";
                            String billerCoverage = jsonObject.has("billerCoverage") ? jsonObject.getString("billerCoverage") : "";
                            String fetchRequirement = jsonObject.has("fetchRequirement") ? jsonObject.getString("fetchRequirement") : "";
                            String paymentAmountExactness = jsonObject.has("paymentAmountExactness") ? jsonObject.getString("paymentAmountExactness") : "";
                            String supportBillValidation = jsonObject.has("supportBillValidation") ? jsonObject.getString("supportBillValidation") : "";
                            String billerEffctvFrom = jsonObject.has("billerEffctvFrom") ? jsonObject.getString("billerEffctvFrom") : "";
                            String status = jsonObject.has("status") ? jsonObject.getString("status") : "";

                            bbpsCategoryModel.setBillerId(billerId);
                            bbpsCategoryModel.setBillerName(billerName);
                            bbpsCategoryModel.setBillerAliasName(billerAliasName);
                            bbpsCategoryModel.setBillerCategoryName(billerCategoryName);
                            bbpsCategoryModel.setBillerMode(billerMode);
                            bbpsCategoryModel.setBillerAcceptsAdhoc(billerAcceptsAdhoc);
                            bbpsCategoryModel.setParentBiller(parentBiller);
                            bbpsCategoryModel.setParentBillerId(parentBillerId);
                            bbpsCategoryModel.setBillerCoverage(billerCoverage);
                            bbpsCategoryModel.setFetchRequirement(fetchRequirement);
                            bbpsCategoryModel.setPaymentAmountExactness(paymentAmountExactness);
                            bbpsCategoryModel.setSupportBillValidation(supportBillValidation);
                            bbpsCategoryModel.setBillerEffctvFrom(billerEffctvFrom);
                            bbpsCategoryModel.setStatus(status);
                            bbpsSearchModelList.add(bbpsCategoryModel);
                        }
                    } else {
                        error = "No Records Found.";
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
                        AlertDialogMethod.alertDialogOk(BBPSBillerCategoryActivity.this,
                                getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok),
                                0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, searchbillerCoordinatorLayoutId);
                    }

                } else {

                    if (bbpsSearchModelList != null && bbpsSearchModelList.size() != 0) {


                        Intent intent = new Intent(BBPSBillerCategoryActivity.this, BBPSBillerSearchListActivity.class);
                        intent.putExtra("billerSearchList", (Serializable) bbpsSearchModelList);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //trustMethods.activityOpenAnimation();
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
        TrustMethods.showBackButtonAlert(BBPSBillerCategoryActivity.this);
    }
}