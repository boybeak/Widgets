package com.nulldreams.widget.decoration;

import android.graphics.Canvas;
import android.os.SystemClock;

import com.nulldreams.widget.AmplitudeBarView;

import static com.nulldreams.widget.AmplitudeBarView.DIRECTION_LEFT_TO_RIGHT;
import static com.nulldreams.widget.AmplitudeBarView.DIRECTION_RIGHT_TO_LEFT;

/**
 * Created by gaoyunfei on 2017/5/7.
 */

public class DefaultDecoration implements BarDecoration {



    @Override
    public boolean drawBars(AmplitudeBarView barView, Canvas canvas, int cursor, byte[] data) {

        return barView.isAttachedWithRecorder();
    }

    /*private void drawFromLeftToRight (Canvas canvas, float deltaX, long deltaTime) {
        int offset = 0;
        for (int i = mCursor; i > mCursor - mDrawBarBufSize && i > 0; i--) {
            float left = getPaddingLeft() + (mGapWidth + mBarWidth) * offset + mGapWidth + deltaX;
            float right = left + mBarWidth;
            float maxRight = getWidth() - getPaddingRight();
            if (right > maxRight) {
                right = maxRight;
            }
            float bottom = getHeight() - getPaddingBottom();
            if (offset == 0) {
                float full = mByteArray[i] * mHeightUnit;
                float remain = full * ((float)deltaTime / getPeriod()) * 3;
                if (remain > full) {
                    remain = full;
                }
                canvas.drawRect(left, bottom - remain, right, bottom, mPaint);
            } else {
                canvas.drawRect(left, bottom - (mByteArray[i] * mHeightUnit), right, bottom, mPaint);
            }
            if (debug) {
                float b = (getHeight() - getPaddingBottom() - getPaddingTop()) / 2;
                canvas.drawText(i + "", left, b - 10, mTextPaint);
                canvas.drawText(mByteArray[i] + "", left, b + 10, mTextPaint);
            }
            offset++;
        }
    }

    private void drawFromRightToLeft (Canvas canvas, float deltaX, long deltaTime) {
        int offset = 0;
        for (int i = mCursor; i > mCursor - mDrawBarBufSize && i > 0; i--) {
            float right = getWidth() - getPaddingRight() - (mGapWidth + mBarWidth) * offset - mGapWidth - deltaX;
            float left = right - mBarWidth;
            float maxLeft = getPaddingLeft();
            if (left < maxLeft) {
                left = maxLeft;
            }
            float bottom = getHeight() - getPaddingBottom();
            if (offset == 0) {
                float full = mByteArray[i] * mHeightUnit;
                float remain = full * ((float)deltaTime / getPeriod()) * 3;
                if (remain > full) {
                    remain = full;
                }
                canvas.drawRect(left, bottom - remain, right, bottom, mPaint);
            } else {
                canvas.drawRect(left, bottom - (mByteArray[i] * mHeightUnit), right, bottom, mPaint);
            }
            if (debug) {
                float b = (getHeight() - getPaddingBottom() - getPaddingTop()) / 2;
                canvas.drawText(i + "", left, b - 10, mTextPaint);
                canvas.drawText(mByteArray[i] + "", left, b + 10, mTextPaint);
            }
            offset++;
        }
    }*/
}
