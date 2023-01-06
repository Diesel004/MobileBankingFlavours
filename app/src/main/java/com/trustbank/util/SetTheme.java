package com.trustbank.util;

import android.app.Activity;
import android.content.Context;

import com.trustbank.R;


public class SetTheme {

    private static int sTheme;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_BLUE = 1;
    public final static int THEME_DEFAULT_NO_ACTION_BAR = 3;
    public final static int THEME_BLUE_NO_ACTION_BAR = 4;

    Activity activity;
    int theme;
    private static SharePreferenceUtils sharePreferenceUtils;

    public static void changeToTheme(Context activity, boolean isNoActionBar) {
        sharePreferenceUtils = new SharePreferenceUtils(activity);
        if (activity.getPackageName().equals("com.trustbank.pdccbank") ) {
            sTheme = 1;
        } else {
            sTheme = 0;  // default theme in red.
//            sTheme = 1;
        }

        switch (sTheme) {
            case 0:   // default theme
                if (isNoActionBar) {
                    onActivityCreateSetTheme(activity, SetTheme.THEME_DEFAULT_NO_ACTION_BAR);
                } else {
                    onActivityCreateSetTheme(activity, SetTheme.THEME_DEFAULT);
                }
                break;

            case 1:
                if (isNoActionBar) {
                    onActivityCreateSetTheme(activity, SetTheme.THEME_BLUE_NO_ACTION_BAR);
                } else {
                    onActivityCreateSetTheme(activity, SetTheme.THEME_BLUE);
                }
                break;
        }

    }

    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Context activity, int theme) {
        switch (theme) {
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.AppThemeBlue);
                break;
            case THEME_BLUE_NO_ACTION_BAR:
                activity.setTheme(R.style.NoActionBarThemeBlue);
                break;
            case THEME_DEFAULT_NO_ACTION_BAR:
                activity.setTheme(R.style.NoActionBarTheme);
                break;
            default:
                activity.setTheme(R.style.AppTheme);
                break;
        }
    }
}
