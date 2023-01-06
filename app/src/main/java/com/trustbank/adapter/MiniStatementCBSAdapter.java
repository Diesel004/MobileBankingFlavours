package com.trustbank.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.trustbank.Model.MiniStatementModel;
import com.trustbank.R;
import com.trustbank.util.TrustMethods;

import java.util.List;


public class MiniStatementCBSAdapter extends RecyclerView.Adapter<MiniStatementCBSAdapter.ViewHolder> {

    private static final String TAG = MiniStatementCBSAdapter.class.getSimpleName();
    private Activity mActivity;
    private TrustMethods trustMethods;
    private List<MiniStatementModel> miniStatementModelsList;

    public MiniStatementCBSAdapter(Activity activity, List<MiniStatementModel> miniStatementModelsList) {
        this.mActivity = activity;
        this.miniStatementModelsList = miniStatementModelsList;
        trustMethods = new TrustMethods(mActivity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mini_statement_cbs, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            MiniStatementModel miniStatementModel = miniStatementModelsList.get(position);

            if (!TextUtils.isEmpty(miniStatementModel.getDate())) {
                viewHolder.txtDate.setText(miniStatementModel.getDate().substring(0, 10));
            }

            viewHolder.txtAmt.setText(TrustMethods.trimWithPrefixCommsepareted(miniStatementModel.getAmt()));
            viewHolder.txtRemarkId.setText(miniStatementModel.getRemarks());
            int accType = miniStatementModel.getAccType();
            if (accType != -1) {
                if (accType == 0) {  //0 credit
                    viewHolder.txtTransType.setText("Credit");
                    viewHolder.txtTransType.setTextColor(mActivity.getResources().getColor(R.color.greenColor,null));
                    viewHolder.txtAmt.setTextColor(mActivity.getResources().getColor(R.color.greenColor,null));
                } else {
                    viewHolder.txtTransType.setText("Debit");
                    viewHolder.txtTransType.setTextColor(mActivity.getResources().getColor(R.color.buying_tips,null));
                    viewHolder.txtAmt.setTextColor(mActivity.getResources().getColor(R.color.buying_tips,null));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDate, txtAmt, txtTransType, txtRemarkId;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtDate = itemLayoutView.findViewById(R.id.txtDateId);
            txtAmt = itemLayoutView.findViewById(R.id.txtAmountId);
            txtTransType = itemLayoutView.findViewById(R.id.txtTransTypeId);
            txtRemarkId = itemLayoutView.findViewById(R.id.txtRemarkId);
        }
    }

    @Override
    public int getItemCount() {
        return miniStatementModelsList.size();
    }
}
