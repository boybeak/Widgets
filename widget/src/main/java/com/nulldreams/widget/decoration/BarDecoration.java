package com.nulldreams.widget.decoration;

import android.graphics.Canvas;

import com.nulldreams.widget.AmplitudeBarView;

/**
 * Created by gaoyunfei on 2017/5/6.
 */

public interface BarDecoration {
    boolean drawBars (AmplitudeBarView barView, Canvas canvas, int cursor, byte[] data);
}
