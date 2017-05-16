package com.nulldreams.demo.widgets;

import android.graphics.Color;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;

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
            switch (v.getId()) {
                case R.id.btn_record_or_stop:
                    if (mAmpView.isAttachedWithRecorder()) {
                        mAmpView.detachedMediaRecorder();
                    } else {
                        mLastPath = getRecordPath();
                        MediaRecorder recorder = VoiceManager.getInstance(AmplitudeActivity.this).startRecord(mLastPath);
                        mAmpView.attachMediaRecorder(recorder);
                    }
                    v.setSelected(mAmpView.isAttachedWithRecorder());
                    break;
            }
        }
    };

    private AmplitudeView mAmpView;

    private FrameLayout mAmpLayout;

    private AppCompatImageButton mRecordStartStopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amplitude);

        mAmpLayout = (FrameLayout)findViewById(R.id.view_layout);

        mRecordStartStopBtn = (AppCompatImageButton)findViewById(R.id.btn_record_or_stop);

        mSpinner = (AppCompatSpinner)findViewById(R.id.spinner);
        mSpinnerAdapter = new SpinnerAdapter();
        mSpinner.setAdapter(mSpinnerAdapter);

        mSpinner.setOnItemSelectedListener(mSpinnerListener);

        showAmpView(mSpinner.getSelectedItemPosition());

        mRecordStartStopBtn.setOnClickListener(mClickListener);
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
                break;
            case R.string.title_amplitude_bezier_view:
                mAmpView = new AmplitudeBezierView(this);
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
