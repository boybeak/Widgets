package com.nulldreams.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by gaoyunfei on 2017/5/14.
 */

public class VisualizerBarView extends VisualizerView {

    private static final String TAG = VisualizerBarView.class.getSimpleName();

    private Paint mPaint;

    public VisualizerBarView(Context context) {
        this(context, null);
    }

    public VisualizerBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VisualizerBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVisualizerBarView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VisualizerBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVisualizerBarView(context, attrs);
    }

    private void initVisualizerBarView (Context context, @Nullable AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
    }

    @Override
    public void onDrawWaveform(Canvas canvas, byte[] waveform, int samplingRate) {
        Log.v(TAG, "onDrawWaveform data.length=" + waveform.length);
        int widthUnit = getWidth() / waveform.length;
        for (int i = 0; i < waveform.length; i++) {
            canvas.drawText("w:" + waveform[i], 10 + widthUnit * i, 20, mPaint);
        }
    }

    @Override
    public void onDrawFft(Canvas canvas, byte[] fft, int samplingRate) {
        Log.v(TAG, "onDrawFft data.length=" + fft.length);
        int widthUnit = getWidth() / fft.length;
        for (int i = 0; i < fft.length; i++) {
            canvas.drawText("f:" + fft[i], 10 + widthUnit * i, 40, mPaint);
        }
    }
}
