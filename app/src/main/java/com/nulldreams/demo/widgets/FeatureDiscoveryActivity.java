package com.nulldreams.demo.widgets;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.nulldreams.widget.FeatureDiscoveryView;

public class FeatureDiscoveryActivity extends AppCompatActivity {

    private FloatingActionButton mFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_discovery);

        mFAB = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FeatureDiscoveryView(v.getContext()).attachTo(v);
            }
        });
    }
}
