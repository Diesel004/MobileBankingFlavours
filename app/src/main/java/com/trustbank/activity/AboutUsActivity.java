package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.trustbank.R;
import com.trustbank.fragment.PrivacyPolicyFragmentDialog;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends AppCompatActivity implements AlertDialogOkListener {

    private TrustMethods trustMethods;
    @BindView(R.id.txtAboutUsId)
    WebView txtAboutUs;

    @BindView(R.id.txtTermsAndConditionId)
    TextView txtTermsAndConditionId;


    @BindView(R.id.coordinatorLayoutId)
    CoordinatorLayout coordinatorLayout;

    AlertDialogOkListener alertDialogOkListener = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN){
            if (savedInstanceState != null){
                Object currentPID = String.valueOf(android.os.Process.myPid());
                // Check current PID with old PID
                if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                    // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                    TrustMethods.naviagteToSplashScreen(AboutUsActivity.this);
                }
            }
        }

        SetTheme.changeToTheme(AboutUsActivity.this, false);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        inIt();
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN){
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }

    private void inIt() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        trustMethods = new TrustMethods(AboutUsActivity.this);
        trustMethods.activityOpenAnimation();

        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
            if (NetworkUtil.getConnectivityStatus(AboutUsActivity.this)) {
                new AsyncTaskGetAboutUs(AboutUsActivity.this).execute();
            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
            }
        } else {
            TrustMethods.displaySimErrorDialog(this);
        }
        if (getPackageName().equals("com.trustbank.pucbmbank")) {
            txtTermsAndConditionId.setVisibility(View.VISIBLE);
        } else {
            txtTermsAndConditionId.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.txtTermsAndConditionId})
    public void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.txtTermsAndConditionId:
                if (getPackageName().equals("com.trustbank.pucbmbank")) {
                    FragmentManager manager = getSupportFragmentManager();
                    DialogFragment newFragment = PrivacyPolicyFragmentDialog.newInstance();
                    newFragment.show(manager, "dialog");
                }
                break;
        }
    }

    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 0) {
            Intent intentLogin = new Intent(getApplicationContext(), LockActivity.class);
            intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentLogin);
            trustMethods.activityCloseAnimation();
        } else {
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            trustMethods.activityCloseAnimation();
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskGetAboutUs extends AsyncTask<Void, Void, String> {
        private String error = "";
        private Context ctx;
        private String response = null;
        private ProgressDialog pDialog;
        private String htmlData;
        private String errorCode = "";
        private String termsAndConditios = "";

        public AsyncTaskGetAboutUs(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AboutUsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.getAboutUsDetails();
                if (!url.equals("")) {
                    response = HttpClientWrapper.getResponceDirectalyGET(url, AppConstants.getAuth_token());
                    TrustMethods.LogMessage("response-->", response);
                    if (response == null || response.equals("")) {
                        error = AppConstants.SERVER_NOT_RESPONDING;
                        return error;
                    }
                    try {
                        printLog("GsonString:" + new Gson().toJson(response));
                        printLog("Replaced:" + convertStandardJSONString(response));
                        JSONObject jsonObject = new JSONObject(convertStandardJSONString(response));
                        printLog("JsonString:\n" + jsonObject.toString());
                        printLog(jsonObject.toString());
                        String errorMsg = jsonObject.has("error") ? jsonObject.getString("error") : null;
                        if (errorMsg != null) {
                            error = errorMsg;
                            return error;
                        }


                        JSONObject jsonObjecttable = jsonObject.getJSONObject("table");
                        JSONArray jsonArray = jsonObjecttable.getJSONArray("rows");
                        JSONObject objects = jsonArray.getJSONObject(0);
                        htmlData = objects.getString("about_us");
                        printLog("HtmlData" + htmlData);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                TrustMethods.LogMessage("Exception ", ex.getMessage());
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
                        AlertDialogMethod.alertDialogOk(AboutUsActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else if (TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(AboutUsActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(AboutUsActivity.this, this.error, "",
                                getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    }


                } else {
                    if (htmlData != null) {
                        txtAboutUs.loadData(htmlData, "text/html", "utf-8");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String convertStandardJSONString(String data_json) {
        data_json = data_json.replace("\\\\", "\\");
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        data_json = data_json.replace("\\\"", "\"");
        return data_json;
    }

    private void printLog(String message) {
        TrustMethods.LogMessage("MenuActivity", message + "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                trustMethods.activityCloseAnimation();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(AboutUsActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        txtAboutUs.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtAboutUs.onResume();
    }
}
