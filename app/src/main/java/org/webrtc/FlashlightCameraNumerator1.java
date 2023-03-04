package org.webrtc;

public class FlashlightCameraNumerator1 extends Camera1Enumerator{



    @Override
    public CameraVideoCapturer createCapturer(String deviceName, CameraVideoCapturer.CameraEventsHandler eventsHandler) {
        return new FlashlightCapturer1(deviceName, eventsHandler, true);
    }
}
