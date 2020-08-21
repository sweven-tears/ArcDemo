package pers.sweven.arc.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.view.TextureView;

import com.sweven.console.LogUtil;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.ViewDataBinding;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pers.sweven.arc.entity.RectInfo;
import pers.sweven.arc.utils.CameraHolder;
import pers.sweven.arc.utils.FaceHelper;
import pers.sweven.arc.widget.FaceRectView;
import pers.sweven.mvvm.base.BaseActivity;
import pers.sweven.mvvm.base.BaseViewModel;

import static pers.sweven.arc.utils.CameraHolder.releaseCamera;

/**
 * Created by Sweven on 2020/8/21--14:09.
 */
public abstract class BaseCameraActivity<T extends ViewDataBinding, VM extends BaseViewModel> extends BaseActivity<T, VM> implements Camera.PreviewCallback {
    public static final int REQUEST_CODE_CAMERA = 1001;
    protected Camera mCamera;
    private Camera.Size size;

    @Override
    protected void initObservable() {
        super.initObservable();
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
            }
            return;
        }
        dealWithCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dealWithCamera();
        }

    }


    protected abstract TextureView getTextureView();

    @SuppressLint("MissingPermission")
    private void dealWithCamera() {
        getTextureView().setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                LogUtil.with("BaseCameraActivity").d("sizeChange");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                LogUtil.with("BaseCameraActivity").d("destroy");
                releaseCamera();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                LogUtil.with("BaseCameraActivity").d("updated");

            }
        });

    }

    private long last;
    private boolean scanFace = false;

    @SuppressLint("CheckResult")
    private void openCamera() {
        releaseCamera();
        mCamera = CameraHolder.openCamera();
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            mCamera.setPreviewTexture(getTextureView().getSurfaceTexture());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        mCamera.setPreviewCallback((data, camera) -> {
            if (!scanFace) return;
            if (size == null) {
                size = camera.getParameters().getPreviewSize();
            }
            Observable.just(1)
                    .map(i -> getFaceHelper().track(data, size.width, size.height))
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> {
                        List<RectInfo> faces = getFaceHelper().track(data, size.width, size.height);
                        if (getRectView() != null) {
                            getRectView().setNewFaceInfo(faces, size.width, size.height);
                        }
                    }, Throwable::printStackTrace)
                    .isDisposed();
            long now = System.currentTimeMillis();
            if (now - last < 800) return;
            last = now;
            onPreviewFrame(data, camera);
        });

    }

    protected abstract FaceHelper getFaceHelper();

    protected void startScanFace() {
        scanFace = true;
    }

    protected void pauseScanFace() {
        scanFace = false;
    }

    protected abstract FaceRectView getRectView();




    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCamera();
        getFaceHelper().release();
    }
}
