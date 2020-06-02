package com.jitian.mysimpletest.activity;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.jitian.mysimpletest.R;
import com.jitian.mysimpletest.utils.LogUtil;
import com.jitian.mysimpletest.utils.ThreadUtil;

/**
 * @author YangDing
 * @date 2020/5/6
 */
public class WebActivity extends AppCompatActivity {

    private static final String ACTION_CHARGE_STATE_CHANGED = "action_charge_state_changed";
    private static final String ACTION_BATTERY_LEVEL_CHANGED = "action_battery_level_changed";
    private static final String ARGS_VALUE = "args_value";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d("intent:" + intent.getAction());
            if ("action_hide_nav_bar".equals(intent.getAction())) {
                mShowNavButton.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goneSystemUiWindow();
        //goneSystemUi();
        setContentView(R.layout.activity_web);
        Configuration mConfiguration = this.getResources().getConfiguration();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action_customer_serial_service_control_intent");
        intentFilter.addAction(ACTION_CHARGE_STATE_CHANGED);
        intentFilter.addAction(ACTION_BATTERY_LEVEL_CHANGED);
        intentFilter.addAction("action_hide_nav_bar");
        registerReceiver(mBroadcastReceiver, intentFilter);
        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendKeyCode(4);
                //startActivity(new Intent(WebActivity.this, MainActivity.class));
                //SystemPropertiesUtil.setProperty("persist.sys.stopserial", "yes");
                //stopSerialService();
                //getWindow().getDecorView().setSystemUiVisibility(1006638598);
            }
        });
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goneSystemUi();
                //SystemPropertiesUtil.setProperty("persist.sys.stopserial", "no");
                //startSerialService();
            }
        });
        startCustomNavBar();
        startFloatButton();
    }

    private LinearLayout mShowNavButton;
    private LinearLayout mCustomNavBarLayout;
    private WindowManager.LayoutParams showButtonLayoutParams;
    private WindowManager.LayoutParams customNavBarLayoutParams;

    private void startFloatButton() {
        showButtonLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            showButtonLayoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        } else {
            showButtonLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        showButtonLayoutParams.format = PixelFormat.RGBA_8888;
        showButtonLayoutParams.gravity = Gravity.END | Gravity.BOTTOM;
        showButtonLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        showButtonLayoutParams.width = (int) getResources().getDimension(R.dimen.float_button_size_width);
        showButtonLayoutParams.height = (int) getResources().getDimension(R.dimen.float_button_size_height);
        showButtonLayoutParams.x = (int) getResources().getDimension(R.dimen.float_button_start_right);
        showButtonLayoutParams.y = 240;
        mShowNavButton = (LinearLayout) View.inflate(this, R.layout.show_nav_bar_normal, null);
        mShowNavButton.setBackgroundResource(R.drawable.nav_normal_press_background);
        getWindowManager().addView(mShowNavButton, showButtonLayoutParams);
        mShowNavButton.setVisibility(View.VISIBLE);
        //mShowNavButton.setOnClickListener(customNavBarClickListener);
    }

    private void startCustomNavBar() {
        customNavBarLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            customNavBarLayoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        } else {
            customNavBarLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        customNavBarLayoutParams.format = PixelFormat.RGBA_8888;
        customNavBarLayoutParams.gravity = Gravity.END | Gravity.BOTTOM;
        customNavBarLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        customNavBarLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        customNavBarLayoutParams.height = (int) getResources().getDimension(R.dimen.float_button_size_height);
        customNavBarLayoutParams.x = (int) getResources().getDimension(R.dimen.float_button_start_right);
        customNavBarLayoutParams.y = 200;
        mCustomNavBarLayout = (LinearLayout) View.inflate(this, R.layout.custom_nav_bar, null);
        /*mCustomNavBarLayout.findViewById(R.id.recent).setOnClickListener(customNavBarClickListener);
        mCustomNavBarLayout.findViewById(R.id.back).setOnClickListener(customNavBarClickListener);
        mCustomNavBarLayout.findViewById(R.id.home).setOnClickListener(customNavBarClickListener);
        mCustomNavBarLayout.findViewById(R.id.back).setOnTouchListener(mOnTouchListener);
        mCustomNavBarLayout.findViewById(R.id.home).setOnTouchListener(mOnTouchListener);
        mCustomNavBarLayout.findViewById(R.id.hide).setOnClickListener(customNavBarClickListener);
        mCustomNavBarLayout.findViewById(R.id.show).setOnClickListener(customNavBarClickListener);
        */
        getWindowManager().addView(mCustomNavBarLayout, customNavBarLayoutParams);
    }


    private void goneSystemUiWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
        window.setAttributes(params);
    }

    public void goneSystemUi() {
        //隐藏虚拟按键
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private long mDownTime;
    private boolean mGestureAborted;
    private boolean mLongClicked;
    private int mTouchSlop;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            return dealOnTouchEvent(v, ev);
        }
    };

    public boolean dealOnTouchEvent(View view , MotionEvent ev) {
        final int action = ev.getAction();
        int x, y;
        if (action == MotionEvent.ACTION_DOWN) {
            mGestureAborted = false;
        }
        if (mGestureAborted) {
            return false;
        }
        int mCode = 0;
        if (view.getId() == R.id.back) {
            mCode = KeyEvent.KEYCODE_BACK;
        } else if (view.getId() == R.id.home) {
            mCode = KeyEvent.KEYCODE_HOME;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = SystemClock.uptimeMillis();
                mLongClicked = false;
                view.setPressed(true);
                if (mCode != 0) {
                    sendEvent(KeyEvent.ACTION_DOWN, 0, mDownTime, mCode);
                } else {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                x = (int) ev.getX();
                y = (int) ev.getY();
                view.setPressed(x >= -mTouchSlop
                        && x < view.getWidth() + mTouchSlop
                        && y >= -mTouchSlop
                        && y < view.getHeight() + mTouchSlop);
                break;
            case MotionEvent.ACTION_CANCEL:
                view.setPressed(false);
                if (mCode != 0) {
                    sendEvent(KeyEvent.ACTION_UP, KeyEvent.FLAG_CANCELED, mCode);
                }
                break;
            case MotionEvent.ACTION_UP:
                final boolean doIt = view.isPressed() && !mLongClicked;
                view.setPressed(false);
                if (mCode != 0) {
                    if (doIt) {
                        sendEvent(KeyEvent.ACTION_UP, 0, mCode);
                    } else {
                        sendEvent(KeyEvent.ACTION_UP, KeyEvent.FLAG_CANCELED, mCode);
                    }
                } else {
                    if (doIt) {
                        view.performClick();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void sendEvent(int action, int flags, int code) {
        sendEvent(action, flags, SystemClock.uptimeMillis(), code);
    }

    void sendEvent(int action, int flags, long when, int code) {
        final int repeatCount = (flags & KeyEvent.FLAG_LONG_PRESS) != 0 ? 1 : 0;
        final KeyEvent ev = new KeyEvent(mDownTime, when, action, code, repeatCount,
                0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                flags | KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY,
                InputDevice.SOURCE_KEYBOARD);
        //InputManager.getInstance().injectInputEvent(ev, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }


    private void startSearchWord(String word) {
        Uri uri = Uri.parse("https://www.baidu.com/s?wd=" + word);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.ifacetv.browser");
        startActivity(intent);
    }

    private void stopSerialService() {
        LogUtil.d("stop");
        Intent intent = new Intent();
        intent.setAction("action_customer_serial_service_control_intent");
        intent.putExtra("controlaction", "stop");
        sendBroadcast(intent);
    }

    private void startSerialService() {
        Intent intent = new Intent();
        intent.setAction("action_customer_serial_service_control_intent");
        intent.putExtra("controlaction", "start");
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void toggleCustomNavBar(boolean showButton) {
        if (showButton) {
            if (mCustomNavBarLayout.getParent() != null) {
                getWindowManager().removeView(mCustomNavBarLayout);
            }
            if (mShowNavButton.getParent() == null) {
                getWindowManager().addView(mShowNavButton, showButtonLayoutParams);
            }
        } else {
            if (mShowNavButton.getParent() != null) {
                getWindowManager().removeView(mShowNavButton);
            }
            if (mCustomNavBarLayout.getParent() == null) {
                getWindowManager().addView(mCustomNavBarLayout, customNavBarLayoutParams);
            }
        }
    }

    private View.OnClickListener customNavBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    sendKeyCode(KeyEvent.KEYCODE_BACK);
                    break;
                case R.id.recent:
                    break;
                case R.id.home:
                    sendKeyCode(KeyEvent.KEYCODE_HOME);
                    break;
                case R.id.hide:
                    toggleCustomNavBar(true);
                    break;
                case R.id.show_normal:
                    toggleCustomNavBar(false);
                    break;
                default:
                    break;
            }
        }
    };

    private static void sendKeyCode(final int keyCode) {
        LogUtil.d("sendKeyCode:" + keyCode);
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Instrumentation instrumentation = new Instrumentation();
                    instrumentation.sendCharacterSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
