package com.trustbank.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.trustbank.interfaces.OtpDetailsListener;


import static com.trustbank.activity.VerifyMobileNumber.SMS_CONSENT_REQUEST;
import static com.trustbank.util.AppConstants.OTP_MESSAGE;
import static com.trustbank.util.AppConstants.SUB_ID;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    private OtpDetailsListener otpDetailsListener;

    public void initOTPListener(OtpDetailsListener otpDetailsListener) {
        this.otpDetailsListener = otpDetailsListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status smsRetrieverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (smsRetrieverStatus.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get consent intent
                        //Intent consentIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                        if (otpDetailsListener != null) {
                            Intent consentIntent = new Intent();
                            consentIntent.putExtra(OTP_MESSAGE, extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE));
                            consentIntent.putExtra(SUB_ID, extras.getInt(SmsRetriever.EXTRA_SIM_SUBSCRIPTION_ID));
                            otpDetailsListener.onOTPReceived(consentIntent, SMS_CONSENT_REQUEST);
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        if (otpDetailsListener != null) {
                            otpDetailsListener.onOTPTimeOut();
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (otpDetailsListener != null) {
                otpDetailsListener.onOTPTimeOut();
            }
        }

    }

}
