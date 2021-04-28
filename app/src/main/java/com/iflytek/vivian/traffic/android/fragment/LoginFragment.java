
package com.iflytek.vivian.traffic.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.activity.AdminMainActivity;
import com.iflytek.vivian.traffic.android.activity.UserMainActivity;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.user.UserLoginEvent;
import com.iflytek.vivian.traffic.android.utils.RandomUtils;
import com.iflytek.vivian.traffic.android.utils.SettingUtils;
import com.iflytek.vivian.traffic.android.utils.TokenUtils;
import com.iflytek.vivian.traffic.android.utils.Utils;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xutil.app.ActivityUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 登录页面
 *
 * @author xuexiang
 * @since 2019-11-17 22:15
 */
@Page(anim = CoreAnim.none)
public class LoginFragment extends BaseFragment {

    @BindView(R.id.et_user_name)
    MaterialEditText etUsername;
    @BindView(R.id.et_password)
    MaterialEditText etPassword;

    private CountDownButtonHelper mCountDownHelper;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle()
                .setImmersive(true);
        titleBar.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTitle("");
        titleBar.setLeftImageDrawable(ResUtils.getVectorDrawable(getContext(), R.drawable.ic_login_close));
        titleBar.setActionTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));

        /*titleBar.addAction(new TitleBar.TextAction(R.string.title_jump_login) {
            @Override
            public void performAction(View view) {
//                onLoginSuccess();
            }
        });*/
        return titleBar;
    }

    @Override
    protected void initViews() {
        // mCountDownHelper = new CountDownButtonHelper(btnGetVerifyCode, 60);

        //隐私政策弹窗
        if (!SettingUtils.isAgreePrivacy()) {
            Utils.showPrivacyDialog(getContext(), (dialog, which) -> {
                dialog.dismiss();
                SettingUtils.setIsAgreePrivacy(true);
            });
        }
    }

    /**
     * 单击事件响应
     * @param view
     */
    @SingleClick
    @OnClick({R.id.btn_login, R.id.tv_forget_password, R.id.tv_user_protocol, R.id.tv_privacy_protocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (etUsername.validate()) {
                    if (etPassword.validate()) {
//                        userLogin(etUsername.getEditValue(), etPassword.getEditValue());
                        User user = new User(etUsername.getEditValue(), etPassword.getEditValue());
                        // 用户登录
                        UserClient.userLogin(getString(R.string.server_url), user);
                    }
                }
                break;
            case R.id.tv_forget_password:
                XToastUtils.info("忘记密码");
                break;
            case R.id.tv_user_protocol:
                XToastUtils.info("用户协议");
                break;
            case R.id.tv_privacy_protocol:
                XToastUtils.info("隐私政策");
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    /**
     * 登录成功的处理
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(UserLoginEvent event) {

        String token = RandomUtils.getRandomNumbersAndLetters(16);

        setLoginToken(event.getData());

        if (TokenUtils.handleLoginSuccess(token)) {
            if (event.isSuccess()) {
                Intent intent = new Intent();
                intent.putExtra("data", JSON.toJSONString(event.getData()));
                popToBack();    // 弹出当前framework
                /**
                 * 区分用户类型
                 * 普通用户 —— 0
                 * 管理员 —— 1
                 */
                if (event.getData().getIsAdmin() == 0) {
                    ActivityUtils.startActivity(UserMainActivity.class);
                } else {
                    ActivityUtils.startActivity(AdminMainActivity.class);
                }
            } else {
                new MaterialDialog.Builder(getContext()).iconRes(R.drawable.ic_menu_about).title("登陆失败")
                        .content("用户名或密码错误！").positiveText("确定").show();
            }
        }
    }


    @Override
    public void onDestroyView() {
        if (mCountDownHelper != null) {
            mCountDownHelper.recycle();
        }
        super.onDestroyView();
    }

    public void setLoginToken(User user) {
        // 生成的token与当前用户信息保存到本地
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("loginToken", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", user.getId());
        editor.putString("userName", user.getName());
        editor.putInt("userIsAdmin", user.getIsAdmin());
        editor.putString("userAge", user.getAge());
        editor.putString("userDepart", user.getDepartment());
        editor.putString("userRole", user.getRole());
        editor.commit();
    }

    /**
     * 获取验证码
     */
    private void getVerifyCode(String phoneNumber) {
        // TODO: 2020/8/29 这里只是界面演示而已
        XToastUtils.warning("只是演示，验证码请随便输");
        mCountDownHelper.start();
    }

    /**
     * 根据验证码登录
     *
     * @param phoneNumber 手机号
     * @param verifyCode  验证码
     */
    private void loginByVerifyCode(String phoneNumber, String verifyCode) {
        // TODO: 2020/8/29 这里只是界面演示而已
//        onLoginSuccess();
    }
}

