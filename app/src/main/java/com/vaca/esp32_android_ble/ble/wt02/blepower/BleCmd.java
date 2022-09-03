package com.vaca.esp32_android_ble.ble.wt02.blepower;


import static com.vaca.esp32_android_ble.ble.wt02.utils.CRCUtils.calCRC8;

public class BleCmd {

    public static int ER2_RESET = 0xE3;
    private static int seqNo = 0;


    private static void addNo() {
        seqNo++;
        if (seqNo >= 255) {
            seqNo = 0;
        }
    }


    public static byte[] reset() {
        int len = 0;
        byte[] cmd = new byte[8 + len];
        cmd[0] = (byte) 0xA5;
        cmd[1] = (byte) ER2_RESET;
        cmd[2] = (byte) ~ER2_RESET;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) seqNo;
        cmd[5] = (byte) 0;
        cmd[6] = (byte) 0;
        cmd[7] = calCRC8(cmd);
        addNo();
        return cmd;
    }




    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[3] = (byte) ((i >> 24) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }
}
