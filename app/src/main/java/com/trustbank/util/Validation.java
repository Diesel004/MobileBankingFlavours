package com.trustbank.util;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.widget.EditText;

import com.trustbank.R;


public class Validation {

    public static boolean validationFundTransferOtp(CoordinatorLayout coordinatorLayout, Context context,
                                                    EditText editTextOtp, EditText editTextMpin) {

        if (editTextOtp.getText().toString().isEmpty() && editTextMpin.getText().toString().isEmpty()) {
            TrustMethods.showSnackBarMessage(context.getResources().getString(R.string.error_all_fields_mandatory), coordinatorLayout);
            return false;
        } else if (editTextOtp.getText().toString().isEmpty()) {
            TrustMethods.showSnackBarMessage(context.getResources().getString(R.string.error_blank_otp), coordinatorLayout);
            return false;
        } else if (editTextMpin.getText().toString().isEmpty()) {
            TrustMethods.showSnackBarMessage(context.getResources().getString(R.string.error_blank_tpin), coordinatorLayout);
            return false;
        } else {
            return true;
        }
    }


}