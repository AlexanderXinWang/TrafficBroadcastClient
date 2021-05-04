package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.event.EventDetailEvent;
import com.iflytek.vivian.traffic.android.utils.DateFormatUtil;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

@Page(anim = CoreAnim.slide, name = "警情大厅详情")
public class EventDetailFragment extends BaseFragment {

    @BindView(R.id.event_hall_detail_location)
    TextView location;
    @BindView(R.id.event_hall_detail_user_name)
    TextView userName;
    @BindView(R.id.event_hall_detail_user_id)
    TextView userId;
    @BindView(R.id.event_hall_detail_time)
    TextView time;
    @BindView(R.id.event_hall_detail_vehicle)
    TextView vehicle;
    @BindView(R.id.event_hall_detail_status)
    TextView status;
    @BindView(R.id.event_hall_detail_desc)
    TextView eventDesc;
    @BindView(R.id.event_hall_detail_result)
    TextView eventResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        EventClient.selectEvent(getString(R.string.server_url), getArguments().getString("eventId"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_detail;
    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventDetail(EventDetailEvent eventDetailEvent) {
        if (eventDetailEvent.isSuccess()) {
            Event event = eventDetailEvent.getData();
            System.out.println(event);
            location.setText(event.getLocation());
            userName.setText(event.getPolicemanName());
            userId.setText(event.getPolicemanId());
            time.setText(DateFormatUtil.format(event.getStartTime()));
            vehicle.setText(event.getVehicle());
            eventDesc.setText(event.getEvent());
            eventResult.setText(event.getEventResult());
        } else {
            // TODO 弹出请求错误
        }
    }
}
