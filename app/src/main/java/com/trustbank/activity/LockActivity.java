package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.trustbank.Model.CheckSimInfoModel;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SessionManager;
import com.trustbank.util.SetTheme;
import com.trustbank.util.SharePreferenceUtils;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;
import com.trustbank.util.barcode.BarcodeCaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.trustbank.util.AppConstants.isAutoReadOTPEnabled;
import static com.trustbank.util.MBank.loadAppLogo;

public class LockActivity extends AppCompatActivity implements View.OnClickListener, AlertDialogOkListener {
    @BindViews({R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btn_clear})
    List<View> btnNumPads;

    @BindViews({R.id.dot_1, R.id.dot_2, R.id.dot_3, R.id.dot_4})
    List<EditText> dots;

    private ImageView ivAppLogo, imgClient;
    private static final int MAX_LENGHT = 4;
    private String codeString = "";
    private CoordinatorLayout coordinatorLayout;
    private TrustMethods method;
    private SessionManager sessionManager;
    private TextView txtShowHide, textClient, txtVersionNameId;
    private EditText mPin1;
    private EditText mPin2;
    private ImageView btnClientIdOk;
    private EditText mPin3;
    private EditText mPin4, etClient;
    private String versionName;
    private String simErrorMsg;
    private LinearLayout linearClient;
    private RelativeLayout linearParentClientId;
    AlertDialogOkListener alertDialogOkListener = this;
    private SharePreferenceUtils sharePreferenceUtils;
    private AutoCompleteTextView autoCompleteTextViewClient_Id;
    private LinearLayout linearAutoComplete;
    private ArrayAdapter<String> mAdapter = null;
    private  List<String> clientIdList = null;
    private ImageView btnEditClientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTheme.changeToTheme(LockActivity.this, true);
        setContentView(R.layout.activity_lock_new);

        ButterKnife.bind(this);
        inIt();
    }

    private void inIt() {
        try {
            ivAppLogo = findViewById(R.id.ivAppLogo);
            loadAppLogo(ivAppLogo);

            method = new TrustMethods(LockActivity.this);
            sessionManager = new SessionManager(LockActivity.this);
            TextView txtForgotPin = findViewById(R.id.txtForgotPinId);
            txtShowHide = findViewById(R.id.txtShowHideId);
            coordinatorLayout = findViewById(R.id.coordinatorLayoutId);
            ImageView btnLogin = findViewById(R.id.btn_space);
            mPin1 = findViewById(R.id.dot_1);
            mPin2 = findViewById(R.id.dot_2);
            mPin3 = findViewById(R.id.dot_3);
            mPin4 = findViewById(R.id.dot_4);

            sharePreferenceUtils = new SharePreferenceUtils(LockActivity.this);
            sharePreferenceUtils.clearValue(AppConstants.AUTH_TOKEN);
            textClient = findViewById(R.id.textClientId);
            imgClient = findViewById(R.id.imgClientId);
            etClient = findViewById(R.id.etClientId);
            btnClientIdOk = findViewById(R.id.btnClientIdOk);
            linearClient = findViewById(R.id.linearClientId);
            linearParentClientId = findViewById(R.id.linearParentClientId);
            txtVersionNameId = findViewById(R.id.txtVersionNameId);

            autoCompleteTextViewClient_Id = findViewById(R.id.autoCompleteTextViewClient_Id);
            linearAutoComplete=findViewById(R.id.layoutAutoComplete);

            btnEditClientID=findViewById(R.id.imgEditClient);
            btnEditClientID.setOnClickListener(this);


            versionName = TrustMethods.getVersionName(LockActivity.this);
            txtVersionNameId.setText("Application Version " + versionName);
            AppConstants.setUSERMOBILENUMBER(sessionManager.getMobileNUmber(SessionManager.KEY_MOBILE_NO, SessionManager.KEY_MOBILE_NO_OLD));
            if (!TextUtils.isEmpty(AppConstants.getCLIENTID())) {
                AppConstants.setCLIENTID(AppConstants.getCLIENTID());
            } else {
                AppConstants.setCLIENTID(sessionManager.getClientId());
            }

            txtForgotPin.setOnClickListener(this);
            btnLogin.setOnClickListener(this);
            txtShowHide.setOnClickListener(this);

            if (!TextUtils.isEmpty(AppConstants.getCLIENTID())) {
                linearParentClientId.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(AppConstants.getCLIENTID())) {
                    String customerId = AppConstants.getCLIENTID();
                    if (customerId.length() == 1) {
                        textClient.setText("Customer ID : XXXX" + customerId);
                    } else {
                        String clientid = customerId.substring(customerId.length() - 2);
                        textClient.setText("Customer ID : XXXX" + clientid);
                    }
                }
            }

            if (isAutoReadOTPEnabled) {
                Intent intent = getIntent();
                if (intent.getStringExtra(AppConstants.SIM_ERROR_MSG) != null) {
                    simErrorMsg = intent.getStringExtra(AppConstants.SIM_ERROR_MSG);
                    if (simErrorMsg.equalsIgnoreCase(AppConstants.SIM_NOT_EXISTS)) {
                        AlertDialogMethod.alertDialogOk(LockActivity.this, "NO SIM Card", "No " +
                                        "Sim Card Not Detected. Please insert the sim in slot and try again",
                                getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                    }
                } else if (!TrustMethods.isSimVerified(this)) {
                    CheckSimInfoModel checkSimInfoModel = TrustMethods.getSimDetails(this);
                    AlertDialogMethod.alertDialogOk(LockActivity.this, "SIM not detected for mobile number " + checkSimInfoModel.getMobileNumber(), "" +
                                    "Click ok to register with new number",
                            getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                }
            }

            imgClient.setOnClickListener(v -> {
                try {
                    linearClient.setVisibility(View.GONE);
                    if (getPackageName().equals("com.trustbank.sadhnambank")) {
                        linearAutoComplete.setVisibility(View.VISIBLE);
                        autoCompleteTextViewClient_Id.setVisibility(View.VISIBLE);
                        etClient.setVisibility(View.GONE);
                        Set<String> mySetClientList = sessionManager.getClientListIds();
                        clientIdList = new ArrayList<>();
                        clientIdList.addAll(mySetClientList);
                        autoCompleteTextViewClient_Id.setText(AppConstants.getCLIENTID());

                        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, clientIdList);
                        autoCompleteTextViewClient_Id.setThreshold(1);
                        autoCompleteTextViewClient_Id.setAdapter(mAdapter);


                    } else {
                        etClient.setVisibility(View.VISIBLE);
                        etClient.setText(AppConstants.getCLIENTID());
                        linearAutoComplete.setVisibility(View.GONE);
                        autoCompleteTextViewClient_Id.setVisibility(View.GONE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            btnClientIdOk.setOnClickListener(v -> {
                try {
                    if (TextUtils.isEmpty(etClient.getText().toString().trim())) {
                        TrustMethods.message(LockActivity.this, "Client id cannot be empty.");
                    } else {
                        TrustMethods.hideSoftKeyboard(LockActivity.this);
                        etClient.clearFocus();
                        linearClient.setVisibility(View.VISIBLE);
                        etClient.setVisibility(View.GONE);
                        btnClientIdOk.setVisibility(View.GONE);
                        linearParentClientId.setVisibility(View.VISIBLE);
                        String customerId = etClient.getText().toString().trim();
                        if (customerId.length() == 1) {
                            textClient.setText("Customer ID : XXXX" + customerId);
                        } else {
                            String clientid = customerId.substring(customerId.length() - 2);
                            textClient.setText("Customer ID : XXXX" + clientid);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @OnClick(R.id.btn_clear)
    public void onClear() {
        if (codeString.length() > 0) {
            codeString = removeLastChar(codeString);
            setDotImagesState();
        }
    }

    @OnClick({R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9})
    public void onClick(Button button) {


        TrustMethods.hideSoftKeyboard(LockActivity.this);

        getStringCode(button.getId());

        if (codeString.length() == MAX_LENGHT) {
            if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
                if (NetworkUtil.getConnectivityStatus(LockActivity.this)) {
                    String clientId = "";
                    if (etClient.getVisibility() == View.VISIBLE) {
                        clientId = etClient.getText().toString().trim();
                    } else if (autoCompleteTextViewClient_Id.getVisibility() == View.VISIBLE) {
                        clientId = autoCompleteTextViewClient_Id.getText().toString();
                    } else {
                        clientId = AppConstants.getCLIENTID();
                    }
//                    if (!TextUtils.isEmpty(clientId)) {


                    if ((!TextUtils.isEmpty(AppConstants.getPlay_store_validate())) && AppConstants.getPlay_store_validate().equalsIgnoreCase("1")) {

                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        method.activityOpenAnimation();
                    } else if (AppConstants.getUSERMOBILENUMBER().equalsIgnoreCase(AppConstants.getPlayStoreDemoUserMobile()) &&
                            clientId.equalsIgnoreCase(AppConstants.getPlayStoreDemoPasswordClientid())) {
                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        method.activityOpenAnimation();
                    } else {

                        new AuthenticateUserAsyncTask(LockActivity.this, AppConstants.getUSERMOBILENUMBER(), codeString, clientId).execute();
                    }

                  /*  } else {
                        TrustMethods.showSnackBarMessage("Client Id not found, please provide customer id.", coordinatorLayout);
                    }*/

                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                }
            } else {
                TrustMethods.displaySimErrorDialog(this);
            }
        } else if (codeString.length() > MAX_LENGHT) {
            //reset the input code
            codeString = "";
            getStringCode(button.getId());
        }
        setDotImagesState();
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim);
        findViewById(R.id.dot_layout).startAnimation(shake);
        clearAllMpinField();
    }

    private void getStringCode(int buttonId) {
        switch (buttonId) {
            case R.id.btn0:
                codeString += "0";
                break;
            case R.id.btn1:
                codeString += "1";
                break;
            case R.id.btn2:
                codeString += "2";
                break;
            case R.id.btn3:
                codeString += "3";
                break;
            case R.id.btn4:
                codeString += "4";
                break;
            case R.id.btn5:
                codeString += "5";
                break;
            case R.id.btn6:
                codeString += "6";
                break;
            case R.id.btn7:
                codeString += "7";
                break;
            case R.id.btn8:
                codeString += "8";
                break;
            case R.id.btn9:
                codeString += "9";
                break;
            default:
                break;
        }
    }

    private void setDotImagesState() {
        char[] charArray = codeString.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            String char2 = String.valueOf(charArray[i]);
            TrustMethods.LogMessage("char1", String.valueOf(charArray[i]));
            dots.get(i).setText(char2);
        }
        if (codeString.length() < 4) {
            for (int j = codeString.length(); j < 4; j++) {
                dots.get(j).setText("");
            }
        }
    }

    public void clearAllMpinField() {
        for (int j = 0; j < dots.size(); j++) {
            dots.get(j).setText("");
        }
    }

    private String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length() - 1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_space:
                loginCheck();
                break;

            case R.id.txtForgotPinId:
                Intent pinActivationIntent = new Intent(LockActivity.this, PinActivation.class);
                pinActivationIntent.putExtra("operationType", "forgotMpin");
                startActivity(pinActivationIntent);
                method.activityOpenAnimation();



                break;

            case R.id.txtShowHideId:
                if (txtShowHide.getText().toString().equalsIgnoreCase("Show")) {
                    txtShowHide.setText("Hide");
                    txtShowHide.setCompoundDrawablesWithIntrinsicBounds(R.drawable.eye_off, 0, 0, 0);
                    mPin1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    mPin2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    mPin3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    mPin4.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    mPin1.setTextSize(25);
                    mPin2.setTextSize(25);
                    mPin3.setTextSize(25);
                    mPin4.setTextSize(25);
                } else {
                    txtShowHide.setText("SHOW");
                    txtShowHide.setCompoundDrawablesWithIntrinsicBounds(R.drawable.eye, 0, 0, 0);
                    mPin1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPin2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPin3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPin4.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPin1.setTextSize(35);
                    mPin2.setTextSize(35);
                    mPin3.setTextSize(35);
                    mPin4.setTextSize(35);
                }
                break;

            case R.id.imgEditClient:

                if (clientIdList != null && clientIdList.size() > 0) {
                    // show all suggestions
                    if (!autoCompleteTextViewClient_Id.getText().toString().equals(""))
                        mAdapter.getFilter().filter(null);
                    autoCompleteTextViewClient_Id.showDropDown();
                }

                break;

            default:
                break;
        }
    }

    private void loginCheck() {
        try {
            TrustMethods.hideSoftKeyboard(LockActivity.this);
            if (codeString.length() == MAX_LENGHT) {
                if (!TextUtils.isEmpty(codeString)) {
                    if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
                        if (NetworkUtil.getConnectivityStatus(LockActivity.this)) {
                            String clientId = "";
                            if (etClient.getVisibility() == View.VISIBLE) {

                                clientId = etClient.getText().toString().trim();

                            } else if (autoCompleteTextViewClient_Id.getVisibility() == View.VISIBLE) {
                                clientId = autoCompleteTextViewClient_Id.getText().toString().trim();
                            } else {
                                clientId = AppConstants.getCLIENTID();
                            }
//                            if (!TextUtils.isEmpty(clientId)) {


                            if ((!TextUtils.isEmpty(AppConstants.getPlay_store_validate())) && AppConstants.getPlay_store_validate().equalsIgnoreCase("1")) {
                                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                method.activityOpenAnimation();
                            } else if (AppConstants.getUSERMOBILENUMBER().equalsIgnoreCase(AppConstants.getPlayStoreDemoUserMobile()) &&
                                    clientId.equalsIgnoreCase(AppConstants.getPlayStoreDemoPasswordClientid())) {
                                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                method.activityOpenAnimation();
                            } else {
                                new AuthenticateUserAsyncTask(LockActivity.this, AppConstants.getUSERMOBILENUMBER(), codeString, clientId).execute();
                            }


                          /*  } else {
                                TrustMethods.showSnackBarMessage("Client Id not found, " +
                                        "please provide customer id.", coordinatorLayout);
                            }*/
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                        }
                    } else {
                        TrustMethods.displaySimErrorDialog(this);
                    }
                } else {
                    TrustMethods.showSnackBarMessage("Enter Valid 4 digit MPIN", coordinatorLayout);
                }
            } else {
                TrustMethods.showSnackBarMessage("Enter Valid 4 digit MPIN", coordinatorLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDialogOk(int resultCode) {
        switch (resultCode) {
            case 0:
                Intent intent = new Intent(LockActivity.this, VerifyMobileNumber.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case 1:
                finish();
                break;
            default:
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AuthenticateUserAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String mMobileNo;
        String mMpin, mClientId;
        String result;

        public AuthenticateUserAsyncTask(Context ctx, String mobileNo, String mpin, String mClientId) {
            this.ctx = ctx;
            this.mMobileNo = mobileNo;
            this.mMpin = mpin;
            this.mClientId = mClientId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LockActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.AuthenticateUserUrl();
                String jsonString = "{\"mobile_number\":\"" + mMobileNo + "\", \"mpin\":\"" + mMpin + "\", \"custid\":\"" + mClientId + "\"}";
                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);

                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithoutHeader(url, jsonString);
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
                    JSONObject dataObject = jsonResponse.getJSONObject("response");
                    response = dataObject.has("auth_token") ? dataObject.getString("auth_token") : "NA";
                    AppConstants.setAuth_token(response);
                    sharePreferenceUtils.putString(AppConstants.AUTH_TOKEN, response);
                } else {
                    String errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
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
                    TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    shakeAnimation();
                } else {
                    if (response != null) {

                        if (getPackageName().equals("com.trustbank.sadhnambank")) {
                            Set<String> mySetClientList = sessionManager.getClientListIds();
                            if (mySetClientList != null){
                                mySetClientList.add(mClientId.trim());
                            }else {
                                mySetClientList = new HashSet<>();
                                mySetClientList.add(mClientId.trim());
                            }
                            sessionManager.storeclientList(mySetClientList);
                        }
                        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(LockActivity.this)) {
                            if (NetworkUtil.getConnectivityStatus(LockActivity.this)) {
                                new GetMenuListAsyncTask(LockActivity.this, mClientId).execute();
                            } else {
                                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                            }
                        } else {
                            TrustMethods.displaySimErrorDialog(LockActivity.this);
                        }
                    } else {
                        TrustMethods.showSnackBarMessage("Wrong Pass code", coordinatorLayout);

                        //vibrate the dots layout
                        shakeAnimation();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetMenuListAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String result, mClientId;
        String actionName = "GET_MENUS";

        public GetMenuListAsyncTask(Context ctx, String mClientId) {
            this.ctx = ctx;
            this.mClientId = mClientId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LockActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.GetMenuListUrl();
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
                    JSONObject dataObject = jsonResponse.getJSONObject("response");
                    if (dataObject.has("error")) {
                        error = dataObject.getString("error");
                        return error;
                    }

                    JSONObject rowJsonObject = dataObject.getJSONObject("row");
                    if (rowJsonObject.has("error")) {
                        error = rowJsonObject.getString("error");
                        return error;
                    }

                    AppConstants.setMnu_accounts(rowJsonObject.has("mnu_accounts") ? rowJsonObject.getString("mnu_accounts") : "0");
                    AppConstants.setMnu_fundtransfer(rowJsonObject.has("mnu_fundtransfer") ? rowJsonObject.getString("mnu_fundtransfer") : "0");
                    AppConstants.setMnu_locate_atms(rowJsonObject.has("mnu_locate_atms") ? rowJsonObject.getString("mnu_locate_atms") : "0");
                    AppConstants.setMnu_locate_branch(rowJsonObject.has("mnu_locate_branch") ? rowJsonObject.getString("mnu_locate_branch") : "0");
                    AppConstants.setMnu_contact_us(rowJsonObject.has("mnu_contact_us") ? rowJsonObject.getString("mnu_contact_us") : "0");
                    AppConstants.setMnu_about_us(rowJsonObject.has("mnu_about_us") ? rowJsonObject.getString("mnu_about_us") : "0");
                    AppConstants.setMnu_accounts_menu_accountdetails(rowJsonObject.has("mnu_accounts_menu_accountdetails") ? rowJsonObject.getString("mnu_accounts_menu_accountdetails") : "0");
                    AppConstants.setMnu_accounts_menu_balenquiry(rowJsonObject.has("mnu_accounts_menu_balenquiry") ? rowJsonObject.getString("mnu_accounts_menu_balenquiry") : "0");
                    AppConstants.setMnu_accounts_menu_ministatemnt(rowJsonObject.has("mnu_accounts_menu_ministatemnt") ? rowJsonObject.getString("mnu_accounts_menu_ministatemnt") : "0");
                    AppConstants.setMnu_accounts_menu_showmmid(rowJsonObject.has("mnu_accounts_menu_showmmid") ? rowJsonObject.getString("mnu_accounts_menu_showmmid") : "0");
                    AppConstants.setMnu_accounts_menu_last5imps(rowJsonObject.has("mnu_accounts_menu_last5imps") ? rowJsonObject.getString("mnu_accounts_menu_last5imps") : "0");
                    AppConstants.setMnu_accounts_menu_neftenquiry(rowJsonObject.has("mnu_accounts_menu_neftenquiry") ? rowJsonObject.getString("mnu_accounts_menu_neftenquiry") : "0");
                    AppConstants.setMnu_fundtransfer_ownbank(rowJsonObject.has("mnu_fundtransfer_ownbank") ? rowJsonObject.getString("mnu_fundtransfer_ownbank") : "0");
                    AppConstants.setMnu_fundtransfer_impstoaccount(rowJsonObject.has("mnu_fundtransfer_impstoaccount") ? rowJsonObject.getString("mnu_fundtransfer_impstoaccount") : "0");
                    AppConstants.setMnu_fundtransfer_nefttoaccount(rowJsonObject.has("mnu_fundtransfer_nefttoaccount") ? rowJsonObject.getString("mnu_fundtransfer_nefttoaccount") : "0");
                    AppConstants.setMnu_fundtransfer_impstomobile(rowJsonObject.has("mnu_fundtransfer_impstomobile") ? rowJsonObject.getString("mnu_fundtransfer_impstomobile") : "0");
                    AppConstants.setMnu_fundtransfer_upi_collect_money(rowJsonObject.has("mnu_fundtransfer_upi_collect_money") ? rowJsonObject.getString("mnu_fundtransfer_upi_collect_money") : "0");
                    AppConstants.setMnu_fundtransfer_mngbenefeciaries(rowJsonObject.has("mnu_fundtransfer_mngbenefeciaries") ? rowJsonObject.getString("mnu_fundtransfer_mngbenefeciaries") : "0");
                    AppConstants.setMnu_faq(rowJsonObject.has("mnu_FAQ") ? rowJsonObject.getString("mnu_FAQ") : "0");
                    AppConstants.setMnu_checkbook_request(rowJsonObject.has("mnu_accounts_menu_cheqbkreq") ? rowJsonObject.getString("mnu_accounts_menu_cheqbkreq") : "0");
                    AppConstants.setMnu_account_statement(rowJsonObject.has("mnu_accounts_menu_acc_stmnt") ? rowJsonObject.getString("mnu_accounts_menu_acc_stmnt") : "0");

                    AppConstants.setMnu_self_transfer_to_account(rowJsonObject.has("mnu_fundtransfer_menu_selftrf") ? rowJsonObject.getString("mnu_fundtransfer_menu_selftrf") : "0");
                    AppConstants.setMnu_Change_MPin(rowJsonObject.has("mnu_settings_menu_change_mpin") ? rowJsonObject.getString("mnu_settings_menu_change_mpin") : "0");
                    AppConstants.setMnu_Change_TPin(rowJsonObject.has("mnu_settings_menu_change_tpin") ? rowJsonObject.getString("mnu_settings_menu_change_tpin") : "0");
                    AppConstants.setMnu_Reset_TPin(rowJsonObject.has("mnu_settings_menu_reset_tpin") ? rowJsonObject.getString("mnu_settings_menu_reset_tpin") : "0");
                    AppConstants.setMnu_Limit_Transaction(rowJsonObject.has("mnu_settings_menu_change_limit") ? rowJsonObject.getString("mnu_settings_menu_change_limit") : "0");
                    AppConstants.setMnu_setting(rowJsonObject.has("mnu_settings") ? rowJsonObject.getString("mnu_settings") : "0");
                    AppConstants.setStopChequebookStatus(rowJsonObject.has("mnu_accounts_stop_chq") ? rowJsonObject.getString("mnu_accounts_stop_chq") : "0");

                    AppConstants.setInqueriChquebookStatus(rowJsonObject.has("mnu_accounts_chq_status") ? rowJsonObject.getString("mnu_accounts_chq_status") : "0");
                    AppConstants.setMnu_accounts_menu_balenquiry_cbs(rowJsonObject.has("mnu_accounts_menu_balenquiry_cbs") ? rowJsonObject.getString("mnu_accounts_menu_balenquiry_cbs") : "0");
                    AppConstants.setMnu_accounts_menu_ministatemnt_cbs(rowJsonObject.has("mnu_accounts_menu_ministatemnt_cbs") ? rowJsonObject.getString("mnu_accounts_menu_ministatemnt_cbs") : "0");
                    AppConstants.setMnu_accounts_menu_showmmid_cbs(rowJsonObject.has("mnu_accounts_menu_showmmid_cbs") ? rowJsonObject.getString("mnu_accounts_menu_showmmid_cbs") : "0");
                    AppConstants.setMnu_accounts_menu_last5imps_cbs(rowJsonObject.has("mnu_accounts_menu_last5imps_cbs") ? rowJsonObject.getString("mnu_accounts_menu_last5imps_cbs") : "0");
                    AppConstants.setMnu_beneficiary_own_bank(rowJsonObject.has("mnu_beneficiary_own_bank") ? rowJsonObject.getString("mnu_beneficiary_own_bank") : "0");
                    AppConstants.setMnu_beneficiary_imps_neft_account_bank(rowJsonObject.has("mnu_beneficiary_imps_neft_account_bank") ? rowJsonObject.getString("mnu_beneficiary_imps_neft_account_bank") : "0");
                    AppConstants.setMnu_beneficiary_imps_mobile_bank(rowJsonObject.has("mnu_beneficiary_imps_mobile_bank") ? rowJsonObject.getString("mnu_beneficiary_imps_mobile_bank") : "0");
                    AppConstants.setMnu_pps_request(rowJsonObject.has("mnu_accounts_menu_pps_request") ? rowJsonObject.getString("mnu_accounts_menu_pps_request") : "0");
                    AppConstants.setMnu_pps_request_enquiry(rowJsonObject.has("mnu_accounts_menu_pps_request_enquiry") ? rowJsonObject.getString("mnu_accounts_menu_pps_request_enquiry") : "0");
                    AppConstants.setMnu_block_debit_card(rowJsonObject.has("mnu_account_block_debit_card") ? rowJsonObject.getString("mnu_account_block_debit_card") : "0");
                    AppConstants.setMnu_verify_beneficiary_name(rowJsonObject.has("mnu_verify_beneficiary_name") ? rowJsonObject.getString("mnu_verify_beneficiary_name") : "0");
                    AppConstants.setMnu_check_imps_transaction_status(rowJsonObject.has("mnu_imps_transction_status") ? rowJsonObject.getString("mnu_imps_transction_status") : "0");
                    AppConstants.setMnu_neft_trans_switch_transaction(rowJsonObject.has("mnu_neft_switch_transaction") ? rowJsonObject.getString("mnu_neft_switch_transaction") : "0");
                    AppConstants.setMnu_bill_pay(rowJsonObject.has("mnu_bill_pay") ? rowJsonObject.getString("mnu_bill_pay") : "0");
                    AppConstants.setMnu_bill_pay_complaint_management(rowJsonObject.has("mnu_bill_pay_complaint_management") ? rowJsonObject.getString("mnu_bill_pay_complaint_management") : "0");
                    AppConstants.setMnu_bill_pay_register_complaints(rowJsonObject.has("mnu_bill_pay_register_complaints") ? rowJsonObject.getString("mnu_bill_pay_register_complaints") : "0");
                    AppConstants.setMnu_bill_pay_track_complaints(rowJsonObject.has("mnu_bill_pay_track_complaints") ? rowJsonObject.getString("mnu_bill_pay_track_complaints") : "0");
                    AppConstants.setMnu_fundtransfer_upi(rowJsonObject.has("mnu_fundtransfer_upi") ? rowJsonObject.getString("mnu_fundtransfer_upi") : "0");
                    AppConstants.setCheckImpsTransStatusFundTransfer(rowJsonObject.has("imps_trans_status_ft") ? rowJsonObject.getString("imps_trans_status_ft") : "0"); //after successfull imps transaction call transaction status apis.

                    AppConstants.setMnu_block_debit_card_switch(rowJsonObject.has("mnu_block_debit_card_switch") ? rowJsonObject.getString("mnu_block_debit_card_switch") : "0"); //TODO
                    AppConstants.setMnu_debit_card_pin_generation(rowJsonObject.has("mnu_debit_card_pin_generation") ? rowJsonObject.getString("mnu_debit_card_pin_generation") : "0");//TODO
                    AppConstants.setMnu_mandate_cancel(rowJsonObject.has("mnu_mandate_cancel") ? rowJsonObject.getString("mnu_mandate_cancel") : "0");
                    AppConstants.setMnu_fundtransfer_upi_get_qr(rowJsonObject.has("mnu_fetch_qr") ? rowJsonObject.getString("mnu_fetch_qr") : "0");
                    AppConstants.setMnu_fundtransfer_upi_create_qr(rowJsonObject.has("mnu_create_new_qr") ? rowJsonObject.getString("mnu_create_new_qr") : "0");
                } else {
                    String errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
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
                    TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    if (error.equalsIgnoreCase("auth token expired.")) {
                        AlertDialogMethod.alertDialogOk(LockActivity.this,
                                getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(LockActivity.this, this.error, "", getResources().getString(R.string.btn_ok),
                                1, false, alertDialogOkListener);
                    }
                } else {
                    //.......................TODO..................... 13-12-2018
                    sessionManager.createLoginSession(AppConstants.getUSERMOBILENUMBER(), mClientId);
                    AppConstants.setCLIENTID(sessionManager.getClientId());
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    method.activityOpenAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        clearAllMpinField();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        method.activityCloseAnimation();
    }


}