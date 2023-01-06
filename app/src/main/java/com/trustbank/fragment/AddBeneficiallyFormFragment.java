package com.trustbank.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.trustbank.Model.GenerateStanRRNModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.activity.IMPSTransferToAccount;
import com.trustbank.activity.LockActivity;
import com.trustbank.activity.OtpVerificationActivity;
import com.trustbank.activity.MenuActivity;
import com.trustbank.interfaces.AddBeneficiaryFragmentListener;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.tcpconnection.tdto.MessageDtoBuilder;
import com.trustbank.tcpconnection.tmessage.TMessage;
import com.trustbank.tcpconnection.tmessage.TMessageUtil;
import com.trustbank.tcpconnection.util.ResponseEntity;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


public class AddBeneficiallyFormFragment extends DialogFragment implements AlertDialogOkListener {

    private CoordinatorLayout coordinatorLayout;
    private EditText etNickName, etAccName, etAccNum, etIfscCode, etMobileNum, etMmid, etConfirmAccNumId, etConfirmMmidId, etUpiId,
            etUpiAccountNameId;
    private TextInputLayout ifscInputLayout;
    AddBeneficiaryFragmentListener addBeneficiallyFormFragment;
    private TrustMethods trustMethods;

    private TextView tvOwnBankIMPStoAccountTitle;
    private LinearLayout layoutForImpsToAccountNeftRtgs, layoutForImpsToMobile, verifyBenfLL, layoutForUPIId;
    private String ben_type;
    private ImageView searchIfscCodeId;
    private AlertDialogOkListener alertDialogOkListener = this;

    CardView cardbankDetailsId, cardVerifyBenDetailsId;
    TextView txtBankNameId, txtBranchNameId, txtBranchAddressId, txtVerifyBenId;
    private Button btnSaveBenef, bnVerifyBenefId,btnValidateBenefId;
    private String remmittterAccNo, remmitterName;

    public static DialogFragment newInstance(String beneficiaryType) {
        AddBeneficiallyFormFragment fragment = new AddBeneficiallyFormFragment();
        Bundle args = new Bundle();
        args.putString("ben_type", beneficiaryType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // creating the fullscreen dialog
        Dialog dialogFragment = new Dialog(Objects.requireNonNull(getActivity()));
        dialogFragment.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFragment.setContentView(root);
//        Objects.requireNonNull(dialogFragment.getWindow()).getAttributes().windowAnimations = R.style.DialogTheme;
        dialogFragment.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_beneficially_form_fragment, container, false);

        inIt(view);
        return view;
    }

    private void inIt(View view) {
        try {
            trustMethods = new TrustMethods(getActivity());
            addBeneficiallyFormFragment = (AddBeneficiaryFragmentListener) getActivity();
            ImageView cancelDialogue = view.findViewById(R.id.cancelDialogue);

            etNickName = view.findViewById(R.id.etNickNameId);
            Switch switchWithinBank = view.findViewById(R.id.switchWithinBankId);

            etAccName = view.findViewById(R.id.etAccNameId);
            etAccNum = view.findViewById(R.id.etAccNumId);
            btnSaveBenef = view.findViewById(R.id.btnSaveBenefId);
            etIfscCode = view.findViewById(R.id.etIfscCodeId);
            etMobileNum = view.findViewById(R.id.etMobileNumId);
            etMmid = view.findViewById(R.id.etMmidId);
            ifscInputLayout = view.findViewById(R.id.ifscInputLayoutId);
            etConfirmAccNumId = view.findViewById(R.id.etConfirmAccNumId);
            etConfirmMmidId = view.findViewById(R.id.etConfirmMmidId);
            coordinatorLayout = view.findViewById(R.id.coordinatorLayout);

            searchIfscCodeId = view.findViewById(R.id.searchIfscCodeId);
            tvOwnBankIMPStoAccountTitle = view.findViewById(R.id.tvOwnBankIMPStoAccountTitle);
            layoutForImpsToAccountNeftRtgs = view.findViewById(R.id.layoutForImpsToAccountNeftRtgs);
            layoutForImpsToMobile = view.findViewById(R.id.layoutForImpsToMobile);

            cardbankDetailsId = view.findViewById(R.id.cardbankDetailsId);
            txtBankNameId = view.findViewById(R.id.txtBankNameId);
            txtBranchNameId = view.findViewById(R.id.txtBranchNameId);
            txtBranchAddressId = view.findViewById(R.id.txtBranchAddressId);
            verifyBenfLL = view.findViewById(R.id.verifyBenfLL);
            bnVerifyBenefId = view.findViewById(R.id.bnVerifyBenefId);
            cardVerifyBenDetailsId = view.findViewById(R.id.cardVerifyBenDetailsId);
            txtVerifyBenId = view.findViewById(R.id.txtVerifyBenId);

            layoutForUPIId = view.findViewById(R.id.layoutForUPIID);
            etUpiId = view.findViewById(R.id.etUpiId);
            etUpiAccountNameId = view.findViewById(R.id.etUpiAccountNameId);
            btnValidateBenefId = view.findViewById(R.id.btnValidateBenefId);

            assert getArguments() != null;
            if (getArguments().containsKey("ben_type")) {
                ben_type = getArguments().getString("ben_type");
                assert ben_type != null;
                switch (ben_type) {
                    case "1":
                        ifscInputLayout.setVisibility(View.GONE);
                        tvOwnBankIMPStoAccountTitle.setText(getActivity().getResources().getString(R.string.txt_own_bank));
                        layoutForImpsToAccountNeftRtgs.setVisibility(View.VISIBLE);
                        layoutForImpsToMobile.setVisibility(View.GONE);
                        layoutForUPIId.setVisibility(View.GONE);
                        bnVerifyBenefId.setVisibility(View.GONE);
                        btnValidateBenefId.setVisibility(View.VISIBLE);
                        btnSaveBenef.setVisibility(View.GONE);
                        break;
                    case "2":
                        ifscInputLayout.setVisibility(View.VISIBLE);
                        tvOwnBankIMPStoAccountTitle.setText(getActivity().getResources().getString(R.string.txt_imps_acc_neft));
                        layoutForImpsToAccountNeftRtgs.setVisibility(View.VISIBLE);
                        layoutForImpsToMobile.setVisibility(View.GONE);
                        btnSaveBenef.setVisibility(View.GONE);
                        bnVerifyBenefId.setVisibility(View.GONE);
                        layoutForUPIId.setVisibility(View.GONE);
//                searchIfscCodeId.setVisibility(View.VISIBLE);
                        break;
                    case "3":
                        layoutForImpsToAccountNeftRtgs.setVisibility(View.GONE);
                        layoutForImpsToMobile.setVisibility(View.VISIBLE);
                        layoutForUPIId.setVisibility(View.GONE);
                        bnVerifyBenefId.setVisibility(View.GONE);
                        if (AppConstants.getMnu_verify_beneficiary_name().equalsIgnoreCase("1")) {
                            bnVerifyBenefId.setVisibility(View.VISIBLE);
                        }
                        break;

                    case "4":
                        layoutForImpsToAccountNeftRtgs.setVisibility(View.GONE);
                        layoutForImpsToMobile.setVisibility(View.GONE);
                        layoutForUPIId.setVisibility(View.VISIBLE);
                        break;
                }
            }

            getIMPSremitterAccountsDetails();  // get remmitter account.

            if (ben_type.equalsIgnoreCase("2") || ben_type.equalsIgnoreCase("3")) {
                if (AppConstants.getMnu_verify_beneficiary_name().equalsIgnoreCase("1")) { // 1-- means if Verify beneficery option allow.
                    //Verify Beneficery
                    if (!TextUtils.isEmpty(remmittterAccNo)) {  //if any of remiiter account not register for IMPS transactions then don't show.
                        verifyBenfLL.setVisibility(View.VISIBLE);
                        // btnSaveBenef.setVisibility(View.GONE);
                    } else {
                        verifyBenfLL.setVisibility(View.GONE);
                    }
                } else {
                    verifyBenfLL.setVisibility(View.GONE);
                }
            }


            if (ben_type.equalsIgnoreCase("4")) { //TODO for UPI id.
                // verifyBenfLL.setVisibility(View.VISIBLE);
                btnSaveBenef.setVisibility(View.VISIBLE);

            }

            if (Objects.requireNonNull(getActivity()).getPackageName().equalsIgnoreCase("com.trustbank.shivajibank") ||
                    getActivity().getPackageName().equalsIgnoreCase("com.trustbank.vmucbbank") ||
                    getActivity().getPackageName().equalsIgnoreCase("com.trustbank.punepeoplesbank") ||
                    getActivity().getPackageName().equalsIgnoreCase("com.trustbank.gondiamahilabank") ||
                    getActivity().getPackageName().equalsIgnoreCase("com.trustbank.pdccbank")) {
                TrustMethods.setEditTextMaxLength(21, etAccNum);
                etAccNum.setRawInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etAccNum.setSingleLine(true);
                etAccNum.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else if (getActivity().getPackageName().equalsIgnoreCase("com.trustbank.pdccbank")) { //PDCC Bank need account no. input type should be number only.
                TrustMethods.setEditTextMaxLength(21, etAccNum);
                etAccNum.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                etAccNum.setSingleLine(true);
                etAccNum.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                TrustMethods.setEditTextMaxLength(21, etAccNum);
                etAccNum.setRawInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etAccNum.setSingleLine(true);
                etAccNum.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }

            if (getActivity().getPackageName().equalsIgnoreCase("com.trustbank.shivajibank") ||
                    getActivity().getPackageName().equalsIgnoreCase("com.trustbank.vmucbbank") ||
                    getActivity().getPackageName().equalsIgnoreCase("com.trustbank.punepeoplesbank") ||
                    getActivity().getPackageName().equalsIgnoreCase("com.trustbank.gondiamahilabank") ||
                    getActivity().getPackageName().equalsIgnoreCase("com.trustbank.pdccbank")) {
                TrustMethods.setEditTextMaxLength(21, etConfirmAccNumId);
                etConfirmAccNumId.setInputType(InputType.TYPE_CLASS_TEXT);
            } else if (getActivity().getPackageName().equalsIgnoreCase("com.trustbank.pdccbank")) {    //PDCC Bank need account no. input type should be number only.
                TrustMethods.setEditTextMaxLength(18, etConfirmAccNumId);
                etConfirmAccNumId.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                TrustMethods.setEditTextMaxLength(21, etConfirmAccNumId);
                etConfirmAccNumId.setInputType(InputType.TYPE_CLASS_TEXT);
            }


            switchWithinBank.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (switchWithinBank.isChecked()) {
                    ifscInputLayout.setVisibility(View.GONE);
                } else {
                    ifscInputLayout.setVisibility(View.VISIBLE);
                }
            });


            etIfscCode.addTextChangedListener(new TextWatcher() {

                CharSequence beforeCharSeq;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    beforeCharSeq = s;
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 11) {
                        searchIfscCodeId.setVisibility(View.VISIBLE);
                    } else {
                        searchIfscCodeId.setVisibility(View.GONE);
                        btnSaveBenef.setVisibility(View.GONE);
                        cardbankDetailsId.setVisibility(View.GONE);
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.e("after", String.valueOf(s));
                }
            });

            searchIfscCodeId.setOnClickListener(v -> {
                if (NetworkUtil.getConnectivityStatus(getActivity())) {
                    String ifscCode = etIfscCode.getText().toString();
                    if (TextUtils.isEmpty(ifscCode)) {
                        etIfscCode.setError(getResources().getString(R.string.error_enter_ifsc_code));
                    }
                    if (!trustMethods.isValidImeI(etIfscCode.getText().toString().trim())) {
                        etIfscCode.setError(getResources().getString(R.string.error_invalid_ifsc_code));
                    } else {
                        new GetBranchDetailsAsyncTask(getActivity(), ifscCode).execute();
                    }
                } else {
                    AlertDialogMethod.alertDialogOk(getActivity(), getResources().getString(R.string.error_check_internet), "", getResources().getString(R.string.btn_ok),
                            3, false, alertDialogOkListener);
                }
            });

            btnValidateBenefId.setOnClickListener(v -> {
                try {
                    if (!TextUtils.isEmpty(etNickName.getText().toString().trim())) {
                        if (ben_type.equals("1")) {
                            //response..
                            if (validateForOwnBank()) {
                                validateWithinBen(etAccNum.getText().toString().trim(), ben_type);
                            }
                        }
                    }else {
                        TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_nickname), coordinatorLayout);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            btnSaveBenef.setOnClickListener(view1 -> {
                TrustMethods.hideSoftKeyboard(getActivity());

                if (!TextUtils.isEmpty(etNickName.getText().toString().trim())) {
                    if (!TextUtils.isEmpty(etNickName.getText().toString().trim())) {
                        if (ben_type.equals("1")) {
                            //response..
                           /* if (validateForOwnBank()) {
                                validateWithinBen(etAccNum.getText().toString().trim(), ben_type);
                            }*/
                            addBeneficiary(AppConstants.getUSERMOBILENUMBER(), etNickName.getText().toString().trim(), etAccName.getText().toString().trim(), etAccNum.getText().toString().trim(), "", "", "", false, ben_type, "");
                        } else if (ben_type.equals("2")) {
                            if (validateForIMPSToAccountNeftRtgs()) {
                                try {
                                    if (TrustMethods.isSimAvailable(getActivity()) && TrustMethods.isSimVerified(getActivity())) {
                                        if (NetworkUtil.getConnectivityStatus(Objects.requireNonNull(getActivity()))) {
                                            new BeneficieryAddValidationAsyncTask(getActivity(), etAccNum.getText().toString().trim(), ben_type, etIfscCode.getText().toString().trim()).execute();
                                        } else {
                                            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                        }
                                    } else {
                                        TrustMethods.displaySimErrorDialog(Objects.requireNonNull(getActivity()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // addBeneficiary(AppConstants.getUSERMOBILENUMBER(), etNickName.getText().toString().trim(), etAccName.getText().toString().trim(), etAccNum.getText().toString().trim(), etIfscCode.getText().toString().trim(), "", "", false, ben_type);
                            }
                        } else if (ben_type.equals("3")) {
                            if (validateForIMPSToMobile()) {
                                addBeneficiary(AppConstants.getUSERMOBILENUMBER(), etNickName.getText().toString().trim(), "", "", "", etMobileNum.getText().toString().trim(), etMmid.getText().toString().trim(), false, ben_type, "");
                            }
                        } else if (ben_type.equalsIgnoreCase("4")) { //for UPI Id.
                            if (validateForUpiId()) {
                                addBeneficiary(AppConstants.getUSERMOBILENUMBER(), etNickName.getText().toString().trim(), etUpiAccountNameId.getText().toString().trim(), "", "",
                                        etMobileNum.getText().toString().trim(), etMmid.getText().toString().trim(), false, ben_type,
                                        etUpiId.getText().toString());
                            }
                        }
                    }
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_blank_nickname), coordinatorLayout);
                }

            });

            bnVerifyBenefId.setOnClickListener(view12 -> {
                if (ben_type.equals("2")) {
                    if (validateForIMPSToAccountNeftRtgs()) {
                        try {
                            if (TrustMethods.isSimAvailable(getActivity()) && TrustMethods.isSimVerified(getActivity())) {
                                if (NetworkUtil.getConnectivityStatus(Objects.requireNonNull(getActivity()))) {

                                    String mobile = AppConstants.getUSERMOBILENUMBER();
                                    String nickName = etNickName.getText().toString().trim();
                                    String accName = etAccName.getText().toString().trim();
                                    String accNumber = etAccNum.getText().toString().trim();
                                    String ifscCode = etIfscCode.getText().toString().trim();
                                    String benType = ben_type;
                                    //p2P
                                    String benMobileNumber = "";
                                    String benMMID = "";
                                    //impsp2p addBeneficiary(AppConstants.getUSERMOBILENUMBER(), etNickName.getText().toString().trim(), "", "", "", etMobileNum.getText().toString().trim(), etMmid.getText().toString().trim(), false, ben_type);
                                    //impsp2A addBeneficiary(AppConstants.getUSERMOBILENUMBER(), etNickName.getText().toString().trim(), etAccName.getText().toString().trim(), etAccNum.getText().toString().trim(), "", "", "", false, ben_type);
                                    new VerifyBeneficieryAsyncTask(getActivity(), mobile,
                                            nickName, accName, accNumber, benType, benMobileNumber, benMMID, ifscCode,
                                            "").execute();
                                } else {
                                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                                }
                            } else {
                                TrustMethods.displaySimErrorDialog(Objects.requireNonNull(getActivity()));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //addBeneficiary(AppConstants.getUSERMOBILENUMBER(), etNickName.getText().toString().trim(), etAccName.getText().toString().trim(), etAccNum.getText().toString().trim(), etIfscCode.getText().toString().trim(), "", "", false, ben_type);
                    }
                } else if (ben_type.equals("3")) {
                    if (validateForIMPSToMobile()) {
                        String mobile = AppConstants.getUSERMOBILENUMBER();
                        String nickName = etNickName.getText().toString().trim();
                        String accName = "";
                        String accNumber = "";
                        String benType = ben_type;
                        //p2P
                        String benMobileNumber = etMobileNum.getText().toString().trim();
                        String benMMID = etMmid.getText().toString().trim();
                        String ifscCode = "";

                        //impsp2p addBeneficiary(AppConstants.getUSERMOBILENUMBER(), etNickName.getText().toString().trim(), "", "", "", etMobileNum.getText().toString().trim(), etMmid.getText().toString().trim(), false, ben_type);
                        //impsp2A addBeneficiary(AppConstants.getUSERMOBILENUMBER(), etNickName.getText().toString().trim(), etAccName.getText().toString().trim(), etAccNum.getText().toString().trim(), "", "", "", false, ben_type);
                        new VerifyBeneficieryAsyncTask(getActivity(), mobile,
                                nickName, accName, accNumber, benType, benMobileNumber, benMMID, ifscCode,
                                "").execute();
                    }
                } else if (ben_type.equalsIgnoreCase("4")) { //for upi id
                    if (validateForUpiId()) {

                        String strUPiId = etUpiId.getText().toString().trim();
                        String nickName = etNickName.getText().toString().trim();

                        //TODO need to verify ben before adding.
                    /*    new VerifyBeneficieryAsyncTask(getActivity(), AppConstants.getUSERMOBILENUMBER(),
                                nickName, "", "", ben_type, "", "", "",
                                strUPiId).execute();*/

                        addBeneficiary(AppConstants.getUSERMOBILENUMBER(), nickName, "","",
                                "", "", "", false,
                                ben_type, strUPiId);
                    }
                }
            });

            cancelDialogue.setOnClickListener(v -> getDialog().dismiss());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBeneficiary(String mobileNo, String nickname, String accName, String accNo,
                                String ifscCode, String benfMobNo, String mmid, boolean withinChecked, String ben_type,
                                String upiId) {

        if (NetworkUtil.getConnectivityStatus(getActivity())) {
            if (TrustMethods.isSimAvailable(getActivity().getApplicationContext()) && TrustMethods.isSimVerified(getActivity())) {
                Intent intent = new Intent(getActivity(), OtpVerificationActivity.class);
                intent.putExtra("checkTransferType", "addBeneficiary");
                intent.putExtra("mobileNo", mobileNo);
                intent.putExtra("nickname", nickname);
                intent.putExtra("accName", accName);
                intent.putExtra("accNo", accNo);
                intent.putExtra("ifscCode", ifscCode);
                intent.putExtra("benfMobNo", benfMobNo);
                intent.putExtra("mmid", mmid);
                intent.putExtra("withinChecked", withinChecked);
                intent.putExtra("ben_type", ben_type);
                intent.putExtra("upi_id", upiId);

                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(intent);
                trustMethods.activityOpenAnimation();


            } else {
                TrustMethods.displaySimErrorDialog(getActivity());
            }
        } else {
            TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
        }

    }

    private boolean validateForOwnBank() {

       /* if (TextUtils.isEmpty(etAccName.getText().toString().trim())) {
            etAccName.setError("Required Account Name");
            return false;
        } else */
            if (TextUtils.isEmpty(etAccNum.getText().toString().trim())) {
            etAccNum.setError("Required Account No.");
            return false;
        } else if (!trustMethods.isValidAccNo(etAccNum.getText().toString().trim(), getActivity())) {
            etAccNum.setError("Enter Valid Account Number");
            return false;
        } else if (TextUtils.isEmpty(etConfirmAccNumId.getText().toString().trim())) {
            etConfirmAccNumId.setError("Required Confirm Account No.");
            return false;
        } else if (!etAccNum.getText().toString().trim().equals(etConfirmAccNumId.getText().toString().trim())) {
            etConfirmAccNumId.setError("Confirm Account No. not matched");
            return false;
        }else {
            return true;
        }

    }

    private boolean validateForIMPSToAccountNeftRtgs() {

        if (TextUtils.isEmpty(etAccName.getText().toString().trim())) {
            etAccName.setError("Required Account Name");
            return false;
        } else if (TextUtils.isEmpty(etAccNum.getText().toString().trim())) {
            etAccNum.setError("Required Account No.");
            return false;
        } else if (!trustMethods.isValidAccNo(etAccNum.getText().toString().trim(), getActivity())) {
            etAccNum.setError("Enter Valid Account Number");
            return false;
        } else if (TextUtils.isEmpty(etConfirmAccNumId.getText().toString().trim())) {
            etConfirmAccNumId.setError("Required Confirm Account No.");
            return false;
        } else if (!etAccNum.getText().toString().trim().equals(etConfirmAccNumId.getText().toString().trim())) {
            etConfirmAccNumId.setError("Confirm Account No. not matched");
            return false;
        } else if (TextUtils.isEmpty(etIfscCode.getText().toString().trim())) {
            etIfscCode.setError("Required IFSC Code");
            return false;
        } else if (etIfscCode.getText().toString().length() != 11) {
            TrustMethods.showSnackBarMessage("IFSC code must be 11 character", coordinatorLayout);
            return false;
        } else if (!trustMethods.isValidImeI(etIfscCode.getText().toString().trim())) {
            etIfscCode.setError(getResources().getString(R.string.error_invalid_ifsc_code));
            return false;
        } else {
            return true;
        }

    }

    private boolean validateForIMPSToMobile() {

        if (TextUtils.isEmpty(etMobileNum.getText().toString().trim())) {
            etMobileNum.setError("Required Mobile Number");
            return false;
        } else if (TextUtils.isEmpty(etMmid.getText().toString().trim())) {
            etMmid.setError("Required MMID");
            return false;
        } else if (TextUtils.isEmpty(etConfirmMmidId.getText().toString().trim())) {
            etConfirmMmidId.setError("Required Confirm MMID");
            return false;
        } else if (!etMmid.getText().toString().trim().equals(etConfirmMmidId.getText().toString().trim())) {
            etConfirmMmidId.setError("Confirm MMID not matched");
            return false;
        } else {
            return true;
        }

    }

    private boolean validateForUpiId() {

        if (TextUtils.isEmpty(etNickName.getText().toString().trim())) {
            etNickName.setError("Required Nick Name");
            return false;
        }else if (TextUtils.isEmpty(etUpiId.getText().toString().trim())) {
            etUpiId.setError("Required UPI Id");
            return false;
        } else if (!TrustMethods.validateUPI(etUpiId.getText().toString().trim())) {
            etUpiId.setError("Invalid UPI Id,Please enter valid UPI id.");
            return false;
        } else if(etUpiId.getText().toString().trim().contains(" ")) {
            etUpiId.setError("No Space Allow.");
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 1) {
            addBeneficiallyFormFragment.addBenficiaryClick();
            Intent fundTransferMenuIntent = new Intent(getActivity(), MenuActivity.class);
            fundTransferMenuIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(fundTransferMenuIntent);
            trustMethods.activityOpenAnimation();
            Objects.requireNonNull(getDialog()).dismiss();
        } else if (resultCode == 2) {
            //Dismiss alert dialog here.
        } else if (resultCode == 3) {
            //Dismiss alert dialog here.
        } else if (resultCode == 0) {
            Intent intent = new Intent(getActivity(), LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            trustMethods.activityCloseAnimation();
        }else if(resultCode==55){
            //Dismiss alert dialog here.
        }

    }

    private void validateWithinBen(String accountNo, String ben_type) {
        try {
            if (TrustMethods.isSimAvailable(getActivity()) && TrustMethods.isSimVerified(getActivity())) {
                if (NetworkUtil.getConnectivityStatus(Objects.requireNonNull(getActivity()))) {
                    new BeneficieryAddValidationAsyncTask(getActivity(), accountNo, ben_type, "").execute();
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                }
            } else {
                TrustMethods.displaySimErrorDialog(Objects.requireNonNull(getActivity()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetBranchDetailsAsyncTask extends AsyncTask<Void, Void, String> {

        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String result;
        String ifscCode, bankName, branch, address;
        String actionName = "GET_LIST";
        private String errorCode;

        public GetBranchDetailsAsyncTask(Context ctx, String ifscCode) {
            this.ifscCode = ifscCode;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.GetBranchDetailsRequestUrl(ifscCode);

                if (!url.equals("")) {
                    result = HttpClientWrapper.getResponseGET(url, actionName, AppConstants.getAuth_token());
                }
                if (result == null || result.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResponse = (new JSONObject(result));
                if (jsonResponse.has("error")) {
                    error = jsonResponse.getString("error");
                    return error;
                }
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";

                if (responseCode.equals("1")) {
                    JSONObject responseObject = jsonResponse.getJSONObject("response");
                    if (responseObject.has("error")) {
                        error = responseObject.getString("error");
                        return error;
                    }

                    JSONArray jsonArray = responseObject.has("list") ? responseObject.getJSONArray("list") : null;
                    if (jsonArray != null && jsonArray.length() != 0) {

                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        bankName = jsonObject.has("bankname") ? jsonObject.getString("bankname") : "";
                        branch = jsonObject.has("branch") ? jsonObject.getString("branch") : "";
                        address = jsonObject.has("address") ? jsonObject.getString("address") : "";
                    }


                } else {
                    errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
                    error = jsonResponse.has("error_message") ? jsonResponse.getString("error_message") : "NA";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return error;
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);

            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                btnSaveBenef.setVisibility(View.VISIBLE); //TODO need to remove for production
                if (AppConstants.getMnu_verify_beneficiary_name().equalsIgnoreCase("1")) {
                    bnVerifyBenefId.setVisibility(View.VISIBLE);
                }
                if (!this.error.equals("")) {
                    if (!TextUtils.isEmpty(errorCode) && errorCode.equalsIgnoreCase("1001")) {
                      /*  btnSaveBenef.setVisibility(View.VISIBLE); //TODO need to uncomment this
                        if (AppConstants.getMnu_verify_beneficiary_name().equalsIgnoreCase("1")) {
                            bnVerifyBenefId.setVisibility(View.VISIBLE);
                        }*/
                        AlertDialogMethod.alertDialogOk(getActivity(), this.error, "", getResources().getString(R.string.btn_ok),
                                2, false, alertDialogOkListener);
                    } else if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        btnSaveBenef.setVisibility(View.GONE);
                        AlertDialogMethod.alertDialogOk(getActivity(), getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else if (!TextUtils.isEmpty(error) && TrustMethods.isSessionExpiredWithString(error)) {
                        btnSaveBenef.setVisibility(View.GONE);
                        AlertDialogMethod.alertDialogOk(getActivity(), getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        AlertDialogMethod.alertDialogOk(getActivity(), this.error, "", getResources().getString(R.string.btn_ok),
                                1, false, alertDialogOkListener);
                        btnSaveBenef.setVisibility(View.GONE);
                    }
                } else {


                   /* if (AppConstants.getMnu_verify_beneficiary_name().equalsIgnoreCase("0")) {
                        btnSaveBenef.setVisibility(View.VISIBLE);
                        bnVerifyBenefId.setVisibility(View.GONE);
                    } else {
                        bnVerifyBenefId.setVisibility(View.VISIBLE);
                    }*/
                    if (AppConstants.getMnu_verify_beneficiary_name().equalsIgnoreCase("1")) {
                        bnVerifyBenefId.setVisibility(View.VISIBLE);
                    }
                    btnSaveBenef.setVisibility(View.VISIBLE);
                    cardbankDetailsId.setVisibility(View.VISIBLE);
                    txtBankNameId.setText(bankName);
                    txtBranchAddressId.setText(address);
                    txtBranchNameId.setText(branch);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //check validation api call.
    @SuppressLint("StaticFieldLeak")
    private class BeneficieryAddValidationAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse,accname;
        ProgressDialog pDialog;
        String response;
        String actionName = "VALIDATE_BEN";
        String result;
        private String errorCode;
        private String accountNo, benType, ifscCode;


        public BeneficieryAddValidationAsyncTask(Context ctx, String accountNo, String benType, String ifscCode) {
            this.ctx = ctx;
            this.accountNo = accountNo;
            this.benType = benType;
            this.ifscCode = ifscCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                String url = TrustURL.getURLForFundTransferOwnAndNeft();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("ben_ac_no", accountNo);
                jsonObject.put("ben_type", benType);
                jsonObject.put("ben_ifsc", ifscCode);
                if (!url.equals("")) {
                    result = HttpClientWrapper.postWithActionAuthToken(url, jsonObject.toString(), actionName, AppConstants.getAuth_token());

                }
                if (result == null || result.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResponse = (new JSONObject(result));
                if (jsonResponse.has("error")) {
                    error = jsonResponse.getString("error");
                    return error;
                }
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";

                if (responseCode.equals("1")) {
                    finalResponse = jsonResponse.has("response") ? jsonResponse.getString("response") : "NA";
                   JSONObject jsonObjectRes = jsonResponse.has("response") ? jsonResponse.getJSONObject("response"):null;
                   if (jsonObjectRes != null){
                       JSONArray tablejsonarray= jsonObjectRes.has("Table")? jsonObjectRes.getJSONArray("Table"):null;
                       if (tablejsonarray != null){
                           accname = tablejsonarray.getJSONObject(0).getString("accname");
                       }
                   }


                    //accname = jsonResponse.has("accname") ? jsonResponse.getString("accname") : "";
                } else {
                    errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
                    error = jsonResponse.has("error_message") ? jsonResponse.getString("error_message") : "NA";
                }

            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return error;
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(getActivity(), getResources().getString(R.string.error_session_expire),
                                "",
                                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(getActivity()," ",
                                this.error,
                                getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }

                } else {
                    if (benType.equalsIgnoreCase("1")) {
                        btnSaveBenef.setVisibility(View.VISIBLE);
                        etAccName.setText(accname);
                        etAccName.setClickable(false);
                     //   etAccName.setEnabled(false);
                    //    addBeneficiary(AppConstants.getUSERMOBILENUMBER(), etNickName.getText().toString().trim(), etAccName.getText().toString().trim(), etAccNum.getText().toString().trim(), "", "", "", false, ben_type, "");
                    } else
                        if (benType.equalsIgnoreCase("2")) {
                        addBeneficiary(AppConstants.getUSERMOBILENUMBER(), etNickName.getText().toString().trim(), etAccName.getText().toString().trim(), etAccNum.getText().toString().trim(), etIfscCode.getText().toString().trim(), "", "",
                                false, ben_type, etUpiId.getText().toString().trim());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //check validation api call.
    @SuppressLint("StaticFieldLeak")
    private class VerifyBeneficieryAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String finalResponse;
        ProgressDialog pDialog;
        String response;
        String actionName = "VALIDATE_BEN";
        String result;
        private String errorCode;

        String mobile;
        String nickName;
        String accName;
        String accNumber;
        String benType;
        String ifscCode;
        String upiId;
        //p2P
        String benMobileNumber;
        String benMMID;
        private TMessage msg;

        public VerifyBeneficieryAsyncTask(Context ctx, String mobile, String nickName, String accName,
                                          String accNumber, String benType, String benMobileNumber, String benMMID,
                                          String ifscCode, String upiId) {
            this.ctx = ctx;
            this.mobile = mobile;
            this.nickName = nickName;
            this.accName = accName;
            this.accNumber = accNumber;
            this.benType = benType;
            this.benMobileNumber = benMobileNumber;
            this.benMMID = benMMID;
            this.ifscCode = ifscCode;
            this.upiId = upiId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {

            try {
                //String jsonString = "{\"filter\":[\"stan\"]}";
                String jsonString = "{\"filter\":[\"stan\",\"channel_ref_no\"]}";
                GenerateStanRRNModel generateStanRRNModel = TMessageUtil.GetNextStanRrn(getActivity(), jsonString,
                        TrustURL.GenerateStanRrnUrl(), AppConstants.getAuth_token());

                String url = TrustURL.httpCallUrl();
                MessageDtoBuilder msgDto = new MessageDtoBuilder();

                if (generateStanRRNModel.getError() != null) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                } else {
                    if (ben_type.equalsIgnoreCase("3")) { //impstomobile
                        msg = msgDto.VerifyP2PBeneficeryDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                                mobile, remmittterAccNo,
                                remmitterName, benMobileNumber,
                                benMMID/*mMmid*/,
                                AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no());
                        Log.d("msg.GetXml():", msg.GetXml());

                    } else if (ben_type.equalsIgnoreCase("2")) {//impsToAccount

                        msg = msgDto.VerifyP2ABeneficeryDto(TMessageUtil.GetLocalTxnDtTime(), generateStanRRNModel.getStan(),
                                mobile, remmittterAccNo, remmitterName,
                                accNumber, ifscCode,
                                AppConstants.INSTITUTION_ID, generateStanRRNModel.getChannel_ref_no());
                        Log.d("msg.GetXml():", msg.GetXml());
                    } else if (ben_type.equalsIgnoreCase("4")) { //TODO for UPI.

                    }
                    String base64EncodedRequestJson = Base64.encodeToString(msg.GetXml().getBytes(), Base64.NO_WRAP);

                    String jsonStringRequest = "{\"data\":\"" + base64EncodedRequestJson + "\"}";
                    TrustMethods.LogMessage("", "base64EncodedRequestJson : " + jsonStringRequest);

                    if (!url.equals("")) {
                        //   TrustMethods.LogMessage(TAG, "URL:-" + url);
                        result = HttpClientWrapper.postWitAuthHeader(url, jsonStringRequest, AppConstants.getAuth_token());
                        // TrustMethods.LogMessage(TAG, "Frm Enquiry details response-->" + response);
                    }
                    if (result == null || result.equals("")) {
                        error = AppConstants.SERVER_NOT_RESPONDING;
                        return error;
                    }
                    JSONObject jsonResponse = (new JSONObject(result));
                    if (jsonResponse.has("error")) {
                        error = jsonResponse.getString("error");
                        return error;
                    }
                    String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";

                    if (responseCode.equals("1")) {
                        response = jsonResponse.has("response") ? jsonResponse.getString("response") : "NA";

                        String responseValue = TrustMethods.decodeBase64(response);
                        Log.d("responseValue", responseValue);

                        ResponseEntity resParse = TMessage.ParseMessage(responseValue);
                        TMessage responseMsg = (TMessage) resParse.response;

                        if (responseMsg.ActCode.Value.equals("000")) {
                            finalResponse = responseMsg.BeneName.Value;
                        } else {
                            error = "Act Code: " + responseMsg.ActCode.Value + ": " + responseMsg.ActCodeDesc.Value;
                        }
                        onProgressUpdate();
                    } else {
                        errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
                        error = jsonResponse.has("error_message") ? jsonResponse.getString("error_message") : "NA";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return error;
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            return response;

        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(getActivity(), getResources().getString(R.string.error_session_expire),
                                "",
                                getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        //TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                        AlertDialogMethod.alertDialogOk(getActivity(), " ",
                                this.error,
                                getResources().getString(R.string.btn_ok), 55, false, alertDialogOkListener);
                    }

                } else {

                    cardVerifyBenDetailsId.setVisibility(View.VISIBLE);
                    txtVerifyBenId.setText(finalResponse);
                    btnSaveBenef.setVisibility(View.VISIBLE);

                    if (benType.equalsIgnoreCase("2")) {
                        etAccNum.setEnabled(false);
                        etAccName.setEnabled(false);
                        etIfscCode.setEnabled(false);
                        etConfirmAccNumId.setEnabled(false);
                    } else if (benType.equalsIgnoreCase("3")) {
                        etMmid.setEnabled(false);
                        etMobileNum.setEnabled(false);
                        etConfirmMmidId.setEnabled(false);
                        etConfirmMmidId.setFocusable(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void getIMPSremitterAccountsDetails() {
        ArrayList<GetUserProfileModal> accountsArrayList = trustMethods.getArrayList(getActivity(), "AccountListPref");
        for (int i = 0; i < accountsArrayList.size(); i++) {
            GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
            if (TrustMethods.isAccountTypeIsImpsRegValid(getUserProfileModal.getActType(), getUserProfileModal.getIs_imps_reg())) {
                remmittterAccNo = getUserProfileModal.getAccNo();
                remmitterName = getUserProfileModal.getName();
                break;
            }
        }
    }
}
