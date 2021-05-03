package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.GridLayoutHelper;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.base.delegate.SimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.entity.AdapterManagerItem;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.user.UserListEvent;
import com.iflytek.vivian.traffic.android.utils.DemoDataProvider;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.ImageLoader;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

/**
 * 用户管理
 */
@Page(anim = CoreAnim.none)
public class UserManagerFragment extends BaseFragment {

    @BindView(R.id.user_manager_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.user_manager_refreshLayout)
    RefreshLayout refreshLayout;

    private SimpleDelegateAdapter<User> mUserAdapter;

    private List<User> userList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        UserClient.listUser(getString(R.string.server_url));
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
        return R.layout.fragment_user_manager;
    }

    @Override
    protected void initViews() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0,10);

        //管理工具栏（添加 / 筛选 / 多选）
//        GridLayoutHelper gridLayoutHelper = new GridLayoutHelper(3);
//        gridLayoutHelper.setPadding(0, 16, 0, 0);
//        gridLayoutHelper.setVGap(10);
//        gridLayoutHelper.setHGap(0);
//        SimpleDelegateAdapter<AdapterManagerItem> commonAdapter = new SimpleDelegateAdapter<AdapterManagerItem>(R.layout.adapter_manager_toolbar, gridLayoutHelper, DemoDataProvider.getManagerItems(getContext())) {
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

        //用户列表
        mUserAdapter = new BroccoliSimpleDelegateAdapter<User>(R.layout.adapter_user_manager_card_view_list_item, new LinearLayoutHelper(), userList) {
            @Override
            protected void onBindData(RecyclerViewHolder holder, User model, int position) {
                if (model != null) {
                    holder.text(R.id.tv_user_name, model.getName());
                    holder.text(R.id.tv_user_id, model.getId());
                    holder.text(R.id.tv_role, model.getRole());
                    holder.text(R.id.tv_depart, model.getDepartment());

                    holder.click(R.id.card_view, v -> openNewPage(UserDetailFragment.class, "userId", model.getId()));
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

            @Override
            public void selectAll() {

            }

            @Override
            public void unSelectAll() {

            }

            @Override
            public void initCheck(Boolean flag) {

            }
        };

        DelegateAdapter delegateAdapter = new DelegateAdapter(virtualLayoutManager);
//        delegateAdapter.addAdapter(commonAdapter);
        delegateAdapter.addAdapter(mUserAdapter);

        recyclerView.setAdapter(delegateAdapter);
    }

    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mUserAdapter.refresh(userList);
                refreshLayout.finishRefresh();
            }, 1000);
        });

        //上拉加载
        /*refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mUserAdapter.loadMore(userList);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUserList(UserListEvent event) {
        if (event.isSuccess()) {
            userList = event.getData();
        }
    }

}