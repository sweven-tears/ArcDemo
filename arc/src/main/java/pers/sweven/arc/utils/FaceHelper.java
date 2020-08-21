package pers.sweven.arc.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.VersionInfo;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import pers.sweven.arc.entity.FaceData;
import pers.sweven.arc.entity.FaceResult;
import pers.sweven.arc.entity.RectInfo;

public class FaceHelper {
    private static final String TAG = FaceHelper.class.getName();
    private static ExecutorService pool;
    private static int initCode;
    private FaceEngine faceEngine;
    private FaceEngine faceImageEngine;
    private float minSimilar = 0.6f;
    private final List<FaceData> registerFaceList = new ArrayList<>();

    public FaceHelper() {
    }

    public FaceHelper(Context context) {
        init(context);
    }

    public static void active(Context context, String APP_ID, String SDK_KEY) {
        int code = FaceEngine.activeOnline(context, APP_ID, SDK_KEY);
        if (code == ErrorInfo.MOK) {
            Log.i(TAG, "activeOnline success");
        } else if (code == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
            Log.i(TAG, "already activated");
        } else {
            Log.i(TAG, "activeOnline failed, code is : " + code);
        }
    }

    public void init(Context context) {
        if (pool == null) pool = Executors.newSingleThreadExecutor();

        if (faceEngine == null) faceEngine = new FaceEngine();
        if (faceImageEngine == null) faceImageEngine = new FaceEngine();
        synchronized (FaceEngine.class) {
            initCode = faceEngine.init(context, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                    16, 4, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);
            faceImageEngine.init(context, DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                    16, 1, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);
        }
        VersionInfo versionInfo = new VersionInfo();
        FaceEngine.getVersion(versionInfo);
        Log.i(TAG, "initEngine:  init: " + initCode + "  version:" + versionInfo);
    }


    public void release() {
        if (initCode == 0 && faceEngine != null) {
            synchronized (FaceEngine.class) {
                int code = faceEngine.unInit();
                Log.i(TAG, "unInitEngine: " + code);
            }
        }
    }


    /**
     * 从相机中检测人脸
     *
     * @param bytes 数据流
     * @param w     宽
     * @param h     高
     * @return
     */
    public List<RectInfo> track(byte[] bytes, int w, int h) {

        List<FaceInfo> faceInfoList = new ArrayList<>();
        int code;
        synchronized (FaceEngine.class) {
            code = faceEngine.detectFaces(bytes, w, h, FaceEngine.CP_PAF_NV21, faceInfoList);
        }
        if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
            List<RectInfo> rectInfoList = new ArrayList<>();
            for (FaceInfo faceInfo : faceInfoList) {
                RectInfo rectInfo = new RectInfo();
                rectInfo.setRect(faceInfo.getRect());
                rectInfoList.add(rectInfo);
            }
            return rectInfoList;
        } else {
            return new ArrayList<>();
        }

    }

    public void clearFace() {
        registerFaceList.clear();
    }

    /**
     * 注册人脸
     *
     * @param filePath 相片
     * @param value    相片对应值
     * @param id       id
     * @return 是否注册成功
     */
    public boolean registerFace(String filePath, String value, Long id) {
        Bitmap originalBitmap = BitmapFactory.decodeFile(filePath);
        Bitmap bitmap = ArcSoftImageUtil.getAlignedBitmap(originalBitmap, true);
        byte[] bgr24 = ArcSoftImageUtil.createImageData(bitmap.getWidth(), bitmap.getHeight(), ArcSoftImageFormat.BGR24);
        int transformCode = ArcSoftImageUtil.bitmapToImageData(bitmap, bgr24, ArcSoftImageFormat.BGR24);
        if (transformCode != ArcSoftImageUtilError.CODE_SUCCESS) {
            Log.i(TAG, "transform failed, code is : " + transformCode);
            return false;
        }
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int code = faceEngine.detectFaces(bgr24, bitmap.getWidth(), bitmap.getHeight(), FaceEngine.CP_PAF_BGR24, faceInfoList);
        if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
            Log.i(TAG, "detectFaces, face num is : " + faceInfoList.size());
            FaceFeature faceFeature = new FaceFeature();
            int featureCode = faceImageEngine.extractFaceFeature(bgr24, bitmap.getWidth(), bitmap.getHeight(), FaceEngine.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
            if (featureCode == ErrorInfo.MOK) {
                FaceData faceData = new FaceData();
                faceData.setFacePath(filePath);
                faceData.setValue(value);
                faceData.setId(id);
                faceData.setFaceData(faceFeature.getFeatureData());
                registerFaceList.add(faceData);
                return true;
            }
        } else {
            Log.i(TAG, "no face detected, code is : " + code);
            return false;
        }
        return false;
    }


    /**
     * 检测人脸结果
     *
     * @param bytes 数据流
     * @param w     宽
     * @param h     高
     * @return
     */
    public Observable<List<FaceResult>> identify(byte[] bytes, int w, int h) {
        return Observable.just(1)
                .observeOn(Schedulers.io())
                .map(integer -> {
                    List<FaceResult> faceResultList = new ArrayList<>();

                    List<FaceInfo> faceInfoList = new ArrayList<>();
                    //先获取有几个脸
                    int code;
                    synchronized (FaceEngine.class) {
                        code = faceEngine.detectFaces(bytes, w, h, FaceEngine.CP_PAF_NV21, faceInfoList);
                    }
                    if (code != ErrorInfo.MOK) return new ArrayList<>();
                    for (FaceInfo faceInfo : faceInfoList) {
                        //每个脸获取对应的特征
                        FaceFeature targetFeature = new FaceFeature();
                        int frCode;
                        synchronized (FaceEngine.class) {
                            frCode = faceEngine.extractFaceFeature(bytes, w, h, FaceEngine.CP_PAF_NV21, faceInfo, targetFeature);
                        }
                        if (frCode != ErrorInfo.MOK) continue;


                        float similar = 0;
                        FaceData maxSimilarData = null;
                        //搜索库中和特征匹配的人
                        for (FaceData faceData : registerFaceList) {
                            FaceFeature feature = new FaceFeature();
                            feature.setFeatureData(faceData.getFaceData());
                            FaceSimilar faceSimilar = new FaceSimilar();
                            synchronized (FaceEngine.class) {
                                faceEngine.compareFaceFeature(targetFeature, feature, faceSimilar);
                            }
                            if (faceSimilar.getScore() > similar) {
                                similar = faceSimilar.getScore();
                                maxSimilarData = faceData;
                            }
                        }

                        if (maxSimilarData != null && similar > this.minSimilar) {
                            similarDeal(maxSimilarData, faceResultList);
                        }

                    }
                    return faceResultList;
                });
    }

    /**
     * @param data           识别出来的人脸
     * @param faceResultList 匹配成功的结果集
     */
    protected void similarDeal(FaceData data, List<FaceResult> faceResultList) {
        faceResultList.add(new FaceResult(data.getValue()));
    }

    public static void buffer2Image(byte[] data, String inPath) {
        try {
            FileOutputStream stream = new FileOutputStream(inPath);
            stream.write(data);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
