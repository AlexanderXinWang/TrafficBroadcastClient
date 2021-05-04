package com.iflytek.vivian.traffic.android.fragment;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.event.EventDeleteEvent;
import com.iflytek.vivian.traffic.android.event.event.EventDetailEvent;
import com.iflytek.vivian.traffic.android.event.event.EventUpdateEvent;
import com.iflytek.vivian.traffic.android.utils.DateFormatUtil;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@Page(anim = CoreAnim.slide, name = "警情管理详情")
public class EventManagerDetailFragment extends BaseFragment {

    private static final String TAG = "EventManagerDetail";

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        EventClient.selectEvent(getString(R.string.server_url), getArguments().getString("eventManagerId"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_manager_detail;
    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @SingleClick
    @OnClick({R.id.event_detail_update, R.id.event_detail_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.event_detail_update:
                Event event = new Event();
                event.setId(getArguments().getString("eventManagerId"));
                // TODO 参数校验
                event.setLocation(location.getText().toString());
                event.setPolicemanName(userName.getText().toString());
                event.setPolicemanId(userId.getText().toString());
                event.setVehicle(vehicle.getText().toString());
                event.setEvent(eventDesc.getText().toString());
                event.setEventResult(eventResult.getText().toString());

                new MaterialDialog.Builder(getContext()).title("确认保存？").positiveText("确认").negativeText("取消")
                        .onPositive((dialog, which) -> EventClient.updateEvent(getString(R.string.server_url), event)).show();
                break;
            case R.id.event_detail_delete:
                List<String> eventsToDelete = new ArrayList<>();
                eventsToDelete.add(getArguments().getString("eventManagerId"));
                new MaterialDialog.Builder(getContext()).title("确认删除？").positiveText("确认").negativeText("取消")
                        .onPositive(((dialog, which) -> EventClient.deleteEvent(getString(R.string.server_url), eventsToDelete))).show();
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventDetail(EventDetailEvent eventDetailEvent) {
        if (eventDetailEvent.isSuccess()) {
            Event event = eventDetailEvent.getData();
            location.setText(event.getLocation());
            userName.setText(event.getPolicemanName());
            userId.setText(event.getPolicemanId());
            time.setText(DateFormatUtil.format(event.getStartTime()));
            vehicle.setText(event.getVehicle());
            eventDesc.setText(event.getEvent());
            eventResult.setText(event.getEventResult());
        } else {
            XToastUtils.error("加载事件详情错误！");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateEvent(EventUpdateEvent event) {
        if (event.isSuccess()) {
            Log.i(TAG, "更新" + event.getData().toString());
            XToastUtils.success("更新警情事件成功，请刷新");
            popToBack();
        } else {
            XToastUtils.error("更新警情事件失败！");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteEvent(EventDeleteEvent event) {
        if (event.isSuccess()) {
            Log.i(TAG, "删除" + event.getData().toString());
            XToastUtils.success("删除警情事件成功，请刷新");
            popToBack();
        } else {
            XToastUtils.error("删除警情事件失败！");
        }
    }

}
