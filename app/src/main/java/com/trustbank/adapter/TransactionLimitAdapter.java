package com.trustbank.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.trustbank.Model.TransactionLimitModel;
import com.trustbank.Model.TransactionModel;
import com.trustbank.R;
import com.trustbank.activity.TransactionLimitSubmitActivity;
import com.trustbank.util.TrustMethods;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TransactionLimitAdapter extends RecyclerView.Adapter<TransactionLimitAdapter.ViewHolder> {

    private static final String TAG = FundsTransferAdapter.class.getSimpleName();
    Activity mActivity;
    TrustMethods trustMethods;
    private ArrayList<TransactionLimitModel> transactionLimitModels;
    private String accountNo;

    public TransactionLimitAdapter(FragmentActivity activity,
                                   ArrayList<TransactionLimitModel> transactionLimitModels, String accountNo) {
        this.mActivity = activity;
        this.transactionLimitModels = transactionLimitModels;
        this.accountNo = accountNo;
        trustMethods = new TrustMethods(mActivity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_transaction_limit, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TransactionLimitModel transactionModel = transactionLimitModels.get(position);


        List<TransactionModel> transactionModels = transactionModel.getTransactionList();
        viewHolder.textTransactionTitle.setText(transactionModel.getName());
        for (TransactionModel transactionModel1 : transactionModels) {
            if (transactionModel1.getLimitType() == 1) {
                viewHolder.textLimitPerDayId.setText("Per Day: " + transactionModel1.getLimit_value());
            } else if (transactionModel1.getLimitType() == 2) {
                viewHolder.textNoOfTransPerDayId.setText("No.Of Txn Per Day: " + transactionModel1.getLimit_value());
            } else if (transactionModel1.getLimitType() == 3) {
                viewHolder.textLimitPerTransId.setText("Per Txn: " + transactionModel1.getLimit_value());
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textTransactionTitle, textLimitPerDayId, textLimitPerTransId, textNoOfTransPerDayId;
        public ImageView editTransaction;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            textTransactionTitle = itemLayoutView.findViewById(R.id.textTransactionTitleId);
            textLimitPerDayId = itemLayoutView.findViewById(R.id.textLimitPerDayId);
            textLimitPerTransId = itemLayoutView.findViewById(R.id.textLimitPerTransId);
            textNoOfTransPerDayId = itemLayoutView.findViewById(R.id.textNoOfTransPerDayId);
            editTransaction = itemLayoutView.findViewById(R.id.editTransactionId);
            editTransaction.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (!TextUtils.isEmpty(transactionLimitModels.get(getAdapterPosition()).getName().trim())) {

                List<TransactionModel> transactionLimitModelList = transactionLimitModels.get(getAdapterPosition()).getTransactionList();
                Intent intentTransactionLimit = new Intent(view.getContext(), TransactionLimitSubmitActivity.class);
                intentTransactionLimit.putExtra("transactionLimit", (Serializable) transactionLimitModelList);
                intentTransactionLimit.putExtra("accountNo", accountNo);
                intentTransactionLimit.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivityForResult(intentTransactionLimit, 1);
                trustMethods.activityOpenAnimation();
            }
        }
    }

    @Override
    public int getItemCount() {
        return transactionLimitModels.size();
    }


}

