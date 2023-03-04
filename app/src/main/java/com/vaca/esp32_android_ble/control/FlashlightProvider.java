package com.vaca.esp32_android_ble.control;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

public class FlashlightProvider {

    private static final String TAG = FlashlightProvider.class.getSimpleName();
    private CameraManager camManager;
    private Context context;

    public FlashlightProvider(Context context) {
        this.context = context;
    }

    public void turnFlashlightOn() {
        try {
            camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            if (camManager != null) {
                cameraId = camManager.getCameraIdList()[0];
                camManager.setTorchMode(cameraId, true);
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void turnFlashlightOff() {
        try {
            String cameraId;
            camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            if (camManager != null) {
                cameraId = camManager.getCameraIdList()[0]; // Usually front camera is at 0 position.
                camManager.setTorchMode(cameraId, false);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}