package com.trustbank.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.trustbank.Model.FAQModel;
import com.trustbank.R;
import com.trustbank.adapter.FAQExpandableListAdapter;
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
import java.util.HashMap;
import java.util.List;


public class FAQActivity extends AppCompatActivity implements AlertDialogOkListener {

    private ExpandableListView lv_rd_recovery_list;
    private List<String> listDataHeader;
    private HashMap<String, List<FAQModel>> listDataChild;
    private FAQExpandableListAdapter listAdapter;
    private int lastExpandPosition = -1;
    private String errorCode = "";
    AlertDialogOkListener alertDialogOkListener = this;
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
                        TrustMethods.naviagteToSplashScreen(FAQActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(FAQActivity.this, false);
        setContentView(R.layout.activity_frm_faq);
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
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            lv_rd_recovery_list = findViewById(R.id.lv_dcb_headwise);
            method = new TrustMethods(FAQActivity.this);
            if (NetworkUtil.getConnectivityStatus(FAQActivity.this)) {
                new LoadContactAddressAsyncTask(FAQActivity.this).execute();

            } else {
                TrustMethods.message(FAQActivity.this, getResources().getString(R.string.error_check_internet));
            }
            lv_rd_recovery_list.setOnGroupExpandListener(groupPosition -> {
                if (lastExpandPosition != -1 && groupPosition != lastExpandPosition) {
                    lv_rd_recovery_list.collapseGroup(lastExpandPosition);
                }
                lastExpandPosition = groupPosition;
            });
        }catch (Exception e){
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
    private class LoadContactAddressAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;


        public LoadContactAddressAsyncTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FAQActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.FAQUrl();
                if (!url.equals("")) {
                    response = HttpClientWrapper.getResponceDirectalyGET(url, AppConstants.getAuth_token());

                }
                if (response == null || response.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResult = (new JSONObject(response));


                if (jsonResult.has("error")) {
                    error = jsonResult.getString("error");
                    return error;
                }

                JSONObject faqObject = new JSONObject(response);
                String responseCode = faqObject.has("response_code") ? faqObject.getString("response_code") : "NA";

                if (faqObject.has("error")) {
                    error = faqObject.getString("error");
                    return error;
                }

                parseFaqDetails(faqObject);

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
                        AlertDialogMethod.alertDialogOk(FAQActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else if (TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(FAQActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.message(FAQActivity.this, this.error);
                    }
                } else {
                    if (listDataHeader != null && listDataChild != null) {
                        listAdapter = new FAQExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild);
                        lv_rd_recovery_list.setAdapter(listAdapter);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void parseFaqDetails(JSONObject jObj) {

        try {
            listDataHeader = new ArrayList<>();
            listDataChild = new HashMap<>();
            List<FAQModel> rdChildArrayList;

            JSONArray jsonArray = jObj.getJSONArray("question_list");
            for (int i = 0; i < jsonArray.length(); i++) {

                rdChildArrayList = new ArrayList<>();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String question = jsonObject.has("Question") ? jsonObject.getString("Question") : "";
                String answer = jsonObject.has("answer") ? jsonObject.getString("answer") : "";

                FAQModel faqModel = new FAQModel();
                faqModel.setQuestions(question);
                faqModel.setAnswers(answer);
                rdChildArrayList.add(faqModel);
                listDataHeader.add(faqModel.getQuestions());
                listDataChild.put(faqModel.getQuestions(), rdChildArrayList);
            }


        } catch (Exception e) {
            e.printStackTrace();
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
        TrustMethods.showBackButtonAlert(FAQActivity.this);
    }
}
