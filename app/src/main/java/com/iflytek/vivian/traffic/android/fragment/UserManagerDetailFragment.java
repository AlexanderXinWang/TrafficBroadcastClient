package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.user.UserDeleteEvent;
import com.iflytek.vivian.traffic.android.event.user.UserDetailEvent;
import com.iflytek.vivian.traffic.android.event.user.UserUpdateEvent;
import com.iflytek.vivian.traffic.android.utils.DataProvider;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Page(anim = CoreAnim.slide, name = "警员详情")
public class UserManagerDetailFragment extends BaseFragment {

    private static final String TAG = "UserManagerDetail";

    @BindView(R.id.user_detail_name)
    EditText name;
    @BindView(R.id.user_detail_id)
    EditText id;
    @BindView(R.id.user_detail_age)
    EditText age;
    @BindView(R.id.user_detail_role)
    EditText role;
    @BindView(R.id.user_detail_place)
    EditText place;
    @BindView(R.id.user_detail_department)
    EditText department;
    @BindView(R.id.user_detail_image)
    RadiusImageView image;

    private User user = new User();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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

    @SingleClick
    @OnClick({R.id.user_detail_update, R.id.user_detail_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_detail_update:
                user = new User();
                // TODO
                user.setImageUrl("");
                user.setId(id.getText().toString());
                user.setName(name.getText().toString());
                user.setRole(role.getText().toString());
                user.setPlace(place.getText().toString());
                user.setDepartment(department.getText().toString());

                new MaterialDialog.Builder(getContext()).title("确认修改？").positiveText("确认").negativeText("取消")
                        .onPositive((dialog, which) -> UserClient.updateUser(getString(R.string.server_url), user)).show();
                break;
            case R.id.user_detail_delete:
                List<String> usersToDelete = new ArrayList<>();
                usersToDelete.add(getArguments().getString("userId"));
                new MaterialDialog.Builder(getContext()).title("确认删除？").positiveText("确认").negativeText("取消")
                        .onPositive((dialog, which) -> UserClient.deleteUser(getString(R.string.server_url), usersToDelete)).show();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUserDetail(UserDetailEvent event) {
        if (event.isSuccess()) {
            user = event.getData();
            name.setText(user.getName());
            id.setText(user.getId());
            age.setText(user.getAge());
            role.setText(user.getRole());
            department.setText(user.getDepartment());
            try {
                image.setImageBitmap(DataProvider.getBitmap(user.getImageUrl()));
            } catch (IOException e) {
                image.setImageResource(R.drawable.ic_default_head);
                Log.e(TAG, "加载头像图片错误" + e.getMessage());
            }
        } else {
            XToastUtils.error("加载用户详情信息错误！");
            Log.e(TAG, "加载用户详情信息错误" + event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateUser(UserUpdateEvent event) {
        if (event.isSuccess()) {
            XToastUtils.success("更新警员信息成功，请刷新");
            popToBack();
        } else {
            XToastUtils.error("更新警员信息失败！请重试");
            Log.e(TAG, "更新警员信息失败" + event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteUser(UserDeleteEvent event) {
        if (event.isSuccess()) {
            XToastUtils.success("删除警员用户成功，请刷新");
            popToBack();
        } else {
            XToastUtils.error("删除警员用户失败！请重试");
            Log.e(TAG, "删除警员用户失败" + event.getErrorMessage());
        }
    }
}
