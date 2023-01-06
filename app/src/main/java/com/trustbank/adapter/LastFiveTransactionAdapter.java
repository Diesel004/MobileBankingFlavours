package com.trustbank.adapter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trustbank.Model.LastFiveImpsTransactionModel;
import com.trustbank.R;
import com.trustbank.util.TrustMethods;

import java.util.ArrayList;


public class LastFiveTransactionAdapter extends RecyclerView.Adapter<LastFiveTransactionAdapter.ViewHolder> {

    private static final String TAG = LastFiveTransactionAdapter.class.getSimpleName();
    private Activity mActivity;
    private TrustMethods trustMethods;
    private ArrayList<LastFiveImpsTransactionModel> LastFiveImpsTransactionList;

    public LastFiveTransactionAdapter(Activity activity,
                                      ArrayList<LastFiveImpsTransactionModel> LastFiveImpsTransactionList) {
        this.mActivity = activity;
        this.LastFiveImpsTransactionList = LastFiveImpsTransactionList;
        trustMethods = new TrustMethods(mActivity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.e("onCreateViewHolder","call");
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_last_five_transactions, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Log.e("onBindViewHolder","call");
        LastFiveImpsTransactionModel lastFiveImpsTransactionModel = LastFiveImpsTransactionList.get(position);
        try {
            if (!TextUtils.isEmpty(lastFiveImpsTransactionModel.getDate())){
                viewHolder.txtDate.setText(lastFiveImpsTransactionModel.getDate().substring(0, 10));
            }
            viewHolder.txtAmt.setText(lastFiveImpsTransactionModel.getAmount());
            viewHolder.txtTransType.setText(lastFiveImpsTransactionModel.getTransactionType());
            viewHolder.txtBeneficiary.setText(lastFiveImpsTransactionModel.getBeneficiery());
            viewHolder.txtRefNo.setText(lastFiveImpsTransactionModel.getRefNo());
        } catch (Exception e) {
            e.printStackTrace();
            viewHolder.txtDate.setText(lastFiveImpsTransactionModel.getDate());
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder { // implements View.OnClickListener {

        public TextView txtDate, txtAmt, txtTransType, txtBeneficiary, txtRefNo;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtDate = itemLayoutView.findViewById(R.id.txtDateId);
            txtAmt = itemLayoutView.findViewById(R.id.txtAmtId);
            txtTransType = itemLayoutView.findViewById(R.id.txtTransTypeId);
            txtBeneficiary = itemLayoutView.findViewById(R.id.txtBeneficiaryId);
            txtRefNo = itemLayoutView.findViewById(R.id.txtRefNoId);
        }
    }

    @Override
    public int getItemCount() {
        return LastFiveImpsTransactionList.size();
    }
}
