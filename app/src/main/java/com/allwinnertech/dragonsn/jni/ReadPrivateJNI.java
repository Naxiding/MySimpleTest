
package com.allwinnertech.dragonsn.jni;

public class ReadPrivateJNI {

    static {
        System.loadLibrary("allwinnertech_read_private");
        nativeInit();
    }

    public static native boolean nativeInit();

    public static native String nativeGetParameter(String name);

    public static native boolean nativeSetParameter(String name, String value);

    public static native void nativeRelease();

}
