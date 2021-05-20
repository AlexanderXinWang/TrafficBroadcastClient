package com.iflytek.vivian.traffic.android.fragment;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.event.EventDeleteEvent;
import com.iflytek.vivian.traffic.android.event.event.EventDetailEvent;
import com.iflytek.vivian.traffic.android.event.event.EventUpdateEvent;
import com.iflytek.vivian.traffic.android.event.event.GetUserImageEvent;
import com.iflytek.vivian.traffic.android.event.user.UserDetailEvent;
import com.iflytek.vivian.traffic.android.utils.DataProvider;
import com.iflytek.vivian.traffic.android.utils.DateFormatUtil;
import com.iflytek.vivian.traffic.android.utils.StringUtil;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.picker.widget.TimePickerView;
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder;
import com.xuexiang.xui.widget.picker.widget.configure.TimePickerType;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.data.DateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
    @BindView(R.id.event_detail_user_image)
    RadiusImageView image;

    private TimePickerView mTimePickerDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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
    protected void initListeners() {
        super.initListeners();
        userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                image.setImageResource(R.drawable.ic_default_head);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                image.setImageResource(R.drawable.ic_default_head);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtil.isNotEmpty(userId.getText().toString())) {
//                    UserClient.getUserImage(getString(R.string.server_url), userId.getText().toString());
                    UserClient.selectUser(getString(R.string.server_url), userId.getText().toString());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @SingleClick
    @OnClick({R.id.event_detail_update, R.id.event_detail_delete, R.id.event_detail_time_picker, R.id.event_detail_user_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.event_detail_update:
                Event event = new Event();
                event.setId(getArguments().getString("eventManagerId"));
                // 参数校验
                if (StringUtil.isNotEmpty(location.getText().toString())) {
                    event.setLocation(location.getText().toString());
                }
                if (StringUtil.isNotEmpty(userName.getText().toString())) {
                    event.setPolicemanName(userName.getText().toString());
                }
                if (StringUtil.isNotEmpty(userId.getText().toString())) {
                    event.setPolicemanId(userId.getText().toString());
                }
                if (StringUtil.isNotEmpty(vehicle.getText().toString())) {
                    event.setVehicle(vehicle.getText().toString());
                }
                if (StringUtil.isNotEmpty(eventDesc.getText().toString())) {
                    event.setEvent(eventDesc.getText().toString());
                }
                if (StringUtil.isNotEmpty(eventResult.getText().toString())) {
                    event.setEventResult(eventResult.getText().toString());
                }
                if (StringUtil.isNotEmpty(time.getText().toString())) {
                    event.setStartTime(DateUtils.string2Date(time.getText().toString(), DateUtils.yyyyMMddHHmmss.get()));
                }
                new MaterialDialog.Builder(getContext()).title("确认保存？").positiveText("确认").negativeText("取消")
                        .onPositive((dialog, which) -> EventClient.updateEvent(getString(R.string.server_url), event)).show();
                break;
            case R.id.event_detail_delete:
                List<String> eventsToDelete = new ArrayList<>();
                eventsToDelete.add(getArguments().getString("eventManagerId"));
                new MaterialDialog.Builder(getContext()).title("确认删除？").positiveText("确认").negativeText("取消")
                        .onPositive(((dialog, which) -> EventClient.deleteEvent(getString(R.string.server_url), eventsToDelete))).show();
                break;
            case R.id.event_detail_time_picker:
                showTimePickerDialog();
                break;
            case R.id.event_detail_user_image:
                if (StringUtil.isNotEmpty(userId.getText().toString())) {
                    openNewPage(UserDetailFragment.class, "userId", userId.getText().toString());
                }
//                XToastUtils.error("获取当前用户Id失败！");
            default:
                break;
        }
    }

    private void showTimePickerDialog() {
        if (mTimePickerDialog == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateUtils.string2Date("2021-05-17 09:01:46", DateUtils.yyyyMMddHHmmss.get()));
            mTimePickerDialog = new TimePickerBuilder(getContext(), (date, v) -> {
                XToastUtils.toast(DateUtils.date2String(date, DateUtils.yyyyMMddHHmmss.get()));
                time.setText(DateUtils.date2String(date, DateUtils.yyyyMMddHHmmss.get()));
            })
                    .setTimeSelectChangeListener(date -> Log.i("pvTime", "onTimeSelectChanged"))
                    .setType(TimePickerType.ALL)
                    .setTitleText("时间选择")
                    .isDialog(true)
                    .setOutSideCancelable(false)
                    .setDate(calendar)
                    .build();
        }
        mTimePickerDialog.show();
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
//            try {
//                image.setImageBitmap(DataProvider.getBitmap(event.getPolicemanImage()));
//            } catch (IOException e) {
//                image.setImageResource(R.drawable.ic_default_head);
//                XToastUtils.error("加载此事件上报人头像错误！");
//                Log.e(TAG, "加载头像图片错误" + e.getMessage());
//            }
//            UserClient.getUserImage(getString(R.string.server_url), event.getPolicemanId());
            UserClient.selectUser(getString(R.string.server_url), event.getPolicemanId());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserImage(GetUserImageEvent event) {
        if (event.isSuccess()) {
            Glide.with(getContext()).load(event.getData()).into(image);
        } else {
//            XToastUtils.error("加载用户头像失败！");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserDetail(UserDetailEvent event) {
        if (event.isSuccess()) {
            if (null != event.getData()) {
                Glide.with(getContext()).load(event.getData().getImageUrl()).into(image);
                userName.setText(event.getData().getName());
            }
        }
    }

}
