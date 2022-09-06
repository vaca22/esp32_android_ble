package com.vaca.esp32_android_ble.ble.wt02.blepower;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;


import com.viatom.littlePu.er2.blepower.NotifyListener;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;



public abstract class BaseBleManager extends BleManager {
    public final static String TAG = "BaseBleManagerER2";
    public UUID service_uuid;
    public UUID write_uuid;
    public UUID notify_uuid;

    public BluetoothGattCharacteristic write_char, notify_char;
    boolean isUpdater = false;
    private NotifyListener listener;

    public BaseBleManager(@NonNull final Context context) {
        super(context);
        initUUID();

    }

    public void setNotifyListener(NotifyListener listener) {
        this.listener = listener;
    }



    public abstract void initUUID();

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new MyManagerGattCallback();
    }

    protected abstract void init();

    public void setNotify() {
        setNotificationCallback(notify_char)
                .with((device, data) -> {
                    listener.onNotify(device, data);
                });
    }

    public abstract void initReqQueue();

    public void sendCmd(byte[] bytes) {
        try {
            writeCharacteristic(write_char, bytes,BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                    .split()
                    .done(device -> {
                    })
                    .enqueue();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void log(final int priority, @NonNull final String message) {
        //LepuBleLog.d(TAG, message);
    }

    public boolean isUpdater() {
        return isUpdater;
    }

    public void setUpdater(boolean updater) {
        isUpdater = updater;
    }

    @Override
    public boolean shouldClearCacheWhenDisconnected() {
        return true;
    }

    /**
     * BluetoothGatt callbacks object.
     */
    private class MyManagerGattCallback extends BleManagerGattCallback {


        @Override
        public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {


            final BluetoothGattService service = gatt.getService(service_uuid);

            if (service != null) {
                write_char = service.getCharacteristic(write_uuid);
                notify_char = service.getCharacteristic(notify_uuid);
            }

            boolean notify = false;
            if (notify_char != null) {
                final int properties = notify_char.getProperties();
             //   LepuBleLog.d(TAG, "notifyChar properties ==  " + properties);
                notify = (properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
            }
            boolean writeRequest = false;
            if (write_char != null) {
                final int properties = write_char.getProperties();
                int writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
                if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) {
                    writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
                }
                write_char.setWriteType(writeType);
                writeRequest = (properties & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 || (properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0;


            }

            Log.e("gaga",""+(write_char != null && notify_char != null
                    && notify && writeRequest));

            return write_char != null && notify_char != null
                    && notify && writeRequest;
        }


        // If you have any optional services, allocate them here. Return true only if
        // they are found.
        @Override
        protected boolean isOptionalServiceSupported(@NonNull final BluetoothGatt gatt) {
            return super.isOptionalServiceSupported(gatt);
        }

        // Initialize your device here. Often you need to enable notifications and set required
        // MTU or write some initial data. Do it here.
        @Override
        protected void initialize() {


            initReqQueue();
            setNotify();
            BaseBleManager.this.init();

        }

        @Override
        protected void onDeviceDisconnected() {
            // Device disconnected. Release your references here.
            write_char = null;
            notify_char = null;
        }

        @Override
        protected void onServicesInvalidated() {

        }
    }
}