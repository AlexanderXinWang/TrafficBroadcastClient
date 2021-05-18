

package com.iflytek.vivian.traffic.android.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.user.UserCheckPwdEvent;
import com.iflytek.vivian.traffic.android.event.user.UserUpdatePwdEvent;
import com.iflytek.vivian.traffic.android.utils.TokenUtils;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.XUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;


@Page(name = "设置")
public class SettingsFragment extends BaseFragment implements SuperTextView.OnSuperTextViewClickListener {

    private static final String TAG = "SettingsFragment";

    @BindView(R.id.menu_common)
    SuperTextView menuCommon;
    @BindView(R.id.menu_privacy)
    SuperTextView menuPrivacy;
    @BindView(R.id.menu_push)
    SuperTextView menuPush;
    @BindView(R.id.menu_helper)
    SuperTextView menuHelper;
    @BindView(R.id.menu_change_password)
    SuperTextView menuChangeAccount;
    @BindView(R.id.menu_logout)
    SuperTextView menuLogout;

    private String userId;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
        EventBus.getDefault().register(this);
        initData();
    }

    private void initData() {
        SharedPreferences preferences = getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        userId = preferences.getString("userId", "");
        Log.i(TAG, "当前用户Id" + userId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initViews() {
        menuCommon.setOnSuperTextViewClickListener(this);
        menuPrivacy.setOnSuperTextViewClickListener(this);
        menuPush.setOnSuperTextViewClickListener(this);
        menuHelper.setOnSuperTextViewClickListener(this);
        menuChangeAccount.setOnSuperTextViewClickListener(this);
        menuLogout.setOnSuperTextViewClickListener(this);
    }

    @SingleClick
    @Override
    public void onClick(SuperTextView superTextView) {
        switch (superTextView.getId()) {
            case R.id.menu_common:
            case R.id.menu_privacy:
            case R.id.menu_push:
            case R.id.menu_helper:
                XToastUtils.toast(superTextView.getLeftString());
                break;
            case R.id.menu_change_password:
                // TODO
//                XToastUtils.toast(superTextView.getCenterString());
                checkOldPwdDialog();
                break;
            case R.id.menu_logout:
                DialogLoader.getInstance().showConfirmDialog(
                        getContext(),
                        getString(R.string.lab_logout_confirm),
                        getString(R.string.lab_yes),
                        (dialog, which) -> {
                            dialog.dismiss();
                            XUtil.getActivityLifecycleHelper().exit();
                            TokenUtils.handleLogoutSuccess();
                        },
                        getString(R.string.lab_no),
                        (dialog, which) -> dialog.dismiss()
                );
                break;
            default:
                break;
        }
    }

    /**
     * 带输入框的对话框
     */
    private void checkOldPwdDialog() {
        new MaterialDialog.Builder(getContext())
                .iconRes(R.drawable.icon_warning)
                .title("修改密码")
                .inputType(
                        InputType.TYPE_CLASS_TEXT
                                | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input(
                        getString(R.string.hint_please_input_old_password),
                        "",
                        false,
                        ((dialog, input) -> {
                        }))
//                .inputRange(3, 5)
                .positiveText("继续")
                .negativeText("取消")
                .onPositive((dialog, which) -> {
                    User user = new User();
                    password = dialog.getInputEditText().getText().toString();
                    user.setId(userId);
                    user.setPassword(password);
                    UserClient.checkOldPassword(getString(R.string.server_url), user);
                })
                .cancelable(true)
                .show();
    }

    public void updatePwdDialog() {
        new MaterialDialog.Builder(getContext())
                .title("修改密码")
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input(getString(R.string.hint_please_input_new_password),
                        "",
                        false,
                        ((dialog, input) -> {
//                            password = input.toString();
                        }))
                .positiveText("确认")
                .negativeText("取消")
                .onPositive((dialog, which) -> {
                    User user = new User();
                    password = dialog.getInputEditText().getText().toString();
                    user.setId(userId);
                    user.setPassword(password);
                    UserClient.updatePassword(getString(R.string.server_url), user);
                })
                .cancelable(true)
                .show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckPassword(UserCheckPwdEvent event) {
        if (event.isSuccess()) {
            updatePwdDialog();
        } else {
            XToastUtils.error("密码错误");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatePassword(UserUpdatePwdEvent event) {
        if (event.isSuccess()) {
            XToastUtils.success("修改密码成功，请重新登录");
            XUtil.getActivityLifecycleHelper().exit();
            TokenUtils.handleLogoutSuccess();
        } else {
            XToastUtils.error("修改密码失败");
        }
    }
}
