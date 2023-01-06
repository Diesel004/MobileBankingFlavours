package com.trustbank.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.util.AppConstants;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import java.util.ArrayList;
import java.util.List;

public class FrmPPSServiceRequestEnquiry extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FrmPPSServiceRequestEnquiry.class.getSimpleName();
    private TextView editTextDateTo, editTextDateFrom;
    private Button buttonDisplay, buttonCancel;
    private TrustMethods trustMethods;
    private Spinner spinnerAccountNumber;
    public static String dateFrom;
    public static String dateTo;
    private ArrayList<GetUserProfileModal> accountsArrayList;


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
                        TrustMethods.naviagteToSplashScreen(FrmPPSServiceRequestEnquiry.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(FrmPPSServiceRequestEnquiry.this, false);
        setContentView(R.layout.activity_pps_service_enquiry);
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        trustMethods = new TrustMethods(FrmPPSServiceRequestEnquiry.this);
        trustMethods.activityOpenAnimation();
        editTextDateTo = findViewById(R.id.editTextToDate);
        editTextDateFrom = findViewById(R.id.editTextFromDate);
        buttonDisplay = findViewById(R.id.buttonDisplay);
        editTextDateTo.setOnClickListener(this);
        editTextDateFrom.setOnClickListener(this);
        buttonDisplay.setOnClickListener(this);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);
        spinnerAccountNumber = findViewById(R.id.spinnerAccount_Id);

        setAccountNoSpinner();
        setDate();
    }


    private void setDate() {
        try {
            editTextDateFrom.setText(TrustMethods.getCurrentMonthFirstDate());
            editTextDateTo.setText(TrustMethods.getMonthYear());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setAccountNoSpinner() {
        try {
            accountsArrayList = trustMethods.getArrayList(FrmPPSServiceRequestEnquiry.this, "AccountListPref");
            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                List<String> accountList = new ArrayList<>();
                accountList.add(0, "Select Account Number");
                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    if (TrustMethods.isAccountTypeValid(getUserProfileModal.getActType())) {
                        String accNo = getUserProfileModal.getAccNo();
                        String accTypeCode = getUserProfileModal.getAcTypeCode();
                        accountList.add(accNo + " - " + accTypeCode);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(FrmPPSServiceRequestEnquiry.this, android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAccountNumber.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.editTextFromDate:
                    trustMethods.datePicker(FrmPPSServiceRequestEnquiry.this, editTextDateFrom);
                    break;
                case R.id.editTextToDate:
                    trustMethods.datePicker(FrmPPSServiceRequestEnquiry.this, editTextDateTo);
                    break;
                case R.id.buttonDisplay:
                    dateFrom = editTextDateFrom.getText().toString();
                    dateTo = editTextDateTo.getText().toString();

                    if (spinnerAccountNumber.getSelectedItemPosition() == 0) {
                        TrustMethods.message(getApplicationContext(), "Please select Account Number");
                        return;
                    }

                    if (dateTo.trim().length() == 0 && dateFrom.trim().length() == 0) {
                        TrustMethods.message(getApplicationContext(), "Please select dates");
                        return;
                    }

                    if (dateFrom.trim().length() == 0) {
                        TrustMethods.message(getApplicationContext(), "Please select from date");
                        return;
                    }

                    if (dateTo.trim().length() == 0) {
                        TrustMethods.message(getApplicationContext(), "Please select to date");
                        return;
                    }

                    if (TrustMethods.isFromDateGreaterThanToDate(dateFrom.trim(), dateTo.trim())) {
                        TrustMethods.message(getApplicationContext(), "From date cannot be greater than to date.");
                        return;
                    }

                    String accountNo = TrustMethods.getValidAccountNo(spinnerAccountNumber.getSelectedItem().toString());

                    Intent intentShowReport = new Intent(getApplicationContext(), FrmPPSServiceRequestEnquiryDetails.class);
                    intentShowReport.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentShowReport.putExtra("fromDate", dateFrom);
                    intentShowReport.putExtra("toDate", dateTo);
                    intentShowReport.putExtra("accountNo", accountNo);
                    startActivity(intentShowReport);
                    trustMethods.activityOpenAnimation();
                    break;

                case R.id.buttonCancel:
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;

                default:
                    break;

            }
        }catch (Exception e){
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
        TrustMethods.showBackButtonAlert(FrmPPSServiceRequestEnquiry.this);
    }

}
