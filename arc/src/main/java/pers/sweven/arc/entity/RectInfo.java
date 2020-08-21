package pers.sweven.arc.entity;

import android.graphics.Rect;

/**
 * Created by Sweven on 2020/8/21--16:15.
 */
public class RectInfo {
    private Rect rect;
    private Object extra;

    public RectInfo() {
    }

    public RectInfo(Rect rect) {
        this.rect = rect;
    }

    public RectInfo(Rect rect, Object extra) {
        this.rect = rect;
        this.extra = extra;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
