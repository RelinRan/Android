package com.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Relin
 * on 2016/4/29.
 */
public class MeasureListView extends ListView {
    public MeasureListView(Context context) {
        super(context);
    }

    public MeasureListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
