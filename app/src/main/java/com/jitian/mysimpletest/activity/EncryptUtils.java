package com.jitian.mysimpletest.activity;

import com.jitian.mysimpletest.utils.LogUtil;

/**
 * @author YangDing
 * @date 2020/4/17
 */
public class EncryptUtils {

    public static byte[] encryptData(int num) {
        String before = String.valueOf(num);
        int sum = 0;
        for (char c : before.toCharArray()) {
            sum += (c - '0');
        }
        sum += before.length();
        LogUtil.d("encryptData:" + sum);
        return new byte[]{(byte) sum};
    }

    public static int getRandomNum() {
        int dataLength = (int) ((Math.random() * 9 + 1));
        int realData = (int) ((Math.random() * 9 + 1) * (Math.pow(10, dataLength - 1)));
        LogUtil.d("dataLength:" + dataLength + " , realData:" + realData);
        return realData;
    }

}
