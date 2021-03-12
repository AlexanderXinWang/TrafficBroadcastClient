package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.base.delegate.SimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.User;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;

/**
 * 用户管理
 */
@Page(anim = CoreAnim.none)
public class UserManagerFragment extends BaseFragment {

    private SimpleDelegateAdapter<User> mUserAdapter;

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_manager;
    }

    @Override
    protected void initViews() {

    }
}