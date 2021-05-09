package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.SimpleRecyclerAdapter;
import com.iflytek.vivian.traffic.android.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.event.EventDeleteEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByEventAscEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByEventDescEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByLocationAscEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByLocationDescEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByNameAscEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByNameDescEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByTimeAscEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByTimeDescEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListEvent;
import com.iflytek.vivian.traffic.android.utils.DataProvider;
import com.iflytek.vivian.traffic.android.utils.DateFormatUtil;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.SmoothCheckBox;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @BindView(R.id.event_manager_select_all)
    SmoothCheckBox selectAll;

    private BroccoliSimpleDelegateAdapter<Event> mEventAdapter;

    private List<Event> eventList = new ArrayList<>();

    private Map<Integer, String> eventPosition = new HashMap<>();

    //用来记录所有checkbox的状态
    private Map<Integer, Boolean> checkStatus = new HashMap<>();

    private XUISimplePopup mFilterPopup;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        EventClient.listEvent(getString(R.string.server_url));
        initFilterPopup();
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

    public void initData() {
        for (int i = 0; i < eventList.size(); i++) {
            checkStatus.put(i, false);
        }
    }

    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0,10);

        //警情
        mEventAdapter = new BroccoliSimpleDelegateAdapter<Event>(R.layout.adapter_event_manager_card_view_list_item, new LinearLayoutHelper(), eventList) {

            @Override
            public void selectAll() {
                initCheck(true);
                notifyDataSetChanged();
            }

            @Override
            public void unSelectAll() {
                initCheck(false);
                notifyDataSetChanged();
            }

            @Override
            public void initCheck(Boolean flag) {
                for (int i = 0; i < eventList.size() ; i++) {
                    checkStatus.put(i, flag);
                }
            }

            @Override
            protected void onBindData(RecyclerViewHolder holder, Event model, int position) {

                if (model != null) {

                    SmoothCheckBox smoothCheckBox = holder.findViewById(R.id.checkbox);
                    smoothCheckBox.setOnCheckedChangeListener(null);
                    smoothCheckBox.setChecked(checkStatus.get(position));
                    smoothCheckBox.setOnCheckedChangeListener(((checkBox, isChecked) -> {
                        checkStatus.put(position, isChecked);
                        if (checkAllChoose()) {
                            // 已选中所有item -> 让全选按钮为选中状态
                            selectAll.setChecked(true);
                        } else {
                            // 所有item未选中 -> 让全选按钮为未选中状态
                            selectAll.setChecked(false);
                        }
                    }));
                    eventPosition.put(position, model.getId());

                    holder.setIsRecyclable(false);

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

        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果

        selectAll.setOnCheckedChangeListener(((checkBox, isChecked) -> {
            if (selectAll.isChecked()) {
                if (!checkAllChoose()) {
                    // 只有不是手动达到全选的情况下才触发自动全选
                    mEventAdapter.selectAll();
                }
            } else {
                // 只有不是部分选中 || 点击全选后再次点击（当前所有item状态仍为选中）触发取消全选
                if (!checkPartlyChoose() || checkAllChoose()) {
                    mEventAdapter.unSelectAll();
                }
            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initFilterPopup() {
        int maxHeight = 700;
        mFilterPopup = new XUISimplePopup(getContext(), DataProvider.eventFilterItems)
                .create(maxHeight, (adapter, item, position) -> {
                    switch (position) {
                        case 0:
                            EventClient.listEventByTimeAsc(ResUtils.getString(R.string.server_url));
                            break;
                        case 1:
                            EventClient.listEventByTimeDesc(ResUtils.getString(R.string.server_url));
                            break;
                        case 2:
                            EventClient.listEventByNameAsc(ResUtils.getString(R.string.server_url));
                            break;
                        case 3:
                            EventClient.listEventByNameDesc(ResUtils.getString(R.string.server_url));
                            break;
                        case 4:
                            EventClient.listEventByLocationAsc(ResUtils.getString(R.string.server_url));
                            break;
                        case 5:
                            EventClient.listEventByLocationDesc(ResUtils.getString(R.string.server_url));
                            break;
                        default:
                            break;
                    }
                });
    }

    /**
     * 管理工具栏（全选 / 添加 / 筛选 / 删除）
     * @param view
     */
    @SingleClick
    @OnClick({R.id.event_manager_add, R.id.event_manager_filter, R.id.event_manager_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.event_manager_add:
                openNewPage(EventManagerAddFragment.class);
                break;
            case R.id.event_manager_filter:
                mFilterPopup.showDown(view);
//                showFilterDialog();
                break;
            case R.id.event_manager_delete:
                List<String> eventsToDelete = new ArrayList<>();

                for (Integer position : checkStatus.keySet()) {
                    if (checkStatus.get(position)) {
                        eventsToDelete.add(eventPosition.get(position));
                    }
                }

                if (!eventsToDelete.isEmpty()) {
                    new MaterialDialog.Builder(getContext()).title("确认删除？").content(eventsToDelete.toString()).positiveText("确认").negativeText("取消")
                            .onPositive((dialog, which) -> EventClient.deleteEvent(getString(R.string.server_url), eventsToDelete)).show();
                } else {
//                    new MaterialDialog.Builder(getContext()).title("请选择事件！").positiveText("确认").show();
                    XToastUtils.error("请选择事件！");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 筛选底部弹窗
     */
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

    /**
     * 初始化底部弹窗数据
     * @param recyclerView
     */
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
                            EventClient.listEventByNameAsc(ResUtils.getString(R.string.server_url));
                            break;
                        case 3:
                            EventClient.listEventByNameDesc(ResUtils.getString(R.string.server_url));
                            break;
                        case 4:
                            EventClient.listEventByLocationAsc(ResUtils.getString(R.string.server_url));
                            break;
                        case 5:
                            EventClient.listEventByLocationDesc(ResUtils.getString(R.string.server_url));
                            break;
                        default:
                            break;
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    /**
     * 检验是否部分选择
     * @return
     */
    public Boolean checkPartlyChoose() {
        Boolean flag = false;
        for(Integer position : checkStatus.keySet()) {
            if (checkStatus.get(position)) {
                flag =  true;
            }
        }
        return flag;
    }

    /**
     * 检验是否全部选择
     * @return
     */
    public Boolean checkAllChoose() {
        Boolean flag = false;
        int count = 0;
        for(Integer position : checkStatus.keySet()) {
            if (checkStatus.get(position)) {
                count++;
            }
        }
        if (count == eventList.size()) {
            flag = true;
        }
        return flag;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EventBus 订阅事件

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventList(EventListEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
            initData();
        } else {
            XToastUtils.error("刷新事件列表错误！");
            Log.e(TAG, event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDelete(EventDeleteEvent event) {
        if (event.isSuccess()) {
            XToastUtils.success("删除事件成功");
            refreshLayout.autoRefresh(5000);
        } else {
            XToastUtils.error("删除事件失败");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListByTimeAsc(EventListByTimeAscEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
            selectAll.setChecked(false);
            mEventAdapter.unSelectAll();
            refreshLayout.autoRefresh();
//            mEventAdapter.refresh(eventList);
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
            selectAll.setChecked(false);
            mEventAdapter.unSelectAll();
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
            selectAll.setChecked(false);
            mEventAdapter.unSelectAll();
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
            selectAll.setChecked(false);
            mEventAdapter.unSelectAll();
            refreshLayout.autoRefresh();
            XToastUtils.success("根据事件名称降序排列");
        } else {
            XToastUtils.error("按照事件名称降序排列错误！");
            Log.e(TAG, "事件名称降序" + event.getErrorMessage());
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListByNameAsc(EventListByNameAscEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
            selectAll.setChecked(false);
            mEventAdapter.unSelectAll();
            refreshLayout.autoRefresh();
            XToastUtils.success("根据上报人升序排列");
        } else {
            XToastUtils.error("按照上报人升序排列错误！");
            Log.e(TAG, "上报人升序" + event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListByNameDesc(EventListByNameDescEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
            selectAll.setChecked(false);
            mEventAdapter.unSelectAll();
            refreshLayout.autoRefresh();
            XToastUtils.success("根据上报人降序排列");
        } else {
            XToastUtils.error("按照上报人降序排列错误！");
            Log.e(TAG, "上报人名称降序" + event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListByLocationAsc(EventListByLocationAscEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
            selectAll.setChecked(false);
            mEventAdapter.unSelectAll();
            refreshLayout.autoRefresh();
            XToastUtils.success("根据地点升序排列");
        } else {
            XToastUtils.error("按照地点升序排列错误！");
            Log.e(TAG, "地点升序" + event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventListByLocationDesc(EventListByLocationDescEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
            selectAll.setChecked(false);
            mEventAdapter.unSelectAll();
            refreshLayout.autoRefresh();
            XToastUtils.success("根据地点降序排列");
        } else {
            XToastUtils.error("按照地点降序排列错误！");
            Log.e(TAG, "地点名称降序" + event.getErrorMessage());
        }
    }

}