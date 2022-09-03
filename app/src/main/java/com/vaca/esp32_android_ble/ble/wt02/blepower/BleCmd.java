package com.vaca.esp32_android_ble.ble.wt02.blepower;


import static com.vaca.esp32_android_ble.ble.wt02.utils.CRCUtils.calCRC8;

public class BleCmd {

    public static int ACTIVATE = 0xA1;

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





}
