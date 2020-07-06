package com.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 导航栏控件
 */
public class NavigationBarView extends FrameLayout {

    public NavigationBarView(Context context) {
        super(context);
    }

    public NavigationBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void initAttrs(Context context, AttributeSet attrs){

    }


}
