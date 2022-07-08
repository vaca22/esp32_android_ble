package com.vaca.esp32_android_ble.ble

import android.bluetooth.BluetoothDevice
import com.vaca.esp32_android_ble.BleScanManager
import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.ble.er2.blepower.Er2BleDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*

object BleServer {
    val dataScope = CoroutineScope(Dispatchers.IO)
    val scan = BleScanManager()
    val er2_worker: Er2BleDataWorker = Er2BleDataWorker()
    var er2ConnectFlag = false
    val waveDataX = LinkedList<Float>()



   fun connect(b: BluetoothDevice){
       er2_worker.initWorker(MainApplication.application,b)
   }

}
