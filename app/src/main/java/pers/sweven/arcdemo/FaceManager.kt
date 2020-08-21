package pers.sweven.arcdemo

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sweven.util.PreferenceUtil

/**
 * Created by Sweven on 2020/8/21--17:19.
 */
class FaceManager {
    companion object {
        @JvmStatic
        val instance = FaceManager()
    }

    var students: ArrayList<Student> = arrayListOf()
        get() {
            if (field.isNotEmpty()) {
                return field
            } else {
                return getStudents(App.get())
            }
        }

    private fun getStudents(context: Context): ArrayList<Student> {
        val shared = PreferenceUtil(context, "arc_demo")
        val json = shared.getString("students")
        return Gson().fromJson<ArrayList<Student>>(json, object : TypeToken<ArrayList<Student?>?>() {}.type)
    }

    fun saveStudent(context: Context,student: Student){
        val shared = PreferenceUtil(context, "arc_demo")
        students.add(student)
        shared.editor.putString("students",Gson().toJson(students)).apply()
    }

}