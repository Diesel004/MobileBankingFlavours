package com.trustbank.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.trustbank.Module.ImageTextMenuModel;
import com.trustbank.R;
import com.trustbank.adapter.AccountsAdapter;
import com.trustbank.adapter.CompalintsManagementMenuAdapter;
import com.trustbank.util.AppConstants;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComplaintManagementActivity extends AppCompatActivity {

    @BindView(R.id.recyclerComplaintsId)
    RecyclerView recyclerCompalintsManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            if (savedInstanceState != null) {
                Object currentPID = String.valueOf(android.os.Process.myPid());
                // Check current PID with old PID
                if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                    // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                    TrustMethods.naviagteToSplashScreen(ComplaintManagementActivity.this);
                }
            }
        }
        SetTheme.changeToTheme(ComplaintManagementActivity.this, false);
        setContentView(R.layout.activity_complaint_management);
        ButterKnife.bind(this);
        inIt();
    }

    private void inIt() {

        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }


            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

            ImageTextMenuModel[] oldItemData = {
                    new ImageTextMenuModel("Register Complaints", R.drawable.fd_rd_reckoner, AppConstants.getMnu_bill_pay_register_complaints(), "Register Complaints"),
                    new ImageTextMenuModel("Track Complaints", R.drawable.fd_rd_reckoner, AppConstants.getMnu_bill_pay_track_complaints(), "Track Complaints") //TODO need to change menu code.
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

            recyclerCompalintsManagement.setLayoutManager(mLayoutManager);
            CompalintsManagementMenuAdapter accountsAdapter = new CompalintsManagementMenuAdapter(ComplaintManagementActivity.this, newitemData);
            recyclerCompalintsManagement.setAdapter(accountsAdapter);


        } catch (Exception e) {
            e.printStackTrace();
        }
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
        TrustMethods.showBackButtonAlert(ComplaintManagementActivity.this);
    }


}