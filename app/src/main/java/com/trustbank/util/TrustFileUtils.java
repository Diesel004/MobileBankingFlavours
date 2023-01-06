package com.trustbank.util;


import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class TrustFileUtils {

    public TrustFileUtils() {
    }

    public static File createFilePath(Context context,String fileName,String imagePath) {
        File mFileTemp;
        File noMedia;
        File mFilepath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
             mFilepath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), imagePath);
        }else {
             mFilepath = new File(Environment.getExternalStorageDirectory(), imagePath);
        }

        if (!mFilepath.exists()) {
            boolean mkdirs = mFilepath.mkdirs();
        }
        noMedia = new File(mFilepath, AppConstants.NO_MEDIA);
        if (!noMedia.exists()) {
            try {
                boolean createNewFile = noMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mFileTemp = new File(mFilepath, fileName);
        return mFileTemp;
    }

}
