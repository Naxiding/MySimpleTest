package com.jitian.mysimpletest;

import android.util.Log;


/**
 * @author yd
 * @date 2019/6/6 0006
 */

public class LogUtil {

    public static void d(Class cl, String message) {
        Log.d(cl.getName(), message);
    }

    public static void d(String message) {
        Log.d(LogUtil.class.getName(), message);
    }

    public static void d(byte[] message) {
        StringBuilder builder = new StringBuilder();
        for (byte b : message) {
            if ((b & 0xff) < 0x10) {
                builder.append("0");
            }
            builder.append(Integer.toHexString(b & 0xff).toUpperCase()).append(" ");
        }
        Log.d(LogUtil.class.getName(), "Byte{ " + builder.toString() + " }");
    }

}
