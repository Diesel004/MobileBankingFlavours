package com.trustbank.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;


import static com.trustbank.util.AppConstants.LoginInfo_isSetServerEnabled;
import static com.trustbank.util.AppConstants.isAutoReadOTPEnabled;

public class WelcomeScreen extends AppCompatActivity implements AlertDialogOkListener {

    static String TAG = WelcomeScreen.class.getSimpleName();
    private SharedPreferences sharedpreferences;
    private TrustMethods trustMethods;
    private String simErrorMsg;
    private Button setServerButton;
    private TextView txtVersionNameId;
    AlertDialogOkListener alertDialogOkListener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetTheme.changeToTheme(WelcomeScreen.this, true);
        setContentView(R.layout.activity_welcome_screen);

        if (!TrustMethods.isRooted(getApplicationContext())) {
            if (!TrustMethods.isHookUpDevice(getApplicationContext())) {
                initcomponent();
            } else {
                TrustMethods.message(getApplicationContext(), getResources().getString(R.string.device_hooked_up_message));
                finish();
            }
        } else {
            TrustMethods.message(getApplicationContext(), getResources().getString(R.string.device_rooted_message));
            finish();
        }

          /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }*/
    }

    private void initcomponent() {

        trustMethods = new TrustMethods(WelcomeScreen.this);
        Button btnActivate = findViewById(R.id.btnActivate);
        setServerButton = findViewById(R.id.setServerButton);
        txtVersionNameId = findViewById(R.id.txtVersionNameId);
        sharedpreferences = getSharedPreferences(AppConstants.MyPREFERENCES, Context.MODE_PRIVATE);

        if (LoginInfo_isSetServerEnabled) {
            setServerButton.setVisibility(View.VISIBLE);
        } else {
            setServerButton.setVisibility(View.GONE);
        }

        if (isAutoReadOTPEnabled) {
            Intent intent = getIntent();
            if (intent.getStringExtra(AppConstants.SIM_ERROR_MSG) != null) {
                simErrorMsg = intent.getStringExtra(AppConstants.SIM_ERROR_MSG);
                assert simErrorMsg != null;
                if (simErrorMsg.equalsIgnoreCase(AppConstants.SIM_NOT_EXISTS)) {
                    AlertDialogMethod.alertDialogOk(WelcomeScreen.this, "NO SIM Card", "No " +
                                    "Sim Card Not Detected. Please insert the sim in slot and try again",
                            getResources().getString(R.string.btn_ok), 1, false, alertDialogOkListener);
                }
            }
        }


        btnActivate.setOnClickListener(view -> {
            if (!TrustMethods.isRooted(getApplicationContext())) {
                if (!TrustMethods.isHookUpDevice(getApplicationContext())) {
                    Intent intent = new Intent(WelcomeScreen.this, VerifyMobileNumber.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    trustMethods.activityOpenAnimation();
                } else {
                    TrustMethods.message(getApplicationContext(), getResources().getString(R.string.device_hooked_up_message));
                    finish();
                }

            } else {
                TrustMethods.message(getApplicationContext(), getResources().getString(R.string.device_rooted_message));
            }


        });

        setServerButton.setOnClickListener(v -> serverSettings());
      //  Toast.makeText(getApplicationContext(),"Ip "+ AppConstants.IP,Toast.LENGTH_LONG).show();
        txtVersionNameId.setText("Application Version " + TrustMethods.getVersionName(WelcomeScreen.this));
    }


    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 1) {
            finish();
        }
    }

    protected void serverSettings() {
        Intent intent = new Intent(this, FrmServer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

