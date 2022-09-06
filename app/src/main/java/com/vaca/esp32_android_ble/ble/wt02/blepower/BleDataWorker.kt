package com.vaca.esp32_android_ble.ble.wt02.blepower


import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.fragment.DashboardFragment

import com.viatom.littlePu.er2.blepower.NotifyListener

import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.callback.InvalidRequestCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver


class BleDataWorker {
    private var myBleDataManager: BleDataManager? = null
    private var pool: ByteArray? = null


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
                Log.e("gaga",this.size.toString())


            }
        }

    }



    private val connectState = object : ConnectionObserver {
        override fun onDeviceConnecting(device: BluetoothDevice) {
            Log.e("dada1", "dada4")
            BleServer.bleState.postValue("状态：蓝牙连接中")
        }

        override fun onDeviceConnected(device: BluetoothDevice) {

            BleServer.er2ConnectFlag = true
            Log.e("dada1", "dada5")

            BleServer.bleState.postValue("状态：蓝牙已连接")


        }

        override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {

            Log.e("dada1", "dada6")
            BleServer.er2ConnectFlag = false
            BleServer.bleState.postValue("状态：蓝牙连接失败")


        }

        override fun onDeviceReady(device: BluetoothDevice) {

            BleServer.bleState.postValue("状态：蓝牙已连接")
        }

        override fun onDeviceDisconnecting(device: BluetoothDevice) {
            BleServer.bleState.postValue("状态：蓝牙断开中")

        }

        override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
            BleServer.bleState.postValue("状态：蓝牙已断开")

            BleServer.er2ConnectFlag = false


        }

    }


    fun sendCmd(bs: ByteArray) {

        val gg=byteArray2String(bs)
        DashboardFragment.currentCmd.postValue(gg)
        Log.e("sendCmd",gg)
        myBleDataManager?.sendCmd(bs)
    }


    fun initWorker(context: Context, bluetoothDevice: BluetoothDevice?) {
        bluetoothDevice?.let {
            if (myBleDataManager == null) {
                myBleDataManager = BleDataManager(MainApplication.application)
                myBleDataManager?.setNotifyListener(comeData)
                myBleDataManager?.setConnectionObserver(connectState)
            }
            myBleDataManager?.disconnect()?.enqueue();


            myBleDataManager?.connect(it)
                ?.useAutoConnect(true)
                ?.retry(10, 100)
                ?.timeout(10000)
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
        myBleDataManager?.disconnect()?.enqueue()
        BleServer.er2ConnectFlag = false
    }


}