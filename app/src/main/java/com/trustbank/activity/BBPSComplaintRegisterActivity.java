package com.trustbank.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.trustbank.R;
import com.trustbank.util.TrustMethods;

public class BBPSComplaintRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_b_p_s_complaint_register);
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(BBPSComplaintRegisterActivity.this);
    }
}