package com.nulldreams.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

/**
 * Created by gaoyunfei on 2017/5/10.
 */

public class AmplitudeBezierView extends AmplitudeView {

    private Path mPath = null;

    private Paint mPaint;

    public AmplitudeBezierView(Context context) {
        this(context, null);
    }

    public AmplitudeBezierView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AmplitudeBezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAmplitudeBezierView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AmplitudeBezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAmplitudeBezierView(context, attrs);
    }

    private void initAmplitudeBezierView (Context context, AttributeSet attrs) {
        mPath = new Path();

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOneBezier(canvas);
    }

    private void drawOneBezier (Canvas canvas) {
        getLastAmplitude()
        mPath.moveTo(0, getHeight() / 2);
        mPath.quadTo(getWidth() / 4, 0, getWidth() / 2, getHeight() / 2);
        mPath.quadTo(getWidth() / 4 * 3, getHeight(), getWidth(), getHeight() / 2);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void onNewAmplitude(int amplitude) {
        super.onNewAmplitude(amplitude);
    }
}
