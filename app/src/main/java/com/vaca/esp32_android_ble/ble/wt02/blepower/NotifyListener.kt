package com.vaca.esp32_android_ble.ble.wt02.blepower

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.data.Data


internal interface NotifyListener {

    fun onNotify(device: BluetoothDevice, data: Data)

}