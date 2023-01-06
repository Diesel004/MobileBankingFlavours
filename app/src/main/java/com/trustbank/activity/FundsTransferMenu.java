package com.trustbank.activity;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.MenuItem;

import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.Module.ImageTextMenuModel;
import com.trustbank.R;
import com.trustbank.adapter.FundsTransferAdapter;
import com.trustbank.util.AppConstants;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;

import java.util.ArrayList;


public class FundsTransferMenu extends AppCompatActivity {
    private TrustMethods method;
    private CoordinatorLayout coordinatorLayout;
    ArrayList<BeneficiaryModal> beneficiaryList;
    private RecyclerView recyclerAccounts;
    private RecyclerView.LayoutManager mLayoutManager;


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
                        TrustMethods.naviagteToSplashScreen(FundsTransferMenu.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(FundsTransferMenu.this, false);
        setContentView(R.layout.activity_accounts);
        inIt();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        try {
            if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
                bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inIt() {
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            method = new TrustMethods(FundsTransferMenu.this);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            recyclerAccounts = findViewById(R.id.recyclerAccountsId);
            mLayoutManager = new LinearLayoutManager(getApplicationContext());


            if ((!TextUtils.isEmpty(AppConstants.getPlay_store_validate())) && AppConstants.getPlay_store_validate().equalsIgnoreCase("1")) {

                staticData(mLayoutManager);
            } else if (AppConstants.getUSERMOBILENUMBER().equalsIgnoreCase(AppConstants.getPlayStoreDemoUserMobile()) &&
                    AppConstants.getCLIENTID().equalsIgnoreCase(AppConstants.getPlayStoreDemoPasswordClientid())) {
                staticData(mLayoutManager);
            } else {

                ImageTextMenuModel[] oldItemData = {
                        new ImageTextMenuModel("Within Bank Transfer", R.drawable.fd_rd_reckoner, AppConstants.getMnu_fundtransfer_ownbank()),
                        new ImageTextMenuModel("IMPS Transfer To Account", R.drawable.manage_acc, AppConstants.getMnu_fundtransfer_impstoaccount()),
                        new ImageTextMenuModel("NEFT Transfer To Account", R.drawable.manage_acc, AppConstants.getMnu_fundtransfer_nefttoaccount()),
                        new ImageTextMenuModel("IMPS Transfer To Mobile Phone", R.drawable.change_password, AppConstants.getMnu_fundtransfer_impstomobile()),
                        new ImageTextMenuModel("Self Transfer To Account", R.drawable.change_password, AppConstants.getMnu_self_transfer_to_account()),
                        new ImageTextMenuModel("UPI", R.drawable.fd_rd_reckoner, AppConstants.getMnu_fundtransfer_upi()),
                        new ImageTextMenuModel("Manage Beneficiaries", R.drawable.fd_rd_reckoner, AppConstants.getMnu_fundtransfer_mngbenefeciaries()),
                        new ImageTextMenuModel("Collect Money", R.drawable.fd_rd_reckoner, AppConstants.getMnu_fundtransfer_upi_collect_money()),
                        new ImageTextMenuModel("Create New QR Code", R.drawable.fd_rd_reckoner, AppConstants.getMnu_fundtransfer_upi_create_qr()),
                        new ImageTextMenuModel("Display QR Code", R.drawable.fd_rd_reckoner, AppConstants.getMnu_fundtransfer_upi_get_qr())

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
                FundsTransferAdapter fundsTransferAdapter = new FundsTransferAdapter(FundsTransferMenu.this,
                        newitemData, beneficiaryList);
                recyclerAccounts.setAdapter(fundsTransferAdapter);


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void staticData(RecyclerView.LayoutManager mLayoutManager) {

        ImageTextMenuModel[] oldItemData = {
                new ImageTextMenuModel("Within Bank Transfer", R.drawable.fd_rd_reckoner, "1"),
                new ImageTextMenuModel("IMPS Transfer To Account", R.drawable.manage_acc, "1"),
                new ImageTextMenuModel("NEFT Transfer To Account", R.drawable.manage_acc, "1"),
                new ImageTextMenuModel("IMPS Transfer To Mobile Phone", R.drawable.change_password, "1"),
                new ImageTextMenuModel("Self Transfer To Account", R.drawable.change_password, "1"),
                new ImageTextMenuModel("Manage Beneficiaries", R.drawable.fd_rd_reckoner, "1")
        };

        int menuSize = 0;
        for (int k = 0; k < oldItemData.length; k++) {
            if (!TextUtils.isEmpty(oldItemData[k].getIsEnabled())) {
                if (oldItemData[k].getIsEnabled().trim().equals("1")) {
                    menuSize++;
                }
            }

        }
        int j = 0;
        ImageTextMenuModel[] newitemData = new ImageTextMenuModel[menuSize];
        for (int i = 0; i < oldItemData.length; i++) {
            if (!TextUtils.isEmpty(oldItemData[i].getIsEnabled())) {
                if (oldItemData[i].getIsEnabled().trim().equals("1")) {
                    newitemData[j] = oldItemData[i];
                    j++;
                }
            }
        }
        recyclerAccounts.setLayoutManager(mLayoutManager);
        FundsTransferAdapter fundsTransferAdapter = new FundsTransferAdapter(FundsTransferMenu.this, newitemData, beneficiaryList);
        recyclerAccounts.setAdapter(fundsTransferAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                method.activityCloseAnimation();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(FundsTransferMenu.this);
    }
}