package com.vaca.esp32_android_ble.ble.er2.blething;




import static com.vaca.esp32_android_ble.ble.er2.utils.CRCUtils.calCRC8;

import java.util.Date;

public class Er2BleCmd {
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

    private static byte[] getReq(byte cmd, byte pkgNo, byte[] data) {
       Er2RequestPkg requestPkg = new Er2RequestPkg();
        requestPkg.setCmd(cmd)
                .setPkgNo(pkgNo)
                .setData(data)
                .build();
        return requestPkg.getBuf();
    }

    public static byte[] setTime() {
        TimeData timeData = new TimeData(new Date());
        addNo();
        return getReq(CMD_SET_TIME, (byte) seqNo, timeData.convert2Data());
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


    public static byte[] getPara() {
        int len = 0;
        byte[] cmd = new byte[8 + len];
        cmd[0] = (byte) 0xA5;
        cmd[1] = (byte) ER2_CMD_GET_PARA;
        cmd[2] = (byte) ~ER2_CMD_GET_PARA;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) seqNo;
        cmd[5] = (byte) 0;
        cmd[6] = (byte) 0;
        cmd[7] = calCRC8(cmd);
        addNo();
        return cmd;
    }


    public static byte[] setPara(boolean b) {
        int len = 5;
        byte[] cmd = new byte[8 + len];
        cmd[0] = (byte) 0xA5;
        cmd[1] = (byte) ER2_CMD_SET_PARA;
        cmd[2] = (byte) ~ER2_CMD_SET_PARA;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) seqNo;
        cmd[5] = (byte) 0x05;
        cmd[6] = (byte) 0;
        if (b) {
            cmd[7] = (byte) 0x01;
        } else {
            cmd[7] = (byte) 0x00;
        }
        cmd[8] = (byte) 0;
        cmd[9] = (byte) 0;
        cmd[10] = (byte) 0;
        cmd[11] = (byte) 0;
        cmd[12] = calCRC8(cmd);
        addNo();
        return cmd;
    }


    public static byte[] getInfo() {
        int len = 0;

        byte[] cmd = new byte[8 + len];
        cmd[0] = (byte) 0xA5;
        cmd[1] = (byte) ER2_CMD_GET_INFO;
        cmd[2] = (byte) ~ER2_CMD_GET_INFO;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) seqNo;
        cmd[5] = (byte) 0;
        cmd[6] = (byte) 0;
        cmd[7] = calCRC8(cmd);

        addNo();

        return cmd;
    }


    public static byte[] getRtData() {
        int len = 1;

        byte[] cmd = new byte[8 + len];
        cmd[0] = (byte) 0xA5;
        cmd[1] = (byte) ER2_CMD_RT_DATA;
        cmd[2] = (byte) ~ER2_CMD_RT_DATA;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) seqNo;
        cmd[5] = (byte) 0x01;
        cmd[6] = (byte) 0x00;
        cmd[7] = (byte) 0x7D;  // 0 -> 125hz;  1-> 62.5hz
        cmd[8] = calCRC8(cmd);

        addNo();
        return cmd;
    }


    public static byte[] getFileList() {
        int len = 0;

        byte[] cmd = new byte[8 + len];
        cmd[0] = (byte) 0xA5;
        cmd[1] = (byte) ER2_CMD_READ_FILE_LIST;
        cmd[2] = (byte) ~ER2_CMD_READ_FILE_LIST;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) seqNo;
        cmd[5] = (byte) 0x00;
        cmd[6] = (byte) 0x00;
        cmd[7] = calCRC8(cmd);
        addNo();
        return cmd;
    }

    public static byte[] readFileStart(byte[] name, int offset) {
        int len = 20;

        byte[] cmd = new byte[8 + len];
        cmd[0] = (byte) 0xA5;
        cmd[1] = (byte) ER2_CMD_READ_FILE_START;
        cmd[2] = (byte) ~ER2_CMD_READ_FILE_START;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) seqNo;
        cmd[5] = (byte) 0x14;
        cmd[6] = (byte) 0x00;
        int k = 0;
        for (k = 0; k < 16; k++) {
            cmd[7 + k] = name[k];
        }
        byte[] temp = intToByteArray(offset);
        for (k = 0; k < 4; k++) {
            cmd[23 + k] = temp[k];
        }
        cmd[27] = calCRC8(cmd);
        addNo();
        return cmd;
    }


    public static byte[] readFileData(int addr_offset) {
        int len = 4;
        byte[] cmd = new byte[8 + len];
        cmd[0] = (byte) 0xA5;
        cmd[1] = (byte) ER2_CMD_READ_FILE_DATA;
        cmd[2] = (byte) ~ER2_CMD_READ_FILE_DATA;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) seqNo;
        cmd[5] = (byte) 0x04;
        cmd[6] = (byte) 0x00;
        int k;
        byte[] temp = intToByteArray(addr_offset);
        for (k = 0; k < 4; k++) {
            cmd[7 + k] = temp[k];
        }

        cmd[11] = calCRC8(cmd);
        addNo();
        return cmd;
    }

    public static byte[] readFileEnd() {
        int len = 0;
        byte[] cmd = new byte[8 + len];
        cmd[0] = (byte) 0xA5;
        cmd[1] = (byte) ER2_CMD_READ_FILE_END;
        cmd[2] = (byte) ~ER2_CMD_READ_FILE_END;
        cmd[3] = (byte) 0x00;
        cmd[4] = (byte) seqNo;
        cmd[5] = (byte) 0x00;
        cmd[6] = (byte) 0x00;
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
