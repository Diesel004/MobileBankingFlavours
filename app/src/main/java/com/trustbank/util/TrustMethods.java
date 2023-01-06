package com.trustbank.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trustbank.Model.BeneficiaryModal;
import com.trustbank.Model.CheckSimInfoModel;
import com.trustbank.Model.GetUserProfileModal;
import com.trustbank.R;
import com.google.gson.Gson;
import com.trustbank.activity.LockActivity;
import com.trustbank.activity.MenuActivity;
import com.trustbank.activity.SplashScreenActivity;
import com.trustbank.activity.VerifyMobileNumber;
import com.trustbank.interfaces.AlertDialogOkListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.select.Evaluator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.trustbank.util.AppConstants.DISPLAY_NAME;
import static com.trustbank.util.AppConstants.LoginInfo_isHookedDeviceDetectionEnabled;
import static com.trustbank.util.AppConstants.LoginInfo_isLogEnabled;
import static com.trustbank.util.AppConstants.LoginInfo_isRootDetectionEnabled;
import static com.trustbank.util.AppConstants.MOB_NO;
import static com.trustbank.util.AppConstants.SIM_NUMBER;
import static com.trustbank.util.AppConstants.SIM_SUBSCRIPTION_ID;
import static com.trustbank.util.AppConstants.SLOT_INDEX;
import static com.trustbank.util.AppConstants.isAutoReadOTPEnabled;

@SuppressLint({"SimpleDateFormat", "Registered"})
public class TrustMethods extends Activity {

    public static final String PREFS_NAME = "ACCOUNT_LIST";
    public static final String BEN_ACC_LIST = "BEN_ACC_LIST";
    TextView editTextDate;
    public int mYear;
    public int mMonth;
    public int mDay;
    public String months;
    Context mContext;
    Activity activity;
//    public static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "." + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");

    private static String EMAIL_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    public TrustMethods(Context context) {
        this.mContext = context;
        this.activity = (Activity) context;
    }


    public void datePicker(final Context context, TextView editText) {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        editTextDate = editText;

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            mYear = year;
            mMonth = monthOfYear + 1;
            mDay = dayOfMonth;

            switch (mMonth) {
                case 0:
                    months = "Jan";
                    break;
                case 1:
                    months = "Feb";
                    break;
                case 2:
                    months = "Mar";
                    break;
                case 3:
                    months = "Apr";
                    break;
                case 4:
                    months = "May";
                    break;
                case 5:
                    months = "Jun";
                    break;
                case 6:
                    months = "Jul";
                    break;
                case 7:
                    months = "Aug";
                    break;
                case 8:
                    months = "Sep";
                    break;
                case 9:
                    months = "Oct";
                    break;
                case 10:
                    months = "Nov";
                    break;
                case 11:
                    months = "Dec";
                    break;
                default:
                    break;
            }
            if (view.isShown()) {
                String day = String.valueOf(mDay).trim();
                String month = String.valueOf(mMonth).trim();
                if (day.length() == 1) {
                    day = "0" + day;
                }
                if (month.length() == 1) {
                    month = "0" + month;
                }
                editTextDate.setText(day + "/" + month + "/" + mYear);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void datePickerDisableFuturesDate(final Context context, TextView editText) {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        editTextDate = editText;

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            mYear = year;
            mMonth = monthOfYear + 1;
            mDay = dayOfMonth;

            switch (mMonth) {
                case 0:
                    months = "Jan";
                    break;
                case 1:
                    months = "Feb";
                    break;
                case 2:
                    months = "Mar";
                    break;
                case 3:
                    months = "Apr";
                    break;
                case 4:
                    months = "May";
                    break;
                case 5:
                    months = "Jun";
                    break;
                case 6:
                    months = "Jul";
                    break;
                case 7:
                    months = "Aug";
                    break;
                case 8:
                    months = "Sep";
                    break;
                case 9:
                    months = "Oct";
                    break;
                case 10:
                    months = "Nov";
                    break;
                case 11:
                    months = "Dec";
                    break;
                default:
                    break;
            }
            if (view.isShown()) {
                String day = String.valueOf(mDay).trim();
                String month = String.valueOf(mMonth).trim();
                if (day.length() == 1) {
                    day = "0" + day;
                }
                if (month.length() == 1) {
                    month = "0" + month;
                }
                editTextDate.setText(day + "/" + month + "/" + mYear);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public static void message(Context context, String toastMessage) {
        Toast toast = Toast.makeText(context, toastMessage, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static String getMonthYear() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return df.format(c.getTime());
    }

    public void activityOpenAnimation() {
        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    public void activityCloseAnimation() {
        activity.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    public static void showMessage(Context context, String toastMessage) {
        Toast toast = Toast.makeText(context, toastMessage, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showSnackBarMessage(String message, CoordinatorLayout coordinatorLayout) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, 7000);

        snackbar.setActionTextColor(Color.RED);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public static void customSnackbar(Context context, String message, CoordinatorLayout coordinatorLayout, String action) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE).setAction(action, view -> {
        });

        // Changing message text color
        snackbar.setActionTextColor(Color.YELLOW);

        // Changing action button text color
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }


    public static boolean validateEmailAddress(String emailAddress) {
        CharSequence inputStr = emailAddress;
        Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null && inputManager != null) {
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                inputManager.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public static void showSoftKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (activity.getCurrentFocus() != null && inputManager != null) {
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//        }
    }

    //SharedPreferences Method to get & set arrayList.....
    public void saveArrayList(Context context, ArrayList<GetUserProfileModal> list, String key) {
        try {
            SharedPreferences settings;
            SharedPreferences.Editor editor;

            settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            editor = settings.edit();
            Gson gson = new Gson();
            String jsonFavorites = gson.toJson(list);
            editor.putString(key, jsonFavorites);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //SharedPreferences Method to get & set arrayList.....
    public void saveBenArrayList(Context context, ArrayList<BeneficiaryModal> list, String key) {
        try {
            SharedPreferences settings;
            SharedPreferences.Editor editor;

            settings = context.getSharedPreferences(BEN_ACC_LIST, Context.MODE_PRIVATE);
            editor = settings.edit();
            Gson gson = new Gson();
            String jsonFavorites = gson.toJson(list);
            editor.putString(key, jsonFavorites);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clearAccountsArrayList(Context context) {
        try {
            SharedPreferences settings;
            SharedPreferences.Editor editor;

            settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            editor = settings.edit();
            editor.clear();
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public ArrayList<GetUserProfileModal> getArrayList(Context context, String key) {
        SharedPreferences settings;
        List<GetUserProfileModal> favorites;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (settings.contains(key)) {
            String jsonFavorites = settings.getString(key, null);
            Gson gson = new Gson();
            GetUserProfileModal[] favoriteItems = gson.fromJson(jsonFavorites, GetUserProfileModal[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<GetUserProfileModal>(favorites);
        } else return null;
        return (ArrayList<GetUserProfileModal>) favorites;
    }

    //get ben from shared pref
    public ArrayList<BeneficiaryModal> getBenArrayList(Context context, String key) {
        SharedPreferences settings;
        List<BeneficiaryModal> favorites;
        settings = context.getSharedPreferences(BEN_ACC_LIST, Context.MODE_PRIVATE);

        if (settings.contains(key)) {
            String jsonFavorites = settings.getString(key, null);
            Gson gson = new Gson();
            BeneficiaryModal[] favoriteItems = gson.fromJson(jsonFavorites, BeneficiaryModal[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<BeneficiaryModal>(favorites);
        } else return null;
        return (ArrayList<BeneficiaryModal>) favorites;
    }

    public boolean checkLocation(Context context) {
        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public static String getVersionName(Context context) {
        String imeiNo = "";
        try {
            imeiNo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imeiNo;
    }

    public void refreshToken(Context context) {
        Toast.makeText(getApplicationContext(), "Refresh Token Call", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String decodeBase64(String decodeString) {
        byte[] responseByteArray = Base64.decode(decodeString, Base64.DEFAULT);
        return new String(responseByteArray, StandardCharsets.UTF_8);
    }

    //Detect OTP
    public static String getVerificationCode(String message, String senderAddress, String fromActivity) {
        String code = null;
        if (fromActivity.equalsIgnoreCase("verifyMobileNumber")) {
            int index = message.indexOf(AppConstants.REG_CODE_MSG);
            if (index != -1) {
                int start = index + 18;//4
                int length = 6;
                code = message.substring(start, start + length);
                if (code.matches("[0-9]+") && code.length() == 6) {
                    return code;
                } else {
                    int start1 = index + 4;//18
                    int length1 = 6;
                    code = message.substring(start1, start1 + length1);
                    if (code.matches("[0-9]+") && code.length() == 6) {
                        return code;
                    } else {
                        return "NA";
                    }
                }

            }
        }
        return code;
    }


    public boolean isValidImeI(String ImeiNo) {
        boolean check;
        if (!Pattern.matches("^[A-Za-z]{4}0[A-Z0-9a-z]{6}$", ImeiNo)) {
            check = false;
        } else {
            check = true;
        }
        return check;
    }

    public boolean isValidAccNo(String accno, Context context) {
        boolean check = false;
        try {
            if (context.getPackageName().equalsIgnoreCase("com.trustbank.shivajibank") ||
                    context.getPackageName().equalsIgnoreCase("com.trustbank.vmucbbank") ||
                    context.getPackageName().equalsIgnoreCase("com.trustbank.punepeoplesbank") ||
                    context.getPackageName().equalsIgnoreCase("com.trustbank.gondiamahilabank")) {
                if (!Pattern.matches("[0-9a-zA-Z]{9,18}", accno)) {
                    check = false;
                } else {
                    check = true;
                }
            } else {
                if (!Pattern.matches("[0-9a-zA-Z]{9,18}", accno)) {
                    check = false;
                } else {
                    check = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return check;
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void LogMessage(String TAG, String message) {
        if (LoginInfo_isLogEnabled) {
            Log.e(TAG, message);
        }
    }

    public static void systemMessage(String systemMessage) {
        if (LoginInfo_isLogEnabled) {
            System.out.println(systemMessage);
        }
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getDeviceID(Context context) {
        try {
            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String imei = "";
                if (Objects.requireNonNull(telephonyManager).getDeviceId() != null) {
                    imei = telephonyManager.getDeviceId();
                }
                String iccId = getICCID(context);
                String androidId = getAndroidID(context);
                return prepareJsonObjectForDeviceID("imei", imei, iccId, androidId, context);
            } else {
                String iccId = "";
                List<SubscriptionInfo> subsList = null;
                SubscriptionManager subsManager = null;
                subsManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                subsList = Objects.requireNonNull(subsManager).getActiveSubscriptionInfoList();
                iccId = subsList.get(0).getIccId();
                String androidId = getAndroidID(context);
                return prepareJsonObjectForDeviceID("imei", "", iccId, androidId, context);
            }
        } catch (Exception e) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (Objects.requireNonNull(telephonyManager).getDeviceId() != null && !TextUtils.isEmpty(telephonyManager.getDeviceId())) {
                    String iccId = getICCID(context);
                    String androidId = getAndroidID(context);
                    return prepareJsonObjectForDeviceID("imei", telephonyManager.getDeviceId(), iccId, androidId, context);
                } else {
                    try {
                        String iccId = getICCID(context);
                        String androidId = getAndroidID(context);
                        return prepareJsonObjectForDeviceID("imei", "", iccId, androidId, context);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        String androidId = getAndroidID(context);
                        return prepareJsonObjectForDeviceID("imei", "", "", androidId, context);
                    }

                }
            } catch (Exception ex) {
                try {
                    String iccId = getICCID(context);
                    String androidId = getAndroidID(context);
                    return prepareJsonObjectForDeviceID("imei", "", iccId, androidId, context);
                } catch (Exception ex1) {
                    String androidId = getAndroidID(context);
                    return prepareJsonObjectForDeviceID("imei", "", "", androidId, context);
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    public static String getICCID(Context context) {
        String iccId = "";
        try {
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            List<SubscriptionInfo> subsList = null;
            SubscriptionManager subsManager = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                subsManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                if (subsManager != null) {
                    if (subsManager.getActiveSubscriptionInfoList() != null) {
                        subsList = subsManager.getActiveSubscriptionInfoList();
                        if (!TextUtils.isEmpty(subsList.get(0).getIccId())) {
                            iccId = subsList.get(0).getIccId();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
        return iccId;
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String prepareJsonObjectForDeviceID(String type, String imei, String iccId, String androidId, Context context) {
      /*{
            "imei": "",
            "icc_no": "",
            "android_id": ""
        }*/
        try {
            String androidVersion = Build.VERSION.RELEASE;

            String versionName = TrustMethods.getVersionName(context);
            JSONObject jsonObject = new JSONObject();
//            if (type.equals("imei")) {
            jsonObject.put("imei", imei);
            jsonObject.put("icc_no", iccId);
            jsonObject.put("android_id", androidId);
            jsonObject.put("application_version", versionName);
            jsonObject.put("android_version", androidVersion);

            return jsonObject.toString();
         /*   } else if (type.equals("icc_no")) {
                jsonObject.put("imei", "");
                jsonObject.put("icc_no", deviceID);
                jsonObject.put("android_id", "");
                return jsonObject.toString();
            } else {
                return "{}";
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }

    }

    public static boolean isRooted(Context context) {
        if (LoginInfo_isRootDetectionEnabled) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec("su");
                return true;
            } catch (Exception e) {
                return false;
            } finally {
                if (process != null) {
                    try {
                        process.destroy();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            return false;
        }
    }


    public static boolean isHookUpDevice(Context context) {
        boolean isHookuDevice = false;
        if (LoginInfo_isHookedDeviceDetectionEnabled) {
            try {
                PackageManager packageManager = context.getPackageManager();
                List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
                for (ApplicationInfo applicationInfo : applicationInfoList) {
                    if (applicationInfo.packageName.equals("de.robv.android.xposed.installer")) {
                        Log.wtf("HookDetection", "Xposed found on the system.");
                        isHookuDevice = true;
                        return isHookuDevice;

                    } else if (applicationInfo.packageName.equals("com.saurik.substrate")) {
                        Log.wtf("HookDetection", "Substrate found on the system.");
                        isHookuDevice = true;
                        return isHookuDevice;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return isHookuDevice;
            }
        }
        return isHookuDevice;
    }


    @SuppressLint("MissingPermission")
    public static boolean isSimAvailable(Context context) {
        if (isAutoReadOTPEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager sManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                SubscriptionInfo infoSim1 = sManager.getActiveSubscriptionInfoForSimSlotIndex(0);
                SubscriptionInfo infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1);
                if (infoSim1 != null || infoSim2 != null) {
                    return true;
                } else {
                    return false;
                }
            } else {
                //========getting only defualt sim one serial number===============//
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager.getSimSerialNumber() != null) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return true;
        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static boolean isSimDetected(FragmentActivity activity, int sub_id, String mobile_no) {

        if (isAutoReadOTPEnabled) {
            CheckSimInfoModel checkSimInfoModel = new CheckSimInfoModel();
            if (sub_id != -1) {

                checkSimInfoModel.setSIM_SUBSCRIPTION_ID(String.valueOf(sub_id));
                SubscriptionInfo infoSim1;
                SubscriptionInfo infoSim2;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    SubscriptionManager sManager = (SubscriptionManager) activity.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                    infoSim1 = sManager.getActiveSubscriptionInfoForSimSlotIndex(0);
                    infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1);
                    if (infoSim1 != null || infoSim2 != null) {
                        if (infoSim1 != null) {
                            if (Objects.requireNonNull(infoSim1).getSubscriptionId() == sub_id) {
                                checkSimInfoModel.setDisplayName((String) infoSim1.getDisplayName());
                                checkSimInfoModel.setSimSerialNumber(String.valueOf(infoSim1.getIccId()));
                                checkSimInfoModel.setSlot(String.valueOf(infoSim1.getSimSlotIndex()));
                                checkSimInfoModel.setMobileNumber(mobile_no);
                                setSimDetails(activity, checkSimInfoModel);
                                return true;
                            } else if (infoSim2 != null) {
                                if (Objects.requireNonNull(infoSim2).getSubscriptionId() == sub_id) {
                                    checkSimInfoModel.setDisplayName((String) infoSim2.getDisplayName());
                                    checkSimInfoModel.setSimSerialNumber(String.valueOf(infoSim2.getIccId()));
                                    checkSimInfoModel.setSlot(String.valueOf(infoSim2.getSimSlotIndex()));
                                    checkSimInfoModel.setMobileNumber(mobile_no);
                                    setSimDetails(activity, checkSimInfoModel);
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else if (infoSim2 != null) {
                            if (Objects.requireNonNull(infoSim2).getSubscriptionId() == sub_id) {
                                checkSimInfoModel.setDisplayName((String) infoSim2.getDisplayName());
                                checkSimInfoModel.setSimSerialNumber(String.valueOf(infoSim2.getIccId()));
                                checkSimInfoModel.setSlot(String.valueOf(infoSim2.getSimSlotIndex()));
                                checkSimInfoModel.setMobileNumber(mobile_no);
                                setSimDetails(activity, checkSimInfoModel);
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                } else {
                    //========getting only defualt sim one serial number===============//
                    TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                    if (telephonyManager.getSimSerialNumber() != null) {
                        if (Objects.requireNonNull(telephonyManager.getSubscriberId()).equals(String.valueOf(sub_id))) {
                            checkSimInfoModel.setDisplayName(telephonyManager.getSimOperatorName());
                            checkSimInfoModel.setSimSerialNumber(telephonyManager.getSimSerialNumber());
                            checkSimInfoModel.setSlot("0");
                            checkSimInfoModel.setMobileNumber(mobile_no);
                            setSimDetails(activity, checkSimInfoModel);
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }

            }

            return false;
        } else {
            return true;
        }

    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static boolean isSimVerified(FragmentActivity activity) {

        if (isAutoReadOTPEnabled) {
            CheckSimInfoModel checkSimInfoModel = getSimDetails(activity);
            if (!checkSimInfoModel.getSIM_SUBSCRIPTION_ID().equals("-1")) {
                int sub_id = Integer.parseInt(checkSimInfoModel.getSIM_SUBSCRIPTION_ID());
                int slot = Integer.parseInt(checkSimInfoModel.getSlot());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    SubscriptionManager sManager = (SubscriptionManager) activity.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                    SubscriptionInfo infoSim1 = sManager.getActiveSubscriptionInfoForSimSlotIndex(0);
                    SubscriptionInfo infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1);
                    if (infoSim1 != null || infoSim2 != null) {
                        if (infoSim1 != null) {
                            if (Objects.requireNonNull(infoSim1).getSubscriptionId() == sub_id && Objects.requireNonNull(infoSim1).getSimSlotIndex() == slot) {
                                if (checkSimInfoModel.getSimSerialNumber().equals(String.valueOf(infoSim1.getIccId()))) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else if (infoSim2 != null) {
                                if (Objects.requireNonNull(infoSim2).getSubscriptionId() == sub_id && Objects.requireNonNull(infoSim2).getSimSlotIndex() == slot) {
                                    if (checkSimInfoModel.getSimSerialNumber().equals(String.valueOf(infoSim2.getIccId()))) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else if (infoSim2 != null) {
                            if (Objects.requireNonNull(infoSim2).getSubscriptionId() == sub_id && Objects.requireNonNull(infoSim2).getSimSlotIndex() == slot) {
                                if (checkSimInfoModel.getSimSerialNumber().equals(String.valueOf(infoSim2.getIccId()))) {
                                    return true;
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    //========getting only defualt sim one serial number===============//
                    TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                    if (telephonyManager.getSimSerialNumber() != null) {
                        if (Objects.requireNonNull(telephonyManager.getSubscriberId()).equals(String.valueOf(sub_id)) && "0".equals(String.valueOf(slot))) {
                            if (checkSimInfoModel.getSimSerialNumber().equals(String.valueOf(telephonyManager.getSimSerialNumber()))) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public static void displaySimErrorDialog(FragmentActivity activity) {

        if (!isSimAvailable(activity.getApplicationContext())) {
            AlertDialogMethod.alertDialogOk(activity, "NO SIM Card", "No " +
                            "Sim Card Not Detected. Please insert the sim in slot and try again",
                    activity.getResources().getString(R.string.btn_ok), 1, false, new AlertDialogOkListener() {
                        @Override
                        public void onDialogOk(int resultCode) {
                            if (resultCode == 1) {
                                System.exit(0);
                            }
                        }
                    });
        } else if (!TrustMethods.isSimVerified(activity)) {
            CheckSimInfoModel checkSimInfoModel = TrustMethods.getSimDetails(activity);
            AlertDialogMethod.alertDialogOk(activity, "SIM not detected for mobile number " + checkSimInfoModel.getMobileNumber(), "" +
                            "Click ok to register with new number",
                    activity.getResources().getString(R.string.btn_ok), 0, false, new AlertDialogOkListener() {
                        @Override
                        public void onDialogOk(int resultCode) {
                            if (resultCode == 0) {
                                Intent intent = new Intent(activity, VerifyMobileNumber.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                activity.startActivity(intent);
                            }
                        }
                    });
        }
    }

    public static void setSimDetails(FragmentActivity activity, CheckSimInfoModel checkSimInfoModel) {
        SharePreferenceUtils sharePreferenceUtils = new SharePreferenceUtils(activity.getApplicationContext());
        sharePreferenceUtils.putString(DISPLAY_NAME, checkSimInfoModel.getDisplayName());
        sharePreferenceUtils.putString(SIM_NUMBER, checkSimInfoModel.getSimSerialNumber());
        sharePreferenceUtils.putString(SLOT_INDEX, checkSimInfoModel.getSlot());
        sharePreferenceUtils.putInteger(SIM_SUBSCRIPTION_ID, Integer.parseInt(checkSimInfoModel.getSIM_SUBSCRIPTION_ID()));
        sharePreferenceUtils.putString(MOB_NO, checkSimInfoModel.getMobileNumber());
    }

    public static CheckSimInfoModel getSimDetails(FragmentActivity activity) {
        SharePreferenceUtils sharePreferenceUtils = new SharePreferenceUtils(activity.getApplicationContext());
        CheckSimInfoModel checkSimInfoModel = new CheckSimInfoModel();
        checkSimInfoModel.setDisplayName(sharePreferenceUtils.getString(DISPLAY_NAME));
        checkSimInfoModel.setSimSerialNumber(sharePreferenceUtils.getString(SIM_NUMBER));
        checkSimInfoModel.setSlot(sharePreferenceUtils.getString(SLOT_INDEX));
        checkSimInfoModel.setSIM_SUBSCRIPTION_ID(String.valueOf(sharePreferenceUtils.getInt(SIM_SUBSCRIPTION_ID, -1)));
        checkSimInfoModel.setMobileNumber(sharePreferenceUtils.getString(MOB_NO));
        return checkSimInfoModel;
    }

    public static String formatDate(String date, String initDateFormat, String endDateFormat) {

        try {
            Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
            assert initDate != null;
            return formatter.format(initDate);
        } catch (ParseException e) {
            return e.getMessage();
        }
    }


    public static String subString(String value, String concateWith) {
        return value.substring(0, value.length() - concateWith.length()) + concateWith;
    }

    public static boolean isSessionExpired(String errorMsg) {
        try {
            return !TextUtils.isEmpty(errorMsg) && (errorMsg.equalsIgnoreCase("9004") || errorMsg.equalsIgnoreCase("9006") || errorMsg.equalsIgnoreCase("9003"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isLogoutSessionExpired(String errorMsg) {
        try {
            return !TextUtils.isEmpty(errorMsg) && (errorMsg.equalsIgnoreCase("9004") || errorMsg.equalsIgnoreCase("9006") || errorMsg.equalsIgnoreCase("9003") || errorMsg.equalsIgnoreCase("9002"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isSessionExpiredWithString(String errorMsg) {

        try {
            return errorMsg.equalsIgnoreCase("Invalid Auth Token.") || errorMsg.equalsIgnoreCase("Auth Token Expired.") || errorMsg.equalsIgnoreCase("Old Token Expired.");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean isFromDateGreaterThanToDate(String strFromDate, String strToDate) {
        boolean isToDateGreater = false;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date fromDate = formatter.parse(strFromDate);
            Date toDate = formatter.parse(strToDate);
            if (fromDate != null && toDate != null) {
                if (toDate.compareTo(fromDate) < 0) {
                    System.out.println("fromDate is Greater than my toDate");
                    isToDateGreater = true;
                }
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
            isToDateGreater = false;
        }
        return isToDateGreater;
    }

    public static void showBackButtonAlert(Context context) {
        try {
            AlertDialogMethod.alertDialogOk(context, "Info", context.getResources().getString(R.string.msg_back_button), context.getResources().getString(R.string.btn_ok),
                    3, false, resultCode -> {
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAccountTypeValid(String acctType) {
        return acctType.equalsIgnoreCase("78") || acctType.equalsIgnoreCase("79")
                || acctType.equalsIgnoreCase("82");
    }

    public static boolean isAccountTypeValidAjara(String acctType) {
        return acctType.equalsIgnoreCase("78") || acctType.equalsIgnoreCase("79");
    }

    public static boolean isAccountTypeValidRDLN(String acctType) {
        return acctType.equalsIgnoreCase("78") || acctType.equalsIgnoreCase("79")
                || acctType.equalsIgnoreCase("82") || acctType.equalsIgnoreCase("77") ||
                acctType.equalsIgnoreCase("81");
    }

    public static boolean isAccountTypeValidRD(String acctType) {
        //Shivaji bank requirement allow benefiecry account 81 for transaction in self and within.
        return acctType.equalsIgnoreCase("78") || acctType.equalsIgnoreCase("79")
                || acctType.equalsIgnoreCase("82") || acctType.equalsIgnoreCase("81");
    }

    public static boolean isAccountTypeIsImpsRegValid(String acctType, String impsReg) {
        return (acctType.equalsIgnoreCase("78") || acctType.equalsIgnoreCase("79")
                || acctType.equalsIgnoreCase("82")) && impsReg.equalsIgnoreCase("1");

    }


    public static boolean isAccountTypeQRValid(String acctType) {
        return (acctType.equalsIgnoreCase("78") || acctType.equalsIgnoreCase("79")
                || acctType.equalsIgnoreCase("82") || acctType.equalsIgnoreCase("173"));

    }

    public static boolean isAccountTypeIsImpsRegValidAjara(String acctType, String impsReg) {
        return (acctType.equalsIgnoreCase("78") || acctType.equalsIgnoreCase("79"))
                && impsReg.equalsIgnoreCase("1");
    }

    public static String getIp(String value) {
        String[] ip = AppConstants.IP.split("//");
        String[] ip1 = ip[1].split(":");
        return ip1[0];
    }

    public static File createPdfFolder(Context context) {
        File f = null;
        try {
            String appName = context.getResources().getString(R.string.app_name);
            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + appName + File.separator + "TermsAndconditions";
            f = new File(rootPath);
            if (!f.exists()) {
                f.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    public static String getValidAccountNo(String selectedAccNo) {
        if (selectedAccNo.contains("-")) {
            String[] accounts = selectedAccNo.split("-");
            return accounts[0].trim();
        } else {
            return selectedAccNo.trim();
        }
    }

    public static String getCurrentMonthFirstDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }

    public static int getColorPrimary(Activity activity) {
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.textColorPrimary, typedValue, true);
        return typedValue.data;
    }

    public static boolean isAmoutLessThanZero(String amount) {
        double amt = Double.parseDouble(amount);
        return amt < 1;
    }

    public static String getValueCommaSeparated(String value) {
        try {
            if (value.equalsIgnoreCase("0.00") || value.equalsIgnoreCase("0.0")) {
                return value;
            }
            double doubleValue = Double.parseDouble(value);
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.00");
            return formatter.format(doubleValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String trimWithPrefixCommsepareted(String amount) {
        try {
            if (amount.contains("Rs.")) {
                String[] amountArray = amount.split(" ");
                return "Rs. " + getValueCommaSeparated(amountArray[1]);
            } else if (amount.contains("Cr")) {
                String[] amountArray = amount.split(" ");
                return getValueCommaSeparated(amountArray[0]) + " Cr";
            } else if (amount.contains("Dr")) {
                String[] amountArray = amount.split(" ");
                return getValueCommaSeparated(amountArray[0]) + " Dr";
            } else {
                return amount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amount;

    }

    public static void setEditTextMaxLength(int length, EditText editText) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        editText.setFilters(filterArray);
    }

    public static void naviagteToSplashScreen(Activity activity) {
        Intent intent = new Intent(activity, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public static String convertdmyintoymd(String date) {
        try {
            Date initDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(initDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }


    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getDeviceBilPayID(Context context) {
        try {
            if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String imei = "";
                if (Objects.requireNonNull(telephonyManager).getDeviceId() != null) {
                    imei = telephonyManager.getDeviceId();
                }
                String iccId = getICCID(context);
                String androidId = getAndroidID(context);
                return prepareJsonObjectForDeviceIDBillPay("imei", imei, iccId, androidId, context);
            } else {
                String iccId = "";
                List<SubscriptionInfo> subsList = null;
                SubscriptionManager subsManager = null;
                subsManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                subsList = Objects.requireNonNull(subsManager).getActiveSubscriptionInfoList();
                iccId = subsList.get(0).getIccId();
                String androidId = getAndroidID(context);
                return prepareJsonObjectForDeviceIDBillPay("imei", "", iccId, androidId, context);
            }
        } catch (Exception e) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Objects.requireNonNull(telephonyManager).getDeviceId() != null && !TextUtils.isEmpty(telephonyManager.getDeviceId())) {
                String iccId = getICCID(context);
                String androidId = getAndroidID(context);
                return prepareJsonObjectForDeviceIDBillPay("imei", telephonyManager.getDeviceId(), iccId, androidId, context);
            } else {
                try {
                    String iccId = getICCID(context);
                    String androidId = getAndroidID(context);
                    return prepareJsonObjectForDeviceIDBillPay("imei", "", iccId, androidId, context);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    String androidId = getAndroidID(context);
                    return prepareJsonObjectForDeviceIDBillPay("imei", "", "", androidId, context);
                }

            }
        }
    }

    public static String prepareJsonObjectForDeviceIDBillPay(String type, String imei, String iccId, String androidId, Context context) {
      /*{
            "imei": "",
            "icc_no": "",
            "android_id": ""
        }*/
        try {
            String androidVersion = Build.VERSION.RELEASE;

            String versionName = TrustMethods.getVersionName(context);
            JSONObject jsonObject = new JSONObject();
//            if (type.equals("imei")) {
            if (!TextUtils.isEmpty(imei)) {
                jsonObject.put("IMEI", imei);
            } else if (!TextUtils.isEmpty(iccId)) {
                jsonObject.put("IMEI", iccId);
            } else if (!TextUtils.isEmpty(androidId)) {
                jsonObject.put("IMEI", androidId);
            }

          /*  jsonObject.put("icc_no", iccId);
            jsonObject.put("android_id", androidId);*/
            jsonObject.put("application_version", versionName);
            jsonObject.put("android_version", androidVersion);

            return jsonObject.toString();
         /*   } else if (type.equals("icc_no")) {
                jsonObject.put("imei", "");
                jsonObject.put("icc_no", deviceID);
                jsonObject.put("android_id", "");
                return jsonObject.toString();
            } else {
                return "{}";
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }

    }

    public static boolean validateUPI(String upi) {
        final Pattern VALID_UPI_ADDRESS_REGEX = Pattern.compile("^(.+)@(.+)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_UPI_ADDRESS_REGEX.matcher(upi);
        return matcher.find();
    }


    public static String getFrequency(String frequncyId) {

        String frequncy = "";
        switch (frequncyId) {
            case "71":
                frequncy = "Monthly";
                break;

            case "72":
                frequncy = "Quarterly";
                break;

            case "73":
                frequncy = "Half Yearly";
                break;

            case "74":
                frequncy = "Yearly";
                break;

            case "240":
                frequncy = "Bi Monthly";
                break;

            case "241":
                frequncy = "Adhoc";
                break;

            case "242":
                frequncy = "Intra Day";
                break;

            case "243":
                frequncy = "Daily";
                break;

            case "244":
                frequncy = "Weekly";

                break;

            case "245":
                frequncy = "Semi Annually";
                break;
        }
        return frequncy;

    }


}