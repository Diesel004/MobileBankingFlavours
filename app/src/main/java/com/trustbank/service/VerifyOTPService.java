package com.trustbank.service;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;
import com.trustbank.util.AppConstants;

/**
 * Created by Trust on 24-05-2018.
 */

public class VerifyOTPService extends IntentService {

    public static final String ACTION_MyIntentService = "com.trustbank.RESPONSE";
    public static final String AUTO_READ_OTP_KEY = "AUTO_READ_OTP";
    public static final String SERVER_RESPONCE_OTP_KEY = "SERVER_RESPONCE_OTP";
    public static final String MOBILE_KEY_OUT = "MOBILE_KEY_OUT";
    public static final String SMS_KEY_OUT = "SMS_KEY_OUT";
    public static final String ERROR_KEY_OUT = "ERROR_KEY_OUT";
    public static final String SUCCESS_KEY = "SUCCESS_KEY";
    public static final String SLOT_SIM = "SLOT_SIM";

    public VerifyOTPService() {
        super(VerifyOTPService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String otp = intent.getStringExtra("otp");
            String slot = intent.getStringExtra("slot");
            if (otp != null) {
//                    String otpSentMsg = TrustMethods.getVerificationCode(sms, "");
                Intent intentResponse = new Intent();
                intentResponse.setAction(ACTION_MyIntentService);
                intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                intentResponse.putExtra(SUCCESS_KEY, "success");
                intentResponse.putExtra(AUTO_READ_OTP_KEY, otp);
                intentResponse.putExtra(SERVER_RESPONCE_OTP_KEY, AppConstants.getServerOtp());
                intentResponse.putExtra(MOBILE_KEY_OUT, AppConstants.getUSERMOBILENUMBER());
                intentResponse.putExtra(SMS_KEY_OUT, "");
                intentResponse.putExtra(SLOT_SIM, slot);
                sendBroadcast(intentResponse);
            } else {
                Intent intentResponse = new Intent();
                intentResponse.setAction(ACTION_MyIntentService);
                intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                intentResponse.putExtra(SUCCESS_KEY, "error");
                intentResponse.putExtra(ERROR_KEY_OUT, AppConstants.getServerError());
                sendBroadcast(intentResponse);
            }
        }
    }
}