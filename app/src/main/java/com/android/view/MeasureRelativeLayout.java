package com.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Relin
 * on 2016/4/29.
 * 经行测量的RelativeLayout
 */
public class MeasureRelativeLayout extends RelativeLayout {

    public MeasureRelativeLayout(Context context) {
        super(context);
    }

    public MeasureRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
