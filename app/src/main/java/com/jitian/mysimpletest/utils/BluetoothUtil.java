package com.jitian.mysimpletest.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author yd
 * @date 2019/10/26 0026
 */
public class BluetoothUtil {

    @SuppressWarnings({"JavaReflectionMemberAccess", "ConstantConditions"})
    public static boolean isConnected(BluetoothDevice device) {
        Class<BluetoothDevice> bluetoothDeviceClass = BluetoothDevice.class;
        try {
            Method method = bluetoothDeviceClass.getDeclaredMethod("isConnected", (Class[]) null);
            method.setAccessible(true);
            return (boolean) method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.d("LogUtil", e.getMessage());
        }
        return false;
    }

    @SuppressWarnings({"JavaReflectionMemberAccess", "ConstantConditions"})
    public static boolean refreshGatt(BluetoothGatt gatt) {
        Class<BluetoothGatt> gattClass = BluetoothGatt.class;
        try {
            Method method = gattClass.getDeclaredMethod("refresh", (Class[]) null);
            method.setAccessible(true);
            return (boolean) method.invoke(gatt, (Object[]) null);
        } catch (Exception e) {
            Log.d("LogUtil", e.getMessage());
        }
        return false;
    }

    @SuppressWarnings({"JavaReflectionMemberAccess", "ConstantConditions"})
    public static void removeBond(BluetoothDevice device) {
        Class<BluetoothDevice> bluetoothDeviceClass = BluetoothDevice.class;
        try {
            Method method = bluetoothDeviceClass.getMethod("removeBond");
            method.invoke(device);
        } catch (Exception e) {
            Log.d("LogUtil", e.getMessage());
        }
    }

}
