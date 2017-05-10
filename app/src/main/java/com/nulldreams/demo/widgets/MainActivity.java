package com.nulldreams.demo.widgets;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.demo.widgets.adapter.IndexDelegate;
import com.nulldreams.demo.widgets.module.Index;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRv;

    private DelegateAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRv = (RecyclerView)findViewById(R.id.main_rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new DelegateAdapter(this);
        mRv.setAdapter(mAdapter);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mAdapter.addAll(DataList.DATA, new DelegateParser<Index>() {
            @Override
            public LayoutImpl parse(DelegateAdapter adapter, Index data) {
                return new IndexDelegate(data);
            }
        });
    }
}
