package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.event.EventDeleteEvent;
import com.iflytek.vivian.traffic.android.event.user.UserDeleteEvent;
import com.iflytek.vivian.traffic.android.event.user.UserDetailEvent;
import com.iflytek.vivian.traffic.android.event.user.UserListEvent;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.SmoothCheckBox;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

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
 * 用户管理
 */
@Page(anim = CoreAnim.none)
public class UserManagerFragment extends BaseFragment {

    private static final String TAG = "UserManagerFragment";

    @BindView(R.id.user_manager_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.user_manager_refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.user_manager_select_all)
    SmoothCheckBox selectAll;

    private BroccoliSimpleDelegateAdapter<User> mUserAdapter;

    private List<User> userList = null;


    private Map<Integer, String> userPosition = new HashMap<>();

    //用来记录所有checkbox的状态
    private Map<Integer, Boolean> checkStatus = new HashMap<>();

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

    public void initData() {
        for (int i = 0; i < userList.size(); i++) {
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

        //用户列表
        mUserAdapter = new BroccoliSimpleDelegateAdapter<User>(R.layout.adapter_user_manager_card_view_list_item, new LinearLayoutHelper(), userList) {
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
                for (int i = 0; i < userList.size() ; i++) {
                    checkStatus.put(i, flag);
                }
            }
            @Override
            protected void onBindData(RecyclerViewHolder holder, User model, int position) {
                if (model != null) {

                    SmoothCheckBox smoothCheckBox = holder.findViewById(R.id.checkbox);
                    smoothCheckBox.setOnCheckedChangeListener(null);
                    smoothCheckBox.setChecked(checkStatus.get(position));
                    smoothCheckBox.setOnCheckedChangeListener(((checkBox, isChecked) -> checkStatus.put(position, isChecked)));
                    userPosition.put(position, model.getId());

                    holder.text(R.id.tv_user_name, model.getName());
                    holder.text(R.id.tv_user_id, model.getId());
                    holder.text(R.id.tv_role, model.getRole());
                    holder.text(R.id.tv_depart, model.getDepartment());

                    holder.click(R.id.card_view, v -> openNewPage(UserManagerDetailFragment.class, "userId", model.getId()));
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
        delegateAdapter.addAdapter(mUserAdapter);
        recyclerView.setAdapter(delegateAdapter);
    }

    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> refreshLayout.getLayout().postDelayed(() -> {
            mUserAdapter.refresh(userList);
            refreshLayout.finishRefresh();
        }, 1000));

        //上拉加载
        /*refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            // TODO: 2020-02-25 这里只是模拟了网络请求
            refreshLayout.getLayout().postDelayed(() -> {
                mUserAdapter.loadMore(userList);
                refreshLayout.finishLoadMore();
            }, 1000);
        });*/

        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果

        selectAll.setOnCheckedChangeListener(((checkBox, isChecked) -> {
            if (selectAll.isChecked()) {
                mUserAdapter.selectAll();
            } else {
                mUserAdapter.unSelectAll();
            }
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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
                openNewPage(UserManagerAddFragment.class);
                break;
            case R.id.event_manager_filter:
//                showFilterDialog();
                break;
            case R.id.event_manager_delete:
                List<String> usersToDelete = new ArrayList<>();

                for (Integer position : checkStatus.keySet()) {
                    if (checkStatus.get(position)) {
                        usersToDelete.add(userPosition.get(position));
                    }
                }

                if (!usersToDelete.isEmpty()) {
                    new MaterialDialog.Builder(getContext()).title("确认删除？").content(usersToDelete.toString()).positiveText("确认").negativeText("取消")
                            .onPositive((dialog, which) -> UserClient.deleteUser(getString(R.string.server_url), usersToDelete)).show();
                } else {
//                    new MaterialDialog.Builder(getContext()).title("请选择事件！").positiveText("确认").show();
                    XToastUtils.error("请选择事件！");
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUserList(UserListEvent event) {
        if (event.isSuccess()) {
            userList = event.getData();
            initData();
        } else {
            XToastUtils.error("刷新用户列表错误！");
            Log.e(TAG, event.getErrorMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserDelete(UserDeleteEvent event) {
        if (event.isSuccess()) {
            XToastUtils.success("删除用户成功");
            refreshLayout.autoRefresh(5000);
        } else {
            XToastUtils.error("删除用户失败");
        }
    }
}