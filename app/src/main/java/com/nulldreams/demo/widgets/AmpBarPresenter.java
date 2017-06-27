package com.nulldreams.demo.widgets;

import android.support.annotation.IdRes;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.nulldreams.widget.media.AmplitudeBarView;

/**
 * Created by gaoyunfei on 2017/5/18.
 */

public class AmpBarPresenter implements AmpPresenter {

    private AmplitudeBarView mBarView;
    private View mPrefView;

    private AppCompatCheckBox mDebugCb;
    private RadioGroup mRg;

    private RadioGroup.OnCheckedChangeListener mCheckListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId) {
                case R.id.bar_left_to_right:
                    mBarView.setDirection(AmplitudeBarView.DIRECTION_LEFT_TO_RIGHT);
                    break;
                case R.id.bar_right_to_left:
                    mBarView.setDirection(AmplitudeBarView.DIRECTION_RIGHT_TO_LEFT);
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener mDebugListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mBarView.setDebug(isChecked);
        }
    };

    public AmpBarPresenter (AmplitudeBarView barView, View prefView) {
        mBarView = barView;
        mPrefView = prefView;

        mDebugCb = (AppCompatCheckBox) mPrefView.findViewById(R.id.pref_debug_cb);
        mRg = (RadioGroup) mPrefView.findViewById(R.id.pref_rg);

        mRg.setOnCheckedChangeListener(mCheckListener);
        mDebugCb.setOnCheckedChangeListener(mDebugListener);

    }

}
