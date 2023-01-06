package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.MenuItem;

import com.trustbank.Module.ImageTextMenuModel;
import com.trustbank.R;
import com.trustbank.adapter.AccountsAdapter;
import com.trustbank.adapter.MenuAdapter;
import com.trustbank.util.AppConstants;
import com.trustbank.util.ItemOffsetDecoration;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountsActivity extends AppCompatActivity {

    @BindView(R.id.recyclerAccountsId)
    RecyclerView recyclerAccounts;

    RecyclerView recyclerViewHoriListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            if (savedInstanceState != null) {
                Object currentPID = String.valueOf(android.os.Process.myPid());
                // Check current PID with old PID
                if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                    // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                    TrustMethods.naviagteToSplashScreen(AccountsActivity.this);
                }
            }
        }
        SetTheme.changeToTheme(AccountsActivity.this, false);
        setContentView(R.layout.activity_accounts);
        ButterKnife.bind(this);
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

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }


            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);

            recyclerAccounts.setNestedScrollingEnabled(false);
            recyclerAccounts.setItemAnimator(new DefaultItemAnimator());
            recyclerAccounts.setHasFixedSize(true);
            recyclerAccounts.setLayoutManager(gridLayoutManager);

            if ((!TextUtils.isEmpty(AppConstants.getPlay_store_validate())) && AppConstants.getPlay_store_validate().equalsIgnoreCase("1")) {
                staticData(gridLayoutManager);
            } else if (AppConstants.getUSERMOBILENUMBER().equalsIgnoreCase(AppConstants.getPlayStoreDemoUserMobile()) &&
                    AppConstants.getCLIENTID().equalsIgnoreCase(AppConstants.getPlayStoreDemoPasswordClientid())) {
                staticData(gridLayoutManager);
            } else {
                ImageTextMenuModel[] oldItemData = {
                        new ImageTextMenuModel("Account Details", R.drawable.fd_rd_reckoner, AppConstants.getMnu_accounts_menu_accountdetails(), "Account Details"),
                        new ImageTextMenuModel("Balance Enquiry", R.drawable.fd_rd_reckoner, AppConstants.getMnu_accounts_menu_balenquiry(), "Balance Enquiry"),
                        new ImageTextMenuModel("Balance Enquiry CBS", R.drawable.fd_rd_reckoner, AppConstants.getMnu_accounts_menu_balenquiry_cbs(), "Balance Enquiry"),
                        new ImageTextMenuModel("Statement Request", R.drawable.change_password, AppConstants.getMnu_accounts_menu_ministatemnt(), "Mini Statement"),
                        new ImageTextMenuModel("Statement Request CBS", R.drawable.change_password, AppConstants.getMnu_accounts_menu_ministatemnt_cbs(), "Mini Statement"),
                        new ImageTextMenuModel("Last 5 IMPS Transactions", R.drawable.fd_rd_reckoner, AppConstants.getMnu_accounts_menu_last5imps(), "Last 5 IMPS Transactions"),
                        new ImageTextMenuModel("Last 5 IMPS Transactions CBS", R.drawable.fd_rd_reckoner, AppConstants.getMnu_accounts_menu_last5imps_cbs(), "Last 5 IMPS Transactions"),
                        new ImageTextMenuModel("Show MMID", R.drawable.change_password, AppConstants.getMnu_accounts_menu_showmmid(), "Show MMID"),
                        new ImageTextMenuModel("Show MMID CBS", R.drawable.change_password, AppConstants.getMnu_accounts_menu_showmmid_cbs(), "Show MMID"),
                        new ImageTextMenuModel("NEFT Enquiry", R.drawable.change_password, AppConstants.getMnu_accounts_menu_neftenquiry(), "NEFT Enquiry"),
                        new ImageTextMenuModel("Chequebook Request", R.drawable.change_password, AppConstants.getMnu_checkbook_request(), "Chequebook Request"),
                        new ImageTextMenuModel("mPassBook", R.drawable.change_password, AppConstants.getMnu_account_statement(), "mPassBook"),
                        new ImageTextMenuModel("Cheque Status", R.drawable.change_password, AppConstants.getInqueriChquebookStatus(), "Cheque Status"),
                        new ImageTextMenuModel("Stop Cheque Request", R.drawable.change_password, AppConstants.getStopChequebookStatus(), "Stop Cheque Request"),
                        new ImageTextMenuModel("PPS Request", R.drawable.change_password, AppConstants.getMnu_pps_request(), "Positive Pay Request"),
                        new ImageTextMenuModel("PPS Request Enquiry", R.drawable.change_password, AppConstants.getMnu_pps_request_enquiry(), "Positive Pay Request Enquiry"),
                        new ImageTextMenuModel("Block Debit Card", R.drawable.change_password, AppConstants.getMnu_block_debit_card(), "Block Debit Card"),
                        new ImageTextMenuModel("Check Transaction Status", R.drawable.change_password, AppConstants.getMnu_check_imps_transaction_status(), "Check Transaction Status"),
                        new ImageTextMenuModel("Complaint Managements", R.drawable.change_password, AppConstants.getMnu_bill_pay_complaint_management(), "Complaint Managements"),
                        new ImageTextMenuModel("Block Debit Card Switch", R.drawable.change_password, AppConstants.getMnu_block_debit_card_switch(), "Block Debit Card Switch"),
                        new ImageTextMenuModel("ECS Mandate Cancellation", R.drawable.change_password, AppConstants.getMnu_mandate_cancel(), "ECS Mandate Cancellation")
                };

                int menuSize = 0;
                for (ImageTextMenuModel itemDatum : oldItemData) {
                    if (!TextUtils.isEmpty(itemDatum.getIsEnabled())) {
                        if (itemDatum.getIsEnabled().trim().equals("1")) {
                            menuSize++;
                        }
                    }
                }

                int j = 0;
                ImageTextMenuModel[] newitemData = new ImageTextMenuModel[menuSize];
                for (ImageTextMenuModel oldItemDatum : oldItemData) {
                    if (!TextUtils.isEmpty(oldItemDatum.getIsEnabled())) {
                        if (oldItemDatum.getIsEnabled().trim().equals("1")) {
                            newitemData[j] = oldItemDatum;
                            j++;
                        }
                    }
                }

                recyclerAccounts.setLayoutManager(gridLayoutManager);
                AccountsAdapter accountsAdapter = new AccountsAdapter(AccountsActivity.this, newitemData);
                recyclerAccounts.setAdapter(accountsAdapter);
            }

            horizontalRecyclerView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void staticData(GridLayoutManager mLayoutManager) {
        ImageTextMenuModel[] oldItemData = {
                new ImageTextMenuModel("Account Details", R.drawable.fd_rd_reckoner, "1", "Account Details"),
                new ImageTextMenuModel("Balance Enquiry", R.drawable.fd_rd_reckoner, "1", "Balance Enquiry"),
                new ImageTextMenuModel("Balance Enquiry CBS", R.drawable.fd_rd_reckoner, "0", "Balance Enquiry"),
                new ImageTextMenuModel("Statement Request", R.drawable.change_password, "1", "Mini Statement"),
                new ImageTextMenuModel("Statement Request CBS", R.drawable.change_password, AppConstants.getMnu_accounts_menu_ministatemnt_cbs(), "Mini Statement"),
                new ImageTextMenuModel("Last 5 IMPS Transactions", R.drawable.fd_rd_reckoner, AppConstants.getMnu_accounts_menu_last5imps(), "Last 5 IMPS Transactions"),
                new ImageTextMenuModel("Last 5 IMPS Transactions CBS", R.drawable.fd_rd_reckoner, AppConstants.getMnu_accounts_menu_last5imps_cbs(), "Last 5 IMPS Transactions"),
                new ImageTextMenuModel("Show MMID", R.drawable.change_password, AppConstants.getMnu_accounts_menu_showmmid(), "Show MMID"),
                new ImageTextMenuModel("Show MMID CBS", R.drawable.change_password, AppConstants.getMnu_accounts_menu_showmmid_cbs(), "Show MMID"),
                new ImageTextMenuModel("NEFT Enquiry", R.drawable.change_password, "1", "NEFT Enquiry"),
                new ImageTextMenuModel("Chequebook Request", R.drawable.change_password, AppConstants.getMnu_checkbook_request(), "Chequebook Request"),
                new ImageTextMenuModel("mPassBook", R.drawable.change_password, AppConstants.getMnu_account_statement(), "mPassBook"),
                new ImageTextMenuModel("Cheque Status", R.drawable.change_password, AppConstants.getInqueriChquebookStatus(), "Cheque Status"),
                new ImageTextMenuModel("Stop Cheque Request", R.drawable.change_password, AppConstants.getStopChequebookStatus(), "Stop Cheque Request"),
                new ImageTextMenuModel("PPS Request", R.drawable.change_password, AppConstants.getMnu_pps_request(), "Positive Pay Request"),
                new ImageTextMenuModel("PPS Request Enquiry", R.drawable.change_password, AppConstants.getMnu_pps_request_enquiry(), "Positive Pay Request Enquiry"),
                new ImageTextMenuModel("Block Debit Card", R.drawable.change_password, AppConstants.getMnu_block_debit_card(), "Block Debit Card"),
                new ImageTextMenuModel("Check Transaction Status", R.drawable.change_password, AppConstants.getMnu_check_imps_transaction_status(), "Check Transaction Status")
        };

        int menuSize = 0;
        for (ImageTextMenuModel itemDatum : oldItemData) {
            if (!TextUtils.isEmpty(itemDatum.getIsEnabled())) {
                if (itemDatum.getIsEnabled().trim().equals("1")) {
                    menuSize++;
                }
            }
        }

        int j = 0;
        ImageTextMenuModel[] newitemData = new ImageTextMenuModel[menuSize];
        for (ImageTextMenuModel oldItemDatum : oldItemData) {
            if (!TextUtils.isEmpty(oldItemDatum.getIsEnabled())) {
                if (oldItemDatum.getIsEnabled().trim().equals("1")) {
                    newitemData[j] = oldItemDatum;
                    j++;
                }
            }
        }

        recyclerAccounts.setLayoutManager(mLayoutManager);
        AccountsAdapter accountsAdapter = new AccountsAdapter(AccountsActivity.this, newitemData);
        recyclerAccounts.setAdapter(accountsAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(AccountsActivity.this);
    }


    private void horizontalRecyclerView() {

        try {
            ImageTextMenuModel[] newitemData = null;
            recyclerViewHoriListId = findViewById(R.id.recyclerViewHoriListId);


            ImageTextMenuModel[] oldItemData = {
                    new ImageTextMenuModel("Accounts", R.drawable.accounts_one, AppConstants.getMnu_accounts()),
                    new ImageTextMenuModel("Funds Transfer", R.drawable.fund_transfer_one, AppConstants.getMnu_fundtransfer()),
                    new ImageTextMenuModel("Locate ATMs", R.drawable.locate_atm_one, AppConstants.getMnu_locate_atms()),
                    new ImageTextMenuModel("Locate Branches", R.drawable.locate_branch_one, AppConstants.getMnu_locate_branch()),
                    new ImageTextMenuModel("FAQs", R.drawable.ic_frequently_asked_questions, AppConstants.getMnu_faq()),
                    new ImageTextMenuModel("Settings", R.drawable.ic_setting, AppConstants.getMnu_setting()),
                    new ImageTextMenuModel("Contact Us", R.drawable.contact_us_one, AppConstants.getMnu_contact_us()),
                    new ImageTextMenuModel("About Us", R.drawable.about_us_one, AppConstants.getMnu_about_us()),
                    new ImageTextMenuModel("Bill Pay", R.drawable.about_us_one, AppConstants.getMnu_bill_pay()),
            };

            int menuSize = 0;
            for (ImageTextMenuModel oldItemDatum : oldItemData) {
                if (!TextUtils.isEmpty(oldItemDatum.getIsEnabled())) {
                    if (oldItemDatum.getIsEnabled().trim().equals("1")) {
                        menuSize++;
                    }
                }
            }

            int j = 0;
            newitemData = new ImageTextMenuModel[menuSize];
            for (ImageTextMenuModel oldItemDatum : oldItemData) {
                if (!TextUtils.isEmpty(oldItemDatum.getIsEnabled())) {
                    if (oldItemDatum.getIsEnabled().trim().equals("1")) {
                        newitemData[j] = oldItemDatum;
                        j++;
                    }
                }
            }


            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
            recyclerViewHoriListId.setLayoutManager(mLayoutManager);
            MenuAdapter accountsAdapter = new MenuAdapter(AccountsActivity.this, newitemData);
            recyclerViewHoriListId.setAdapter(accountsAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}