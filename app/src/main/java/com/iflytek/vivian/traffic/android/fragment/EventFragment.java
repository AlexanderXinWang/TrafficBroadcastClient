package com.iflytek.vivian.traffic.android.fragment;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.adapter.base.broccoli.BroccoliSimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.base.delegate.SimpleDelegateAdapter;
import com.iflytek.vivian.traffic.android.adapter.base.delegate.SingleDelegateAdapter;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.event.event.EventGetPlayPathEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListByTimeDescEvent;
import com.iflytek.vivian.traffic.android.event.event.EventListEvent;
import com.iflytek.vivian.traffic.android.utils.DateFormatUtil;
import com.iflytek.vivian.traffic.android.utils.DemoDataProvider;
import com.iflytek.vivian.traffic.android.utils.StringUtil;
import com.iflytek.vivian.traffic.android.utils.XToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.banner.widget.banner.SimpleImageBanner;
import com.xuexiang.xui.widget.button.SmoothCheckBox;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import me.samlss.broccoli.Broccoli;
import me.samlss.broccoli.PlaceholderParameter;

/**
 * 警情动态
 */
@Page(anim = CoreAnim.none)
public class EventFragment extends BaseFragment {

    private static final String TAG = "EventFragment";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private BroccoliSimpleDelegateAdapter<Event> mEventAdapter;

    private List<Event> eventList = new ArrayList<>();

    private List<String> mp3List = new ArrayList<>();

    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        EventClient.listEvent(getString(R.string.server_url));
        timer.schedule(task, 0, 15 * 1000);
    }

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * 布局的资源id
     *
     * @return
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
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);
        RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        recyclerView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);

        //轮播条
        SingleDelegateAdapter bannerAdapter = new SingleDelegateAdapter(R.layout.include_head_view_banner) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
                SimpleImageBanner banner = holder.findViewById(R.id.sib_simple_usage);
                banner.setSource(DemoDataProvider.getBannerList())
                        .setOnItemClickListener((view, item, position1) -> XToastUtils.toast("headBanner position--->" + position1)).startScroll();
            }
        };

        //资讯的标题
        SingleDelegateAdapter titleAdapter = new SingleDelegateAdapter(R.layout.adapter_title_item) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
                holder.text(R.id.tv_title, "警情");
                holder.text(R.id.tv_play, "自动播报");
                SmoothCheckBox autoPlay = holder.findViewById(R.id.event_auto_play);
                autoPlay.setOnCheckedChangeListener((checkBox, isChecked) -> {
                    if (autoPlay.isChecked()) {
                        EventClient.getEventPlayPath(getString(R.string.server_url));
//                        new MaterialDialog.Builder(getContext()).title("确认开始播报警情？").positiveText("确认").negativeText("取消")
//                                .onPositive(((dialog, which) -> EventClient.getEventPlayPath(getString(R.string.server_url)))).show();
                        XToastUtils.success("成功开启自动播报");
                    } else {
                        task.cancel();
                        XToastUtils.error("警情自动播报关闭");
                    }
                });
//                new MaterialDialog.Builder(getContext()).title("确认开始播报警情？").positiveText("确认").negativeText("取消")
//                        .onPositive(((dialog, which) -> EventClient.getEventPlayPath(getString(R.string.server_url)))).show();
            }
        };

        //警情
        mEventAdapter = new BroccoliSimpleDelegateAdapter<Event>(R.layout.adapter_event_card_view_list_item, new LinearLayoutHelper(), eventList) {
            @Override
            protected void onBindData(RecyclerViewHolder holder, Event model, int position) {
                if (model != null) {
                    holder.text(R.id.tv_user_name, model.getPolicemanName());
                    holder.text(R.id.tv_tag, DateFormatUtil.format(model.getStartTime()));
                    holder.text(R.id.tv_title, model.getLocation());
                    holder.text(R.id.tv_summary, model.getEvent());

                    holder.click(R.id.card_view, v -> openNewPage(EventDetailFragment.class, "eventId", model.getId()));
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
        delegateAdapter.addAdapter(bannerAdapter);
        delegateAdapter.addAdapter(titleAdapter);
        delegateAdapter.addAdapter(mEventAdapter);

        recyclerView.setAdapter(delegateAdapter);
    }

    @Override
    protected void initListeners() {
        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> refreshLayout.getLayout().postDelayed(() -> {
            EventClient.listEvent(getString(R.string.server_url));
            mEventAdapter.refresh(eventList);
            refreshLayout.finishRefresh();
        }, 1000));

        //上拉加载
        /*refreshLayout.setOnLoadMoreListener(refreshLayout -> refreshLayout.getLayout().postDelayed(() -> {
            mEventAdapter.loadMore(DemoDataProvider.getDemoEventInfo());
            refreshLayout.finishLoadMore();
        }, 1000));*/

        refreshLayout.setDisableContentWhenRefresh(true);
        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果

//        autoPlay.setOnCheckedChangeListener((checkBox, isChecked) -> {
//            if (isChecked) {
//                new MaterialDialog.Builder(getContext()).title("确认开始播报警情？").positiveText("确认").negativeText("取消")
//                        .onPositive(((dialog, which) -> EventClient.getEventPlayPath(getString(R.string.server_url)))).show();
//                XToastUtils.success("成功开启自动播报");
//            } else {
//                task.cancel();
//                XToastUtils.error("警情自动播报关闭");
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        mEventAdapter.recycle();
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        task.cancel();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventList(EventListEvent event) {
        if (event.isSuccess()) {
            eventList = event.getData();
            Log.i(TAG, eventList.toString());
        } else {
            XToastUtils.error("加载失败！");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventPlayPath(EventGetPlayPathEvent event) {
        if (event.isSuccess()) {
            mp3List = event.getData();
            if (mp3List == null) {
                XToastUtils.toast("警情已全部播报！");
            } else {
                new Thread(new PlayTask()).start();
            }
        } else {
            Log.e(TAG, "请求播放路径错误" + event.getErrorMessage());
            XToastUtils.error("请求播放路径失败！");
        }
    }


    private Boolean flag = false;
    private Timer timer = new Timer();

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (flag && !mediaPlayer.isPlaying()) {
                EventClient.getEventPlayPath(getString(R.string.server_url));
            }
        }
    };

    private class PlayTask implements Runnable {
        @Override
        public void run() {
            int count = 0;
            while (count < mp3List.size()) {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    playMp3(mp3List.get(count));
                    count++;
                }
            }
            if (count == mp3List.size()) {
                flag = true;
//                Looper.prepare();
//                XToastUtils.success("警情事件播报完毕");
//                Looper.loop();
            }
        }
    }

    private void playMp3(String mp3Path) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getContext(), Uri.parse(mp3Path));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "播放错误" + e.getMessage());
            XToastUtils.error("播放错误！");
        }
    }

    /**  播报事故的另一种处理方式
     *   1、在事故播报中， 没播报一个事故， 则在列表中移除
     *   2、每个10s刷新一下数据库， 返回上报的未处理事件
     *   3、将刷新返回的新数据 拼接到events列表后面
     * @param
     */
//    private TimerTask task = new TimerTask() {
//        public void run() {
//            // 定时任务执行
//            refreshEvent();
//        }
//    };
//    private class PlayTask implements Runnable{
//        @Override
//        public void run() {
//            while (events.size() > 0){
//                if (mediaPlayer != null && !mediaPlayer.isPlaying()){
//                    playMp3(events.get(0).getMp3());
//                    events.remove(0);
//                }
//            }
//        }
//    }


    /**
     * 定时任务取消
     */
    public void stopTimerTask(){
        task.cancel();
        task = null;//如果不重新new，会报异常
    }


}
