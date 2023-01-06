package com.trustbank.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trustbank.Model.NeftEnquiryModel;
import com.trustbank.R;
import com.trustbank.activity.NeftTransactionDetailsActivity;
import com.trustbank.util.TrustMethods;

import java.util.List;

public class NeftTransactionAdapter extends RecyclerView.Adapter<NeftTransactionAdapter.ViewHolder> {


    private Activity mActivity;
    private TrustMethods trustMethods;
    private List<NeftEnquiryModel> neftEnquiryList;

    public NeftTransactionAdapter(Activity activity, List<NeftEnquiryModel> neftEnquiryList) {
        this.mActivity = activity;
        this.neftEnquiryList = neftEnquiryList;
        trustMethods = new TrustMethods(mActivity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_neft_transaction, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        NeftEnquiryModel neftEnquiryModel = neftEnquiryList.get(position);

        if (neftEnquiryModel != null){
            viewHolder.tvTransID.setText("Txn Id - " + neftEnquiryModel.getTransactionID());
            viewHolder.tvTransDate.setText("Date - " + neftEnquiryModel.getTran_Date());
            viewHolder.tvAccountNo.setText("Acc No : " + neftEnquiryModel.getTo_AccountNo());
            viewHolder.tvAccountName.setText("Ben Name : " + neftEnquiryModel.getBen_Name());
            viewHolder.tvAmount.setText("Amt : ₹ " + TrustMethods.getValueCommaSeparated(neftEnquiryModel.getAmount()));
            viewHolder.tvresUTR.setText("Response UTR : "+ neftEnquiryModel.getResUtr());

            if (neftEnquiryModel.getStatus().equals("0")) {
                viewHolder.tvStatus.setText("Status : Transaction in process");
                TrustMethods.LogMessage("NEFT Transaction List", "Transaction Status Error Message : " + neftEnquiryModel.getError());
            } else if (neftEnquiryModel.getStatus().equals("1")) {
                viewHolder.tvStatus.setText("Status : Success");
                TrustMethods.LogMessage("NEFT Transaction List", "Transaction Status Error Message : " + neftEnquiryModel.getError());
            } else if (neftEnquiryModel.getStatus().equals("2")) {
                viewHolder.tvStatus.setText("Status : Failed");
                TrustMethods.LogMessage("NEFT Transaction List", "Transaction Status Error Message : " + neftEnquiryModel.getError());
            }

            viewHolder.btnEnquiry.setOnClickListener(v -> {
                Intent intent = new Intent(mActivity, NeftTransactionDetailsActivity.class);
                intent.putExtra("NeftEnquiryModel", neftEnquiryModel);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(intent);
                trustMethods.activityOpenAnimation();
            });
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTransID, tvTransDate, tvAccountNo, tvAccountName, tvAmount,tvresUTR, tvStatus, btnEnquiry;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tvTransID = itemLayoutView.findViewById(R.id.tvTransID);
            tvTransDate = itemLayoutView.findViewById(R.id.tvTransDate);
            tvAccountNo = itemLayoutView.findViewById(R.id.tvAccountNo);
            tvAccountName = itemLayoutView.findViewById(R.id.tvAccountName);
            tvresUTR = itemLayoutView.findViewById(R.id.tvresUTR);
            tvresUTR = itemLayoutView.findViewById(R.id.tvresUTR);
            tvAmount = itemLayoutView.findViewById(R.id.tvAmount);
            tvStatus = itemLayoutView.findViewById(R.id.tvStatus);
            btnEnquiry = itemLayoutView.findViewById(R.id.btnEnquiry);
        }
    }

    @Override
    public int getItemCount() {
        return neftEnquiryList.size();
    }

}
