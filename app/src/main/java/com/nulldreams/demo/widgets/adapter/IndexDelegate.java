package com.nulldreams.demo.widgets.adapter;

import com.nulldreams.adapter.annotation.AnnotationDelegate;
import com.nulldreams.adapter.annotation.DelegateInfo;
import com.nulldreams.demo.widgets.R;
import com.nulldreams.demo.widgets.module.Index;

/**
 * Created by boybe on 2017/5/4.
 */
@DelegateInfo(layoutID = R.layout.layout_index, holderClass = IndexHolder.class)
public class IndexDelegate extends AnnotationDelegate<Index> {

    public IndexDelegate(Index index) {
        super(index);
    }

}
