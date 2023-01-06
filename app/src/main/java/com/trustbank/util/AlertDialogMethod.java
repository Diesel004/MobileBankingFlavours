package com.trustbank.util;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;

import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogListener;
import com.trustbank.interfaces.AlertDialogOkListener;

import java.util.Objects;

public class AlertDialogMethod {
    private Context mContext;
    private final Activity activity;

    public AlertDialogMethod(Context context) {
        this.mContext = context;
        this.activity = (Activity) context;

    }

    /*  Use this dialog for two buttons*/

    public static void alertDialog(Context context, String title, String message, String positiveButton,
                                   String negativeButton, final int resultCode, boolean setCancel,
                                   final AlertDialogListener alertDialogListener1) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setCancelable(setCancel);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(positiveButton,
                (dialog, arg1) -> {
                });

        if (!TextUtils.isEmpty(negativeButton)){
            alert.setNegativeButton(negativeButton,
                    (dialog, arg1) -> {
                    });
        }

        final AlertDialog alertDialog1 = alert.create();
//        Objects.requireNonNull(alertDialog1.getWindow()).getAttributes().windowAnimations = R.style.DialogTheme;
        alertDialog1.show();

        alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            alertDialogListener1.onDialogOk(resultCode);
            if (alertDialog1.isShowing()){
                alertDialog1.dismiss();
            }

        });
        alertDialog1.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(view -> {
            alertDialogListener1.onDialogCancel(resultCode);
            alertDialog1.dismiss();
        });
    }

    /*
    Use this dialog for single button
   */
    public static void alertDialogOk(Context context, String title, String message,
                                     String button, final int resultCode, boolean setCancel,
                                     final AlertDialogOkListener alertDialogOkListener) {

        try {
//            final AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.customDialogue);
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setCancelable(setCancel);
            alert.setTitle(title);
            alert.setMessage(message);
            alert.setPositiveButton(button,
                    (dialog, arg1) -> {
                    });

            final AlertDialog alertDialog = alert.create();
            Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.dialogTheme;
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                if (alertDialogOkListener != null) {
                    alertDialogOkListener.onDialogOk(resultCode);
                    alertDialog.dismiss();
                }

            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
