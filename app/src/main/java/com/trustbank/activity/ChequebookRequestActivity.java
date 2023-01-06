package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.trustbank.Model.ChequeBookRequestModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.Model.NoOfLeavesModel;
import com.trustbank.Model.NoOfLeavesPerBook;
import com.trustbank.R;
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

public class ChequebookRequestActivity extends AppCompatActivity implements AlertDialogOkListener {

    private TrustMethods method;
    private Spinner selectAccSpinner, checkbookqueSpinnerId, leafCountSpinnerId, chequeTypeSpinnerId;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private List<String> accountList;
    private EditText remarkEditId;
    private Button btnRequestSendId;
    private AlertDialogOkListener alertDialogOkListener = this;
    private List<ChequeBookRequestModel> chequeBookRequestModels;
    private HashMap<String, List<NoOfLeavesPerBook>> noOfLeavesHashMap = new HashMap<>();
    private HashMap<String, List<NoOfLeavesModel>> noOfChequebookHashmap = new HashMap<>();
    private ArrayAdapter<String> adapterLeaf = null;
    private ArrayAdapter<String> adapterChequeBookRequest = null;
    private LinearLayout linearChequeTypeId;

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
                        TrustMethods.naviagteToSplashScreen(ChequebookRequestActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(ChequebookRequestActivity.this, false);
        setContentView(R.layout.activity_checkbook_request);
        inIt();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        try {
            if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
                bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inIt() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            method = new TrustMethods(ChequebookRequestActivity.this);
            selectAccSpinner = findViewById(R.id.selectAccSpinnerId);
            checkbookqueSpinnerId = findViewById(R.id.checkbookqueSpinnerId);
            leafCountSpinnerId = findViewById(R.id.leafCountSpinnerId);
            btnRequestSendId = findViewById(R.id.btnRequestSendId);
            remarkEditId = findViewById(R.id.remarkEditId);

            linearChequeTypeId = findViewById(R.id.linearChequeTypeId);
            chequeTypeSpinnerId = findViewById(R.id.chequeTypeSpinnerId);
            accountNoSpinner();


            btnRequestSendId.setOnClickListener(v -> {
                checkBookRequestSendEvent();
            });

            linearChequeTypeId.setVisibility(View.GONE);
          //  chequeBookTypeDropdown();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    private void checkBookRequestSendEvent() {

        if (selectAccSpinner.getSelectedItemPosition() == 0) {
            TrustMethods.message(this, "Please Select Account No.");
        } else if (checkbookqueSpinnerId.getSelectedItemPosition() == 0) {
            TrustMethods.message(this, "Please Select No. of ChequeBook.");
        } else if (leafCountSpinnerId.getSelectedItemPosition() == 0) {
            TrustMethods.message(this, "Please Select  Leaf Count.");
        } else {
            String accountNo = TrustMethods.getValidAccountNo(selectAccSpinner.getSelectedItem().toString());
            String noOfChequebook = checkbookqueSpinnerId.getSelectedItem().toString();
            String noOfLeaves = leafCountSpinnerId.getSelectedItem().toString();
            String remark = remarkEditId.getText().toString();
            if (NetworkUtil.getConnectivityStatus(ChequebookRequestActivity.this)) {
                new CallChequeBookRequestAsyncTask(this, accountNo.trim(), noOfChequebook, noOfLeaves, remark).execute();
            } else {
                TrustMethods.message(ChequebookRequestActivity.this, getResources().getString(R.string.error_check_internet));
            }
        }

    }

    private void accountNoSpinner() {
        try {
            accountsArrayList = method.getArrayList(ChequebookRequestActivity.this, "AccountListPref");

            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                accountList = new ArrayList<>();
                accountList.add(0, "Select Account Number");
                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    if (TrustMethods.isAccountTypeValid(getUserProfileModal.getActType())) {
                        String accNo = getUserProfileModal.getAccNo();
                        String accTypeCode = getUserProfileModal.getAcTypeCode();
                        accountList.add(accNo + " - " + accTypeCode);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ChequebookRequestActivity.this,
                        android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectAccSpinner.setAdapter(adapter);

            }

            selectAccSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position != 0) {
                        String selectedAccNo = TrustMethods.getValidAccountNo(selectAccSpinner.getSelectedItem().toString());
                        if (NetworkUtil.getConnectivityStatus(ChequebookRequestActivity.this)) {
                            new GetNoOfCheckbookRequestAsyncTask(ChequebookRequestActivity.this, selectedAccNo.trim()).execute();
                        } else {
                            AlertDialogMethod.alertDialogOk(ChequebookRequestActivity.this, getResources().getString(R.string.error_check_internet), "", getResources().getString(R.string.btn_ok),
                                    3, false, alertDialogOkListener);
                        }
                    } else {
                        leafCountSpinnerId.setSelection(0);
                        checkbookqueSpinnerId.setSelection(0);
                        checkbookqueSpinnerId.setAdapter(null);
                        leafCountSpinnerId.setAdapter(null);

                        checkbookqueSpinnerId.setSelection(0);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            checkbookqueSpinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {

                        String chequeBookList = checkbookqueSpinnerId.getSelectedItem().toString();
                        if (noOfLeavesHashMap.size() != 0) {
                            if (noOfLeavesHashMap.containsKey(chequeBookList)) {
                                List<NoOfLeavesPerBook> noOfLeavesPerBooks = noOfLeavesHashMap.get(chequeBookList);
                                if (noOfLeavesPerBooks != null && noOfLeavesPerBooks.size() != 0) {
                                    List<String> leavesStringList = new ArrayList<>();
                                    leavesStringList.add("Select Leaf count");
                                    for (int i = 0; i < noOfLeavesPerBooks.size(); i++) {
                                        leavesStringList.add(noOfLeavesPerBooks.get(i).getNoOfLeavesPerBook());
                                    }
                                    adapterLeaf = new ArrayAdapter<>(ChequebookRequestActivity.this,
                                            android.R.layout.simple_spinner_item, leavesStringList);
                                    adapterLeaf.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    leafCountSpinnerId.setAdapter(adapterLeaf);
                                }
                            }
                        }
                    } else {
                        leafCountSpinnerId.setSelection(0);
                        leafCountSpinnerId.setAdapter(null);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
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
    public void onDialogOk(int resultCode) {
        switch (resultCode) {
            case 0:
                Intent intent = new Intent(ChequebookRequestActivity.this, LockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case 1:
                break;
            case 2:
                clearAllFields();
                break;
            case 3:
                finish();
                break;
            default:
                break;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class GetNoOfCheckbookRequestAsyncTask extends AsyncTask<Void, Void, String> {

        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String result;
        String accNo;
        String actionName = "GET_CHEQUE_MASTER";
        private String errorCode;


        public GetNoOfCheckbookRequestAsyncTask(Context ctx, String accNo) {
            this.accNo = accNo;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ChequebookRequestActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
               // String url = TrustURL.GetChequeBookDetailsUrl(AppConstants.getUSERMOBILENUMBER(),accNo);
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
                    JSONObject responseObject = jsonResponse.getJSONObject("response");
                    if (responseObject.has("error")) {
                        error = responseObject.getString("error");
                        return error;
                    }
                    JSONObject rootObject = responseObject.getJSONObject("root");
                    if (rootObject.has("error")) {
                        error = rootObject.getString("error");
                        return error;
                    }

                    JSONArray elementArray = rootObject.getJSONArray("Element");

                    chequeBookRequestModels = new ArrayList<>();
                    for (int k = 0; k < elementArray.length(); k++) {
                        ChequeBookRequestModel chequeBookRequestModel = new ChequeBookRequestModel();

                        JSONObject jsonObject = elementArray.getJSONObject(k);
                        String accountType = jsonObject.getString("AccountType");
                        chequeBookRequestModel.setAccountType(accountType);

                        JSONArray jsonArray = jsonObject.getJSONArray("NoofChequeBook");

                        List<NoOfLeavesModel> noOfLeavesModels = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {

                            NoOfLeavesModel noOfLeavesModel = new NoOfLeavesModel();
                            JSONObject noOfCheckBookObject = jsonArray.getJSONObject(i);
                            String noOfChequeBook = noOfCheckBookObject.getString("NoOfChequeBook");
                            noOfLeavesModel.setNoOfChequeBook(noOfChequeBook);

                            JSONArray leavesArrayObject = noOfCheckBookObject.getJSONArray("NoofLeavesPerBook");
                            List<NoOfLeavesPerBook> noOfLeavesPerBooks = new ArrayList<>();
                            for (int j = 0; j < leavesArrayObject.length(); j++) {
                                NoOfLeavesPerBook noOfLeavesPerBook = new NoOfLeavesPerBook();
                                JSONObject noOfLeavesBookObject = leavesArrayObject.getJSONObject(j);
                                String noOfLeaves = noOfLeavesBookObject.getString("NoOfLeavesPerBook");
                                noOfLeavesPerBook.setNoOfLeavesPerBook(noOfLeaves);
                                noOfLeavesPerBooks.add(noOfLeavesPerBook);
                            }

                            noOfLeavesModel.setNoOfLeavesPerBooks(noOfLeavesPerBooks);
                            noOfLeavesModels.add(noOfLeavesModel);
                        }
                        chequeBookRequestModel.setNoOfLeavesModels(noOfLeavesModels);

                        chequeBookRequestModels.add(chequeBookRequestModel);

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
                        AlertDialogMethod.alertDialogOk(ChequebookRequestActivity.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(ChequebookRequestActivity.this, this.error, "", getResources().getString(R.string.btn_ok),
                                1, false, alertDialogOkListener);
                    }
                } else {
                    //TODO..................... 13-12-2018

                    for (ChequeBookRequestModel chequeBookRequestModel : chequeBookRequestModels) {
                        String accounType = chequeBookRequestModel.getAccountType();
                        noOfChequebookHashmap.put(accounType, chequeBookRequestModel.getNoOfLeavesModels());
                    }

                    String accountType;
                    List<NoOfLeavesModel> noOfLeavesModels = null;
                    for (GetUserProfileModal getUserProfileModal : accountsArrayList) {
                        if (getUserProfileModal.getAccNo().equalsIgnoreCase(accNo)) {
                            accountType = getUserProfileModal.getActType();
                            if (noOfChequebookHashmap != null && noOfChequebookHashmap.size() != 0) {
                                if (noOfChequebookHashmap.containsKey(accountType)) {
                                    noOfLeavesModels = noOfChequebookHashmap.get(accountType);
                                    break;
                                }
                            }
                        }
                    }
                    if (noOfLeavesModels != null && noOfLeavesModels.size() != 0) {
                        List<String> chequeBookList = new ArrayList<>();
                        chequeBookList.add(0, "Select No. Of Chequebook");
                        leafCountSpinnerId.setSelection(0);
                        for (NoOfLeavesModel noOfLeavesModel : noOfLeavesModels) {
                            chequeBookList.add(noOfLeavesModel.getNoOfChequeBook());
                            noOfLeavesHashMap.put(noOfLeavesModel.getNoOfChequeBook(), noOfLeavesModel.getNoOfLeavesPerBooks());
                        }
                        adapterChequeBookRequest = new ArrayAdapter<>(ChequebookRequestActivity.this,
                                android.R.layout.simple_spinner_item, chequeBookList);
                        adapterChequeBookRequest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        checkbookqueSpinnerId.setAdapter(adapterChequeBookRequest);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


    @SuppressLint("StaticFieldLeak")
    private class CallChequeBookRequestAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String accountNumber, noOfChequeBook, noOfLeaves, remark;
        String actionName = "SAVE_CHEQUE_DETAILS";
        String result;
        private String errorCode;

        public CallChequeBookRequestAsyncTask(Context ctx, String accountNumber,
                                              String noOfChequeBook, String noOfLeaves,
                                              String remark) {
            this.ctx = ctx;
            this.accountNumber = accountNumber;
            this.noOfChequeBook = noOfChequeBook;
            this.noOfLeaves = noOfLeaves;
            this.remark = remark;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ChequebookRequestActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.GetMenuListUrl();

                String jsonString = "{\"account_number\":\"" + accountNumber + "\",\"number_of_chequebooks\":\"" + noOfChequeBook + "\", \"number_of_leaves\":\"" + noOfLeaves + "\", \"remarks\":\"" + remark + "\",\"mobile_number\":\"" + AppConstants.getUSERMOBILENUMBER() + "\",\"profile_id\":\"" + AppConstants.getProfileID() + "\"}";
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

                    JSONObject responseObject = jsonResponse.has("response") ? jsonResponse.getJSONObject("response") : null;

                    JSONArray tableJsonArray = responseObject.getJSONArray("Table1");
                    if (tableJsonArray.length() > 0) {
                        JSONObject jsonObjectTable = tableJsonArray.getJSONObject(0);
                        response = jsonObjectTable.has("Column1") ? jsonObjectTable.getString("Column1") : "";
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
                        AlertDialogMethod.alertDialogOk(ChequebookRequestActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(ChequebookRequestActivity.this, this.error, "", getResources().getString(R.string.btn_ok),
                                1, false, alertDialogOkListener);
                    }
                } else {
                    if (response != null) {

                        String message = "Chequebook request created successfully for request id: " + response;
                        AlertDialogMethod.alertDialogOk(ChequebookRequestActivity.this, "", message, getResources().getString(R.string.btn_ok),
                                2, false, alertDialogOkListener);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void clearAllFields() {
        try {
            selectAccSpinner.setSelection(0);
            checkbookqueSpinnerId.setSelection(0);
            leafCountSpinnerId.setSelection(0);
            adapterLeaf.clear();
            adapterChequeBookRequest.clear();
            remarkEditId.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(ChequebookRequestActivity.this);
    }
}