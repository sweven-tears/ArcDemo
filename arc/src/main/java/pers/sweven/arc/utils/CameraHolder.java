package pers.sweven.arc.utils;

import android.hardware.Camera;

public class CameraHolder {
    private static Camera mCamera;

    public static Camera openCamera() {
        if (mCamera == null){
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        return mCamera;
    }

    public static void releaseCamera() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCamera.release();
            mCamera = null;
        }

    }
}
