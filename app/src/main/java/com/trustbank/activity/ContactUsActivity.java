package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener, AlertDialogOkListener {
    private String TAG = ContactUsActivity.class.getSimpleName();
    private TrustMethods method;
    private TextView txtHeadOfficeAddr, txtEmailAddr, txtContactNo, txtWebsite;
    private CoordinatorLayout coordinatorLayout;
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
                        TrustMethods.naviagteToSplashScreen(ContactUsActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(ContactUsActivity.this, false);
        setContentView(R.layout.activity_contact_us);
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
        method = new TrustMethods(ContactUsActivity.this);
        txtHeadOfficeAddr = findViewById(R.id.txtHeadOfficeAddrId);
        txtEmailAddr = findViewById(R.id.txtEmailAddrId);
        txtContactNo = findViewById(R.id.txtContactNoId);
        txtWebsite = findViewById(R.id.txtWebsiteId);
        Button btnBranchLocator = findViewById(R.id.btnBranchLocatorId);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        btnBranchLocator.setOnClickListener(this);
        loadData();
    }

    private void loadData() {
        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
            if (NetworkUtil.getConnectivityStatus(ContactUsActivity.this)) {
                new LoadContactAddressAsyncTask(ContactUsActivity.this).execute();
            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
            }
        } else {
            TrustMethods.displaySimErrorDialog(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBranchLocatorId:
                Intent intent = new Intent(ContactUsActivity.this, BranchesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                view.getContext().startActivity(intent);
                method.activityOpenAnimation();
                break;
            default:
                break;
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
        String headOffice;
        String email;
        String telephoneNo;
        String website;
        private String errorCode = "";

        public LoadContactAddressAsyncTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ContactUsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.ContactUsUrl() + "?rnd=" + String.valueOf(Math.random());
                if (!url.equals("")) {
                    TrustMethods.LogMessage(TAG, "URL:-" + url);
                    response = HttpClientWrapper.getResponceDirectalyGET(url, AppConstants.getAuth_token());
                    TrustMethods.LogMessage(TAG, "Frm Enquiry details response-->" + response);
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

                JSONObject contactDetObject = (new JSONObject(jsonResult.getString("contact_details")));
                if (contactDetObject.has("error")) {
                    error = contactDetObject.getString("error");
                    return error;
                }
                headOffice = contactDetObject.has("head_office") ? contactDetObject.getString("head_office") : "NA";
                email = contactDetObject.has("email") ? contactDetObject.getString("email") : "NA";
                telephoneNo = contactDetObject.has("telephone_no") ? contactDetObject.getString("telephone_no") : "NA";
                website = contactDetObject.has("website") ? contactDetObject.getString("website") : "NA";

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
                        AlertDialogMethod.alertDialogOk(ContactUsActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else if (TrustMethods.isSessionExpiredWithString(error)){
                        AlertDialogMethod.alertDialogOk(ContactUsActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    }else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }

                } else {
                    if (!TextUtils.isEmpty(headOffice)) {
                        txtHeadOfficeAddr.setText(headOffice);
                    }
                    if (!TextUtils.isEmpty(email)) {
                        txtEmailAddr.setText(email);
                    }
                    if (!TextUtils.isEmpty(telephoneNo)) {
                        txtContactNo.setText(telephoneNo);
                    }
                    if (!TextUtils.isEmpty(website)) {
                        txtWebsite.setText(website);
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
        TrustMethods.showBackButtonAlert(ContactUsActivity.this);
    }
}