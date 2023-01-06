package com.trustbank.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trustbank.Model.BbpsTransactionRequestModel;
import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.R;
import com.trustbank.tcpconnection.tdto.MessageDtoBuilder;
import com.trustbank.tcpconnection.tmessage.TMessage;
import com.trustbank.tcpconnection.tmessage.TMessageUtil;
import com.trustbank.tcpconnection.util.ResponseEntity;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONException;
import org.json.JSONObject;

public class ComplaintHistory extends AppCompatActivity implements View.OnClickListener {
    Button btnNext;
    TextView txtFrmDate, txtToDate;
    private LinearLayout layoutSearchByDate;
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
                        TrustMethods.naviagteToSplashScreen(ComplaintHistory.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(ComplaintHistory.this, false);
        setContentView(R.layout.activity_complaint_history);
        init();
    }

    private void init() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            method = new TrustMethods(ComplaintHistory.this);
            layoutSearchByDate = findViewById(R.id.layoutSearchByDate);
            btnNext = findViewById(R.id.btnNext);
            txtFrmDate = findViewById(R.id.txtFrmDateId);
            txtToDate = findViewById(R.id.txtToDateId);


            btnNext.setOnClickListener(this);
            txtFrmDate.setOnClickListener(this);
            txtToDate.setOnClickListener(this);
            setDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDate() {
        try {
            txtFrmDate.setText(TrustMethods.getCurrentMonthFirstDate());
            txtToDate.setText(TrustMethods.getMonthYear());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.txtFrmDateId:
                    method.datePickerDisableFuturesDate(ComplaintHistory.this, txtFrmDate);
                    break;
                case R.id.txtToDateId:
                    method.datePickerDisableFuturesDate(ComplaintHistory.this, txtToDate);
                    break;
                case R.id.btnNext:
                    if (validateEntries()) {
                        Intent intent = new Intent(this, ComplaintHistoryListActivity.class);
                        String fromDate = txtFrmDate.getText().toString();
                        String toDate = txtToDate.getText().toString();
                        intent.putExtra("FromDate", fromDate);
                        intent.putExtra("ToDate", toDate);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        method.activityOpenAnimation();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean validateEntries() {
        try{
            String fromDate = txtFrmDate.getText().toString();
            String toDate = txtToDate.getText().toString();
            if (fromDate.isEmpty()) {
                TrustMethods.message(ComplaintHistory.this, "Select From Date");
                return false;
            } else if (toDate.isEmpty()) {
                TrustMethods.message(ComplaintHistory.this, "Select To Date");
                return false;
            } else if (!TextUtils.isEmpty(fromDate) && !TextUtils.isEmpty(toDate)) {
                if (TrustMethods.isFromDateGreaterThanToDate(fromDate.trim(), toDate.trim())) {
                    TrustMethods.message(getApplicationContext(), "From date cannot be greater than to date.");
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
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
        TrustMethods.showBackButtonAlert(ComplaintHistory.this);
    }

}