package com.nulldreams.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;

import com.nulldreams.widget.decoration.BarDecoration;
import com.nulldreams.widget.decoration.DefaultDecoration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/**
 * Created by boybe on 2017/5/2.
 */

public class AmplitudeBarView extends AmplitudeView {

    private static final String TAG = AmplitudeBarView.class.getSimpleName();

    private static final int DEFAULT_AMP_MAX = /*32767*/18000;

    private static final int DEFAULT_BAR_WIDTH_DP = 8, DEFAULT_GAP_WIDTH_DP = 2,
            DEFAULT_PERIOD = 1000, DEFAULT_MAX_DURATION = 60 * 60 * 1000;

    public static final int DIRECTION_LEFT_TO_RIGHT = 1, DIRECTION_RIGHT_TO_LEFT = -1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DIRECTION_LEFT_TO_RIGHT, DIRECTION_RIGHT_TO_LEFT})
    public @interface Direction{}

    private boolean debug = false;

//    private byte[] mByteArray;

    private int mCursor = 0, mDrawBarBufSize = 50;
    private float mBarWidth, mGapWidth, mHeightUnit;
    private long /*mPeriod = DEFAULT_PERIOD,*/ mMovePeriod = 20, mLastNewBarTime = 0,
            mMaxDuration = DEFAULT_MAX_DURATION;

    private int mBarColor;

    private int mMaxValue, mMinValue, mAmpUnit = 1, mMaxAmp;

    private @Direction int mDirection = DIRECTION_LEFT_TO_RIGHT;

    private Paint mPaint, mTextPaint;

    private BarDecoration mDecoration = new DefaultDecoration();

    public AmplitudeBarView(Context context) {
        this(context, null);
    }

    public AmplitudeBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AmplitudeBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initThis(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AmplitudeBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initThis(context, attrs);
    }

    private void initThis (Context context, @Nullable AttributeSet attrs) {

        final float density = context.getResources().getDisplayMetrics().density;

        final float barWidthDef = density * DEFAULT_BAR_WIDTH_DP;
        final float gapWidthDef = density * DEFAULT_GAP_WIDTH_DP;

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AmplitudeBarView);
            mBarColor = array.getColor(R.styleable.AmplitudeBarView_barColor, Color.GREEN);
            mBarWidth = array.getDimensionPixelSize(R.styleable.AmplitudeBarView_barWidth, 0);
            setBarColor(array.getColor(R.styleable.AmplitudeBarView_barColor, Color.GREEN));

            if (mBarWidth == 0) {
                mBarWidth = barWidthDef;
            }
            mGapWidth = array.getDimensionPixelSize(R.styleable.AmplitudeBarView_gapWidth, 0);
            if (mGapWidth == 0) {
                mGapWidth = gapWidthDef;
            }
            setAmpMax(array.getInt(R.styleable.AmplitudeBarView_maxAmplitude, DEFAULT_AMP_MAX));
            setMaxValue(array.getInt(R.styleable.AmplitudeBarView_maxValue, Byte.MAX_VALUE));
            setMinValue(array.getInt(R.styleable.AmplitudeBarView_minValue, 0));
            setMaxDuration(array.getInt(R.styleable.AmplitudeBarView_maxDuration, DEFAULT_MAX_DURATION));
            setDirection(array.getInteger(R.styleable.AmplitudeBarView_direction, DIRECTION_LEFT_TO_RIGHT));
            setDebug(array.getBoolean(R.styleable.AmplitudeBarView_debug, false));
            array.recycle();
        }

        mPaint = new Paint();
        mTextPaint = new Paint();
        mPaint.setColor(mBarColor);
        mTextPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /*Log.v(TAG, "onMeasure widthMode=" + widthMode + " widthSize=" + widthSize);
        Log.v(TAG, "onMeasure widthSizeMeasured=" + widthSize);*/

        mDrawBarBufSize = (int) Math.ceil(getMeasuredWidth() / (mBarWidth + mGapWidth));
        mMovePeriod = (int)(getPeriod() / (mBarWidth + mGapWidth));
        refreshAmpUnit();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isAttachedWithRecorder()) {

            drawBars(canvas);
            postInvalidate();

        }
    }

    private void drawBars (Canvas canvas) {

        long now = SystemClock.elapsedRealtime();

        long newBarDelta = now - mLastNewBarTime;

        float move = 1f * newBarDelta / mMovePeriod;

        if (debug) {
            String s = "ViewWid=" + getWidth() + " ViewHei=" + getHeight() + " mAmpUnit=" + mAmpUnit + " mHeightUnit=" + mHeightUnit;
            canvas.drawText(s, 0, 10, mTextPaint);
        }

        //mDecoration.drawBars(this, canvas, mCursor, mByteArray);
        if (mDirection == DIRECTION_LEFT_TO_RIGHT) {
            drawFromLeftToRight(canvas, move, newBarDelta);
        } else if (mDirection == DIRECTION_RIGHT_TO_LEFT) {
            drawFromRightToLeft(canvas, move, newBarDelta);
        }

        if (newBarDelta > getPeriod()) {
            mCursor = getAmplitudeSize() - 1;
            mLastNewBarTime = now;
        }
    }

    private void drawFromLeftToRight (Canvas canvas, float deltaX, long deltaTime) {
        int offset = 0;
        for (int i = mCursor; i > mCursor - mDrawBarBufSize && i >= 0; i--) {

            float left = getPaddingLeft() + (mGapWidth + mBarWidth) * offset + mGapWidth + deltaX;
            float right = left + mBarWidth;
            float maxRight = getWidth() - getPaddingRight();
            if (right > maxRight) {
                right = maxRight;
            }
            float bottom = getHeight() - getPaddingBottom();
            if (offset == 0) {
                float full = valueAt(i) * mHeightUnit;
                float remain = full * ((float)deltaTime / getPeriod()) * 3.2f;
                if (remain > full) {
                    remain = full;
                }
                canvas.drawRect(left, bottom - remain, right, bottom, mPaint);
            } else {
                canvas.drawRect(left, bottom - (valueAt(i) * mHeightUnit), right, bottom, mPaint);
            }
            if (debug) {
                float b = (getHeight() - getPaddingBottom() - getPaddingTop()) / 2;
                canvas.drawText(i + "", left, b - 10, mTextPaint);
                canvas.drawText(valueAt(i) + "", left, b + 10, mTextPaint);
            }
            offset++;
        }
    }

    private void drawFromRightToLeft (Canvas canvas, float deltaX, long deltaTime) {
        int offset = 0;
        for (int i = mCursor; i > mCursor - mDrawBarBufSize && i >= 0; i--) {

            float right = getWidth() - getPaddingRight()
                    - (mGapWidth + mBarWidth) * offset - mGapWidth - deltaX;
            float left = right - mBarWidth;
            float maxLeft = getPaddingLeft();
            if (left < maxLeft) {
                left = maxLeft;
            }
            float bottom = getHeight() - getPaddingBottom();
            if (offset == 0) {
                float full = valueAt(i) * mHeightUnit;
                float remain = full * ((float)deltaTime / getPeriod()) * 3.2f;
                if (remain > full) {
                    remain = full;
                }
                canvas.drawRect(left, bottom - remain, right, bottom, mPaint);
            } else {
                canvas.drawRect(left, bottom - (valueAt(i) * mHeightUnit), right, bottom, mPaint);
            }
            if (debug) {
                float b = (getHeight() - getPaddingBottom() - getPaddingTop()) / 2;
                canvas.drawText(i + "", left, b - 10, mTextPaint);
                canvas.drawText(valueAt(i) + "", left, b + 10, mTextPaint);
            }
            offset++;
        }
    }

    /*public void addWave (byte wave) {
        mByteArray.add(wave);
    }*/

    public void attachMediaRecorder(MediaRecorder recorder) {
        super.attachMediaRecorder(recorder);
        reset();
        mCursor = getAmplitudeSize() - 1;
        mLastNewBarTime = SystemClock.elapsedRealtime();
        postInvalidate();
    }

    public Amplitude detachedMediaRecorder () {
        super.detachedMediaRecorder();
        mCursor = 0;
        mLastNewBarTime = 0;
        /*byte[] subArray = Arrays.copyOfRange(mByteArray, 0, mCursor);
        Amplitude amplitude = new Amplitude(
                subArray, mDirection, mCursor, mDrawBarBufSize, mMaxValue, mMinValue, mAmpUnit,
                mMaxAmp, mBarColor, mBarWidth, mGapWidth, mHeightUnit, getPeriod(), mMovePeriod
        );


        mByteArray = null;*/

        return null;
    }

    private void reset () {
        refreshByteArray();
        //mCursor = 0;
        mLastNewBarTime = 0;
    }

    private int amplitudeToValue (int amp) {
        return Math.max(Math.min(mMaxValue, amp / mAmpUnit), mMinValue);
    }

    private int valueAt (int index) {
        return amplitudeToValue(getAmplitude(index));
    }

    /*private void putInt (int amp) {
        putByte((byte)amplitudeToValue(amp));
    }

    private void putByte (byte b) {
        if (mCursor < mByteArray.length - 1) {
            if (b < mMinValue) {
                b = (byte) mMinValue;
            } else if (b > mMaxValue) {
                b = (byte) mMaxValue;
            }
            mByteArray[mCursor + 1] = b;
        }
    }*/

    public void setBarColorResource (@ColorRes int colorRes) {
        setBarColor(getContext().getResources().getColor(colorRes));
    }

    public void setBarColor (int barColor) {
        this.mBarColor = barColor;
        if (this.mPaint != null) {
            this.mPaint.setColor(mBarColor);
        }
    }

    public void setBarWidth(float barWidth) {
        this.mBarWidth = barWidth;
    }

    public float getBarWidth() {
        return mBarWidth;
    }

    public void setGapWidth (float gapWidth) {
        this.mGapWidth = gapWidth;
    }

    public float getGapWidth() {
        return mGapWidth;
    }

    public int getBarColor () {
        return mBarColor;
    }

    public void setDirection (@Direction int direction) {
        mDirection = direction;
    }

    public @Direction int getDirection () {
        return mDirection;
    }

    public void setMaxValue (int maxValue) {
        mMaxValue = maxValue;
        refreshAmpUnit();
    }

    public int getMaxValue () {
        return mMaxValue;
    }

    public void setMinValue (int minValue) {
        mMinValue = minValue;
        //refreshAmpUnit();
    }

    private void setAmpMax (int ampMax) {
        this.mMaxAmp = ampMax;
        refreshAmpUnit();
    }

    public int getMinValue () {
        return mMinValue;
    }

    public void setMaxDuration (int maxDuration) {
        mMaxDuration = maxDuration;
        refreshByteArray();
    }

    public long getMaxDuration () {
        return mMaxDuration;
    }

    public void setPeriod (int period) {
        super.setPeriod(period);
        refreshByteArray();
    }

    private void refreshByteArray () {
        int length = (int)(mMaxDuration / getPeriod());
        /*if (mByteArray == null || mByteArray.length != length) {
            mByteArray = new byte[length];
        }
        for (int i = 0; i < mByteArray.length; i++) {
            if (mByteArray[i] <= 0) {
                mByteArray[i] = (byte) mMinValue;
            }
        }*/
    }

    private void refreshAmpUnit () {
        //int minus =  - mMinValue;
        if (mMaxValue <= 0) {
            return;
        }
        mAmpUnit = mMaxAmp / mMaxValue;
        mHeightUnit = (float) (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / mMaxValue;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }
}
