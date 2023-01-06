package com.trustbank.util;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.trustbank.BuildConfig;
import com.trustbank.R;
import com.trustbank.activity.FrmServer;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AppConstants {

    public static boolean isInterceptorEnabled = false;
    public static boolean isReadDeviceIDFeatureEnabled = false;
    public static boolean isAutoReadOTPEnabled = false;
    public static boolean LoginInfo_isSetServerEnabled = false;
    public static boolean LoginInfo_isLogEnabled = false;
    public static boolean LoginInfo_isRootDetectionEnabled = false;
    public static boolean LoginInfo_isHookedDeviceDetectionEnabled = false;

    public static boolean isAutoReadOTPReadOnlyRegisterMobile = false;

    public static String IP = "";
    public static String INSTITUTION_ID = "";
    public static String API_KEY = "";

    //Dynamic Menu Flags
    private static String mnu_accounts;
    private static String mnu_fundtransfer;
    private static String mnu_locate_atms;
    private static String mnu_locate_branch;
    private static String mnu_contact_us;
    private static String mnu_about_us;
    private static String mnu_accounts_menu_accountdetails;
    private static String mnu_accounts_menu_balenquiry;
    private static String mnu_accounts_menu_ministatemnt;
    private static String mnu_accounts_menu_showmmid;
    private static String mnu_accounts_menu_last5imps;
    private static String Mnu_fundtransfer_upi_get_qr;

    private static String mnu_accounts_menu_balenquiry_cbs;
    private static String mnu_accounts_menu_ministatemnt_cbs;
    private static String mnu_accounts_menu_showmmid_cbs;
    private static String mnu_accounts_menu_last5imps_cbs;

    private static String mnu_accounts_menu_neftenquiry;
    private static String mnu_fundtransfer_ownbank;
    private static String mnu_fundtransfer_impstoaccount;
    private static String mnu_fundtransfer_nefttoaccount;
    private static String mnu_fundtransfer_impstomobile;
    private static String mnu_fundtransfer_upi;
    private static String mnu_fundtransfer_mngbenefeciaries;
    private static String mnu_self_transfer_to_account;
    private static String mnu_check_imps_transaction_status;
    private static String mnu_neft_trans_switch_transaction;
    private static String mnu_bill_pay;
    private static String mnu_fundtransfer_upi_create_qr;
    private static String mnu_fundtransfer_upi_get_qr;



    private static String mnu_bill_pay_complaint_management;
    private static String mnu_bill_pay_register_complaints;
    private static String mnu_bill_pay_track_complaints;


    private static String mnu_faq;
    private static String mnu_checkbook_request;
    private static String mnu_account_statement;

    private static String mnu_setting;
    private static String mnu_Change_MPin;
    private static String mnu_Change_TPin;
    private static String mnu_Reset_TPin;
    private static String mnu_Limit_Transaction;

    private static String mnu_beneficiary_own_bank;
    private static String mnu_beneficiary_imps_neft_account_bank;
    private static String mnu_beneficiary_imps_mobile_bank;

    private static String stopChequebookStatus;
    private static String inqueriChquebookStatus;

    private static String mnu_pps_request;
    private static String mnu_pps_request_enquiry;
    private static String mnu_block_debit_card;

    private static String checkImpsTransStatusFundTransfer;
    private static String mnu_verify_beneficiary_name;

    private static String mnu_fundtransfer_upi_collect_money; //TODO need to update on procedure.
    private static String mnu_block_debit_card_switch; //TODO Need to update on proc.
    private static String mnu_debit_card_pin_generation;
    private static String mnu_mandate_cancel;
    //Menu section end

    public static String playStoreDemoUserMobile;
    public static String playStoreDemoPasswordClientid;

    public static final String SMS_ORIGIN = "ANHIVE";

    public static final String OTP_MESSAGE = "OTP_MESSAGE";
    public static final String SUB_ID = "SUB_ID";
    public static final String DISPLAY_NAME = "DISPLAY_NAME";
    public static final String SIM_NUMBER = "SIM_NUMBER";
    public static final String SLOT_INDEX = "SLOT_INDEX";
    public static final String SIM_SUBSCRIPTION_ID = "SIM_SUBSCRIPTION_ID";
    public static final String MOB_NO = "MOB_NO";

    // special character to prefix the otp. Make sure this character appears only once in the sms.
    public static final String OTP_DELIMITER = ":";
    public static final String OTP_MSG = "OTP";
    public static final String REG_CODE_MSG = "Registration code";

    public static final String SERVER_NOT_RESPONDING = "Server not responding,Your internet connectivity is poor, please check connection and try again.";
    public static final String NO_RECORDS_FOUND = "No records found";
    public static final String MyPREFERENCES = "myprefrences";
    public static String USERMOBILENUMBER;
    public static String USEREMAILADDRESS;
    public static String USERNAME;
    public static String CLIENTID;
    public static String ANOTHERCLIENTID;
    public static String ANOTHERCUSTOMERNAME;
    public static String SERVER_OTP = "";
    public static String SERVER_ERROR;

    public static boolean IS_CHECK_ACCESS_BROKEN;

    public static final String PREFERENCES = "preferences";
    public static final String STORE_IP = "storeIp";
    public static final String PID = "pid";

    public static final String AUTH_TOKEN = "AUTH_TOKEN";

    public static final String FILE_PATH = "/MBank";
    public static final String STATEMENTS = FILE_PATH + "/Statements/";
    public static final String PROFILE_IMAGE = FILE_PATH + "/ProfileImg/";
    public static final String NO_MEDIA = "/.nomedia";
    public static final String STATEMENT_SHEET = "Statement sheet";
    public static final String STATMENT_PDF_SHEET = STATEMENTS + "/PDF FILE/";
    public static final String STATMENT_EXCEL_SHEET = STATEMENTS + "/EXCEL FILE/";
    public static final String EXCEL_STATEMENT_FILE_NAME = "AccountStatement.xls";
    public static final String PDF_STATEMENT_FILE_NAME = "/AccountStatement.pdf";
    public static final String IMAGE_FILE_AUTHORITIES = "com.trustbank.fileprovider";
    public static final String ACC_PREFERENCES = "addNewAccPref";

    public static final int PICK_FROM_CAMERA_Photo = 1;
    public static final int PICK_FROM_FILE_GALLERY = 2;
    public static final String PROFILE_NAME = "Img_Profile.jpeg";
    public static final String THEME_CHANGE = "select_theme";

    public static final String SIM_ERROR_MSG = "SIM_ERROR_MSG";
    public static final String SIM_NOT_EXISTS = "SIM_NOT_EXISTS";
    public static final String SIM_MISMATCH = "SIM_MISMATCH";
    public static final String DOUBTFULL_TRANSACTION = "DOUBTFULL_TRANSACTION";
    public static final String DOUBTFULL = "DOUBTFUL";

    public static String profileID = "";
    public static String auth_token;
    public static String tpin;
    public static String securityCodeHint;
    public static int autoReadOtpTimeout;
    public static String play_store_validate;

    // public static int timeOut = 150000;  //150 second.
    public static int timeOut = 1500000;  //150 second.
//    public static int timeOut = 1500;  //150 second.


    byte[] sessionKey;


    public static String getMnu_fundtransfer_upi_create_qr() {
        return mnu_fundtransfer_upi_create_qr;
    }

    public static void setMnu_fundtransfer_upi_create_qr(String mnu_fundtransfer_upi_create_qr) {
        AppConstants.mnu_fundtransfer_upi_create_qr = mnu_fundtransfer_upi_create_qr;
    }

    public static String getMnu_fundtransfer_upi_get_qr() {
        return mnu_fundtransfer_upi_get_qr;
    }

    public static void setMnu_fundtransfer_upi_get_qr(String mnu_fundtransfer_upi_get_qr) {
        AppConstants.mnu_fundtransfer_upi_get_qr = mnu_fundtransfer_upi_get_qr;
    }



    public static String getMnu_mandate_cancel() {
        return mnu_mandate_cancel;
    }

    public static void setMnu_mandate_cancel(String mnu_mandate_cancel) {
        AppConstants.mnu_mandate_cancel = mnu_mandate_cancel;
    }

    public static String getMnu_block_debit_card_switch() {
        return mnu_block_debit_card_switch;
    }

    public static void setMnu_block_debit_card_switch(String mnu_block_debit_card_switch) {
        AppConstants.mnu_block_debit_card_switch = mnu_block_debit_card_switch;
    }

    public static String getMnu_debit_card_pin_generation() {
        return mnu_debit_card_pin_generation;
    }

    public static void setMnu_debit_card_pin_generation(String mnu_debit_card_pin_generation) {
        AppConstants.mnu_debit_card_pin_generation = mnu_debit_card_pin_generation;
    }

    public static String getMnu_fundtransfer_upi_collect_money() {
        return mnu_fundtransfer_upi_collect_money;
    }

    public static void setMnu_fundtransfer_upi_collect_money(String mnu_fundtransfer_upi_collect_money) {
        AppConstants.mnu_fundtransfer_upi_collect_money = mnu_fundtransfer_upi_collect_money;
    }

    public static String getCheckImpsTransStatusFundTransfer() {
        return checkImpsTransStatusFundTransfer;
    }

    public static void setCheckImpsTransStatusFundTransfer(String checkImpsTransStatusFundTransfer) {
        AppConstants.checkImpsTransStatusFundTransfer = checkImpsTransStatusFundTransfer;
    }

    public static String getPlayStoreDemoUserMobile() {
        return playStoreDemoUserMobile;
    }

    public static void setPlayStoreDemoUserMobile(String playStoreDemoUserMobile) {
        AppConstants.playStoreDemoUserMobile = playStoreDemoUserMobile;
    }

    public static String getPlayStoreDemoPasswordClientid() {
        return playStoreDemoPasswordClientid;
    }

    public static void setPlayStoreDemoPasswordClientid(String playStoreDemoPasswordClientid) {
        AppConstants.playStoreDemoPasswordClientid = playStoreDemoPasswordClientid;
    }

    public static String getMnu_fundtransfer_upi() {
        return mnu_fundtransfer_upi;
    }

    public static void setMnu_fundtransfer_upi(String mnu_fundtransfer_upi) {
        AppConstants.mnu_fundtransfer_upi = mnu_fundtransfer_upi;
    }

    public static String getMnu_bill_pay() {
        return mnu_bill_pay;
    }

    public static void setMnu_bill_pay(String mnu_bill_pay) {
        AppConstants.mnu_bill_pay = mnu_bill_pay;
    }

    public static String getMnu_neft_trans_switch_transaction() {
        return mnu_neft_trans_switch_transaction;
    }

    public static void setMnu_neft_trans_switch_transaction(String mnu_neft_trans_switch_transaction) {
        AppConstants.mnu_neft_trans_switch_transaction = mnu_neft_trans_switch_transaction;
    }

    public static String getPlay_store_validate() {
        return play_store_validate;
    }

    public static void setPlay_store_validate(String play_store_validate) {
        AppConstants.play_store_validate = play_store_validate;
    }

    public static int getAutoReadOtpTimeout() {
        return autoReadOtpTimeout;
    }

    public static void setAutoReadOtpTimeout(int autoReadOtpTimeout) {
        AppConstants.autoReadOtpTimeout = autoReadOtpTimeout;
    }

    public static String getMnu_block_debit_card() {
        return mnu_block_debit_card;
    }

    public static void setMnu_block_debit_card(String mnu_block_debit_card) {
        AppConstants.mnu_block_debit_card = mnu_block_debit_card;
    }

    public static String getMnu_pps_request() {
        return mnu_pps_request;
    }

    public static void setMnu_pps_request(String mnu_pps_request) {
        AppConstants.mnu_pps_request = mnu_pps_request;
    }

    public static String getMnu_pps_request_enquiry() {
        return mnu_pps_request_enquiry;
    }

    public static void setMnu_pps_request_enquiry(String mnu_pps_request_enquiry) {
        AppConstants.mnu_pps_request_enquiry = mnu_pps_request_enquiry;
    }

    public static String getANOTHERCUSTOMERNAME() {
        return ANOTHERCUSTOMERNAME;
    }

    public static void setANOTHERCUSTOMERNAME(String ANOTHERCUSTOMERNAME) {
        AppConstants.ANOTHERCUSTOMERNAME = ANOTHERCUSTOMERNAME;
    }

    public static String getANOTHERCLIENTID() {
        return ANOTHERCLIENTID;
    }

    public static void setANOTHERCLIENTID(String ANOTHERCLIENTID) {
        AppConstants.ANOTHERCLIENTID = ANOTHERCLIENTID;
    }

    public static String getMnu_beneficiary_own_bank() {
        return mnu_beneficiary_own_bank;
    }

    public static void setMnu_beneficiary_own_bank(String mnu_beneficiary_own_bank) {
        AppConstants.mnu_beneficiary_own_bank = mnu_beneficiary_own_bank;
    }

    public static String getMnu_beneficiary_imps_neft_account_bank() {
        return mnu_beneficiary_imps_neft_account_bank;
    }

    public static void setMnu_beneficiary_imps_neft_account_bank(String mnu_beneficiary_imps_neft_account_bank) {
        AppConstants.mnu_beneficiary_imps_neft_account_bank = mnu_beneficiary_imps_neft_account_bank;
    }

    public static String getMnu_beneficiary_imps_mobile_bank() {
        return mnu_beneficiary_imps_mobile_bank;
    }

    public static void setMnu_beneficiary_imps_mobile_bank(String mnu_beneficiary_imps_mobile_bank) {
        AppConstants.mnu_beneficiary_imps_mobile_bank = mnu_beneficiary_imps_mobile_bank;
    }

    public static String getMnu_accounts_menu_balenquiry_cbs() {
        return mnu_accounts_menu_balenquiry_cbs;
    }

    public static void setMnu_accounts_menu_balenquiry_cbs(String mnu_accounts_menu_balenquiry_cbs) {
        AppConstants.mnu_accounts_menu_balenquiry_cbs = mnu_accounts_menu_balenquiry_cbs;
    }

    public static String getMnu_accounts_menu_ministatemnt_cbs() {
        return mnu_accounts_menu_ministatemnt_cbs;
    }

    public static void setMnu_accounts_menu_ministatemnt_cbs(String mnu_accounts_menu_ministatemnt_cbs) {
        AppConstants.mnu_accounts_menu_ministatemnt_cbs = mnu_accounts_menu_ministatemnt_cbs;
    }

    public static String getMnu_accounts_menu_showmmid_cbs() {
        return mnu_accounts_menu_showmmid_cbs;
    }

    public static void setMnu_accounts_menu_showmmid_cbs(String mnu_accounts_menu_showmmid_cbs) {
        AppConstants.mnu_accounts_menu_showmmid_cbs = mnu_accounts_menu_showmmid_cbs;
    }

    public static String getMnu_accounts_menu_last5imps_cbs() {
        return mnu_accounts_menu_last5imps_cbs;
    }

    public static void setMnu_accounts_menu_last5imps_cbs(String mnu_accounts_menu_last5imps_cbs) {
        AppConstants.mnu_accounts_menu_last5imps_cbs = mnu_accounts_menu_last5imps_cbs;
    }

    public static String getSecurityCodeHint() {
        return securityCodeHint;
    }

    public static void setSecurityCodeHint(String securityCodeHint) {
        AppConstants.securityCodeHint = securityCodeHint;
    }

    public static String getInqueriChquebookStatus() {
        return inqueriChquebookStatus;
    }

    public static void setInqueriChquebookStatus(String inqueriChquebookStatus) {
        AppConstants.inqueriChquebookStatus = inqueriChquebookStatus;
    }

    public static String getStopChequebookStatus() {
        return stopChequebookStatus;
    }

    public static void setStopChequebookStatus(String stopChequebookStatus) {
        AppConstants.stopChequebookStatus = stopChequebookStatus;
    }

    public static String getCLIENTID() {
        return CLIENTID;
    }

    public static void setCLIENTID(String CLIENTID) {
        AppConstants.CLIENTID = CLIENTID;
    }

    public static String getMnu_Change_MPin() {
        return mnu_Change_MPin;
    }

    public static void setMnu_Change_MPin(String mnu_Change_MPin) {
        AppConstants.mnu_Change_MPin = mnu_Change_MPin;
    }

    public static String getMnu_Change_TPin() {
        return mnu_Change_TPin;
    }

    public static void setMnu_Change_TPin(String mnu_Change_TPin) {
        AppConstants.mnu_Change_TPin = mnu_Change_TPin;
    }

    public static String getMnu_Reset_TPin() {
        return mnu_Reset_TPin;
    }

    public static void setMnu_Reset_TPin(String mnu_Reset_TPin) {
        AppConstants.mnu_Reset_TPin = mnu_Reset_TPin;
    }

    public static String getMnu_Limit_Transaction() {
        return mnu_Limit_Transaction;
    }

    public static void setMnu_Limit_Transaction(String mnu_Limit_Transaction) {
        AppConstants.mnu_Limit_Transaction = mnu_Limit_Transaction;
    }

    public static String getMnu_setting() {
        return mnu_setting;
    }

    public static void setMnu_setting(String mnu_setting) {
        AppConstants.mnu_setting = mnu_setting;
    }

    public static String getMnu_self_transfer_to_account() {
        return mnu_self_transfer_to_account;
    }

    public static void setMnu_self_transfer_to_account(String mnu_self_transfer_to_account) {
        AppConstants.mnu_self_transfer_to_account = mnu_self_transfer_to_account;
    }

    public static String getMnu_account_statement() {
        return mnu_account_statement;
    }

    public static void setMnu_account_statement(String mnu_account_statement) {
        AppConstants.mnu_account_statement = mnu_account_statement;
    }

    public static String getProfileID() {
        return profileID;
    }

    public static void setProfileID(String profileID) {
        AppConstants.profileID = profileID;
    }

    public static String getAuth_token() {
        SharePreferenceUtils sharePreferenceUtils = new SharePreferenceUtils(MBank.getInstance().getApplicationContext());
        return sharePreferenceUtils.getString(AppConstants.AUTH_TOKEN);
        //  return auth_token;
    }


    public static String getMnu_checkbook_request() {
        return mnu_checkbook_request;
    }

    public static void setMnu_checkbook_request(String mnu_checkbook_request) {
        AppConstants.mnu_checkbook_request = mnu_checkbook_request;
    }

    public static String getMnu_faq() {
        return mnu_faq;
    }


    public static void setMnu_faq(String mnu_faq) {
        AppConstants.mnu_faq = mnu_faq;
    }

    public static void setAuth_token(String auth_token) {

        AppConstants.auth_token = auth_token;
    }

    public static void setTPin(String TPin) {
        AppConstants.tpin = TPin;
    }

    public static String getTPIn() {
        return tpin;
    }

    public static String getUSERMOBILENUMBER() {
        return USERMOBILENUMBER;
    }

    public static void setUSERMOBILENUMBER(String USERMOBILENUMBER) {
        AppConstants.USERMOBILENUMBER = USERMOBILENUMBER;
    }

    public static String getUSEREMAILADDRESS() {
        return USEREMAILADDRESS;
    }

    public static void setUSEREMAILADDRESS(String USEREMAILADDRESS) {
        AppConstants.USEREMAILADDRESS = USEREMAILADDRESS;
    }

    public static String getUSERNAME() {
        return USERNAME;
    }

    public static void setUSERNAME(String USERNAME) {
        AppConstants.USERNAME = USERNAME;
    }

    public static String getServerOtp() {
        return SERVER_OTP;
    }

    public static void setServerOtp(String serverOtp) {
        SERVER_OTP = serverOtp;
    }

    public static String getServerError() {
        return SERVER_ERROR;
    }

    public static void setServerError(String serverError) {
        SERVER_ERROR = serverError;
    }

    //****************Menu Flags Getters & Setters***************************//

    public static String getMnu_accounts() {
        return mnu_accounts;
    }

    public static void setMnu_accounts(String mnu_accounts) {
        AppConstants.mnu_accounts = mnu_accounts;
    }

    public static String getMnu_fundtransfer() {
        return mnu_fundtransfer;
    }

    public static void setMnu_fundtransfer(String mnu_fundtransfer) {
        AppConstants.mnu_fundtransfer = mnu_fundtransfer;
    }

    public static String getMnu_locate_atms() {
        return mnu_locate_atms;
    }

    public static void setMnu_locate_atms(String mnu_locate_atms) {
        AppConstants.mnu_locate_atms = mnu_locate_atms;
    }

    public static String getMnu_locate_branch() {
        return mnu_locate_branch;
    }

    public static void setMnu_locate_branch(String mnu_locate_branch) {
        AppConstants.mnu_locate_branch = mnu_locate_branch;
    }

    public static String getMnu_contact_us() {
        return mnu_contact_us;
    }

    public static void setMnu_contact_us(String mnu_contact_us) {
        AppConstants.mnu_contact_us = mnu_contact_us;
    }

    public static String getMnu_about_us() {
        return mnu_about_us;
    }

    public static void setMnu_about_us(String mnu_about_us) {
        AppConstants.mnu_about_us = mnu_about_us;
    }

    public static String getMnu_accounts_menu_accountdetails() {
        return mnu_accounts_menu_accountdetails;
    }

    public static void setMnu_accounts_menu_accountdetails(String mnu_accounts_menu_accountdetails) {
        AppConstants.mnu_accounts_menu_accountdetails = mnu_accounts_menu_accountdetails;
    }

    public static String getMnu_accounts_menu_balenquiry() {
        return mnu_accounts_menu_balenquiry;
    }

    public static void setMnu_accounts_menu_balenquiry(String mnu_accounts_menu_balenquiry) {
        AppConstants.mnu_accounts_menu_balenquiry = mnu_accounts_menu_balenquiry;
    }

    public static String getMnu_accounts_menu_ministatemnt() {
        return mnu_accounts_menu_ministatemnt;
    }

    public static void setMnu_accounts_menu_ministatemnt(String mnu_accounts_menu_ministatemnt) {
        AppConstants.mnu_accounts_menu_ministatemnt = mnu_accounts_menu_ministatemnt;
    }

    public static String getMnu_accounts_menu_showmmid() {
        return mnu_accounts_menu_showmmid;
    }

    public static void setMnu_accounts_menu_showmmid(String mnu_accounts_menu_showmmid) {
        AppConstants.mnu_accounts_menu_showmmid = mnu_accounts_menu_showmmid;
    }

    public static String getMnu_fundtransfer_ownbank() {
        return mnu_fundtransfer_ownbank;
    }

    public static void setMnu_fundtransfer_ownbank(String mnu_fundtransfer_ownbank) {
        AppConstants.mnu_fundtransfer_ownbank = mnu_fundtransfer_ownbank;
    }

    public static String getMnu_fundtransfer_impstoaccount() {
        return mnu_fundtransfer_impstoaccount;
    }

    public static void setMnu_fundtransfer_impstoaccount(String mnu_fundtransfer_impstoaccount) {
        AppConstants.mnu_fundtransfer_impstoaccount = mnu_fundtransfer_impstoaccount;
    }

    public static String getMnu_fundtransfer_nefttoaccount() {
        return mnu_fundtransfer_nefttoaccount;
    }

    public static void setMnu_fundtransfer_nefttoaccount(String mnu_fundtransfer_nefttoaccount) {
        AppConstants.mnu_fundtransfer_nefttoaccount = mnu_fundtransfer_nefttoaccount;
    }

    public static String getMnu_fundtransfer_impstomobile() {
        return mnu_fundtransfer_impstomobile;
    }

    public static void setMnu_fundtransfer_impstomobile(String mnu_fundtransfer_impstomobile) {
        AppConstants.mnu_fundtransfer_impstomobile = mnu_fundtransfer_impstomobile;
    }

    public static String getMnu_fundtransfer_mngbenefeciaries() {
        return mnu_fundtransfer_mngbenefeciaries;
    }

    public static void setMnu_fundtransfer_mngbenefeciaries(String mnu_fundtransfer_mngbenefeciaries) {
        AppConstants.mnu_fundtransfer_mngbenefeciaries = mnu_fundtransfer_mngbenefeciaries;
    }

    public static String getMnu_accounts_menu_last5imps() {
        return mnu_accounts_menu_last5imps;
    }

    public static void setMnu_accounts_menu_last5imps(String mnu_accounts_menu_last5imps) {
        AppConstants.mnu_accounts_menu_last5imps = mnu_accounts_menu_last5imps;
    }

    public static String getMnu_accounts_menu_neftenquiry() {
        return mnu_accounts_menu_neftenquiry;
    }

    public static void setMnu_accounts_menu_neftenquiry(String mnu_accounts_menu_neftenquiry) {
        AppConstants.mnu_accounts_menu_neftenquiry = mnu_accounts_menu_neftenquiry;
    }

    public static String getMnu_verify_beneficiary_name() {
        return mnu_verify_beneficiary_name;
    }

    public static void setMnu_verify_beneficiary_name(String mnu_verify_beneficiary_name) {
        AppConstants.mnu_verify_beneficiary_name = mnu_verify_beneficiary_name;
    }


    public static String getMnu_check_imps_transaction_status() {
        return mnu_check_imps_transaction_status;
    }

    public static void setMnu_check_imps_transaction_status(String mnu_check_imps_transaction_status) {
        AppConstants.mnu_check_imps_transaction_status = mnu_check_imps_transaction_status;
    }


    public static String getMnu_bill_pay_complaint_management() {
        return mnu_bill_pay_complaint_management;
    }

    public static void setMnu_bill_pay_complaint_management(String mnu_bill_pay_complaint_management) {
        AppConstants.mnu_bill_pay_complaint_management = mnu_bill_pay_complaint_management;
    }

    public static String getMnu_bill_pay_register_complaints() {
        return mnu_bill_pay_register_complaints;
    }

    public static void setMnu_bill_pay_register_complaints(String mnu_bill_pay_register_complaints) {
        AppConstants.mnu_bill_pay_register_complaints = mnu_bill_pay_register_complaints;
    }

    public static String getMnu_bill_pay_track_complaints() {
        return mnu_bill_pay_track_complaints;
    }

    public static void setMnu_bill_pay_track_complaints(String mnu_bill_pay_track_complaints) {
        AppConstants.mnu_bill_pay_track_complaints = mnu_bill_pay_track_complaints;
    }

    //****************Menu Flags Getters & Setters***************************//


    public static void setLogo(MBank mInstance, ImageView ivAppLogo) {
        if (mInstance.getPackageName().equals("com.trustbank.trustmbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.ic_launcher));
        } else if (mInstance.getPackageName().equals("com.trustbank.trustdemombankapp")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.ic_launcher));
        } else if (mInstance.getPackageName().equals("com.trustbank.sadhnambank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.sadhna_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.pucbmbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.puc_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.pdccbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.pdcc_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.bucbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.ic_brahmpuri_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.shivajibank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.shivaji_bank_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.vmucbbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.vmucb_app_icon));
        } else if (mInstance.getPackageName().equals("com.trustbank.punepeoplesbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.ppc_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.gondiamahilabank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.gondia_mahila_bank));
        } else if (mInstance.getPackageName().equals("com.trustbank.smritibank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.smriti_bank));
        } else if (mInstance.getPackageName().equals("com.trustbank.manndeshibank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.mandeshi_bank_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.hedgewarmbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.hedgewar_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.janataajarambank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.janata_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.parijatmbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.parijat_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.kucbmbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.kucb_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.mdccmbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.mdcc_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.anuradhambankbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.anuradha_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.vnspmmbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.vnspm_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.mdccmbankapp")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.mdcc_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.nccsmbankapp")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.ic_nccs_logo));
        } else if (mInstance.getPackageName().equals("com.trustbank.motimbankapp")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.ic_moti_logo));
        }else if (mInstance.getPackageName().equals("com.trustbank.nagpursahakarimbankapp")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.ic_moti_logo));
        }else if (mInstance.getPackageName().equals("com.trustbank.itparkbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.ic_moti_logo));
        }else if (mInstance.getPackageName().equals("com.trustbank.cucbmbank")) {
            ivAppLogo.setImageDrawable(mInstance.getResources().getDrawable(R.drawable.ic_cucb_logo));
        }
    }

    public static void setUniqueKeys(MBank mInstance) {
        if (mInstance.getPackageName().equals("com.trustbank.trustmbank")) {  //for demo
            INSTITUTION_ID = "406";//"530";//PUCB---"406"//SADHANA
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.trustdemombankapp")) {  //for development
            INSTITUTION_ID = "406";//"530";//PUCB---"406"//SADHANA
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.pdccbank")) {
            INSTITUTION_ID = "406";//"530";//PUCB---"406"//SADHANA
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.sadhnambank")) {
            INSTITUTION_ID = "406";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.pucbmbank")) {
            INSTITUTION_ID = "530";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.bucbank")) { // brahmpuri bank.
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.shivajibank")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.vmucbbank")) {
            INSTITUTION_ID = "541";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.punepeoplesbank")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.gondiamahilabank")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.smritibank")) {
            INSTITUTION_ID = "64";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.manndeshibank")) {
            INSTITUTION_ID = "281";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.hedgewarmbank")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.janataajarambank")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.parijatmbank")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.kucbmbank")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.mdccmbank")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.anuradhambankbank")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.vnspmmbank")) {
            INSTITUTION_ID = "406";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.mdccmbankapp")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.nccsmbankapp")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        } else if (mInstance.getPackageName().equals("com.trustbank.motimbankapp")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        }else if (mInstance.getPackageName().equals("com.trustbank.nagpursahakarimbankapp")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        }else if (mInstance.getPackageName().equals("com.trustbank.itparkbank")) {
            INSTITUTION_ID = "479";
            API_KEY = BuildConfig.api_key;
        }else if (mInstance.getPackageName().equals("com.trustbank.cucbmbank")) {
            INSTITUTION_ID = "628";
            API_KEY = BuildConfig.api_key;
        }
    }

    public static void setIP(SharePreferenceUtils sharePreferenceUtils, Resources resources) {

        String stringIP;
        if (LoginInfo_isSetServerEnabled) {
           /* if (!TextUtils.isEmpty(sharePreferenceUtils.getString(AppConstants.STORE_IP))) {
            } else {
                sharePreferenceUtils.putString(AppConstants.STORE_IP, resources.getString(R.string.set_server_IP));
            }*/
            sharePreferenceUtils.putString(AppConstants.STORE_IP, resources.getString(R.string.set_server_IP)); //TODO For Sadhana updatation made this, above make commented.
            stringIP = sharePreferenceUtils.getString(AppConstants.STORE_IP);
            AppConstants.IP = stringIP;
        } else {
            stringIP = resources.getString(R.string.set_server_IP);
            AppConstants.IP = stringIP;
        }
    }

   /* public static void  printData(){
        try{

            String sercretKey = "a11b4f86cf186d127caa05db929d6f41";
            String message = "YB31YB41MOB521611569";
            String base64EncryptedString = "";
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digestOfPassword = md.digest(sercretKey.getBytes("UTF-8"));
            byte[] keyBytes =	Arrays.copyOf(digestOfPassword, 24);
            byte[] iv = Arrays.copyOf(digestOfPassword, 16);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher= Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new 	IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            byte[] plainTextBytes = message.getBytes("UTF-8");
            byte[] buf = cipher.doFinal(plainTextBytes);
            String result =  Base64.encodeToString(buf, Base64.DEFAULT);
            Log.e("result",result);
            //byte[] base64Bytes =  En
        }catch (Exception e){
            e.printStackTrace();
        }*/


    //base64EncryptedString = new String(base64Bytes);

    //System.out.println(base64EncryptedString);
    //}
}
