package com.jitian.remote;
import com.jitian.remote.IRemoteBluetoothCallback;

interface IRemoteCallbackService {

    void registerCallback(IRemoteBluetoothCallback callback);

    void unregisterCallback(IRemoteBluetoothCallback callback);

}
