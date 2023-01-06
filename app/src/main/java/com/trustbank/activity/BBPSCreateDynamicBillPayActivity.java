package com.trustbank.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.trustbank.Model.AtributeModelClass;
import com.trustbank.Model.BBPSBillerResponseModel;
import com.trustbank.Model.BBPSCustomeParamater;
import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.tcpconnection.tdto.MessageDtoBuilder;
import com.trustbank.tcpconnection.tmessage.TMessage;
import com.trustbank.tcpconnection.tmessage.TMessageUtil;
import com.trustbank.tcpconnection.util.ResponseEntity;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.DecimalDigitsInputFilter;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class BBPSCreateDynamicBillPayActivity extends AppCompatActivity implements AlertDialogOkListener {

    Button btnSearchBillId;
    LinearLayout linearCustomeProperty;
    Map<String, AtributeModelClass> attributeViewMap;
    String billerId = "";
    CoordinatorLayout coordinatorLayout;
    AlertDialogOkListener alertDialogOkListener = this;
    private TrustMethods method;
    private String billerName;

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
                        TrustMethods.naviagteToSplashScreen(BBPSCreateDynamicBillPayActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(BBPSCreateDynamicBillPayActivity.this, false);
        setContentView(R.layout.activity_b_b_p_s_create_dynamic_bill_pay);

        initComponet();
    }

    private void initComponet() {

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            method = new TrustMethods(BBPSCreateDynamicBillPayActivity.this);
            linearCustomeProperty = findViewById(R.id.linearCustomePropertyID);
            btnSearchBillId = findViewById(R.id.btnSearchBillId);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);

            Intent intent = getIntent();
            List<BBPSCustomeParamater> bbpsCustomeParameter = (List<BBPSCustomeParamater>) intent.getSerializableExtra("customParametr");
            billerId = intent.getStringExtra("billerId");
            billerName = intent.getStringExtra("billerName");
            if (bbpsCustomeParameter != null || bbpsCustomeParameter.size() != 0) {

                createDynamicForm(bbpsCustomeParameter);
            }

            btnSearchBillId.setOnClickListener(v -> {
                if (attributeViewMap != null) {

                    Map<String, Object> attributeValueMap = new HashMap<>();
                    boolean isNoMandatoryAttributeRemain = true;

                    for (AtributeModelClass attributeViewModel : attributeViewMap.values()) {

                        View view1 = attributeViewModel.getView();
                        String attributeValue = "";
                        String finalValue = "";

                        if (view1 instanceof TextInputLayout) {

                            EditText editText = ((TextInputLayout) view1).getEditText();
                            if (editText != null) {
                                attributeValue = editText.getText().toString();
                            }
                        }
                        BBPSCustomeParamater bbpsCustomeParamater = attributeViewModel.getBbpsCustomeParamater();
                        Object type = bbpsCustomeParamater.getDataType();

                        if (type.equals("NUMERIC") || type.equals("Numeric") || type.equals("TEXT") || type.equals("ALPHANUMERIC")) {
                            if (!attributeValue.isEmpty()) {
                                finalValue = attributeValue;
                            }
                        }

                        boolean isMandotory = true;

                        if (isMandotory) {
                            if (TextUtils.isEmpty(finalValue)) {
                                isNoMandatoryAttributeRemain = false;
                                if (view1 instanceof TextInputLayout) {
                                    String errorMsg = "Enter " + bbpsCustomeParamater.getParamaName();
                                    TrustMethods.message(BBPSCreateDynamicBillPayActivity.this, errorMsg);
                                    return;
                                }

                            } else {

                                attributeValueMap.put(bbpsCustomeParamater.getParamaName(), finalValue);

                            }

                        }
                    }

                    if (isNoMandatoryAttributeRemain) {
                        createJson(billerId, attributeValueMap);
                    }

                }

            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void createJson(String billerId, Map<String, Object> attributeValueMap) {

        try {


            JSONObject jsonObjeCustDetails = new JSONObject();
            jsonObjeCustDetails.put("mobileNo", AppConstants.getUSERMOBILENUMBER());

            JSONArray jsonArrayCustomerTag = new JSONArray();
            JSONObject jsonObjectCustomerTag1 = new JSONObject();
            jsonObjectCustomerTag1.put("name", "Email");
            String email = "";
            if (!TextUtils.isEmpty(AppConstants.getUSEREMAILADDRESS())) {
                email = AppConstants.getUSEREMAILADDRESS();
            }
            jsonObjectCustomerTag1.put("value", email);
            JSONObject jsonObjectCustomerTag2 = new JSONObject();
            jsonObjectCustomerTag2.put("name", "ADHAR");
            jsonObjectCustomerTag2.put("value", "");
            JSONObject jsonObjectCustomerTag3 = new JSONObject();
            jsonObjectCustomerTag3.put("name", "PAN");
            jsonObjectCustomerTag3.put("value", "");
            jsonArrayCustomerTag.put(jsonObjectCustomerTag1);
            jsonArrayCustomerTag.put(jsonObjectCustomerTag2);
            jsonArrayCustomerTag.put(jsonObjectCustomerTag3);

            jsonObjeCustDetails.put("customerTags", jsonArrayCustomerTag);

            //-----------agent details------------------------//
            JSONObject jsonObjeAgentDetails = new JSONObject();
            jsonObjeAgentDetails.put("agentId", AppConstants.getCLIENTID());
            JSONArray jsonArrayDeviceTag = new JSONArray();

            JSONObject jsonObjectDeviceTag1 = new JSONObject();
            jsonObjectDeviceTag1.put("name", "INITIATING_CHANNEL");
            jsonObjectDeviceTag1.put("value", "MOB");

            JSONObject jsonObjectDeviceTag2 = new JSONObject();
            jsonObjectDeviceTag2.put("name", "IP");
            jsonObjectDeviceTag2.put("value", AppConstants.IP);

            JSONObject jsonObjectDeviceTag3 = new JSONObject();
            jsonObjectDeviceTag3.put("name", "APP");
            jsonObjectDeviceTag3.put("value", "");

            JSONObject jsonObjectDeviceTag4 = new JSONObject();
            jsonObjectDeviceTag4.put("name", "OS");
            jsonObjectDeviceTag4.put("value", "Android");

            JSONObject jsonObjectDeviceTag5 = new JSONObject();
            jsonObjectDeviceTag5.put("name", "IMEI");
            jsonObjectDeviceTag5.put("value", TrustMethods.getDeviceID(getApplicationContext()));
            // jsonObjectDeviceTag5.put("value", "123654674646bedyeye");

            jsonArrayDeviceTag.put(jsonObjectDeviceTag1);
            jsonArrayDeviceTag.put(jsonObjectDeviceTag2);
            jsonArrayDeviceTag.put(jsonObjectDeviceTag3);
            jsonArrayDeviceTag.put(jsonObjectDeviceTag4);
            jsonArrayDeviceTag.put(jsonObjectDeviceTag5);
            jsonObjeAgentDetails.put("deviceTags", jsonArrayDeviceTag);

            //---------biller details------------------//
            JSONObject billDetailsObje = new JSONObject();
            billDetailsObje.put("billerId", billerId);

            JSONArray jsonArrayCustomerParams = new JSONArray();
            for (String key : attributeValueMap.keySet()) {
                String value = (String) attributeValueMap.get(key);
                JSONObject jsonObjectCustomerParams = new JSONObject();
                jsonObjectCustomerParams.put("name", key);
                jsonObjectCustomerParams.put("value", value);
                jsonArrayCustomerParams.put(jsonObjectCustomerParams);
            }


            billDetailsObje.put("customerParams", jsonArrayCustomerParams);

            //----add at last all the object-----------//
            JSONObject mainJsonObject = new JSONObject();
//            mainJsonObject.put("chId", 1);
//            mainJsonObject.put("custDetails", jsonObjeCustDetails);
            //  mainJsonObject.put("agentDetails", jsonObjeAgentDetails);
            mainJsonObject.put("billDetails", billDetailsObje);

            Log.e("finalObject", mainJsonObject.toString());


            if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(BBPSCreateDynamicBillPayActivity.this)) {
                if (NetworkUtil.getConnectivityStatus(BBPSCreateDynamicBillPayActivity.this)) {
                    new BillFetchAsyncTask(BBPSCreateDynamicBillPayActivity.this, mainJsonObject.toString(), billDetailsObje).execute();
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                }
            } else {
                TrustMethods.displaySimErrorDialog(BBPSCreateDynamicBillPayActivity.this);
            }

        } catch (Exception e) {
            e.printStackTrace();
            TrustMethods.message(this, e.getMessage());
        }
    }

    private void createDynamicForm(List<BBPSCustomeParamater> bbpsCustomeParameter) {

        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(BBPSCreateDynamicBillPayActivity.this)) {
            if (NetworkUtil.getConnectivityStatus(BBPSCreateDynamicBillPayActivity.this)) {
                new GetBillerDetailsAsyncTask(BBPSCreateDynamicBillPayActivity.this, bbpsCustomeParameter).execute();
            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
            }
        } else {
            TrustMethods.displaySimErrorDialog(BBPSCreateDynamicBillPayActivity.this);
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

    private class GetBillerDetailsAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;

        private String errorCode;
        List<BBPSCustomeParamater> bbpsCustomeParameter;


        public GetBillerDetailsAsyncTask(Context ctx, List<BBPSCustomeParamater> bbpsCustomeParameter) {
            this.ctx = ctx;
            this.bbpsCustomeParameter = bbpsCustomeParameter;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BBPSCreateDynamicBillPayActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                attributeViewMap = new HashMap<>();
                for (BBPSCustomeParamater bbpsCustomeParamater : bbpsCustomeParameter) {
                    View view;
                    AtributeModelClass atributeModelClass = new AtributeModelClass();
                    atributeModelClass.setBbpsCustomeParamater(bbpsCustomeParamater);
                    atributeModelClass.setName(bbpsCustomeParamater.getParamaName());

                    if (bbpsCustomeParamater.getDataType().equals("NUMERIC") || bbpsCustomeParamater.getDataType().equals("ALPHANUMERIC")
                            || bbpsCustomeParamater.getDataType().equals("TEXT")) {
                        view = createEditTextView(BBPSCreateDynamicBillPayActivity.this, bbpsCustomeParamater.getParamaName(), bbpsCustomeParamater.getParamaName(), bbpsCustomeParamater.getDataType(), bbpsCustomeParamater.isOptional(),
                                true, bbpsCustomeParamater.getMaxLenght(), "", bbpsCustomeParamater.getRegex());
                        atributeModelClass.setView(view);
                    }
                    attributeViewMap.put(bbpsCustomeParamater.getParamaName(), atributeModelClass);
                }

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
                for (AtributeModelClass attributeViewModel : attributeViewMap.values()) {

                    View view = attributeViewModel.getView();
                    linearCustomeProperty.addView(view);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private TextInputLayout createEditTextView(Activity activity, String name, String alias, Object type, boolean isMandatory,
                                               boolean isEditable, int length, String value, String regex) {

        TextInputLayout textInputLayout = new TextInputLayout(new ContextThemeWrapper(activity, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox));

        try {

            LinearLayout.LayoutParams textInputParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

//            textInputLayout.setHint(activity.getResources().getString(R.string.hint_enter) + " " + alias);
            textInputLayout.setHint("Enter" + " " + alias);
            textInputLayout.setTag(name);
            textInputLayout.setLayoutParams(textInputParam);
            textInputLayout.setPadding(0, 10, 10, 10);

            final TextInputEditText editText = new TextInputEditText(activity);
            editText.setTextSize(22);
            editText.setTag(name);

            if (isMandatory) {
                //textInputLayout.setDefaultHintTextColor(ColorStateList.valueOf(Color.RED));
            }

            editText.setMaxLines(1);
            if ("NUMERIC".equals(type)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
            } else if ("DOUBLE".equals(type)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
            } else if ("FLOAT".equals(type)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});

           /*     case DATE:
                    editText.setInputType(InputType.TYPE_NULL);
                    editText.setFocusable(false);

                    String dateString = AppMethods.convertToValidDateString(value, false);
                    if (!TextUtils.isEmpty(dateString)) {
                        value = dateString;
                    }

                    editText.setOnClickListener(v -> AppMethods.datePicker(activity, editText, null, null));
                    break;
*/
            } else if ("ALPHANUMERIC".equals(type)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                if (length != 0) {
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});

                }
            } else if ("UNKOWN".equals(type)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                if (length != 0) {
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
                }
            }

            if (!TextUtils.isEmpty(value)) {
                editText.setText(value);
            }

            if (!isEditable) {
                editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                editText.setFocusable(false);
                editText.setEnabled(false);
            }

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textInputLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            textInputLayout.addView(editText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return textInputLayout;
    }


    @SuppressLint("StaticFieldLeak")
    private class BillFetchAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "", responseError = "", status = "";
        Context ctx;
        String response;
        String result;
        ProgressDialog pDialog;
        String data;
        //String action = "send_to_switch";
        String finalResponse;
        private String errorCode, errorDetails;
        private String refId;

        HashMap<String, Object> billDetailsMap = new HashMap<>();
        HashMap<String, Object> additionalInfoMap = new HashMap<>();
        BBPSBillerResponseModel bbpsBillerResponseModel;
        private JSONObject billDetailsJsonObject;


        public BillFetchAsyncTask(Context ctx, String data, JSONObject billDetailsJsonObject) {
            this.ctx = ctx;
            this.data = data;
            this.billDetailsJsonObject = billDetailsJsonObject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(BBPSCreateDynamicBillPayActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                //  String jsonString = "{\"filter\":[\"stan\"]}";
                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(BBPSCreateDynamicBillPayActivity.this, jsonString, TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    if (generateStanRRNModel.getError().equalsIgnoreCase("Old auth token.")) {
                        errorCode = "9004";

                    }
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {
                    String base64Data = Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);

                    TMessage msg = msgDto.SearchBillRequest(TMessageUtil.GetLocalTxnDtTime(),
                            generateStanRRNModel.getStan(),
                            generateStanRRNModel.getChannel_ref_no(), base64Data); //TMessageUtil.MSG_INSTITUTION_ID);

                    Log.d("msg.GetXml()" + "" + ":", msg.GetXml());

                    String base64EncodedRequestJson = Base64.encodeToString(msg.GetXml().getBytes(), Base64.NO_WRAP);

                    String jsonStringRequest = "{\"data\":\"" + base64EncodedRequestJson + "\"}";
                    TrustMethods.LogMessage("", "base64EncodedRequestJson : " + jsonStringRequest);

                    if (!url.equals("")) {

                        result = HttpClientWrapper.postWitAuthHeader(url, jsonStringRequest, AppConstants.getAuth_token());

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

                        JSONObject jsonObject = new JSONObject(responseValue);
                        String statusCode = jsonObject.has("respCode") ? jsonObject.getString("respCode") : "";
                        status = jsonObject.has("status") ? jsonObject.getString("status") : "";

                        JSONObject responseJsonObject = jsonObject.has("response") ? jsonObject.getJSONObject("response") : null;
                        if (responseJsonObject != null) {

                            String actularResponseCode = responseJsonObject.has("responseCode") ? responseJsonObject.getString("responseCode") : "";
                            refId = responseJsonObject.has("refId") ? responseJsonObject.getString("refId") : "";

                            if (actularResponseCode.equalsIgnoreCase("000") || actularResponseCode.equalsIgnoreCase("0000")) {

                                JSONArray billDetailsJsonArray = responseJsonObject.has("billDetails") ? responseJsonObject.getJSONArray("billDetails") : null;
                                if (billDetailsJsonArray != null) {
                                    for (int i = 0; i < billDetailsJsonArray.length(); i++) {
                                        JSONObject billDetailsObject = billDetailsJsonArray.getJSONObject(i);
                                        String name = billDetailsObject.has("name") ? billDetailsObject.getString("name") : "";
                                        String value = billDetailsObject.has("value") ? billDetailsObject.getString("value") : "";
                                        billDetailsMap.put(name, value);
                                    }
                                }

                                bbpsBillerResponseModel = new BBPSBillerResponseModel();
                                JSONObject billerResJsonObject = responseJsonObject.has("billerResponse") ? responseJsonObject.getJSONObject("billerResponse") : null;
                                String customerName = billerResJsonObject.has("customerName") ? billerResJsonObject.getString("customerName") : "";
                                String amount = billerResJsonObject.has("amount") ? billerResJsonObject.getString("amount") : "";
                                String dueDate = billerResJsonObject.has("dueDate") ? billerResJsonObject.getString("dueDate") : "";
                                String billDate = billerResJsonObject.has("billDate") ? billerResJsonObject.getString("billDate") : "";
                                String billNumber = billerResJsonObject.has("billNumber") ? billerResJsonObject.getString("billNumber") : "";
                                String billPeriod = billerResJsonObject.has("billPeriod") ? billerResJsonObject.getString("billPeriod") : "";

                                bbpsBillerResponseModel.setCustomerName(customerName);
                                bbpsBillerResponseModel.setAmount(amount);
                                bbpsBillerResponseModel.setDueDate(dueDate);
                                bbpsBillerResponseModel.setBillDate(billDate);
                                bbpsBillerResponseModel.setBillNumber(billNumber);
                                bbpsBillerResponseModel.setBillPeriod(billPeriod);

                                JSONArray billTagsJsonArray = billerResJsonObject.has("billTags") ? billerResJsonObject.getJSONArray("billTags") : null;
                                if (billTagsJsonArray != null) {
                                    LinkedHashMap<String, Object> billTagsMap = new LinkedHashMap<>();
                                    for (int i = 0; i < billTagsJsonArray.length(); i++) {

                                        JSONObject jsonObjectBillTags = billTagsJsonArray.getJSONObject(i);
                                        String name = jsonObjectBillTags.has("name") ? jsonObjectBillTags.getString("name") : "";
                                        String value = jsonObjectBillTags.has("value") ? jsonObjectBillTags.getString("value") : "";
                                        billTagsMap.put(name, value);
                                    }
                                    bbpsBillerResponseModel.setBillTagsMap(billTagsMap);

                                    JSONArray additionalInfoJsonArray = responseJsonObject.has("additionalInfo") ? responseJsonObject.getJSONArray("additionalInfo") : null;
                                    if (additionalInfoJsonArray != null) {
                                        for (int i = 0; i < additionalInfoJsonArray.length(); i++) {
                                            JSONObject additionalObjeObject = additionalInfoJsonArray.getJSONObject(i);
                                            String name = additionalObjeObject.has("name") ? additionalObjeObject.getString("name") : "";
                                            String value = additionalObjeObject.has("value") ? additionalObjeObject.getString("value") : "";
                                            additionalInfoMap.put(name, value);
                                        }
                                    }

                                }
                            } else {
                                errorCode = actularResponseCode;

                                error = responseJsonObject.has("complianceReason") ? responseJsonObject.getString("complianceReason") : "";
                                responseError = responseJsonObject.has("responseReason") ? responseJsonObject.getString("responseReason") : "";
                                if (responseJsonObject.has("errorList")) {
                                    JSONArray jsonArray = responseJsonObject.getJSONArray("errorList");
                                    if (jsonArray.length() > 0) {
                                        errorDetails = jsonArray.getJSONObject(0).has("errorDtl") ? jsonArray.getJSONObject(0).getString("errorDtl") : "";
                                    }
                                }
                            }
                        }
                        // onProgressUpdate();
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

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!TextUtils.isEmpty(error) || !TextUtils.isEmpty(errorCode) || !TextUtils.isEmpty(responseError)) {
                    //   TrustMethods.message(BBPSCreateDynamicBillPayActivity.this, error);
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(BBPSCreateDynamicBillPayActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(BBPSCreateDynamicBillPayActivity.this, status,
                                error + "\n" + responseError + "\n" + errorDetails, getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);

                     /*   if (!TextUtils.isEmpty(error)) {
                            TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        } else {
                            TrustMethods.showSnackBarMessage(this.responseError + " " + this.errorDetails, coordinatorLayout);
                        }*/

                    }
                    //    cardBalMsg.setVisibility(View.GONE);
                } else {

                    Intent intent = new Intent(BBPSCreateDynamicBillPayActivity.this, BBPSDisplayBillFetchedDetailsActivity.class);
                    intent.putExtra("billDetailsMap", billDetailsMap);
                    intent.putExtra("additionalInfoMap", additionalInfoMap);
                    intent.putExtra("BBPSBillerResponseModel", bbpsBillerResponseModel);
                    intent.putExtra("billerName", billerName);
                    intent.putExtra("billerId", billerId);
                    intent.putExtra("refId", refId);
                    intent.putExtra("billDetailsJsonObject", billDetailsJsonObject.toString());
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
        TrustMethods.showBackButtonAlert(BBPSCreateDynamicBillPayActivity.this);
    }

}