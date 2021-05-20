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

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.fragment.AboutFragment;
import com.iflytek.vivian.traffic.android.fragment.EventFragment;
import com.iflytek.vivian.traffic.android.fragment.EventReportFragment;
import com.iflytek.vivian.traffic.android.fragment.SettingsFragment;
import com.iflytek.vivian.traffic.android.fragment.UserDetailFragment;
import com.iflytek.vivian.traffic.android.fragment.profile.ProfileFragment;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.core.BaseActivity;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.utils.DataProvider;
import com.iflytek.vivian.traffic.android.utils.Utils;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.widget.imageview.ImageLoader;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.strategy.DiskCacheStrategyEnum;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.ClickUtils;
import com.xuexiang.xutil.common.CollectionUtils;
import com.xuexiang.xutil.display.Colors;

import java.io.IOException;

import butterknife.BindView;

/**
 * 用户程序主页面
 */
public class UserMainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener, ClickUtils.OnClick2ExitListener, Toolbar.OnMenuItemClickListener {

    private static final String TAG = "UserMainActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    /**
     * 底部导航栏
     */
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;
    /**
     * 侧边栏
     */
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private String[] mTitles;

    private String userId;
    private String userName;
    private String userAge;
    private String userRole;
    private String userDepart;
    private String imageUrl;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initData();
        initViews();
        initListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    private void initViews() {
        mTitles = ResUtils.getStringArray(R.array.home_titles);
        toolbar.setTitle(mTitles[0]);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this);

        initHeader();

        //主页内容填充
        BaseFragment[] fragments = new BaseFragment[]{
//                new NewsFragment(),
                new EventFragment(),
                new EventReportFragment(),
                new ProfileFragment()
        };
        FragmentAdapter<BaseFragment> adapter = new FragmentAdapter<>(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(mTitles.length - 1);
        viewPager.setAdapter(adapter);

//        GuideTipsDialog.showTips(this);
    }

    private void initData() {

        SharedPreferences preferences = getSharedPreferences("loginUser", MODE_PRIVATE);
        userId = preferences.getString("userId", "");
        userName = preferences.getString("userName", "");
        userRole = preferences.getString("userRole", "");
        userDepart = preferences.getString("userDepart", "");
        imageUrl = preferences.getString("imageUrl", "");

        Log.i(TAG, "当前用户id" + userId);

//        loginUser.setId(userId);
//        loginUser.setName(userName);
//        loginUser.setAge("");
//        loginUser.setRole(userRole);
//        loginUser.setDepartment(userDepart);

//        loginUser.setId(preferences.getString("userId", ""));
//        loginUser.setName(preferences.getString("userName", ""));
//        loginUser.setRole(preferences.getString("userRole", ""));
//        loginUser.setDepartment(preferences.getString("userDepart", ""));

//        UserClient.selectUser(getString(R.string.server_url), userId);

//        String userName = getIntent().getStringExtra("userName");
//        String userRole = getIntent().getStringExtra("userRole");
//        String userDepart = getIntent().getStringExtra("userDepart");
//        if (StringUtil.isNotEmpty(userId)) {
//            loginUser.setId(userId);
//        }
//        if (StringUtil.isNotEmpty(userName)) {
//            loginUser.setName(userName);
//        }
//        if (StringUtil.isNotEmpty(userRole)) {
//            loginUser.setRole(userRole);
//        }
//        if (StringUtil.isNotEmpty(userDepart)) {
//            loginUser.setDepartment(userDepart);
//        }
//        Context context = UserMainActivity.this;
//        SharedPreferences localUser = context.getSharedPreferences("loginUser", 0);
//        loginUser.setId(localUser.getString("userId",""));
//        loginUser.setName(localUser.getString("userName", ""));
//        loginUser.setRole(localUser.getString("userRole", ""));
//        loginUser.setDepartment(localUser.getString("userDepart", ""));
    }

    private void initHeader() {
        navView.setItemIconTintList(null);
        View headerView = navView.getHeaderView(0);
        LinearLayout navHeader = headerView.findViewById(R.id.nav_header);
        RadiusImageView ivAvatar = headerView.findViewById(R.id.iv_avatar);
        TextView tvId = headerView.findViewById(R.id.tv_user_id);
        TextView tvAvatar = headerView.findViewById(R.id.tv_avatar);
        TextView tvSign = headerView.findViewById(R.id.tv_sign);

        if (Utils.isColorDark(ThemeUtils.resolveColor(this, R.attr.colorAccent))) {
            tvAvatar.setTextColor(Colors.WHITE);
            tvSign.setTextColor(Colors.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivAvatar.setImageTintList(ResUtils.getColors(R.color.xui_config_color_white));
            }
        } else {
            tvAvatar.setTextColor(ThemeUtils.resolveColor(this, R.attr.xui_config_color_title_text));
            tvSign.setTextColor(ThemeUtils.resolveColor(this, R.attr.xui_config_color_explain_text));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivAvatar.setImageTintList(ResUtils.getColors(R.color.xui_config_color_gray_3));
            }
        }

        // TODO: 2019-10-09 初始化数据
//        ivAvatar.setImageResource(R.drawable.ic_default_head);
//        try {
//            ivAvatar.setImageBitmap(DataProvider.getBitmap(imageUrl));
//        } catch (IOException e) {
//            ivAvatar.setImageResource(R.drawable.ic_default_head);
//            Log.e(TAG, "加载头像图片错误" + imageUrl + e.getMessage());
//        }
        Glide.with(this).load(imageUrl).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE).into(ivAvatar);

//        ImageLoader.get().loadImage(findViewById(R.id.iv_avatar), imageUrl, DiskCacheStrategyEnum.AUTOMATIC);
        tvId.setText(userId);
        tvAvatar.setText(userName);
        tvSign.setText(userRole + "   " + userDepart);
        navHeader.setOnClickListener(this);
    }

    protected void initListeners() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //侧边栏点击事件
        navView.setNavigationItemSelectedListener(menuItem -> {
            UserClient.getUserImage(getString(R.string.server_url), userId);
            if (menuItem.isCheckable()) {
                drawerLayout.closeDrawers();
                return handleNavigationItemSelected(menuItem);
            } else {
                switch (menuItem.getItemId()) {
                    case R.id.nav_settings:
                        openNewPage(SettingsFragment.class);
                        break;
                    case R.id.nav_about:
                        openNewPage(AboutFragment.class);
                        break;
                    default:
                        XToastUtils.toast("点击了:" + menuItem.getTitle());
                        break;
                }
            }
            return true;
        });

        //主页事件监听
        viewPager.addOnPageChangeListener(this);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    /**
     * 处理侧边栏点击事件
     *
     * @param menuItem
     * @return
     */
    private boolean handleNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        if (index != -1) {
            toolbar.setTitle(menuItem.getTitle());
            viewPager.setCurrentItem(index, false);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Utils.showPrivacyDialog(this, null);
                break;
            default:
                break;
        }
        return false;
    }

    @SingleClick
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_header:
//                XToastUtils.toast("点击头部！");
//                Bundle bundle = new Bundle();
//                bundle.putString("userId", userId);
////                openPage(UserDetailFragment.class, bundle);
//                openPage("UserDetailFragment", bundle, CoreAnim.fade);
                break;
            default:
                break;
        }
    }

    //=============ViewPager===================//

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        MenuItem item = bottomNavigation.getMenu().getItem(position);
        toolbar.setTitle(item.getTitle());
        item.setChecked(true);

        updateSideNavStatus(item);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    //================Navigation================//

    /**
     * 底部导航栏点击事件
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = CollectionUtils.arrayIndexOf(mTitles, menuItem.getTitle());
        if (index != -1) {
            toolbar.setTitle(menuItem.getTitle());
            viewPager.setCurrentItem(index, false);

            updateSideNavStatus(menuItem);
            return true;
        }
        return false;
    }

    /**
     * 更新侧边栏菜单选中状态
     *
     * @param menuItem
     */
    private void updateSideNavStatus(MenuItem menuItem) {
        MenuItem side = navView.getMenu().findItem(menuItem.getItemId());
        if (side != null) {
            side.setChecked(true);
        }
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ClickUtils.exitBy2Click(2000, this);
        }
        return true;
    }

    @Override
    public void onRetry() {
        XToastUtils.toast("再按一次退出程序");
    }

    @Override
    public void onExit() {
        XUtil.exitApp();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLoginUser(UserDetailEvent event) {
//        if (event.isSuccess()) {
//            loginUser = event.getData();
//        } else {
//            new MaterialDialog.Builder(this).iconRes(R.drawable.ic_menu_about).title("服务器错误")
//                    .content("获取当前用户信息失败！").positiveText("确定").show();
//        }
//    }

}
