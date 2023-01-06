package com.trustbank.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.trustbank.Model.AtmModel;
import com.trustbank.R;
import com.trustbank.adapter.LocateAtmAdapter;
import com.trustbank.interfaces.AlertDialogOkListener;
import com.trustbank.maps.MapsActivity;
import com.trustbank.util.AlertDialogMethod;
import com.trustbank.util.AppConstants;
import com.trustbank.util.HttpClientWrapper;
import com.trustbank.util.NetworkUtil;
import com.trustbank.util.SetTheme;
import com.trustbank.util.TrustMethods;
import com.trustbank.util.TrustURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class LocateAtmsActivity extends AppCompatActivity implements AlertDialogOkListener {
    private TrustMethods method;
    private Spinner selectStateSpinner, selectCitySpinner;
    private LinearLayout selectCityLayout;
    private EditText searchLocationPin;
    private RecyclerView recyclerAtm;
    private CoordinatorLayout coordinatorLayout;
    String selectedState;
    private String TAG = LocateAtmsActivity.class.getSimpleName();
    private LocateAtmAdapter locateAtmAdapter;
    ArrayList<AtmModel> atmModelsList;
    ArrayList<String> atmModelsStateList;
    ArrayList<String> atmModelsCityList;
    private FloatingActionButton floatingAtmMap;
    private ArrayList<AtmModel> atmModelsFilterData;
    AlertDialogOkListener alertDialogListener = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
                if (savedInstanceState != null) {
                    Object currentPID = String.valueOf(android.os.Process.myPid());
                    // Check current PID with old PID
                    if (currentPID != savedInstanceState.getString(AppConstants.PID)) {
                        // If current PID and old PID are not equal, new process was created, restart the app from SplashActivity
                        TrustMethods.naviagteToSplashScreen(LocateAtmsActivity.this);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SetTheme.changeToTheme(LocateAtmsActivity.this, false);
        setContentView(R.layout.activity_locate_atms);

        inIt();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        try {
            if (AppConstants.IS_CHECK_ACCESS_BROKEN) {
                bundle.putString(AppConstants.PID, String.valueOf(android.os.Process.myPid()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void inIt() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        method = new TrustMethods(LocateAtmsActivity.this);
        selectStateSpinner = findViewById(R.id.selectStateSpinnerId);
        selectCitySpinner = findViewById(R.id.selectCitySpinnerId);
        selectCityLayout = findViewById(R.id.selectCityLayoutId);
        searchLocationPin = findViewById(R.id.searchLocationPinId);
        recyclerAtm = findViewById(R.id.recyclerAtmId);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        floatingAtmMap = findViewById(R.id.floatingAtmMapId);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerAtm.setLayoutManager(mLayoutManager);

        floatingAtmMap.setOnClickListener(view -> {
            try {
                if (atmModelsFilterData != null && atmModelsFilterData.size() > 0) {

                    if (method.checkLocation(LocateAtmsActivity.this)) {
                        Intent intentMaps = new Intent(LocateAtmsActivity.this, MapsActivity.class);
                        intentMaps.putExtra("locationDetArraylist", atmModelsFilterData);
                        startActivity(intentMaps);
                        method.activityOpenAnimation();
                    } else {
                        AlertDialogMethod.alertDialogOk(LocateAtmsActivity.this, "GPS Setting", "", getResources().getString(R.string.msg_gps_enable), 1, false, alertDialogListener);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        setSpinner();
        searchFunctionality();


    }


    private void setSpinner() {
        try {
            if (TrustMethods.isSimAvailable(getApplicationContext()) && TrustMethods.isSimVerified(this)) {
                if (NetworkUtil.getConnectivityStatus(LocateAtmsActivity.this)) {
                    new LoadStateSpinnerAsyncTask(LocateAtmsActivity.this).execute();
                } else {
                    TrustMethods.showSnackBarMessage(getResources().getString(R.string.error_check_internet), coordinatorLayout);
                }
            } else {
                TrustMethods.displaySimErrorDialog(this);
            }

            selectStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                    try {
                        ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                        selectedState = (String) adapterView.getItemAtPosition(pos);
                        if (!selectedState.equalsIgnoreCase("Select State")) {
                            loadCity(selectedState);
                        } else {
                            selectCityLayout.setVisibility(View.GONE);
                            searchLocationPin.setVisibility(View.GONE);
                            recyclerAtm.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            selectCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                    String selectedCity = (String) adapterView.getItemAtPosition(pos);
                    if (pos != 0) {
                        searchLocationPin.setVisibility(View.VISIBLE);
                        loadAtmList(selectedState, selectedCity);

                    } else {
                        searchLocationPin.setVisibility(View.GONE);
                        recyclerAtm.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDialogOk(int resultCode) {
        if (resultCode == 0) {
            Intent intent = new Intent(getApplicationContext(), LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            method.activityCloseAnimation();
        } else {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

    }


    @SuppressLint("StaticFieldLeak")
    private class LoadStateSpinnerAsyncTask extends AsyncTask<Void, Void, String> {
        String error = "";
        Context ctx;
        String response;
        ProgressDialog pDialog;
        private String errorCode = "";


        public LoadStateSpinnerAsyncTask(Context ctx) {
            this.ctx = ctx;
            atmModelsStateList = new ArrayList<String>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LocateAtmsActivity.this);
            pDialog.setMessage(getResources().getString(R.string.loading_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                String url = TrustURL.LocateAtmUrl("", "") + "?rnd=" + String.valueOf(Math.random());

                if (!url.equals("")) {
                    TrustMethods.LogMessage(TAG, "URL:-" + url);
                    response = HttpClientWrapper.getResponceDirectalyGET(url, AppConstants.getAuth_token());
                    TrustMethods.LogMessage(TAG, "Frm Enquiry details response-->" + response);
                }
                if (response == null || response.equals("")) {
                    error = AppConstants.SERVER_NOT_RESPONDING;
                    return error;
                }
                JSONObject jsonResult = (new JSONObject(response));
                if (jsonResult.has("error")) {
                    error = jsonResult.getString("error");
                    return error;
                }
                String responseCode = jsonResult.has("response_code") ? jsonResult.getString("response_code") : "NA";

//                if (responseCode.equalsIgnoreCase("1")) {
                JSONArray atmsArray = jsonResult.getJSONArray("atms");
                atmModelsList = new ArrayList<>();

                if (atmsArray.length() == 0) {
                    error = AppConstants.NO_RECORDS_FOUND;
                    return error;
                }

                for (int i = 0; i < atmsArray.length(); i++) {
                    JSONObject atmJsonObject = atmsArray.getJSONObject(i);
                    String terminalCode = atmJsonObject.has("ternimal_code") ? atmJsonObject.getString("ternimal_code") : "NA";
                    String state = atmJsonObject.has("state") ? atmJsonObject.getString("state") : "NA";
                    String city = atmJsonObject.has("city") ? atmJsonObject.getString("city") : "NA";
                    String location = atmJsonObject.has("location") ? atmJsonObject.getString("location") : "NA";
                    String pinCode = atmJsonObject.has("pincode") ? atmJsonObject.getString("pincode") : "NA";
                    String latitude = atmJsonObject.has("latitude") ? atmJsonObject.getString("latitude") : "NA";
                    String longitude = atmJsonObject.has("longitude") ? atmJsonObject.getString("longitude") : "NA";
                    String ifscCode = atmJsonObject.has("ifsc_code") ? atmJsonObject.getString("ifsc_code") : "NA";
                    String micrCode = atmJsonObject.has("micr") ? atmJsonObject.getString("micr") : "NA";
                    String contact_no = atmJsonObject.has("contact_no") ? atmJsonObject.getString("contact_no") : "NA";
                    String branch_manager = atmJsonObject.has("branch_manager") ? atmJsonObject.getString("branch_manager") : "NA";
                    String branch_manager_mob = atmJsonObject.has("branch_manager_mob") ? atmJsonObject.getString("branch_manager_mob") : "NA";

                    AtmModel atmModel = new AtmModel();
                    atmModel.setTerminalCode(terminalCode);
                    atmModel.setState(state);
                    atmModel.setCity(city);
                    atmModel.setLocation(location);
                    atmModel.setPincode(pinCode);
                    atmModel.setLatitude(latitude);
                    atmModel.setLongitute(longitude);
                    atmModel.setIfscCode(ifscCode);
                    atmModel.setMicrCode(micrCode);
                    atmModel.setContact_no(contact_no);
                    atmModel.setBranch_manager(branch_manager);
                    atmModel.setBranch_manager_mob(branch_manager_mob);
                    atmModelsList.add(atmModel);
                    atmModelsStateList.add(state);
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
                    if (!TextUtils.isEmpty(errorCode) && TrustMethods.isSessionExpired(errorCode)) {
                        AlertDialogMethod.alertDialogOk(LocateAtmsActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogListener);
                    } else if (TrustMethods.isSessionExpiredWithString(error)) {
                        AlertDialogMethod.alertDialogOk(LocateAtmsActivity.this, getResources().getString(R.string.error_session_expire), "", getResources().getString(R.string.btn_ok), 0, false, alertDialogListener);
                    } else {
                        TrustMethods.showSnackBarMessage(this.error, coordinatorLayout);
                    }
                } else {
                    if (atmModelsStateList != null && atmModelsStateList.size() != 0) {
                        List<String> atmModelsStateList1 = duplicateEntries("State", atmModelsStateList);
                        if (atmModelsStateList1.size() == 1) {
                            selectStateSpinner.setClickable(false);
                            selectStateSpinner.setEnabled(false);
                        } else {
                            atmModelsStateList1.add(0, "Select State");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(LocateAtmsActivity.this,
                                android.R.layout.simple_spinner_item, atmModelsStateList1);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectStateSpinner.setAdapter(adapter);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadCity(String selectedState) {


        atmModelsCityList = new ArrayList<>();
        for (AtmModel atmModel : atmModelsList) {
            if (selectedState.equals(atmModel.getState())) {
                atmModelsCityList.add(atmModel.getCity());
            }
        }
        if (atmModelsCityList != null && atmModelsCityList.size() != 0) {
            selectCityLayout.setVisibility(View.VISIBLE);
            List<String> atmModelsCityList1 = duplicateEntries("City", atmModelsCityList);
            atmModelsCityList1.add(0, "Select City/Taluka");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(LocateAtmsActivity.this,
                    android.R.layout.simple_spinner_item, atmModelsCityList1);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectCitySpinner.setAdapter(adapter);
        }
    }

    private void loadAtmList(String mSelectedState, String mSelectedCity) {
        if (atmModelsList != null && atmModelsList.size() != 0) {
            recyclerAtm.setVisibility(View.VISIBLE);
            floatingAtmMap.hide();

            atmModelsFilterData = new ArrayList<>();

            for (AtmModel atmModel : atmModelsList) {

                if (mSelectedState.equals(atmModel.getState()) && mSelectedCity.equals(atmModel.getCity())) {
                    AtmModel atmModel1 = new AtmModel();
                    atmModel1.setTerminalCode(atmModel.getTerminalCode());
                    atmModel1.setState(atmModel.getState());
                    atmModel1.setCity(atmModel.getCity());
                    atmModel1.setLocation(atmModel.getLocation());
                    atmModel1.setPincode(atmModel.getPincode());
                    atmModel1.setLatitude(atmModel.getLatitude());
                    atmModel1.setLongitute(atmModel.getLongitute());
                    atmModel1.setIfscCode(atmModel.getIfscCode());
                    atmModel1.setMicrCode(atmModel.getMicrCode());
                    atmModelsFilterData.add(atmModel);
                }
            }

            locateAtmAdapter = new LocateAtmAdapter(LocateAtmsActivity.this, atmModelsFilterData);
            recyclerAtm.setAdapter(locateAtmAdapter);
        } else {
            recyclerAtm.setVisibility(View.GONE);
            floatingAtmMap.hide();
        }
    }

    public List<String> duplicateEntries(String value, List<String> listStrings) {
        TreeSet<String> hashSet = new TreeSet<>(listStrings);
        listStrings.clear();
        listStrings.addAll(hashSet);
        return listStrings;
    }

    private void searchFunctionality() {
        searchLocationPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (locateAtmAdapter != null) {
                    locateAtmAdapter.filter(searchLocationPin.getText().toString().toLowerCase(Locale.getDefault()));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        TrustMethods.showBackButtonAlert(LocateAtmsActivity.this);
    }
}
