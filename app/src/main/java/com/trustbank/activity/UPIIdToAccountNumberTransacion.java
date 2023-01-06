package com.trustbank.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AppConstants;
import com.trustbank.util.DecimalDigitsInputFilter;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;

import java.util.ArrayList;
import java.util.List;

public class UPIIdToAccountNumberTransacion extends AppCompatActivity implements AlertDialogOkListener, View.OnClickListener {


    private Spinner spinnerFrmUpiId, spinnerLinkedAccountId, spinnerToBenImpsAccId;

    private CoordinatorLayout coordinatorLayout;
    private TrustMethods trustMethods;
    private AlertDialogOkListener alertDialogOkListener = this;
    private LinearLayout formLayoutFundTransferBenId;
    private EditText etAccountNoId, etIFSCId, etAmountId, etRemarksId;
    private Button btnUpiToAccTranNextId;

    private ArrayList<GetUserProfileModal> accountsArrayList;
    private ArrayList<BeneficiaryModal> beneficiaryModalsSortedData;
    private ArrayList<BeneficiaryModal> beneficiaryArrayList;

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
                        TrustMethods.naviagteToSplashScreen(UPIIdToAccountNumberTransacion.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(UPIIdToAccountNumberTransacion.this, false);
        setContentView(R.layout.activity_u_p_i_id_to_account_number_transacion);
        initcomponent();
    }

    private void initcomponent() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            trustMethods = new TrustMethods(UPIIdToAccountNumberTransacion.this);
            spinnerFrmUpiId = findViewById(R.id.spinnerFrmUpiId);
            spinnerLinkedAccountId = findViewById(R.id.spinnerLinkedAccountId);

            formLayoutFundTransferBenId = findViewById(R.id.formLayoutFundTransferBenId);
            spinnerToBenImpsAccId = findViewById(R.id.spinnerToBenImpsAccId);

            etAccountNoId = findViewById(R.id.etAccountNoId);
            etIFSCId = findViewById(R.id.etIFSCId);
            etAmountId = findViewById(R.id.etAmountId);
            etRemarksId = findViewById(R.id.etRemarksId);
            btnUpiToAccTranNextId = findViewById(R.id.btnUpiToAccTranNextId);
            btnUpiToAccTranNextId.setOnClickListener(this);

            setBeneficiarySpinner();
            getSpinnerData();
            etAmountId.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});
            etAmountId.addTextChangedListener(new TextWatcher() {
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
                            etAmountId.setText("");
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

    private void getSpinnerData() {
        try {
            spinnerToBenImpsAccId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    try {
                        if (position != 0) {
                            // beneficiary = (String) adapterView.getItemAtPosition(position);
                            String strAccountNumber = beneficiaryModalsSortedData.get(position - 1).getBenAccNo();
                            String strIFSCCode = beneficiaryModalsSortedData.get(position - 1).getBenIfscCode();
                            etAccountNoId.setText(strAccountNumber);
                            etIFSCId.setText(strIFSCCode);
                            etAccountNoId.setClickable(false);
                            etIFSCId.setClickable(false);
                            // benAccountFullName = beneficiaryModalsSortedData.get(position - 1).getBanAccName();
                        } else {
                            etAccountNoId.setText("");
                            etIFSCId.setText("");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBeneficiarySpinner() {
        try {
            if (getIntent().getExtras() != null) {
                beneficiaryArrayList = (ArrayList<BeneficiaryModal>) getIntent().getSerializableExtra("beneficiaryList");

            }

            List<String> beneficiaryList = new ArrayList<>();
            beneficiaryList.add(0, "Select Beneficiary");

            beneficiaryModalsSortedData = new ArrayList<>();
            if (beneficiaryArrayList != null && beneficiaryArrayList.size() > 0) {
                for (int i = 0; i < beneficiaryArrayList.size(); i++) {
                    BeneficiaryModal beneficiaryModal = beneficiaryArrayList.get(i);
                    if (beneficiaryModal.getBenType().equals("2")) {
                        BeneficiaryModal beneficiaryModal1 = new BeneficiaryModal();
                        beneficiaryModal1.setBenNickname(beneficiaryModal.getBenNickname());
                        beneficiaryModal1.setBenAccNo(beneficiaryModal.getBenAccNo());
                        beneficiaryModal1.setBenIfscCode(beneficiaryModal.getBenIfscCode());
                        beneficiaryModal1.setBanAccName(beneficiaryModal.getBanAccName());
                        beneficiaryModalsSortedData.add(beneficiaryModal1);
                        beneficiaryList.add(beneficiaryModal.getBenNickname());
                    }
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(UPIIdToAccountNumberTransacion.this, android.R.layout.simple_spinner_item, beneficiaryList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerToBenImpsAccId.setAdapter(adapter);

           /* if (!TextUtils.isEmpty(beneficiaryNickName)) {
                formLayoutFundTransfer.setVisibility(View.GONE);
                setFundTransferLayout.setVisibility(View.VISIBLE);
                for (int i = 0; i < beneficiaryModalsSortedData.size(); i++) {
                    if (beneficiaryNickName.equalsIgnoreCase(beneficiaryModalsSortedData.get(i).getBenNickname())) {
                        txtToBenName.setText(beneficiaryModalsSortedData.get(i).getBenNickname());
                        txtToAccNo.setText(beneficiaryModalsSortedData.get(i).getBenAccNo());
                        txtToIfscCode.setText(beneficiaryModalsSortedData.get(i).getBenIfscCode());
                    }
                }
            } else {
                formLayoutFundTransfer.setVisibility(View.VISIBLE);
                setFundTransferLayout.setVisibility(View.GONE);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogOk(int resultCode) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUpiToAccTranNextId:

                break;
        }
    }
}