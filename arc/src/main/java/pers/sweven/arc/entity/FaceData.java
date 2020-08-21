package pers.sweven.arc.entity;

/**
 * Created by Sweven on 2020/8/21--16:28.
 */
public class FaceData {
    private String facePath;
    private byte[] faceData;
    private String value;
    private long id;

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public byte[] getFaceData() {
        return faceData;
    }

    public void setFaceData(byte[] faceData) {
        this.faceData = faceData;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
