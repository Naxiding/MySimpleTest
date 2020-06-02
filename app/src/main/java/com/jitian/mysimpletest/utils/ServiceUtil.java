package com.jitian.mysimpletest.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * @author YangDing
 * @date 2020/4/23
 */
public class ServiceUtil {

    public static final String TEST_PACKAGE = "com.globalhome.services";
    public static final String TEST_SERVICE = "com.globalhome.services.SerialService";

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext    context
     * @param serviceName 是包名+服务的类名（例如：com.jitian.service.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAm = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAm.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
