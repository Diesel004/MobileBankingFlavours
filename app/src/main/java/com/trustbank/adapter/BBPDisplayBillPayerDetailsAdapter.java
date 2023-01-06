package com.trustbank.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trustbank.Model.AtmModel;
import com.trustbank.Model.BBPSSearchBillerModel;
import com.trustbank.Model.BBPSTitlevalueModel;
import com.trustbank.R;
import com.trustbank.interfaces.BBPSSearchBillerInterface;

import java.util.ArrayList;
import java.util.List;

public class BBPDisplayBillPayerDetailsAdapter extends RecyclerView.Adapter<BBPDisplayBillPayerDetailsAdapter.ViewHolder> {

    private Activity mActivity;
    List<BBPSTitlevalueModel> bbpsTitlevalueModelList;
    private ArrayList<AtmModel> arraylist;

    public BBPDisplayBillPayerDetailsAdapter(Activity activity, List<BBPSTitlevalueModel> bbpsTitlevalueModelList) {
        this.mActivity = activity;
        this.bbpsTitlevalueModelList = bbpsTitlevalueModelList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bpps_display_bill_payer_details, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);
        return new ViewHolder(itemLayoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        BBPSTitlevalueModel bbpsTitlevalueModel = bbpsTitlevalueModelList.get(position);
        viewHolder.txtTitleId.setText(bbpsTitlevalueModel.getTitle());
        viewHolder.txValueId.setText(bbpsTitlevalueModel.getValue());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitleId, txValueId;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtTitleId = itemLayoutView.findViewById(R.id.txtTitleId);
            txValueId = itemLayoutView.findViewById(R.id.txValueId);

        }
    }

    @Override
    public int getItemCount() {
        return bbpsTitlevalueModelList.size();
    }


}
