package pers.sweven.arc.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.Nullable;
import pers.sweven.arc.entity.RectInfo;
import pers.sweven.arc.utils.FaceHelper;

public class FaceRectView extends View {
    private static final String TAG = "FaceRectView";
    private CopyOnWriteArrayList<RectInfo> faceRectList = new CopyOnWriteArrayList<>();

    public FaceRectView(Context context) {
        this(context, null);
    }

    public FaceRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faceRectList != null && faceRectList.size() > 0) {
            for (int i = 0; i < faceRectList.size(); i++) {
                drawFaceRect(canvas, faceRectList.get(i));
            }
        }
    }


    public void setNewFaceInfo(List<RectInfo> faceInfoList, int w, int h) {
        faceRectList.clear();
        post(() -> {
            if (faceInfoList != null) {
                float height = getMeasuredHeight();
                float width = getMeasuredWidth();
                float hs = height / h;
                float ws = width / w;
                for (RectInfo rectInfo : faceInfoList) {
                    Rect rect = rectInfo.getRect();
                    rect.top = (int) (rect.top * hs);
                    rect.bottom = (int) (rect.bottom * hs);
                    rect.left = (int) (rect.left * ws);
                    rect.right = (int) (rect.right * ws);
                }
                faceRectList.addAll(faceInfoList);
            }

            postInvalidate();
        });

    }


    /**
     * @param canvas   需要被绘制的view的canvas
     * @param drawInfo 绘制信息
     */
    public static void drawFaceRect(Canvas canvas, RectInfo drawInfo) {
        if (canvas == null || drawInfo == null) {
            return;
        }
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(Color.GREEN);
        Path mPath = new Path();
        //左上
        Rect rect = drawInfo.getRect();
        mPath.moveTo(rect.left, rect.top + rect.height() / 4);
        mPath.lineTo(rect.left, rect.top);
        mPath.lineTo(rect.left + rect.width() / 4, rect.top);
        //右上
        mPath.moveTo(rect.right - rect.width() / 4, rect.top);
        mPath.lineTo(rect.right, rect.top);
        mPath.lineTo(rect.right, rect.top + rect.height() / 4);
        //右下
        mPath.moveTo(rect.right, rect.bottom - rect.height() / 4);
        mPath.lineTo(rect.right, rect.bottom);
        mPath.lineTo(rect.right - rect.width() / 4, rect.bottom);
        //左下
        mPath.moveTo(rect.left + rect.width() / 4, rect.bottom);
        mPath.lineTo(rect.left, rect.bottom);
        mPath.lineTo(rect.left, rect.bottom - rect.height() / 4);
        canvas.drawPath(mPath, paint);
    }
}