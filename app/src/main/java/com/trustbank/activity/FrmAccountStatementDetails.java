package com.trustbank.activity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.trustbank.BuildConfig;
import com.trustbank.Model.AccountStatementModel;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustFileUtils;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Security;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class FrmAccountStatementDetails extends AppCompatActivity implements AlertDialogListener {
    private static final String TAG = FrmAccountStatementDetails.class.getSimpleName();

    private TextView textViewName, textViewAccountTitle, textViewClosingBalance;
    private TrustMethods trustMethods;
    private ArrayList<AccountStatementModel> accountStatementDetailsList = new ArrayList<AccountStatementModel>();
    private AccountStatementModel accountStatementModel;
    private TableLayout tableLayout;
    public String closingBalance = "";
    public String openingBalanceValue = "", narrationText;
    private FloatingActionMenu menuBtn;
    private FloatingActionButton floatingLogOutBtn;
    private FloatingActionButton floatingMenuBtn;
    private TextView tv_branch_name;
    private TextView tv_ifsc_code;
    private TextView tv_account_number;
    private String name = "";
    private String branchName = "";
    private String bankName = "";
    private String ifsc_code = "";
    private String accountNo = "";
    private String accountTitle = "";
    private String mFromDate = "";
    private String mToDate = "";

    private FloatingActionButton floatingdownloadPdfBtn_Id;
    private FloatingActionButton floatingdownloadExcel_Id;
    private File file;
    private TextView textViewFromDate;
    private TextView textViewToDate;
    private String dateFrom;
    private String dateTo;
    private String customerId;
    private TextView textViewCustomerId;
    private LinearLayout linear_ifsc;
    private AlertDialogListener alertDialogOkListener = this;
    private TextView textViewBankNameId, textOpeningBalance, textOpeningBalanceId;
    private LinearLayout openingBalanceLinear;

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
                        TrustMethods.naviagteToSplashScreen(FrmAccountStatementDetails.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(FrmAccountStatementDetails.this, false);
        setContentView(R.layout.activity_frm_enqiry_details);
        try {
            init();

            if (getIntent().getExtras() != null) {
                Intent i = getIntent();
                accountNo = i.getStringExtra("accountNo");

                mFromDate = getIntent().getStringExtra("fromDate");
                mToDate = getIntent().getStringExtra("toDate");

                dateFrom = TrustMethods.formatDate(mFromDate, "dd/MM/yyyy", "yyyy-MM-dd");
                dateTo = TrustMethods.formatDate(mToDate, "dd/MM/yyyy", "yyyy-MM-dd");

                if (NetworkUtil.getConnectivityStatus(FrmAccountStatementDetails.this)) {
                    new AsyncTaskEnquiryDetails(accountNo, dateFrom, dateTo).execute();
                } else {
                    TrustMethods.message(FrmAccountStatementDetails.this, getResources().getString(R.string.error_check_internet));
                }

            }
            menuBtn.setOnMenuButtonClickListener(v -> {
                if (menuBtn.isOpened()) {
                }
                menuBtn.toggle(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        trustMethods = new TrustMethods(FrmAccountStatementDetails.this);
        trustMethods.activityOpenAnimation();
        textViewName = findViewById(R.id.textViewNameEnq);
        textViewAccountTitle = findViewById(R.id.textViewAccountTitle);
        tv_branch_name = findViewById(R.id.tv_branch_name);
        linear_ifsc = findViewById(R.id.linear_ifsc);
        tv_ifsc_code = findViewById(R.id.tv_ifsc_code);
        textViewClosingBalance = findViewById(R.id.textViewClosingBalance_Id);
        textViewCustomerId = findViewById(R.id.textViewCustomerId);
        textViewFromDate = findViewById(R.id.textViewFromDate);
        textViewToDate = findViewById(R.id.textViewDate);
        textViewBankNameId = findViewById(R.id.textViewBankNameId);

        tv_account_number = findViewById(R.id.tv_account_number);
        textOpeningBalance = findViewById(R.id.textOpeningBalance);
        textOpeningBalanceId = findViewById(R.id.textOpeningBalanceId);
        openingBalanceLinear = findViewById(R.id.openingBalanceLinearId);
        tableLayout = findViewById(R.id.table1);
        tableLayout.removeAllViewsInLayout();
        menuBtn = findViewById(R.id.menuBtn_Id);

        floatingLogOutBtn = findViewById(R.id.floatingLogOutBtn_Id);
        floatingMenuBtn = findViewById(R.id.floatingMenuBtn_Id);
        floatingdownloadPdfBtn_Id = findViewById(R.id.floatingdownloadPdfBtn_Id);
        floatingdownloadExcel_Id = findViewById(R.id.floatingdownloadExcel_Id);

        floatingLogOutBtn.setOnClickListener(clickListener);
        floatingMenuBtn.setOnClickListener(clickListener);
        floatingdownloadPdfBtn_Id.setOnClickListener(clickListener);
        floatingdownloadExcel_Id.setOnClickListener(clickListener);
        menuBtn.setClosedOnTouchOutside(true);

        if (getPackageName().equals("com.trustbank.pdccbank")) {
            floatingdownloadExcel_Id.setVisibility(View.GONE);
        }

    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {

                case R.id.floatingdownloadPdfBtn_Id:
                    String message = getResources().getString(R.string.alert_pdf_message_short);
                    AlertDialogMethod.alertDialog(FrmAccountStatementDetails.this, getResources().getString(R.string.app_name), message, getResources().getString(R.string.btn_ok), "", -1, true, alertDialogOkListener);
                    break;

                case R.id.floatingdownloadExcel_Id:
                    new AccountLedgerReportAsyncTask(AppConstants.getUSERNAME(), branchName, ifsc_code, accountNo,
                            accountTitle, closingBalance, mToDate, mFromDate, customerId, "", "", "", "", "", accountTitle, "", "", "", accountStatementDetailsList).execute();
                    break;

            }
        }
    };

    @Override
    public void onDialogOk(int resultCode) {
        switch (resultCode) {
            case -1:
                new AccountLedgerPDFReportAsyncTask(AppConstants.getUSERNAME(), branchName, ifsc_code, accountNo,
                        accountTitle, closingBalance, mToDate, mFromDate, customerId, "", "",
                        "", "", "", accountTitle, "",
                        "", "", accountStatementDetailsList, narrationText, openingBalanceValue).execute();
                break;

            case 0:
                try {

                    /*if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + AppConstants.STATEMENTS + AppConstants.PDF_STATEMENT_FILE_NAME);
                    }else {
                        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AppConstants.STATEMENTS + AppConstants.PDF_STATEMENT_FILE_NAME);
                    }*/

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AppConstants.STATEMENTS + AppConstants.PDF_STATEMENT_FILE_NAME);
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                     //   uri = FileProvider.getUriForFile(this, "com.trustbank.fileprovider", file);
                        uri = FileProvider.getUriForFile(this,  BuildConfig.APPLICATION_ID+".fileprovider", file);

                        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    } else {
                        uri = Uri.fromFile(file);
                    }

                    intent.setDataAndType(uri, "application/pdf");

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    //New lines added for pdf view
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent chooserIntent = Intent.createChooser(intent, "Open Report");

                    startActivity(chooserIntent);

                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(FrmAccountStatementDetails.this, "File does not Support,Please Install PDF Viewer", Toast.LENGTH_SHORT).show();
                }
                break;

            case 1:
                try {
                   /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                         file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + AppConstants.STATEMENTS + AppConstants.EXCEL_STATEMENT_FILE_NAME);
                    }else {
                         file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AppConstants.STATEMENTS + AppConstants.EXCEL_STATEMENT_FILE_NAME);
                    }*/

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                      // uri = FileProvider.getUriForFile(this, "com.trustbank.fileprovider", file);
                        uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider", file);

                        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    } else {
                        uri = Uri.fromFile(file);
                    }

                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //New lines added for pdf view
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(FrmAccountStatementDetails.this, "File does not Support,Please Install Excel Viewer", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                Intent intent = new Intent(FrmAccountStatementDetails.this, LockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                trustMethods.activityCloseAnimation();
                break;

            case 3:
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onDialogCancel(int resultCode) {
    }


    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskEnquiryDetails extends AsyncTask<Void, Void, String> {
        private ProgressDialog pDialog;
        private String error;
        private Context ctx;
        private String response;
        private String mAccountId;
        private String fromDate;
        private String toDate;
        private String mOrglementId;
        private String amountDr = null;
        private String amountCr = null;
        private String trainingate;
        private String chequeDate;
        private String chequeNumber;
        private String narration;
        private String modeoftransaction;
        private String trNo;
        private String branchCode = null;
        private String valueDate;
        private String description;
        private String rowid;
        private String actionName = "GET_ACC_STATEMENT";
        private String errorCode;

        public AsyncTaskEnquiryDetails(String accountNo, String dateFrom, String dateTo) {
            this.error = "";
            this.mAccountId = accountNo;
            this.fromDate = dateFrom;
            this.toDate = dateTo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(FrmAccountStatementDetails.this);
            pDialog.setMessage("Loading Account Statement Details....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                String url = TrustURL.MobileNoVerifyUrl();

                String jsonString = "{\"accountno\":\"" + mAccountId + "\",\"fromdate\":\"" + fromDate + "\"," + "\"todate\":\"" + toDate + "\"}";  //TODO
                TrustMethods.LogMessage("", "json string for change pass : " + jsonString);


                if (!url.equals("")) {
                    response = HttpClientWrapper.postWithActionAuthToken(url, jsonString, actionName, AppConstants.getAuth_token());
                }

                if (response == null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResult = (new JSONObject(response));

                if (jsonResult.has("error")) {
                    error = jsonResult.getString("error");
                    return error;
                }
                TrustMethods.LogMessage("jsonResult****", jsonResult.toString());

                String responseCode = jsonResult.has("response_code") ? jsonResult.getString("response_code") : "NA";

                if (responseCode.equals("1")) {

                    JSONObject validObject = (new JSONObject(jsonResult.getString("response")));

                    if (validObject.has("error")) {
                        error = validObject.getString("error");
                        return error;
                    }

                    JSONArray jsonarray = validObject.getJSONArray("Table1");
                    if (jsonarray.length() == 0) {
                        error = AppConstants.NO_RECORDS_FOUND;
                        return error;
                    }
                    for (int i = 0; i < jsonarray.length(); i++) {

                        JSONObject jsonObjectEnquiryDetailsList = jsonarray.getJSONObject(i);
                        trainingate = jsonObjectEnquiryDetailsList.getString("Tr Date");
                        amountDr = jsonObjectEnquiryDetailsList.getString("Withdrawal");
                        amountCr = jsonObjectEnquiryDetailsList.getString("Deposit");
                        chequeDate = jsonObjectEnquiryDetailsList.getString("Cheque Date");
                        chequeNumber = jsonObjectEnquiryDetailsList.getString("Cheque No");
                        narration = jsonObjectEnquiryDetailsList.getString("Narration");
                        closingBalance = jsonObjectEnquiryDetailsList.getString("Closing Balance");
                        if (i == 0) {
                            openingBalanceValue = closingBalance;
                            narrationText = narration;
                        }

                        modeoftransaction = jsonObjectEnquiryDetailsList.getString("ModeOfTransaction");
                        valueDate = jsonObjectEnquiryDetailsList.getString("Value Date");
                        trNo = jsonObjectEnquiryDetailsList.getString("Tr No");
                        if (jsonObjectEnquiryDetailsList.has("Code")) {
                            branchCode = jsonObjectEnquiryDetailsList.getString("Code");
                        }

                        description = jsonObjectEnquiryDetailsList.getString("Description");
                        rowid = jsonObjectEnquiryDetailsList.getString("rowId");

                        accountStatementModel = new AccountStatementModel();
                        if (!amountCr.equals("null")) {
                            String crAmount = amountCr + " Cr";
                            TrustMethods.LogMessage(TAG, "crAmount" + crAmount);
                            accountStatementModel.setAmount(crAmount);
                            accountStatementModel.setCreditAmount(amountCr);
                        }
                        if (!amountDr.equals("null")) {
                            String drAmount = amountDr + " Dr";
                            TrustMethods.LogMessage(TAG, "drAmount" + drAmount);
                            accountStatementModel.setAmount(drAmount);
                            accountStatementModel.setDebitAmount(amountDr);
                        }
                        if (!trainingate.equals("null")) {
                            accountStatementModel.setDate(trainingate);
                        }
                        if (!narration.equals("null")) {
                            if (!chequeNumber.equals("null") && !(chequeNumber.equals("0"))) {
                                accountStatementModel.setNarration(chequeNumber + "-" + narration);
                            } else {
                                accountStatementModel.setNarration(narration);
                            }
                        }

                        if (!TextUtils.isEmpty(closingBalance) && !closingBalance.equalsIgnoreCase("null")) {
                            accountStatementModel.setClosingBalance(closingBalance);
                        } else {
                            accountStatementModel.setClosingBalance("");
                        }

                        if (!TextUtils.isEmpty(modeoftransaction) && !modeoftransaction.equalsIgnoreCase("null")) {
                            accountStatementModel.setModeOfTransaction(modeoftransaction);
                        } else {
                            accountStatementModel.setModeOfTransaction("");
                        }

                        if (!TextUtils.isEmpty(trNo) && !trNo.equalsIgnoreCase("null")) {
                            accountStatementModel.setTransactionNumber(trNo);
                        } else {
                            accountStatementModel.setTransactionNumber("");
                        }

                        if (!TextUtils.isEmpty(branchCode) && !branchCode.equalsIgnoreCase("null")) {
                            accountStatementModel.setBranchCode(branchCode);
                        } else {
                            accountStatementModel.setBranchCode("");
                        }
                        if (!TextUtils.isEmpty(valueDate) && !valueDate.equalsIgnoreCase("null")) {
                            accountStatementModel.setValueDate(valueDate);
                        } else {
                            accountStatementModel.setValueDate("");
                        }
                        if (!TextUtils.isEmpty(description) && !description.equalsIgnoreCase("null")) {
                            accountStatementModel.setDescription(description);
                        } else {
                            accountStatementModel.setDescription("");
                        }
                        if (!TextUtils.isEmpty(rowid) && !rowid.equalsIgnoreCase("null")) {
                            accountStatementModel.setRowId(rowid);
                        } else {
                            accountStatementModel.setRowId("");
                        }
                        if (!TextUtils.isEmpty(chequeNumber) && !chequeNumber.equalsIgnoreCase("null")) {
                            accountStatementModel.setChequeNumber(chequeNumber);
                        } else {
                            accountStatementModel.setChequeNumber("");
                        }
                        accountStatementDetailsList.add(accountStatementModel);
                    }

                    JSONArray accountInfo = validObject.getJSONArray("Table");
                    JSONObject accountJsonObject = accountInfo.getJSONObject(0);
                    name = accountJsonObject.getString("ClientName");
                    branchName = accountJsonObject.getString("Name");
                    ifsc_code = accountJsonObject.getString("IFSCCode");
                    accountTitle = accountJsonObject.getString("HeadName");
                    customerId = accountJsonObject.getString("ClientId");

                    JSONArray bankNamArray = validObject.has("Table2") ? validObject.getJSONArray("Table2") : null;
                    if (bankNamArray != null) {
                        JSONObject bankNameObject = bankNamArray.getJSONObject(0);
                        bankName = bankNameObject.has("value") ? bankNameObject.getString("value") : "";
                    }

                } else {
                    errorCode = jsonResult.has("error_code") ? jsonResult.getString("error_code") : "NA";
                    error = jsonResult.has("error_message") ? jsonResult.getString("error_message") : "NA";
                    return error;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return error;
            } catch (Exception ex) {
                error = ex.getMessage();
                return error;
            }
            return response;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String value) {
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                if (this.error != "" && !TextUtils.isEmpty(error)) {

                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialog(FrmAccountStatementDetails.this, getResources().getString(R.string.app_name), getResources().getString(R.string.error_session_expire), getResources().getString(R.string.btn_ok), "", 2, false, alertDialogOkListener);
                    } else if (!TextUtils.isEmpty(error) && TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialog(FrmAccountStatementDetails.this, getResources().getString(R.string.app_name), getResources().getString(R.string.error_session_expire), getResources().getString(R.string.btn_ok), "", 2, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialog(FrmAccountStatementDetails.this, getResources().getString(R.string.app_name), this.error, getResources().getString(R.string.btn_ok), "", 3, false, alertDialogOkListener);
                    }

                } else {
                    TrustMethods.LogMessage(TAG, "closing balance on post-->" + closingBalance);
                    textViewClosingBalance.setText(" ₹ " + TrustMethods.trimWithPrefixCommsepareted(closingBalance));
                    if (!TextUtils.isEmpty(narrationText) && narrationText.equalsIgnoreCase("Opening Balance")) {
                        openingBalanceLinear.setVisibility(View.VISIBLE);
                        textOpeningBalance.setText(" ₹ " + TrustMethods.trimWithPrefixCommsepareted(openingBalanceValue));
                        textOpeningBalanceId.setText(narrationText);
                    } else {
                        openingBalanceLinear.setVisibility(View.GONE);
                    }

                    loadData();
                    setTextValue();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setTextValue() {
        textViewName.setText(name);
        textViewAccountTitle.setText(accountTitle);
        tv_branch_name.setText(branchName);
        tv_ifsc_code.setText(ifsc_code);
        textViewCustomerId.setText(customerId);
        textViewFromDate.setText(mFromDate);
        textViewToDate.setText(mToDate);
        tv_account_number.setText(accountNo);
        textViewBankNameId.setText(bankName);
    }

    public void loadData() {
        try {
            ArrayList<AccountStatementModel> accountStatementDetailsList1 = new ArrayList<AccountStatementModel>();
            AccountStatementModel accountStatementModel = new AccountStatementModel();
            String date = "Date";
            accountStatementModel.setDate(date);
            String amount = "Amount";
            accountStatementModel.setAmount(amount);
            String narrattion = "Narration";
            accountStatementModel.setNarration(narrattion);
            String balance = "Balance";
            accountStatementModel.setClosingBalance(balance);
            accountStatementDetailsList1.add(accountStatementModel);


            accountStatementDetailsList1.addAll(accountStatementDetailsList);
            for (int i = 0; i < accountStatementDetailsList1.size(); i++) {

                TableRow tableRow = new TableRow(FrmAccountStatementDetails.this);
                TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);

                int leftMargin = 15;
                int topMargin = 10;
                int rightMargin = 15;
                int bottomMargin = 10;

                tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

                tableRow.setLayoutParams(tableRowParams);

                tableRow.setId(i);

                if (accountStatementDetailsList1.get(i).getDate() != null) {
                    String trainingDate = accountStatementDetailsList1.get(i).getDate();
                    if (!trainingDate.equalsIgnoreCase("Date")) {

                        TextView textViewDate = new TextView(FrmAccountStatementDetails.this, null, 0);
                        textViewDate.setTextColor(getResources().getColor(R.color.black));
                        textViewDate.setTypeface(null, Typeface.BOLD);
                        textViewDate.setTextSize(14);
                        textViewDate.setText(trainingDate);
                        textViewDate.setFreezesText(true);
                        textViewDate.setGravity(Gravity.START);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            textViewDate.setWidth(250);
                        } else {
                            textViewDate.setWidth(170);
                        }
                        tableRow.addView(textViewDate);
                    } else {
                        TextView textViewDate = new TextView(FrmAccountStatementDetails.this, null, 0);
                        textViewDate.setTextSize(16);
                        textViewDate.setText(trainingDate);
                        textViewDate.setTextColor(getResources().getColor(R.color.black));
                        textViewDate.setTypeface(null, Typeface.BOLD);
                        textViewDate.setFreezesText(true);
                        textViewDate.setGravity(Gravity.START);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            textViewDate.setWidth(250);
                        } else {
                            textViewDate.setWidth(170);
                        }

                        tableRow.addView(textViewDate);
                    }
                }


                TextView textViewAmount = null;

                if (i == 0) {
                    textViewAmount = new TextView(FrmAccountStatementDetails.this, null, 0);
                    textViewAmount.setText(accountStatementDetailsList1.get(i).getAmount());
                    textViewAmount.setTextSize(16);
                    textViewAmount.setTypeface(null, Typeface.BOLD);
                    textViewAmount.setGravity(Gravity.CENTER);

                } else {
                    if (!TextUtils.isEmpty(accountStatementDetailsList1.get(i).getAmount()) && accountStatementDetailsList1.get(i).getAmount() != null) {
                        if (accountStatementDetailsList1.get(i).getAmount().contains("Dr")) {
                            textViewAmount = new TextView(FrmAccountStatementDetails.this, null, 0);
                            textViewAmount.setTextColor(getResources().getColor(R.color.pinkColor));

                            textViewAmount.setText(TrustMethods.trimWithPrefixCommsepareted(accountStatementDetailsList1.get(i).getAmount()));
                        } else {
                            textViewAmount = new TextView(FrmAccountStatementDetails.this, null, 0);
                            textViewAmount.setTextColor(getResources().getColor(R.color.colorGreen));
                            textViewAmount.setText(TrustMethods.trimWithPrefixCommsepareted(accountStatementDetailsList1.get(i).getAmount()));
                        }
                    } else {
                        textViewAmount = new TextView(FrmAccountStatementDetails.this, null, 0);
                        textViewAmount.setTextColor(getResources().getColor(R.color.pinkColor));
                        textViewAmount.setText("");
                    }
                    textViewAmount.setTextSize(14);
                    textViewAmount.setTypeface(null, Typeface.BOLD);
                    textViewAmount.setGravity(Gravity.RIGHT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        textViewAmount.setForegroundGravity(Gravity.RIGHT);
                    }
                    textViewAmount.setPadding(0, 0, 50, 0);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textViewAmount.setWidth(400);
                } else {
                    textViewAmount.setWidth(300);
                }

                tableRow.addView(textViewAmount);

                TextView textViewBalance = null;
                if (i == 0) {
                    textViewBalance = new TextView(FrmAccountStatementDetails.this, null, 0);
                    textViewBalance.setText(accountStatementDetailsList1.get(i).getClosingBalance());
                    textViewBalance.setTextSize(16);
                    textViewBalance.setTypeface(null, Typeface.BOLD);
                    textViewBalance.setGravity(Gravity.CENTER);
                    textViewBalance.setPadding(0, 0, 0, 0);
                } else {

                    if (!TextUtils.isEmpty(accountStatementDetailsList1.get(i).getClosingBalance()) && !accountStatementDetailsList1.get(i).getClosingBalance().equalsIgnoreCase("null")) {
                        if (accountStatementDetailsList1.get(i).getClosingBalance().contains("Dr")) {
                            textViewBalance = new TextView(FrmAccountStatementDetails.this, null, 0);
                            textViewBalance.setTextColor(getResources().getColor(R.color.pinkColor));
                            textViewBalance.setText(TrustMethods.trimWithPrefixCommsepareted(accountStatementDetailsList1.get(i).getClosingBalance()));
                        } else {
                            textViewBalance = new TextView(FrmAccountStatementDetails.this, null, 0);
                            textViewBalance.setTextColor(getResources().getColor(R.color.colorGreen));
                            textViewBalance.setText(TrustMethods.trimWithPrefixCommsepareted(accountStatementDetailsList1.get(i).getClosingBalance()));
                        }
                        textViewBalance.setTypeface(null, Typeface.BOLD);
                    } else {
                        textViewBalance = new TextView(FrmAccountStatementDetails.this, null, 0);
                        textViewBalance.setText("");
                    }
                    textViewBalance.setGravity(Gravity.END);
                    textViewBalance.setPadding(0, 0, 50, 0);

                }
                textViewBalance.setFreezesText(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textViewBalance.setWidth(400);
                } else {
                    textViewBalance.setWidth(300);
                }

                tableRow.addView(textViewBalance);


                //===========narration===========//

                TextView textViewNarration = null;
                if (i == 0) {
                    textViewNarration = new TextView(FrmAccountStatementDetails.this, null, 0);
                    textViewNarration.setText(accountStatementDetailsList1.get(i).getNarration());
                    textViewNarration.setTextSize(16);
                    textViewNarration.setTypeface(null, Typeface.BOLD);

                } else {
                    if (!TextUtils.isEmpty(accountStatementDetailsList1.get(i).getNarration()) && !accountStatementDetailsList1.get(i).getNarration().equalsIgnoreCase("null")) {
                        textViewNarration = new TextView(FrmAccountStatementDetails.this, null, 0);
                        textViewNarration.setTextSize(14);
                        textViewNarration.setTextColor(getResources().getColor(R.color.greenColor));
                        textViewNarration.setText(accountStatementDetailsList1.get(i).getNarration());
                    } else {
                        textViewNarration = new TextView(FrmAccountStatementDetails.this, null, 0);
                        textViewNarration.setText("");
                    }

                    textViewNarration.setFreezesText(true);
                    textViewNarration.setTypeface(null, Typeface.BOLD);
                    textViewNarration.setTextColor(getResources().getColor(R.color.black));
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textViewNarration.setWidth(800);
                } else {
                    textViewNarration.setWidth(600);
                }
                textViewNarration.setPadding(40, 0, 0, 0);
                textViewNarration.setGravity(Gravity.START);
                tableRow.addView(textViewNarration);

                tableLayout.addView(tableRow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        table.addCell(cell);

    }


    @SuppressLint("StaticFieldLeak")
    public class AccountLedgerReportAsyncTask extends AsyncTask<String, String, String> {

        private final String strStateName;
        String TAG = AccountLedgerReportAsyncTask.class.getSimpleName();

        WorkbookSettings wbSettings;
        WritableWorkbook workbook;
        WritableSheet sheet;
        File fileExcel;
        String mName;
        String mBranchName;
        String mIfscCode;
        String mAccountNo;
        String accountTitle;
        String closingBalance;
        ArrayList<AccountStatementModel> mAccountStatementDetailsList;
        String result = null;
        private ProgressDialog progressDialog;
        String toDate;
        String fromDate;
        String customerId;
        private String strSpousename;
        private String strAddress;
        private String strFathername;
        private String straAccounttype;
        private String strCityname;
        private String strAccounttypecode;
        private String strBranchCode;
        private String strMotherName;

        public AccountLedgerReportAsyncTask(String name, String branchName, String ifsc_code, String accountNo, String accountTitle, String closingBalance, String toDate, String fromDate, String customerId, String strSpousename, String strAddress, String strStateName, String strFathername, String strMotherName, String straAccounttype, String strCityname, String strAccounttypecode, String strBranchCode, ArrayList<AccountStatementModel> accountStatementDetailsList) {

            this.mName = name;
            this.mBranchName = branchName;
            this.mIfscCode = ifsc_code;
            this.mAccountNo = accountNo;
            this.accountTitle = accountTitle;
            this.closingBalance = closingBalance;
            this.toDate = toDate;
            this.fromDate = fromDate;
            this.customerId = customerId;
            this.strSpousename = strSpousename;
            this.strAddress = strAddress;
            this.strFathername = strFathername;
            this.straAccounttype = straAccounttype;
            this.strCityname = strCityname;
            this.strAccounttypecode = strAccounttypecode;
            this.strBranchCode = strBranchCode;
            this.strMotherName = strMotherName;
            this.strStateName = strStateName;
            this.mAccountStatementDetailsList = accountStatementDetailsList;
            wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(FrmAccountStatementDetails.this);
            progressDialog.setMessage("Loading...Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
             /*   fileExcel = TrustFileUtils.createFilePath(FrmAccountStatementDetails.this, AppConstants.EXCEL_STATEMENT_FILE_NAME,
                        AppConstants.STATEMENTS);*/

                String outpath;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    //outpath = Environment.getExternalStorageDirectory().getPath() + "/Download/MPassbook/Statements/AccountStatement"+ System.currentTimeMillis() +".pdf";
                    outpath = Environment.getExternalStorageDirectory().getPath() + "/Download/AccountStatement"+ System.currentTimeMillis() +".xls";
                    file = new File(outpath);
                }else {
                    file = TrustFileUtils.createFilePath(FrmAccountStatementDetails.this, AppConstants.EXCEL_STATEMENT_FILE_NAME,
                            AppConstants.STATEMENTS);
                }


                workbook = Workbook.createWorkbook(file, wbSettings);

                // font change
                WritableFont font1 = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);

                WritableFont font = new WritableFont(WritableFont.TIMES, 14, WritableFont.BOLD);

                WritableCellFormat format1 = new WritableCellFormat(font1);
                WritableCellFormat format = new WritableCellFormat(font);
                sheet = workbook.createSheet(AppConstants.STATEMENT_SHEET, 0);

                Label lablebankName = new Label(1, 1, bankName, format);
                Label labelTitle = new Label(1, 3, "Account Statement", format1);

                Label labelTitleName = new Label(0, 5, "Name:- " + mName);
                Label labelTitleName1 = new Label(0, 6, "Branch Name:- " + mBranchName);
                Label labelTitleName2 = new Label(0, 7, "Account Number:- " + mAccountNo);
                Label labelTitleName4 = new Label(0, 8, "Account Title:- " + accountTitle);
                Label labelTitleName5 = new Label(0, 9, "Customer Id:- " + customerId);


                Label labelTitleName13 = new Label(0, 10, "From Date:- " + fromDate);
                Label labelTitleName12 = new Label(0, 11, "To Date:- " + toDate);

                Label labelTitleDt = new Label(0, 20, "TXN Date", format1);
                Label labelTitlValueDate = new Label(1, 20, "Value Date", format1);
                Label labelTitlNarration = new Label(2, 20, "Narration", format1);
                Label labelTitleReferenceNo = new Label(3, 20, "ReferenceNo/ChequeNo", format1);
                Label labelTitleMode = new Label(4, 20, "Tr Mode", format1);
                Label labelTitleBranchCode = new Label(5, 20, "Branch Code", format1);
                Label labelTitleAmount = new Label(6, 20, "Debit", format1);
                Label labelTitleCredit = new Label(7, 20, "Credit", format1);
                Label labelTitleBalance = new Label(8, 20, "Balance", format1);
                try {
                    sheet.addCell(labelTitleName);
                    sheet.addCell(labelTitle);
                    sheet.addCell(labelTitleName1);
                    sheet.addCell(labelTitleName2);
                    sheet.addCell(labelTitleName4);
                    sheet.addCell(labelTitleName5);
                    sheet.addCell(lablebankName);

                    sheet.addCell(labelTitleName12);
                    sheet.addCell(labelTitleName13);

                    sheet.addCell(labelTitleDt);
                    sheet.addCell(labelTitlValueDate);
                    sheet.addCell(labelTitlNarration);
                    sheet.addCell(labelTitleReferenceNo);
                    sheet.addCell(labelTitleMode);
                    sheet.addCell(labelTitleBranchCode);
                    sheet.addCell(labelTitleAmount);
                    sheet.addCell(labelTitleCredit);
                    sheet.addCell(labelTitleBalance);
                } catch (WriteException e) {
                    e.printStackTrace();
                }

                sheet.setColumnView(0, 20);
                sheet.setColumnView(1, 20);
                sheet.setColumnView(2, 80);
                sheet.setColumnView(3, 20);
                sheet.setColumnView(4, 20);
                sheet.setColumnView(5, 20);
                sheet.setColumnView(6, 20);
                sheet.setColumnView(7, 30);
                sheet.setColumnView(8, 30);


                for (int i = 0; i < mAccountStatementDetailsList.size(); i++) {
                    Label labelSubName = null;
                    try {
                        if (!TextUtils.isEmpty(mAccountStatementDetailsList.get(i).getDate()) && mAccountStatementDetailsList.get(i).getDate() != null) {
                            String trDateString = mAccountStatementDetailsList.get(i).getDate();
                            TrustMethods.LogMessage(TAG, "trDateString1-->" + trDateString);
                            labelSubName = new Label(0, 21 + i, trDateString);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Label labelValueDate = null;
                    try {
                        if (!TextUtils.isEmpty(mAccountStatementDetailsList.get(i).getValueDate()) && mAccountStatementDetailsList.get(i).getValueDate() != null) {
                            String valueDateString = mAccountStatementDetailsList.get(i).getValueDate();

                            labelValueDate = new Label(1, 21 + i, valueDateString);
                        } else {
                            labelValueDate = new Label(1, 21 + i, "");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Label labelOpening = new Label(2, 21 + i, !TextUtils.isEmpty(mAccountStatementDetailsList.get(i).getNarration()) ? mAccountStatementDetailsList.get(i).getNarration() : "");
                    Label labelChequeNumber = new Label(3, 21 + i, !TextUtils.isEmpty(mAccountStatementDetailsList.get(i).getChequeNumber()) ? mAccountStatementDetailsList.get(i).getChequeNumber() : "");
                    Label labelModeOfTras = new Label(4, 21 + i, !TextUtils.isEmpty(mAccountStatementDetailsList.get(i).getModeOfTransaction()) ? mAccountStatementDetailsList.get(i).getModeOfTransaction() : "");
                    Label labelBranchCode = new Label(5, 21 + i, !TextUtils.isEmpty(mAccountStatementDetailsList.get(i).getBranchCode()) ? mAccountStatementDetailsList.get(i).getBranchCode() : "");
                    Label labelDebit = new Label(6, 21 + i, !TextUtils.isEmpty(mAccountStatementDetailsList.get(i).getDebitAmount()) ? mAccountStatementDetailsList.get(i).getDebitAmount() : "-");
                    Label labelCredit = new Label(7, 21 + i, !TextUtils.isEmpty(mAccountStatementDetailsList.get(i).getCreditAmount()) ? mAccountStatementDetailsList.get(i).getCreditAmount() : "-");

                    Label labelBalance = new Label(8, 21 + i, !TextUtils.isEmpty(mAccountStatementDetailsList.get(i).getClosingBalance()) ? mAccountStatementDetailsList.get(i).getClosingBalance() : "");

                    try {
                        sheet.addCell(labelSubName);
                        sheet.addCell(labelValueDate);
                        sheet.addCell(labelOpening);
                        sheet.addCell(labelChequeNumber);
                        sheet.addCell(labelModeOfTras);
                        sheet.addCell(labelBranchCode);
                        sheet.addCell(labelCredit);
                        sheet.addCell(labelDebit);
                        sheet.addCell(labelBalance);
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                }

                Label labelClsBalance = new Label(8, 24 + mAccountStatementDetailsList.size(), "Closing Balance:- " + TrustMethods.getValueCommaSeparated(closingBalance), format1);
                try {
                    sheet.addCell(labelClsBalance);
                } catch (WriteException e) {
                    e.printStackTrace();
                }
                workbook.write();
                result = "success";
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    if (workbook != null) {
                        workbook.close();
                    }
                } catch (IOException | WriteException e1) {
                    e1.printStackTrace();
                }
            } finally {
                try {
                    if (workbook != null) {
                        workbook.close();
                    }
                } catch (IOException | WriteException e1) {
                    e1.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String success) {
            super.onPostExecute(success);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            try {
                if (TextUtils.isEmpty(result)) {
                } else if (result.equals("success")) {

                    AlertDialogMethod.alertDialog(FrmAccountStatementDetails.this, getResources().getString(R.string.app_name), getResources().getString(R.string.alert_excel_message), getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), 1, false, alertDialogOkListener);

                } else {
                    TrustMethods.message(FrmAccountStatementDetails.this, "Something Went Wrong, Please Try Again..");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AccountLedgerPDFReportAsyncTask extends AsyncTask<String, String, String> {
        private String strStateName;
        private File fileExcel;
        private String mName;
        private String mBranchName;
        private String mIfscCode;
        private String mAccountNo;
        private String accountTitle;
        private String closingBalance;
        private ArrayList<AccountStatementModel> mAccountStatementDetailsList;
        private String result = null;
        private ProgressDialog progressDialog;
        private String toDate;
        private String fromDate;
        private String customerId;
        private String strSpousename;
        private String strAddress;
        private String strFathername;
        private String straAccounttype;
        private String strCityname;
        private String strAccounttypecode;
        private String strBranchCode;
        private String strMotherName;
        private String openingBalanceTitle, openingBalanceValue;


        public AccountLedgerPDFReportAsyncTask(String name, String branchName,
                                               String ifsc_code, String accountNo,
                                               String accountTitle, String closingBalance,
                                               String toDate, String fromDate, String customerId,
                                               String strSpousename, String strAddress,
                                               String strStateName, String strFathername,
                                               String strMotherName, String straAccounttype,
                                               String strCityname, String strAccounttypecode,
                                               String strBranchCode, ArrayList<AccountStatementModel> accountStatementDetailsList, String openingBalanceTitle,
                                               String openingBalanceValue) {

            this.mName = name;
            this.mBranchName = branchName;
            this.mIfscCode = ifsc_code;
            this.mAccountNo = accountNo;
            this.accountTitle = accountTitle;
            this.closingBalance = closingBalance;
            this.toDate = toDate;
            this.fromDate = fromDate;
            this.customerId = customerId;
            this.strSpousename = strSpousename;
            this.strAddress = strAddress;
            this.strFathername = strFathername;
            this.straAccounttype = straAccounttype;
            this.strCityname = strCityname;
            this.strAccounttypecode = strAccounttypecode;
            this.strBranchCode = strBranchCode;
            this.strMotherName = strMotherName;
            this.strStateName = strStateName;
            this.mAccountStatementDetailsList = accountStatementDetailsList;
            this.openingBalanceValue = openingBalanceValue;
            this.openingBalanceTitle = openingBalanceTitle;
        }


        @Override
        protected String doInBackground(String... params) {


            try {
                Security.insertProviderAt(new BouncyCastleProvider(), 1);
                Document doc = new Document();
               /* String outpath;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + AppConstants.STATEMENTS);
                     outpath = file + AppConstants.PDF_STATEMENT_FILE_NAME;
                }else {
                    file = new File(Environment.getExternalStorageDirectory() + File.separator + AppConstants.STATEMENTS);
                     outpath = file + AppConstants.PDF_STATEMENT_FILE_NAME;
                    file.mkdirs();
                }*/

                String outpath;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    //outpath = Environment.getExternalStorageDirectory().getPath() + "/Download/MPassbook/Statements/AccountStatement"+ System.currentTimeMillis() +".pdf";
                    outpath = Environment.getExternalStorageDirectory().getPath() + "/Download/AccountStatement"+ System.currentTimeMillis() +".pdf";
                    file = new File(outpath);
                }else {
                    file = new File(Environment.getExternalStorageDirectory() + File.separator + AppConstants.STATEMENTS);
                    outpath = file + AppConstants.PDF_STATEMENT_FILE_NAME;
                    file.mkdirs();
                }


                OutputStream pdfOutputFile = new FileOutputStream(outpath);
                PdfWriter writer = PdfWriter.getInstance(doc, pdfOutputFile);

                String custId = AppConstants.CLIENTID;
                if (TextUtils.isEmpty(AppConstants.CLIENTID)){
                    custId = AppConstants.USERMOBILENUMBER;
                }

                writer.setEncryption(custId.getBytes(), custId.getBytes(),
                        PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);

                //create pdf writer instance
                Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, new BaseColor(0, 0, 0));
                Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 10);

                DecimalFormat df = new DecimalFormat("0.00");
                doc.addCreationDate();
                doc.addProducer();
                doc.setPageSize(PageSize.LETTER);
                doc.open();


                Font f = new Font(Font.FontFamily.TIMES_ROMAN, 25.0f, Font.BOLD, BaseColor.BLACK);
                Chunk c = new Chunk(bankName, f);
                Paragraph p = new Paragraph(c);
                p.setAlignment(Paragraph.ALIGN_CENTER);
                doc.add(p);

                Font f1 = new Font(Font.FontFamily.TIMES_ROMAN, 22.0f, Font.BOLD, BaseColor.BLACK);
                Chunk c1 = new Chunk("Account Statement \n", f1);
                Paragraph p1 = new Paragraph(c1);
                p1.setAlignment(Paragraph.ALIGN_CENTER);
                doc.add(p1);

                Font f2 = new Font(Font.FontFamily.TIMES_ROMAN, 22.0f, Font.BOLD, BaseColor.BLACK);
                Chunk c2 = new Chunk("", f2);
                Paragraph p2 = new Paragraph(c2);
                p2.setAlignment(Paragraph.ALIGN_CENTER);
                doc.add(p2);


                //=========add cell===
                Paragraph paragraph;
                //create a paragraph

                String openingBalance = "";
                if (!TextUtils.isEmpty(openingBalanceTitle) && openingBalanceTitle.equalsIgnoreCase("Opening Balance")) {
                    openingBalance = "\n" + openingBalanceTitle + " : " + openingBalanceValue;
                }

                if (!ifsc_code.equalsIgnoreCase("null")) {
                    paragraph = new Paragraph("Name : " + name + "\n" + "Branch Name : " + branchName + "\n" + "Account Number : " + accountNo +
                            "\n" + "IFSC Code : " + ifsc_code + "\n" + "Account Title : " + accountTitle + "\n" + "Customer Id : " + customerId + openingBalance + "\n" + "From Date : " + fromDate + "\n" + "To Date : " + toDate);
                } else {
                    paragraph = new Paragraph("Name : " + name + "\n" + "Branch Name : " + branchName + "\n" + "Account Number : " + accountNo +
                            "\n" + "Account Title :" + accountTitle
                            + "\n" + "Customer Id : " + customerId +
                            "\n" + "Father's : " + strFathername +
                            "\n" + "Mother's : " + strMotherName +
                            "\n" + "Spousename : " + strSpousename +
                            "\n" + "Address : " + strAddress +
                            "\n" + "City : " + strCityname +
                            "\n" + "State : " + strStateName + openingBalance +
                            "\n" + "From Date : " + fromDate + "\n" + "To Date : " + toDate);
                }


                //specify column widths
                float[] columnWidths = {4f, 4f, 6f, 4f, 4f, 4f, 4f, 4f, 4f};
                //create PDF table with the given widths
                PdfPTable table = new PdfPTable(9);
                // set table width a percentage of the page width
                table.setWidthPercentage(100f);

                //insert column headings
                insertCell(table, "TXN Date", Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, "Value Date", Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, "Narration", Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, "ReferenceNo/ChequeNo", Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, "Tr Mode", Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, "Br Code", Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, "Debit", Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, "Credit", Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, "Balance", Element.ALIGN_CENTER, 1, bfBold12);
                table.setHeaderRows(1);


                for (int x = 0; x < accountStatementDetailsList.size(); x++) {

                    if (!TextUtils.isEmpty(accountStatementDetailsList.get(x).getDate()) && accountStatementDetailsList.get(x).getDate() != null) {
                        String trainingDate = accountStatementDetailsList.get(x).getDate();
                        if (trainingDate != null) {
                            insertCell(table, trainingDate, Element.ALIGN_CENTER, 1, bf12);
                        } else {
                            insertCell(table, "", Element.ALIGN_CENTER, 1, bf12);
                        }
                    } else {
                        insertCell(table, "", Element.ALIGN_CENTER, 1, bf12);
                    }


                    if (!TextUtils.isEmpty(accountStatementDetailsList.get(x).getValueDate()) && accountStatementDetailsList.get(x).getValueDate() != null) {
                        String valueDate = accountStatementDetailsList.get(x).getValueDate();
                        if (valueDate != null) {
                            insertCell(table, valueDate, Element.ALIGN_CENTER, 1, bf12);
                        } else {
                            insertCell(table, "", Element.ALIGN_CENTER, 1, bf12);
                        }

                    } else {
                        insertCell(table, "", Element.ALIGN_CENTER, 1, bf12);
                    }


                    if (!TextUtils.isEmpty(accountStatementDetailsList.get(x).getNarration()) && accountStatementDetailsList.get(x).getNarration() != null) {
                        insertCell(table, accountStatementDetailsList.get(x).getNarration(), Element.ALIGN_CENTER, 1, bf12);
                    } else {
                        insertCell(table, "", Element.ALIGN_CENTER, 1, bf12);
                    }


                    if (!TextUtils.isEmpty(accountStatementDetailsList.get(x).getChequeNumber()) && accountStatementDetailsList.get(x).getChequeNumber() != null) {
                        insertCell(table, accountStatementDetailsList.get(x).getChequeNumber(), Element.ALIGN_CENTER, 1, bf12);
                    } else {
                        insertCell(table, "", Element.ALIGN_CENTER, 1, bf12);
                    }

                    if (!TextUtils.isEmpty(accountStatementDetailsList.get(x).getModeOfTransaction()) && accountStatementDetailsList.get(x).getModeOfTransaction() != null) {
                        insertCell(table, accountStatementDetailsList.get(x).getModeOfTransaction(), Element.ALIGN_CENTER, 1, bf12);
                    } else {
                        insertCell(table, "", Element.ALIGN_CENTER, 1, bf12);
                    }


                    if (!TextUtils.isEmpty(accountStatementDetailsList.get(x).getBranchCode()) && accountStatementDetailsList.get(x).getBranchCode() != null) {
                        insertCell(table, accountStatementDetailsList.get(x).getBranchCode(), Element.ALIGN_CENTER, 1, bf12);
                    } else {
                        insertCell(table, "", Element.ALIGN_CENTER, 1, bf12);
                    }


                    if (!TextUtils.isEmpty(accountStatementDetailsList.get(x).getDebitAmount()) && accountStatementDetailsList.get(x).getDebitAmount() != null) {

                        insertCell(table, accountStatementDetailsList.get(x).getDebitAmount(), Element.ALIGN_RIGHT, 1, bf12);

                    } else {
                        insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);
                    }


                    if (!TextUtils.isEmpty(accountStatementDetailsList.get(x).getCreditAmount()) && accountStatementDetailsList.get(x).getCreditAmount() != null) {

                        insertCell(table, accountStatementDetailsList.get(x).getCreditAmount(), Element.ALIGN_RIGHT, 1, bf12);

                    } else {
                        insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);
                    }


                    if (!TextUtils.isEmpty(accountStatementDetailsList.get(x).getClosingBalance()) && accountStatementDetailsList.get(x).getClosingBalance() != null) {
                        insertCell(table, accountStatementDetailsList.get(x).getClosingBalance(), Element.ALIGN_RIGHT, 1, bf12);
                    } else {
                        insertCell(table, "", Element.ALIGN_RIGHT, 1, bf12);
                    }
                }

                insertCell(table, "Closing Balance", Element.ALIGN_RIGHT, 3, bfBold12);
                insertCell(table, closingBalance, Element.ALIGN_RIGHT, 1, bfBold12);
                paragraph.add(table);
                doc.add(paragraph);
                doc.close();
                pdfOutputFile.close();

                result = "Success";

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;

        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(FrmAccountStatementDetails.this);
            progressDialog.setMessage("Loading...Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            try {
                if (TextUtils.isEmpty(result)) {
                } else if (result.equalsIgnoreCase("Success")) {

                    String message = getResources().getString(R.string.alert_pdf_message);
                    AlertDialogMethod.alertDialog(FrmAccountStatementDetails.this, getResources().getString(R.string.app_name), message, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), 0, false, alertDialogOkListener);
                } else {
                    TrustMethods.message(FrmAccountStatementDetails.this, "Something Went Wrong, Please Try Again..");
                }
            } catch (Exception e) {
                e.printStackTrace();

            }


        }
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(FrmAccountStatementDetails.this);
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
}