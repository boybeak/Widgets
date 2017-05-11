package com.nulldreams.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by gaoyunfei on 2017/5/10.
 */

public class AmplitudeBezierView extends AmplitudeView {

    private static final String TAG = AmplitudeBezierView.class.getSimpleName();

    private static final float[] P = {0f, 1f, 0f, -1f};

    private Path mPath = null;

    private Paint mPaint;

    private float mStartX, mStartY;
    private float mOffset;
    private int mKeyPointCount = 1;
    private float mUnitX = 0;

    private PointF[] mPoints = null;

    private int direction = 1;

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

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AmplitudeBezierView);
        mKeyPointCount = array.getInt(R.styleable.AmplitudeBezierView_keyPointCount, 1);
        array.recycle();

        mPoints = new PointF[mKeyPointCount * 4 + 1];
        for (int i = 0; i < mPoints.length; i++) {
            mPoints[i] = new PointF();
        }

        mPath = new Path();

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mStartX = -getMeasuredWidth() + getPaddingLeft();
        mStartY = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / 2;
        mUnitX = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) * 1.0f / (mKeyPointCount * 2);

        for (int i = 0; i < mPoints.length; i++) {
            mPoints[i].x = mStartX + i * mUnitX;
            mPoints[i].y = mStartY + P[i % P.length] * 80;
            Log.v(TAG, "onMeasure [" + i + "]" + " x=" + mPoints[i].x + " y=" + mPoints[i].y);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOneBezier(canvas);
    }

    private void drawOneBezier (Canvas canvas) {
        if (mOffset > getWidth() - getPaddingRight()) {
            mOffset = mOffset - (getWidth() - getPaddingRight());
        }
        mPath.reset();
        mPath.moveTo(mPoints[0].x, mPoints[0].y);
        for (int i = 1; i < mPoints.length; i += 2) {
            mPath.quadTo(mPoints[i].x + mOffset, mPoints[i].y, mPoints[i + 1].x + mOffset, mPoints[i + 1].y);
        }
        //mPath.close();
        canvas.drawPath(mPath, mPaint);
        for (int i = 0; i < mPoints.length; i++) {
            canvas.drawPoint(mPoints[i].x, mPoints[i].y, mPaint);
        }
        mOffset += 20 * direction;
        invalidate();
    }

    @Override
    public void onNewAmplitude(int amplitude) {
        super.onNewAmplitude(amplitude);
    }
}
