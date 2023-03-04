package org.webrtc;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.util.Log;

public class FlashlightCapturer extends CameraCapturer{




    private final Context context;

    private final CameraManager cameraManager;

    public FlashlightCapturer(Context context, String cameraName, CameraEventsHandler eventsHandler) {
        super(cameraName, eventsHandler, new Camera2Enumerator(context));
        this.context = context;
        this.cameraManager = (CameraManager)context.getSystemService("camera");
    }

    FlashlightCameraSession cameraSession;

    protected void createCameraSession(CameraSession.CreateSessionCallback createSessionCallback, CameraSession.Events events, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, String cameraName, int width, int height, int framerate) {

        CameraSession.CreateSessionCallback myCallback = new CameraSession.CreateSessionCallback() {
            @Override
            public void onDone(CameraSession cameraSession) {
                Log.e("gaga","yess");
                FlashlightCapturer.this.cameraSession = (FlashlightCameraSession) cameraSession;
                createSessionCallback.onDone(cameraSession);
            }

            @Override
            public void onFailure(CameraSession.FailureType failureType, String s) {
                createSessionCallback.onFailure(failureType, s);
            }
        };
        FlashlightCameraSession.create(myCallback, events, applicationContext, this.cameraManager, surfaceTextureHelper, cameraName, width, height, framerate);
    }
    public void turnOnFlashlight() {
        cameraSession.setFlashlightActive(true);
    }

    public void turnOffFlashlight() {
        cameraSession.setFlashlightActive(false);
    }
}
