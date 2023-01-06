package com.trustbank.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.trustbank.Model.AccountDetailsModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AccountDetailsActivity extends AppCompatActivity implements AlertDialogOkListener {
    private TrustMethods method;
    private Spinner spinnerFrmAct;
    private TextView txtCustName, txtCustMobNo, txtCustEmail;
    private ArrayList<GetUserProfileModal> accountsArrayList;
    private List<String> accountList;
    private CardView accDetailsCardView;
    private CoordinatorLayout accDetCoordinatorLayout;
    private LinearLayout headNmLayout;
    private TextView txtHeadName;
    private LinearLayout nameLayout;
    private LinearLayout clientNameLayout;
    private LinearLayout balLayout;
    private TextView txtBalance;
    private LinearLayout descLayout;
    private TextView txtDesc;
    private LinearLayout accOpenDtLayout;
    private TextView txtAccOpenDate;
    private LinearLayout dobLayout;
    private TextView txtDob;
    private LinearLayout mobileNoLayout;
    private LinearLayout phoneNoLayout, ifscCodeLayoutId;
    private TextView txtPhoneNo;
    private LinearLayout emailIdLayout;
    private LinearLayout addressLayout;
    private TextView txtAddress;
    private LinearLayout nationalityLayout;
    private TextView txtNationality, txtIFSCCodeId;
    private TextView txtName;
    private LinearLayout withdrawlamtLayout;
    private TextView txtWithdrawlAmount;
    private LinearLayout expiryDateLayout;
    private TextView txtExpiryDate;
    private LinearLayout installmentAmountLayout;
    private TextView txtInstallmentAmount;
    private LinearLayout depositPeriodLayout;
    private TextView txtDepositPeriod;
    private LinearLayout maturityAmountLayout;
    private TextView txtMaturityAmount;
    private LinearLayout maturityDateLayout;
    private TextView txtMaturityDate;
    private LinearLayout schemeNameLayout;
    private TextView txtSchemeName;
    private LinearLayout interestRateLayout;
    private TextView txtInterestRate;
    private LinearLayout accountstatusLayoutId,sanctionAmtLayoutId,sanctionDateLayoutId;
    private TextView txtAccStatusId,txtSanctionAmtId,txtSanctionDateId;

    private AlertDialogOkListener alertDialogOkListener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            if (savedInstanceState != null) {
                Object currentPID = String.valueOf(android.os.Process.myPid());
                // Check current PID with old PID
                if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                    // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                    TrustMethods.naviagteToSplashScreen(AccountDetailsActivity.this);
                }
            }
        }
        SetTheme.changeToTheme(AccountDetailsActivity.this, false);
        setContentView(R.layout.activity_account_details);
        inIt();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }

    private void inIt() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        method = new TrustMethods(AccountDetailsActivity.this);
        spinnerFrmAct = findViewById(R.id.spinnerFrmActId);
        headNmLayout = findViewById(R.id.headNmLayoutId);
        txtHeadName = findViewById(R.id.txtHeadNameId);
        nameLayout = findViewById(R.id.NameLayoutId);
        txtName = findViewById(R.id.txtNameId);
        clientNameLayout = findViewById(R.id.ClientNameLayoutId);
        txtCustName = findViewById(R.id.txtCustNameId);
        balLayout = findViewById(R.id.balLayoutId);
        txtBalance = findViewById(R.id.txtBalanceId);
        descLayout = findViewById(R.id.descLayoutId);
        txtDesc = findViewById(R.id.txtDescId);
        accOpenDtLayout = findViewById(R.id.accOpenDtLayoutId);
        txtAccOpenDate = findViewById(R.id.txtAccOpenDateId);
        dobLayout = findViewById(R.id.dobLayoutId);

        txtDob = findViewById(R.id.txtDobId);
        mobileNoLayout = findViewById(R.id.mobileNoLayoutId);
        txtCustMobNo = findViewById(R.id.txtCustMobNoId);
        phoneNoLayout = findViewById(R.id.phoneNoLayoutId);
        txtPhoneNo = findViewById(R.id.txtPhoneNoId);
        emailIdLayout = findViewById(R.id.emailIdLayout);
        txtCustEmail = findViewById(R.id.txtCustEmailId);
        addressLayout = findViewById(R.id.addressLayoutId);
        txtAddress = findViewById(R.id.txtAddressId);
        nationalityLayout = findViewById(R.id.nationalityLayoutId);
        txtNationality = findViewById(R.id.txtNationalityId);
        accDetCoordinatorLayout = findViewById(R.id.accDetCoordinatorLayoutId);
        accDetailsCardView = findViewById(R.id.accDetailsCardViewId);
        txtIFSCCodeId = findViewById(R.id.txtIFSCCodeId);
        ifscCodeLayoutId = findViewById(R.id.ifscCodeLayoutId);
        withdrawlamtLayout = findViewById(R.id.withdralAmountLayoutId);
        txtWithdrawlAmount = findViewById(R.id.txtWithdrawlAmountId);

        accountstatusLayoutId = findViewById(R.id.accountstatusLayoutId);
        txtAccStatusId = findViewById(R.id.txtAccStatusId);
        expiryDateLayout = findViewById(R.id.expiryDateLayoutId);
        txtExpiryDate = findViewById(R.id.txtexpiryDateId);
        installmentAmountLayout = findViewById(R.id.instalmentAmountLayoutId);
        txtInstallmentAmount = findViewById(R.id.txtinstalmentAmountId);
        depositPeriodLayout = findViewById(R.id.depositPeriodLayoutId);
        txtDepositPeriod = findViewById(R.id.txtDepositPeriodId);
        maturityAmountLayout = findViewById(R.id.maturityAmountLayoutId);
        txtMaturityAmount = findViewById(R.id.txtMaturityAmountId);
        maturityDateLayout = findViewById(R.id.maturityDateLayoutId);
        txtMaturityDate = findViewById(R.id.txtMaturityDateId);
        schemeNameLayout = findViewById(R.id.scheamNameLayoutId);
        txtSchemeName = findViewById(R.id.txtScheamNameId);
        interestRateLayout = findViewById(R.id.interestRateLayoutId);
        txtInterestRate = findViewById(R.id.txtinterestRateId);

        sanctionAmtLayoutId = findViewById(R.id.sanctionAmtLayoutId);
        sanctionDateLayoutId = findViewById(R.id.sanctionDateLayoutId);
        txtSanctionAmtId = findViewById(R.id.txtSanctionAmtId);
        txtSanctionDateId = findViewById(R.id.txtSanctionDateId);


        accNumberSpinner();
    }

    private void accNumberSpinner() {
        try {
            accountsArrayList = method.getArrayList(AccountDetailsActivity.this, "AccountListPref");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (accountsArrayList != null && accountsArrayList.size() > 0) {
            accountList = new ArrayList<>();
            accountList.add(0, "Select Account Number");
            for (int i = 0; i < accountsArrayList.size(); i++) {
                GetUserProfileModal getUserProfileModal = accountsArrayList.get(i);
                String accNo = getUserProfileModal.getAccNo();
                String accTypeCode = getUserProfileModal.getAcTypeCode();
                accountList.add(accNo + " - " + accTypeCode);

            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AccountDetailsActivity.this, android.R.layout.simple_spinner_item, accountList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFrmAct.setAdapter(adapter);
        }

        spinnerFrmAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    if (position != 0) {
                        String selectedAccNo = (String) adapterView.getItemAtPosition(position);
                        String accNo = "";
                        if (selectedAccNo.contains("-")) {
                            String[] accounts = selectedAccNo.split("-");
                            accNo = accounts[0];
                        } else {
                            accNo = selectedAccNo;
                        }

                        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(AccountDetailsActivity.this)) {
                            if (NetworkUtil.getConnectivityStatus(AccountDetailsActivity.this)) {
                                new CreateProfileAsyncTask(AccountDetailsActivity.this, accNo).execute();
                            } else {
                                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), accDetCoordinatorLayout);
                            }
                        } else {
                            TrustMethods.displaySimErrorDialog(AccountDetailsActivity.this);
                        }

                    } else {
                        accDetailsCardView.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onDialogOk(int resultCode) {
        switch (resultCode) {
            case 0:
                Intent intentLogin = new Intent(getApplicationContext(), LockActivity.class);
                intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentLogin);
                method.activityCloseAnimation();
                break;
            default:
                break;
        }

    }


    private class CreateProfileAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";

        AccountDetailsModel accountDetailsModel;
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String actionName = "GET_ACCOUNT_DETAILS";
        String mAccNo;
        String result;
        ArrayList<AccountDetailsModel> accountDetailsModelArrayList = new ArrayList<>();
        private String errorCode;

        public CreateProfileAsyncTask(Context ctx, String accNo) {
            this.ctx = ctx;
            this.mAccNo = accNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AccountDetailsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.getAccountDetails(mAccNo);

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
                    JSONArray accountJsonArray = jsonResponse.getJSONObject("response").getJSONArray("account");
                    JSONObject accountJsonObject = accountJsonArray.getJSONObject(0);

                    String mAccounttypecode = accountJsonObject.has("accounttypecode") ? accountJsonObject.getString("accounttypecode") : "";
                    //if(mAccounttypecode.equals("78")|| mAccounttypecode.equals("81")||mAccounttypecode.equals("75")) {
                    String mClientname = accountJsonObject.has("clientname") ? accountJsonObject.getString("clientname") : "";
                    String mBalance = accountJsonObject.has("balance") ? accountJsonObject.getString("balance") : "";
                    String mHeadname = accountJsonObject.has("headname") ? accountJsonObject.getString("headname") : "";
                    String mName = accountJsonObject.has("name") ? accountJsonObject.getString("name") : "";
                    String mOrgelementId = accountJsonObject.has("orgelementId") ? accountJsonObject.getString("orgelementId") : "";
                    String mCurrency = accountJsonObject.has("currency") ? accountJsonObject.getString("currency") : "";
                    String mCurrencyCode = accountJsonObject.has("currencyCode") ? accountJsonObject.getString("currencyCode") : "";
                    String mDateofbirth = accountJsonObject.has("dateofbirth") ? accountJsonObject.getString("dateofbirth") : "";
                    String mAddressline1 = accountJsonObject.has("addressline1") ? accountJsonObject.getString("addressline1") : "";
                    String mAddressline2 = accountJsonObject.has("addressline2") ? accountJsonObject.getString("addressline2") : "";
                    String mAddressline3 = accountJsonObject.has("addressline3") ? accountJsonObject.getString("addressline3") : "";
                    String mEmail = accountJsonObject.has("email") ? accountJsonObject.getString("email") : "";
                    String mTelephone1 = accountJsonObject.has("telephone1") ? accountJsonObject.getString("telephone1") : "";
                    String mMobile = accountJsonObject.has("mobile") ? accountJsonObject.getString("mobile") : "";
                    String mCountry = accountJsonObject.has("country") ? accountJsonObject.getString("country") : "";
                    String mCityname = accountJsonObject.has("cityname") ? accountJsonObject.getString("cityname") : "";
                    String mDescription = accountJsonObject.has("description") ? accountJsonObject.getString("description") : "";
                    String mAccountopendate = accountJsonObject.has("accountopendate") ? accountJsonObject.getString("accountopendate") : "";
                    String mLedgernumber = accountJsonObject.has("ledgernumber") ? accountJsonObject.getString("ledgernumber") : "";
                    String mNationality = accountJsonObject.has("nationality") ? accountJsonObject.getString("nationality") : "";
                    String mClientid = accountJsonObject.has("clientid") ? accountJsonObject.getString("clientid") : "";
                    String ifscCode = accountJsonObject.has("ifsccode") ? accountJsonObject.getString("ifsccode") : "";
                    String withdrawableAmount = accountJsonObject.has("withdrawableamount") ? accountJsonObject.getString("withdrawableamount") : "";
                    String accountstatus = accountJsonObject.has("accountstatus") ? accountJsonObject.getString("accountstatus") : "";

                    accountDetailsModel = new AccountDetailsModel();
                    accountDetailsModel.setAccountTypeCode(mAccounttypecode);
                    accountDetailsModel.setClientName(mClientname);
                    accountDetailsModel.setBalance(mBalance);
                    accountDetailsModel.setHeadName(mHeadname);
                    accountDetailsModel.setName(mName);
                    accountDetailsModel.setOrgelementId(mOrgelementId);
                    accountDetailsModel.setCurrency(mCurrency);
                    accountDetailsModel.setCurrencyCode(mCurrencyCode);
                    accountDetailsModel.setDateOfBirth(mDateofbirth);
                    accountDetailsModel.setAddressLine1(mAddressline1);
                    accountDetailsModel.setAddressLine2(mAddressline2);
                    accountDetailsModel.setAddressLine3(mAddressline3);
                    accountDetailsModel.setEmail(mEmail);
                    accountDetailsModel.setTelephone1(mTelephone1);
                    accountDetailsModel.setMobile(mMobile);
                    accountDetailsModel.setCountry(mCountry);
                    accountDetailsModel.setCityName(mCityname);
                    accountDetailsModel.setDescription(mDescription);
                    accountDetailsModel.setAccountOpenDate(mAccountopendate);
                    accountDetailsModel.setLedgerNumber(mLedgernumber);
                    accountDetailsModel.setNationality(mNationality);
                    accountDetailsModel.setClientId(mClientid);
                    accountDetailsModel.setIfscCode(ifscCode);
                    accountDetailsModel.setWithdrawableAmount(withdrawableAmount);
                    accountDetailsModel.setAccountStatus(accountstatus);


//
//                        accountDetailsModel = new AccountDetailsModel();
//                        accountDetailsModel.setAccountTypeCode(mAccounttypecode);
//                        accountDetailsModel.setClientName(mClientname);
//                        accountDetailsModel.setBalance(mBalance);
//                        accountDetailsModel.setHeadName(mHeadname);
//                        accountDetailsModel.setName(mName);
//                        accountDetailsModel.setOrgelementId(mOrgelementId);
//                        accountDetailsModel.setCurrency(mCurrency);
//                        accountDetailsModel.setCurrencyCode(mCurrencyCode);
//                        accountDetailsModel.setDateOfBirth(mDateofbirth);
//                        accountDetailsModel.setAddressLine1(mAddressline1);
//                        accountDetailsModel.setAddressLine2(mAddressline2);
//                        accountDetailsModel.setAddressLine3(mAddressline3);
//                        accountDetailsModel.setEmail(mEmail);
//                        accountDetailsModel.setTelephone1(mTelephone1);
//                        accountDetailsModel.setMobile(mMobile);
//                        accountDetailsModel.setCountry(mCountry);
//                        accountDetailsModel.setCityName(mCityname);
//                        accountDetailsModel.setDescription(mDescription);
//                        accountDetailsModel.setAccountOpenDate(mAccountopendate);
//                        accountDetailsModel.setLedgerNumber(mLedgernumber);
//                        accountDetailsModel.setNationality(mNationality);
//                        accountDetailsModel.setClientId(mClientid);
//                        accountDetailsModel.setIfscCode(ifscCode);
//                        accountDetailsModel.setWithdrawableAmount(withdrawableAmount);
//                        accountDetailsModel.setAccountStatus(accountstatus);

                      /*  accountDetailsModel.setExpiryDate("");
                        accountDetailsModel.setInstallmentAmount("");
                        accountDetailsModel.setDepositPeriod("");
                        accountDetailsModel.setMaturityAmount("");
                        accountDetailsModel.setMaturityDate("");
                        accountDetailsModel.setSchemeName("");
                        accountDetailsModel.setInterestRate("");*/
                    //todo added new varibales.
                    // if(mAccounttypecode.equals("81")||mAccounttypecode.equals("75")) {
                    String expiryDate = "",installmentAmount= "",depositPeriod="",maturityAmount="",maturityDate="",schemeName="",
                            interestRate="",disbursementDate = "",disbursementAmount = "";
                    JSONObject resObject = jsonResponse.getJSONObject("response");
                    JSONArray tableJsonArray = resObject.has("Table1") ? resObject.getJSONArray("Table1") : null;
                    if (tableJsonArray != null) {
                        JSONObject tableJsonObject = tableJsonArray.getJSONObject(0);
                        if (tableJsonObject != null) {
                             expiryDate = tableJsonObject.has("ExpiryDate") ? tableJsonObject.getString("ExpiryDate") : "";
                             installmentAmount = tableJsonObject.has("InstallmentAmount") ? tableJsonObject.getString("InstallmentAmount") : "";
                             depositPeriod = tableJsonObject.has("DepositPeriod") ? tableJsonObject.getString("DepositPeriod") : "";
                             maturityAmount = tableJsonObject.has("MaturityAmount") ? tableJsonObject.getString("MaturityAmount") : "";
                             maturityDate = tableJsonObject.has("MaturityDate") ? tableJsonObject.getString("MaturityDate") : "";
                             schemeName = tableJsonObject.has("SchemeName") ? tableJsonObject.getString("SchemeName") : "";
                             interestRate = tableJsonObject.has("InterestRate") ? tableJsonObject.getString("InterestRate") : "";
                            disbursementDate = tableJsonObject.has("DisbursementDate") ? tableJsonObject.getString("DisbursementDate") : "";
                            disbursementAmount = tableJsonObject.has("DisbursementAmount") ? tableJsonObject.getString("DisbursementAmount") : "";


                        }
                    }

                    accountDetailsModel.setExpiryDate(expiryDate);
                    accountDetailsModel.setInstallmentAmount(installmentAmount);
                    accountDetailsModel.setDepositPeriod(depositPeriod);
                    accountDetailsModel.setMaturityAmount(maturityAmount);
                    accountDetailsModel.setMaturityDate(maturityDate);
                    accountDetailsModel.setSchemeName(schemeName);
                    accountDetailsModel.setInterestRate(interestRate);
                    accountDetailsModel.setDisbursementAmount(disbursementAmount);
                    accountDetailsModel.setDisbursementDate(disbursementDate);


                    //TODO Need to set in the Text View.

                    //  }
                    // }
                    accountDetailsModelArrayList.add(accountDetailsModel);
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
                if (accountDetailsModelArrayList != null && accountDetailsModelArrayList.size() != 0) {
                    accDetailsCardView.setVisibility(View.VISIBLE);

                    if (!accountDetailsModelArrayList.get(0).getHeadName().equalsIgnoreCase("")) {
                        headNmLayout.setVisibility(View.VISIBLE);
                        txtHeadName.setText(accountDetailsModelArrayList.get(0).getHeadName());
                    } else {
                        headNmLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getName().equalsIgnoreCase("")) {
                        nameLayout.setVisibility(View.VISIBLE);
                        txtName.setText(accountDetailsModelArrayList.get(0).getName());
                    } else {
                        nameLayout.setVisibility(View.GONE);
                    }

                    if (!accountDetailsModelArrayList.get(0).getIfscCode().equalsIgnoreCase("")) {
                        ifscCodeLayoutId.setVisibility(View.VISIBLE);
                        txtIFSCCodeId.setText(accountDetailsModelArrayList.get(0).getIfscCode());
                    } else {
                        ifscCodeLayoutId.setVisibility(View.GONE);
                    }

                    if (!accountDetailsModelArrayList.get(0).getClientName().equalsIgnoreCase("")) {
                        clientNameLayout.setVisibility(View.VISIBLE);
                        txtCustName.setText(accountDetailsModelArrayList.get(0).getClientName());
                    } else {
                        clientNameLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getBalance().equalsIgnoreCase("")) {
                        balLayout.setVisibility(View.VISIBLE);
                        txtBalance.setText(TrustMethods.getValueCommaSeparated(accountDetailsModelArrayList.get(0).getBalance()));

                    } else {
                        balLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getDescription().equalsIgnoreCase("")) {
                        descLayout.setVisibility(View.VISIBLE);
                        txtDesc.setText(accountDetailsModelArrayList.get(0).getDescription());
                    } else {
                        descLayout.setVisibility(View.GONE);
                    }

                    if (!accountDetailsModelArrayList.get(0).getAccountOpenDate().equalsIgnoreCase("")) {
                        accOpenDtLayout.setVisibility(View.VISIBLE);
                        txtAccOpenDate.setText(accountDetailsModelArrayList.get(0).getAccountOpenDate());
                    } else {
                        accOpenDtLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getDateOfBirth().equalsIgnoreCase("")) {
                        dobLayout.setVisibility(View.VISIBLE);
                        txtDob.setText(accountDetailsModelArrayList.get(0).getDateOfBirth());
                    } else {
                        dobLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getMobile().equalsIgnoreCase("")) {
                        mobileNoLayout.setVisibility(View.VISIBLE);
                        txtCustMobNo.setText(accountDetailsModelArrayList.get(0).getMobile());
                    } else {
                        mobileNoLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getTelephone1().equalsIgnoreCase("")) {
                        phoneNoLayout.setVisibility(View.VISIBLE);
                        txtPhoneNo.setText(accountDetailsModelArrayList.get(0).getTelephone1());
                    } else {
                        phoneNoLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getEmail().equalsIgnoreCase("")) {
                        emailIdLayout.setVisibility(View.VISIBLE);
                        if (TextUtils.isEmpty(accountDetailsModelArrayList.get(0).getEmail()) ||
                                accountDetailsModelArrayList.get(0).getEmail().equalsIgnoreCase("null")) {
                            txtCustEmail.setText("");
                        } else {
                            txtCustEmail.setText(accountDetailsModelArrayList.get(0).getEmail());
                        }

                    } else {
                        emailIdLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getNationality().equalsIgnoreCase("")) {
                        nationalityLayout.setVisibility(View.VISIBLE);
                        txtNationality.setText(accountDetailsModelArrayList.get(0).getNationality());
                    } else {
                        nationalityLayout.setVisibility(View.GONE);
                    }


                    if (!accountDetailsModelArrayList.get(0).getWithdrawableAmount().equalsIgnoreCase("")) {
                        withdrawlamtLayout.setVisibility(View.VISIBLE);
                        txtWithdrawlAmount.setText(accountDetailsModelArrayList.get(0).getWithdrawableAmount());
                    } else {
                        withdrawlamtLayout.setVisibility(View.GONE);
                    }

                    if (!accountDetailsModelArrayList.get(0).getAccountStatus().equalsIgnoreCase("")) {
                        accountstatusLayoutId.setVisibility(View.VISIBLE);
                        txtAccStatusId.setText(accountDetailsModelArrayList.get(0).getAccountStatus());
                    } else {
                        accountstatusLayoutId.setVisibility(View.GONE);
                    }
                    //Todo


                    if (!accountDetailsModelArrayList.get(0).getExpiryDate().equalsIgnoreCase("")) {
                        expiryDateLayout.setVisibility(View.VISIBLE);
                        txtExpiryDate.setText(accountDetailsModelArrayList.get(0).getExpiryDate());
                    } else {
                        expiryDateLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getInstallmentAmount().equalsIgnoreCase("")) {
                        installmentAmountLayout.setVisibility(View.VISIBLE);
                        txtInstallmentAmount.setText(accountDetailsModelArrayList.get(0).getInstallmentAmount());
                    } else {
                        installmentAmountLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getDepositPeriod().equalsIgnoreCase("")) {
                        depositPeriodLayout.setVisibility(View.VISIBLE);
                        txtDepositPeriod.setText(accountDetailsModelArrayList.get(0).getDepositPeriod());
                    } else {
                        depositPeriodLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getMaturityAmount().equalsIgnoreCase("")) {
                        maturityAmountLayout.setVisibility(View.VISIBLE);
                        txtMaturityAmount.setText(accountDetailsModelArrayList.get(0).getMaturityAmount());
                    } else {
                        maturityAmountLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getMaturityDate().equalsIgnoreCase("")) {
                        maturityDateLayout.setVisibility(View.VISIBLE);
                        txtMaturityDate.setText(accountDetailsModelArrayList.get(0).getMaturityDate());
                    } else {
                        maturityDateLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getSchemeName().equalsIgnoreCase("")) {
                        schemeNameLayout.setVisibility(View.VISIBLE);
                        txtSchemeName.setText(accountDetailsModelArrayList.get(0).getSchemeName());
                    } else {
                        schemeNameLayout.setVisibility(View.GONE);
                    }
                    if (!accountDetailsModelArrayList.get(0).getInterestRate().equalsIgnoreCase("")) {
                        interestRateLayout.setVisibility(View.VISIBLE);
                        txtInterestRate.setText(accountDetailsModelArrayList.get(0).getInterestRate());
                    } else {
                        interestRateLayout.setVisibility(View.GONE);
                    }

                    if (!accountDetailsModelArrayList.get(0).getDisbursementAmount().equalsIgnoreCase("")) {
                        sanctionAmtLayoutId.setVisibility(View.VISIBLE);
                        txtSanctionAmtId.setText( accountDetailsModelArrayList.get(0).getDisbursementAmount());
                    } else {
                        sanctionAmtLayoutId.setVisibility(View.GONE);
                    }

                    if (!accountDetailsModelArrayList.get(0).getDisbursementDate().equalsIgnoreCase("")) {
                        sanctionDateLayoutId.setVisibility(View.VISIBLE);
                        txtSanctionDateId.setText(accountDetailsModelArrayList.get(0).getDisbursementDate());
                    } else {
                        sanctionDateLayoutId.setVisibility(View.GONE);
                    }

                    if (!accountDetailsModelArrayList.get(0).getAddressLine1().equalsIgnoreCase("") ||
                            !accountDetailsModelArrayList.get(0).getAddressLine2().equalsIgnoreCase("") ||
                            !accountDetailsModelArrayList.get(0).getAddressLine3().equalsIgnoreCase("") ||
                            !accountDetailsModelArrayList.get(0).getCityName().equalsIgnoreCase("") ||
                            !accountDetailsModelArrayList.get(0).getCountry().equalsIgnoreCase("")) {
                        addressLayout.setVisibility(View.VISIBLE);

                        txtAddress.setText(accountDetailsModelArrayList.get(0).getAddressLine1() + ", "
                                + accountDetailsModelArrayList.get(0).getAddressLine2() + ", " +
                                accountDetailsModelArrayList.get(0).getAddressLine3() + ", " +
                                accountDetailsModelArrayList.get(0).getCityName() + ", " +
                                accountDetailsModelArrayList.get(0).getCountry());
                    } else {
                        addressLayout.setVisibility(View.GONE);
                    }
                } else {
                    accDetailsCardView.setVisibility(View.GONE);
                    if (!this.error.equals("")) {
                        if (TrustMethods.isSessionExpired(errorCode)) {
                            AlertDialogMethod.alertDialogOk(AccountDetailsActivity.this,
                                    getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok),
                                    0, false, alertDialogOkListener);
                        } else {
                            TrustMethods.showSnackBarMessage(this.error, accDetCoordinatorLayout);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(AccountDetailsActivity.this);
    }

}
