package com.trustbank.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.trustbank.R;
import com.trustbank.util.TrustMethods;

public class BBPSDynamicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_b_p_s_dynamic);
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(BBPSDynamicActivity.this);
    }
}