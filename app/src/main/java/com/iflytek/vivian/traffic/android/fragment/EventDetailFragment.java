package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.event.EventDetailEvent;
import com.iflytek.vivian.traffic.android.utils.DataProvider;
import com.iflytek.vivian.traffic.android.utils.DateFormatUtil;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.BindView;

@Page(anim = CoreAnim.slide, name = "警情大厅详情")
public class EventDetailFragment extends BaseFragment {

    private static final String TAG = "EventDetail" ;

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
    @BindView(R.id.event_hall_detail_user_image)
    RadiusImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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
            try {
                image.setImageBitmap(DataProvider.getBitmap(event.getPolicemanImage()));
            } catch (IOException e) {
                XToastUtils.error("加载此事件上报人头像失败！");
                image.setImageResource(R.drawable.ic_default_head);
                Log.e(TAG, "加载头像图片错误" + e.getMessage());
            }

        } else {
            // TODO 弹出请求错误
        }
    }
}
