package pers.sweven.arc.entity;

/**
 * Created by Sweven on 2020/8/21--16:18.
 */
public class FaceResult {
    private String value;
    private Object extra;

    public FaceResult() {
    }

    public FaceResult(String value) {
        this.value = value;
    }

    public FaceResult(String value, Object extra) {
        this.value = value;
        this.extra = extra;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
