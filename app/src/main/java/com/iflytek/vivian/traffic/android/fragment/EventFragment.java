package com.iflytek.vivian.traffic.android.fragment;

import androidx.annotation.NonNull;

import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.base.delegate.SimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.base.delegate.SingleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.entity.NewInfo;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.utils.DemoDataProvider;
import com.iflytek.vivian.traffic.android.utils.Utils;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.umeng.commonsdk.debug.E;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.samlss.broccoli.Broccoli;

/**
 * 警情动态
 */
@Page(anim = CoreAnim.none)
public class EventFragment extends BaseFragment {

    private SimpleDelegateAdapter<Event> mEventAdapter;

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
        //资讯的标题
        SingleDelegateAdapter titleAdapter = new SingleDelegateAdapter(R.layout.adapter_title_item) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
                holder.text(R.id.tv_title, "资讯");
                holder.text(R.id.tv_action, "更多");
                holder.click(R.id.tv_action, v -> XToastUtils.toast("更多"));
            }
        };


        List<Event> eventList = new ArrayList<>();

        //资讯
        mEventAdapter = new BroccoliSimpleDelegateAdapter<Event>(R.layout.adapter_event_card_view_list_item, new LinearLayoutHelper(), eventList  ) {
            @Override
            protected void onBindData(RecyclerViewHolder holder, Event event, int position) {
                if (event != null) {
                    holder.text(R.id.tv_user_name, event.getPolicemanId()); //TODO 根据用户ID查找用户姓名
//                    holder.text(R.id.tv_tag,  event.getPostTime());
                    holder.text(R.id.tv_title, event.getEvent());
                    holder.text(R.id.tv_summary, event.getEventResult());

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
//                        holder.findView(R.id.tv_praise),
//                        holder.findView(R.id.tv_comment),
//                        holder.findView(R.id.tv_read),
//                        holder.findView(R.id.iv_image)
                );
            }
        };

    }
}
