package org.webrtc;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.util.Log;
import android.util.Range;
import android.view.Surface;

import com.vaca.esp32_android_ble.MainApplication;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FlashlightCameraSession implements CameraSession {

    private static final String TAG = "FlashlightCameraSession";
    private static final Histogram camera2StartTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera2.StartTimeMs", 1, 10000, 50);
    private static final Histogram camera2StopTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera2.StopTimeMs", 1, 10000, 50);
    private static final Histogram camera2ResolutionHistogram;
    private final Handler cameraThreadHandler;
    private final CreateSessionCallback callback;
    private final Events events;
    private final Context applicationContext;
    private final CameraManager cameraManager;
    private final SurfaceTextureHelper surfaceTextureHelper;
    private final String cameraId;
    private final int width;
    private final int height;
    private final int framerate;
    private CameraCharacteristics cameraCharacteristics;
    private int cameraOrientation;
    private boolean isCameraFrontFacing;
    private int fpsUnitFactor;
    private CameraEnumerationAndroid.CaptureFormat captureFormat;

    private CameraDevice cameraDevice;

    private Surface surface;

    private CameraCaptureSession captureSession;
    private FlashlightCameraSession.SessionState state;
    private boolean firstFrameReported;
    private final long constructionTimeNs;

    public static void create(CreateSessionCallback callback, Events events, Context applicationContext, CameraManager cameraManager, SurfaceTextureHelper surfaceTextureHelper, String cameraId, int width, int height, int framerate) {
        new FlashlightCameraSession(callback, events, applicationContext, cameraManager, surfaceTextureHelper, cameraId, width, height, framerate);
    }

    private FlashlightCameraSession(CreateSessionCallback callback, Events events, Context applicationContext, CameraManager cameraManager, SurfaceTextureHelper surfaceTextureHelper, String cameraId, int width, int height, int framerate) {
        this.state = FlashlightCameraSession.SessionState.RUNNING;
        Logging.d("FlashlightCameraSession", "Create new camera2 session on camera " + cameraId);
        this.constructionTimeNs = System.nanoTime();
        this.cameraThreadHandler = new Handler();
        this.callback = callback;
        this.events = events;
        this.applicationContext = applicationContext;
        this.cameraManager = cameraManager;
        this.surfaceTextureHelper = surfaceTextureHelper;
        this.cameraId = cameraId;
        this.width = width;
        this.height = height;
        this.framerate = framerate;
        this.start();
    }

    private void start() {
        this.checkIsOnCameraThread();
        Logging.d("FlashlightCameraSession", "start");

        try {
            this.cameraCharacteristics = this.cameraManager.getCameraCharacteristics(this.cameraId);
        } catch (CameraAccessException var2) {
            this.reportError("getCameraCharacteristics(): " + var2.getMessage());
            return;
        }

        this.cameraOrientation = (Integer)this.cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        this.isCameraFrontFacing = (Integer)this.cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == 0;
        this.findCaptureFormat();
        this.openCamera();
    }

    private void findCaptureFormat() {
        this.checkIsOnCameraThread();
        Range<Integer>[] fpsRanges = (Range[])this.cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
        this.fpsUnitFactor = Camera2Enumerator.getFpsUnitFactor(fpsRanges);
        List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> framerateRanges = Camera2Enumerator.convertFramerates(fpsRanges, this.fpsUnitFactor);
        List<Size> sizes = Camera2Enumerator.getSupportedSizes(this.cameraCharacteristics);
        Logging.d("FlashlightCameraSession", "Available preview sizes: " + sizes);
        Logging.d("FlashlightCameraSession", "Available fps ranges: " + framerateRanges);
        if (!framerateRanges.isEmpty() && !sizes.isEmpty()) {
            CameraEnumerationAndroid.CaptureFormat.FramerateRange bestFpsRange = CameraEnumerationAndroid.getClosestSupportedFramerateRange(framerateRanges, this.framerate);
            Size bestSize = CameraEnumerationAndroid.getClosestSupportedSize(sizes, this.width, this.height);
            CameraEnumerationAndroid.reportCameraResolution(camera2ResolutionHistogram, bestSize);
            this.captureFormat = new CameraEnumerationAndroid.CaptureFormat(bestSize.width, bestSize.height, bestFpsRange);
            Logging.d("FlashlightCameraSession", "Using capture format: " + this.captureFormat);
        } else {
            this.reportError("No supported capture formats.");
        }
    }

    private void openCamera() {
        this.checkIsOnCameraThread();
        Logging.d("FlashlightCameraSession", "Opening camera " + this.cameraId);
        this.events.onCameraOpening();

        try {
            this.cameraManager.openCamera(this.cameraId, new FlashlightCameraSession.CameraStateCallback(), this.cameraThreadHandler);
        } catch (CameraAccessException var2) {
            this.reportError("Failed to open camera: " + var2);
        }
    }

   public void setFlashlightActive(boolean isActive) {
        try {

            if (isActive) {
                this.cameraManager.setTorchMode(this.cameraId,true);
            } else {
                this.cameraManager.setTorchMode(this.cameraId,false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public void stop() {
        Logging.d("FlashlightCameraSession", "Stop camera2 session on camera " + this.cameraId);
        this.checkIsOnCameraThread();
        if (this.state != FlashlightCameraSession.SessionState.STOPPED) {
            long stopStartTime = System.nanoTime();
            this.state = FlashlightCameraSession.SessionState.STOPPED;
            this.stopInternal();
            int stopTimeMs = (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - stopStartTime);
            camera2StopTimeMsHistogram.addSample(stopTimeMs);
        }

    }

    private void stopInternal() {
        Logging.d("FlashlightCameraSession", "Stop internal");
        this.checkIsOnCameraThread();
        this.surfaceTextureHelper.stopListening();
        if (this.captureSession != null) {
            this.captureSession.close();
            this.captureSession = null;
        }

        if (this.surface != null) {
            this.surface.release();
            this.surface = null;
        }

        if (this.cameraDevice != null) {
            this.cameraDevice.close();
            this.cameraDevice = null;
        }

        Logging.d("FlashlightCameraSession", "Stop done");
    }

    private void reportError(String error) {
        this.checkIsOnCameraThread();
        Logging.e("FlashlightCameraSession", "Error: " + error);
        boolean startFailure = this.captureSession == null && this.state != FlashlightCameraSession.SessionState.STOPPED;
        this.state = FlashlightCameraSession.SessionState.STOPPED;
        this.stopInternal();
        if (startFailure) {
            this.callback.onFailure(FailureType.ERROR, error);
        } else {
            this.events.onCameraError(this, error);
        }

    }

    private int getFrameOrientation() {
        int rotation = CameraSession.getDeviceOrientation(this.applicationContext);
        if (!this.isCameraFrontFacing) {
            rotation = 360 - rotation;
        }

        return (this.cameraOrientation + rotation) % 360;
    }

    private void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            throw new IllegalStateException("Wrong thread");
        }
    }

    static {
        camera2ResolutionHistogram = Histogram.createEnumeration("WebRTC.Android.Camera2.Resolution", CameraEnumerationAndroid.COMMON_RESOLUTIONS.size());
    }

    private static class CameraCaptureCallback extends CameraCaptureSession.CaptureCallback {
        private CameraCaptureCallback() {
        }

        public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
            Logging.d("FlashlightCameraSession", "Capture failed: " + failure);
        }
    }

    private class CaptureSessionCallback extends CameraCaptureSession.StateCallback {
        private CaptureSessionCallback() {
        }

        public void onConfigureFailed(CameraCaptureSession session) {
            FlashlightCameraSession.this.checkIsOnCameraThread();
            session.close();
            FlashlightCameraSession.this.reportError("Failed to configure capture session.");
        }

        public void onConfigured(CameraCaptureSession session) {
            FlashlightCameraSession.this.checkIsOnCameraThread();
            Logging.d("FlashlightCameraSession", "Camera capture session configured.");
            FlashlightCameraSession.this.captureSession = session;

            try {
                CaptureRequest.Builder captureRequestBuilder = FlashlightCameraSession.this.cameraDevice.createCaptureRequest(3);
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range(FlashlightCameraSession.this.captureFormat.framerate.min / FlashlightCameraSession.this.fpsUnitFactor, FlashlightCameraSession.this.captureFormat.framerate.max / FlashlightCameraSession.this.fpsUnitFactor));
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, 1);
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false);
                this.chooseStabilizationMode(captureRequestBuilder);
                this.chooseFocusMode(captureRequestBuilder);
                captureRequestBuilder.addTarget(FlashlightCameraSession.this.surface);
                session.setRepeatingRequest(captureRequestBuilder.build(), new FlashlightCameraSession.CameraCaptureCallback(), FlashlightCameraSession.this.cameraThreadHandler);
            } catch (CameraAccessException var3) {
                FlashlightCameraSession.this.reportError("Failed to start capture request. " + var3);
                return;
            }

            FlashlightCameraSession.this.surfaceTextureHelper.startListening((frame) -> {
                FlashlightCameraSession.this.checkIsOnCameraThread();
                if (FlashlightCameraSession.this.state != FlashlightCameraSession.SessionState.RUNNING) {
                    Logging.d("FlashlightCameraSession", "Texture frame captured but camera is no longer running.");
                } else {
                    if (!FlashlightCameraSession.this.firstFrameReported) {
                        FlashlightCameraSession.this.firstFrameReported = true;
                        int startTimeMs = (int)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - FlashlightCameraSession.this.constructionTimeNs);
                        FlashlightCameraSession.camera2StartTimeMsHistogram.addSample(startTimeMs);
                    }

                    VideoFrame modifiedFrame = new VideoFrame(CameraSession.createTextureBufferWithModifiedTransformMatrix((TextureBufferImpl)frame.getBuffer(), FlashlightCameraSession.this.isCameraFrontFacing, -FlashlightCameraSession.this.cameraOrientation), FlashlightCameraSession.this.getFrameOrientation(), frame.getTimestampNs());
                    FlashlightCameraSession.this.events.onFrameCaptured(FlashlightCameraSession.this, modifiedFrame);
                    modifiedFrame.release();
                }
            });
            Logging.d("FlashlightCameraSession", "Camera device successfully started.");
            FlashlightCameraSession.this.callback.onDone(FlashlightCameraSession.this);
        }

        private void chooseStabilizationMode(CaptureRequest.Builder captureRequestBuilder) {
            int[] availableOpticalStabilization = (int[])FlashlightCameraSession.this.cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);
            int[] availableVideoStabilization;
            int var5;
            int mode;
            if (availableOpticalStabilization != null) {
                availableVideoStabilization = availableOpticalStabilization;
                int var4 = availableOpticalStabilization.length;

                for(var5 = 0; var5 < var4; ++var5) {
                    mode = availableVideoStabilization[var5];
                    if (mode == 1) {
                        captureRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, 1);
                        captureRequestBuilder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, 0);
                        Logging.d("FlashlightCameraSession", "Using optical stabilization.");
                        return;
                    }
                }
            }

            availableVideoStabilization = (int[])FlashlightCameraSession.this.cameraCharacteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);
            int[] var8 = availableVideoStabilization;
            var5 = availableVideoStabilization.length;

            for(mode = 0; mode < var5; ++mode) {
                int modex = var8[mode];
                if (modex == 1) {
                    captureRequestBuilder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, 1);
                    captureRequestBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, 0);
                    Logging.d("FlashlightCameraSession", "Using video stabilization.");
                    return;
                }
            }

            Logging.d("FlashlightCameraSession", "Stabilization not available.");
        }

        private void chooseFocusMode(CaptureRequest.Builder captureRequestBuilder) {
            int[] availableFocusModes = (int[])FlashlightCameraSession.this.cameraCharacteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
            int[] var3 = availableFocusModes;
            int var4 = availableFocusModes.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                int mode = var3[var5];
                if (mode == 3) {
                    captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, 3);
                    Logging.d("FlashlightCameraSession", "Using continuous video auto-focus.");
                    return;
                }
            }

            Logging.d("FlashlightCameraSession", "Auto-focus is not available.");
        }
    }

    private class CameraStateCallback extends android.hardware.camera2.CameraDevice.StateCallback {
        private CameraStateCallback() {
        }

        private String getErrorDescription(int errorCode) {
            switch(errorCode) {
                case 1:
                    return "Camera device is in use already.";
                case 2:
                    return "Camera device could not be opened because there are too many other open camera devices.";
                case 3:
                    return "Camera device could not be opened due to a device policy.";
                case 4:
                    return "Camera device has encountered a fatal error.";
                case 5:
                    return "Camera service has encountered a fatal error.";
                default:
                    return "Unknown camera error: " + errorCode;
            }
        }

        public void onDisconnected(CameraDevice camera) {
            FlashlightCameraSession.this.checkIsOnCameraThread();
            boolean startFailure = FlashlightCameraSession.this.captureSession == null && FlashlightCameraSession.this.state != FlashlightCameraSession.SessionState.STOPPED;
            FlashlightCameraSession.this.state = FlashlightCameraSession.SessionState.STOPPED;
            FlashlightCameraSession.this.stopInternal();
            if (startFailure) {
                FlashlightCameraSession.this.callback.onFailure(FailureType.DISCONNECTED, "Camera disconnected / evicted.");
            } else {
                FlashlightCameraSession.this.events.onCameraDisconnected(FlashlightCameraSession.this);
            }

        }

        public void onError(CameraDevice camera, int errorCode) {
            FlashlightCameraSession.this.checkIsOnCameraThread();
            FlashlightCameraSession.this.reportError(this.getErrorDescription(errorCode));
        }

        public void onOpened(CameraDevice camera) {
            FlashlightCameraSession.this.checkIsOnCameraThread();
            Logging.d("FlashlightCameraSession", "Camera opened.");
            FlashlightCameraSession.this.cameraDevice = camera;
            FlashlightCameraSession.this.surfaceTextureHelper.setTextureSize(FlashlightCameraSession.this.captureFormat.width, FlashlightCameraSession.this.captureFormat.height);
            FlashlightCameraSession.this.surface = new Surface(FlashlightCameraSession.this.surfaceTextureHelper.getSurfaceTexture());

            try {
                camera.createCaptureSession(Arrays.asList(FlashlightCameraSession.this.surface), FlashlightCameraSession.this.new CaptureSessionCallback(), FlashlightCameraSession.this.cameraThreadHandler);
            } catch (CameraAccessException var3) {
                FlashlightCameraSession.this.reportError("Failed to create capture session. " + var3);
            }
        }

        public void onClosed(CameraDevice camera) {
            FlashlightCameraSession.this.checkIsOnCameraThread();
            Logging.d("FlashlightCameraSession", "Camera device closed.");
            FlashlightCameraSession.this.events.onCameraClosed(FlashlightCameraSession.this);
        }
    }

    private static enum SessionState {
        RUNNING,
        STOPPED;

        private SessionState() {
        }
    }

}
