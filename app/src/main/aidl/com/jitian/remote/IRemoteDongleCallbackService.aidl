package com.jitian.remote;
import com.jitian.remote.IRemoteDongleCallback;

interface IRemoteDongleCallbackService {

    void registerCallback(IRemoteDongleCallback callback);

    void unregisterCallback(IRemoteDongleCallback callback);

}
