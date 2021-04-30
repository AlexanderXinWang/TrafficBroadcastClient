package com.iflytek.vivian.traffic.android.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.iflytek.vivian.traffic.android.R;
import com.iflytek.vivian.traffic.android.activity.SplashActivity;
import com.iflytek.vivian.traffic.android.client.EventClient;
import com.iflytek.vivian.traffic.android.core.BaseFragment;
import com.iflytek.vivian.traffic.android.dto.Event;
import com.iflytek.vivian.traffic.android.dto.User;
import com.iflytek.vivian.traffic.android.event.event.EventSaveEvent;
import com.iflytek.vivian.traffic.android.event.event.IatEvent;
import com.iflytek.vivian.traffic.android.utils.AlertDialogUtil;
import com.iflytek.vivian.traffic.android.utils.StringUtil;
import com.iflytek.vivian.traffic.android.utils.WaveUtil;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.tip.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.iflytek.vivian.traffic.android.utils.WaveUtil.writeHead;

@Page(anim = CoreAnim.none)
public class EventReportFragment extends BaseFragment {

    private static final String TAG = "警情上报EventReportFragment";

    @BindView(R.id.event_report_location)
    EditText eventLocation;
    @BindView(R.id.event_report_vehicle)
    EditText eventVehicle;
    @BindView(R.id.event_report_event)
    EditText eventDesc;
    @BindView(R.id.event_report_result)
    EditText eventResult;
    @BindView(R.id.event_report_iat)
    TextView iatResult;

    //来源：麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    // 采样率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    private int audioSampleRate = 16000;
    //    private final static int audioSampleRate = 48000;
    // 音频通道 单声道 还是双声道 1,2 内部格式会转换
    private final static int AUDIO_CHANNEL = 1;
    // 音频格式：PCM编码CM编码
    private final static int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    AudioRecord mAudioRecord;

    private int bufferSize;
    private boolean isWorking=false;
    private byte[] voiceData = new byte[1280];

    private Event event;
    private User loginUser;

//    SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginToken", Context.MODE_PRIVATE);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initData();
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
        return R.layout.fragment_report;
    }

    @Override
    protected void initViews() {
        /*String data = getActivity().getIntent().getStringExtra("data");
        if (StringUtil.isNotEmpty(data)) {
            loginUser = JSON.parseObject(data, User.class);
        }*/
    }

    public void initData() {
        String data = getActivity().getIntent().getStringExtra("data");
        if (StringUtil.isNotEmpty(data)) {
            loginUser = JSON.parseObject(data, User.class);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @SingleClick
    @OnClick({R.id.event_report_flush, R.id.event_report_record, R.id.event_report})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.event_report_flush:
                eventLocation.setText(null);
                eventVehicle.setText(null);
                eventDesc.setText(null);
                eventResult.setText(null);
                break;
            case R.id.event_report_record:
                if (isWorking) {
                    isWorking = false;
                    onBtnClickedIat();
                } else {
                    startRecord();
                    // TODO 录音状态提示
                }
                break;
            case R.id.event_report:
                try {
                    Event event = new Event();
                    event.setLocation(eventLocation.getText().toString());
                    event.setVehicle(eventVehicle.getText().toString());
                    event.setEvent(eventDesc.getText().toString());
                    event.setEventResult(eventResult.getText().toString());
                    event.setIatResult(iatResult.getText().toString());
                    /*event.setPolicemanId(loginUser.getId());
                    event.setPolicemanName(loginUser.getName());*/

                    if ("".equals(event.getLocation()) && "".equals(event.getVehicle()) && "".equals(event.getEvent()) && "".equals(event.getEventResult())) {
                        AlertDialogUtil.warning(getContext(), "输入为空");
                    } else {
                        EventClient.saveEvent(getString(R.string.server_url), event);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "上报事件失败：" + e.getMessage());
                }
                break;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 录音

    /**
     * 语音输入按钮绑定事件
     */
    public void onRecordBtnClicked() {
        if (isWorking) {
            isWorking = false;
            onBtnClickedIat();
        } else {
            startRecord();
            // TODO 录音状态提示
        }
    }

    /**
     * 开启录音线程
     */
    public void startRecord() {
        startRecording();
        isWorking = true;
        new Thread(new RecordTask()).start();
    }

    /**
     * 录音初始化
     */
    public void startRecording() {
        bufferSize = AudioRecord.getMinBufferSize(audioSampleRate,
                AUDIO_CHANNEL==1?AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO, AUDIO_FORMAT);
        mAudioRecord = new AudioRecord(AUDIO_INPUT, audioSampleRate,
                AUDIO_CHANNEL==1?AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO, AUDIO_FORMAT, bufferSize);

        if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            mAudioRecord = null;
            isWorking = false;
//            Toast.makeText(this, "初始化失败！", Toast.LENGTH_SHORT).show();
            ToastUtils.toast("初始化失败！");
            return;
        }
        mAudioRecord.startRecording();
    }

    /**
     * 录音线程
     */
    private class RecordTask implements Runnable {

        @Override
        public void run() {
            // 设置高权限
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            try {
                byte[] buffer = new byte[bufferSize];
                while (isWorking) {
                    int read = mAudioRecord.read(buffer, 0, bufferSize);
                    if (read >=2) {
                        byte[] temp = byteMerger(voiceData, buffer);
                        voiceData = temp;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "录音异常" + e.getMessage());
            } finally {
                if (mAudioRecord != null) {
                    mAudioRecord.stop();
                    mAudioRecord.release();
                }
                isWorking = false;
            }
        }
    }

    public void onBtnClickedIat() {
        if (voiceData != null) {
            try {
                EventClient.iatEvent(getString(R.string.server_url), byteMerger(getHead(voiceData.length, audioSampleRate, 16, 1), voiceData));
            } catch (IOException e) {
                Log.e(TAG, "语音识别异常" + e.getMessage());
            }
        } else {
            AlertDialogUtil.warning(getContext(), "请先输入语音");
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // EventBus事件响应

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIatEvent(IatEvent iatEvent) {
        if (iatEvent.isSuccess()) {
            event = iatEvent.getData();
            if (StringUtil.isNotEmpty(event.getIatResult())) {
                iatResult.setText(event.getIatResult());
            }
            if (null != event) {
                if (null != event.getLocation()) {
                    eventLocation.setText(event.getLocation());
                }
                if (null != event.getVehicle()) {
                    eventVehicle.setText(event.getVehicle());
                }
                if (null != event.getEvent()) {
                    eventDesc.setText(event.getEvent());
                }
                if (null != event.getEventResult()) {
                    eventResult.setText(event.getEventResult());
                }
            }
        } else {
            AlertDialogUtil.warning(getContext(), "iat响应失败：" + iatEvent.getErrorMessage() );
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReportEvent(EventSaveEvent event) {
        if (event.isSuccess()) {
            String message = String.format("周星星"/*, loginUser.getName()*/);
            AlertDialogUtil.infoAndClose(getActivity(), message, "事件上报成功，点击返回");
            Log.i(TAG, "保存事件成功");
        } else {
            AlertDialogUtil.warning(getContext(), "事件上报失败：" + event.getErrorMessage());
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 语音录入处理

    /**
     * 在发送语音数组前加上语音头
     * @param pcmDataSize
     * @param sampleRate
     * @param sampleBits
     * @param channels
     * @return
     * @throws IOException
     */
    public byte[] getHead(int pcmDataSize, int sampleRate, int sampleBits, int channels) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writeHead(byteArrayOutputStream, pcmDataSize, sampleRate, sampleBits, channels);
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 合并两个byte数组
     * @param byte1
     * @param byte2
     * @return
     */
    public byte[] byteMerger(byte[] byte1, byte[] byte2) {
        byte[] bytes = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, bytes, 0, byte1.length);
        System.arraycopy(byte2, 0, bytes, byte1.length, byte2.length);
        return bytes;
    }







}
