package com.trustbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.trustbank.R;
import com.trustbank.util.AppConstants;
import com.trustbank.util.SetTheme;
import com.trustbank.util.SharePreferenceUtils;
import com.trustbank.util.TrustMethods;

public class FrmServer extends AppCompatActivity {
    private Button setServerButton;
    public static EditText ipEditText;
    String Ip;
    SharePreferenceUtils sharePreferenceUtils;
    TrustMethods trustMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTheme.changeToTheme(FrmServer.this, false);
        setContentView(R.layout.activity_server);
        initComponents();
        loadSavedPreferences();
        if (!Ip.equals(ipEditText.getText().toString())) {
            ipEditText.setText(Ip);
        }
        registerEvents();
    }

    public void loadSavedPreferences() {
        Ip = sharePreferenceUtils.getString(AppConstants.STORE_IP);
    }

    private void registerEvents() {
        setServerButton.setOnClickListener(v -> {
            sharePreferenceUtils.putString(AppConstants.STORE_IP, ipEditText.getText().toString());
            AppConstants.setIP(sharePreferenceUtils, getResources());
            Intent intent = new Intent(FrmServer.this, WelcomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            trustMethods.activityOpenAnimation();
            finish();
        });
    }

    private void initComponents() {
        trustMethods = new TrustMethods(FrmServer.this);
        trustMethods.activityOpenAnimation();
        sharePreferenceUtils = new SharePreferenceUtils(FrmServer.this);
        setServerButton = findViewById(R.id.setServerButton);
        ipEditText = findViewById(R.id.ipEditText);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FrmServer.this, WelcomeScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        trustMethods.activityOpenAnimation();
        finish();
    }

}
