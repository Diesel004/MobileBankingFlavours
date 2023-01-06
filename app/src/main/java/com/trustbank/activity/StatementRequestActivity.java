package com.trustbank.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.util.AppConstants;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;

import java.util.ArrayList;
import java.util.List;


public class StatementRequestActivity extends AppCompatActivity implements View.OnClickListener {
    private TrustMethods method;
    private Spinner selectAccSpinner;
    private TextView txtFrmDate, txtToDate;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private List<String> accountList;

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
                        TrustMethods.naviagteToSplashScreen(StatementRequestActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(StatementRequestActivity.this, false);
        setContentView(R.layout.activity_statement_request);
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

        method = new TrustMethods(StatementRequestActivity.this);
        selectAccSpinner = findViewById(R.id.selectAccSpinnerId);
        txtFrmDate = findViewById(R.id.txtFrmDateId);
        txtToDate = findViewById(R.id.txtToDateId);

        txtFrmDate.setOnClickListener(this);
        txtToDate.setOnClickListener(this);

        currentDate();
        accountNoSpinner();
    }

    private void accountNoSpinner() {
        try {
            accountsArrayList = method.getArrayList(StatementRequestActivity.this, "AccountListPref");
            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                accountList = new ArrayList<>();
                accountList.add(0, "Select Account Number");
                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    accountList.add(getUserProfileModal.getAccNo());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(StatementRequestActivity.this, android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectAccSpinner.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void currentDate() {
        txtFrmDate.setText(TrustMethods.getMonthYear());
        txtToDate.setText(TrustMethods.getMonthYear());
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtFrmDateId:
                method.datePickerDisableFuturesDate(StatementRequestActivity.this, txtFrmDate);
                break;
            case R.id.txtToDateId:
                method.datePickerDisableFuturesDate(StatementRequestActivity.this, txtFrmDate);
                break;
            default:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(StatementRequestActivity.this);
    }
}
