package pers.sweven.arcdemo.view;

import android.os.Bundle;

import pers.sweven.arcdemo.R;
import pers.sweven.arcdemo.databinding.ActivityRegisterBinding;
import pers.sweven.mvvm.base.BaseActivity;
import pers.sweven.mvvm.base.BaseViewModel;

/**
 * Created by Sweven on 2020/8/21--17:58.
 */
public class RegisterActivity extends BaseActivity<ActivityRegisterBinding, BaseViewModel> {
    @Override
    protected int bindLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initParams(Bundle bundle) {

    }

    @Override
    protected void initData() {

    }
}
