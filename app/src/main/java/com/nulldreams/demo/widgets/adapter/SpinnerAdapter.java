package com.nulldreams.demo.widgets.adapter;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nulldreams.demo.widgets.DataList;
import com.nulldreams.demo.widgets.R;
import com.nulldreams.demo.widgets.module.Index;

/**
 * Created by gaoyunfei on 2017/5/17.
 */

public class SpinnerAdapter extends BaseAdapter {
    @Override
    public int getCount() {
        return DataList.DATA.length;
    }

    @Override
    public Index getItem(int position) {
        return DataList.DATA[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Index index = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_index, null);
        }
        AppCompatTextView titleTv = (AppCompatTextView)convertView.findViewById(R.id.index_name);
        AppCompatImageView iconIv = (AppCompatImageView)convertView.findViewById(R.id.index_icon);
        titleTv.setText(index.getTitle());
        iconIv.setImageResource(index.getIcon());
        return convertView;
    }
}
