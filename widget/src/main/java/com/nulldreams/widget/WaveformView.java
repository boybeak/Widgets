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
import android.view.View;

/**
 * Created by boybe on 2017/5/2.
 */

public class WaveformView extends View {

    private static final String TAG = WaveformView.class.getSimpleName();

    private static final int AMP_MAX = 32767;

    private static final int DEFAULT_BAR_WIDTH_DP = 8, DEFAULT_GAP_WIDTH_DP = 2,
            DEFAULT_PERIOD = 1000, DEFAULT_MAX_DURATION = 60 * 60 * 1000;

    public static final int DIRECTION_LEFT_TO_RIGHT = 1, DIRECTION_RIGHT_TO_LEFT = -1;

    @IntDef({DIRECTION_LEFT_TO_RIGHT, DIRECTION_RIGHT_TO_LEFT})
    public @interface Direction{}

    private boolean debug = false;

    private byte[] mByteArray;

    private int mCursor = 0, mCount = 50;
    private float mBarWidth, mGapWidth, mHeightUnit;
    private long mPeriod = DEFAULT_PERIOD, mMovePeriod = 20, mLastNewBarTime = 0,
            mMaxDuration = DEFAULT_MAX_DURATION;

    private int mBarColor;

    private int mMaxValue, mMinValue, mAmpUnit = 1;

    private @Direction int mDirection = DIRECTION_LEFT_TO_RIGHT;

    private MediaRecorder mRecorder;

    private Paint mPaint, mTextPaint;

    private Runnable mAmpRun = new Runnable() {
        @Override
        public void run() {
            if (mAmpRun != null) {
                putInt(mRecorder.getMaxAmplitude());
                postDelayed(this, mPeriod);
            }
        }
    };

    public WaveformView(Context context) {
        this(context, null);
    }

    public WaveformView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveformView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initThis(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WaveformView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initThis(context, attrs);
    }

    private void initThis (Context context, @Nullable AttributeSet attrs) {

        final float density = context.getResources().getDisplayMetrics().density;

        final float barWidthDef = density * DEFAULT_BAR_WIDTH_DP;
        final float gapWidthDef = density * DEFAULT_GAP_WIDTH_DP;

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WaveformView);
            mBarColor = array.getColor(R.styleable.WaveformView_barColor, Color.GREEN);
            mBarWidth = array.getDimensionPixelSize(R.styleable.WaveformView_barWidth, 0);
            setBarColor(array.getColor(R.styleable.WaveformView_barColor, Color.GREEN));

            if (mBarWidth == 0) {
                mBarWidth = barWidthDef;
            }
            mGapWidth = array.getDimensionPixelSize(R.styleable.WaveformView_gapWidth, 0);
            if (mGapWidth == 0) {
                mGapWidth = gapWidthDef;
            }
            setMaxValue(array.getInt(R.styleable.WaveformView_maxValue, Byte.MAX_VALUE));
            setMinValue(array.getInt(R.styleable.WaveformView_minValue, 0));
            setPeriod(array.getInt(R.styleable.WaveformView_period, DEFAULT_PERIOD));
            setMaxDuration(array.getInt(R.styleable.WaveformView_maxDuration, DEFAULT_MAX_DURATION));
            setDirection(array.getInteger(R.styleable.WaveformView_direction, DIRECTION_LEFT_TO_RIGHT));
            setDebug(array.getBoolean(R.styleable.WaveformView_debug, false));
            array.recycle();
        }

        mPaint = new Paint();
        mTextPaint = new Paint();
        mPaint.setColor(mBarColor);
        mTextPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCount = (int) Math.ceil(getMeasuredWidth() / (mBarWidth + mGapWidth));
        mMovePeriod = (int)(mPeriod / (mBarWidth + mGapWidth));
        refreshAmpUnit();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRecorder != null) {
            drawBars(canvas);
            postInvalidate();
        }
    }

    private void drawBars (Canvas canvas) {
        long now = SystemClock.elapsedRealtime();

        long delta = now - mLastNewBarTime;

        float move = delta / mMovePeriod;

        if (mCursor >= mByteArray.length) {
            return;
        }

        if (mDirection == DIRECTION_LEFT_TO_RIGHT) {
            drawFromLeftToRight(canvas, move, delta);
        } else if (mDirection == DIRECTION_RIGHT_TO_LEFT) {
            drawFromRightToLeft(canvas, move, delta);
        }

        if (delta > mPeriod) {
            mCursor++;
            mLastNewBarTime = now;
        }
    }

    private void drawFromLeftToRight (Canvas canvas, float deltaX, long deltaTime) {
        int offset = 0;
        for (int i = mCursor; i > mCursor - mCount && i > 0; i--) {
            float left = getPaddingLeft() + (mGapWidth + mBarWidth) * offset + mGapWidth + deltaX;
            float right = left + mBarWidth;
            float maxRight = getWidth() - getPaddingRight();
            if (right > maxRight) {
                right = maxRight;
            }
            float bottom = getHeight() - getPaddingBottom();
            if (offset == 0) {
                float full = mByteArray[i] * mHeightUnit;
                float remain = full * ((float)deltaTime / mPeriod) * 3;
                if (remain > full) {
                    remain = full;
                }
                canvas.drawRect(left, bottom - remain, right, bottom, mPaint);
            } else {
                canvas.drawRect(left, bottom - (mByteArray[i] * mHeightUnit), right, bottom, mPaint);
            }
            if (debug) {
                canvas.drawText(i + "", left, bottom - 30, mTextPaint);
                canvas.drawText(mByteArray[i] + "", left, bottom - 15, mTextPaint);
            }
            offset++;
        }
    }

    private void drawFromRightToLeft (Canvas canvas, float deltaX, long deltaTime) {
        int offset = 0;
        for (int i = mCursor; i > mCursor - mCount && i > 0; i--) {
            float right = getWidth() - getPaddingRight() - (mGapWidth + mBarWidth) * offset - mGapWidth - deltaX;
            float left = right - mBarWidth;
            float maxLeft = getPaddingLeft();
            if (left < maxLeft) {
                left = maxLeft;
            }
            float bottom = getHeight() - getPaddingBottom();
            if (offset == 0) {
                float full = mByteArray[i] * mHeightUnit;
                float remain = full * ((float)deltaTime / mPeriod) * 3;
                if (remain > full) {
                    remain = full;
                }
                canvas.drawRect(left, bottom - remain, right, bottom, mPaint);
            } else {
                canvas.drawRect(left, bottom - (mByteArray[i] * mHeightUnit), right, bottom, mPaint);
            }
            if (debug) {
                canvas.drawText(i + "", left, bottom - 30, mTextPaint);
                canvas.drawText(mByteArray[i] + "", left, bottom - 15, mTextPaint);
            }
            offset++;
        }
    }

    /*public void addWave (byte wave) {
        mByteArray.add(wave);
    }*/

    public void attachMediaRecorder(MediaRecorder recorder) {
        reset();
        mRecorder = recorder;
        //mLastNewBarTime = SystemClock.elapsedRealtime();
        post(mAmpRun);
        postInvalidate();
    }

    public void detachedMediaRecorder () {
        removeCallbacks(mAmpRun);
        mRecorder = null;
        mByteArray = null;
    }

    private void reset () {
        refreshByteArray();
        mCursor = 0;
        mLastNewBarTime = 0;
    }

    private void putInt (int amp) {
        putByte((byte)(amp / mAmpUnit));
    }

    private void putByte (byte b) {
        if (mCursor < mByteArray.length - 1) {
            if (b < mMinValue) {
                b = (byte) mMinValue;
            }
            mByteArray[mCursor + 1] = b;
        }
    }

    public void setBarColorResource (@ColorRes int colorRes) {
        setBarColor(getContext().getResources().getColor(colorRes));
    }

    public void setBarColor (int barColor) {
        this.mBarColor = barColor;
        if (this.mPaint != null) {
            this.mPaint.setColor(mBarColor);
        }
    }

    public void setDirection (@Direction int direction) {
        mDirection = direction;
    }

    public void setMaxValue (int maxValue) {
        mMaxValue = maxValue;
        refreshAmpUnit();
    }

    public void setMinValue (int minValue) {
        mMinValue = minValue;
        refreshAmpUnit();
    }

    public void setMaxDuration (int maxDuration) {
        mMaxDuration = maxDuration;
        refreshByteArray();
    }

    public void setPeriod (int period) {
        mPeriod = period;
        refreshByteArray();
    }

    private void refreshByteArray () {
        int length = (int)(mMaxDuration / mPeriod);
        if (mByteArray == null || mByteArray.length != length) {
            mByteArray = new byte[length];
        }
        for (int i = 0; i < mByteArray.length; i++) {
            if (mByteArray[i] <= 0) {
                mByteArray[i] = (byte) mMinValue;
            }
        }
    }

    private void refreshAmpUnit () {
        //int minus =  - mMinValue;
        mAmpUnit = AMP_MAX / mMaxValue;
        mHeightUnit = (float) (getMeasuredHeight() - getPaddingTop() - getPaddingBottom()) / mMaxValue;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
