package com.iflytek.vivian.traffic.android.fragment;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.user.UserSaveEvent;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

@Page(anim = CoreAnim.slide, name = "新增警员用户")
public class UserManagerAddFragment extends BaseFragment {

    private static final String TAG = "UserManagerAdd";

    @BindView(R.id.user_add_image)
    RadiusImageView image;
    @BindView(R.id.user_add_name)
    EditText name;
    @BindView(R.id.user_add_id)
    EditText id;
    @BindView(R.id.user_add_age)
    EditText age;
    @BindView(R.id.user_add_role)
    EditText role;
    @BindView(R.id.user_add_place)
    EditText place;
    @BindView(R.id.user_add_department)
    EditText depart;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_manager_add;
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
    @OnClick(R.id.user_add_save)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_add_save:
                // TODO 入参验证
                user = new User();
                user.setImageUrl("");
                user.setId(id.getText().toString());
                user.setName(name.getText().toString());
                user.setAge(age.getText().toString());
                user.setRole(role.getText().toString());
                user.setPlace(place.getText().toString());
                user.setDepartment(place.getText().toString());
                user.setPassword(id.getText().toString());

                new MaterialDialog.Builder(getContext()).title("确认保存？").positiveText("确认").negativeText("取消")
                        .onPositive(((dialog, which) -> UserClient.saveUser(getString(R.string.server_url), user))).show();

                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSaveUser(UserSaveEvent event) {
        if (event.isSuccess()) {
            XToastUtils.success("新增警员用户成功，请刷新");
            popToBack();
        } else {
            XToastUtils.error("新增警员用户失败！");
            Log.e(TAG, "新增警员用户失败" + event.getErrorMessage());
        }
    }
}
