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
import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.R;
import com.trustbank.adapter.UPIBenVPANameAdapter;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.interfaces.UPIGetNameAdapterInterface;
import com.trustbank.interfaces.UPISelectBenInterface;

import java.util.ArrayList;
import java.util.List;

public class UPIBenListFragmentDialog extends DialogFragment implements AlertDialogOkListener, UPIGetNameAdapterInterface {

    private RecyclerView recyclerView;
    private List<BeneficiaryModal> beneficiaryArrayList;
    UPISelectBenInterface upiSelectBenInterface;


    public static DialogFragment newInstance(ArrayList<BeneficiaryModal> beneficiaryArrayList, UPISelectBenInterface upiSelectBenInterface) {
        UPIBenListFragmentDialog fragment = new UPIBenListFragmentDialog();
        fragment.setBeneficiaryList(beneficiaryArrayList);
        fragment.setInterface(upiSelectBenInterface);
        return fragment;
    }

    private void setInterface(UPISelectBenInterface upiSelectBenInterface) {
        this.upiSelectBenInterface = upiSelectBenInterface;
    }


    public void setBeneficiaryList(ArrayList<BeneficiaryModal> beneficiaryArrayList) {
        this.beneficiaryArrayList = beneficiaryArrayList;
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
        View view = inflater.inflate(R.layout.upi_ben_vpa_name_fragment, container, false);
        inIt(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("SetJavaScriptEnabled")
    private void inIt(View view) {
        try {

            recyclerView = view.findViewById(R.id.recyclerUpiNameID);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);

            UPIBenVPANameAdapter bbpsSearchBillerAdapter = new UPIBenVPANameAdapter(getActivity(),
                    beneficiaryArrayList, this);
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
    public void getUpiBenNameAdapter(String upiId,String custName,String nickName) {
        upiSelectBenInterface.selectUPIVPABenName(upiId,custName,nickName);
        dismiss();
    }


   /* public interface GetLIstClickListnerInterface {

        void GetAmountClickOnListEvent(String amount);
    }*/

}
