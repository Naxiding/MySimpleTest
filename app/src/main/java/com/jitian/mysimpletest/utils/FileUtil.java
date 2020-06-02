package com.jitian.mysimpletest.utils;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author YangDing
 * @date 2019/10/30
 */
public class FileUtil {

    private static final String BLUETOOTH_PARENT_PATH = "/bluetooth";
    private static final String FILE_END = ".pcm";

    public static String saveDataToPcmFile(ArrayList<byte[]> resultData, Context context) {
        BufferedOutputStream bufferedOutputStream = null;
        File targetFile = createNewPcmFile(context);
        if (targetFile != null && targetFile.canWrite()) {
            try {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
                bufferedOutputStream.write(mergeBytes(resultData));
                bufferedOutputStream.flush();
                return targetFile.getPath();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedOutputStream != null) {
                    try {
                        bufferedOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    private static byte[] mergeBytes(ArrayList<byte[]> values) {
        if (values == null) {
            throw new NullPointerException();
        }
        int length = 0;
        for (byte[] bytes : values) {
            length += bytes.length;
        }
        byte[] bytesAll = new byte[length];
        int countLength = 0;
        for (byte[] b : values) {
            System.arraycopy(b, 0, bytesAll, countLength, b.length);
            countLength += b.length;
        }
        return bytesAll;
    }

    private static File createNewPcmFile(Context context) {
        File root = context.getObbDir();
        if (root != null && root.canWrite()) {
            String targetFileDir = root.getPath() + FileUtil.BLUETOOTH_PARENT_PATH;
            File targetDir = new File(targetFileDir);
            if (!targetDir.exists()) {
                targetDir.mkdir();
            }
            String targetFilePath = root.getPath() + FileUtil.BLUETOOTH_PARENT_PATH + File.separator + System.currentTimeMillis() + FILE_END;
            File targetFile = new File(targetFilePath);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            try {
                if (targetFile.createNewFile()) {
                    return targetFile;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
