package com.trustbank.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trustbank.Model.BBPSTitlevalueModel;
import com.trustbank.R;
import com.trustbank.activity.BBPSDisplayBillFetchedDetailsActivity;
import com.trustbank.adapter.BBPCustomAmountAdapter;
import com.trustbank.adapter.BBPDisplayBillPayerDetailsAdapter;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.interfaces.BBPSClickEvenAmountInterface;
import com.trustbank.interfaces.BBPSGetLIstClickListnerInterface;
import com.trustbank.util.TrustURL;

import java.util.List;

public class BBPSCustomAmountFragmentDialog extends DialogFragment implements AlertDialogOkListener, BBPSGetLIstClickListnerInterface {

    private RecyclerView recyclerView;
    private List<BBPSTitlevalueModel> bbpsTitlevalueModelsList;
    BBPSClickEvenAmountInterface clickEvenAmount;


    public static DialogFragment newInstance(List<BBPSTitlevalueModel> bbpsTitlevalueModelsList,
                                             BBPSClickEvenAmountInterface clickEvenAmount) {
        BBPSCustomAmountFragmentDialog fragment = new BBPSCustomAmountFragmentDialog();
        fragment.setBBPSTitleValueList(bbpsTitlevalueModelsList);
        fragment.setInterface(clickEvenAmount);
        return fragment;
    }

    private void setInterface(BBPSClickEvenAmountInterface clickEvenAmount) {
        this.clickEvenAmount = clickEvenAmount;
    }


    public void setBBPSTitleValueList(List<BBPSTitlevalueModel> bbpsTitlevalueModelsList) {
        this.bbpsTitlevalueModelsList = bbpsTitlevalueModelsList;
    }

    @Override
    public void onStart() {
        super.onStart();
     /*   Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }*/
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bbps_custom_amt_fragment, container, false);
        inIt(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("SetJavaScriptEnabled")
    private void inIt(View view) {
        try {

            recyclerView = view.findViewById(R.id.recyclerCustomeAmountID);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);

            BBPCustomAmountAdapter bbpsSearchBillerAdapter = new BBPCustomAmountAdapter(getActivity(),
                    bbpsTitlevalueModelsList,this);
            recyclerView.setAdapter(bbpsSearchBillerAdapter);


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

    @Override
    public void GetAmountClickOnListEvent(String amount) {
        clickEvenAmount.getSelectedAmount(amount);
        dismiss();
    }


   /* public interface GetLIstClickListnerInterface {

        void GetAmountClickOnListEvent(String amount);
    }*/

}
