package com.nulldreams.demo.widgets.module;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by boybe on 2017/5/4.
 */

public class Index {

    private int icon, title;

    public Index (@DrawableRes int iconRes, @StringRes int stringRes) {
        icon = iconRes;
        title = stringRes;
    }

    public int getIcon() {
        return icon;
    }

    public int getTitle() {
        return title;
    }
}
