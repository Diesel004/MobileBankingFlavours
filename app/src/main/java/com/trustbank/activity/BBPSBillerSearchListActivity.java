package com.trustbank.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import com.trustbank.Model.BBPSCustomeParamater;
import com.trustbank.Model.BBPSSearchBillerModel;
import com.trustbank.R;
import com.trustbank.adapter.BBPSSearchBillerAdapter;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.interfaces.BBPSSearchBillerInterface;
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

public class BBPSBillerSearchListActivity extends AppCompatActivity implements BBPSSearchBillerInterface, AlertDialogOkListener {

    RecyclerView recyclerBillerSearchId;
    CoordinatorLayout coordinatorLayout;
    List<BBPSCustomeParamater> bbpsCustomeParameter = null;
    private AlertDialogOkListener alertDialogOkListener = this;
    private TrustMethods method;

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
                        TrustMethods.naviagteToSplashScreen(BBPSBillerSearchListActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(BBPSBillerSearchListActivity.this, false);
        setContentView(R.layout.activity_b_b_p_s_biller_search_list);

        initComponet();
    }

    private void initComponet() {

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            method = new TrustMethods(BBPSBillerSearchListActivity.this);
            recyclerBillerSearchId = findViewById(R.id.recyclerBillerSearchId);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            Intent intent = getIntent();
            List<BBPSSearchBillerModel> bbpsSearchModelList = (List<BBPSSearchBillerModel>) intent.getSerializableExtra("billerSearchList");
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerBillerSearchId.setLayoutManager(mLayoutManager);
            BBPSSearchBillerAdapter bbpsSearchBillerAdapter = new BBPSSearchBillerAdapter(BBPSBillerSearchListActivity.this, bbpsSearchModelList);
            recyclerBillerSearchId.setAdapter(bbpsSearchBillerAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void adapterCallEvent(String billerId,String billerName) {
//        TrustMethods.showMessage(this, "billerId " + billerId);

        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(BBPSBillerSearchListActivity.this)) {
            if (NetworkUtil.getConnectivityStatus(BBPSBillerSearchListActivity.this)) {
                new GetBillerDetailsAsyncTask(BBPSBillerSearchListActivity.this, billerId,billerName).execute();
            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
            }
        } else {
            TrustMethods.displaySimErrorDialog(BBPSBillerSearchListActivity.this);
        }
    }

    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 0){
            Intent intent = new Intent(getApplicationContext(), LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            method.activityCloseAnimation();
        }

    }

    private class GetBillerDetailsAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String actionName = "BBPS_GET_BILLER_DETAILS";
        String result;
        String billerId;

        private String errorCode,billerName;

        public GetBillerDetailsAsyncTask(Context ctx, String billerId,String billerName) {
            this.ctx = ctx;
            this.billerId = billerId;
            this.billerName = billerName;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BBPSBillerSearchListActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.getbillerDetails(billerId);

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

                    bbpsCustomeParameter = new ArrayList<>();

                    JSONObject jsonResponseObj = jsonResponse.getJSONObject("response");
                    JSONArray jsonArrayCategory = jsonResponseObj.getJSONArray("custom_params");

                    for (int i = 0; i < jsonArrayCategory.length(); i++) {
                        JSONObject jsonObject = jsonArrayCategory.getJSONObject(i);
                        String paramName = jsonObject.has("paramName") ? jsonObject.getString("paramName") : "";
                        Object dataType = jsonObject.has("dataType") ? jsonObject.getString("dataType") : "";
                        boolean optional = jsonObject.has("optional") && jsonObject.getBoolean("optional");
                        int minLength = jsonObject.has("minLength") ? jsonObject.getInt("minLength") : 1;
                        int maxLength = jsonObject.has("maxLength") ? jsonObject.getInt("maxLength") : 1;
                        String regex = jsonObject.has("regex") ? jsonObject.getString("regex") : "";

                        BBPSCustomeParamater bbpsCustomeParamater = new BBPSCustomeParamater();
                        bbpsCustomeParamater.setParamaName(paramName);
                        bbpsCustomeParamater.setDataType(dataType);
                        bbpsCustomeParamater.setOptional(optional);
                        bbpsCustomeParamater.setMinLenght(minLength);
                        bbpsCustomeParamater.setMaxLenght(maxLength);
                        bbpsCustomeParamater.setRegex(regex);
                        bbpsCustomeParameter.add(bbpsCustomeParamater);


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
                        AlertDialogMethod.alertDialogOk(BBPSBillerSearchListActivity.this,
                                getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok),
                                0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                } else {

                    Intent intent = new Intent(BBPSBillerSearchListActivity.this, BBPSCreateDynamicBillPayActivity.class);
                    intent.putExtra("customParametr", (Serializable) bbpsCustomeParameter);
                    intent.putExtra("billerId", billerId);
                    intent.putExtra("billerName", billerName);
                    startActivity(intent);

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
        TrustMethods.showBackButtonAlert(BBPSBillerSearchListActivity.this);
    }
}