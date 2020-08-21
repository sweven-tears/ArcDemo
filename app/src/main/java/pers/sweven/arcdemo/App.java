package pers.sweven.arcdemo;

import android.app.Application;

import pers.sweven.arc.utils.FaceHelper;

/**
 * Created by Sweven on 2020/8/21--16:31.
 */
public class App extends Application {
    private static App app;

    public static App get() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FaceHelper.active(this, Config.ARC_APP_ID, Config.ARC_SDK_KEY);
        app = this;
    }
}
