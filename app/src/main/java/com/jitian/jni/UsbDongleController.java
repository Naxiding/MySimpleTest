package com.jitian.jni;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.jitian.mysimpletest.LogUtil;
import com.jitian.mysimpletest.ThreadUtil;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author YangDing
 * @date 2020/4/17
 */
public class UsbDongleController {

    public static final int STATE_DONGLE_ERROR = -1;
    public static final int STATE_DONGLE_ADDED = 1;
    public static final int STATE_DONGLE_REMOVED = 2;
    private static final int TARGET_VENDOR_ID = 36705;
    private static final int TARGET_PRODUCT_ID = 63540;
    private static final int DEFAULT_TIMEOUT = 2000;
    private static final int DEFAULT_WAIT_TIMES = 3;
    private UsbManager mUsbManager;
    private UsbDevice mTargetUsbDevice;
    private static final String ACTION_USB_PERMISSION = "action_usb_permission";
    private UsbEndpoint mInPoint;
    private UsbEndpoint mOutPoint;
    private UsbDeviceConnection mConnection;
    private UsbInterface mUsbInterface;
    private OnChangeCallback mOnChangeCallback;
    private Context mContext;
    private boolean mReceiverRegistered;
    private byte[] mNativeData;
    private JiTianEncrypt mNativeEncrypt;

    private volatile boolean isStarted = false;
    private boolean shouldStillListener = true;

    public void bindThis(Context context, OnChangeCallback onChangeCallback) {
        mContext = context;
        mOnChangeCallback = onChangeCallback;
        mNativeEncrypt = new JiTianEncrypt();
        registerDongleReceiver();
        initUsbDevice();
    }

    private void registerDongleReceiver() {
        if (mContext != null && !mReceiverRegistered) {
            mReceiverRegistered = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            mContext.registerReceiver(mDongleStateReceiver, filter);
        }
    }

    private void initUsbDevice() {
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (mUsbManager != null) {
            HashMap<String, UsbDevice> mUsbDevices = mUsbManager.getDeviceList();
            LogUtil.d("mUsbDevices:" + mUsbDevices);
            if (mUsbDevices != null && !mUsbDevices.isEmpty()) {
                for (UsbDevice usbDevice : mUsbDevices.values()) {
                    if (isTargetUsbDevice(usbDevice)) {
                        mTargetUsbDevice = usbDevice;
                        break;
                    }
                }
            }
            if (mTargetUsbDevice != null) {
                if (!mUsbManager.hasPermission(mTargetUsbDevice)) {
                    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                    mContext.registerReceiver(mUsbPermissionReceiver, filter);
                    PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    mUsbManager.requestPermission(mTargetUsbDevice, pi);
                } else {
                    initEndPoint(mTargetUsbDevice);
                }
            }
        }
    }

    private void initEndPoint(UsbDevice device) {
        int interfaceCount = device.getInterfaceCount();
        LogUtil.d("interfaceCount:" + interfaceCount);
        UsbInterface usbInterface = null;
        for (int i = 0; i < interfaceCount; i++) {
            usbInterface = device.getInterface(i);
            LogUtil.d("" + usbInterface);
            if (usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_HID && usbInterface.getEndpointCount() == 2) {
                mUsbInterface = usbInterface;
                break;
            }
        }
        if (usbInterface != null) {
            mConnection = mUsbManager.openDevice(device);
            if (mConnection != null) {
                if (mConnection.claimInterface(usbInterface, true)) {
                    for (int j = 0; j < usbInterface.getEndpointCount(); j++) {
                        UsbEndpoint endpoint = usbInterface.getEndpoint(j);
                        LogUtil.d("endpoint:" + endpoint.getType() + " , " + endpoint.getDirection());
                        if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_INT) {
                            if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                                mOutPoint = endpoint;
                            } else {
                                mInPoint = endpoint;
                            }
                        }
                    }
                }
            }
        }
        if (mOutPoint != null && mInPoint != null) {
            if (mOnChangeCallback != null) {
                mOnChangeCallback.onStateChanged(STATE_DONGLE_ADDED);
            }
        } else {
            if (mOnChangeCallback != null) {
                mOnChangeCallback.onStateChanged(STATE_DONGLE_ERROR);
            }
        }
    }

    public void sendData() {
        mNativeData = mNativeEncrypt.getEncryptData();
        sendData(mNativeData);
    }

    private void sendData(byte[] bytes) {
        if (mOutPoint != null && mConnection != null) {
            int write = mConnection.bulkTransfer(mOutPoint, bytes, bytes.length, DEFAULT_TIMEOUT);
            LogUtil.d("write:" + write);
            if (write > 0) {
                startReadListener();
            }
        }
    }

    private int readData() {
        if (mInPoint != null && mConnection != null) {
            byte[] readData = new byte[64];
            int read = mConnection.bulkTransfer(mInPoint, readData, readData.length, DEFAULT_TIMEOUT);
            LogUtil.d("read:" + read);
            if (read > 0) {
                LogUtil.d(readData);
                if (mOnChangeCallback != null) {
                    mOnChangeCallback.onMacResponse(analysisDongleMac(readData));
                }
            }
            return read;
        }
        return 0;
    }

    private static final int DEFINE_DATA_LENGTH = 7;

    private String analysisDongleMac(byte[] responseData) {
        if (responseData == null || responseData[0] != mNativeEncrypt.getCertifiedData(mNativeData)) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            for (int index = 1; index < DEFINE_DATA_LENGTH; index++) {
                if ((responseData[index] & 0xff) < 0x10) {
                    builder.append("0");
                }
                builder.append(Integer.toHexString(responseData[index] & 0xff).toUpperCase());
                if (index < DEFINE_DATA_LENGTH - 1) {
                    builder.append(":");
                }
            }
            return builder.toString();
        }
    }

    private void startReadListener() {
        if (!isStarted) {
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    int times = 1;
                    while (shouldStillListener) {
                        if (readData() > 0 || times >= DEFAULT_WAIT_TIMES) {
                            shouldStillListener = false;
                            isStarted = false;
                            break;
                        } else {
                            times++;
                        }
                    }
                }
            });
            isStarted = true;
        }
    }

    private void unRegisterDongleReceiver() {
        if (mContext != null && mReceiverRegistered) {
            mReceiverRegistered = false;
            mContext.unregisterReceiver(mDongleStateReceiver);
        }
        mOnChangeCallback = null;
    }

    public void releaseUsbResources() {
        if (mConnection != null) {
            if (mUsbInterface != null) {
                mConnection.releaseInterface(mUsbInterface);
            }
            mConnection.close();
        }
        mUsbInterface = null;
        mConnection = null;
        mInPoint = null;
        mOutPoint = null;
        mTargetUsbDevice = null;
    }

    public void unBindThis() {
        unRegisterDongleReceiver();
        releaseUsbResources();
    }

    private BroadcastReceiver mDongleStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onReceiverIntent(intent);
        }
    };

    private BroadcastReceiver mUsbPermissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                if (granted) {
                    initEndPoint(device);
                }
            }
        }
    };

    private void onReceiverIntent(Intent intent) {
        if (mOnChangeCallback != null && intent != null) {
            UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (!isTargetUsbDevice(usbDevice)) {
                return;
            }
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
                initEndPoint(usbDevice);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
                mOnChangeCallback.onStateChanged(STATE_DONGLE_REMOVED);
            }
        }
    }

    private boolean isTargetUsbDevice(UsbDevice usbDevice) {
        if (usbDevice == null) {
            return false;
        }
        return TARGET_VENDOR_ID == usbDevice.getVendorId() && TARGET_PRODUCT_ID == usbDevice.getProductId();
    }

    public interface OnChangeCallback {
        /**
         * dongle状态变化的回调
         *
         * @param currentState 变化后的状态
         */
        void onStateChanged(int currentState);

        /**
         * 回调解析的dongle mac
         *
         * @param dongleMac 数据内容
         */
        void onMacResponse(String dongleMac);
    }
}
