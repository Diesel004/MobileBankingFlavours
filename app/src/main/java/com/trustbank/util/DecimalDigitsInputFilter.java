package com.trustbank.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalDigitsInputFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsInputFilter() {
        mPattern = Pattern.compile("[0-9]{0," + (14 - 1) + "}+((\\.[0-9]{0," + (2 - 1) + "})?)||(\\.)?");
    }

    public DecimalDigitsInputFilter(String regex) {
        mPattern = Pattern.compile("^[7-9][0-9]{9}$");
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        Matcher matcher = mPattern.matcher(dest);
        if (!matcher.matches()) {
            return "";
        }
        return null;
    }
}
