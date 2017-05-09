package com.nulldreams.demo.widgets;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.nulldreams.widget.Amplitude;
import com.nulldreams.widget.AmplitudeBarView;

import java.io.File;
import java.io.IOException;

public class WaveformActivity extends AppCompatActivity {

    private AmplitudeBarView waveformView;
    private Button btn, checkBtn, playBtn;
    private RadioGroup mRg;
    private AppCompatCheckBox mCb;
    private AppCompatTextView mLogTv;

    private File ampFile;

    private Amplitude amp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waveform);

        waveformView = (AmplitudeBarView) findViewById(R.id.waveform_view);
        btn = (Button)findViewById(R.id.waveform_start_stop);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PackageManager.PERMISSION_GRANTED !=
                        ContextCompat.checkSelfPermission(WaveformActivity.this, Manifest.permission.RECORD_AUDIO)) {
                    ActivityCompat.requestPermissions(WaveformActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
                } else {
                    if (recorder == null) {
                        try {
                            startRecord();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        stopRecord();
                    }
                }
            }
        });
        mRg = (RadioGroup)findViewById(R.id.waveform_direction);
        mRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.waveform_left_to_right:
                        waveformView.setDirection(AmplitudeBarView.DIRECTION_LEFT_TO_RIGHT);
                        break;
                    case R.id.waveform_right_to_left:
                        waveformView.setDirection(AmplitudeBarView.DIRECTION_RIGHT_TO_LEFT);
                        break;
                }
            }
        });
        mCb = (AppCompatCheckBox)findViewById(R.id.waveform_debug);
        mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                waveformView.setDebug(isChecked);
            }
        });
        findViewById(R.id.waveform_check_amp_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ampFile != null && ampFile.exists()) {
                    Amplitude amp = Amplitude.fromFile(ampFile);
                    mLogTv.append(amp.toString() + "\n");
                }
            }
        });
        playBtn = (Button) findViewById(R.id.waveform_play_amp_file);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amp != null) {
                    if (waveformView.isPlaying()) {
                        waveformView.stopPlay();
                    } else {
                        waveformView.setAmplitude(amp);
                        waveformView.startPlay();
                    }
                }
                playBtn.setText(waveformView.isPlaying() ? R.string.btn_stop_play_amp_file : R.string.btn_play_amp_file);
            }
        });
        mLogTv = (AppCompatTextView)findViewById(R.id.waveform_log_tv);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                startRecord();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recorder != null) {
            stopRecord();
        }
    }

    private MediaRecorder recorder;
    private long startTime;
    private void startRecord () throws IOException {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        startTime = System.currentTimeMillis();
        recorder.setOutputFile(new File(getExternalCacheDir(), startTime + ".mp3").getAbsolutePath());
        recorder.prepare();
        recorder.start();

        btn.setText(R.string.btn_stop_record);

        waveformView.attachMediaRecorder(recorder);
        mLogTv.setText(null);
    }

    private void stopRecord () {
        ampFile = new File(getExternalCacheDir(), "amp" + File.separator + startTime + ".amp");
        amp = waveformView.detachedMediaRecorder();
        if (amp != null) {
            amp.flushTo(ampFile);
        }
        mLogTv.append(".amp file saved at:" + ampFile.getAbsolutePath() + "\n");
        // Recording is now started
        recorder.stop();
        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
        recorder.release();
        recorder = null;

        btn.setText(R.string.btn_start_record);
    }

}
