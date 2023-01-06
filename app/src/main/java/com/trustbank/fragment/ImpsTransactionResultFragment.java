package com.trustbank.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.trustbank.Model.IMPSTransactionRequestModel;
import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;

public class ImpsTransactionResultFragment extends DialogFragment implements AlertDialogOkListener {

    private ImageView cancelDialogue;
    private TextView transactionStatusId, transactionDateId, rrnNoTextViewId, tranTypeTextViewId,
            benNameTextViewId, AmountTextViewId, benMobileNoTextViewId, MMIDTextViewId, benRemmitterAccNameTextViewId,
            benAccountNoTextViewId, benIFSCCodeTextViewId, benRemmitterAccNoextViewId;
    private CardView cardMobileId;
    private IMPSTransactionRequestModel resultImpsTransactionModel;


    public static DialogFragment newInstance(IMPSTransactionRequestModel resultImpsTransactionModel) {
        ImpsTransactionResultFragment fragment = new ImpsTransactionResultFragment();
        fragment.setImpsResultMode(resultImpsTransactionModel);
        return fragment;
    }

    private void setImpsResultMode(IMPSTransactionRequestModel resultImpsTransactionModel) {
        this.resultImpsTransactionModel = resultImpsTransactionModel;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.imps_transactions_result_fragment, container, false);
        inIt(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("SetJavaScriptEnabled")
    private void inIt(View view) {
        try {
            cancelDialogue = view.findViewById(R.id.cancelDialogue);
            transactionStatusId = view.findViewById(R.id.transactionStatusId);
            transactionDateId = view.findViewById(R.id.transactionDateId);
            rrnNoTextViewId = view.findViewById(R.id.rrnNoTextViewId);
            tranTypeTextViewId = view.findViewById(R.id.tranTypeTextViewId);
            //  benNameTextViewId = view.findViewById(R.id.benNameTextViewId);
            AmountTextViewId = view.findViewById(R.id.AmountTextViewId);
            benNameTextViewId = view.findViewById(R.id.benNameTextViewId);
            benMobileNoTextViewId = view.findViewById(R.id.benMobileNoTextViewId);
            MMIDTextViewId = view.findViewById(R.id.MMIDTextViewId);
            benRemmitterAccNameTextViewId = view.findViewById(R.id.benRemmitterAccNameTextViewId);
            cardMobileId = view.findViewById(R.id.cardMobileId);

            benAccountNoTextViewId = view.findViewById(R.id.benAccountNoTextViewId);
            benIFSCCodeTextViewId = view.findViewById(R.id.benIFSCCodeTextViewId);
            benRemmitterAccNoextViewId = view.findViewById(R.id.benRemmitterAccNoextViewId);

            //  benRemmitterAccNameTextViewId = view.findViewById(R.id.benRemmitterAccNameTextViewId);
            cancelDialogue.setOnClickListener(v -> dismiss());

            transactionStatusId.setText(resultImpsTransactionModel.getImpsResultMessage());
            transactionDateId.setText(resultImpsTransactionModel.getTransDateTime());
            rrnNoTextViewId.setText(resultImpsTransactionModel.getRrn());
            tranTypeTextViewId.setText(resultImpsTransactionModel.getTranType());
            AmountTextViewId.setText("\u20B9" + " " + resultImpsTransactionModel.getAmount());
            benNameTextViewId.setText(resultImpsTransactionModel.getBenName());
            benRemmitterAccNoextViewId.setText("Account No : " + resultImpsTransactionModel.getRemAcno());

            benAccountNoTextViewId.setText("Account No : " + resultImpsTransactionModel.getBenAcno());
            benIFSCCodeTextViewId.setText("IFSC Code  : " + resultImpsTransactionModel.getBenIfsc());

            if (!TextUtils.isEmpty(resultImpsTransactionModel.getBenMobile())) {
                benMobileNoTextViewId.setText("Mobile : " + resultImpsTransactionModel.getBenMobile());
            } else {
                benMobileNoTextViewId.setVisibility(View.GONE);
            }



            if (resultImpsTransactionModel.getSwitchReqType().equalsIgnoreCase("4")) {
                MMIDTextViewId.setText("MMID   : " + resultImpsTransactionModel.getBenMmid());
                benAccountNoTextViewId.setVisibility(View.GONE);
                benIFSCCodeTextViewId.setVisibility(View.GONE);
                if (TextUtils.isEmpty(resultImpsTransactionModel.getTranType())) {
                    tranTypeTextViewId.setText("IMPS-MOBILE");
                }
            } else if (resultImpsTransactionModel.getSwitchReqType().equalsIgnoreCase("8")) {
                MMIDTextViewId.setVisibility(View.GONE);
                benAccountNoTextViewId.setVisibility(View.VISIBLE);
                benIFSCCodeTextViewId.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(resultImpsTransactionModel.getTranType())) {
                    tranTypeTextViewId.setText("IMPS-ACCOUNT");
                }
            } else if (resultImpsTransactionModel.getSwitchReqType().equalsIgnoreCase("12")) {
                MMIDTextViewId.setVisibility(View.GONE);
                benMobileNoTextViewId.setVisibility(View.GONE);
                benAccountNoTextViewId.setVisibility(View.VISIBLE);
                benIFSCCodeTextViewId.setVisibility(View.GONE);
                if (TextUtils.isEmpty(resultImpsTransactionModel.getTranType())) {
                    tranTypeTextViewId.setText("UPI Transaction");
                }
                benAccountNoTextViewId.setText("UPI ID : " + resultImpsTransactionModel.getBenUpiId());
            } else if (resultImpsTransactionModel.getSwitchReqType().equalsIgnoreCase("13")) {
                MMIDTextViewId.setVisibility(View.GONE);
                benAccountNoTextViewId.setVisibility(View.VISIBLE);
                benIFSCCodeTextViewId.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(resultImpsTransactionModel.getTranType())) {
                    tranTypeTextViewId.setText("NEFT-ACCOUNT");
                }
            } else if (resultImpsTransactionModel.getSwitchReqType().equalsIgnoreCase("19")) {
                MMIDTextViewId.setVisibility(View.GONE);
                benAccountNoTextViewId.setVisibility(View.VISIBLE);
                benIFSCCodeTextViewId.setVisibility(View.GONE);
                if (TextUtils.isEmpty(resultImpsTransactionModel.getTranType())) {
                    tranTypeTextViewId.setText("BILL PAY");
                }
                benAccountNoTextViewId.setText("Biller Name: " + resultImpsTransactionModel.getBenName());
            }


            benRemmitterAccNameTextViewId.setText(resultImpsTransactionModel.getRemName());


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDialogOk(int resultCode) {
        try {
            if (resultCode == 1) {

            } else if (resultCode == 2) {
                //Dismiss alert dialog here.
            } else if (resultCode == 3) {
                //Dismiss alert dialog here.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
