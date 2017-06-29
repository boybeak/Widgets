package com.nulldreams.demo.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by gaoyunfei on 2017/6/28.
 */

public class DebugView extends View {

    private Paint mPaint;

    private PorterDuffXfermode mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    private Bitmap bmp = null;

    public DebugView(Context context) {
        this(context, null);
    }

    public DebugView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DebugView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(Color.CYAN);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPaint(mPaint);
        mPaint.setXfermode(mClearMode);
        mPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(0, 0, 120, 120, mPaint);
    }
}
