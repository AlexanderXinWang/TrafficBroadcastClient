package com.iflytek.vivian.traffic.android.fragment;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.alibaba.fastjson.JSON;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.base.delegate.SimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.event.EventDetailEvent;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import butterknife.BindAnim;
import butterknife.BindView;
import me.samlss.broccoli.Broccoli;

@Page(name = "警情详情")
public class EventDetailFragment extends BaseFragment {

    private SimpleDelegateAdapter<Event> mEventAdapter;

    @BindView(R.id.event_detail_location)
    TextView location;
    @BindView(R.id.event_detail_user_name)
    TextView userName;
    @BindView(R.id.event_detail_user_id)
    TextView userId;
    @BindView(R.id.event_detail_time)
    TextView time;
    @BindView(R.id.event_detail_vehicle)
    TextView vehicle;
    @BindView(R.id.event_detail_status)
    TextView status;
    @BindView(R.id.event_detail_desc)
    TextView eventDesc;
    @BindView(R.id.event_detail_result)
    TextView eventResult;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        EventClient.selectEvent(getString(R.string.server_url), getArguments().getString("eventId"));
        System.out.println(getArguments().getString("eventId"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
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
        return R.layout.fragment_event_detail;
    }

    @Override
    protected void initViews() {

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventDetail(EventDetailEvent eventDetailEvent) {
        if (eventDetailEvent.isSuccess()) {
            Event event = eventDetailEvent.getData();
            System.out.println(event);
            location.setText(event.getLocation());
            userName.setText(event.getPolicemanName());
        } else {
            // TODO 弹出请求错误
        }
    }

}
