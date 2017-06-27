package com.nulldreams.demo.widgets;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nulldreams.adapter.DelegateAdapter;
import com.nulldreams.adapter.DelegateParser;
import com.nulldreams.adapter.impl.LayoutImpl;
import com.nulldreams.demo.widgets.adapter.IndexDelegate;
import com.nulldreams.demo.widgets.module.Index;
import com.nulldreams.widget.Chip;

import static com.nulldreams.widget.R.styleable.Chip;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRv;

    private DelegateAdapter mAdapter;

    private boolean canRecord = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRv = (RecyclerView)findViewById(R.id.main_rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new DelegateAdapter(this);
        mRv.setAdapter(mAdapter);

    }

    public void onClick (View view) {
        ((com.nulldreams.widget.Chip)findViewById(R.id.chip)).getImageView().setImageResource(R.mipmap.ic_launcher_round);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        } else {
            fillList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fillList();
        }
    }

    private void fillList () {
        mAdapter.addAll(DataList.DATA, new DelegateParser<Index>() {
            @Override
            public LayoutImpl parse(DelegateAdapter adapter, Index data) {
                return new IndexDelegate(data);
            }
        }).autoNotify();
    }
}
