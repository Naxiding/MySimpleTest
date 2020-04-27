package com.jitian.jni;

/**
 * @author YangDing
 * @date 2020/4/21
 */
public class JiTianEncrypt {

    private static final String LIB_NAME = "jitian_encrypt";

    static {
        System.loadLibrary(LIB_NAME);
    }

    public native byte[] getEncryptData();

    public native int getCertifiedData(byte[] encryptData);

}
