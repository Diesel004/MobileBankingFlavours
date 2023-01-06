package com.trustbank.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.Model.FundTransferSubModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.fragment.UPIBenListFragmentDialog;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.interfaces.UPISelectBenInterface;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.DecimalDigitsInputFilter;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.barcode.ScannedBarcodeActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UPIToUPITransactions extends AppCompatActivity implements View.OnClickListener,
        AlertDialogOkListener, UPISelectBenInterface {

    private TrustMethods trustMethods;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private Spinner spinnerFromAccountId;
    private EditText etUPIIDId, etAmount, etRemarksId, etCustomerNameId,etNickNameId;
    private Button btnUpiIdTransId;
    private CoordinatorLayout coordinatorLayout;
    TextView textVerifyUPITransId;
    private ImageView imgSelectUPIId;
    private ArrayList<BeneficiaryModal> beneficiaryArrayList;
    private String accountNo;
    private String remitterAccName;
    private AlertDialogOkListener alertDialogOkListener=this;
    //    private LinearLayout bottom_linear_layout_id;
//
//    private BottomSheetBehavior sheetBehavior;
    private int BARCODE_READER_REQUEST_CODE = 1;

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
                        TrustMethods.naviagteToSplashScreen(UPIToUPITransactions.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(UPIToUPITransactions.this, false);
        setContentView(R.layout.activity_u_p_i_to_u_p_i_transactions);

        initcomponent();
    }

    private void initcomponent() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            trustMethods = new TrustMethods(UPIToUPITransactions.this);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            spinnerFromAccountId = findViewById(R.id.spinnerFromAccountId);
            etAmount = findViewById(R.id.etAmountId);
            etRemarksId = findViewById(R.id.etRemarksId);
            btnUpiIdTransId = findViewById(R.id.btnUpiIdTransId);
            etUPIIDId = findViewById(R.id.etUPIIDId);
            textVerifyUPITransId = findViewById(R.id.textVerifyUPITransId);
            etCustomerNameId = findViewById(R.id.etCustomerNameId);
            etNickNameId = findViewById(R.id.etNickNameId);// new added.
            imgSelectUPIId = findViewById(R.id.imgSelectUPIId);
            btnUpiIdTransId.setOnClickListener(this);
            textVerifyUPITransId.setOnClickListener(this);
            imgSelectUPIId.setOnClickListener(this);
            setAccountNoSpinner();
            textVerifyUPITransId.setVisibility(View.GONE);
            etAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

            etAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String s1 = s.toString();
                        if (s1.length() == 1 && s1.equalsIgnoreCase("0")) {
                            etAmount.setText("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setAccountNoSpinner() {
        try {
            accountsArrayList = trustMethods.getArrayList(UPIToUPITransactions.this, "AccountListPref");

            if (getIntent().getExtras() != null) {
                beneficiaryArrayList = (ArrayList<BeneficiaryModal>) getIntent().getSerializableExtra("beneficiaryList");
            }

            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                List<String> accountList = new ArrayList<>();
                accountList.add(0, "Select Account Number");
                for (int i = 0; i < accountsArrayList.size(); i++) {
                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                    if (TrustMethods.isAccountTypeIsImpsRegValid(getUserProfileModal.getActType(),
                            getUserProfileModal.getIs_imps_reg())) {
                        String accNo = getUserProfileModal.getAccNo();
                        String accTypeCode = getUserProfileModal.getAcTypeCode();
                        accountList.add(accNo + " - " + accTypeCode);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(UPIToUPITransactions.this, android.R.layout.simple_spinner_item, accountList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFromAccountId.setAdapter(adapter);

                spinnerFromAccountId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                        if (pos != 0) {
                            accountNo = TrustMethods.getValidAccountNo((String) adapterView.getItemAtPosition(pos));
                            //Get Profile Info List to get remitter name for the selected account number
                            if (accountsArrayList != null && accountsArrayList.size() > 0) {
                                for (int i = 0; i < accountsArrayList.size(); i++) {
                                    GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                                    if (getUserProfileModal.getAccNo().equalsIgnoreCase(accountNo.trim())) {
                                        remitterAccName = getUserProfileModal.getName();
                                        Log.d("remitterAccName", remitterAccName);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUpiIdTransId:
                if (spinnerFromAccountId.getSelectedItem().equals("Select Account Number")) {
                    AlertDialogMethod.alertDialogOk(UPIToUPITransactions.this, " ",getResources().getString(R.string.error_select_acc_debit_no) ,
                            getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                } else if (TextUtils.isEmpty(etUPIIDId.getText().toString().trim())) {
                   // TrustMethods.showSnackBarMessage("Please Enter Payee's UPI Id ", coordinatorLayout);
                    AlertDialogMethod.alertDialogOk(UPIToUPITransactions.this, " ","Please Enter Payee's UPI Id" ,
                            getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                }else if ((etUPIIDId.getText().toString().trim().contains(" "))) {
                    etUPIIDId.setError("No Space Allow.");
                } else if (!TrustMethods.validateUPI(etUPIIDId.getText().toString().trim())) {
                    TrustMethods.showSnackBarMessage("Invalid UPI Id, Please enter valid UPI id.", coordinatorLayout);
                } else if (TextUtils.isEmpty(etCustomerNameId.getText().toString())) {
                    TrustMethods.showSnackBarMessage("Enter Customer Name", coordinatorLayout);
                } else if (TextUtils.isEmpty(etAmount.getText().toString())) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_amt), coordinatorLayout);
                } else if (TrustMethods.isAmoutLessThanZero(etAmount.getText().toString().trim())) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_valid_amt), coordinatorLayout);
                } else if (TextUtils.isEmpty(etRemarksId.getText().toString().trim())) {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_enter_remark), coordinatorLayout);
                } else {

                    String amt = etAmount.getText().toString().trim();
                    String remark = etRemarksId.getText().toString().trim();
                    String upiId = etUPIIDId.getText().toString();
                    String payeeCustName = etCustomerNameId.getText().toString();
                    String nickName = etNickNameId.getText().toString();

                    List<FundTransferSubModel> fundTransferSubModalList = new ArrayList<>();
                    FundTransferSubModel fundTransferSubModel = new FundTransferSubModel();
                    fundTransferSubModel.setAccNo(accountNo);
                    fundTransferSubModel.setRemitterAccName(remitterAccName);
                    fundTransferSubModel.setBenAccNo("");
                    fundTransferSubModel.setBenIfscCode("");
                    fundTransferSubModel.setAmt(amt);
                    fundTransferSubModel.setRemark(remark);
                    fundTransferSubModel.setBenAccName(payeeCustName);
                    fundTransferSubModel.setToBenName(nickName);// added nick name
                    fundTransferSubModel.setUpiId(upiId);
                    fundTransferSubModalList.add(fundTransferSubModel);

                    Intent intent = new Intent(UPIToUPITransactions.this, OtpVerificationActivity.class);
                    intent.putExtra("checkTransferType", "upiToUpiTransaction");
                    intent.putExtra("fundTransferDataList", (Serializable) fundTransferSubModalList);
                    intent.putExtra("beneficiaryList", beneficiaryArrayList);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    trustMethods.activityOpenAnimation();

                }
                break;

            case R.id.textVerifyUPITransId:
                //TODO Verify ben here.
                break;

            case R.id.imgSelectUPIId:  //TODO  select UPI Id From fragment dialogs..
                if (beneficiaryArrayList != null && beneficiaryArrayList.size() != 0) {

                    ArrayList<BeneficiaryModal> beneficiaryUPiArrayList = new ArrayList<>();

                    for (BeneficiaryModal beneficiaryModal : beneficiaryArrayList) {
                        if (beneficiaryModal.getBenType().equalsIgnoreCase("4")) {
                            beneficiaryUPiArrayList.add(beneficiaryModal);
                        }
                    }
                    if (beneficiaryUPiArrayList.size() != 0) {
                        FragmentManager manager = getSupportFragmentManager();
                        DialogFragment newFragment = UPIBenListFragmentDialog.newInstance(beneficiaryUPiArrayList, this);
                        newFragment.show(manager, "dialog");
                    }

                }
                break;
        }
    }

    /*  private void openConfrimBottomSheet(FundTransferSubModel fundTransferSubModel) {
          if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
              sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
          }
      }
  */
    @Override
    public void onDialogOk(int resultCode) {
        try {
            if (resultCode == 0) {
                Intent intent = new Intent(UPIToUPITransactions.this, LockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                trustMethods.activityCloseAnimation();
            }else if(resultCode==55){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upi_scan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_scan) {
            Intent intent = new Intent(UPIToUPITransactions.this, ScannedBarcodeActivity.class);
            startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(UPIToUPITransactions.this);
    }

    @Override
    public void selectUPIVPABenName(String upiBenVpaName, String custName,String nickName) {
        etUPIIDId.setText(upiBenVpaName);
        etCustomerNameId.setText(custName);
        etNickNameId.setText(nickName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == BARCODE_READER_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                String upi_id = data.getStringExtra("upiid");
                String upi_name = data.getStringExtra("upi_name");
                if (upi_id != null) {
                    etUPIIDId.setText(upi_id);
                    etCustomerNameId.setText(upi_name);
                    etNickNameId.setText(" ");
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_barcode_captured, Toast.LENGTH_SHORT).show();
                }
            }

        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
}