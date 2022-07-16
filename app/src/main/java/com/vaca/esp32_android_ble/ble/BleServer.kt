package com.vaca.esp32_android_ble.ble

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData
import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.ble.er2.blepower.Er2BleDataWorker
import com.viatom.littlePu.er2.view.WaveView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*

object BleServer {
    val dataScope = CoroutineScope(Dispatchers.IO)
    val scan = BleScanManager()
    val er2_worker: Er2BleDataWorker = Er2BleDataWorker()
    var er2ConnectFlag = false
    val waveDataX = LinkedList<Float>()
    var textInfo="";
    var textTotal=MutableLiveData<String>()
    val bleState=MutableLiveData<String>()

    var drawTask: WaveView.Companion.DrawTask? = null

    val er2Graph = MutableLiveData<Boolean>()

   fun connect(b: BluetoothDevice){
       er2_worker.initWorker(MainApplication.application,b)
   }

}
