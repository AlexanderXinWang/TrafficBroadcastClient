package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

/**
 * 用户管理
 */
@Page(anim = CoreAnim.none)
public class UserManagerFragment extends BaseFragment {


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_manager;
    }

    @Override
    protected void initViews() {

    }
}