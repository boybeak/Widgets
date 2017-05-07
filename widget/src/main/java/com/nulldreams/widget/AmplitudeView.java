package com.nulldreams.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoyunfei on 2017/5/7.
 */

abstract class AmplitudeView extends View {

    private static final String TAG = AmplitudeView.class.getSimpleName();

    private int mPeriod;

    private MediaRecorder mRecorder;

    private List<Integer> mAmpArray;

    private Runnable mAmpRun = new Runnable() {
        @Override
        public void run() {
            if (mRecorder != null) {
                final int amp = mRecorder.getMaxAmplitude();
                mAmpArray.add(amp);
                onNewAmplitude(amp);
                postDelayed(this, getPeriod());
            }
        }
    };

    private int mLastAmplitude;

    public AmplitudeView(Context context) {
        this(context, null);
    }

    public AmplitudeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AmplitudeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAmplitudeView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AmplitudeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAmplitudeView(context, attrs);
    }

    private void initAmplitudeView (Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AmplitudeView);
        setPeriod(array.getInt(R.styleable.AmplitudeView_period,
                getContext().getResources().getInteger(R.integer.config_amplitude_period_default)));
        array.recycle();
        mAmpArray = new ArrayList<>();
    }

    public void onNewAmplitude (int amplitude) {
        mLastAmplitude = amplitude;
    }

    public int getLastAmplitude () {
        return mLastAmplitude;
    }

    public int getPeriod() {
        return mPeriod;
    }

    public void setPeriod(int period) {
        this.mPeriod = period;
    }

    public boolean isAttachedWithRecorder () {
        return mRecorder != null;
    }

    public void attachMediaRecorder(MediaRecorder recorder) {
        //reset();
        mRecorder = recorder;
        post(mAmpRun);
        //postInvalidate();
    }

    public Amplitude detachedMediaRecorder () {
        Amplitude amplitude = new Amplitude(mPeriod, mAmpArray);

        mAmpArray.clear();
        removeCallbacks(mAmpRun);
        mRecorder = null;
        return amplitude;
        //mByteArray = null;

        //return amplitude;
    }

    public int getAmplitudeSize () {
        return mAmpArray.size();
    }

    public int getAmplitude (int index) {
        return mAmpArray.get(index);
    }

    public void play (Amplitude amplitude) {

    }
}
