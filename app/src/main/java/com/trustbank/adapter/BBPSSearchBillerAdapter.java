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
import com.trustbank.R;
import com.trustbank.interfaces.BBPSSearchBillerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class BBPSSearchBillerAdapter extends RecyclerView.Adapter<BBPSSearchBillerAdapter.ViewHolder> {
    private static final String TAG = BBPSSearchBillerAdapter.class.getSimpleName();
    private Activity mActivity;
    private List<BBPSSearchBillerModel> atmModelsList;
    private ArrayList<AtmModel> arraylist;
    BBPSSearchBillerInterface bbpsSearchBillerInterface;

    public BBPSSearchBillerAdapter(Activity activity, List<BBPSSearchBillerModel> atmModelsList) {
        this.mActivity = activity;
        this.bbpsSearchBillerInterface = (BBPSSearchBillerInterface) activity;
        this.atmModelsList = atmModelsList;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bpps_search_biller, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);

        return new ViewHolder(itemLayoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        BBPSSearchBillerModel atmModel = atmModelsList.get(position);

        viewHolder.txtstatusId.setText(atmModel.getStatus());
        viewHolder.txtBillerNameId.setText(atmModel.getBillerName());
        viewHolder.txtbillerShortNameId.setText(atmModel.getBillerAliasName());
        viewHolder.linearSearchBillerId.setOnClickListener(view -> {

            String billerId = atmModel.getBillerId();
            String billername = atmModel.getBillerName();
            bbpsSearchBillerInterface.adapterCallEvent(billerId,billername);
        });


    /*    viewHolder.txtTerminalCode.setText(atmModel.getTerminalCode());
        viewHolder.txtAddress.setText(mActivity.getResources().getString(R.string.txt_address) + " " + atmModel.getLocation() + ", " + atmModel.getCity() + " " + atmModel.getPincode());

        if (mActivity.getPackageName().equals("com.trustbank.pucbmbank")) {
            viewHolder.txtContactNo.setVisibility(View.VISIBLE);
            viewHolder.txtBranchManagerName.setVisibility(View.VISIBLE);
            viewHolder.txtBranchManagerMobNo.setVisibility(View.VISIBLE);
            viewHolder.txtContactNo.setText("Contact No : " + atmModel.getContact_no());
            viewHolder.txtBranchManagerName.setText("Branch Manager Name : " + atmModel.getBranch_manager());
            viewHolder.txtBranchManagerMobNo.setText("Branch Manager Contact No. : " + atmModel.getBranch_manager_mob());
        }*/

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtBillerNameId, txtstatusId, txtbillerShortNameId;
        public LinearLayout linearSearchBillerId;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtBillerNameId = itemLayoutView.findViewById(R.id.txtBillerNameId);
            txtstatusId = itemLayoutView.findViewById(R.id.txtstatusId);
            txtbillerShortNameId = itemLayoutView.findViewById(R.id.txtbillerShortNameId);
            linearSearchBillerId = itemLayoutView.findViewById(R.id.linearSearchBillerId);


        }
    }

    @Override
    public int getItemCount() {
        return atmModelsList.size();
    }

/*
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        atmModelsList.clear();
        if (charText.length() == 0) {
            atmModelsList.addAll(arraylist);
        } else {
            for (AtmModel wp : arraylist) {
                if (wp.getPincode().toLowerCase(Locale.getDefault()).startsWith(charText) || wp.getLocation().toLowerCase(Locale.getDefault()).startsWith(charText)) {
                    atmModelsList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }*/
}
