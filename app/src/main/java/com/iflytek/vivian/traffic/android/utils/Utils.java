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

package com.iflytek.vivian.traffic.android.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.activity.AdminMainActivity;
import com.iflytek.vivian.traffic.android.activity.UserMainActivity;
import com.iflytek.vivian.traffic.android.core.webview.AgentWebActivity;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.data.DateUtils;
import com.xuexiang.xutil.file.FileIOUtils;
import com.xuexiang.xutil.file.FileUtils;

import java.io.File;

import static com.iflytek.vivian.traffic.android.core.webview.AgentWebFragment.KEY_URL;

/**
 * 工具类
 *
 * @author xuexiang
 * @since 2020-02-23 15:12
 */
public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 这里填写你的应用隐私政策网页地址
     */
    private static final String PRIVACY_URL = "https://gitee.com/xuexiangjys/TemplateAppProject/raw/master/LICENSE";

    /**
     * 显示隐私政策的提示
     *
     * @param context
     * @param submitListener 同意的监听
     * @return
     */
    public static Dialog showPrivacyDialog(Context context, MaterialDialog.SingleButtonCallback submitListener) {
        MaterialDialog dialog = new MaterialDialog.Builder(context).title(R.string.title_reminder).autoDismiss(false).cancelable(false)
                .positiveText(R.string.lab_agree).onPositive((dialog1, which) -> {
                    if (submitListener != null) {
                        submitListener.onClick(dialog1, which);
                    } else {
                        dialog1.dismiss();
                    }
                })
                .negativeText(R.string.lab_disagree).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        DialogLoader.getInstance().showConfirmDialog(context, ResUtils.getString(R.string.title_reminder), String.format(ResUtils.getString(R.string.content_privacy_explain_again), ResUtils.getString(R.string.app_name)), ResUtils.getString(R.string.lab_look_again), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                showPrivacyDialog(context, submitListener);
                            }
                        }, ResUtils.getString(R.string.lab_still_disagree), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                DialogLoader.getInstance().showConfirmDialog(context, ResUtils.getString(R.string.content_think_about_it_again), ResUtils.getString(R.string.lab_look_again), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        showPrivacyDialog(context, submitListener);
                                    }
                                }, ResUtils.getString(R.string.lab_exit_app), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        XUtil.exitApp();
                                    }
                                });
                            }
                        });
                    }
                }).build();
        dialog.setContent(getPrivacyContent(context));
        //开始响应点击事件
        dialog.getContentView().setMovementMethod(LinkMovementMethod.getInstance());
        dialog.show();
        return dialog;
    }

    /**
     * 初始化主题
     */
    public static void initTheme(Activity activity) {
        if (SettingSPUtils.getInstance().isUseCustomTheme()) {
            activity.setTheme(R.style.CustomAppTheme);
        } else {
            XUI.initTheme(activity);
        }
    }

    /**
     * 同步首页状态,未启动就启动
     */
    public static void syncAdminMainPageStatus() {
        if (!XUtil.getActivityLifecycleHelper().isActivityExist(AdminMainActivity.class)) {
            ActivityUtils.startActivity(AdminMainActivity.class);
        }
    }
    public static void syncUserMainPageStatus() {
        if (!XUtil.getActivityLifecycleHelper().isActivityExist(UserMainActivity.class)) {
            ActivityUtils.startActivity(UserMainActivity.class);
        }
    }

    /**
     * @return 隐私政策说明
     */
    private static SpannableStringBuilder getPrivacyContent(Context context) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder()
                .append("    欢迎来到").append(ResUtils.getString(R.string.app_name)).append("!\n")
                .append("    我们深知个人信息对你的重要性，也感谢你对我们的信任。\n")
                .append("    为了更好地保护你的权益，同时遵守相关监管的要求，我们将通过");
        stringBuilder.append(getPrivacyLink(context, PRIVACY_URL))
                .append("向你说明我们会如何收集、存储、保护、使用及对外提供你的信息，并说明你享有的权利。\n")
                .append("    更多详情，敬请查阅")
                .append(getPrivacyLink(context, PRIVACY_URL))
                .append("全文。");
        return stringBuilder;
    }

    /**
     * @param context 隐私政策的链接
     * @return
     */
    private static SpannableString getPrivacyLink(Context context, String privacyUrl) {
        String privacyName = String.format(ResUtils.getString(R.string.lab_privacy_name), ResUtils.getString(R.string.app_name));
        SpannableString spannableString = new SpannableString(privacyName);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                goWeb(context, privacyUrl);
            }
        }, 0, privacyName.length(), Spanned.SPAN_MARK_MARK);
        return spannableString;
    }


    /**
     * 请求浏览器
     *
     * @param url
     */
    public static void goWeb(Context context, final String url) {
        Intent intent = new Intent(context, AgentWebActivity.class);
        intent.putExtra(KEY_URL, url);
        context.startActivity(intent);
    }


    /**
     * 是否是深色的颜色
     *
     * @param color
     * @return
     */
    public static boolean isColorDark(@ColorInt int color) {
        double darkness =
                1
                        - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color))
                        / 255;
        return darkness >= 0.382;
    }

    //==========图片选择===========//

    /**
     * 获取图片选择的配置
     *
     * @param fragment
     * @return
     */
    public static PictureSelectionModel getPictureSelector(Fragment fragment) {
        return PictureSelector.create(fragment)
                .openGallery(PictureMimeType.ofImage())
                .theme(SettingSPUtils.getInstance().isUseCustomTheme() ? R.style.XUIPictureStyle_Custom : R.style.XUIPictureStyle)
                .maxSelectNum(8)
                .minSelectNum(1)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .isCamera(true)
                .enableCrop(false)
                .compress(true)
                .previewEggs(true);
    }

    public static PictureSelectionModel getPictureSelector(Activity activity) {
        return PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .theme(SettingSPUtils.getInstance().isUseCustomTheme() ? R.style.XUIPictureStyle_Custom : R.style.XUIPictureStyle)
                .maxSelectNum(8)
                .minSelectNum(1)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .isCamera(true)
                .enableCrop(false)
                .compress(true)
                .previewEggs(true);
    }

    //==========拍照===========//

    public static final String JPEG = ".jpeg";

    /**
     * 处理拍照的回调
     *
     * @param data
     * @return
     */
    public static String handleOnPictureTaken(byte[] data) {
        return handleOnPictureTaken(data, JPEG);
    }

    /**
     * 处理拍照的回调
     *
     * @param data
     * @return
     */
    public static String handleOnPictureTaken(byte[] data, String fileSuffix) {
        String picPath = FileUtils.getDiskCacheDir() + "/images/" + DateUtils.getNowMills() + fileSuffix;
        boolean result = FileIOUtils.writeFileFromBytesByStream(picPath, data);
        return result ? picPath : "";
    }

    public static String getImageSavePath() {
        return FileUtils.getDiskCacheDir("images") + File.separator + DateUtils.getNowMills() + JPEG;
    }

}
