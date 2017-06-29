package com.nulldreams.demo.widgets.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.nulldreams.adapter.AbsViewHolder;
import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.demo.widgets.AmplitudeActivity;
import com.nulldreams.demo.widgets.FeatureDiscoveryActivity;
import com.nulldreams.demo.widgets.R;
import com.nulldreams.demo.widgets.module.Index;

/**
 * Created by boybe on 2017/5/4.
 */

public class IndexHolder extends AbsViewHolder<IndexDelegate> {

    private AppCompatImageView iconIv;
    private AppCompatTextView titleTv;

    public IndexHolder(View itemView) {
        super(itemView);

        iconIv = (AppCompatImageView)findViewById(R.id.index_icon);
        titleTv = (AppCompatTextView)findViewById(R.id.index_name);
    }

    @Override
    public void onBindView(final Context context, IndexDelegate indexDelegate, int position, DelegateAdapter adapter) {
        final Index index = indexDelegate.getSource();
        iconIv.setImageResource(index.getIcon());
        titleTv.setText(index.getTitle());

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class clz = null;
                switch (index.getIcon()) {
                    case R.drawable.ic_chart_bar:
                    case R.drawable.ic_chart_line:
                        clz = AmplitudeActivity.class;
                        break;
                    case R.drawable.ic_disk:
                        clz = FeatureDiscoveryActivity.class;
                        break;
                }
                if (clz != null) {
                    context.startActivity(new Intent(context, clz));
                }
            }
        });
    }
}
