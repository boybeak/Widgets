package com.nulldreams.demo.widgets;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.nulldreams.widget.WaveformView;

import java.io.File;
import java.io.IOException;

public class WaveformActivity extends AppCompatActivity {

    private WaveformView waveformView;
    private Button btn;
    private RadioGroup mRg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waveform);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        waveformView = (WaveformView) findViewById(R.id.waveform_view);
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
                        waveformView.setDirection(WaveformView.DIRECTION_LEFT_TO_RIGHT);
                        break;
                    case R.id.waveform_right_to_left:
                        waveformView.setDirection(WaveformView.DIRECTION_RIGHT_TO_LEFT);
                        break;
                }
            }
        });
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
    private void startRecord () throws IOException {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(new File(getExternalCacheDir(), System.currentTimeMillis() + ".mp3").getAbsolutePath());
        recorder.prepare();
        recorder.start();

        btn.setText(R.string.btn_stop_record);

        waveformView.attachMediaRecorder(recorder);
    }

    private void stopRecord () {
        waveformView.detachedMediaRecorder();
        // Recording is now started
        recorder.stop();
        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
        recorder.release();
        recorder = null;

        btn.setText(R.string.btn_start_record);
    }

}
