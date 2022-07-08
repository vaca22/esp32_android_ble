package com.vaca.esp32_android_ble.ble

import com.vaca.esp32_android_ble.BleScanManager
import com.vaca.esp32_android_ble.ble.er2.blepower.Er2BleDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*

object BleServer {
    val dataScope = CoroutineScope(Dispatchers.IO)
    val scan = BleScanManager()
    val ER_2_BLE_DATA_WORKER: Er2BleDataWorker = Er2BleDataWorker()
    var er2ConnectFlag = false
    val waveDataX = LinkedList<Float>()



}
