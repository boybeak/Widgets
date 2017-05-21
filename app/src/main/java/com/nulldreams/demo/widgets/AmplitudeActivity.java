package com.nulldreams.demo.widgets;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.nulldreams.demo.widgets.adapter.SpinnerAdapter;
import com.nulldreams.demo.widgets.module.Index;
import com.nulldreams.widget.AmplitudeBarView;
import com.nulldreams.widget.AmplitudeBezierView;
import com.nulldreams.widget.AmplitudeView;

import java.io.File;

public class AmplitudeActivity extends AppCompatActivity {

    private String mLastPath;

    private AppCompatSpinner mSpinner;

    private SpinnerAdapter mSpinnerAdapter;
    private AdapterView.OnItemSelectedListener mSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            showAmpView(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VoiceManager voiceManager = VoiceManager.getInstance(AmplitudeActivity.this);
            switch (v.getId()) {
                case R.id.btn_record_or_stop:
                    if (voiceManager.isRecording()) {
                        voiceManager.stopRecord();
                    } else {
                        mLastPath = getRecordPath();
                        voiceManager.startRecord(mLastPath);
                    }
                    v.setSelected(mAmpView.isAttachedWithRecorder());
                    break;
                case R.id.btn_play_or_pause:
                    if (voiceManager.isPlayStarted()) {
                        if (voiceManager.isPlaying()) {
                            voiceManager.pausePlay();
                        } else {
                            voiceManager.resumePlay();
                        }
                    } else {
                        if (!TextUtils.isEmpty(mLastPath)) {
                            voiceManager.startPlay(mLastPath);
                        }
                    }
                    break;
                case R.id.btn_stop:
                    if (voiceManager.isPlayStarted()) {
                        voiceManager.stopPlay();
                    }
                    break;
            }
        }
    };

    private AmplitudeView mAmpView;

    private FrameLayout mAmpLayout;

    private AppCompatImageButton mRecordStartStopBtn, mPlayPauseBtn;

    private AppCompatTextView mTimeTv;

    private ScrollView mSv;

    private AmpPresenter mAp;

    private VoiceManager.OnRecordListener mRecordListener = new VoiceManager.OnRecordListener() {
        @Override
        public void onRecordStart(String targetPath, MediaRecorder recorder) {
            mSpinner.setEnabled(false);
            mAmpView.attachMediaRecorder(recorder);
        }

        @Override
        public void onRecording(String targetPath, MediaRecorder recorder, long currentDuration) {
            mTimeTv.setText("" + (currentDuration / 1000));
        }

        @Override
        public void onRecordPaused() {

        }

        @Override
        public void onRecordResumed() {

        }

        @Override
        public void onRecordStop(String targetPath, long durationInMills) {
            mSpinner.setEnabled(true);
            mAmpView.detachedMediaRecorder();
        }
    };

    private VoiceManager.OnPlayListener mPlayListener = new VoiceManager.OnPlayListener() {
        @Override
        public void onPlayStart(VoiceManager voiceManager, MediaPlayer player) {
            mAmpView.startPlay();
            mPlayPauseBtn.setSelected(true);
        }

        @Override
        public void onPlaying(VoiceManager voiceManager, String targetPath, MediaPlayer mp, long position) {
            mTimeTv.setText("" + (position / 1000));
        }

        @Override
        public void onPlayPaused() {
            mAmpView.pausePlay();
            mPlayPauseBtn.setSelected(false);
        }

        @Override
        public void onPlayResumed() {
            mAmpView.resumePlay();
            mPlayPauseBtn.setSelected(true);
        }

        @Override
        public void onPlayStop(VoiceManager voiceManager) {
            mAmpView.stopPlay();
            mPlayPauseBtn.setSelected(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amplitude);

        mAmpLayout = (FrameLayout)findViewById(R.id.view_layout);
        mTimeTv = (AppCompatTextView)findViewById(R.id.record_time);

        mRecordStartStopBtn = (AppCompatImageButton)findViewById(R.id.btn_record_or_stop);
        mPlayPauseBtn = (AppCompatImageButton)findViewById(R.id.btn_play_or_pause);

        mSpinner = (AppCompatSpinner)findViewById(R.id.spinner);
        mSpinnerAdapter = new SpinnerAdapter();
        mSpinner.setAdapter(mSpinnerAdapter);

        mSv = (ScrollView) findViewById(R.id.amplitude_pref_layout);

        mSpinner.setOnItemSelectedListener(mSpinnerListener);

        showAmpView(mSpinner.getSelectedItemPosition());

        mRecordStartStopBtn.setOnClickListener(mClickListener);
        mPlayPauseBtn.setOnClickListener(mClickListener);

        VoiceManager.getInstance(this).setOnRecordListener(mRecordListener);
        VoiceManager.getInstance(this).setOnPlayListener(mPlayListener);
    }

    private void showAmpView (int position) {
        Index index = mSpinnerAdapter.getItem(position);
        switch (index.getTitle()) {
            case R.string.title_amplitude_bar_view:
                AmplitudeBarView barView = new AmplitudeBarView(this);
                barView.setPeriod(400);
                barView.setDirection(AmplitudeBarView.DIRECTION_LEFT_TO_RIGHT);
                barView.setMinValue(2);
                barView.setMaxValue(100);
                barView.setDebug(true);
                mAmpView = barView;
                View prefView = LayoutInflater.from(this).inflate(R.layout.layout_pref_amp_bar, null);
                mSv.removeAllViews();
                mSv.addView(prefView);
                mAp = new AmpBarPresenter(barView, prefView);
                break;
            case R.string.title_amplitude_bezier_view:
                AmplitudeBezierView bezierView = new AmplitudeBezierView(this);
                bezierView.setMoveSpeed(12);
                bezierView.setShowHintLine(true);
                bezierView.setWaveCount(2);
                mAmpView = bezierView;
                mSv.removeAllViews();
                break;
        }
        if (mAmpView != null) {
            mAmpLayout.removeAllViews();

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            );
            final int padding = (int)(getResources().getDisplayMetrics().density * 8);
            mAmpView.setPadding(padding, padding, padding, padding);
            mAmpLayout.addView(mAmpView, params);
        }
    }

    private String getRecordPath () {
        File file = new File(getExternalCacheDir(),
                "voice" + File.separator + System.currentTimeMillis() + ".mp3");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file.getAbsolutePath();
    }
}
