package com.trustbank.adapter;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trustbank.Module.ImageTextMenuModel;
import com.trustbank.R;
import com.trustbank.activity.AboutUsActivity;
import com.trustbank.activity.AccountsActivity;
import com.trustbank.activity.BBPSBillerCategoryActivity;
import com.trustbank.activity.BranchesActivity;
import com.trustbank.activity.ContactUsActivity;
import com.trustbank.activity.FAQActivity;
import com.trustbank.activity.FundsTransferMenu;
import com.trustbank.activity.LocateAtmsActivity;
import com.trustbank.activity.SettingActivity;
import com.trustbank.util.TrustMethods;


public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private static final String TAG = MenuAdapter.class.getSimpleName();
    private ImageTextMenuModel[] itemsData;
    Activity mActivity;
    TrustMethods trustMethods;

    public MenuAdapter(Activity activity, ImageTextMenuModel[] itemsData) {
        this.itemsData = itemsData;
        this.mActivity = activity;
        trustMethods = new TrustMethods(mActivity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_menu_demo, null);
       /* RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.w, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);*/

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        switch (itemsData[position].getItemName().trim()) {

            case "Accounts":
                viewHolder.imgCrdView.setBackgroundColor(mActivity.getResources().getColor(R.color.buying_tips));
                viewHolder.txtViewTitle.setText(itemsData[position].getItemName());
                viewHolder.imgViewIcon.setImageResource(itemsData[position].getImage());
                break;

            case "Funds Transfer":
                viewHolder.imgCrdView.setBackgroundColor(mActivity.getResources().getColor(R.color.live_score));
                viewHolder.txtViewTitle.setText(itemsData[position].getItemName());
                viewHolder.imgViewIcon.setImageResource(itemsData[position].getImage());
                break;

            case "Locate ATMs":
                viewHolder.imgCrdView.setBackgroundColor(mActivity.getResources().getColor(R.color.tips_schedule));
                viewHolder.txtViewTitle.setText(itemsData[position].getItemName());
                viewHolder.imgViewIcon.setImageResource(itemsData[position].getImage());
                break;

            case "Locate Branches":
                viewHolder.imgCrdView.setBackgroundColor(mActivity.getResources().getColor(R.color.result));
                viewHolder.txtViewTitle.setText(itemsData[position].getItemName());
                viewHolder.imgViewIcon.setImageResource(itemsData[position].getImage());
                break;

            case "Contact Us":
                viewHolder.imgCrdView.setBackgroundColor(mActivity.getResources().getColor(R.color.video));
                viewHolder.txtViewTitle.setText(itemsData[position].getItemName());
                viewHolder.imgViewIcon.setImageResource(itemsData[position].getImage());
                break;

            case "About Us":
                viewHolder.imgCrdView.setBackgroundColor(mActivity.getResources().getColor(R.color.rate_us));
                viewHolder.txtViewTitle.setText(itemsData[position].getItemName());
                viewHolder.imgViewIcon.setImageResource(itemsData[position].getImage());
                break;
            case "FAQs":
                viewHolder.imgCrdView.setBackgroundColor(mActivity.getResources().getColor(R.color.faq));
                viewHolder.txtViewTitle.setText(itemsData[position].getItemName());
                viewHolder.imgViewIcon.setImageResource(itemsData[position].getImage());
                break;

            case "Settings":
                viewHolder.imgCrdView.setBackgroundColor(mActivity.getResources().getColor(R.color.chnage_tpin));
                viewHolder.txtViewTitle.setText(itemsData[position].getItemName());
                viewHolder.imgViewIcon.setImageResource(itemsData[position].getImage());
                break;

            case "Bill Pay":
                viewHolder.imgCrdView.setBackgroundColor(mActivity.getResources().getColor(R.color.live_score));
                viewHolder.txtViewTitle.setText(itemsData[position].getItemName());
                viewHolder.imgViewIcon.setImageResource(itemsData[position].getImage());
                break;

            default:
                break;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final RelativeLayout linear_menu;
        public TextView txtViewTitle;
        public ImageView imgViewIcon;
        private CardView imgCrdView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            txtViewTitle = itemLayoutView.findViewById(R.id.txt_state_name);
            imgViewIcon = itemLayoutView.findViewById(R.id.departmentIV);
            linear_menu = itemLayoutView.findViewById(R.id.linear_menu);
            imgCrdView = itemLayoutView.findViewById(R.id.imgCrdViewId);
        }

        @Override
        public void onClick(View view) {
            switch (itemsData[getAdapterPosition()].getItemName().trim()) {

                case "Accounts":
                    Intent intentAccounts = new Intent(view.getContext(), AccountsActivity.class);
                    intentAccounts.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentAccounts);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Funds Transfer":
                    Intent intentFundTransferAccount = new Intent(view.getContext(), FundsTransferMenu.class);
                    intentFundTransferAccount.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentFundTransferAccount);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Locate ATMs":
                    Intent intenttmAccounts = new Intent(view.getContext(), LocateAtmsActivity.class);
                    intenttmAccounts.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intenttmAccounts);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Locate Branches":
                    Intent intentBranches = new Intent(view.getContext(), BranchesActivity.class);
                    intentBranches.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentBranches);
                    trustMethods.activityOpenAnimation();
                    break;


                case "Contact Us":
                    Intent intentContactUs = new Intent(view.getContext(), ContactUsActivity.class);
                    intentContactUs.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentContactUs);
                    trustMethods.activityOpenAnimation();
                    break;

                case "About Us":
                    Intent intentAboutUs = new Intent(view.getContext(), AboutUsActivity.class);
                    intentAboutUs.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentAboutUs);
                    trustMethods.activityOpenAnimation();
                    break;
                case "FAQs":
                    Intent intentFAQ = new Intent(view.getContext(), FAQActivity.class);
                    intentFAQ.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentFAQ);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Settings":
                    Intent intentChangeTpin = new Intent(view.getContext(), SettingActivity.class);
                    intentChangeTpin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentChangeTpin);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Bill Pay":
                    Intent intentBillPay= new Intent(view.getContext(), BBPSBillerCategoryActivity.class);
                    intentBillPay.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentBillPay);
                    trustMethods.activityOpenAnimation();
                    break;


                default:
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        return itemsData.length;
    }
}
