package com.trustbank.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.trustbank.Model.PPSRequestModel;
import com.trustbank.R;
import com.trustbank.util.TrustMethods;

import java.util.List;


public class PPSRequestEnquiryAdapter extends RecyclerView.Adapter<PPSRequestEnquiryAdapter.ViewHolder> {

    private static final String TAG = PPSRequestEnquiryAdapter.class.getSimpleName();
    private Activity mActivity;
    private TrustMethods trustMethods;
    private List<PPSRequestModel> miniStatementModelsList;

    public PPSRequestEnquiryAdapter(Activity activity, List<PPSRequestModel> miniStatementModelsList) {
        this.mActivity = activity;
        this.miniStatementModelsList = miniStatementModelsList;
        trustMethods = new TrustMethods(mActivity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pps_request_enquiry, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            PPSRequestModel miniStatementModel = miniStatementModelsList.get(position);
            if (!TextUtils.isEmpty(miniStatementModel.getChequeDate())) {
                viewHolder.txtDate.setText(miniStatementModel.getChequeDate());
            }
            viewHolder.txtAmt.setText(miniStatementModel.getAmount());
            viewHolder.txtStatusId.setText(miniStatementModel.getStatus());
            viewHolder.txtChequeNoId.setText(miniStatementModel.getChequeNo());
            viewHolder.txtPayeeNameId.setText(miniStatementModel.getPayeeName());
            viewHolder.txtChannelCode.setText(miniStatementModel.getChannel());
            viewHolder.txtReqDate.setText(miniStatementModel.getReqDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {// implements View.OnClickListener {

        public TextView txtDate, txtAmt, txtChequeNoId, txtStatusId, txtPayeeNameId, txtChannelCode,txtReqDate;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtDate = itemLayoutView.findViewById(R.id.txtDateId);
            txtAmt = itemLayoutView.findViewById(R.id.txtAmountId);
            txtStatusId = itemLayoutView.findViewById(R.id.txtStatusId);
            txtChequeNoId = itemLayoutView.findViewById(R.id.txtChequeNoId);
            txtPayeeNameId = itemLayoutView.findViewById(R.id.txtPayeeNameId);
            txtChannelCode = itemLayoutView.findViewById(R.id.txtChannelCodeId);
            txtReqDate = itemLayoutView.findViewById(R.id.txtReqDateId);
        }
    }

    @Override
    public int getItemCount() {
        return miniStatementModelsList.size();
    }
}
