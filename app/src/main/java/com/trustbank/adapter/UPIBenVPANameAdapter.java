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
import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.R;
import com.trustbank.interfaces.UPIGetNameAdapterInterface;
import java.util.List;

public class UPIBenVPANameAdapter extends RecyclerView.Adapter<UPIBenVPANameAdapter.ViewHolder> {

    private Activity mActivity;
    List<BeneficiaryModal> beneficiaryArrayList;
    UPIGetNameAdapterInterface upiGetNameAdapterInterface;

    public UPIBenVPANameAdapter(Context activity, List<BeneficiaryModal> beneficiaryArrayList,
                                UPIGetNameAdapterInterface upiGetNameAdapterInterface) {
        this.mActivity = (Activity) activity;
        this.upiGetNameAdapterInterface = upiGetNameAdapterInterface;
        this.beneficiaryArrayList = beneficiaryArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_upi_ben_name, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);
        return new ViewHolder(itemLayoutView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        BeneficiaryModal beneficiaryModal = beneficiaryArrayList.get(position);
        viewHolder.txtTitleId.setText(beneficiaryModal.getBenUpiId());
        viewHolder.txValueName.setText(beneficiaryModal.getBenNickname());
        viewHolder.txValueId.setVisibility(View.GONE);
        viewHolder.linearSearchUPIId.setOnClickListener(view -> upiGetNameAdapterInterface.getUpiBenNameAdapter(beneficiaryModal.getBenUpiId(),
                beneficiaryModal.getBanAccName(),
                beneficiaryModal.getBenNickname()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitleId, txValueId,txValueName;
        private LinearLayout linearSearchUPIId;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtTitleId = itemLayoutView.findViewById(R.id.txtTitleId);
            txValueId = itemLayoutView.findViewById(R.id.txValueId);
            txValueName = itemLayoutView.findViewById(R.id.txValueName);
            linearSearchUPIId = itemLayoutView.findViewById(R.id.linearSearchUPIId);

        }
    }

    @Override
    public int getItemCount() {
        return beneficiaryArrayList.size();
    }

}
