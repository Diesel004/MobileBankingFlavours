package com.trustbank.util;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESEnDecryption {

    public static String encrypt(byte[] key, byte[] text) throws Exception {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(text);
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(byte[] key, byte[] base64DecodedBytes) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(base64DecodedBytes);
        return new String(decrypted, "UTF-8");
    }
}
