package com.nulldreams.widget.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.view.View;

import com.nulldreams.widget.R;
import com.nulldreams.widget.WidgetUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by gaoyunfei on 2017/6/23.
 */

public class MiniMonthView extends View {

    private static final String TAG = MiniMonthView.class.getSimpleName();

    private static final long DAY_LONG = 1000 * 60 * 60 * 24;

    private static final DecimalFormat DATE_FORMAT = new DecimalFormat("00");
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM", Locale.getDefault());

    private static final String[] WEEKS = {"S", "M", "T", "W", "T", "F", "S"};

    private String[] mDateStrArray = new String[42];
    private int mFirstDayIndex, mDayCount;

    private Paint mPaint;
    private Calendar mCal, mToday;

    private int mTitleHeight = 40, mWeekBarHeight = 32,
            mCellStartX, mCellStartY, mCellSize = 36, mGapWidth = 8,
            mGridWidth, mGridHeight, mTodayColor;

    private int mRectColor = Color.argb(20, 20, 20, 20), mTitleTextColor, mWeekTextColor,
            mWeekendTextColor, mCellTextColorPrimary = Color.BLACK, mCellTextColorSecondary = Color.LTGRAY;

    private boolean showOtherMonth = false;

    private Rect mRect = new Rect();

    private boolean isAttachedToWindow = false;

    public MiniMonthView(Context context) {
        this(context, null);
    }

    public MiniMonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiniMonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MiniMonthView);


        try {
            setTitleTextSize(array.getDimensionPixelSize(R.styleable.MiniMonthView_titleTextSize, mTitleHeight));
            setTitleTextColor(array.getColor(R.styleable.MiniMonthView_titleTextColor, Color.DKGRAY));

            setWeekTextColor(array.getColor(R.styleable.MiniMonthView_weekTextColor, Color.DKGRAY));
            setWeekBarHeight(array.getDimensionPixelSize(R.styleable.MiniMonthView_weekBarHeight, mWeekBarHeight));

            setCellSize(array.getDimensionPixelSize(R.styleable.MiniMonthView_cellSize, mCellSize));
            setThisMonthColor(array.getColor(R.styleable.MiniMonthView_thisMonthTextColor, Color.BLACK));
            setWeekendTextColor(array.getColor(R.styleable.MiniMonthView_weekendTextColor, mCellTextColorPrimary));
            setOtherMonthColor(array.getColor(R.styleable.MiniMonthView_otherMonthTextColor, Color.LTGRAY));

            setGapWidth(array.getDimensionPixelSize(R.styleable.MiniMonthView_gapWidth, mGapWidth));
            setShowOtherMonth(array.getBoolean(R.styleable.MiniMonthView_showOtherMonth, false));

            setTodayColor(array.getColor(R.styleable.MiniMonthView_todayTextColor, WidgetUtils.fetchAccentColor(context)));
        } finally {
            array.recycle();
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
//        mPaint.setStyle(Paint.Style.STROKE);

        mToday = Calendar.getInstance();

        mCal = Calendar.getInstance();
        mCal.set(Calendar.DAY_OF_MONTH, 1);
        calculateDateArray();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int cellSizeW = mCellSize, cellSizeH = mCellSize;
        if (widthMode == MeasureSpec.EXACTLY) {
            cellSizeW = (width - getPaddingLeft() - getPaddingRight() - 8 * mGapWidth) / 7;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            cellSizeH = (height - getPaddingTop() - getPaddingBottom() - mTitleHeight - mWeekBarHeight - 7 * mGapWidth) / 6;
        }

        mCellSize = Math.min(cellSizeW, cellSizeH);

        mGridWidth = 7 * mCellSize + 8 * mGapWidth;
        mGridHeight = 6 * mCellSize + 8 * mGapWidth + mTitleHeight + mWeekBarHeight;

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            width = mGridWidth + getPaddingLeft() + getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            height = mGridHeight + getPaddingTop() + getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mCellStartX = (getMeasuredWidth() + getPaddingLeft() - mGridWidth - getPaddingRight()) / 2 + mGapWidth;
        mCellStartY = (getMeasuredHeight() + getPaddingTop() - mGridHeight - getPaddingBottom()) / 2 + mTitleHeight + mWeekBarHeight + mGapWidth;

    }

    /*private void refreshStartPosition () {
        switch (mGravity) {
            case Gravity.CENTER:

                break;
        }
    }

    private void refreshStartPositionX () {

    }*/

    @Override
    protected void onDraw(Canvas canvas) {

        float left = mCellStartX, top = getPaddingTop(), right = left + mCellSize * 7 + mGapWidth * 6, bottom = top + mTitleHeight;

//        canvas.drawRect(left, top, right, bottom, mPaint);
        mPaint.setTextSize(mTitleHeight);
        mPaint.setColor(mTitleTextColor);
        drawTextInCenter(canvas, left, top, right, bottom, MONTH_FORMAT.format(mCal.getTime()));

        mPaint.setTextSize(Math.min(mWeekBarHeight, mCellSize) / 3 * 2);
        mPaint.setColor(mWeekTextColor);
        for (int i = 0; i < WEEKS.length; i++) {
            left = mCellStartX + i * (mCellSize + mGapWidth);
            right = left + mCellSize;
            top = getPaddingBottom() + mTitleHeight + mGapWidth;
            bottom = top + mWeekBarHeight;

            mPaint.setColor(mWeekTextColor);
            drawTextInCenter(canvas, left, top, right, bottom, WEEKS[i]);

            mPaint.setColor(mRectColor);
            canvas.drawRect(left, top, right, bottom, mPaint);
        }

        mPaint.setTextSize(mCellSize / 3 * 2);

        for (int i = 0; i < mDateStrArray.length; i++) {

            int column = i % 7;
            int row = i / 7;

            left = mCellStartX + column * (mCellSize + mGapWidth);
            right = left + mCellSize;
            top = mCellStartY + row * (mCellSize + mGapWidth) + mGapWidth;
            bottom = top + mCellSize;

            if (inThisMonth(i)) {
                if (isToday(i)) {
                    mPaint.setColor(mTodayColor);
                    /*float radius = mCellSize * 1f / 2;
                    float cx = left + radius;
                    float cy = top + radius;
                    canvas.drawCircle(cx, cy, radius, mPaint);*/
                } else {
                    if (column == 0 || column == 6) {
                        mPaint.setColor(mWeekendTextColor);
                    } else {
                        mPaint.setColor(mCellTextColorPrimary);
                    }

                }
            } else {
                if (!showOtherMonth) {
                    continue;
                }
                mPaint.setColor(mCellTextColorSecondary);
            }
            if (!shouldShowLastRow() && i >= 35) {
                break;
            }

            drawTextInCenter(canvas, left, top, right, bottom, mDateStrArray[i]);

        }

    }

    private void drawTextInCenter (Canvas canvas, float left, float top, float right, float bottom, String text) {
        mPaint.getTextBounds(text, 0, text.length(), mRect);
        int w = mRect.right - mRect.left;
        int h = mRect.bottom - mRect.top;
        float x = (left + right - w) / 2;
        float y = (top + bottom + h) / 2;
        canvas.drawText(text, x, y, mPaint);
    }

    public void setYearAndMonth (int year, int month) {
        mCal.set(Calendar.YEAR, year);
        mCal.set(Calendar.MONTH, month);
        calculateDateArray();
    }

    public int getYear() {
        return mCal.get(Calendar.YEAR);
    }

    public void setYear(int year) {
        mCal.set(Calendar.YEAR, year);
        calculateDateArray();
    }

    public int getMonth() {
        return mCal.get(Calendar.MONTH);

    }

    public void setMonth(int month) {
        mCal.set(Calendar.MONTH, month);
        calculateDateArray();
    }

    private void calculateDateArray() {
        mFirstDayIndex = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        mDayCount = mCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        long firstDayTime = mCal.getTimeInMillis();

        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < mDateStrArray.length; i++) {
            calendar.setTimeInMillis((i - mFirstDayIndex) * DAY_LONG + firstDayTime);
            mDateStrArray[i] = DATE_FORMAT.format(calendar.get(Calendar.DAY_OF_MONTH));
        }
        invalidate();
    }

    private boolean inThisMonth (int index) {
        return index >= mFirstDayIndex && index < getLastDayIndex();
    }

    private int getLastDayIndex () {
        return mFirstDayIndex + mDayCount;
    }

    private boolean shouldShowLastRow () {
        return getLastDayIndex() >= 35;
    }

    private boolean isToday (int index) {

        int y = mCal.get(Calendar.YEAR);
        int m = mCal.get(Calendar.MONTH);
        //int d = mCal.get(Calendar.DAY_OF_MONTH);

        int ty = mToday.get(Calendar.YEAR);
        int tm = mToday.get(Calendar.MONTH);
        int td = mToday.get(Calendar.DAY_OF_MONTH) - 1;

        /*if (y == 2017 && m == Calendar.JUNE) {
            Log.v(TAG, "isToday d=" + d + " td=" + td + " index=" + index);
        }*/

        return y == ty && m == tm && index - mFirstDayIndex == td;
    }

    private void setTitleTextSize (@Px int textSize) {
        this.mTitleHeight = textSize;
    }

    private void setTitleTextColor (int color) {
        this.mTitleTextColor = color;
    }

    private void setWeekTextColor (int weekTextColor) {
        this.mWeekTextColor = weekTextColor;
    }

    private void setWeekBarHeight (int weekBarHeight) {
        this.mWeekBarHeight = weekBarHeight;
    }

    private void setCellSize (int cellSize) {
        mCellSize = cellSize;
    }

    private void setWeekendTextColor (int weekendTextColor) {
        if (mWeekendTextColor == weekendTextColor) {
            return;
        }
        mWeekendTextColor = weekendTextColor;
        if (isAttachedToWindow) {
            invalidate();
        }
    }

    private void setThisMonthColor (int thisMonthColor) {
        if (mCellTextColorPrimary == thisMonthColor) {
            return;
        }
        mCellTextColorPrimary = thisMonthColor;
        if (isAttachedToWindow) {
            invalidate();
        }
    }

    private void setOtherMonthColor (int otherMonthColor) {
        if (mCellTextColorSecondary == otherMonthColor) {
            return;
        }
        mCellTextColorSecondary = otherMonthColor;
        if (isAttachedToWindow) {
            invalidate();
        }
    }

    private void setGapWidth (int gapWidth) {
        mGapWidth = gapWidth;
    }

    private void setShowOtherMonth (boolean showOtherMonth) {
        this.showOtherMonth = showOtherMonth;
    }

    private void setTodayColor (int todayColor) {
        if (mTodayColor == todayColor) {
            return;
        }
        mTodayColor = todayColor;
        if (isAttachedToWindow) {
            invalidate();
        }
    }
}
