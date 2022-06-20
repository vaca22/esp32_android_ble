package com.vaca.esp32_android_ble.ble


import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.vaca.esp32_android_ble.MainApplication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver
import java.lang.Thread.sleep

class BleDataWorker {
    private var pool: ByteArray? = null
    private val fileChannel = Channel<Int>(Channel.CONFLATED)
    private val connectChannel = Channel<String>(Channel.CONFLATED)
    var myBleDataManager: BleDataManager? = null
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

    private val comeData = object : BleDataManager.OnNotifyListener {
        override fun onNotify(device: BluetoothDevice?, data: Data?) {
            data?.value?.apply {
                val size = this.size
                Log.e("getit", size.toString())

            }
        }
    }


    fun sendCmd(bs: ByteArray) {
        myBleDataManager?.sendCmd(bs)
    }

    private val connectState = object : ConnectionObserver {
        override fun onDeviceConnecting(device: BluetoothDevice) {

        }

        override fun onDeviceConnected(device: BluetoothDevice) {


        }

        override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {

        }

        override fun onDeviceReady(device: BluetoothDevice) {

        }

        override fun onDeviceDisconnecting(device: BluetoothDevice) {

        }

        override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
                Log.e("gaga","disconnect");
        }

    }


    fun initWorker(context: Context, bluetoothDevice: BluetoothDevice?) {
        bluetoothDevice?.let {
            myBleDataManager?.connect(it)
                ?.useAutoConnect(false)
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
        myBleDataManager?.disconnect()?.enqueue()

    }

    init {
        myBleDataManager = BleDataManager(MainApplication.application)
        myBleDataManager?.setNotifyListener(comeData)
        myBleDataManager?.setConnectionObserver(connectState)
    }

}