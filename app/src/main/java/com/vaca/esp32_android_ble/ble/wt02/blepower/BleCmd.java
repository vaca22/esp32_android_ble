package com.vaca.esp32_android_ble.ble.wt02.blepower;


import static com.vaca.esp32_android_ble.ble.wt02.utils.CRCUtils.calCRC8;

public class BleCmd {

    private static int ACTIVATE = 0xA1;
    private static int SYNCDATA = 0xA4;
    private static int CLEARDATA = 0xA7;
    private static int ENTERTEST = 0xA5;
    private static int GETBAT = 0xA2;
    private static int CHANGEMODE = 0xA3;

    private static int seqNo = 0;

    private static void addNo() {
      //  seqNo++;
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

    public static byte[] activateX(boolean b) {
        int len = 1;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) 0xB1;
        cmd[2] = (byte) ~0xB1;
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
        int len = 0;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) SYNCDATA;
        cmd[2] = (byte) ~SYNCDATA;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 0;
        cmd[5] = calCRC8(cmd);
        addNo();
        return cmd;
    }

    public static byte[] syncDataX() {
        int len = 8;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) 0xB4;
        cmd[2] = (byte) ~0xB4;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) len;
        cmd[5] = (byte) 1;
        cmd[6] = (byte) 2;
        cmd[7] = (byte) 3;
        cmd[8] = (byte) 4;
        cmd[9] = (byte) 5;
        cmd[10] = (byte) 6;
        cmd[11] = (byte) 7;
        cmd[12] = (byte) 8;
        cmd[13] = calCRC8(cmd);
        addNo();
        return cmd;
    }



    public static byte[] clearData() {
        int len = 0;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) CLEARDATA;
        cmd[2] = (byte) ~CLEARDATA;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 0;
        cmd[5] = calCRC8(cmd);
        addNo();
        return cmd;
    }



    public static byte[] clearDataX() {
        int len = 0;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) 0xB7;
        cmd[2] = (byte) ~0xB7;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 0;
        cmd[5] = calCRC8(cmd);
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


    public static byte[] enterTestX() {
        int len = 0;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) 0xB5;
        cmd[2] = (byte) ~0xB5;
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


    public static byte[] getBatX() {
        int len = 1;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) 0xB2;
        cmd[2] = (byte) ~0xB2;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 1;
        cmd[5] = (byte) 0x4C;
        cmd[6] = calCRC8(cmd);
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


    public static byte[] changeModeX(int a) {
        int len = 1;
        byte[] cmd = new byte[6 + len];
        cmd[0] = (byte) 0xCA;
        cmd[1] = (byte) 0xB3;
        cmd[2] = (byte) ~0xB3;
        cmd[3] = (byte) seqNo;
        cmd[4] = (byte) 1;
        cmd[5] = (byte) a;
        cmd[6] = calCRC8(cmd);
        addNo();
        return cmd;
    }


}
