package pers.sweven.arcdemo.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import pers.sweven.arcdemo.Config;
import pers.sweven.arcdemo.R;
import pers.sweven.arcdemo.databinding.ActivityLaunchBinding;
import pers.sweven.mvvm.base.BaseActivity;
import pers.sweven.mvvm.base.BaseViewModel;

/**
 * Created by Sweven on 2020/8/21--15:37.
 */
public class LaunchActivity extends BaseActivity<ActivityLaunchBinding, BaseViewModel> {
    @Override
    protected int bindLayout() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initParams(Bundle bundle) {

    }

    @Override
    protected void initData() {
        getBinding().btn.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, MainActivity.class), Config.SCAN_FACE);
        });
        getBinding().register.setOnClickListener(v->{

        });
    }

    private int count = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.SCAN_FACE && resultCode == RESULT_OK) {
            assert data != null;
            String name = data.getStringExtra("name");
            String age = data.getStringExtra("age");
            String sex = data.getStringExtra("sex");
            getBinding().setName(count + ".这是" + name + ",年龄：" + age + ",sex:" + sex + ".");
            count++;
        }
    }
}
