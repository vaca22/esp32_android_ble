package com.vaca.esp32_android_ble.ble.er2.blepower


import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.ble.BleServer
import com.viatom.littlePu.er2.blepower.NotifyListener
import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.callback.InvalidRequestCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList


class Er2BleDataWorker {
    private var myEr2BleDataManager: Er2BleDataManagerER2? = null
    private var pool: ByteArray? = null

    fun byteArray2String(byteArray: ByteArray): String {
        var fuc = ""
        for (b in byteArray) {
            val st = String.format("%02X", b)
            fuc += ("$st  ");
        }
        return fuc
    }


    data class ReceiveData(val n1: Double, val n2: Double)

    val waveData = ArrayList<Double>()


    private val comeData = object : NotifyListener {
        override fun onNotify(device: BluetoothDevice, data: Data) {
            data.value?.run {

                val a = String(this, Charset.forName("gb2312"));

                if (a.contains("请输入OKx")) {
                    BleServer.er2_worker.sendCmd("OKx".toByteArray())
                } else if (a.contains("请输入RE基准电压")) {
                    BleServer.er2_worker.sendCmd("600x".toByteArray())
                } else if (a.contains("请输入起始电压")) {
                    BleServer.er2_worker.sendCmd("-200x".toByteArray())
                } else if (a.contains("请输入终止电压")) {
                    BleServer.er2_worker.sendCmd("600x".toByteArray())
                } else if (a.contains("请输入阶梯步进")) {
                    BleServer.er2_worker.sendCmd("5x".toByteArray())
                } else if (a.contains("请输入脉冲电压")) {
                    BleServer.er2_worker.sendCmd("50x".toByteArray())
                } else if (a.contains("请输入周期时间")) {
                    BleServer.er2_worker.sendCmd("100x".toByteArray())
                } else if (a.contains("参数设置完成")) {
                    Log.e("gaga", "done")
                    waveData.clear()
                } else {
                    pool = com.viatom.littlePu.utils.add(pool, this)
                    // Log.e("gagax",a)
                    pool?.apply {
                        pool = handleDataPool(pool)
                    }
                }

                if(a.contains("扫描结束")){
                    val size=waveData.size/2;
                    val a=DoubleArray(size){
                        waveData[it*2]
                    }
                    val b=DoubleArray(size){
                        waveData[it*2+1]
                    }
                    val c=DoubleArray(size){
                        (b[it]-a[it])
                    }

                    val size2=waveData.size/4;

                    //-----------------Delta I
                    val d=DoubleArray(size2){
                        c[it*2+1]-c[it*2]
                    }

                    //-----------------奇数行电压
                    val e=DoubleArray(size2){
                        a[it*2]
                    }


                    for(k in d.indices){
                        Log.e("gaga","${d[k]}   ${e[k]}")
                    }


                        Log.e("ghgh",waveData.size.toString())

                }


            }
        }

    }


    private fun handleDataPool(bytes: ByteArray?): ByteArray? {

        if (bytes == null) {
            return null
        }
        var bytesLeft = bytes
        if (bytes.size == 0) {
            return bytes
        }


        var hasA = true
        while (hasA){
            hasA=false
            for (k in bytesLeft!!.indices) {
                if ((bytesLeft[k] == 0x20.toByte()).or(bytesLeft!![k] == 0x0A.toByte())) {
                    try {
                        waveData.add(String(bytesLeft.copyOfRange(0,k)).toDouble())
                    }catch (e:Exception){
                        return null
                    }

                    if(k+1>=bytesLeft.size){
                        bytesLeft=null
                        hasA=false
                    }else{
                        bytesLeft=bytesLeft.copyOfRange(k+1,bytesLeft.size)
                        hasA=true
                    }
                    break;
                }
            }
        }




        return bytesLeft
    }

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