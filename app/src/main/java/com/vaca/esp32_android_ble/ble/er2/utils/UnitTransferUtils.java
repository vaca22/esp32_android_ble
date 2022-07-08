package com.vaca.esp32_android_ble.ble.er2.utils;

import android.content.Context;

/**
 * Created by dds on 2019/1/2.
 * android_shuai@163.com
 */
public class UnitTransferUtils {
    public static double short2mv(short s) {
        return (s * (1.0035 * 1800) / (4096 * 178.74));
    }

    public static short byte2short(byte a, byte b) {
        if (a == (byte) 0xff && b == (byte) 0x7f)
            return 0;

        return (short) ((a & 0xFF) | (b << 8));
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
