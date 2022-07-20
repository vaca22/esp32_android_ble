package com.vaca.esp32_android_ble.ble.er2.blepower


import android.bluetooth.BluetoothDevice
import android.content.Context
import android.graphics.PointF
import android.util.Log
import com.vaca.esp32_android_ble.DateStringUtil
import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.PathUtil
import com.vaca.esp32_android_ble.ble.BleServer
import com.vaca.esp32_android_ble.fragment.SettingFragment
import com.viatom.littlePu.er2.blepower.NotifyListener
import com.viatom.littlePu.er2.view.WaveView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.callback.FailCallback
import no.nordicsemi.android.ble.callback.InvalidRequestCallback
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.observer.ConnectionObserver
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*
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

                val a = String(this, Charset.forName("gb2312"));
                Log.e("gaga",a)
                BleServer.textInfo+=a
             //   BleServer.textTotal.postValue(BleServer.textInfo)
                if (a.contains("请输入OKx")) {
                    waveData.clear()
                    pointData.clear()
                    WaveView.tempDx.clear()
                    WaveView.tempDy.clear()
//                    WaveView.dvy=null
//                    WaveView.dvx=null
                    pool=null
                    BleServer.textInfo=a
                    if(System.currentTimeMillis()-SettingFragment.clickTime<1000){
                        BleServer.er2_worker.sendCmd("OKx".toByteArray())
                    }
                 //   BleServer.er2_worker.sendCmd("OKx".toByteArray())
                } else if (a.contains("请输入RE基准电压")) {
                    BleServer.dataScope.launch {
                        delay(100)
                        BleServer.er2_worker.sendCmd(( Vbias.toString()+"x").toByteArray())
                    }

                } else if (a.contains("请输入起始电压")) {
                    BleServer.er2_worker.sendCmd((Vlow.toString()+"x").toByteArray())
                } else if (a.contains("请输入终止电压")) {
                    BleServer.er2_worker.sendCmd((Vhigh.toString()+"x").toByteArray())
                } else if (a.contains("请输入阶梯步进")) {
                    BleServer.er2_worker.sendCmd((Vstep.toString()+"x").toByteArray())
                } else if (a.contains("请输入脉冲电压")) {
                    BleServer.er2_worker.sendCmd((Vpulse.toString()+"x").toByteArray())
                } else if (a.contains("请输入周期时间")) {
                    BleServer.er2_worker.sendCmd((Tstep.toString()+"x").toByteArray())
                } else if (a.contains("参数设置完成")) {
                    Log.e("gaga", "done")
                    waveData.clear()
                    pool=null
                } else {
                    pool = com.viatom.littlePu.utils.add(pool, this)
                    // Log.e("gagax",a)
                    pool?.apply {
                        pool = handleDataPool(pool)
                    }
                }

                if(a.contains("扫描结束")){
                    val tsMother = System.currentTimeMillis()
                    val ts = DateStringUtil.timeConvert4(tsMother)
                    File(PathUtil.getPathX("SWV_"+ts+".txt")).writeBytes(BleServer.textInfo.toByteArray(Charset.forName("gb2312")))
                    BleServer.copyFileToDownloads(MainApplication.application,File(PathUtil.getPathX("SWV_"+ts+".txt")))

                    val size=waveData.size/2;
                    val a=DoubleArray(size){
                        waveData[it*2]
                    }
                    val b=DoubleArray(size){
                        waveData[it*2+1]
                    }
                    val c=DoubleArray(size){
                        (b[it]-a[it])/10.0
                    }
                    WaveView.peakCurrent=c.max()

                    val size2=waveData.size/4;

                    //-----------------Delta I
                    val d=DoubleArray(size2){
                        c[it*2+1]-c[it*2]
                    }

                    //-----------------奇数行电压
                    val e=DoubleArray(size2){
                        a[it*2]/1000.0
                    }

                    WaveView.dvy=d;
                    WaveView.dvx=e;
                    BleServer.er2Graph.postValue(true)
                    SettingFragment.btColor.postValue(0)
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
                        val len=waveData.size
                        if(len%4==0){
                            val a=waveData[len-4]
                            val b=waveData[len-3]
                            val n1=(b-a)/10
                            val c=waveData[len-2]
                            val d=waveData[len-1]
                            val n2=(d-c)/10
                            val n3=n2-n1
                            val n4=a/1000
                            WaveView.tempDx.add(n4)
                            WaveView.tempDy.add(n3)
                            pointData.add(Pf(n4,n3))
                            if(len>=8){
                                WaveView.currentDrawIndex=len/4
                                BleServer.er2Graph.postValue(true)
                            }
                        }
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