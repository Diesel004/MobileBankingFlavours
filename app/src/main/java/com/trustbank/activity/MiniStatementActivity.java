package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogListener;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.tcpconnection.tdto.MessageDtoBuilder;
import com.trustbank.tcpconnection.tmessage.TMessage;
import com.trustbank.tcpconnection.tmessage.TMessageUtil;
import com.trustbank.tcpconnection.util.ResponseEntity;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SessionManager;
import com.trustbank.util.SetTheme;
import com.trustbank.util.SharePreferenceUtils;
import com.trustbank.util.TrustFileUtils;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileNotFoundException;
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

public class MiniStatementActivity extends AppCompatActivity implements AlertDialogOkListener, AlertDialogListener {

    private String TAG = MiniStatementActivity.class.getSimpleName();
    private SharePreferenceUtils sharedPreferences;
    private SessionManager sessionManager;
    private TrustMethods method;
    private TextView txtAccNo;
    private Spinner selectAccSpinner;
    private CardView cardMiniStatement;
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private List<String> accountList;
    private AlertDialogOkListener alertDialogOkListener = this;
    private AlertDialogListener alertDialogListener = this;
    private FloatingActionMenu menuBtn;
//    String finalResponse;

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
                        TrustMethods.naviagteToSplashScreen(MiniStatementActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(MiniStatementActivity.this, false);
        setContentView(R.layout.activity_mini_statement);
        inIt();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }

    private void inIt() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPreferences = new SharePreferenceUtils(MiniStatementActivity.this);
        sessionManager = new SessionManager(MiniStatementActivity.this);
        method = new TrustMethods(MiniStatementActivity.this);
        selectAccSpinner = findViewById(R.id.selectAccSpinnerId);
        cardMiniStatement = findViewById(R.id.cardMiniStatementId);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        txtAccNo = findViewById(R.id.txtAccNoId);

        menuBtn = findViewById(R.id.menuBtn_Id);
        FloatingActionButton floatingdownloadPdfBtn = findViewById(R.id.floatingdownloadPdfBtn_Id);
        FloatingActionButton floatingdownloadExcel = findViewById(R.id.floatingdownloadExcel_Id);

        floatingdownloadPdfBtn.setOnClickListener(clickListener);
        floatingdownloadExcel.setOnClickListener(clickListener);
        menuBtn.setClosedOnTouchOutside(true);
        menuBtn.setVisibility(View.GONE);

        accountNoSpinner();
        setAccSpinner();

    }

    private void setAccSpinner() {
        selectAccSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position != 0) {
                    String accNo = TrustMethods.getValidAccountNo((String) adapterView.getItemAtPosition(position));
                    if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(MiniStatementActivity.this)) {
                        if (NetworkUtil.getConnectivityStatus(MiniStatementActivity.this)) {
                            new MiniStatementAsyncTask(MiniStatementActivity.this, accNo).execute();
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                        }
                    } else {
                        TrustMethods.displaySimErrorDialog(MiniStatementActivity.this);
                    }
                } else {
                    cardMiniStatement.setVisibility(View.GONE);
                    menuBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class MiniStatementAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        ProgressDialog pDialog;
        String mAccNo;
        String response;
        String result;
        String finalResponse;
        private String errorCode;

        public MiniStatementAsyncTask(Context ctx, String accNo) {
            this.ctx = ctx;
            this.mAccNo = accNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MiniStatementActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
             //   String jsonString = "{\"filter\":[\"stan\"]}";
                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(MiniStatementActivity.this, jsonString,
                        TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());
                String url = TrustURL.httpCallUrl();
                //Build Message
//                TMessageUtil.Init(AppConstants.INSTITUTION_ID, Constants.SERVER_IP, Constants.SERVER_PORT);
                MessageDtoBuilder msgDto = new MessageDtoBuilder();
                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {
                    TMessage requestXmlMsg = msgDto.GetMiniStatementDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(), AppConstants.getUSERMOBILENUMBER(), mAccNo,
                            "", AppConstants.getUSERNAME(), AppConstants.INSTITUTION_ID,generateStanRRNModel.getChannel_ref_no());//TMessageUtil.MSG_INSTITUTION_ID);
                    Log.d("msg.GetXml()" + "" + ":", requestXmlMsg.GetXml());

                    String base64EncodedRequestJson = Base64.encodeToString(requestXmlMsg.GetXml().getBytes(), Base64.NO_WRAP);

                    String jsonStringRequest = "{\"data\":\"" + base64EncodedRequestJson + "\"}";
                    TrustMethods.LogMessage("", "base64EncodedRequestJson : " + jsonStringRequest);

                    if (!url.equals("")) {
                        TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWitAuthHeader(url, jsonStringRequest, AppConstants.getAuth_token());
                        TrustMethods.LogMessage(TAG, "Frm Enquiry details response-->" + response);
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

                        ResponseEntity resParse = TMessage.ParseMessage(responseValue);
                        TMessage responseMsg = (TMessage) resParse.response;

                        if (responseMsg.ActCode.Value.equals("000")) {
                            finalResponse = responseMsg.Message.Value;
                        } else {
                            error = "Act Code: " + responseMsg.ActCode.Value + ": " + responseMsg.ActCodeDesc.Value;
                        }

                        onProgressUpdate();
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
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(MiniStatementActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                    cardMiniStatement.setVisibility(View.GONE);
                    menuBtn.setVisibility(View.GONE);
                } else {
                    if (finalResponse != null) {
                        txtAccNo.setText(finalResponse);
                        cardMiniStatement.setVisibility(View.VISIBLE);
                        menuBtn.setVisibility(View.VISIBLE);
                    } else {
                        cardMiniStatement.setVisibility(View.GONE);
                        menuBtn.setVisibility(View.GONE);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void accountNoSpinner() {
        try {
            accountsArrayList = method.getArrayList(MiniStatementActivity.this, "AccountListPref");
         //   Log.d("TAG", accountsArrayList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MiniStatementActivity.this, android.R.layout.simple_spinner_item, accountList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectAccSpinner.setAdapter(adapter);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                switch (view.getId()) {

                    case R.id.floatingdownloadPdfBtn_Id:

                        String message = getResources().getString(R.string.alert_pdf_message_short);
                        AlertDialogMethod.alertDialog(MiniStatementActivity.this, getResources().getString(R.string.app_name), message, getResources().getString(R.string.btn_ok), "", -1, true, alertDialogListener);

                        break;

                    case R.id.floatingdownloadExcel_Id:
                        if (!TextUtils.isEmpty(txtAccNo.getText().toString().trim())) {
                            new AccountLedgerExcelReportAsyncTask(txtAccNo.getText().toString().trim(), accountsArrayList, selectAccSpinner.getSelectedItem().toString()).execute();
                        } else {
                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_no_data_found), coordinatorLayout);
                        }
                        break;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    @SuppressLint("StaticFieldLeak")
    private class AccountLedgerPDFReportAsyncTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        private File file;
        ArrayList<GetUserProfileModal> accountsArrayList;
        String selectedAccountNo;
        String finalResponse;
        String result = null;

        public AccountLedgerPDFReportAsyncTask(String finalResponse, ArrayList<GetUserProfileModal> accountsArrayList, String selectedAccountNo) {
            this.finalResponse = finalResponse;
            this.accountsArrayList = accountsArrayList;
            this.selectedAccountNo = selectedAccountNo;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Security.insertProviderAt(new BouncyCastleProvider(), 1);

                Document doc = new Document();

                file = new File(Environment.getExternalStorageDirectory() + File.separator + AppConstants.STATEMENTS);

                String outpath = file + AppConstants.PDF_STATEMENT_FILE_NAME;

                file.mkdirs();

                OutputStream pdfOutputFile = new FileOutputStream(outpath);
                // Create a new folder if no folder named SDImageTutorial exist

                //PdfWriter.getInstance(doc, new FileOutputStream(outpath));
                PdfWriter writer = PdfWriter.getInstance(doc, pdfOutputFile);
                writer.setEncryption(AppConstants.getCLIENTID().getBytes(), AppConstants.getCLIENTID().getBytes(), PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);


                //create pdf writer instance
                Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, new BaseColor(0, 0, 0));
                Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 14);

                DecimalFormat df = new DecimalFormat("0.00");
                doc.addCreationDate();
                doc.addProducer();
                doc.setPageSize(PageSize.LETTER);
                doc.open();

                Font f = new Font(Font.FontFamily.TIMES_ROMAN, 25.0f, Font.BOLD, BaseColor.BLACK);
                Chunk c = new Chunk("Account Statement", f);
                Paragraph p1 = new Paragraph(c);
                p1.setAlignment(Paragraph.ALIGN_CENTER);
                doc.add(p1);

                Paragraph paragraph = null;
                if (accountsArrayList != null) {
                    for (GetUserProfileModal getUserProfileModal : accountsArrayList) {
                        if (selectedAccountNo.equalsIgnoreCase(getUserProfileModal.getAccNo())) {

                            paragraph = new Paragraph("Name :- " + getUserProfileModal.getName() + "\n" + "Account Number :- " + getUserProfileModal.getAccNo());
                        }
                    }
                }
                //=========add cell===

                //create a paragraph

                //specify column widths
                float[] columnWidths = {4f, 10f};
                //create PDF table with the given widths
                PdfPTable table = new PdfPTable(columnWidths);
                // set table width a percentage of the page width
                table.setWidthPercentage(100f);

                //insert column headings
                insertCell(table, "Sr No", Element.ALIGN_CENTER, 1, bfBold12);
                insertCell(table, "Statements", Element.ALIGN_CENTER, 1, bfBold12);

                table.setHeaderRows(1);

                insertCell(table, "1", Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, finalResponse, Element.ALIGN_CENTER, 1, bf12);
                paragraph.add(table);
                doc.add(paragraph);
                doc.close();
                pdfOutputFile.close();

                result = "Success";

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
            return result;

        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MiniStatementActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (!TextUtils.isEmpty(result)) {
                    if (result.equalsIgnoreCase("Success")) {
                        String message = getResources().getString(R.string.alert_pdf_message);
                        AlertDialogMethod.alertDialog(MiniStatementActivity.this, getResources().getString(R.string.app_name), message, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), 1, false, alertDialogListener);
                    } else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_try_again), coordinatorLayout);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDialogOk(int resultCode) {
        switch (resultCode) {
            case -1:
                if (!TextUtils.isEmpty(txtAccNo.getText().toString().trim())) {
                    new AccountLedgerPDFReportAsyncTask(txtAccNo.getText().toString().trim(), accountsArrayList, selectAccSpinner.getSelectedItem().toString()).execute();
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_no_data_found), coordinatorLayout);
                }
                break;

            case 0:
                Intent intentLogin = new Intent(getApplicationContext(), LockActivity.class);
                intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogin);
                method.activityCloseAnimation();
                break;

            case 1:
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AppConstants.STATEMENTS + AppConstants.PDF_STATEMENT_FILE_NAME);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(this, "com.trustbank.fileprovider", file);

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
                    TrustMethods.message(MiniStatementActivity.this,getResources().getString(R.string.msg_pdf_not_supported));
                }
                break;

            case 2:
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + AppConstants.STATEMENTS + AppConstants.EXCEL_STATEMENT_FILE_NAME);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(this, "com.trustbank.fileprovider", file);

                        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    } else {
                        uri = Uri.fromFile(file);
                    }

                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(MiniStatementActivity.this, getResources().getString(R.string.msg_excel_not_supported), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogCancel(int resultCode) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                onBackPressed();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AccountLedgerExcelReportAsyncTask extends AsyncTask<String, String, String> {
        private final WorkbookSettings wbSettings;
        String mFinalResponse;
        ArrayList<GetUserProfileModal> mAccountsArrayList;
        String mSelectedAccountNo;
        private ProgressDialog progressDialog;
        private File fileExcel;
        private WritableWorkbook workbook;
        private WritableSheet sheet;
        private Label labelTitleName;
        private Label labelTitleName1;
        private String result;

        public AccountLedgerExcelReportAsyncTask(String finalResponse, ArrayList<GetUserProfileModal> accountsArrayList, String selectedAccountNo) {
            mFinalResponse = finalResponse;
            mAccountsArrayList = accountsArrayList;
            mSelectedAccountNo = selectedAccountNo;
            wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MiniStatementActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                fileExcel = TrustFileUtils.createFilePath(MiniStatementActivity.this, AppConstants.EXCEL_STATEMENT_FILE_NAME, AppConstants.STATEMENTS);
                workbook = Workbook.createWorkbook(fileExcel, wbSettings);

                // font change
                WritableFont font1 = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);
                WritableCellFormat format1 = new WritableCellFormat(font1);
                sheet = workbook.createSheet(AppConstants.STATEMENT_SHEET, 0);

                Label labelTitle = new Label(1, 1, "Account Statement", format1);
                if (accountsArrayList != null) {
                    for (GetUserProfileModal getUserProfileModal : accountsArrayList) {
                        if (mSelectedAccountNo.equalsIgnoreCase(getUserProfileModal.getAccNo())) {
                            labelTitleName = new Label(0, 3, "Name:- " + getUserProfileModal.getName());
                            labelTitleName1 = new Label(0, 4, "Account Number:- " + getUserProfileModal.getAccNo());
                        }
                    }
                }
                Label labelTitleDt = new Label(0, 8, "Sr.No", format1);
                Label labelTitlValueDate = new Label(1, 8, "Transaction Details", format1);
                try {
                    sheet.addCell(labelTitleName);
                    sheet.addCell(labelTitle);
                    sheet.addCell(labelTitleName1);
                    sheet.addCell(labelTitleDt);
                    sheet.addCell(labelTitlValueDate);
                } catch (WriteException e) {
                    e.printStackTrace();
                }
                sheet.setColumnView(0, 20);
                sheet.setColumnView(1, 80);

                Label labelStatement;
                if (mFinalResponse != null) {
                    labelStatement = new Label(0, 9, "1");
                } else {
                    labelStatement = new Label(0, 9, "-");
                }
                Label labelStatementDetails;
                if (mFinalResponse != null) {
                    labelStatementDetails = new Label(1, 9, mFinalResponse);
                } else {
                    labelStatementDetails = new Label(1, 9, "-");
                }
                try {
                    sheet.addCell(labelStatement);
                    sheet.addCell(labelStatementDetails);
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            try {
                if (!TextUtils.isEmpty(result)) {
                    if (result.equals("success")) {
                        AlertDialogMethod.alertDialog(MiniStatementActivity.this, getResources().getString(R.string.app_name), getResources().getString(R.string.msg_excel_download), getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), 2, false, alertDialogListener);
                    } else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_try_again), coordinatorLayout);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(MiniStatementActivity.this);
    }
}