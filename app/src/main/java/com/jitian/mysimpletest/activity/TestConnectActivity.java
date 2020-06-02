package com.jitian.mysimpletest.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.jitian.mysimpletest.utils.BluetoothUtil;
import com.jitian.mysimpletest.utils.ThreadUtil;

import java.util.List;

/**
 * @author YangDing
 * @date 2019/12/5
 */
public class TestConnectActivity extends AppCompatActivity {

    private static final String TAG = "LogUtil";
    private BluetoothAdapter mBluetoothAdapter;
    private CustomGattCallback mCustomGattCallback;
    private BluetoothGatt mBluetoothGatt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice connectedDevice = null;
        Toast.makeText(TestConnectActivity.this, "TestConnectActivity onCreate", Toast.LENGTH_SHORT).show();
        mCustomGattCallback = new CustomGattCallback();
        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            Log.d(TAG, "address:" + device.getAddress() + " , name:" + device.getName());
            if (BluetoothUtil.isConnected(device)) {
                connectedDevice = device;
            }
        }
        if (connectedDevice != null) {
            Log.d(TAG, "address:" + connectedDevice.getAddress() + " , name:" + connectedDevice.getName());
            Toast.makeText(TestConnectActivity.this, "doGattConnection", Toast.LENGTH_SHORT).show();
            doGattConnection(connectedDevice);
        }
    }

    private void doGattConnection(final BluetoothDevice connectedDevice) {
        synchronized (this) {
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    mBluetoothGatt = connectedDevice.connectGatt(TestConnectActivity.this, false, mCustomGattCallback);
                }
            });
        }
    }

    private class CustomGattCallback extends BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "CustomGattCallback --> onConnectionStateChange newState:" + newState);
            Toast.makeText(TestConnectActivity.this, "onConnectionStateChange:" + newState, Toast.LENGTH_SHORT).show();
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (gatt != null) {
                    boolean flag = gatt.discoverServices();
                    Log.d(TAG, "CustomGattCallback --> onConnectionStateChange discoverServices : flag = " + flag);
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(TAG, "BluetoothGattCallback --> onServicesDiscovered status:" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        Log.d(TAG, "characteristic:" + characteristic.getUuid());
                        Toast.makeText(TestConnectActivity.this, "onServicesDiscovered success", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG,"onCharacteristicChanged");
        }
    }

}
