package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.trustbank.Model.NeftEnquiryModel;
import com.trustbank.R;
import com.trustbank.util.AppConstants;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NeftTransactionDetailsActivity extends AppCompatActivity {

    private String TAG = NeftEnquiryActivity.class.getSimpleName();
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
                        TrustMethods.naviagteToSplashScreen(NeftTransactionDetailsActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(NeftTransactionDetailsActivity.this, false);
        setContentView(R.layout.activity_neft_transaction_details);

        inIt();
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }

    @SuppressLint("SetTextI18n")
    private void inIt() {

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            method = new TrustMethods(NeftTransactionDetailsActivity.this);

            TextView tvTransType = findViewById(R.id.tvTransType);
            TextView tvTransId = findViewById(R.id.tvTransId);
            TextView tvTransDate = findViewById(R.id.tvTransDate);
            TextView tvDebitAccount = findViewById(R.id.tvDebitAccount);
            TextView tvAmount = findViewById(R.id.tvAmount);
            TextView tvBenName = findViewById(R.id.tvBenName);
            TextView tvToAcct = findViewById(R.id.tvToAcct);
            TextView tvIFSC = findViewById(R.id.tvIFSC);
            TextView tvUTRNo = findViewById(R.id.tvUTRNo);
            TextView tvStatus = findViewById(R.id.tvStatus);
            LinearLayout layoutUTRNo = findViewById(R.id.layoutUTRNo);

            NeftEnquiryModel neftEnquiryModel = (NeftEnquiryModel) getIntent().getSerializableExtra("NeftEnquiryModel");

            if (neftEnquiryModel != null) {
                tvTransType.setText(": NEFT");
                tvTransId.setText(": " + neftEnquiryModel.getTransactionID());
                tvTransDate.setText(": " + neftEnquiryModel.getTran_Date());
                tvDebitAccount.setText(": " + neftEnquiryModel.getDebit_Account());
                tvAmount.setText(": ₹ " + TrustMethods.getValueCommaSeparated(neftEnquiryModel.getAmount()));
                tvBenName.setText(": " + neftEnquiryModel.getBen_Name());
                tvToAcct.setText(": " + neftEnquiryModel.getTo_AccountNo());
                tvIFSC.setText(": " + neftEnquiryModel.getIFSC());
                if (getPackageName().equalsIgnoreCase("com.trustbank.pdccbank")) {
                    layoutUTRNo.setVisibility(View.GONE);
                } else {
                    layoutUTRNo.setVisibility(View.VISIBLE);
                    tvUTRNo.setText(": " + neftEnquiryModel.getUtr());

                }

                if (neftEnquiryModel.getStatus().equals("0")) {
                    tvStatus.setText(": Transaction in process");
                    TrustMethods.LogMessage("NEFT Transaction List", "Transaction Status Error Message : " + neftEnquiryModel.getError());
                } else if (neftEnquiryModel.getStatus().equals("1")) {
                    tvStatus.setText(": Success");
                    TrustMethods.LogMessage("NEFT Transaction List", "Transaction Status Error Message : " + neftEnquiryModel.getError());
                } else if (neftEnquiryModel.getStatus().equals("2")) {
                    tvStatus.setText(": Failed");
                    TrustMethods.LogMessage("NEFT Transaction List", "Transaction Status Error Message : " + neftEnquiryModel.getError());
                }
            } else {
                TrustMethods.message(NeftTransactionDetailsActivity.this, "NEFT Enquiry not found");
            }
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
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(NeftTransactionDetailsActivity.this);
    }

}