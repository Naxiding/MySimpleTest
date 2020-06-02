package com.jitian.mysimpletest.utils;

import android.annotation.SuppressLint;

import java.lang.reflect.Method;

/**
 * @author YangDing
 * @date 2019/7/3
 */
public class SystemPropertiesUtil {
    public static String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            @SuppressLint("PrivateApi")
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String setProperty(String key, String value) {
        try {
            @SuppressLint("PrivateApi")
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("set", String.class, String.class);
            value = (String) (get.invoke(c, key, value));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
