package com.trustbank.util.barcode;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Objects;
import java.util.Set;

import android.content.pm.PackageManager;

public class ScannedBarcodeActivity extends AppCompatActivity implements AlertDialogOkListener {

    SurfaceView surfaceView;
    // TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private String intentData = "";
    private ImageButton toggleButton;
    private AlertDialogOkListener alertDialogOkListener = this;
    private boolean isError = false;
    private Camera camera = null;
    private boolean flashmode = false;
    private boolean hasCameraFlash = false;
    private boolean flashOn = false;
    private static final int PERMISSION_REQUEST_CODE = 200;
    AlertDialog alertDialog = null;
    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            if (savedInstanceState != null) {
                Object currentPID = String.valueOf(android.os.Process.myPid());
                // Check current PID with old PID
                if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                    // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                    TrustMethods.naviagteToSplashScreen(ScannedBarcodeActivity.this);
                }
            }
        }
        SetTheme.changeToTheme(ScannedBarcodeActivity.this, false);
        setContentView(R.layout.activity_scanned_barcode);
        try {
            initViews();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            surfaceView = findViewById(R.id.surfaceView);
            toggleButton = findViewById(R.id.toggleButton);
            View myRectangleView = findViewById(R.id.myRectangleView);
            View redLayout = findViewById(R.id.redLayout);
            // AnimationUtils.slideDown(redLayout);
            animateView(redLayout);
            flashOnEvent();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateView(View redLayout) {
        try {
            TranslateAnimation mAnimation = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.ABSOLUTE, 0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                    TranslateAnimation.RELATIVE_TO_PARENT, 1.0f);
            mAnimation.setDuration(1000);
            mAnimation.setRepeatCount(-1);
            mAnimation.setRepeatMode(Animation.REVERSE);
            mAnimation.setInterpolator(new LinearInterpolator());
            redLayout.setAnimation(mAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void flashOnEvent() {
        try {
            hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            toggleButton.setOnClickListener(v -> {
                if (hasCameraFlash) {
                    if (flashOn) {
                        flashOn = false;
                        toggleButton.setImageResource(R.drawable.flash);
                    } else {
                        flashOn = true;
                        toggleButton.setImageResource(R.drawable.flash_off);
                    }
                    flashOnButton();
                } else {
                    TrustMethods.message(ScannedBarcodeActivity.this,"No flash light available on your device");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void initialiseDetectorsAndSources() {

        try {
            barcodeDetector = new BarcodeDetector.Builder(this)
                    .setBarcodeFormats(Barcode.QR_CODE)
                    .build();

            cameraSource = new CameraSource.Builder(this, barcodeDetector)
                    .setRequestedPreviewSize(1920, 1080)
//                    .setRequestedPreviewSize(200, 200)
                    .setAutoFocusEnabled(true) //you should add this feature
                    .build();


            surfaceViewcall();


            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {


                }

                @Override
                public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    try {
                        // if (!isError) {
                        if (barcodes != null && barcodes.size() != 0) {
                            // txtBarcodeValue.post(() -> {
                            String abc = barcodes.valueAt(0).displayValue;

                            if (TrustMethods.validateUPI(abc)) {
                                //     txtBarcodeValue.removeCallbacks(null);
                                intentData = abc;
                                // if (isValidURL(intentData)) {
                                Uri uri = Uri.parse(intentData);
                                String protocol = uri.getScheme();
                                String server = uri.getAuthority();
                                String path = uri.getPath();
                                Set<String> args = uri.getQueryParameterNames();
                                if (!TextUtils.isEmpty(uri.getQueryParameter("pa"))) {
                                    String upiId = uri.getQueryParameter("pa").trim();
                                    String CustomerName = uri.getQueryParameter("pn");
                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("upiid", upiId);
                                    returnIntent.putExtra("upi_name", CustomerName);
                                    setResult(Activity.RESULT_OK, returnIntent);
                                    finish();
                                } else {
                                    isError = true;
                                    //  alertDialog("No QR detected");
                                    TrustMethods.message(ScannedBarcodeActivity.this, "No QR detected");

                                }

                            } else {
                                TrustMethods.message(ScannedBarcodeActivity.this,"Invalid UPI Id");
                                // isError = true;
                                //alertDialog("Invalid " + barcodes.valueAt(0).displayValue);

                            }

                            // });
                        } else {
                            isError = true;
                            TrustMethods.message(ScannedBarcodeActivity.this,"No QR detected!!!");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        isError = true;
                        TrustMethods.message(ScannedBarcodeActivity.this,"Invalid Barcode!!!");
                        //  alertDialog("No QR detected");
                    }


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void surfaceViewcall() {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {

                    if (checkPermission()) {
                        //main logic or main code
                        cameraSource.start(surfaceView.getHolder());
                        // . write your main code to execute, It will execute if the permission is already given.
                    } else {
                        requestPermission();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initialiseDetectorsAndSources();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static boolean isValidURL(String urlString) {
        try {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void alertDialog(String message, int resultCode) {
        AlertDialogMethod.alertDialogOk(ScannedBarcodeActivity.this, "", message, getResources().getString(R.string.btn_ok),
                resultCode, false, alertDialogOkListener);
        isError = false;
    }

    @Override
    public void onDialogOk(int resultCode) {

        if (resultCode == 2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission();
            }
        } else if (resultCode == 3) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
//                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void flashOnButton() {
        try {
            camera = getCamera(cameraSource);
            if (camera != null) {

                Camera.Parameters param = camera.getParameters();
                param.setFlashMode(!flashmode ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(param);
                flashmode = !flashmode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Camera getCamera(@NonNull CameraSource cameraSource) {
        try {
            Field[] declaredFields = CameraSource.class.getDeclaredFields();

            for (Field field : declaredFields) {
                if (field.getType() == Camera.class) {
                    field.setAccessible(true);
                    try {
                        return (Camera) field.get(cameraSource);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //  Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    if (checkPermission()) {
                        //main logic or main code
                        try {
                            finish();
                            startActivityForResult(getIntent(), 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // . write your main code to execute, It will execute if the permission is already given.
                    }

                    // main logic
                } else {
                    //  Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(ScannedBarcodeActivity.this, Manifest.permission.CAMERA)) {
                                alertDialog("You need to allow access permissions", 2);
                                break;
                            } else {
                                try {

                                    if (alertDialog != null && alertDialog.isShowing()) {
                                        alertDialog.dismiss();
                                    }
                                    alertDialog = null;
                                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        alertDialogOkApp(this, "", "Go to settings and enable permissions " + count);
                                    }
                                    return;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (alertDialog != null && alertDialog.isShowing()) {
                                        alertDialog.dismiss();
                                    }
                                }
                            }

                        }
                    }
                }
                break;
        }
    }


    public AlertDialog alertDialogOkApp(Context context, String title, String message) {

        try {
//            final AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.customDialogue);
            final AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setCancelable(false);
            alert.setTitle(title);
            alert.setMessage(message);
            alert.setPositiveButton("OK",
                    (dialog, arg1) -> {
                    });

            alertDialog = alert.create();
            Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.dialogTheme;
            alertDialog.show();

            AlertDialog finalAlertDialog = alertDialog;
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                finalAlertDialog.dismiss();
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return alertDialog;
    }

    private boolean checkPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }
}