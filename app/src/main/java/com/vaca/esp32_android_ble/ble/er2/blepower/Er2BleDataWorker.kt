package com.vaca.esp32_android_ble.ble.er2.blepower


import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.vaca.esp32_android_ble.DateStringUtil
import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.PathUtil
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.ble.er2.utils.add

import com.viatom.littlePu.er2.blepower.NotifyListener

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.callback.InvalidRequestCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset
import kotlin.collections.ArrayList


class Er2BleDataWorker {
    private var myEr2BleDataManager: Er2BleDataManagerER2? = null
    private var pool: ByteArray? = null

    companion object{
        var Vbias="600"
        var Vlow="-200"
        var Vhigh="600"
        var Vstep="5"
        var Vpulse="50"
        var Tstep="100"
        val pointData=ArrayList<Pf>()
    }

    fun byteArray2String(byteArray: ByteArray): String {
        var fuc = ""
        for (b in byteArray) {
            val st = String.format("%02X", b)
            fuc += ("$st  ");
        }
        return fuc
    }


    data class ReceiveData(val n1: Double, val n2: Double)
    data class Pf(val x:Double,val y:Double)

    val waveData = ArrayList<Double>()



    private val comeData = object : NotifyListener {
        override fun onNotify(device: BluetoothDevice, data: Data) {
            data.value?.run {



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
        myEr2BleDataManager?.sendCmd(bs)
    }


    fun initWorker(context: Context, bluetoothDevice: BluetoothDevice?) {
        bluetoothDevice?.let {
            if (myEr2BleDataManager == null) {
                myEr2BleDataManager = Er2BleDataManagerER2(MainApplication.application)
                myEr2BleDataManager?.setNotifyListener(comeData)
                myEr2BleDataManager?.setConnectionObserver(connectState)
            }
            myEr2BleDataManager?.disconnect()?.enqueue();


            myEr2BleDataManager?.connect(it)
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
        myEr2BleDataManager?.disconnect()?.enqueue()
        BleServer.er2ConnectFlag = false
    }


}