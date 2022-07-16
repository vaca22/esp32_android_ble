package com.vaca.esp32_android_ble.ble

import android.bluetooth.BluetoothDevice

data class BleBean(var name: String, var bluetoothDevice: BluetoothDevice,var addr:String,var rssi:Int,var span: Boolean =false)
