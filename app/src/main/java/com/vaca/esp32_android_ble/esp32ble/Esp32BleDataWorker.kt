package com.vaca.esp32_android_ble.esp32ble


import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.fragment.SecondFragment

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver

class Esp32BleDataWorker {
    private var pool: ByteArray? = null
    private val fileChannel = Channel<Int>(Channel.CONFLATED)
    private val connectChannel = Channel<String>(Channel.CONFLATED)
    var myEsp32BleDataManager: Esp32BleDataManager? = null
    private val dataScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    private var cmdState = 0;
    var pkgTotal = 0;
    var currentPkg = 0;
    var fileData: ByteArray? = null
    var currentFileName = ""
    var result = 1;
    var currentFileSize = 0
    var lastMill = 0L
    var startMill = 0L


    companion object {
        val fileProgressChannel = Channel<FileProgress>(Channel.CONFLATED)
    }

    data class FileProgress(
        var name: String = "",
        var progress: Int = 0,
        var success: Boolean = false
    )

    private val comeData = object : Esp32BleDataManager.OnNotifyListener {
        override fun onNotify(device: BluetoothDevice?, data: Data?) {
            data?.value?.apply {
                val size = this.size
                Log.e("getit", size.toString())

            }
        }
    }


    fun sendCmd(bs: ByteArray) {
        myEsp32BleDataManager?.sendCmd(bs)
    }

    private val connectState = object : ConnectionObserver {
        override fun onDeviceConnecting(device: BluetoothDevice) {
            SecondFragment.bleStu.postValue("蓝牙连接中")
        }

        override fun onDeviceConnected(device: BluetoothDevice) {


        }

        override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {

        }

        override fun onDeviceReady(device: BluetoothDevice) {
            SecondFragment.bleStu.postValue("蓝牙已连接")
        }

        override fun onDeviceDisconnecting(device: BluetoothDevice) {

        }

        override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
            SecondFragment.bleStu.postValue("蓝牙已断开")
                Log.e("gaga","disconnect");
        }

    }


    fun initWorker(context: Context, bluetoothDevice: BluetoothDevice?) {
        bluetoothDevice?.let {
            myEsp32BleDataManager?.connect(it)
                ?.useAutoConnect(true)
                ?.timeout(10000)
                ?.retry(10, 200)
                ?.done {
                    Log.i("BLE", "连接成功了.>>.....>>>>")


                }?.fail(object : FailCallback {
                    override fun onRequestFailed(device: BluetoothDevice, status: Int) {

                    }

                })
                ?.enqueue()
        }
    }

    suspend fun waitConnect() {
        connectChannel.receive()
    }

    fun disconnect() {
        myEsp32BleDataManager?.disconnect()?.enqueue()

    }

    init {
        myEsp32BleDataManager = Esp32BleDataManager(MainApplication.application)
        myEsp32BleDataManager?.setNotifyListener(comeData)
        myEsp32BleDataManager?.setConnectionObserver(connectState)
    }

}