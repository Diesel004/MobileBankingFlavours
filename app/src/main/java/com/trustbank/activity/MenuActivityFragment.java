package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.Module.ImageTextMenuModel;
import com.trustbank.R;
import com.trustbank.adapter.MenuAdapter;
import com.trustbank.adapter.SlidingImageAdapter;
import com.trustbank.interfaces.AlertDialogListener;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.ItemOffsetDecoration;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.ProfilePictureCapture;
import com.trustbank.util.SessionManager;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.trustbank.util.AlertDialogMethod.alertDialogOk;

public class MenuActivityFragment extends AppCompatActivity implements View.OnClickListener, AlertDialogOkListener, AlertDialogListener {
    private CoordinatorLayout coordinatorLayout;
    private TrustMethods method;
    private SharedPreferences sharedPreferences;
    private SessionManager sessionManager;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "BankApp";
    private String customerName;
    private ProfilePictureCapture profilePictureCapture;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    boolean isShow = false;
    private int currentPage = 0;
    private TextView textViewUserName;
    private AlertDialogOkListener alertDialogOkListener = this;
    private AlertDialogListener alertDialogListener = this;
    private FloatingActionButton logOutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            if (savedInstanceState != null) {
                Object currentPID = String.valueOf(android.os.Process.myPid());
                // Check current PID with old PID
                if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                    // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                    TrustMethods.naviagteToSplashScreen(MenuActivityFragment.this);
                }
            }
        }
        SetTheme.changeToTheme(MenuActivityFragment.this, true);
        setContentView(R.layout.activity_menu);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }*/

        init();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
            bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
        }
    }

    @SuppressLint("CommitPrefEdits")
    private void init() {
        try {
            method = new TrustMethods(MenuActivityFragment.this);

            ImageView imageViewProfile = findViewById(R.id.imageViewProfile_Id);
            profilePictureCapture = new ProfilePictureCapture(this, imageViewProfile);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            /*collapsingToolbarLayout = findViewById(R.id.toolbar_layout);*/
            textViewUserName = findViewById(R.id.textViewUserName_Id);
            logOutButton = findViewById(R.id.fabLogOutButton_Id);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            RecyclerView recyclerViewMenu = findViewById(R.id.recyclerListId);
            coordinatorLayout = findViewById(R.id.coordinatorLayout);
            recyclerViewMenu.setNestedScrollingEnabled(false);

            sharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            sessionManager = new SessionManager(MenuActivityFragment.this);
            imageViewProfile.setOnClickListener(this);

            ImageTextMenuModel[] newitemData = null;

            if ((!TextUtils.isEmpty(AppConstants.getPlay_store_validate())) && AppConstants.getPlay_store_validate().equalsIgnoreCase("1")) {
                newitemData =  staticData(newitemData);
            } else if (AppConstants.getUSERMOBILENUMBER().equalsIgnoreCase(AppConstants.getPlayStoreDemoUserMobile()) &&
                    AppConstants.getCLIENTID().equalsIgnoreCase(AppConstants.getPlayStoreDemoPasswordClientid())) {
                newitemData =   staticData(newitemData);
            } else {

                ImageTextMenuModel[] oldItemData = {
                        new ImageTextMenuModel("Accounts", R.drawable.accounts_one, AppConstants.getMnu_accounts()),
                        new ImageTextMenuModel("Funds Transfer", R.drawable.fund_transfer_one, AppConstants.getMnu_fundtransfer()),
                        new ImageTextMenuModel("Locate ATMs", R.drawable.locate_atm_one, AppConstants.getMnu_locate_atms()),
                        new ImageTextMenuModel("Locate Branches", R.drawable.locate_branch_one, AppConstants.getMnu_locate_branch()),
                        new ImageTextMenuModel("FAQs", R.drawable.ic_frequently_asked_questions, AppConstants.getMnu_faq()),
                        new ImageTextMenuModel("Settings", R.drawable.ic_setting, AppConstants.getMnu_setting()),
                        new ImageTextMenuModel("Contact Us", R.drawable.contact_us_one, AppConstants.getMnu_contact_us()),
                        new ImageTextMenuModel("About Us", R.drawable.about_us_one, AppConstants.getMnu_about_us()),
                        new ImageTextMenuModel("Bill Pay", R.drawable.about_us_one, AppConstants.getMnu_bill_pay()),
                };

                int menuSize = 0;
                for (ImageTextMenuModel oldItemDatum : oldItemData) {
                    if (!TextUtils.isEmpty(oldItemDatum.getIsEnabled())) {
                        if (oldItemDatum.getIsEnabled().trim().equals("1")) {
                            menuSize++;
                        }
                    }
                }

                int j = 0;
                newitemData = new ImageTextMenuModel[menuSize];
                for (ImageTextMenuModel oldItemDatum : oldItemData) {
                    if (!TextUtils.isEmpty(oldItemDatum.getIsEnabled())) {
                        if (oldItemDatum.getIsEnabled().trim().equals("1")) {
                            newitemData[j] = oldItemDatum;
                            j++;
                        }
                    }
                }
            }

            recyclerViewMenu.setItemAnimator(new DefaultItemAnimator());
            recyclerViewMenu.setHasFixedSize(true);
            recyclerViewMenu.setLayoutManager(gridLayoutManager);
            MenuAdapter adapter = new MenuAdapter(MenuActivityFragment.this, newitemData);
            ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(MenuActivityFragment.this, R.dimen.item_offset);
            recyclerViewMenu.addItemDecoration(itemDecoration);
            recyclerViewMenu.setAdapter(adapter);
            recyclerViewMenu.setSelected(true);
            recyclerViewMenu.setItemAnimator(new DefaultItemAnimator());

         /*   collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.Expanded);
            collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
            collapsingToolbarLayout.setTitle(" ");*/

          /*  AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                        isShow = true;
                        invalidateOptionsMenu();
                    }

                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
                        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD);
                        isShow = true;
                        invalidateOptionsMenu();
                    } else if (isShow) {
                        collapsingToolbarLayout.setTitle(" ");
                        isShow = false;
                        invalidateOptionsMenu();
                    }
                }
            });*/

            profilePictureCapture.loadExistingProfile();

            if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
                if ((!TextUtils.isEmpty(AppConstants.getPlay_store_validate())) && AppConstants.getPlay_store_validate().equalsIgnoreCase("1")) {
                    ArrayList<String> stringArrayList = new ArrayList<>();
                    stringArrayList.add("/Advertisment/netbank.png");
                    stringArrayList.add("/Advertisment/netbank.png");
                    viewPager(stringArrayList);
                } else if (AppConstants.getUSERMOBILENUMBER().equalsIgnoreCase(AppConstants.playStoreDemoUserMobile) &&
                        AppConstants.getCLIENTID().equalsIgnoreCase(AppConstants.playStoreDemoPasswordClientid)) {
                    ArrayList<String> stringArrayList = new ArrayList<>();
                    stringArrayList.add("/Advertisment/netbank.png");
                    stringArrayList.add("/Advertisment/netbank.png");
                    viewPager(stringArrayList);
                } else {
                    if (NetworkUtil.getConnectivityStatus(MenuActivityFragment.this)) {
                        if (!TextUtils.isEmpty(AppConstants.getAuth_token())) {
                            new AsyncTaskGetBankDetails(MenuActivityFragment.this).execute();
                        } else {
                            AlertDialogMethod.alertDialog(MenuActivityFragment.this, getResources().getString(R.string.error_session_expire),
                                    getResources().getString(R.string.msg_refresh_token),
                                    getResources().getString(R.string.btn_reload), getResources().getString(R.string.btn_cancel),
                                    11, false, alertDialogListener);
                        }
                    } else {
                        Toast.makeText(MenuActivityFragment.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                TrustMethods.displaySimErrorDialog(this);
            }


            logOutButton.setOnClickListener(v -> {
                AlertDialogMethod.alertDialog(MenuActivityFragment.this, "",
                        getResources().getString(R.string.message_sure_wnt_to_logount),
                        getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_cancel),
                        12, false, alertDialogListener);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ImageTextMenuModel[]  staticData(ImageTextMenuModel[] newitemData) {
        ImageTextMenuModel[] oldItemData = {
                new ImageTextMenuModel("Accounts", R.drawable.accounts_one, "1"),
                new ImageTextMenuModel("Funds Transfer", R.drawable.fund_transfer_one, "1"),
                new ImageTextMenuModel("Locate ATMs", R.drawable.locate_atm_one, "1"),
                new ImageTextMenuModel("Locate Branches", R.drawable.locate_branch_one, "1"),
                new ImageTextMenuModel("FAQs", R.drawable.ic_frequently_asked_questions, "1"),
                new ImageTextMenuModel("Settings", R.drawable.ic_setting, "1"),
                new ImageTextMenuModel("Contact Us", R.drawable.contact_us_one, "1"),
                new ImageTextMenuModel("About Us", R.drawable.about_us_one, "1"),
                new ImageTextMenuModel("Bill Pay", R.drawable.about_us_one, "1"),
        };

        int menuSize = 0;
        for (ImageTextMenuModel oldItemDatum : oldItemData) {
            if (!TextUtils.isEmpty(oldItemDatum.getIsEnabled())) {
                if (oldItemDatum.getIsEnabled().trim().equals("1")) {
                    menuSize++;
                }
            }
        }

        int j = 0;
        newitemData = new ImageTextMenuModel[menuSize];
        for (ImageTextMenuModel oldItemDatum : oldItemData) {
            if (!TextUtils.isEmpty(oldItemDatum.getIsEnabled())) {
                if (oldItemDatum.getIsEnabled().trim().equals("1")) {
                    newitemData[j] = oldItemDatum;
                    j++;
                }
            }
        }

        return newitemData;
    }


    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskGetBankDetails extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response = null;
        ProgressDialog pDialog;
        private String result = null;
        private ArrayList<String> stringArrayList;

        public AsyncTaskGetBankDetails(Context ctx) {
            this.ctx = ctx;
            stringArrayList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MenuActivityFragment.this);
            pDialog.setMessage("Loading Data...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.getAdevertisement();
                if (!url.equals("")) {
                    response = HttpClientWrapper.getResponceDirectalyGET(url, AppConstants.getAuth_token());
                    TrustMethods.LogMessage("response-->", response);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray imageJsonArray = jsonObject.has("files") ? jsonObject.getJSONArray("files") : null;
                    if (imageJsonArray != null) {
                        for (int i = 0; i < imageJsonArray.length(); i++) {
                            stringArrayList.add(imageJsonArray.getString(i));
                        }
                        result = "Success";
                    }

                }
            } catch (Exception ex) {
                TrustMethods.LogMessage("Exception ", ex.getMessage());
                error = ex.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);

            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
                getUserProfile();
                if (this.error != "") {
//                    trustMethods.message(this.ctx, this.error);
                    return;
                } else {
                    if (TextUtils.isEmpty(result)) {
                    } else if (result.equalsIgnoreCase("Success")) {
                        viewPager(stringArrayList);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void viewPager(ArrayList<String> stringArrayList) {
        try {
            float density = getResources().getDisplayMetrics().density;
            final int NUM_PAGES = stringArrayList.size();

            final ViewPager mPager = findViewById(R.id.pager);
            mPager.setAdapter(new SlidingImageAdapter(MenuActivityFragment.this, stringArrayList));

            CirclePageIndicator indicator = findViewById(R.id.indicator);
            indicator.setViewPager(mPager);
            indicator.setRadius(5 * density);

            final Handler handler = new Handler();
            final Runnable Update = () -> {
                if (currentPage == NUM_PAGES) currentPage = 0;
                mPager.setCurrentItem(currentPage++, true);
            };

            Timer swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, 4000, 4000);

            indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrolled(int pos, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int pos) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserProfile() {
        if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
            if (NetworkUtil.getConnectivityStatus(MenuActivityFragment.this)) {
                method.clearAccountsArrayList(MenuActivityFragment.this);
                if (!TextUtils.isEmpty(AppConstants.getAuth_token())) {
                    new AuthenticateUserAsyncTask(MenuActivityFragment.this, AppConstants.getUSERMOBILENUMBER()).execute();
                } else {
                    AlertDialogMethod.alertDialog(MenuActivityFragment.this, getResources().getString(R.string.error_session_expire),
                            getResources().getString(R.string.msg_refresh_token),
                            getResources().getString(R.string.btn_reload), getResources().getString(R.string.btn_cancel),
                            11, false, alertDialogListener);
                }

            } else {
                TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
            }
        } else {
            TrustMethods.displaySimErrorDialog(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewProfile_Id:
                profilePictureCapture.selectTheImage();
                break;
            default:
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AuthenticateUserAsyncTask extends AsyncTask<Void, Void, String> {
        private String error = "";
        private Context ctx;
        private String response;
        private ProgressDialog pDialog;
        private String result;
        private String mMobileNumber;
        private String actionName = "GET_PROFILE";
        private ArrayList<GetUserProfileModal> getUserProfileList;
        private String errorCode;

        public AuthenticateUserAsyncTask(Context ctx, String mobileNumber) {
            this.ctx = ctx;
            this.mMobileNumber = mobileNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MenuActivityFragment.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.GetProfileAccountsAndChequeBookDetailsUrl(mMobileNumber, AppConstants.getCLIENTID());
                if (!url.equals("")) {
                    result = HttpClientWrapper.getResponseGET(url, actionName, AppConstants.getAuth_token());
                }
                if (result == null || result.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResponse = (new JSONObject(result));
                if (jsonResponse.has("error")) {
                    error = jsonResponse.getString("error");
                    return error;
                }
                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";

                if (responseCode.equals("1")) {
                    JSONObject dataObject = jsonResponse.getJSONObject("response");
                    if (dataObject.has("error")) {
                        error = dataObject.getString("error");
                        return error;
                    }

                    JSONObject profileJsonObject = dataObject.getJSONObject("profile");
                    if (profileJsonObject.has("error")) {
                        error = profileJsonObject.getString("error");
                        return error;
                    }
                    String profileId = profileJsonObject.has("profileid") ? profileJsonObject.getString("profileid") : "NA";
                    String mobileNo = profileJsonObject.has("mobileno") ? profileJsonObject.getString("mobileno") : "NA";
                    customerName = profileJsonObject.has("cust_name") ? profileJsonObject.getString("cust_name") : "NA";
                    String emailId = profileJsonObject.has("email") ? profileJsonObject.getString("email") : "NA";
                    String clientId = profileJsonObject.has("clientid") ? profileJsonObject.getString("clientid") : "NA";

                    AppConstants.setProfileID(profileId);
                    AppConstants.setUSEREMAILADDRESS(emailId);
                    AppConstants.setUSERNAME(customerName);
                    AppConstants.setCLIENTID(clientId);


                    JSONObject accountsJsonObject = profileJsonObject.has("accounts") ? profileJsonObject.getJSONObject("accounts") : null;
                    if (accountsJsonObject != null) {
                        if (accountsJsonObject.has("error")) {
                            error = accountsJsonObject.getString("error");
                            return error;
                        }

                        //TODO.....................................................................
                        JSONArray accountsJsonArray = accountsJsonObject.has("account") ? accountsJsonObject.getJSONArray("account") : null;
                        if (accountsJsonArray != null) {
                            getUserProfileList = new ArrayList<>();
                            if (accountsJsonArray.length() > 0) {
                                for (int i = 0; i < accountsJsonArray.length(); i++) {
                                    JSONObject dataJsonObject = accountsJsonArray.getJSONObject(i);

                                    String accNo = dataJsonObject.has("acno") ? dataJsonObject.getString("acno") : "NA";
                                    String mmid = dataJsonObject.has("mmid") ? dataJsonObject.getString("mmid") : "NA";
                                    String name = dataJsonObject.has("name") ? dataJsonObject.getString("name") : "NA";
                                    String acType = dataJsonObject.has("ac_type") ? dataJsonObject.getString("ac_type") : "";
                                    String ac_status = dataJsonObject.has("ac_status") ? dataJsonObject.getString("ac_status") : "";
                                    String is_imps_reg = dataJsonObject.has("is_imps_reg") ? dataJsonObject.getString("is_imps_reg") : "";
                                    String accountid = dataJsonObject.has("accountid") ? dataJsonObject.getString("accountid") : "";
                                    String orgelementid = dataJsonObject.has("orgelementid") ? dataJsonObject.getString("orgelementid") : "";
                                    String ac_type_code = dataJsonObject.has("ac_type_code") ? dataJsonObject.getString("ac_type_code") : "";

                                    GetUserProfileModal getUserProfileModal = new GetUserProfileModal();
                                    getUserProfileModal.setAccNo(accNo);
                                    getUserProfileModal.setMmid(mmid);
                                    getUserProfileModal.setName(name);
                                    getUserProfileModal.setActType(acType);
                                    getUserProfileModal.setAc_status(ac_status);
                                    getUserProfileModal.setIs_imps_reg(is_imps_reg);
                                    getUserProfileModal.setAccountid(accountid);
                                    getUserProfileModal.setOrgelementid(orgelementid);
                                    getUserProfileModal.setAcTypeCode(ac_type_code);

                                    getUserProfileList.add(getUserProfileModal);
                                }
                            }
                        } else {
                            error = "Accounts details not found";
                            return error;
                        }
                    } else {
                        error = "Accounts details not found";
                        return error;
                    }


                } else {
                    errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
                    error = jsonResponse.has("error_message") ? jsonResponse.getString("error_message") : "NA";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return error;
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                if (!this.error.equals("")) {
                    if (TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialog(MenuActivityFragment.this, getResources().getString(R.string.error_session_expire),
                                getResources().getString(R.string.msg_refresh_token),
                                getResources().getString(R.string.btn_reload), getResources().getString(R.string.btn_cancel),
                                11, false, alertDialogListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                } else {
                    if (getUserProfileList != null && getUserProfileList.size() != 0) {
                        textViewUserName.setText(customerName);
                        method.saveArrayList(MenuActivityFragment.this, getUserProfileList, "AccountListPref");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class LogoutUserAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        String result;
        private String errorCode;

        public LogoutUserAsyncTask(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MenuActivityFragment.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String url = TrustURL.LogoutUserUrl();

                if (!url.equals("")) {
                    result = HttpClientWrapper.postWitAuthHeader(url, "", AppConstants.getAuth_token());
                }

                if (result == null || result.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }

                JSONObject jsonResponse = (new JSONObject(result));
                if (jsonResponse.has("error")) {
                    error = jsonResponse.getString("error");
                    return error;
                }

                String responseCode = jsonResponse.has("response_code") ? jsonResponse.getString("response_code") : "NA";
                if (responseCode.equals("1")) {
                    response = "true";
                } else {
                    errorCode = jsonResponse.has("error_code") ? jsonResponse.getString("error_code") : "NA";
                    error = jsonResponse.has("error_message") ? jsonResponse.getString("error_message") : "NA";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = e.getMessage();
                return error;
            } catch (Exception ex) {
                error = ex.getMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String value) {
            super.onPostExecute(value);
            try {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                if (!this.error.equals("")) {
                    if (TrustMethods.isLogoutSessionExpired(errorCode)) {
                        alertDialogOk(MenuActivityFragment.this, getResources().getString(R.string.error_session_expire),
                                "", getResources().getString(R.string.btn_ok), 0, false, alertDialogOkListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }

                } else {
                    if (response != null) {
                        method.clearAccountsArrayList(MenuActivityFragment.this);
                        sessionManager.logoutUser();
                    } else {
                        TrustMethods.showSnackBarMessage("Logout failed", coordinatorLayout);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //Refresh Token Alert Dialog....................
    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 0) {
            Intent intent = new Intent(MenuActivityFragment.this, LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            method.activityCloseAnimation();
            finish();
        } else if (resultCode == 11) {
            method.refreshToken(MenuActivityFragment.this);
        } else if (resultCode == 12) {
            //sessionManager.logoutUser();
            if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
                if (NetworkUtil.getConnectivityStatus(MenuActivityFragment.this)) {
                    new LogoutUserAsyncTask(MenuActivityFragment.this).execute();
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                }
            } else {
                TrustMethods.displaySimErrorDialog(this);
            }
        }
    }

    @Override
    public void onDialogCancel(int resultCode) {
        //Toast.makeText(getApplicationContext(), "Dialog Cancel", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != this.RESULT_OK) return;
        profilePictureCapture.takeImageToActivity(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(MenuActivityFragment.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
