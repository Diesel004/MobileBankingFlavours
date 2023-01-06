package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.trustbank.R;
import com.trustbank.util.AppConstants;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;

public class SettingActivity extends AppCompatActivity {

    private CardView cvChangeMpinId, cvChangeTpinId, cvResetTpinId, cvTransactionLimitId, registerAnotherClientId,debitCardPinGenId;
    private TrustMethods trustMethods;

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
                        TrustMethods.naviagteToSplashScreen(SettingActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(SettingActivity.this, false);
        setContentView(R.layout.activity_setting);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        trustMethods = new TrustMethods(SettingActivity.this);
        cvChangeMpinId = findViewById(R.id.cvChangeMpinId);
        cvChangeTpinId = findViewById(R.id.cvChangeTpinId);
        cvResetTpinId = findViewById(R.id.cvResetTpinId);
        cvTransactionLimitId = findViewById(R.id.transactionLimitId);
        registerAnotherClientId = findViewById(R.id.registerAnotherClientId);
        debitCardPinGenId = findViewById(R.id.debitCardPinGenId);

        if (AppConstants.getMnu_Change_MPin().equalsIgnoreCase("1")) {
            cvChangeMpinId.setVisibility(View.VISIBLE);
        } else {
            cvChangeMpinId.setVisibility(View.GONE);
        }

        if (AppConstants.getMnu_Change_TPin().equalsIgnoreCase("1")) {
            cvChangeTpinId.setVisibility(View.VISIBLE);
        } else {
            cvChangeTpinId.setVisibility(View.GONE);
        }

        if (AppConstants.getMnu_Reset_TPin().equalsIgnoreCase("1")) {
            cvResetTpinId.setVisibility(View.VISIBLE);
        } else {
            cvResetTpinId.setVisibility(View.GONE);
        }

        if (AppConstants.getMnu_Limit_Transaction().equalsIgnoreCase("1")) {
            cvTransactionLimitId.setVisibility(View.VISIBLE);
        } else {
            cvTransactionLimitId.setVisibility(View.GONE);
        }

        if (AppConstants.getMnu_debit_card_pin_generation().equalsIgnoreCase("1")) {
            debitCardPinGenId.setVisibility(View.VISIBLE);
        } else {
            debitCardPinGenId.setVisibility(View.GONE);
        }


        listener();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }

    private void listener() {

        cvChangeMpinId.setOnClickListener(v -> {
            Intent intentChangeTpin = new Intent(this, ChangePINSActivity.class);
            intentChangeTpin.putExtra("formType", "changeMpin");
            intentChangeTpin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentChangeTpin);
            trustMethods.activityOpenAnimation();
        });

        cvChangeTpinId.setOnClickListener(v -> {
            Intent intentChangeTpin = new Intent(this, ChangePINSActivity.class);
            intentChangeTpin.putExtra("formType", "changeTpin");
            intentChangeTpin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentChangeTpin);
            trustMethods.activityOpenAnimation();

        });

        cvResetTpinId.setOnClickListener(v -> {
            Intent intentChangeTpin = new Intent(this, ResetTPinActivity.class);
            intentChangeTpin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentChangeTpin);
            trustMethods.activityOpenAnimation();

        });

        cvTransactionLimitId.setOnClickListener(v -> {
            Intent intentChangeTpin = new Intent(this, TransactionLimitActivity.class);
            intentChangeTpin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentChangeTpin);
            trustMethods.activityOpenAnimation();

        });

        registerAnotherClientId.setOnClickListener(v -> {
            Intent intentChangeTpin = new Intent(this, RegisterClientId.class);
            intentChangeTpin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentChangeTpin);
            trustMethods.activityOpenAnimation();

        });

        debitCardPinGenId.setOnClickListener(v -> {
            Intent intentChangeTpin = new Intent(this, DebitCardPinGenerationActivity.class);
            intentChangeTpin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentChangeTpin);
            trustMethods.activityOpenAnimation();

        });
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
        TrustMethods.showBackButtonAlert(SettingActivity.this);
    }
}