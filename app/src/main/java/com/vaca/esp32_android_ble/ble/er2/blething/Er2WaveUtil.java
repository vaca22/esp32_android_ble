package com.vaca.esp32_android_ble.ble.er2.blething;


public class Er2WaveUtil {
    public static float byteTomV(byte a, byte b) {
        if (a == (byte) 0xff && b == (byte) 0x7f)
            return 0f;

        int n = ((a & 0xFF) | (short) (b << 8));

//        float mv = (float) (n*12.7*1800*1.03)/(10*227*4096);
        float mv = (float) (n * (1.0035 * 1800) / (4096 * 178.74));
//        float mv = (float) (n * 0.002467);

        return mv;
    }
}
