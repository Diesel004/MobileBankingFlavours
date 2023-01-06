package com.trustbank.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.R;
import com.trustbank.activity.IMPSTransferToAccount;
import com.trustbank.activity.IMPSTransferToMobile;
import com.trustbank.activity.NEFTTransferToAccount;
import com.trustbank.activity.WithinBankActivity;
import com.trustbank.interfaces.GetBeneficiaryListInterface;
import com.trustbank.util.AppConstants;
import com.trustbank.util.TrustMethods;

import java.util.ArrayList;
import java.util.Locale;

public class BeneficiaryAdapter extends RecyclerView.Adapter<BeneficiaryAdapter.ViewHolder> {
    private static final String TAG = BeneficiaryAdapter.class.getSimpleName();
    Activity mActivity;
    TrustMethods trustMethods;
    private ArrayList<BeneficiaryModal> beneficiaryList;
    private CoordinatorLayout coordinatorLayout;
    GetBeneficiaryListInterface getBeneficiaryListInterface;
    private ArrayList<BeneficiaryModal> arraylist;

    public BeneficiaryAdapter(Activity activity, ArrayList<BeneficiaryModal> beneficiaryList) {
        this.mActivity = activity;
        this.beneficiaryList = beneficiaryList;
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(beneficiaryList);
        trustMethods = new TrustMethods(mActivity);
        getBeneficiaryListInterface = (GetBeneficiaryListInterface) activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_beneficiary_data, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final BeneficiaryModal beneficiaryModal = beneficiaryList.get(position);
        viewHolder.txtBenf.setText(beneficiaryModal.getBenId());

        if (!TextUtils.isEmpty(beneficiaryModal.getBenNickname().trim())) {
            viewHolder.txtNickname.setText(beneficiaryModal.getBenNickname());
            viewHolder.txtNickname.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtNickname.setVisibility(View.GONE);
        }

        if (beneficiaryModal.getBenType().equals("1")) {

            viewHolder.txtAccName.setText(beneficiaryModal.getBanAccName());
            viewHolder.txtAccNumber.setText(beneficiaryModal.getBenAccNo());

            viewHolder.accNameLayout.setVisibility(View.VISIBLE);
            viewHolder.accNoLayout.setVisibility(View.VISIBLE);
            viewHolder.ifscLayout.setVisibility(View.GONE);
            viewHolder.mobNoLayout.setVisibility(View.GONE);
            viewHolder.mmidLayout.setVisibility(View.GONE);

            if (AppConstants.getMnu_fundtransfer_ownbank().equalsIgnoreCase("1")) {
                viewHolder.btnWithIn.setVisibility(View.VISIBLE);
            }

            viewHolder.btnIMPS.setVisibility(View.GONE);
            viewHolder.btnNEFT.setVisibility(View.GONE);

        } else if (beneficiaryModal.getBenType().equals("2")) {

            viewHolder.txtAccName.setText(beneficiaryModal.getBanAccName());
            viewHolder.txtAccNumber.setText(beneficiaryModal.getBenAccNo());
            viewHolder.txtIfsc.setText(beneficiaryModal.getBenIfscCode());

            viewHolder.accNameLayout.setVisibility(View.VISIBLE);
            viewHolder.accNoLayout.setVisibility(View.VISIBLE);
            viewHolder.ifscLayout.setVisibility(View.VISIBLE);
            viewHolder.mobNoLayout.setVisibility(View.GONE);
            viewHolder.mmidLayout.setVisibility(View.GONE);

            if (AppConstants.getMnu_fundtransfer_impstoaccount().equalsIgnoreCase("1")
                    || AppConstants.getMnu_fundtransfer_impstomobile().equalsIgnoreCase("1")) {
                viewHolder.btnIMPS.setVisibility(View.VISIBLE);
            }
            if (AppConstants.getMnu_fundtransfer_nefttoaccount().equalsIgnoreCase("1")) {
                viewHolder.btnNEFT.setVisibility(View.VISIBLE);
            }
            viewHolder.btnWithIn.setVisibility(View.GONE);

        } else if (beneficiaryModal.getBenType().equals("3")) {
            viewHolder.txtMobNo.setText(beneficiaryModal.getBenMobNo());
            viewHolder.txtMmid.setText(beneficiaryModal.getBenMmid());

            viewHolder.accNameLayout.setVisibility(View.GONE);
            viewHolder.accNoLayout.setVisibility(View.GONE);
            viewHolder.ifscLayout.setVisibility(View.GONE);
            viewHolder.mobNoLayout.setVisibility(View.VISIBLE);
            viewHolder.mmidLayout.setVisibility(View.VISIBLE);

            if (AppConstants.getMnu_fundtransfer_impstoaccount().equalsIgnoreCase("1")
                    || AppConstants.getMnu_fundtransfer_impstomobile().equalsIgnoreCase("1")) {
                viewHolder.btnIMPS.setVisibility(View.VISIBLE);
            }

            viewHolder.btnNEFT.setVisibility(View.GONE);
            viewHolder.btnWithIn.setVisibility(View.GONE);

        } else if (beneficiaryModal.getBenType().equals("4")) {  //for UPI Id

            viewHolder.upiLayoutId.setVisibility(View.VISIBLE);

            viewHolder.upicustomerLayout.setVisibility(View.VISIBLE);

            viewHolder.txtUpiId.setText(beneficiaryModal.getBenUpiId());

            viewHolder.txtUpicustomerName.setText(beneficiaryModal.getBanAccName());


            viewHolder.accNameLayout.setVisibility(View.GONE);
            viewHolder.accNoLayout.setVisibility(View.GONE);
            viewHolder.ifscLayout.setVisibility(View.GONE);
            viewHolder.mobNoLayout.setVisibility(View.GONE);
            viewHolder.mmidLayout.setVisibility(View.GONE);
            viewHolder.btnNEFT.setVisibility(View.GONE);
            viewHolder.btnWithIn.setVisibility(View.GONE);
            viewHolder.btnUpiId.setVisibility(View.VISIBLE);
        }

        final String benfId = viewHolder.txtBenf.getText().toString().trim();
        viewHolder.txtNickname.setOnClickListener(view -> confimationRemoveDialog(benfId));

        viewHolder.btnIMPS.setOnClickListener(view -> {
            try {
                //IF IFSC Code & MOB No both cases will be available
                Log.d("benList", beneficiaryList.toString());
                if (!TextUtils.isEmpty(beneficiaryModal.getBenIfscCode().trim()) &&
                        !TextUtils.isEmpty(beneficiaryModal.getBenMobNo().trim())) {
                    MoveMobileOrAccountDialog(beneficiaryList, beneficiaryModal.getBenNickname());

                } else if (!TextUtils.isEmpty(beneficiaryModal.getBenIfscCode().trim())) {
                    Intent intent = new Intent(mActivity, IMPSTransferToAccount.class);
                    intent.putExtra("beneficiaryList", beneficiaryList);
                    intent.putExtra("beneficiaryNickName", beneficiaryModal.getBenNickname());
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mActivity.startActivity(intent);
                    trustMethods.activityOpenAnimation();

                } else if (!TextUtils.isEmpty(beneficiaryModal.getBenMobNo().trim())) {
                    Intent intent = new Intent(mActivity, IMPSTransferToMobile.class);
                    intent.putExtra("beneficiaryList", beneficiaryList);
                    intent.putExtra("beneficiaryNickName", beneficiaryModal.getBenNickname());
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mActivity.startActivity(intent);
                    trustMethods.activityOpenAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        viewHolder.btnNEFT.setOnClickListener(view -> {
            try {
                if (!TextUtils.isEmpty(beneficiaryModal.getBenIfscCode().trim())) {
                    Intent intent = new Intent(mActivity, NEFTTransferToAccount.class);
                    intent.putExtra("beneficiaryList", beneficiaryList);
                    intent.putExtra("beneficiaryNickName", beneficiaryModal.getBenNickname());
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mActivity.startActivity(intent);
                    trustMethods.activityOpenAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        viewHolder.btnWithIn.setOnClickListener(v -> {
            try {
                if (!TextUtils.isEmpty(beneficiaryModal.getBanAccName().trim())) {
                    Intent intent = new Intent(mActivity, WithinBankActivity.class);
                    intent.putExtra("beneficiaryList", beneficiaryList);
                    intent.putExtra("beneficiaryNickName", beneficiaryModal.getBenNickname());
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mActivity.startActivity(intent);
                    trustMethods.activityOpenAnimation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Button btnIMPS, btnNEFT, btnWithIn, btnUpiId;
        public TextView txtBenf, txtNickname, txtAccName, txtAccNumber, txtIfsc, txtMobNo, txtMmid, txtUpiId,txtUpicustomerName;
        private LinearLayout accNameLayout, accNoLayout, ifscLayout, mobNoLayout, mmidLayout, upiLayoutId,upicustomerLayout;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtBenf = itemLayoutView.findViewById(R.id.txtBenfId);
            txtNickname = itemLayoutView.findViewById(R.id.txtNicknameId);
            txtAccName = itemLayoutView.findViewById(R.id.txtAccNameId);
            txtAccNumber = itemLayoutView.findViewById(R.id.txtAccNumberId);
            txtIfsc = itemLayoutView.findViewById(R.id.txtIfscId);
            txtMobNo = itemLayoutView.findViewById(R.id.txtMobNoId);
            txtMmid = itemLayoutView.findViewById(R.id.txtMmidId);
//            imgDeleteBeneficiary = itemLayoutView.findViewById(R.id.imgDeleteBeneficiaryId);
            coordinatorLayout = itemLayoutView.findViewById(R.id.coordinatorLayout);
            btnIMPS = itemLayoutView.findViewById(R.id.btnIMPSId);
            btnNEFT = itemLayoutView.findViewById(R.id.btnNEFTId);
            btnWithIn = itemLayoutView.findViewById(R.id.btnWithInId);
            accNameLayout = itemLayoutView.findViewById(R.id.accNameLayoutId);
            accNoLayout = itemLayoutView.findViewById(R.id.accNoLayoutId);
            ifscLayout = itemLayoutView.findViewById(R.id.ifscLayoutId);
            mobNoLayout = itemLayoutView.findViewById(R.id.mobNoLayoutId);
            mmidLayout = itemLayoutView.findViewById(R.id.mmidLayoutId);
            upiLayoutId = itemLayoutView.findViewById(R.id.upiLayoutId);
            txtUpiId = itemLayoutView.findViewById(R.id.txtUpiId);
            btnUpiId = itemLayoutView.findViewById(R.id.btnUpiId);

            upicustomerLayout=itemLayoutView.findViewById(R.id.upicustomerLayoutId);
            txtUpicustomerName=itemLayoutView.findViewById(R.id.txtUpicustomerNameId);
        }
    }

    @Override
    public int getItemCount() {
        return beneficiaryList.size();
    }

    private void confimationRemoveDialog(final String benfId) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
        alert.setTitle(mActivity.getString(R.string.app_name));
        alert.setMessage("Are you sure want to Remove?");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                getBeneficiaryListInterface.GetBeneficiaryClick(benfId);
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void MoveMobileOrAccountDialog(final ArrayList<BeneficiaryModal> beneficiaryList, final String benfNickname) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
        alert.setTitle(mActivity.getString(R.string.lbl_fund_transfer));
        alert.setMessage("IMPS Transfer To:");
        alert.setPositiveButton("Mobile", (dialog, whichButton) -> {
            Intent intent = new Intent(mActivity, IMPSTransferToMobile.class);
            intent.putExtra("beneficiaryList", beneficiaryList);
            intent.putExtra("beneficiaryNickName", benfNickname);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mActivity.startActivity(intent);
            trustMethods.activityOpenAnimation();
            dialog.dismiss();
        });

        alert.setNegativeButton("Account", (dialog, whichButton) -> {
            Intent intent = new Intent(mActivity, IMPSTransferToAccount.class);
            intent.putExtra("beneficiaryList", beneficiaryList);
            intent.putExtra("beneficiaryNickName", benfNickname);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mActivity.startActivity(intent);
            trustMethods.activityOpenAnimation();
            dialog.dismiss();
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        beneficiaryList.clear();
        if (charText.length() == 0) {
            beneficiaryList.addAll(arraylist);
        } else {
            for (BeneficiaryModal wp : arraylist) {
                if (wp.getBenNickname().toLowerCase(Locale.getDefault()).startsWith(charText) || wp.getBenIfscCode().toLowerCase(Locale.getDefault()).startsWith(charText)) {
                    beneficiaryList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
