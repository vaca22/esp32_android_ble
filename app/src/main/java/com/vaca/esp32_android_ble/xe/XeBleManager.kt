package com.example.smart_xe_gimble.xe

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*

object XeBleManager {

    val dataScope = CoroutineScope(Dispatchers.IO)
    //    ServiceUuid
    val serviceUUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")

    //write uuid with no response
    val writeUUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")

    //notify uuid
    val notifyUUID = UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb")

    // descriptor uuid
    val DesUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    lateinit var writeCharacteristic: BluetoothGattCharacteristic
    lateinit var notifyCharacteristic: BluetoothGattCharacteristic
    lateinit var descriptor: BluetoothGattDescriptor
    lateinit var gatt:BluetoothGatt


    @SuppressLint("MissingPermission")
    fun writeData(data: ByteArray) {
        writeCharacteristic.value = data
        gatt.writeCharacteristic(writeCharacteristic)
     //   gatt.writeCharacteristic(writeCharacteristic, data,BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
    }


    fun dataStatus(bytes: ByteArray?): Int {
        return if (bytes != null && bytes.size != 0 && bytes.size >= 4) {
            bytes[3].toInt()
        } else (-30001 as Short).toInt()
    }


    fun m20029c0(data: ByteArray?): String? {
        if (data != null && data.size != 0) {
            val m20070f: Int = dataStatus(data)
            if (data[0].toInt() == 85 && m20070f == 81) {
                return "系统状态查询"
            }
            if (data[0].toInt() == 85 && m20070f == 16) {
                return "云台姿态查询"
            }
            if (data[0].toInt() == 85 && m20070f == 34) {
                return "轨迹模式命令"
            }
            if (data[0].toInt() == 85 && m20070f == 35) {
                return "中心跟随命令"
            }
            if (data[0].toInt() == 85 && m20070f == 18) {
                return "扩展模式通知"
            }
            if (data[0].toInt() == 85 && m20070f == 17) {
                return "姿态设置"
            }
            if (data[0].toInt() == 85 && m20070f == -122) {
                return "滚轮"
            }
            if (data[0].toInt() == 85 && m20070f == -124) {
                return "按钮"
            }
            if (data[0].toInt() == 85 && m20070f == -127) {
                return "电量"
            }
            if (data[0].toInt() == 85 && m20070f == 80) {
                return "版本查询"
            }
            if (data[0].toInt() == 85 && m20070f == 113) {
                return "固件升级准备命令"
            }
            if (data[0].toInt() == 85 && m20070f == 114) {
                return "固件升级开始"
            }
            if (data[0].toInt() == 85 && m20070f == 115) {
                return "固件升级数据A"
            }
            if (data[0].toInt() == 85 && m20070f == 116) {
                return "固件升级完成"
            }
            if (data[0].toInt() == 85 && m20070f == 117) {
                return "固件升级数据B"
            }
            if (data[0].toInt() == 85 && m20070f == 118) {
                return "机架版本查询"
            }
            if (data[0].toInt() == 85 && m20070f == 69) {
                return "控制板重启"
            }
            if (data[0].toInt() == 85 && m20070f == 82) {
                return "开关机命令"
            }
            if (data[0].toInt() == 85 && m20070f == 0) {
                return "确认命令"
            }
            if (data[0].toInt() == 85 && m20070f == 1) {
                return "否认命令"
            }
            if (data[0].toInt() == 85 && m20070f == 97) {
                return "云台模式"
            }
            if (data[0].toInt() == 85 && m20070f == 49) {
                return "盗梦空间"
            }
            if (data[0].toInt() == 85 && m20070f == 23) {
                return "跟随参数读取"
            }
            if (data[0].toInt() == 85 && m20070f == 25) {
                return "跟随参数设置"
            }
            if (data[0].toInt() == 85 && m20070f == 26) {
                return "控制参数读取"
            }
            if (data[0].toInt() == 85 && m20070f == 27) {
                return "控制参数设置"
            }
            if (data[0].toInt() == 85 && m20070f == 39) {
                return "电机零位置偏移读取"
            }
            if (data[0].toInt() == 85 && m20070f == 40) {
                return "电机零位置偏移设置"
            }
            if (data[0].toInt() == 85 && m20070f == 21) {
                return "姿态偏移读取"
            }
            if (data[0].toInt() == 85 && m20070f == 22) {
                return "姿态偏移设置"
            }
            if (data[0].toInt() == 85 && m20070f == 48) {
                return "回中"
            }
        }
        return "未知功能码"
    }

}