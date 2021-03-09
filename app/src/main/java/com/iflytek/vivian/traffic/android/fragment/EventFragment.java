package com.iflytek.vivian.traffic.android.fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.base.delegate.SimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.base.delegate.SingleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.entity.NewInfo;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.utils.DateFormatUtil;
import com.iflytek.vivian.traffic.android.utils.DemoDataProvider;
import com.iflytek.vivian.traffic.android.utils.Utils;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.umeng.commonsdk.debug.E;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

/**
 * 警情动态
 */
@Page(anim = CoreAnim.none)
public class EventFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private SimpleDelegateAdapter<Event> mEventAdapter;

    List<Event> eventList = new ArrayList<>();

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 布局的资源id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        //资讯的标题
        SingleDelegateAdapter titleAdapter = new SingleDelegateAdapter(R.layout.adapter_title_item) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
                holder.text(R.id.tv_title, "资讯");
                holder.text(R.id.tv_action, "更多");
                holder.click(R.id.tv_action, v -> XToastUtils.toast("更多"));
            }
        };




        //警情
        mEventAdapter = new BroccoliSimpleDelegateAdapter<Event>(R.layout.adapter_event_card_view_list_item, new LinearLayoutHelper(), eventList) {
            @Override
            protected void onBindData(RecyclerViewHolder holder, Event event, int position) {
                if (event != null) {
                    holder.text(R.id.tv_user_name, event.getPolicemanName());
                    holder.text(R.id.tv_tag, DateFormatUtil.format(event.getPostTime()));
                    holder.text(R.id.tv_title, event.getEvent());
                    holder.text(R.id.tv_summary, event.getEventResult());
                    // 打开警情详情页
                    holder.click(R.id.card_view, view -> openNewPage(EventDetailFragment.class));
//                    holder.click(R.id.card_view, v -> Utils.goWeb(getContext(), model.getDetailUrl()));
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
        delegateAdapter.addAdapter(titleAdapter);
        delegateAdapter.addAdapter(mEventAdapter);

        recyclerView.setAdapter(delegateAdapter);
    }

    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mEventAdapter.refresh(eventList);
                refreshLayout.finishRefresh();
            }, 1000);
        });
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mEventAdapter.loadMore(eventList);
                refreshLayout.finishLoadMore();
            }, 1000);
        });
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果
    }
}
