package com.trustbank.util;

import android.app.Application;
import android.os.Build;
import android.widget.ImageView;
import androidx.annotation.RequiresApi;
import com.trustbank.BuildConfig;


public class MBank extends Application {

    private static MBank mInstance;
    private static SharePreferenceUtils sharePreferenceUtils;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(getApplicationContext());
        appSignatureHelper.getAppSignatures(getApplicationContext());
        mInstance = this;
        sharePreferenceUtils = new SharePreferenceUtils(mInstance.getApplicationContext());
        if (BuildConfig.DEBUG) {
            AppConstants.isAutoReadOTPEnabled = false; //Set True for enabling Auto Read OTP using SMS Retrieval Api
            AppConstants.isAutoReadOTPReadOnlyRegisterMobile = false; //Set True for enabling Auto Read OTP using SMS Retrieval Api
            AppConstants.LoginInfo_isLogEnabled = true; //Set true for enabling Logs
            AppConstants.LoginInfo_isSetServerEnabled = true; //Set true for enabling set server
            AppConstants.isReadDeviceIDFeatureEnabled = true;
            AppConstants.isInterceptorEnabled = true; //TODO Made false only for to Check VAPT without encypt, make True for real working
            AppConstants.LoginInfo_isRootDetectionEnabled = true; //For enabling root detection
            AppConstants.LoginInfo_isHookedDeviceDetectionEnabled = true; //For enabling hooked detection
            AppConstants.IS_CHECK_ACCESS_BROKEN = true; //For to check access broken
        } else {
            AppConstants.isAutoReadOTPEnabled = true; //Set True for enabling Auto Read OTP using SMS Retrieval Api(not in used)
            if ((getPackageName().equalsIgnoreCase("com.trustbank.pdccbank")) || (getPackageName().equalsIgnoreCase("com.trustbank.parijatmbank"))){//com.trustbank.pdccbank parijatmbank
                AppConstants.isAutoReadOTPReadOnlyRegisterMobile = true; //PDCC only Set True for enabling Auto Read OTP using SMS Retrieval Api (false - OFF for Shivaji bank,VMUCB)
            } else {
                AppConstants.isAutoReadOTPReadOnlyRegisterMobile = false; //PDCC only Set True for enabling Auto Read OTP using SMS Retrieval Api (false - OFF for Shivaji bank,VMUCB)
            }
            AppConstants.LoginInfo_isLogEnabled = true; //Set true for enabling Logs
            AppConstants.LoginInfo_isSetServerEnabled = true; //Set true for enabling set server
            AppConstants.isReadDeviceIDFeatureEnabled = true;
            AppConstants.isInterceptorEnabled = true; //TODO Made false only for to Check VAPT without encypt, make True for real working
            AppConstants.LoginInfo_isRootDetectionEnabled = true; //For enabling root detection
            AppConstants.LoginInfo_isHookedDeviceDetectionEnabled = true; //For enabling hooked detection
            AppConstants.IS_CHECK_ACCESS_BROKEN = true; //For to check access broken
        }
        AppConstants.setIP(sharePreferenceUtils, getResources());
        AppConstants.setUniqueKeys(mInstance);
    }

    public static MBank getInstance() {
        return mInstance;
    }

    public static void loadAppLogo(ImageView ivAppLogo) {
        AppConstants.setLogo(mInstance, ivAppLogo);
    }

}
