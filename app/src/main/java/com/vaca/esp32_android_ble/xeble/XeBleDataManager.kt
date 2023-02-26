package com.vaca.esp32_android_ble.xeble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.data.Data
import java.util.*

class XeBleDataManager(context: Context) : BleManager(context) {
    private var write_char: BluetoothGattCharacteristic? = null
    private var notify_char: BluetoothGattCharacteristic? = null
    private var listener: OnNotifyListener? = null
    fun setNotifyListener(listener: OnNotifyListener?) {
        this.listener = listener
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return MyManagerGattCallback()
    }

    fun sendCmd(bytes: ByteArray?) {
        writeCharacteristic(write_char, bytes,BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
            .split()
            .done { }
            .enqueue()
    }

    override fun log(priority: Int, message: String) {

    }


    interface OnNotifyListener {
        fun onNotify(device: BluetoothDevice?, data: Data?)
    }

    /**
     * BluetoothGatt callbacks object.
     */
    private inner class MyManagerGattCallback : BleManagerGattCallback() {
        public override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val service = gatt.getService(service_uuid)
            if (service != null) {
                write_char = service.getCharacteristic(write_uuid)
                notify_char = service.getCharacteristic(notify_uuid)
            }
            // Validate properties
            var notify = false
            if (notify_char != null) {
                val properties = notify_char!!.properties
                notify = properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0
            }
            var writeRequest = false
            if (write_char != null) {
                val properties = write_char!!.properties
                writeRequest =
                    properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0
                write_char!!.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            }
            // Return true if all required services have been found
            return write_char != null && notify_char != null && notify && writeRequest
        }

        // If you have any optional services, allocate them here. Return true only if
        // they are found.
        override fun isOptionalServiceSupported(gatt: BluetoothGatt): Boolean {
            return super.isOptionalServiceSupported(gatt)
        }

        // Initialize your device here. Often you need to enable notifications and set required
        // MTU or write some initial data. Do it here.
        override fun initialize() {
            // You may enqueue multiple operations. A queue ensures that all operations are
            // performed one after another, but it is not required.
            beginAtomicRequestQueue()
                .add(requestMtu(122) // Remember, GATT needs 3 bytes extra. This will allow packet size of 244 bytes.
                    .with { _: BluetoothDevice?, mtu: Int ->
                       Log.e(
                           " Log.INFO",
                            "MTU set to $mtu"
                        )
                    }
                    .fail { _: BluetoothDevice?, status: Int ->
                        Log.e(
                           " Log.WARN",
                            "Requested MTU not supported: $status"
                        )
                    })
                .add(enableNotifications(notify_char))
                .done { log(Log.INFO, "Target initialized") }
                .enqueue()
            // You may easily enqueue more operations here like such:
            setNotificationCallback(notify_char)
                .with { device: BluetoothDevice?, data: Data? -> listener!!.onNotify(device, data) }
        }

        override fun onDeviceDisconnected() {
            // Device disconnected. Release your references here.
            write_char = null
            notify_char = null
        }

        override fun onServicesInvalidated() {

        }
    }

    companion object {
        val service_uuid: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
        val write_uuid: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
        val notify_uuid: UUID = UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb")
    }
}