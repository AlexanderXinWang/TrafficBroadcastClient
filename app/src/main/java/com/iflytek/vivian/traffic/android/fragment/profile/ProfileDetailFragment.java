package com.iflytek.vivian.traffic.android.fragment.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.event.EventFindByUserIdEvent;
import com.iflytek.vivian.traffic.android.event.user.UserDeleteEvent;
import com.iflytek.vivian.traffic.android.event.user.UserDetailEvent;
import com.iflytek.vivian.traffic.android.event.user.UserUpdateEvent;
import com.iflytek.vivian.traffic.android.event.user.UserUploadImageEvent;
import com.iflytek.vivian.traffic.android.fragment.EventDetailFragment;
import com.iflytek.vivian.traffic.android.fragment.ReportedEventFragment;
import com.iflytek.vivian.traffic.android.utils.DataProvider;
import com.iflytek.vivian.traffic.android.utils.DateFormatUtil;
import com.iflytek.vivian.traffic.android.utils.StringUtil;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.samlss.broccoli.Broccoli;

@Page(anim = CoreAnim.slide, name = "账户详情")
public class ProfileDetailFragment extends BaseFragment {

    private static final String TAG = "UserManagerDetail";

    @BindView(R.id.profile_detail_name)
    TextView name;
    @BindView(R.id.profile_detail_id)
    TextView id;
    @BindView(R.id.profile_detail_age)
    TextView age;
    @BindView(R.id.profile_detail_role)
    TextView role;
    @BindView(R.id.profile_detail_place)
    TextView place;
    @BindView(R.id.profile_detail_department)
    TextView department;
    @BindView(R.id.profile_detail_image)
    RadiusImageView image;
//    @BindView(R.id.profile_detail_event_recyclerView)
//    RecyclerView recyclerView;
//    @BindView(R.id.profile_detail_event_refreshLayout)
//    SmartRefreshLayout refreshLayout;
    @BindView(R.id.profile_detail_reported)
    SuperTextView checkEvent;

    private User user = new User();

    private BroccoliSimpleDelegateAdapter<Event> mEventAdapter;
    private List<Event> eventList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        UserClient.selectUser(getString(R.string.server_url), getArguments().getString("userId"));
        EventClient.findEventByUserId(getString(R.string.server_url), getArguments().getString("userId"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile_detail;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        checkEvent.setOnSuperTextViewClickListener(superTextView ->
                openNewPage(ReportedEventFragment.class, "userId", getArguments().getString("userId")));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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
            Glide.with(getContext()).load(user.getImageUrl()).skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(image);
        } else {
            XToastUtils.error("加载用户详情信息错误！");
            Log.e(TAG, "加载用户详情信息错误" + event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFindEventByUserId(EventFindByUserIdEvent event) {
        if (event.isSuccess()) {
            if (event.getData().size() != 0) {
                eventList = event.getData();
            }
        } else {
            XToastUtils.error("加载当前用户已上报警情失败！");
        }
    }
}
