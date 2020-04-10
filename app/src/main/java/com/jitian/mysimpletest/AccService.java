package com.jitian.mysimpletest;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

/**
 * @author YangDing
 * @date 2020/1/8
 */
public class AccService extends AccessibilityService {

    private static final String TAG = AccService.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        //如果没开启，就提醒开启辅助功能
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.d(TAG, "onKeyEvent:" + event.getKeyCode());
        return super.onKeyEvent(event);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent:" + event.getEventType() + " , " + event.getAction());
    }

    @Override
    public void onInterrupt() {

    }

}
