package com.vaca.esp32_android_ble.ble.er2.blepower


import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log


import com.vaca.esp32_android_ble.ble.BleServer

import com.vaca.esp32_android_ble.MainApplication




import com.viatom.littlePu.er2.blepower.NotifyListener


import com.vaca.esp32_android_ble.ble.er2.blething.Er2BleResponse


import com.viatom.littlePu.utils.toUInt
import com.viatom.littlePu.utils.unsigned

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.callback.InvalidRequestCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.experimental.inv


class Er2BleDataWorker {


    private var myEr2BleDataManager: Er2BleDataManagerER2? = null
    private val dataScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()





    fun byteArray2String(byteArray: ByteArray): String {
        var fuc = ""
        for (b in byteArray) {
            val st = String.format("%02X", b)
            fuc += ("$st  ");
        }
        return fuc
    }
    private val comeData = object : NotifyListener {
        override fun onNotify(device: BluetoothDevice, data: Data) {
            data.value?.run {

                val a= String(this, Charset.forName("gb2312"));
                Log.e("bleReceive",a)
            }
        }

    }


    var first=true

    private val connectState = object : ConnectionObserver {
        override fun onDeviceConnecting(device: BluetoothDevice) {
            Log.e("dada1", "dada4")
        }

        override fun onDeviceConnected(device: BluetoothDevice) {

            BleServer.er2ConnectFlag = true
            Log.e("dada1", "dada5")


        }

        override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {

            Log.e("dada1", "dada6")
            BleServer.er2ConnectFlag = false



        }

        override fun onDeviceReady(device: BluetoothDevice) {


        }

        override fun onDeviceDisconnecting(device: BluetoothDevice) {


        }

        override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {

            BleServer.er2ConnectFlag = false


        }

    }





     fun sendCmd(bs: ByteArray) {
        myEr2BleDataManager?.sendCmd(bs)
    }


    fun initWorker(context: Context, bluetoothDevice: BluetoothDevice?) {
        bluetoothDevice?.let {
            if (myEr2BleDataManager == null) {
                myEr2BleDataManager = Er2BleDataManagerER2(MainApplication.application)
                myEr2BleDataManager?.setNotifyListener(comeData)
                myEr2BleDataManager?.setConnectionObserver(connectState)
            }

            myEr2BleDataManager?.connect(it)
                ?.useAutoConnect(true)
                ?.retry(1000, 100)
                ?.invalid(object : InvalidRequestCallback {
                    override fun onInvalidRequest() {
                        Log.e("dada1", "dada1")
                        BleServer.er2ConnectFlag = false


                    }

                })
                ?.done {


                }?.fail(object : FailCallback {
                    override fun onRequestFailed(device: BluetoothDevice, status: Int) {
                        Log.e("dada1", "dada3")
                        BleServer.er2ConnectFlag = false

                    }

                })
                ?.enqueue()

        }
    }


    fun disconnect() {
        myEr2BleDataManager?.disconnect()?.enqueue()
        BleServer.er2ConnectFlag = false
    }






}