package com.trustbank.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trustbank.Model.AtmModel;
import com.trustbank.R;

import java.util.ArrayList;
import java.util.Locale;


public class LocateAtmAdapter extends RecyclerView.Adapter<LocateAtmAdapter.ViewHolder> {
    private static final String TAG = LocateAtmAdapter.class.getSimpleName();
    private Activity mActivity;
    private ArrayList<AtmModel> atmModelsList;
    private ArrayList<AtmModel> arraylist;

    public LocateAtmAdapter(Activity activity, ArrayList<AtmModel> atmModelsList) {
        this.mActivity = activity;
        this.atmModelsList = atmModelsList;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(atmModelsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_locate_atm, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);

        return new ViewHolder(itemLayoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        AtmModel atmModel = atmModelsList.get(position);

        viewHolder.txtTerminalCode.setText(atmModel.getTerminalCode());
        viewHolder.txtAddress.setText(mActivity.getResources().getString(R.string.txt_address) + " " + atmModel.getLocation() + ", " + atmModel.getCity() + " " + atmModel.getPincode());

        if (mActivity.getPackageName().equals("com.trustbank.pucbmbank")) {
            viewHolder.txtContactNo.setVisibility(View.VISIBLE);
            viewHolder.txtBranchManagerName.setVisibility(View.VISIBLE);
            viewHolder.txtBranchManagerMobNo.setVisibility(View.VISIBLE);
            viewHolder.txtContactNo.setText("Contact No : " + atmModel.getContact_no());
            viewHolder.txtBranchManagerName.setText("Branch Manager Name : " + atmModel.getBranch_manager());
            viewHolder.txtBranchManagerMobNo.setText("Branch Manager Contact No. : " + atmModel.getBranch_manager_mob());
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTerminalCode, txtAddress, txtContactNo, txtBranchManagerName, txtBranchManagerMobNo;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtTerminalCode = itemLayoutView.findViewById(R.id.txtTerminalCodeId);
            txtAddress = itemLayoutView.findViewById(R.id.txtAddressId);

            txtContactNo = itemLayoutView.findViewById(R.id.txtContactNo);
            txtBranchManagerName = itemLayoutView.findViewById(R.id.txtBranchManagerName);
            txtBranchManagerMobNo = itemLayoutView.findViewById(R.id.txtBranchManagerMobNo);
        }
    }

    @Override
    public int getItemCount() {
        return atmModelsList.size();
    }


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
    }
}
