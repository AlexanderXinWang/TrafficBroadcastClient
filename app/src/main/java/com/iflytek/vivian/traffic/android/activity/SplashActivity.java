/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.iflytek.vivian.traffic.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;

import com.alibaba.fastjson.JSON;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.utils.SettingUtils;
import com.iflytek.vivian.traffic.android.utils.StringUtil;
import com.iflytek.vivian.traffic.android.utils.TokenUtils;
import com.iflytek.vivian.traffic.android.utils.Utils;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.widget.activity.BaseSplashActivity;
import com.xuexiang.xutil.app.ActivityUtils;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * 启动页【无需适配屏幕大小】
 *
 * @author xuexiang
 * @since 2019-06-30 17:32
 */
public class SplashActivity extends BaseSplashActivity implements CancelAdapt {

    @Override
    protected long getSplashDurationMillis() {
        return 500;
    }

    /**
     * activity启动后的初始化
     */
    @Override
    protected void onCreateActivity() {
        initSplashView(R.drawable.xui_config_bg_splash);
        startSplash(false);
    }


    /**
     * 启动页结束后的动作
     */
    @Override
    protected void onSplashFinished() {
        if (SettingUtils.isAgreePrivacy()) {
            loginOrGoMainPage();
        } else {
            Utils.showPrivacyDialog(this, (dialog, which) -> {
                dialog.dismiss();
                SettingUtils.setIsAgreePrivacy(true);
                loginOrGoMainPage();
            });
        }
    }

    private void loginOrGoMainPage() {

        Context context = SplashActivity.this;

        if (TokenUtils.hasToken()) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("loginUser", 0);
            if (sharedPreferences != null) {
                Integer userIsAdmin = sharedPreferences.getInt("userIsAdmin", 0);
                String userId = sharedPreferences.getString("userId", "");
                // 判断用户角色进入相应界面
                if (userIsAdmin == 0) {
//                    Intent userIntent = new Intent(context, UserMainActivity.class);
//                    userIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    userIntent.putExtra("userId", userId);
//                    ActivityUtils.startActivity(userIntent);
                    ActivityUtils.startActivity(UserMainActivity.class);
                } else if (userIsAdmin == 1) {
//                    Intent adminIntent = new Intent(context, AdminMainActivity.class);
//                    adminIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    adminIntent.putExtra("userId", userId);
//                    ActivityUtils.startActivity(adminIntent);
                    ActivityUtils.startActivity(AdminMainActivity.class);
                }
            } else {
                ActivityUtils.startActivity(LoginActivity.class);
            }
        } else {
            ActivityUtils.startActivity(LoginActivity.class);
        }
        finish();
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return KeyboardUtils.onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event);
    }
}
