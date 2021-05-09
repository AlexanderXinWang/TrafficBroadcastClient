package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.event.EventDetailEvent;
import com.iflytek.vivian.traffic.android.event.event.EventSaveEvent;
import com.iflytek.vivian.traffic.android.utils.DataProvider;
import com.iflytek.vivian.traffic.android.utils.DateFormatUtil;
import com.iflytek.vivian.traffic.android.utils.StringUtil;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xutil.tip.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

@Page(anim = CoreAnim.slide, name = "新增警情事件")
public class EventManagerAddFragment extends BaseFragment {

    private static final String TAG = "EventManagerAdd";
    @BindView(R.id.event_add_location)
    TextView location;
    @BindView(R.id.event_add_user_name)
    TextView userName;
    @BindView(R.id.event_add_user_id)
    TextView userId;
    @BindView(R.id.event_add_vehicle)
    TextView vehicle;
    @BindView(R.id.event_add_status)
    TextView status;
    @BindView(R.id.event_add_desc)
    TextView eventDesc;
    @BindView(R.id.event_add_result)
    TextView eventResult;

    private Event event = new Event();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_manager_add;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initListeners() {
        super.initListeners();
        RadiusImageView image = findViewById(R.id.iv_avatar);
        // TODO 监听输入根据id查找头像url自动展示
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    @SingleClick
    @OnClick(R.id.event_add_save)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.event_add_save:
                event = new Event();
                event.setLocation(location.getText().toString());
                event.setPolicemanName(userName.getText().toString());
                event.setPolicemanId(userId.getText().toString());
                event.setStartTime(new Date());
                event.setVehicle(vehicle.getText().toString());
                event.setEvent(eventDesc.getText().toString());
                event.setEventResult(eventResult.getText().toString());

                new MaterialDialog.Builder(getContext()).title("确认保存？").positiveText("确认").negativeText("取消")
                        .onPositive((dialog, which) -> EventClient.saveEvent(getString(R.string.server_url), event)).show();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSaveEvent(EventSaveEvent saveEvent) {
        if (saveEvent.isSuccess()) {
            XToastUtils.success("新增警情事件成功");
            popToBack();
        } else {
            XToastUtils.error("新增警情事件失败");
        }
    }
}
