package com.trustbank.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.os.Build;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AskPermissions {
    SessionManager mSession;
    Context mContext;
    Activity mActivity;

    public AskPermissions(Context context, SessionManager session) {
        this.mSession = session;
        this.mContext = context;
        this.mActivity = (Activity) context;

        mSession = new SessionManager(mContext);
    }

    private void checkToLaunch() {
        mSession.checkLogin();
    }

    public boolean checkAndRequestPermissions() {
        int read_phone_state = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE);
        int read_external_storage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write_external_storage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int access_fine_location = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        int access_storage_location = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (read_phone_state != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (read_external_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (write_external_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (access_fine_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (access_storage_location != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(mActivity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 0);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, final String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                    perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                }
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
               //perms.put(Manifest.permission.MANAGE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        for (int i = 0; i < permissions.length; i++)
                            perms.put(permissions[i], grantResults[i]);

                        if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            // && perms.get(Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        )
                        {
                            checkToLaunch();
                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_PHONE_STATE) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
//                                ||
//                                ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                            ) {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                                alertDialogBuilder.setMessage("All Permission required for this app");

                                alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> {

                                    checkAndRequestPermissions();
                                    dialog.dismiss();
                                });

                                alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> mActivity.finish());

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                                break;
                            } else {
                                Toast.makeText(mContext, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                                mActivity.finish();
                            }
                        }
                    }else {
                        for (int i = 0; i < permissions.length; i++)
                            perms.put(permissions[i], grantResults[i]);
                        if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            // && perms.get(Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        )
                        {
                            checkToLaunch();
                        } else {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_PHONE_STATE) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                                    ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
//                                ||
//                                ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                            ) {

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                                alertDialogBuilder.setMessage("All Permission required for this app");

                                alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> {

                                    checkAndRequestPermissions();
                                    dialog.dismiss();
                                });

                                alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> mActivity.finish());

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                                break;
                            } else {
                                Toast.makeText(mContext, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                                mActivity.finish();
                            }
                        }
                    }
                }
            }
        }
    }
}
