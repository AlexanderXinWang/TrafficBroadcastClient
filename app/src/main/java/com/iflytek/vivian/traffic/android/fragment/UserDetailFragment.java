package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.event.EventFindByUserIdEvent;
import com.iflytek.vivian.traffic.android.event.user.UserDetailEvent;
import com.iflytek.vivian.traffic.android.utils.DateFormatUtil;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

@Page(anim = CoreAnim.slide, name = "警员")
public class UserDetailFragment extends BaseFragment {

    private static final String TAG = "UserDetail";

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
    @BindView(R.id.user_detail_reported_event)
    SuperTextView checkEvent;


    private User user = new User();

    private BroccoliSimpleDelegateAdapter<Event> mEventAdapter;
    private List<Event> eventList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        UserClient.selectUser(getString(R.string.server_url), getArguments().getString("userId"));
//        EventClient.findEventByUserId(getString(R.string.server_url), getArguments().getString("userId"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_detail;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        checkEvent.setOnSuperTextViewClickListener(superTextView -> openNewPage(ReportedEventFragment.class, "userId", getArguments().getString("userId")));
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

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onFindEventByUserId(EventFindByUserIdEvent event) {
//        if (event.isSuccess()) {
//            if (event.getData().size() != 0) {
//                eventList = event.getData();
//            }
//        } else {
//            XToastUtils.error("加载当前用户已上报警情失败！");
//        }
//    }
}
