package com.dji.sdk.sample.demo.camera;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.controller.DJISampleApplication;
import com.dji.sdk.sample.internal.utils.Helper;
import com.dji.sdk.sample.internal.utils.PopupUtils;
import com.dji.sdk.sample.internal.utils.ToastUtils;
import com.dji.sdk.sample.internal.utils.VideoFeedView;
import com.dji.sdk.sample.internal.view.PresentableView;

import dji.sdk.sdkmanager.LiveVideoBitRateMode;
import dji.sdk.sdkmanager.LiveVideoResolution;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;

import static com.dji.sdk.sample.internal.utils.ToastUtils.showToast;

/**
 * Class for live stream demo.
 *
 * @author Hoker
 * @date 2019/1/28
 * <p>
 * Copyright (c) 2019, DJI All Rights Reserved.
 */
public class DronePlatformView extends LinearLayout implements PresentableView, View.OnClickListener {

    private String liveShowUrl = "please input your live show url here";

    private VideoFeedView primaryVideoFeedView;
    private VideoFeedView fpvVideoFeedView;
    private EditText showUrlInputEdit;

    private Button startLiveShowBtn;
    private Button enableVideoEncodingBtn;
    private Button disableVideoEncodingBtn;
    private Button stopLiveShowBtn;
    private Button soundOnBtn;
    private Button soundOffBtn;
    private Button isLiveShowOnBtn;
    private Button showInfoBtn;
    private Button showLiveStartTimeBtn;
    private Button showCurrentVideoSourceBtn;
    private Button changeVideoSourceBtn;
    private Button startAutoBitBtn;
    private boolean firstTimetoStartAutoBitRate = true;
    private int lastBitRate = 2048;

    private LiveStreamManager.OnLiveChangeListener listener;
    private LiveStreamManager.LiveStreamVideoSource currentVideoSource = LiveStreamManager.LiveStreamVideoSource.Primary;
    private static final String URL_KEY = "sp_stream_url";

    public DronePlatformView(Context context) {
        super(context);
        liveShowUrl = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getString(URL_KEY, liveShowUrl);
        initUI(context);
        initListener();
    }

    private void initUI(Context context) {
        setClickable(true);
        setOrientation(VERTICAL);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_live_stream, this, true);

        primaryVideoFeedView = (VideoFeedView) findViewById(R.id.video_view_primary_video_feed);
        primaryVideoFeedView.registerLiveVideo(VideoFeeder.getInstance().getPrimaryVideoFeed(), true);

        fpvVideoFeedView = (VideoFeedView) findViewById(R.id.video_view_fpv_video_feed);
        fpvVideoFeedView.registerLiveVideo(VideoFeeder.getInstance().getSecondaryVideoFeed(), false);
        if (Helper.isMultiStreamPlatform()){
            fpvVideoFeedView.setVisibility(VISIBLE);
        }

        showUrlInputEdit = (EditText) findViewById(R.id.edit_live_show_url_input);
        showUrlInputEdit.setText(liveShowUrl);

        startLiveShowBtn = (Button) findViewById(R.id.btn_start_live_show);
        enableVideoEncodingBtn = (Button) findViewById(R.id.btn_enable_video_encode);
        disableVideoEncodingBtn = (Button) findViewById(R.id.btn_disable_video_encode);
        stopLiveShowBtn = (Button) findViewById(R.id.btn_stop_live_show);
        soundOnBtn = (Button) findViewById(R.id.btn_sound_on);
        soundOffBtn = (Button) findViewById(R.id.btn_sound_off);
        isLiveShowOnBtn = (Button) findViewById(R.id.btn_is_live_show_on);
        showInfoBtn = (Button) findViewById(R.id.btn_show_info);
        showLiveStartTimeBtn = (Button) findViewById(R.id.btn_show_live_start_time);
        showCurrentVideoSourceBtn = (Button) findViewById(R.id.btn_show_current_video_source);
        changeVideoSourceBtn = (Button) findViewById(R.id.btn_change_video_source);
        Button setResolutionBtn = (Button) findViewById(R.id.btn_set_resolution);
        Button setBitRateBtn = (Button) findViewById(R.id.btn_set_bit_rate);
        startAutoBitBtn = (Button) findViewById(R.id.btn_auto_bitrate);
        setResolutionBtn.setOnClickListener(this);
        setBitRateBtn.setOnClickListener(this);
        startAutoBitBtn.setOnClickListener(this);

        startLiveShowBtn.setOnClickListener(this);
        enableVideoEncodingBtn.setOnClickListener(this);
        disableVideoEncodingBtn.setOnClickListener(this);
        stopLiveShowBtn.setOnClickListener(this);
        soundOnBtn.setOnClickListener(this);
        soundOffBtn.setOnClickListener(this);
        isLiveShowOnBtn.setOnClickListener(this);
        showInfoBtn.setOnClickListener(this);
        showLiveStartTimeBtn.setOnClickListener(this);
        showCurrentVideoSourceBtn.setOnClickListener(this);
        changeVideoSourceBtn.setOnClickListener(this);
    }

    private void initListener() {
        showUrlInputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                liveShowUrl = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listener = new LiveStreamManager.OnLiveChangeListener() {
            @Override
            public void onStatusChanged(int i) {
                ToastUtils.setResultToToast("status changed : " + i);
            }
        };
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BaseProduct product = DJISampleApplication.getProductInstance();
        if (product == null || !product.isConnected()) {
            ToastUtils.setResultToToast("Disconnect");
            return;
        }
        if (isLiveStreamManagerOn()){
            DJISDKManager.getInstance().getLiveStreamManager().registerListener(listener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isLiveStreamManagerOn()){
            DJISDKManager.getInstance().getLiveStreamManager().unregisterListener(listener);
        }
    }

    @Override
    public int getDescription() {
        return R.string.component_listview_live_stream;
    }

    @NonNull
    @Override
    public String getHint() {
        return this.getClass().getSimpleName() + ".java";
    }

    void startLiveShow() {
        ToastUtils.setResultToToast("Start Live Show");
        if (!isLiveStreamManagerOn()) {
            return;
        }
        if (DJISDKManager.getInstance().getLiveStreamManager().isStreaming()) {
            ToastUtils.setResultToToast("already started!");
            return;
        }
        new Thread() {
            @Override
            public void run() {
                DJISDKManager.getInstance().getLiveStreamManager().setLiveUrl(liveShowUrl);
                int result = DJISDKManager.getInstance().getLiveStreamManager().startStream();
                DJISDKManager.getInstance().getLiveStreamManager().setStartTime();
                DronePlatformView.this.getContext().getSharedPreferences(DronePlatformView.this.getContext().getPackageName(),
                        Context.MODE_PRIVATE).edit().putString(URL_KEY,liveShowUrl).commit();

                ToastUtils.setResultToToast("startLive:" + result +
                        "\n isVideoStreamSpeedConfigurable:" + DJISDKManager.getInstance().getLiveStreamManager().isVideoStreamSpeedConfigurable() +
                        "\n isLiveAudioEnabled:" + DJISDKManager.getInstance().getLiveStreamManager().isLiveAudioEnabled());
            }
        }.start();
    }

    private void disableReEncoder() {
        if (!isLiveStreamManagerOn()) {
            return;
        }
        DJISDKManager.getInstance().getLiveStreamManager().setVideoEncodingEnabled(false);
        ToastUtils.setResultToToast("Disable Force Re-Encoder!");
    }

    private void stopLiveShow() {
        if (!isLiveStreamManagerOn()) {
            return;
        }
        DJISDKManager.getInstance().getLiveStreamManager().stopStream();
        ToastUtils.setResultToToast("Stop Live Show");
    }

    private void soundOn() {
        if (!isLiveStreamManagerOn()) {
            return;
        }
        DJISDKManager.getInstance().getLiveStreamManager().setAudioMuted(false);
        ToastUtils.setResultToToast("Sound On");
    }

    private void soundOff() {
        if (!isLiveStreamManagerOn()) {
            return;
        }
        DJISDKManager.getInstance().getLiveStreamManager().setAudioMuted(true);
        ToastUtils.setResultToToast("Sound Off");
    }

    private boolean isLiveStreamManagerOn() {
        if (DJISDKManager.getInstance().getLiveStreamManager() == null) {
            ToastUtils.setResultToToast("No live stream manager!");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_live_show:
                startLiveShow();
                break;
            case R.id.btn_stop_live_show:
                stopLiveShow();
                break;
            case R.id.btn_sound_on:
                soundOn();
                break;
            case R.id.btn_sound_off:
                soundOff();
                break;
            default:
                break;
        }
    }
}
