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

package com.iflytek.vivian.traffic.android.fragment.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.event.GetUserImageEvent;
import com.iflytek.vivian.traffic.android.event.user.UserUpdateImageEvent;
import com.iflytek.vivian.traffic.android.event.user.UserUploadImageEvent;
import com.iflytek.vivian.traffic.android.fragment.AboutFragment;
import com.iflytek.vivian.traffic.android.fragment.ReportedEventFragment;
import com.iflytek.vivian.traffic.android.fragment.SettingsFragment;
import com.iflytek.vivian.traffic.android.utils.StringUtil;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 个人信息页面
 */
@Page(anim = CoreAnim.none)
public class ProfileFragment extends BaseFragment implements SuperTextView.OnSuperTextViewClickListener {

    private static final String TAG = "ProfileFragment";

    @BindView(R.id.profile_head_pic)
    RadiusImageView rivHeadPic;
    @BindView(R.id.profile_image)
    SuperTextView profileImage;
    @BindView(R.id.profile_detail)
    SuperTextView profileDetail;    @BindView(R.id.profile_reported)
    SuperTextView reportedEvent;
    @BindView(R.id.menu_settings)
    SuperTextView menuSettings;
    @BindView(R.id.menu_about)
    SuperTextView menuAbout;

    private String userId = new String();

    private List<LocalMedia> mSelectList = new ArrayList<>();

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 布局的资源id
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initData();
    }

    private void initData() {
        SharedPreferences preferences = getActivity().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        userId = preferences.getString("userId", "");
        UserClient.getUserImage(getString(R.string.server_url), userId);
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {
        profileImage.setOnSuperTextViewClickListener(this);
        profileDetail.setOnSuperTextViewClickListener(this);
        reportedEvent.setOnSuperTextViewClickListener(this);
        menuSettings.setOnSuperTextViewClickListener(this);
        menuAbout.setOnSuperTextViewClickListener(this);
    }

    @SingleClick
    @Override
    public void onClick(SuperTextView view) {
        switch(view.getId()) {
            case R.id.profile_image:
                new MaterialDialog.Builder(getContext()).title("确认修改头像？").positiveText("确认").negativeText("取消")
                        .onPositive(((dialog, which) -> {
                            PictureSelector.create(this)
                                    .openGallery(PictureMimeType.ofImage())
                                    .maxSelectNum(1)
                                    .selectionMode(PictureConfig.SINGLE)
                                    .forResult(PictureConfig.CHOOSE_REQUEST);
                        })).show();
                break;
            case R.id.profile_detail:
                openNewPage(ProfileDetailFragment.class, "userId", userId);
                break;
            case R.id.profile_reported:
                openNewPage(ReportedEventFragment.class, "userId", userId);
                break;
            case R.id.menu_settings:
                openNewPage(SettingsFragment.class);
                break;
            case R.id.menu_about:
                openNewPage(AboutFragment.class);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 上传
                    if (StringUtil.isNotEmpty(userId)) {
                        mSelectList = PictureSelector.obtainMultipleResult(data);
                        LocalMedia media = mSelectList.get(0);
                        String path = media.getPath();
                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.drawable.ic_default_head)
                                .diskCacheStrategy(DiskCacheStrategy.ALL);
                        Glide.with(getContext())
                                .load(path)
                                .apply(options)
                                .into(rivHeadPic);

                        File file = new File(path);
                        UserClient.uploadImage(getString(R.string.server_url), file, userId);
                    } else {
                        Glide.with(getContext()).load(R.drawable.ic_default_head).into(rivHeadPic);
                        XToastUtils.error("请先输入用户编号！");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserImage(GetUserImageEvent event) {
        if (event.isSuccess()) {
            if (StringUtil.isNotEmpty(event.getData())) {
                Glide.with(getContext()).load(event.getData()).skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).into(rivHeadPic);
            }
        } else {
//            XToastUtils.error("加载用户头像失败");
            Log.e(TAG, "加载用户头像失败");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadImage(UserUploadImageEvent event) {
        if (event.isSuccess()) {
            XToastUtils.success("上头像头成功！重新登陆后生效");
            String imageUrl = event.getData();

            if (StringUtil.isNotEmpty(imageUrl)) {
                User user = new User();
                user.setId(userId);
                user.setImageUrl(imageUrl);
                UserClient.updateUserImage(getString(R.string.server_url), user);
            } else {
                XToastUtils.error("上传头像失败！");
            }
        } else {
            XToastUtils.error("上传头像失败！");
            Log.e(TAG, "上传头像失败" + event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateImage(UserUpdateImageEvent event) {
        if (event.isSuccess()) {
            XToastUtils.success("修改头像成功！重新登陆后生效");
        } else {
            XToastUtils.error("修改头像失败！");
            Log.e(TAG, "修改头像失败" + event.getErrorMessage());
        }

    }
}
