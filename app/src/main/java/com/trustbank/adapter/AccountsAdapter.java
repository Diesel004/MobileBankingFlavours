package com.trustbank.adapter;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trustbank.Module.ImageTextMenuModel;
import com.trustbank.R;
import com.trustbank.activity.AccountDetailsActivity;
import com.trustbank.activity.BalanceEnquiryActivity;
import com.trustbank.activity.BalanceEnquiryCBSActivity;
import com.trustbank.activity.BlockDebitCardActivity;
import com.trustbank.activity.BlockDebitCardSwitchActivity;
import com.trustbank.activity.ChequeStatusActivity;
import com.trustbank.activity.ChequebookRequestActivity;
import com.trustbank.activity.ComplaintManagementActivity;
import com.trustbank.activity.FrmAccountStatement;
import com.trustbank.activity.FrmPPSServiceRequestEnquiry;
import com.trustbank.activity.ImpsCheckTransactionRequest;
import com.trustbank.activity.LastFiveImpsTransactionActivity;
import com.trustbank.activity.LastFiveImpsTransactionCBSActivity;
import com.trustbank.activity.MandateCancelActivity;
import com.trustbank.activity.MiniStatementActivity;
import com.trustbank.activity.MiniStatementCBSActivity;
import com.trustbank.activity.NeftEnquiryActivity;
import com.trustbank.activity.PPSRequestActivity;
import com.trustbank.activity.ShowMMIDActivity;
import com.trustbank.activity.ShowMMIDCBSActivity;
import com.trustbank.activity.StopChequeRequestActivity;
import com.trustbank.util.TrustMethods;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.ViewHolder> {
    private static final String TAG = AccountsAdapter.class.getSimpleName();
    Activity mActivity;
    TrustMethods trustMethods;
    private ImageTextMenuModel[] accountItemsData;
    private String newName = "Name";

    public AccountsAdapter(Activity activity, ImageTextMenuModel[] accountItemsData) {
        this.mActivity = activity;
        this.accountItemsData = accountItemsData;
        trustMethods = new TrustMethods(mActivity);

        Log.d("NewerName", newName);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_accounts, null);
        RecyclerView.LayoutParams lp;
        lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemLayoutView.setLayoutParams(lp);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.accountMenuName.setText(accountItemsData[position].getLableName());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView accountMenuName;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            accountMenuName = itemLayoutView.findViewById(R.id.accountMenuNameId);
        }

        @Override
        public void onClick(View view) {
            switch (accountItemsData[getAdapterPosition()].getItemName().trim()) {

                case "Account Details":
                    Intent intentAccDetails = new Intent(view.getContext(), AccountDetailsActivity.class);
                    intentAccDetails.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentAccDetails);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Balance Enquiry":
                    Intent intentBalEnquiry = new Intent(view.getContext(), BalanceEnquiryActivity.class);
                    intentBalEnquiry.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentBalEnquiry);
                    trustMethods.activityOpenAnimation();
                    break;


                case "Statement Request":
                    Intent intentMiniStatement = new Intent(view.getContext(), MiniStatementActivity.class);
                    intentMiniStatement.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentMiniStatement);
                    trustMethods.activityOpenAnimation();
                    break;


                case "Last 5 IMPS Transactions":
                    Intent intentLastFiveTrans = new Intent(view.getContext(), LastFiveImpsTransactionActivity.class);
                    intentLastFiveTrans.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentLastFiveTrans);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Show MMID":
                    Intent intentShowMmid = new Intent(view.getContext(), ShowMMIDActivity.class);
                    intentShowMmid.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentShowMmid);
                    trustMethods.activityOpenAnimation();
                    break;

                case "NEFT Enquiry":
                    Intent intentNeftEnquiry = new Intent(view.getContext(), NeftEnquiryActivity.class);
                    intentNeftEnquiry.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentNeftEnquiry);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Chequebook Request":
                    Intent intentCheckbookRequest = new Intent(view.getContext(), ChequebookRequestActivity.class);
                    intentCheckbookRequest.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentCheckbookRequest);
                    trustMethods.activityOpenAnimation();
                    break;

                case "mPassBook":
                    Intent intentAccountStatement = new Intent(view.getContext(), FrmAccountStatement.class);
                    intentAccountStatement.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentAccountStatement);
                    trustMethods.activityOpenAnimation();
                    break;
                case "Cheque Status":
                    Intent intentChequebookStauts = new Intent(view.getContext(), ChequeStatusActivity.class);
                    intentChequebookStauts.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentChequebookStauts);
                    trustMethods.activityOpenAnimation();
                    break;
                case "Stop Cheque Request":
                    Intent intentStopChequebookStauts = new Intent(view.getContext(), StopChequeRequestActivity.class);
                    intentStopChequebookStauts.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentStopChequebookStauts);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Balance Enquiry CBS":
                    Intent intentBalEnquiryCBS = new Intent(view.getContext(), BalanceEnquiryCBSActivity.class);
                    intentBalEnquiryCBS.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentBalEnquiryCBS);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Statement Request CBS":
                    Intent intentMiniStatementCBS = new Intent(view.getContext(), MiniStatementCBSActivity.class);
                    intentMiniStatementCBS.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentMiniStatementCBS);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Last 5 IMPS Transactions CBS":
                    Intent intentLastFiveTransCBS = new Intent(view.getContext(), LastFiveImpsTransactionCBSActivity.class);
                    intentLastFiveTransCBS.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentLastFiveTransCBS);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Show MMID CBS":
                    Intent intentShowMmidCBS = new Intent(view.getContext(), ShowMMIDCBSActivity.class);
                    intentShowMmidCBS.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentShowMmidCBS);
                    trustMethods.activityOpenAnimation();
                    break;

                case "PPS Request":
                    Intent intentPPSRequest = new Intent(view.getContext(), PPSRequestActivity.class);
                    intentPPSRequest.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentPPSRequest);
                    trustMethods.activityOpenAnimation();
                    break;

                case "PPS Request Enquiry":
                    Intent intentPPSRequestEnquiry = new Intent(view.getContext(), FrmPPSServiceRequestEnquiry.class);
                    intentPPSRequestEnquiry.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentPPSRequestEnquiry);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Block Debit Card":
                    Intent intentDebitCardBlock = new Intent(view.getContext(), BlockDebitCardActivity.class);
                    intentDebitCardBlock.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentDebitCardBlock);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Check Transaction Status":
                    Intent intentImpsTransctionRequest = new Intent(view.getContext(), ImpsCheckTransactionRequest.class);
                    intentImpsTransctionRequest.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentImpsTransctionRequest);
                    trustMethods.activityOpenAnimation();
                    break;

                case "Complaint Managements":
                    Intent intentComplaintsRegistrations = new Intent(view.getContext(), ComplaintManagementActivity.class);
                    intentComplaintsRegistrations.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentComplaintsRegistrations);
                    trustMethods.activityOpenAnimation();
                    break;

                  case "Block Debit Card Switch":
                    Intent intentBlockDebitCardSwitch = new Intent(view.getContext(), BlockDebitCardSwitchActivity.class);
                    intentBlockDebitCardSwitch.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentBlockDebitCardSwitch);
                    trustMethods.activityOpenAnimation();
                    break;

                    case "ECS Mandate Cancellation":
                    Intent intentMandateCancel = new Intent(view.getContext(), MandateCancelActivity.class);
                    intentMandateCancel.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(intentMandateCancel);
                    trustMethods.activityOpenAnimation();
                    break;


                default:
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        return accountItemsData.length;
    }
}
