package com.jitian.mysimpletest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author YangDing
 * @date 2019/8/19
 */
public class SignConfig {

    private static final String H6_SIGN_MD5 = "46:67:DE:7D:CA:54:06:C7:84:82:4C:8A:A7:8E:5F:25";
    private static final String H6_PACKAGE_NAME = "com.jitian.remotecontrol2";

    public static boolean isSigned(Context context) {
        return H6_SIGN_MD5.equals(getSignMd5Str(context));
    }

    /**
     * MD5加密
     *
     * @param byteStr 需要加密的内容
     * @return 返回 byteStr的md5值
     */
    private static String encryptionMd5(byte[] byteStr) {
        MessageDigest messageDigest;
        StringBuilder md5StrBuff = new StringBuilder();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
                if (i < byteArray.length - 1) {
                    md5StrBuff.append(":");
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString().toUpperCase();
    }

    /**
     * 获取app签名md5值,与“keytool -list -keystore D:\Desktop\app_key”获取的md5值一样
     */
    private static String getSignMd5Str(Context context) {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(H6_PACKAGE_NAME, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return encryptionMd5(sign.toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

}
