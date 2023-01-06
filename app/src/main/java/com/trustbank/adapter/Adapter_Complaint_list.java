package com.trustbank.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trustbank.Model.ComplaintsListModel;
import com.trustbank.Model.Transaction_list_Model;
import com.trustbank.R;
import com.trustbank.activity.ComplaintHistoryListActivity;

import java.util.List;

public class Adapter_Complaint_list extends RecyclerView.Adapter<Adapter_Complaint_list.MyViewHolder> {

    Activity mactivity;
    List<ComplaintsListModel> transactionlistmodelList;


    public Adapter_Complaint_list(ComplaintHistoryListActivity activity,
                                  List<ComplaintsListModel> transactionlistmodelList) {
        this.mactivity = activity;
        this.transactionlistmodelList = transactionlistmodelList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_list_adapter, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ComplaintsListModel model = transactionlistmodelList.get(position);

        holder.mTxnRefId.setText(model.getTxnReferenceId());
        holder.mAmount.setText(model.getAmount());
        holder.mTxnDate.setText(model.getTxnDate());
        holder.mAgentid.setText(model.getAgentId());
        holder.mbiller_id.setText(model.getBillerId());
        holder.mTxnStatus.setText(model.getTxnStatus());

    }

    @Override
    public int getItemCount() {
        return transactionlistmodelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTxnRefId, mAmount, mTxnDate, mAgentid,mbiller_id, mTxnStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mTxnRefId=itemView.findViewById(R.id.txn_refid);
            mAmount=itemView.findViewById(R.id.tv_amount);
            mTxnDate=itemView.findViewById(R.id.tv_txn_date);
            mAgentid=itemView.findViewById(R.id.tv_agent_id);
            mbiller_id=itemView.findViewById(R.id.biller_id);
            mTxnStatus=itemView.findViewById(R.id.transaction_status);


        }
    }
}