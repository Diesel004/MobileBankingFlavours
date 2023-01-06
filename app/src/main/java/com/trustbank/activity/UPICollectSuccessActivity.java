package com.trustbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.trustbank.Model.FundTransferSubModel;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.barcode.ScannedBarcodeActivity;

import java.util.ArrayList;
import java.util.List;


public class UPICollectSuccessActivity extends AppCompatActivity implements AlertDialogOkListener {

    private TextView txtUPIId;
    private TextView txtname;
    private TextView txtAmount;
    private TextView txtPayeeUPIID;
    private TextView txtExpiryDate;
    private TextView txtRemarks;
    private TextView tvReferenceid;
    private Button btnOk;
    private LinearLayout layoutUPIId;
    private LinearLayout layoutnameId;
    private LinearLayout layoutAmountId;
    private LinearLayout layoutPayeeUPIId;
    private LinearLayout layoutExpiryDateId;
    private LinearLayout layoutRemarksId;
    private LinearLayout layouttvReferenceId;
    private final AlertDialogOkListener alertDialogOkListener = this;
    private ArrayList<FundTransferSubModel> fundTransferModalDataList;
    private CardView cvreceiptdetails;
    private TrustMethods trustMethods;
    List<FundTransferSubModel> fundTransferSubModalList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
                if (savedInstanceState != null) {
                    Object currentPID = String.valueOf(android.os.Process.myPid());
                    // Check current PID with old PID
                    if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                        // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                        TrustMethods.naviagteToSplashScreen(UPICollectSuccessActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(UPICollectSuccessActivity.this, false);
        setContentView(R.layout.activity_upicollect_success);
        initcomponent();
    }

    private void initcomponent() {
        try{
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            //fundTransferModalDataList = (ArrayList<FundTransferSubModel>) getIntent().getSerializableExtra("fundTransferDataList");
            fundTransferSubModalList = (ArrayList<FundTransferSubModel>) getIntent().getSerializableExtra("fundTransferDataList");

            trustMethods = new TrustMethods(UPICollectSuccessActivity.this);

            cvreceiptdetails = findViewById(R.id.cvreceiptdetails);
            layoutUPIId = findViewById(R.id.layoutUPIId);
            layoutnameId = findViewById(R.id.layoutnameId);
            layoutAmountId = findViewById(R.id.layoutAmountId);
            layoutPayeeUPIId = findViewById(R.id.layoutPayeeUPIId);
            layoutExpiryDateId = findViewById(R.id.layoutExpiryDateId);
            layoutRemarksId = findViewById(R.id.layoutRemarksId);
            layouttvReferenceId = findViewById(R.id.layouttvReferenceId);
            btnOk = findViewById(R.id.btnOk);

            txtUPIId = findViewById(R.id.txtUPIId);
            txtname = findViewById(R.id.txtname);
            txtAmount = findViewById(R.id.txtAmount);
            txtPayeeUPIID = findViewById(R.id.txtPayeeUPIID);
            txtExpiryDate = findViewById(R.id.txtExpiryDate);
            txtRemarks = findViewById(R.id.txtRemarks);
            tvReferenceid = findViewById(R.id.tvReferenceid);

            btnOk.setOnClickListener(view -> {
                Intent intent = new Intent(UPICollectSuccessActivity.this, FundsTransferMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                trustMethods.activityOpenAnimation();
            });


            if (fundTransferSubModalList != null && fundTransferSubModalList.size() != 0) {
                cvreceiptdetails.setVisibility(View.VISIBLE);

                if (!fundTransferSubModalList.get(0).getUpiId().equalsIgnoreCase("")) {
                    layoutUPIId.setVisibility(View.VISIBLE);
                    txtUPIId.setText(fundTransferSubModalList.get(0).getUpiId());
                } else {
                    layoutUPIId.setVisibility(View.GONE);
                }
                if (!fundTransferSubModalList.get(0).getBenAccName().equalsIgnoreCase("")) {
                    layoutnameId.setVisibility(View.VISIBLE);
                    txtname.setText(fundTransferSubModalList.get(0).getBenAccName());
                } else {
                    layoutnameId.setVisibility(View.GONE);
                }
                if (!fundTransferSubModalList.get(0).getAmt().equalsIgnoreCase("")) {
                    layoutAmountId.setVisibility(View.VISIBLE);
                    txtAmount.setText(fundTransferSubModalList.get(0).getAmt());
                } else {
                    layoutAmountId.setVisibility(View.GONE);
                }
                if (!fundTransferSubModalList.get(0).getAccNo().equalsIgnoreCase("")) {
                    layoutPayeeUPIId.setVisibility(View.VISIBLE);
                    txtPayeeUPIID.setText(fundTransferSubModalList.get(0).getAccNo());
                } else {
                    layoutPayeeUPIId.setVisibility(View.GONE);
                }
                if (!fundTransferSubModalList.get(0).getRemitterAccName().equalsIgnoreCase("")) {
                    layoutExpiryDateId.setVisibility(View.VISIBLE);
                    txtExpiryDate.setText(fundTransferSubModalList.get(0).getRemitterAccName());
                } else {
                    layoutExpiryDateId.setVisibility(View.GONE);
                }
                if (!fundTransferSubModalList.get(0).getRemark().equalsIgnoreCase("")) {
                    layoutRemarksId.setVisibility(View.VISIBLE);
                    txtRemarks.setText(fundTransferSubModalList.get(0).getRemark());
                } else {
                    layoutRemarksId.setVisibility(View.GONE);
                }
            } else {

                    cvreceiptdetails.setVisibility(View.GONE);
                    AlertDialogMethod.alertDialogOk(UPICollectSuccessActivity.this,
                            getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok),
                            0, false, alertDialogOkListener);


                }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(UPICollectSuccessActivity.this);
    }

    @Override
    public void onDialogOk(int resultCode) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}