package pers.sweven.arcdemo.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import pers.sweven.arc.entity.FaceData;
import pers.sweven.arc.entity.FaceResult;
import pers.sweven.arc.utils.FaceHelper;
import pers.sweven.arcdemo.FaceManager;
import pers.sweven.arcdemo.Student;

/**
 * Created by Sweven on 2020/8/21--16:27.
 */
public class MyFaceHelper extends FaceHelper {
    private List<Student> list = new ArrayList<>();

    public MyFaceHelper(Context context) {
        super(context);
        list.add(new Student(11L,"sweven","18岁","男"));
        list.add(new Student(10L,"zhang","7岁","女"));
    }

    @Override
    protected void similarDeal(FaceData data, List<FaceResult> faceResultList) {
        Student a=new Student();
        for (Student student : list) {
            if (student.getId()==data.getId()){
                a = student;
            }
        }
        faceResultList.add(new FaceResult(data.getValue(),a));
    }
}
