package com.trustbank.interfaces;

import android.content.Intent;

public interface OtpDetailsListener {

    void onOTPReceived(Intent intent, int requestCode);

    void onOTPTimeOut();

}
