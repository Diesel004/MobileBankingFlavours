package com.trustbank.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;


import com.trustbank.R;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.TrustURL;

public class PrivacyPolicyFragmentDialog extends DialogFragment implements AlertDialogOkListener {


    private WebView wv1;
    private ImageView cancelDialogue;


    public static DialogFragment newInstance() {
        PrivacyPolicyFragmentDialog fragment = new PrivacyPolicyFragmentDialog();
        return fragment;
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.privacy_policy_fragment, container, false);
        inIt(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("SetJavaScriptEnabled")
    private void inIt(View view) {
        try {
            cancelDialogue = view.findViewById(R.id.cancelDialogue);
            wv1 = view.findViewById(R.id.termsConditionId);
            String myPdfUrl = TrustURL.getTermsConditions();
            wv1.getSettings().setLoadsImagesAutomatically(true);
            wv1.getSettings().setJavaScriptEnabled(true);
            wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            wv1.getSettings().setBuiltInZoomControls(true);
            wv1.getSettings().setBuiltInZoomControls(true);
            wv1.getSettings().setDisplayZoomControls(false);
            wv1.loadUrl(myPdfUrl);
            cancelDialogue.setOnClickListener(v -> dismiss());
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
    public void onPause() {
        super.onPause();
        wv1.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        wv1.onResume();
    }
}
