package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.SimpleRecyclerAdapter;
import com.iflytek.vivian.traffic.android.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.base.delegate.SimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.entity.AdapterManagerItem;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.event.EventListByEventAscEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByEventDescEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByTimeAscEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByTimeDescEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListEvent;
import com.iflytek.vivian.traffic.android.utils.DataProvider;
import com.iflytek.vivian.traffic.android.utils.DateFormatUtil;
import com.iflytek.vivian.traffic.android.utils.DemoDataProvider;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.ImageLoader;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.samlss.broccoli.Broccoli;

/**
 * 警情管理
 */
@Page(anim = CoreAnim.none)
public class EventManagerFragment extends BaseFragment {

    private static final String TAG = "EventManagerFragment";

    @BindView(R.id.event_manager_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.event_manager_refreshLayout)
    RefreshLayout refreshLayout;

    private SimpleDelegateAdapter<Event> mEventAdapter;

    private List<Event> eventList = null;
    private Boolean dialogFlag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        EventClient.listEvent(getString(R.string.server_url));
    }

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_manager;
    }

    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0,10);

        //管理工具栏（全选 / 添加 / 筛选 / 删除）
//        frameLayout.bringToFront();
//        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(3);
//        gridLayoutHelper.setPadding(0, 16, 0, 0);
//        gridLayoutHelper.setVGap(10);
//        gridLayoutHelper.setHGap(0);
//        SimpleDelegateAdapter<AdapterManagerItem> commonAdapter = new SimpleDelegateAdapter<AdapterManagerItem>(R.id.adapter_manager_toolbar, gridLayoutHelper, DemoDataProvider.getManagerItems(getContext())) {
//            @Override
//            protected void bindData(@NonNull RecyclerViewHolder holder, int position, AdapterManagerItem item) {
//                if (item != null) {
//                    RadiusImageView imageView = holder.findViewById(R.id.tool_item);
//                    imageView.setCircle(true);
//                    ImageLoader.get().loadImage(imageView, item.getIcon());
//
//                    holder.click(R.id.toolbar_container, v -> XToastUtils.toast("点击了!"));
//                }
//            }
//        };

        //警情
        mEventAdapter = new BroccoliSimpleDelegateAdapter<Event>(R.layout.adapter_event_manager_card_view_list_item, new LinearLayoutHelper(), eventList) {
            @Override
            protected void onBindData(RecyclerViewHolder holder, Event model, int position) {
                if (model != null) {
                    holder.text(R.id.tv_user_name, model.getPolicemanName());
                    holder.text(R.id.tv_tag, DateFormatUtil.format(model.getStartTime()));
                    holder.text(R.id.tv_title, model.getLocation());
                    holder.text(R.id.tv_summary, model.getEvent());

                    holder.click(R.id.card_view, v -> openNewPage(EventManagerDetailFragment.class, "eventManagerId", model.getId()));
                }
            }

            @Override
            protected void onBindBroccoli(RecyclerViewHolder holder, Broccoli broccoli) {
                broccoli.addPlaceholders(
                        holder.findView(R.id.tv_user_name),
                        holder.findView(R.id.tv_tag),
                        holder.findView(R.id.tv_title),
                        holder.findView(R.id.tv_summary)
                );
            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
//        delegateAdapter.addAdapter(commonAdapter);
        delegateAdapter.addAdapter(mEventAdapter);

        recyclerView.setAdapter(delegateAdapter);
    }

    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.getLayout().postDelayed(() -> {
                EventClient.listEvent(getString(R.string.server_url));
                mEventAdapter.refresh(eventList);
                refreshLayout.finishRefresh();
            }, 1000);
        });

        //上拉加载
        /*refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            refreshLayout.getLayout().postDelayed(() -> {
                mEventAdapter.loadMore(eventList);
                refreshLayout.finishLoadMore();
            }, 1000);
        });*/

        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @SingleClick
    @OnClick({R.id.event_manager_add, R.id.event_manager_filter, R.id.event_manager_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.event_manager_add:
                openNewPage(EventManagerAddFragment.class);
                break;
            case R.id.event_manager_filter:
                showFilterDialog();
                break;
            case R.id.event_manager_delete:
                break;
        }
    }


    private void showFilterDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_bottom_sheet, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        initDialogList(recyclerView);

        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setDismissWithAnimation(true);
        dialog.show();
    }

    private void initDialogList(RecyclerView recyclerView) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        SimpleRecyclerAdapter adapter = new SimpleRecyclerAdapter(DataProvider.getDemoData()) {
            @Override
            protected void onBindViewHolder(SmartViewHolder holder, String model, int position) {
                super.onBindViewHolder(holder, model, position);
                holder.text(android.R.id.text1, model);
                holder.textColorId(android.R.id.text1, R.color.xui_config_color_light_blue_gray);
                holder.click(android.R.id.text1, view -> {
                    switch (position) {
                        case 0:
                            EventClient.listEventByTimeAsc(ResUtils.getString(R.string.server_url));
                            break;
                        case 1:
                            EventClient.listEventByTimeDesc(ResUtils.getString(R.string.server_url));
                            break;
                        case 2:
                            EventClient.listEventByEventAsc(ResUtils.getString(R.string.server_url));
                            break;
                        case 3:
                            EventClient.listEventByEventDesc(ResUtils.getString(R.string.server_url));
                            break;
                        default:
                            break;
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EventBus 订阅事件

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventList(EventListEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
        } else {
            XToastUtils.error("刷新事件列表错误！");
            Log.e(TAG, event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListByTimeAsc(EventListByTimeAscEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
            refreshLayout.autoRefresh();
            XToastUtils.success("根据时间升序排列");
        } else {
            XToastUtils.error("按照时间升序排列错误！");
            Log.e(TAG, "时间升序" + event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListByTimeDesc(EventListByTimeDescEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
            refreshLayout.autoRefresh();
            XToastUtils.success("根据时间降序排列");
        } else {
            XToastUtils.error("按照时间降序排列错误！");
            Log.e(TAG, "时间降序" + event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListByEventAsc(EventListByEventAscEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
            refreshLayout.autoRefresh();
            XToastUtils.success("根据事件名称升序排列");
        } else {
            XToastUtils.error("按照事件名称升序排列错误！");
            Log.e(TAG, "事件名称升序" + event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListByEventDesc(EventListByEventDescEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
            refreshLayout.autoRefresh();
            XToastUtils.success("根据事件名称降序排列");
        } else {
            XToastUtils.error("按照事件名称降序排列错误！");
            Log.e(TAG, "事件名称降序" + event.getErrorMessage());
        }
    }
}