package com.jitian.mysimpletest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * @author YangDing
 * @date 2020/4/1
 */
public class SecureZoneUtil {

    private static final String TAG = "yangding";

    /**
     * **simple data**
     * <p>
     * EEPRPM | SN = WS0017050010100092
     * EEPRPM | wifi_mac = 28:07:0D:00:01:0A
     * EEPRPM | PN = 10GW0108JX0102
     * EEPRPM | IMEI = 98823642443134200000000000000000
     * EEPRPM | mac = 28:07:0D:00:01:0A
     * EEPRPM | MD5 = 1005fa0042c617902eb193f530acc077
     * EEPRPM | TID = 010121011000001
     * EEPRPM | EMAC = 0C:C6:55:04:86:FF
     * EEPRPM | WMAC = 0C:C6:55:04:86:9B
     * PRIVATE | SN = WS0017050010100092
     * PRIVATE | wifi_mac = 28:07:0D:00:01:0A
     * PRIVATE | PN = 10GW0108JX0102
     * PRIVATE | IMEI = 98823642443134200000000000000000
     * PRIVATE | mac = 28:07:0D:00:01:0A
     * PRIVATE | MD5 = 93d1d38240a029defc82b6bcc14397fd
     * PRIVATE | TID = 010121011000001
     * PRIVATE | EMAC = 0C:C6:55:04:86:FF
     * PRIVATE | WMAC = 0C:C6:55:04:86:9B
     */
    private static final String READ_COMMAND = "securezone -r";
    private static final String WRITE_COMMAND = "securezone";
    private static final String SUCCESS = "finished";
    private static final String EEPROM_AREA = "-w";
    private static final String PRIVATE_AREA = "-wp";

    private static final String EEPRPM_SN = "EEPRPM | SN = ";
    private static final String EEPRPM_WIFI_MAC = "EEPRPM | wifi_mac = ";
    private static final String EEPRPM_PN = "EEPRPM | PN = ";
    private static final String EEPRPM_VN = "EEPRPM | VN = ";
    private static final String EEPRPM_IMEI = "EEPRPM | IMEI = ";
    private static final String EEPRPM_MAC = "EEPRPM | mac = ";
    private static final String EEPRPM_MD5 = "EEPRPM | MD5 = ";
    private static final String EEPRPM_TID = "EEPRPM | TID = ";
    private static final String EEPRPM_EMAC = "EEPRPM | EMAC = ";
    private static final String EEPRPM_WMAC = "EEPRPM | WMAC = ";
    private static final String EEPRPM_WSSN = "EEPRPM | WSSN";

    private static final String PRIVATE_SN = "PRIVATE | SN = ";
    private static final String PRIVATE_WIFI_MAC = "PRIVATE | wifi_mac = ";
    private static final String PRIVATE_PN = "PRIVATE | PN = ";
    private static final String PRIVATE_VN = "PRIVATE | VN = ";
    private static final String PRIVATE_IMEI = "PRIVATE | IMEI = ";
    private static final String PRIVATE_MAC = "PRIVATE | mac = ";
    private static final String PRIVATE_MD5 = "PRIVATE | MD5 = ";
    private static final String PRIVATE_TID = "PRIVATE | TID = ";
    private static final String PRIVATE_EMAC = "PRIVATE | EMAC = ";
    private static final String PRIVATE_WMAC = "PRIVATE | WMAC = ";
    private static final String PRIVATE_WSSN = "PRIVATE | WSSN";

    private static final String SN = "sn";
    private static final String WIFI_MAC = "wifi_mac";
    private static final String PN = "pn";
    private static final String VN = "vn";
    private static final String IMEI = "imei";
    private static final String MAC = "mac";
    private static final String MD5 = "md5";
    private static final String TID = "tid";
    private static final String EMAC = "emac";
    private static final String WMAC = "wmac";
    private static final String WSSN = "wssn";

    private HashMap<String, String> mTotalNumber = new HashMap<String, String>();

    private SecureZoneUtil() {
        mTotalNumber = initNumber();
    }

    public static SecureZoneUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final SecureZoneUtil INSTANCE = new SecureZoneUtil();
    }

    public void release() {
        if (mTotalNumber != null) {
            mTotalNumber.clear();
        }
    }

    /**
     * 读取机器里面的各种设备码
     *
     * @param keyWord 设备码的关键字
     * @return 返回设备码，如果没有获取到，则返回null
     */
    public String getDeviceNumber(String keyWord) {
        if (keyWord == null || mTotalNumber.isEmpty()) {
            return null;
        }
        String realKey = switchReadKeyWord(keyWord);
        if (realKey != null) {
            return mTotalNumber.get(realKey);
        } else {
            return mTotalNumber.get(keyWord);
        }
    }

    public boolean isEmpty() {
        return mTotalNumber == null;
    }

    public String getSnNumber() {
        return getDeviceNumber(EEPRPM_SN);
    }

    public String getWifiMacNumber() {
        return getDeviceNumber(EEPRPM_WIFI_MAC);
    }

    public String getPnNumber() {
        return getDeviceNumber(EEPRPM_PN);
    }

    public String getVnNumber() {
        return getDeviceNumber(EEPRPM_VN);
    }

    public String getImeiNumber() {
        return getDeviceNumber(EEPRPM_IMEI);
    }

    public String getMacNumber() {
        return getDeviceNumber(EEPRPM_MAC);
    }

    public String getMd5Number() {
        return getDeviceNumber(EEPRPM_MD5);
    }

    public String getTidNumber() {
        return getDeviceNumber(EEPRPM_TID);
    }

    public String getEmacNumber() {
        return getDeviceNumber(EEPRPM_MAC);
    }

    public String getWmacNumber() {
        return getDeviceNumber(EEPRPM_WIFI_MAC);
    }

    public String getWssnNumber() {
        return getDeviceNumber(EEPRPM_WSSN);
    }

    public boolean setDeviceNumber(String name, String value) {
        if (name == null || value == null
                || "".equals(name) || "".equals(value)) {
            return false;
        }
        String keyWord = name.toUpperCase();
        /*String keyWord = switchWriteKeyWord(name);
        if (keyWord == null) {
            keyWord = name.toUpperCase();
        }*/
        boolean writeEepromResult = writeEepromData(keyWord, value);
        boolean writePrivateResult = writePrivateData(keyWord, value);
        if (writeEepromResult || writePrivateResult) {
            mTotalNumber = initNumber();
        }
        return writeEepromResult || writePrivateResult;
    }

    public boolean writeSnNumber(String value) {
        return setDeviceNumber(SN, value);
    }

    public boolean writeWifiMacNumber(String value) {
        return setDeviceNumber(WIFI_MAC, value);
    }

    public boolean writePnNumber(String value) {
        return setDeviceNumber(PN, value);
    }

    public boolean writeVnNumber(String value) {
        return setDeviceNumber(VN, value);
    }

    public boolean writeImeiNumber(String value) {
        return setDeviceNumber(IMEI, value);
    }

    public boolean writeMd5Number(String value) {
        return setDeviceNumber(MD5, value);
    }

    public boolean writeTidNumber(String value) {
        return setDeviceNumber(TID, value);
    }

    public boolean writeTestNumber(String value) {
        return setDeviceNumber("YANGDING", value);
    }

    public boolean writeEmacNumber(String value) {
        return setDeviceNumber(EMAC, value);
    }

    public boolean writeWmacNumber(String value) {
        return setDeviceNumber(WMAC, value);
    }

    public boolean writeMacNumber(String value) {
        return setDeviceNumber(MAC, value);
    }

    public boolean writeWssnNumber(String value) {
        return setDeviceNumber(WSSN, value);
    }


    private static boolean writeEepromData(String name, String value) {
        return writeData(name, value, EEPROM_AREA);
    }

    private static boolean writePrivateData(String name, String value) {
        return writeData(name, value, PRIVATE_AREA);
    }

    private static boolean writeData(String name, String value, String area) {
        Log.d(TAG, "Start writeData name:" + name + " , value:" + value + " , area:" + area);
        String[] args = new String[]{WRITE_COMMAND, area, name, value};
        String result = runProcess(args);
        Log.d(TAG, "WriteData result:" + result);
        return (result != null && result.toLowerCase().contains(SUCCESS));
    }

    private static String switchReadKeyWord(String keyWord) {
        if (keyWord.toLowerCase().contains(WSSN)) {
            return EEPRPM_WSSN;
        } else if (keyWord.toLowerCase().contains(WIFI_MAC)) {
            return EEPRPM_WIFI_MAC;
        } else if (keyWord.toLowerCase().contains(PN)) {
            return EEPRPM_PN;
        } else if (keyWord.toLowerCase().contains(VN)) {
            return EEPRPM_VN;
        } else if (keyWord.toLowerCase().contains(IMEI)) {
            return EEPRPM_IMEI;
        } else if (keyWord.toLowerCase().contains(MD5)) {
            return EEPRPM_MD5;
        } else if (keyWord.toLowerCase().contains(TID)) {
            return EEPRPM_TID;
        } else if (keyWord.toLowerCase().contains(EMAC)) {
            return EEPRPM_MAC;
        } else if (keyWord.toLowerCase().contains(WMAC)) {
            return EEPRPM_WIFI_MAC;
        } else if (keyWord.toLowerCase().contains(MAC)) {
            return EEPRPM_MAC;
        } else if (keyWord.toLowerCase().contains(SN)) {
            return EEPRPM_SN;
        }
        return null;
    }

    private static String switchWriteKeyWord(String keyWord) {
        if (keyWord.toLowerCase().contains(WSSN)) {
            return WSSN.toUpperCase();
        } else if (keyWord.toLowerCase().contains(WIFI_MAC)) {
            return WIFI_MAC.toLowerCase();
        } else if (keyWord.toLowerCase().contains(PN)) {
            return PN.toUpperCase();
        } else if (keyWord.toLowerCase().contains(VN)) {
            return VN.toUpperCase();
        } else if (keyWord.toLowerCase().contains(IMEI)) {
            return IMEI.toUpperCase();
        } else if (keyWord.toLowerCase().contains(MD5)) {
            return MD5.toUpperCase();
        } else if (keyWord.toLowerCase().contains(TID)) {
            return TID.toUpperCase();
        } else if (keyWord.toLowerCase().contains(EMAC)) {
            return MAC.toLowerCase();
        } else if (keyWord.toLowerCase().contains(WMAC)) {
            return WIFI_MAC.toLowerCase();
        } else if (keyWord.toLowerCase().contains(MAC)) {
            return MAC.toLowerCase();
        } else if (keyWord.toLowerCase().contains(SN)) {
            return SN.toUpperCase();
        }
        return null;
    }

    private HashMap<String, String> initNumber() {
        HashMap<String, String> numberHashMap = new HashMap<String, String>(3);
        BufferedReader reader;
        try {
            Process process = Runtime.getRuntime().exec(READ_COMMAND);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                //read eep rom
                if (line.contains(EEPRPM_SN)) {
                    numberHashMap.put(EEPRPM_SN, line.substring(EEPRPM_SN.length()));
                } else if (line.contains(EEPRPM_WIFI_MAC)) {
                    numberHashMap.put(EEPRPM_WIFI_MAC, line.substring(EEPRPM_WIFI_MAC.length()));
                } else if (line.contains(EEPRPM_PN)) {
                    numberHashMap.put(EEPRPM_PN, line.substring(EEPRPM_PN.length()));
                } else if (line.contains(EEPRPM_VN)) {
                    numberHashMap.put(EEPRPM_VN, line.substring(EEPRPM_VN.length()));
                } else if (line.contains(EEPRPM_IMEI)) {
                    numberHashMap.put(EEPRPM_IMEI, line.substring(EEPRPM_IMEI.length()));
                } else if (line.contains(EEPRPM_MAC)) {
                    numberHashMap.put(EEPRPM_MAC, line.substring(EEPRPM_MAC.length()));
                } else if (line.contains(EEPRPM_MD5)) {
                    numberHashMap.put(EEPRPM_MD5, line.substring(EEPRPM_MD5.length()));
                } else if (line.contains(EEPRPM_TID)) {
                    numberHashMap.put(EEPRPM_TID, line.substring(EEPRPM_TID.length()));
                } else if (line.contains(EEPRPM_EMAC)) {
                    numberHashMap.put(EEPRPM_MAC, line.substring(EEPRPM_EMAC.length()));
                } else if (line.contains(EEPRPM_WMAC)) {
                    numberHashMap.put(EEPRPM_WIFI_MAC, line.substring(EEPRPM_WMAC.length()));
                } else if (line.contains(EEPRPM_WSSN)) {
                    numberHashMap.put(EEPRPM_WSSN, line.substring(EEPRPM_WSSN.length()));
                    //read private
                } else if (line.contains(PRIVATE_SN)) {
                    if (isTextNotEmpty(line.substring(PRIVATE_SN.length()))) {
                        numberHashMap.put(EEPRPM_SN, line.substring(PRIVATE_SN.length()));
                    }
                } else if (line.contains(PRIVATE_WIFI_MAC)) {
                    if (isTextNotEmpty(line.substring(PRIVATE_WIFI_MAC.length()))) {
                        numberHashMap.put(EEPRPM_WIFI_MAC, line.substring(PRIVATE_WIFI_MAC.length()));
                    }
                } else if (line.contains(PRIVATE_PN)) {
                    if (isTextNotEmpty(line.substring(PRIVATE_PN.length()))) {
                        numberHashMap.put(EEPRPM_PN, line.substring(PRIVATE_PN.length()));
                    }
                } else if (line.contains(PRIVATE_VN)) {
                    if (isTextNotEmpty(line.substring(PRIVATE_VN.length()))) {
                        numberHashMap.put(EEPRPM_VN, line.substring(PRIVATE_VN.length()));
                    }
                } else if (line.contains(PRIVATE_IMEI)) {
                    if (isTextNotEmpty(line.substring(PRIVATE_IMEI.length()))) {
                        numberHashMap.put(EEPRPM_IMEI, line.substring(PRIVATE_IMEI.length()));
                    }
                } else if (line.contains(PRIVATE_MAC)) {
                    if (isTextNotEmpty(line.substring(PRIVATE_MAC.length()))) {
                        numberHashMap.put(EEPRPM_MAC, line.substring(PRIVATE_MAC.length()));
                    }
                } else if (line.contains(PRIVATE_MD5)) {
                    if (isTextNotEmpty(line.substring(PRIVATE_MD5.length()))) {
                        numberHashMap.put(EEPRPM_MD5, line.substring(PRIVATE_MD5.length()));
                    }
                } else if (line.contains(PRIVATE_TID)) {
                    if (isTextNotEmpty(line.substring(PRIVATE_TID.length()))) {
                        numberHashMap.put(EEPRPM_TID, line.substring(PRIVATE_TID.length()));
                    }
                } else if (line.contains(PRIVATE_EMAC)) {
                    if (isTextNotEmpty(line.substring(PRIVATE_EMAC.length()))) {
                        numberHashMap.put(EEPRPM_MAC, line.substring(PRIVATE_EMAC.length()));
                    }
                } else if (line.contains(PRIVATE_WMAC)) {
                    if (isTextNotEmpty(line.substring(PRIVATE_WMAC.length()))) {
                        numberHashMap.put(EEPRPM_WIFI_MAC, line.substring(PRIVATE_WMAC.length()));
                    }
                } else if (line.contains(PRIVATE_WSSN)) {
                    if (isTextNotEmpty(line.substring(PRIVATE_WSSN.length()))) {
                        numberHashMap.put(EEPRPM_WSSN, line.substring(PRIVATE_WSSN.length()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return numberHashMap;
    }

    private static String runProcess(String[] args) {
        String result = null;
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read;
            processBuilder.redirectErrorStream();
            process = processBuilder.start();
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inIs != null) {
                    inIs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    private static boolean isTextNotEmpty(String text) {
        return text != null && !"".equals(text.trim());
    }

}
