package com.nulldreams.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by gaoyunfei on 2017/6/27.
 */

public class FeatureDiscoveryView extends ViewGroup {

    private static final String TAG = FeatureDiscoveryView.class.getSimpleName();

    private int mTargetX, mTargetY, mTargetViewWidth, mTargetViewHeight;

    private Paint mPaint;


    private FrameLayout mRootLayout, mContentView;

    private PorterDuffXfermode mClearMode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    public FeatureDiscoveryView(Context context) {
        super(context);

        Log.v(TAG, "this.getRootView()=" + this.getRootView().getRootView());

        Activity activity = (Activity)getContext();
        mContentView = (FrameLayout)activity.findViewById(android.R.id.content);

        this.setBackgroundColor(0xffffff);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(WidgetUtils.fetchPrimaryColor(context));

    }

    /*public FeatureDiscoveryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeatureDiscoveryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }*/

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void attachTo (View target) {
        View rootView = target.getRootView();
        if (rootView instanceof FrameLayout) {
            mRootLayout = (FrameLayout)rootView;
        } else {
            throw new UnsupportedOperationException("FeatureDiscoveryView can not work for this device because the DecorView is not a sub class of FrameLayout");
        }
        Log.v(TAG, "rootView.class=" + target.getRootView().getClass().getName());
        Rect rect = new Rect();
        Point point = new Point();
        mRootLayout.getChildVisibleRect(target, rect, point);
        mTargetX = getRelativeLeft(target);
        mTargetY = getRelativeTop(target);
        mTargetViewWidth = target.getWidth();
        mTargetViewHeight = target.getHeight();
        Log.v(TAG, "attachTo mTargetX=" + mTargetX + " mTargetY=" + mTargetY + " mTargetViewWidth=" + mTargetViewWidth + " mTargetViewHeight=" + mTargetViewHeight);
        mRootLayout.addView(this);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mRootLayout.removeView(FeatureDiscoveryView.this);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mTargetViewWidth * mTargetViewHeight > 0) {
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);

            mPaint.setXfermode(mClearMode);
            mPaint.setColor(Color.TRANSPARENT);
            canvas.drawRect(mTargetX, mTargetY, mTargetX + mTargetViewWidth, mTargetY + mTargetViewHeight, mPaint);
        }
        super.onDraw(canvas);
    }

    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    /*private boolean isInCircleArea () {
        
    }*/

    /*private boolean shouldUseSameCenter (View target) {

    }*/
}
