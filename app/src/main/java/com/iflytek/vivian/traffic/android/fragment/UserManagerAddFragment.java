package com.iflytek.vivian.traffic.android.fragment;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

@Page(anim = CoreAnim.none, name = "新增警员用户")
public class UserManagerAddFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_manager_add;
    }

    @Override
    protected void initViews() {

    }
}
