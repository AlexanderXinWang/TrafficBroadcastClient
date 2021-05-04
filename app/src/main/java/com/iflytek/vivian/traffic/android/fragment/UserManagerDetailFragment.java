package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;
import android.widget.EditText;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.user.UserDetailEvent;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

@Page(anim = CoreAnim.slide, name = "警员详情")
public class UserManagerDetailFragment extends BaseFragment {

    @BindView(R.id.user_detail_name)
    EditText name;
    @BindView(R.id.user_detail_id)
    EditText id;
    @BindView(R.id.user_detail_age)
    EditText age;
    @BindView(R.id.user_detail_role)
    EditText role;
    @BindView(R.id.user_detail_department)
    EditText department;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        UserClient.selectUser(getString(R.string.server_url), getArguments().getString("userId"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_manager_detail;
    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUserDetail(UserDetailEvent event) {
        if (event.isSuccess()) {
            User user = event.getData();
            name.setText(user.getName());
            id.setText(user.getId());
            age.setText(user.getAge());
            role.setText(user.getRole());
            department.setText(user.getDepartment());
        } else {
            // TODO 错误返回处理
        }
    }
}
