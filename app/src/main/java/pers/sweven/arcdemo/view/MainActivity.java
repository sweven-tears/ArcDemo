package pers.sweven.arcdemo.view;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pers.sweven.arc.base.BaseCameraActivity;
import pers.sweven.arc.entity.FaceResult;
import pers.sweven.arc.utils.FaceHelper;
import pers.sweven.arc.widget.FaceRectView;
import pers.sweven.arcdemo.utils.MyFaceHelper;
import pers.sweven.arcdemo.R;
import pers.sweven.arcdemo.Student;
import pers.sweven.arcdemo.databinding.ActivityMainBinding;
import pers.sweven.mvvm.base.BaseViewModel;

public class MainActivity extends BaseCameraActivity<ActivityMainBinding, BaseViewModel> {
    private Camera.Size mSize;
    private MyFaceHelper faceHelper;

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initParams(Bundle bundle) {

    }

    @Override
    protected void initData() {
        faceHelper = new MyFaceHelper(this);
        faceHelper.clearFace();
        faceHelper.registerFace("/sdcard/aaa/123.png", "sweven", 11L);
        startScanFace();
        getTextureView().setVisibility(View.VISIBLE);
    }

    @Override
    protected TextureView getTextureView() {
        return getBinding().textureView;
    }

    @Override
    protected FaceHelper getFaceHelper() {
        return faceHelper;
    }

    @Override
    protected FaceRectView getRectView() {
        return getBinding().faceRect;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mSize == null) mSize = camera.getParameters().getPreviewSize();
        faceHelper.identify(data, mSize.width, mSize.height)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(val -> {
                    if (val == null || val.size() == 0) return;
                    FaceResult faceResult = val.get(0);
                    String value = faceResult.getValue();
                    Student student = (Student) faceResult.getExtra();
                    setResult(RESULT_OK,
                            new Intent()
                                    .putExtra("name", value)
                                    .putExtra("age", student.getAge())
                                    .putExtra("sex", student.getSex())
                    );
                    finish();
                }, Throwable::printStackTrace)
                .isDisposed();
    }
}