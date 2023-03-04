package org.webrtc;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.util.Log;

public class FlashlightCapturer1 extends CameraCapturer{


    private final boolean captureToTexture;

    public FlashlightCapturer1(String cameraName, CameraEventsHandler eventsHandler, boolean captureToTexture) {
        super(cameraName, eventsHandler, new Camera1Enumerator(captureToTexture));
        this.captureToTexture = captureToTexture;
    }




    FlashlightCameraSession1 cameraSession;



    @Override
    protected void createCameraSession(CameraSession.CreateSessionCallback createSessionCallback, CameraSession.Events events, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, String cameraName, int width, int height, int framerate) {
        CameraSession.CreateSessionCallback myCallback = new CameraSession.CreateSessionCallback() {
            @Override
            public void onDone(CameraSession cameraSession) {
                FlashlightCapturer1.this.cameraSession = (FlashlightCameraSession1) cameraSession;
                createSessionCallback.onDone(cameraSession);
            }

            @Override
            public void onFailure(CameraSession.FailureType failureType, String s) {
                createSessionCallback.onFailure(failureType, s);
            }
        };

        FlashlightCameraSession1.create(myCallback, events, captureToTexture, applicationContext, surfaceTextureHelper, Camera1Enumerator.getCameraIndex(cameraName), width, height, framerate);
    }

    public void turnOnFlashlight() {
        cameraSession.setFlashlightActive(true);
    }

    public void turnOffFlashlight() {
        cameraSession.setFlashlightActive(false);
    }
}
