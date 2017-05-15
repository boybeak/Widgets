package com.nulldreams.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
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

    private int mAmplitudeValue;

    private int mMoveSpeed = 8;

    private ObjectAnimator animator;

    private float mLineWidth, mHintLineWidth;

    private int mLineColor, mHintLineColor;

    private boolean showHintLine = false;

    private int mPlayingCursor;
    private Runnable mPlayingRun = new Runnable() {
        @Override
        public void run() {
            Amplitude amp = getAmplitude();
            if (isPlaying() && amp != null) {
                int[] ampArray = amp.getAmplitudeArray();
                if (mPlayingCursor < ampArray.length) {
                    onNewAmplitude(amp.getAmplitudeArray()[mPlayingCursor++]);
                    postDelayed(this, getPeriod());
                }
            }
        }
    };

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

        mPaint = new Paint();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AmplitudeBezierView);
        mKeyPointCount = array.getInt(R.styleable.AmplitudeBezierView_waveCount, 1) << 1;
        setMoveSpeed(array.getInt(R.styleable.AmplitudeBezierView_moveSpeed, 8));
        setLineWidth(array.getDimensionPixelSize(R.styleable.AmplitudeBezierView_lineWidth, 4));
        setLineColor(array.getColor(R.styleable.AmplitudeBezierView_lineColor, Color.DKGRAY));
        setShowHintLine(array.getBoolean(R.styleable.AmplitudeBezierView_showHintLine, false));
        setHintLineColor(array.getColor(R.styleable.AmplitudeBezierView_hintLineColor, Color.LTGRAY));
        setHintLineWidth(array.getDimensionPixelSize(R.styleable.AmplitudeBezierView_hintLineWidth, 4));
        array.recycle();

        mPoints = new PointF[mKeyPointCount * 4 + 1];
        for (int i = 0; i < mPoints.length; i++) {
            mPoints[i] = new PointF();
        }

        mPath = new Path();

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mStartX = -getMeasuredWidth();
        mStartY = getMeasuredHeight() / 2;
        mUnitX = getMeasuredWidth() * 1.0f / (mKeyPointCount * 2);

        for (int i = 0; i < mPoints.length; i++) {
            mPoints[i].x = mStartX + i * mUnitX;
            mPoints[i].y = mStartY + P[i % P.length] * 0;
            //Log.v(TAG, "onMeasure [" + i + "]" + " x=" + mPoints[i].x + " y=" + mPoints[i].y);
        }
    }

    @Override
    public void startPlay() {
        super.startPlay();
        post(mPlayingRun);
    }

    @Override
    public void stopPlay() {
        super.stopPlay();
        removeCallbacks(mPlayingRun);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isAttachedWithRecorder() || isPlaying()) {
            drawOneBezier(canvas);
        } else if (showHintLine) {
            drawHintLine(canvas);
        }
        //invalidate();
    }

    private void drawOneBezier (Canvas canvas) {
        if (mOffset > getWidth()) {
            mOffset = 0;
        }
        mPath.reset();
        mPath.moveTo(mPoints[0].x + mOffset, mPoints[0].y);
        for (int i = 1; i < mPoints.length; i += 2) {
            mPath.quadTo(mPoints[i].x + mOffset, mPoints[i].y + computeOffsetY(i), mPoints[i + 1].x + mOffset, mPoints[i + 1].y + computeOffsetY(i + 1));
        }
        //mPath.close();
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(mLineWidth);
        canvas.drawPath(mPath, mPaint);
        mOffset += mMoveSpeed;
    }

    private void drawHintLine (Canvas canvas) {
        mPaint.setColor(mHintLineColor);
        mPaint.setStrokeWidth(mHintLineWidth);
        canvas.drawLine(mPoints[0].x, mPoints[0].y,
                mPoints[mPoints.length - 1].x, mPoints[mPoints.length - 1].y, mPaint);
    }

    @Override
    public void onNewAmplitude(int amplitude) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        if (animator == null) {
            animator = ObjectAnimator.ofInt(this, "amplitudeValue", mAmplitudeValue, amplitude);
        } else {
            animator.setIntValues(mAmplitudeValue, amplitude);
        }
        animator.setDuration(getPeriod());
        animator.start();
        super.onNewAmplitude(amplitude);
    }

    public void setAmplitudeValue (int amplitudeValue) {
        mAmplitudeValue = amplitudeValue;
        invalidate();
    }

    public void setMoveSpeed (int pixel) {
        mMoveSpeed = pixel;
    }

    private float computeOffsetY(int index) {
        float offset = (float) Math.sqrt(mAmplitudeValue);
        offset = Math.min(offset, (getHeight() - getPaddingBottom() - getPaddingTop()) / 2);
        return offset * P[index % P.length];
    }

    public void setLineWidth (float width) {
        mLineWidth = width;
        if (mPaint != null) {
            mPaint.setStrokeWidth(mLineWidth);
        }
    }

    public void setLineColor (int color) {
        mLineColor = color;
    }

    public void setShowHintLine (boolean show) {
        showHintLine = show;
        if (!isAttachedWithRecorder()) {
            invalidate();
        }
    }

    public void setHintLineColor (int hintLineColor) {
        mHintLineColor = hintLineColor;
    }

    public void setHintLineWidth (float hintLineWidth) {
        mHintLineWidth = hintLineWidth;
    }

}
