package com.trustbank.util;

@SuppressWarnings("ALL")
public class TrustURL {

    //----------------------------url-----------------------------------------/
    public static String AuthenticateUserUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/apimictoken/api/web/mbank/authentication";
        } else {
            return "";
        }
    }

    public static String LogoutUserUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/apimictoken/api/web/mbank/logout";
        } else {
            return "";
        }
    }

    public static String GetMenuListUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/mbank.svc/api";
        } else {
            return "";
        }
    }


    public static String GetNeftEnquiryUrl(String enquiryType, String fromDate,
                                           String toDate, String transactionID, String profileId) {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/mbank.svc/api?enquiry_type=" + enquiryType + "&from_date=" + fromDate + "&to_date=" + toDate + "&transaction_id=" + transactionID + "&profile_id=" + profileId;
        } else {
            return "";
        }
    }

    public static String LocateAtmUrl(String state, String city) {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/assets/atm_list.json?state=" + state + "&city=" + city;
        } else {
            return "";
        }
    }

    public static String ContactUsUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/assets/contact.json";
        } else {
            return "";
        }
    }


    public static String SecurityHintUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/apimictoken/api/web/mbank/assets";
        } else {
            return "";
        }
    }

    public static String FAQUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/assets/faq_list.json";
        } else {
            return "";
        }
    }

    public static String BranchesUrl(String state, String city) {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/assets/branch_list.json?state=" + state + "&city=" + city;
        } else {
            return "";
        }
    }

    public static String MobileNoVerifyUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/apimictoken/api/web/mbank/apimob";
        } else {
            return "";
        }
    }

    public static String veryfyMpinUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/apimictoken/api/web/mbank/apimob";
        } else {
            return "";
        }
    }

    public static String GenerateMpinUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/apimictoken/api/web/mbank/generatempin";
        } else {
            return "";
        }
    }


    public static String GeneratePinUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/apimictoken/api/web/mbank/generatempin";
        } else {
            return "";
        }
    }

    public static String httpCallUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/mbank.svc/api/send_to_switch";// + action;
        } else {
            return "";
        }
    }

    public static String GetProfileAccountsAndChequeBookDetailsUrl(String mobileNo, String mClientId) {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/mbank.svc/api?mobile_number=" + mobileNo + "&custid=" + mClientId;
        } else {
            return "";
        }
    }

    public static String GetChequeBookDetailsUrl(String mobileNo, String accNo) {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/mbank.svc/api?mobile_number=" + mobileNo + "&ac_no=" + accNo + "&profile_id=" + AppConstants.getProfileID();
        } else {
            return "";
        }
    }

    public static String CheckChequeBookRequestUrl(String accNo, String chequeNo) {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/mbank.svc/api?args=<data><tag>CHQ_STATUS</tag><ac_no>" + accNo + "</ac_no><chq_no>" + chequeNo + "</chq_no></data>";
        } else {
            return "";
        }
    }

    public static String StopChequeRequestUrl(String accNo, String chequeNo) {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/mbank.svc/api?args=<data><tag>STOP_CHEQUE</tag><ac_no>" + accNo + "</ac_no><chq_no>" + chequeNo + "</chq_no></data>";
        } else {
            return "";
        }
    }

    public static String GetBranchDetailsRequestUrl(String ifscCode) {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/mbank.svc/api?args=<data><tag>IFSC_SEARCH</tag><ifsc_code>" + ifscCode + "</ifsc_code></data>";
        } else {
            return "";
        }
    }

    public static String GenerateStanRrnUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/mbank.svc/api/generate_stan_rrn";
        } else {
            return "";
        }
    }

    public static String GenerateOtpFundTransferUrl() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/apimictoken/api/web/mbank/generateotp";
        } else {
            return "";
        }
    }

    public static String getAboutUsDetails() {
        if (AppConstants.IP != null) {
            String url = AppConstants.IP + "/trustBank.Wcf.NetBanking.CbsNetBank.svc/GetListByTag?args=%3Cdata%3E%3Ctag%3EABOUT_US%3C/tag%3E%3C/data%3E";
            return url;
        } else {
            return "";
        }
    }

    public static String getTermsConditions() {
        if (AppConstants.IP != null) {
            String url = AppConstants.IP + "/assets/mbank_terms_and_conditions.html";
            return url;
        } else {
            return "";
        }
    }

    public static String getAdevertisement() {
        if (AppConstants.IP != null) {
            String url = AppConstants.IP + "/folder_content.aspx";
            return url;
        } else {
            return "";
        }
    }

    public static String getAccountDetails(String accno) {
        if (AppConstants.IP != null) {
            String url = AppConstants.IP + "/mbank.svc/api?account_number=" + accno;
            return url;
        } else {
            return "";
        }
    }

    public static String GetCheckForUpdateURL(String appVesrion) {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/TrustBank.Wcf.FI.AndroidAppService.svc/CheckForUpdate?currentVersion=" + appVesrion;
        } else {
            return "";
        }
    }

    public static String getURLForFundTransferOwnAndNeft() {
        if (AppConstants.IP != null) {
            return AppConstants.IP + "/mbank.svc/api";
        } else {
            return "";
        }
    }

    public static String getCovergaeDetails(String category) {
        if (AppConstants.IP != null) {
            String url = AppConstants.IP + "/mbank.svc/api?category=" + category;
            return url;
        } else {
            return "";
        }
    }

    public static String getbillerDetails(String category) {
        if (AppConstants.IP != null) {
            String url = AppConstants.IP + "/mbank.svc/api?biller_id=" + category;
           // mbank.svc/api?biller_id
            return url;
        } else {
            return "";
        }
    }

    public static String getSearchBillerDetails(String coverage,String category,String billerName,String billerId) {
        if (AppConstants.IP != null) {
            String url = AppConstants.IP + "/mbank.svc/api?category=" + category+"&coverage="+coverage+"&biller_name="+billerName+"&biller_id="+billerId;

            return url;
        } else {
            return "";
        }
    }

    public static String getMandateDetails(String accno) {
        if (AppConstants.IP != null) {
            String url = AppConstants.IP + "/mbank.svc/api?account_number=" + accno;
            return url;
        } else {
            return "";
        }
    }
    public static String getBarcodeDetails(String accno) {
        if (AppConstants.IP != null) {
            String url = AppConstants.IP + "/mbank.svc/api?account_number=" + accno;
            return url;
        } else {
            return "";
        }
    }

}