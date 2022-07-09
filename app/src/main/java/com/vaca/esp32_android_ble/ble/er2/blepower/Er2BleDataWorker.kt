package com.vaca.esp32_android_ble.ble.er2.blepower


import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log


import com.vaca.esp32_android_ble.ble.BleServer

import com.vaca.esp32_android_ble.MainApplication
import com.vaca.esp32_android_ble.ble.er2.blething.Er2BleCmd
import com.vaca.esp32_android_ble.ble.er2.blething.Er2BleCmd.*
import com.vaca.esp32_android_ble.ble.er2.utils.CRCUtils


import com.viatom.littlePu.er2.bean.FileBean
import com.viatom.littlePu.er2.blepower.NotifyListener


import com.vaca.esp32_android_ble.ble.er2.blething.Er2BleResponse
import com.viatom.littlePu.er2.blething.Er2Formatter


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
    private var pool: ByteArray? = null
    private val fileChannel = Channel<Int>(Channel.CONFLATED)
    private val RtChannel = Channel<Er2BleResponse.RtData>(Channel.CONFLATED)
    private val DeviceInfoChannel = Channel<Er2Formatter.Er2DeviceInfo>(Channel.CONFLATED)
    private var fileDataChannel = Channel<ByteArray>(Channel.CONFLATED)

    private var deviceParaChannel = Channel<ByteArray>(Channel.CONFLATED)

    private var commonChannel = Channel<Int>(Channel.CONFLATED)

    private var myEr2BleDataManager: Er2BleDataManagerER2? = null
    private val dataScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()


    var pkgTotal = 0;
    var currentPkg = 0;
    var fileData: ByteArray? = null
    var currentFileName = ""
    var result = 1;
    var currentFileSize = 0


    companion object {
        lateinit var gua: Er2Formatter.Er2FileList

        const val ER2_CMD_GET_INFO = 0xE1
        const val ER2_CMD_RT_DATA = 0x03
        const val ER2_CMD_VIBRATE_CONFIG = 0x00
        const val ER2_CMD_READ_FILE_LIST = 0xF1
        const val ER2_CMD_READ_FILE_START = 0xF2
        const val ER2_CMD_READ_FILE_DATA = 0xF3
        const val ER2_CMD_READ_FILE_END = 0xF4
        val fileProgressChannel = Channel<FileProgress>(Channel.CONFLATED)
        val fileListChannel = Channel<Er2Formatter.Er2FileList>(Channel.CONFLATED)
    }

    data class FileProgress(
        var name: String = "",
        var progress: Int = 0,
        var success: Boolean = false,
    )
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




    private fun onResponseReceived(response: Er2BleResponse.Er2Response) {

        when (response.cmd) {
            CMD_SET_TIME2 -> {
                dataScope.launch {
                    commonChannel.send(0)
                }
            }
            ER2_RESET -> {
                dataScope.launch {
                    commonChannel.send(0)
                }
            }

            ER2_CMD_SET_PARA -> {
                dataScope.launch {
                    deviceParaChannel.send(byteArrayOf(0.toByte()))
                }
            }
            ER2_CMD_GET_PARA -> {
                dataScope.launch {
                    deviceParaChannel.send(response.content)
                }
            }


            ER2_CMD_RT_DATA -> {
                dataScope.launch {
                    RtChannel.send(Er2BleResponse.RtData(response.content))
                }
            }

            ER2_CMD_GET_INFO -> {
                dataScope.launch {
                    DeviceInfoChannel.send(Er2Formatter.Er2DeviceInfo(response.content))
                }
            }


            ER2_CMD_READ_FILE_LIST -> {
                gua = Er2Formatter.Er2FileList(response.content)
                dataScope.launch {
                    fileListChannel.send(gua)
                }


            }

            ER2_CMD_READ_FILE_START -> {
                val fileStart = Er2Formatter.Er2FileSize(response.content)
                dataScope.launch {
                    fileChannel.send(fileStart.size)
                }
            }

            ER2_CMD_READ_FILE_END -> {
                dataScope.launch {
                    fileChannel.send(0)
                }
            }

            ER2_CMD_READ_FILE_DATA -> {
                dataScope.launch {
                    fileDataChannel.send(response.content)
                }
            }

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
//                                BleServer.er2BleState.postValue(false)
//                            MainActivity.mBluetoothAdapter.disable()

                    }

                })
                ?.enqueue()

        }
    }


    fun disconnect() {
        myEr2BleDataManager?.disconnect()?.enqueue()
        BleServer.er2ConnectFlag = false
    }


    suspend fun getFileList(): Er2Formatter.Er2FileList? {
        mutex.withLock {
            val pkg = Er2BleCmd.getFileList()
            sendCmd(pkg)
            var c: Er2Formatter.Er2FileList? = null
            withTimeoutOrNull(5000) {
                c = fileListChannel.receive()
            }
            return c

        }
    }


    suspend fun syncTime() {
        mutex.withLock {
            val pkg = Er2BleCmd.setTime()
            sendCmd(pkg)
            withTimeoutOrNull(2000) {
                commonChannel.receive()
            }

        }
    }


    suspend fun getFile(b: ByteArray): FileBean {
        mutex.withLock {

            val file = Er2Formatter.Er2FileBuf()
            file.fileName = b
            val pkg = Er2BleCmd.readFileStart(b, 0)
            sendCmd(pkg)
            file.fileSize = fileChannel.receive()
            file.filePointer = 0
            withTimeoutOrNull(56000) {
                while (file.filePointer < file.fileSize) {
                    sendCmd(Er2BleCmd.readFileData(file.filePointer))
                    var temp: ByteArray? = null
                    val b = withTimeoutOrNull(4000) {
                        temp = fileDataChannel.receive()
                    }
                    if (b == null) {
                        return@withTimeoutOrNull
                    }
                    if (temp != null) {
                        file.add(temp!!)
                    } else {

                    }
                }
            }
            sendCmd(Er2BleCmd.readFileEnd())
            withTimeoutOrNull(2000) {
                fileChannel.receive()
            }

            file.fileNameString = file.fileNameString + ".dat"



            return FileBean(file.fileNameString, file.fileData, file.fileSize)
        }
    }


    suspend fun getData(): Er2BleResponse.RtData? {
        mutex.withLock {
            sendCmd(getRtData())
            var c: Er2BleResponse.RtData? = null
            withTimeoutOrNull(300) {
                c = RtChannel.receive()
            }
            return c
        }
    }

    suspend fun getDeviceInfo(): Er2Formatter.Er2DeviceInfo? {
        mutex.withLock {
            sendCmd(getInfo())
            var c: Er2Formatter.Er2DeviceInfo? = null
            withTimeoutOrNull(3000) {
                c = DeviceInfoChannel.receive()
            }
            return c
        }
    }


    suspend fun resetDevice() {
        mutex.withLock {
            sendCmd(Er2BleCmd.reset())
            withTimeoutOrNull(3000) {
                commonChannel.receive()
            }
        }
    }

    suspend fun getDevicePara(): ByteArray? {
        mutex.withLock {
            sendCmd(getPara())
            var c: ByteArray? = null
            withTimeoutOrNull(3000) {
                c = deviceParaChannel.receive()
            }
            return c
        }
    }

    suspend fun setDevicePara(b: Boolean) {
        mutex.withLock {
            sendCmd(setPara(b))
            withTimeoutOrNull(3000) {
                deviceParaChannel.receive()
            }
        }
    }

}