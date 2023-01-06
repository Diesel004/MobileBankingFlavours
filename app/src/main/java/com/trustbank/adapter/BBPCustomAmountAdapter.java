package com.trustbank.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trustbank.Model.AtmModel;
import com.trustbank.Model.BBPSTitlevalueModel;
import com.trustbank.R;
import com.trustbank.fragment.BBPSCustomAmountFragmentDialog;
import com.trustbank.interfaces.BBPSGetLIstClickListnerInterface;

import java.util.ArrayList;
import java.util.List;

public class BBPCustomAmountAdapter extends RecyclerView.Adapter<BBPCustomAmountAdapter.ViewHolder> {

    private Activity mActivity;
    List<BBPSTitlevalueModel> bbpsTitlevalueModelList;
    private ArrayList<AtmModel> arraylist;
    BBPSGetLIstClickListnerInterface getLIstClickListnerInterface;

    public BBPCustomAmountAdapter(Context activity, List<BBPSTitlevalueModel> bbpsTitlevalueModelList,
                                  BBPSGetLIstClickListnerInterface getLIstClickListnerInterface
    ) {
        this.mActivity = (Activity) activity;
        this.getLIstClickListnerInterface = getLIstClickListnerInterface;
        this.bbpsTitlevalueModelList = bbpsTitlevalueModelList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bpps_custom_amount, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);
        return new ViewHolder(itemLayoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        BBPSTitlevalueModel bbpsTitlevalueModel = bbpsTitlevalueModelList.get(position);
        viewHolder.txtTitleId.setText(bbpsTitlevalueModel.getTitle());
        viewHolder.txValueId.setText("\u20B9" + " " + bbpsTitlevalueModel.getValue());
        viewHolder.linearSearchBillerId.setOnClickListener(view -> {
            getLIstClickListnerInterface.GetAmountClickOnListEvent(bbpsTitlevalueModel.getValue());
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitleId, txValueId;
        private LinearLayout linearSearchBillerId;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtTitleId = itemLayoutView.findViewById(R.id.txtTitleId);
            txValueId = itemLayoutView.findViewById(R.id.txValueId);
            linearSearchBillerId = itemLayoutView.findViewById(R.id.linearSearchBillerId);

        }
    }

    @Override
    public int getItemCount() {
        return bbpsTitlevalueModelList.size();
    }


}
