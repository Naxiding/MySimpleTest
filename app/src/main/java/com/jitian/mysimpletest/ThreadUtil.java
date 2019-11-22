package com.jitian.mysimpletest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author YangDing
 * @date 2019/7/12
 */
public class ThreadUtil {

    public static void execute(Runnable runnable) {
        getExecutor().execute(runnable);
    }

    private static ExecutorService getExecutor() {
        ThreadFactory nameThread = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ThreadUtil");
            }
        };
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), nameThread);
    }

    private static long lastClickTime = 0;
    private static final int TIME_LIMIT = 1000;

    public static boolean isNotQuickClick() {
        boolean flag = (System.currentTimeMillis() - lastClickTime - TIME_LIMIT > 0);
        lastClickTime = System.currentTimeMillis();
        return flag;
    }

}
