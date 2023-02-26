package com.example.smart_xe_gimble.xe

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.media.AudioManager
import android.util.Log
import java.lang.reflect.InvocationTargetException

object XeBleUtils {




    private var mBluetoothAdapter: BluetoothAdapter? = null

    /**
     * 实例化 BluetoothAdapter
     */
    private fun getInstance(): BluetoothAdapter? {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        }
        return mBluetoothAdapter
    }

    /**
     * 检测设备是否支持蓝牙
     */
    fun checkBluetoothEnable(): Boolean {
        return getInstance() == null
    }

    /**
     * 判断当前蓝牙是否打开
     */
    fun checkBluetoothStateEnable(): Boolean {
        return getInstance()?.isEnabled == true
    }

    /**
     * 获取蓝牙耳机连接状态
     */
    private fun isWiredHeadsetConnected(context: Context): Boolean {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            return audioManager.isWiredHeadsetOn
        } catch (e: Exception) {
        }
        return false
    }

    /**
     * 判断蓝牙耳机是否已连接
     */
    fun hasBluetoothAudioDevice(): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
       return true
    }

    /**
     * 获取到已配对成功蓝牙设备
     */
    @SuppressLint("MissingPermission")
    fun fetchAlReadyConnection() {
        getInstance()?.let {
            val devices = it.bondedDevices
            for (device in devices) {
                Log.e(
                    "HLQ", "----> " +
                            "name ${device.name} " +
                            "address ${device.address} " +
                            "bondState ${device.bondState} " +
                            "type ${device.type} ${device.uuids.size}"
                )
            }
        }
    }

    fun getConnectedBtDevice(): String? {
        //获取蓝牙适配器
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        //得到已匹配的蓝牙设备列表
        val bondedDevices = bluetoothAdapter.bondedDevices
        if (bondedDevices != null && bondedDevices.size > 0) {
            for (bondedDevice in bondedDevices) {
                try {
                    //使用反射调用被隐藏的方法
                    val isConnectedMethod =
                        BluetoothDevice::class.java.getDeclaredMethod(
                            "isConnected"
                        )
                    isConnectedMethod.isAccessible = true
                    val isConnected =
                        isConnectedMethod.invoke(bondedDevice) as Boolean
                    if (isConnected) {
                        return bondedDevice.name
                    }
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }


    lateinit var bluetoothDevice: BluetoothDevice
    fun getXeBtDevice(): String? {
        //获取蓝牙适配器
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        //得到已匹配的蓝牙设备列表
        val bondedDevices = bluetoothAdapter.bondedDevices
        if (bondedDevices != null && bondedDevices.size > 0) {
            for (bondedDevice in bondedDevices) {
                try {
                    //使用反射调用被隐藏的方法
                    val isConnectedMethod =
                        BluetoothDevice::class.java.getDeclaredMethod(
                            "isConnected"
                        )
                    isConnectedMethod.isAccessible = true
                    val isConnected =
                        isConnectedMethod.invoke(bondedDevice) as Boolean
                    if (isConnected) {
                        if(bondedDevice.name.equals("SMART_XE")){
                            bluetoothDevice = bondedDevice
                        }
                        return bondedDevice.name
                    }
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}