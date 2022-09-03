package com.vaca.esp32_android_ble.ble.wt02.blepower

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log

import java.util.*

class BleDataManager(context: Context) : BaseBleManager(context) {
    override fun initUUID() {
        service_uuid = UUID.fromString("0003cdd0-0000-1000-8000-00805f9b0131")
        write_uuid = UUID.fromString("0003cdd2-0000-1000-8000-00805f9b0131")
        notify_uuid = UUID.fromString("0003cdd1-0000-1000-8000-00805f9b0131")
    }

    override fun init() {

    }


    override fun initReqQueue() {
        beginAtomicRequestQueue()
            .add(requestMtu(103) // Remember, GATT needs 3 bytes extra. This will allow packet size of 244 bytes.
                .with { device: BluetoothDevice?, mtu: Int ->
                    log(
                        Log.INFO,
                        "MTU set to $mtu"
                    )
                }
                .fail { device: BluetoothDevice?, status: Int ->
                    log(
                        Log.WARN,
                        "Requested MTU not supported: $status"
                    )
                }) //                    .add(setPreferredPhy(PhyRequest.PHY_LE_2M_MASK, PhyRequest.PHY_LE_2M_MASK, PhyRequest.PHY_OPTION_NO_PREFERRED)
            //                            .fail((device, status) -> log(Log.WARN, "Requested PHY not supported: " + status)))
            //                    .add(requestConnectionPriority(CONNECTION_PRIORITY_HIGH))
            .add(enableNotifications(notify_char))
            .done { device: BluetoothDevice? ->
                log(
                    Log.INFO,
                    "Target initialized"
                )
            }
            .enqueue()
    }


}