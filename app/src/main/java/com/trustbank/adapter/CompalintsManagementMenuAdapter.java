package com.trustbank.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trustbank.Module.ImageTextMenuModel;
import com.trustbank.R;
import com.trustbank.activity.AccountDetailsActivity;
import com.trustbank.activity.BBPSTransactionStatusActivity;
import com.trustbank.activity.BalanceEnquiryActivity;
import com.trustbank.activity.BalanceEnquiryCBSActivity;
import com.trustbank.activity.BlockDebitCardActivity;
import com.trustbank.activity.ChequeStatusActivity;
import com.trustbank.activity.ChequebookRequestActivity;
import com.trustbank.activity.ComplaintHistory;
import com.trustbank.activity.ComplaintHistoryListActivity;
import com.trustbank.activity.FrmAccountStatement;
import com.trustbank.activity.FrmPPSServiceRequestEnquiry;
import com.trustbank.activity.ImpsCheckTransactionRequest;
import com.trustbank.activity.LastFiveImpsTransactionActivity;
import com.trustbank.activity.LastFiveImpsTransactionCBSActivity;
import com.trustbank.activity.MiniStatementActivity;
import com.trustbank.activity.MiniStatementCBSActivity;
import com.trustbank.activity.NeftEnquiryActivity;
import com.trustbank.activity.PPSRequestActivity;
import com.trustbank.activity.RegisterComplaintsActivity;
import com.trustbank.activity.ShowMMIDActivity;
import com.trustbank.activity.ShowMMIDCBSActivity;
import com.trustbank.activity.StopChequeRequestActivity;
import com.trustbank.util.TrustMethods;

public class CompalintsManagementMenuAdapter extends RecyclerView.Adapter<CompalintsManagementMenuAdapter.ViewHolder> {
    private static final String TAG = CompalintsManagementMenuAdapter.class.getSimpleName();
    Activity mActivity;
    TrustMethods trustMethods;
    private ImageTextMenuModel[] accountItemsData;
    private String newName = "Name";

    public CompalintsManagementMenuAdapter(Activity activity, ImageTextMenuModel[] accountItemsData) {
        this.mActivity = activity;
        this.accountItemsData = accountItemsData;
        trustMethods = new TrustMethods(mActivity);

        Log.d("NewerName", newName);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_accounts, null);
        RecyclerView.LayoutParams lp;
        lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.accountMenuName.setText(accountItemsData[position].getLableName());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView accountMenuName;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            accountMenuName = itemLayoutView.findViewById(R.id.accountMenuNameId);
        }

        @Override
        public void onClick(View view) {
            switch (accountItemsData[getAdapterPosition()].getItemName().trim()) {

                case "Register Complaints":
                    Intent intentAccDetails = new Intent(view.getContext(), BBPSTransactionStatusActivity.class);
                    intentAccDetails.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentAccDetails);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Track Complaints":
                    Intent intentBalEnquiry = new Intent(view.getContext(), ComplaintHistory.class);
                    intentBalEnquiry.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentBalEnquiry);
                    trustMethods.activityOpenAnimation();
                    break;

                default:
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        return accountItemsData.length;
    }
}
