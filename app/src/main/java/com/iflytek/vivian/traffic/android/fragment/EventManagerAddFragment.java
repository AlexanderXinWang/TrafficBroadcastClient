package com.iflytek.vivian.traffic.android.fragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.JsonParseException;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.client.UserClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.event.EventDetailEvent;
import com.iflytek.vivian.traffic.android.event.event.EventSaveEvent;
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
import com.xuexiang.xutil.data.DateUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Calendar;
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
    @BindView(R.id.event_add_user_image)
    RadiusImageView image;
    @BindView(R.id.event_add_time)
    TextView time;

    private Event event = new Event();

    private TimePickerView mTimePickerDialog;

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
//        RadiusImageView image = findViewById(R.id.iv_avatar);
        // TODO 监听输入根据id查找头像url自动展示
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
    @OnClick({R.id.event_add_save, R.id.event_add_time_picker})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.event_add_save:
                event = new Event();
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
                        .onPositive((dialog, which) -> EventClient.saveEvent(getString(R.string.server_url), event)).show();
                break;
            case R.id.event_add_time_picker:
                showTimePickerDialog();
                break;
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
    public void onSaveEvent(EventSaveEvent saveEvent) {
        if (saveEvent.isSuccess()) {
            XToastUtils.success("新增警情事件成功");
            popToBack();
        } else {
            XToastUtils.error("新增警情事件失败");
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onGetUserImage(GetUserImageEvent event) {
//        if (event.isSuccess()) {
//            Glide.with(getContext()).load(event.getData()).into(image);
//        }
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserDetail(UserDetailEvent event) {
        if (event.isSuccess()) {
            if (null != event.getData()) {
                Glide.with(getContext()).load(event.getData().getImageUrl())
                        .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(image);
                userName.setText(event.getData().getName());
            }
        }
    }
}
