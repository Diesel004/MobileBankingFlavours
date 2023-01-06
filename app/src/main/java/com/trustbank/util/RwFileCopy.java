package com.trustbank.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class RwFileCopy {
    public static String response ="Success";
    public static String copyFileOrDirectory(String srcDir, String dstDir) {
        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, AppConstants.PROFILE_NAME);

            if (src.isDirectory()) {
                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);
                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = e.getMessage();
        }
        return response;
    }

    public static String copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }catch (Exception e) {
            e.printStackTrace();
            response = e.getMessage();
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
        return response;
    }

}