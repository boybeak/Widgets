package com.nulldreams.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by gaoyunfei on 2017/5/7.
 */

abstract class AmplitudeView extends View {

    private int mPeriod;

    private MediaRecorder mRecorder;

    private Runnable mAmpRun = new Runnable() {
        @Override
        public void run() {
            if (mAmpRun != null) {
                onNewAmplitude(mRecorder.getMaxAmplitude());
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
        /*byte[] subArray = Arrays.copyOfRange(mByteArray, 0, mCursor);
        Amplitude amplitude = new Amplitude(
                subArray, mDirection, mCursor, mDrawBarBufSize, mMaxValue, mMinValue, mAmpUnit,
                AMP_MAX, mBarColor, mBarWidth, mGapWidth, mHeightUnit, getPeriod(), mMovePeriod
        );*/


        removeCallbacks(mAmpRun);
        mRecorder = null;
        return null;
        //mByteArray = null;

        //return amplitude;
    }
}
