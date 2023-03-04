package org.webrtc;

import android.content.Context;

public class FlashlightCameraNumerator extends Camera2Enumerator{


    public FlashlightCameraNumerator(Context context) {
        super(context);
    }


    @Override
    public CameraVideoCapturer createCapturer(String deviceName, CameraVideoCapturer.CameraEventsHandler eventsHandler) {
        return new FlashlightCapturer(context,deviceName,eventsHandler);
    }
}
