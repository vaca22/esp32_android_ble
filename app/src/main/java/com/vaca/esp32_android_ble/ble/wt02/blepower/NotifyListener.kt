package com.vaca.esp32_android_ble.ble.wt02.blepower

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.data.Data

/**
 * author: wujuan
 * created on: 2021/1/21 14:42
 * description:
 */
internal interface NotifyListener {

    fun onNotify(device: BluetoothDevice, data: Data)

}