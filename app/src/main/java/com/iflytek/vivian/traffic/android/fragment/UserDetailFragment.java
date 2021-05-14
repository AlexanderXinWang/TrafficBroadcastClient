package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.bumptech.glide.Glide;
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
//        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
//        recyclerView.setLayoutManager(virtualLayoutManager);
//        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
//        recyclerView.setRecycledViewPool(viewPool);
//        viewPool.setMaxRecycledViews(0, 10);
//
//        mEventAdapter = new BroccoliSimpleDelegateAdapter<Event>(R.layout.adapter_event_card_view_list_item, new LinearLayoutHelper(), eventList) {
//            @Override
//            protected void onBindData(RecyclerViewHolder holder, Event model, int position) {
//                if (model != null) {
//                    holder.text(R.id.tv_user_name, model.getPolicemanName());
//                    holder.text(R.id.tv_tag, DateFormatUtil.format(model.getStartTime()));
//                    holder.text(R.id.tv_title, model.getLocation());
//                    holder.text(R.id.tv_summary, model.getEvent());
//
//                    holder.click(R.id.card_view, v -> openNewPage(EventDetailFragment.class, "eventId", model.getId()));
//                }
//            }
//
//            @Override
//            protected void onBindBroccoli(RecyclerViewHolder holder, Broccoli broccoli) {
//                broccoli.addPlaceholders(
//                        holder.findView(R.id.tv_user_name),
//                        holder.findView(R.id.tv_tag),
//                        holder.findView(R.id.tv_title),
//                        holder.findView(R.id.tv_summary)
//                );
//            }
//
//            @Override
//            public void selectAll() {
//
//            }
//
//            @Override
//            public void unSelectAll() {
//
//            }
//
//            @Override
//            public void initCheck(Boolean flag) {
//
//            }
//        };
//
//        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
//
//        delegateAdapter.addAdapter(mEventAdapter);
//
//        recyclerView.setAdapter(delegateAdapter);
    }

    @Override
    protected void initListeners() {
//        super.initListeners();
//        refreshLayout.setOnRefreshListener(refreshLayout1 -> {
//            EventClient.findEventByUserId(getString(R.string.server_url), getArguments().getString("userId"));
//        });
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
            Glide.with(getContext()).load(user.getImageUrl()).into(image);
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
