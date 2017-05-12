package com.nulldreams.demo.widgets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nulldreams.widget.AmplitudeBezierView;

import java.io.File;

public class BezierActivity extends AppCompatActivity {

    private AmplitudeBezierView mBezierView;

    private Button mStartStopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bezier);

        mBezierView = (AmplitudeBezierView)findViewById(R.id.bezier_view);
        mStartStopBtn = (Button)findViewById(R.id.bezier_start);
    }

    public void voiceBtn (View view) {
        if (VoiceHelper.isRecording()) {
            stopRecord();
        } else {
            startRecord();
        }
    }

    private File mTargetFile = null;
    private void startRecord () {
        mTargetFile = new File(getExternalCacheDir(), System.currentTimeMillis() + ".mp3");
        mBezierView.attachMediaRecorder(VoiceHelper.startRecord(mTargetFile.getAbsolutePath()));
        mStartStopBtn.setText(R.string.btn_stop_record);
    }

    private void stopRecord () {
        if (VoiceHelper.isRecording()) {
            mBezierView.detachedMediaRecorder();
            VoiceHelper.stopRecord();
            mStartStopBtn.setText(R.string.btn_start_record);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecord();
    }
}
