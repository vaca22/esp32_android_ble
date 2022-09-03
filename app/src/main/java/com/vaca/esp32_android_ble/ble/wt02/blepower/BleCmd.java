package com.vaca.esp32_android_ble.ble.wt02.blepower;


import static com.vaca.esp32_android_ble.ble.wt02.utils.CRCUtils.calCRC8;

public class BleCmd {

    public static int ACTIVATE = 0xA1;
    public static int SYNCDATA = 0xA4;
    public static int ENTERTEST = 0xA5;
    public static int GETBAT = 0xA2;
    public static int CHANGEMODE = 0xA3;

    private static int seqNo = 0;

    private static void addNo() {
        seqNo++;
        if (seqNo >= 255) {
            seqNo = 0;
        }
    }


    public static byte[] activate(boolean b) {
        int len = 1;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) ACTIVATE;
        cmd[2] = (byte) ~ACTIVATE;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 1;
        if(b){
            cmd[5] = (byte) 1;
        }else{
            cmd[5] = (byte) 0;
        }
        cmd[6] = calCRC8(cmd);
        addNo();
        return cmd;
    }

    public static byte[] syncData() {
        int len = 1;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) SYNCDATA;
        cmd[2] = (byte) ~SYNCDATA;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 1;
        cmd[5] = (byte) 1;
        cmd[6] = calCRC8(cmd);
        addNo();
        return cmd;
    }


    public static byte[] clearData() {
        int len = 1;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) SYNCDATA;
        cmd[2] = (byte) ~SYNCDATA;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 0;
        cmd[5] = (byte) 1;
        cmd[6] = calCRC8(cmd);
        addNo();
        return cmd;
    }

    public static byte[] enterTest() {
        int len = 0;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) ENTERTEST;
        cmd[2] = (byte) ~ENTERTEST;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 0;
        cmd[5] = calCRC8(cmd);
        addNo();
        return cmd;
    }

    public static byte[] getBat() {
        int len = 0;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) GETBAT;
        cmd[2] = (byte) ~GETBAT;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 0;
        cmd[5] = calCRC8(cmd);
        addNo();
        return cmd;
    }

    public static byte[] changeMode(int a) {
        int len = 1;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) CHANGEMODE;
        cmd[2] = (byte) ~CHANGEMODE;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 1;
        cmd[5] = (byte) a;
        cmd[6] = calCRC8(cmd);
        addNo();
        return cmd;
    }


}
