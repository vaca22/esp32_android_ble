package com.vaca.esp32_android_ble.ble.wt02.blepower;


import static com.vaca.esp32_android_ble.ble.wt02.utils.CRCUtils.calCRC8;

public class BleCmd {
    public final static byte CMD_SET_TIME = (byte) 0xEC;
    public final static int CMD_SET_TIME2 = 0xEC;
    public static int ER2_RESET = 0xE3;
    public static int ER2_CMD_GET_INFO = 0xE1;
    public static int ER2_CMD_RT_DATA = 0x03;
    public static int ER2_CMD_READ_FILE_LIST = 0xF1;
    public static int ER2_CMD_READ_FILE_START = 0xF2;
    public static int ER2_CMD_READ_FILE_DATA = 0xF3;
    public static int ER2_CMD_READ_FILE_END = 0xF4;
    public static int ER2_CMD_GET_PARA = 0x00;
    public static int ER2_CMD_SET_PARA = 0x04;
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
