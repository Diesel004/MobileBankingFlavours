package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AppConstants;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;

public class UpiQRDisplayActivity extends AppCompatActivity implements AlertDialogOkListener {

    public final static int QRcodeWidth = 500;
    private Bitmap bitmap;
    private ImageView iv;
    private TextView fromAccNameId, fromAccNoId, amtId, txtErrorId ,upiId;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout errorLL, successLL;
    private AlertDialogOkListener alertDialogOkListener = this;
    private TrustMethods method;
    private boolean isUpiSelfBarcodeGenerator;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            if (savedInstanceState != null) {
                Object currentPID = String.valueOf(android.os.Process.myPid());
                // Check current PID with old PID
                if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                    // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                    TrustMethods.naviagteToSplashScreen(UpiQRDisplayActivity.this);
                }
            }
        }
        SetTheme.changeToTheme(UpiQRDisplayActivity.this, false);
        setContentView(R.layout.activity_upi_barcode_display);
        init();
    }

    private void init() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            method = new TrustMethods(UpiQRDisplayActivity.this);
            coordinatorLayout = findViewById(R.id.coordinatorLayoutId);
            successLL = findViewById(R.id.successLL);
            errorLL = findViewById(R.id.errorLL);
            txtErrorId = findViewById(R.id.txtErrorId);
            iv = findViewById(R.id.iv_qr_code);
            fromAccNameId = findViewById(R.id.fromAccNameId);
            fromAccNoId = findViewById(R.id.fromAccNoId);
            upiId=findViewById(R.id.upiID);
            amtId = findViewById(R.id.amtId);
            Intent intent = getIntent();
            // generateBarcode("upi://pay?pa=abcpay@icici&pn=AbcLimited&tr=EZY123456789012&am=10&cu=INR&mc=5411");
            if (intent != null) { //TODO open comments to work with dynamic
                isUpiSelfBarcodeGenerator = intent.getBooleanExtra("isUpiSelfBarcodeGenerator", false);

                if (isUpiSelfBarcodeGenerator) {

                    String accNumber = intent.getStringExtra("accNumber");
                    String accName = intent.getStringExtra("accName");
                    String upiURL = intent.getStringExtra("upiUrl");
                    fromAccNoId.setText("A/c No-"+accNumber);
                    upiId.setText(intent.getStringExtra("upiId"));
                    fromAccNameId.setText(accName);

                    showAlertDialog();

                    generateBarcode(upiURL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlertDialog() {
        pDialog = new ProgressDialog(UpiQRDisplayActivity.this);
        pDialog.setMessage(getResources().getString(R.string.loading_wait));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private Bitmap getScreenShotFromView(View v) {
        Bitmap screenshot = null;
        try {
            screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(screenshot);
            v.draw(canvas);
        } catch (Exception e) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.getMessage());
        }
        return screenshot;
    }

    // this method saves the image to gallery
    private void saveMediaToStorage(Bitmap bitmap) {

        try {
            String filename = "Barcode_" + "${System.currentTimeMillis()}.jpg";
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, filename, "Description");
            Toast.makeText(this, "Barcode saved to Gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error while saved to Gallery", Toast.LENGTH_SHORT).show();
        }
    }

    private void generateBarcode(String url) {
        try {
            bitmap = TextToImageEncode(url);
            iv.setImageBitmap(bitmap);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    private Bitmap TextToImageEncode(String url) {
        BitMatrix bitMatrix;
        try {

            bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, QRcodeWidth, QRcodeWidth,
                    null);

            int bitMatrixWidth = bitMatrix.getWidth();
            int bitMatrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

            for (int y = 0; y < bitMatrixHeight; y++) {
                int offset = y * bitMatrixWidth;

                for (int x = 0; x < bitMatrixWidth; x++) {

                    pixels[offset + x] = bitMatrix.get(x, y) ?
                            getResources().getColor(R.color.black) : getResources().getColor(R.color.colorWhite);
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
            bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(UpiQRDisplayActivity.this);
    }

    @Override
    public void onDialogOk(int resultCode) {
        Intent intent = new Intent(getApplicationContext(), LockActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        method.activityCloseAnimation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            Bitmap bitmap = getScreenShotFromView(successLL);
            saveMediaToStorage(bitmap);
        }
        return super.onOptionsItemSelected(item);
    }


}