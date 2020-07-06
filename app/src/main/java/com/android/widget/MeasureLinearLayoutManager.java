package com.android.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Relin
 * on 2016/5/18.
 */

public class MeasureLinearLayoutManager extends LinearLayoutManager {

    private static final String TAG = MeasureLinearLayoutManager.class.getSimpleName();

    private int spaceVerticalSize = 0;
    private int dividerVerticalSize = 0;

    public MeasureLinearLayoutManager(Context context, int spaceVerticalSize, int dividerVerticalSize) {
        super(context);
        if (spaceVerticalSize != 0 && dividerVerticalSize != 0) {
            Log.e(this.getClass().getSimpleName(), "spaceVerticalSize and dividerVerticalSize not both of is not zero!");
        } else {
            spaceVerticalSize = getOrientation() == HORIZONTAL ? -2 : spaceVerticalSize;
            this.spaceVerticalSize = spaceVerticalSize;
            dividerVerticalSize = getOrientation() == HORIZONTAL ? -2 : dividerVerticalSize + 2;
            this.dividerVerticalSize = dividerVerticalSize;
        }
    }

    public MeasureLinearLayoutManager(Context context, int orientation, int spaceVerticalSize, int dividerVerticalSize, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        if (spaceVerticalSize != 0 && dividerVerticalSize != 0) {
            Log.e(this.getClass().getSimpleName(), "spaceVerticalSize and dividerVerticalSize not both of is not zero!");
        } else {
            spaceVerticalSize = getOrientation() == HORIZONTAL ? -2 : spaceVerticalSize;
            this.spaceVerticalSize = spaceVerticalSize;
            dividerVerticalSize = getOrientation() == HORIZONTAL ? -2 : (dividerVerticalSize == 0 ? 0 : dividerVerticalSize + 2);
            this.dividerVerticalSize = dividerVerticalSize;
        }
    }

    private int[] mMeasuredDimension = new int[2];

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);
        int width = 0;
        int height = 0;
        for (int i = 0; i < getItemCount(); i++) {
            measureScrapChild(recycler, i, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), mMeasuredDimension);
            if (getOrientation() == HORIZONTAL) {
                width = width + mMeasuredDimension[0];
                if (i == 0) {
                    height = mMeasuredDimension[1];
                }
            } else {
                height = height + mMeasuredDimension[1];
                if (i == 0) {
                    width = mMeasuredDimension[0];
                }
            }
        }
        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
                width = widthSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        setMeasuredDimension(width, height);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
        try {
            View view = recycler.getViewForPosition(0);//fix 动态添加时报IndexOutOfBoundsException
            if (view != null) {
                RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
                int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), p.width);
                int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), p.height);
                view.measure(childWidthSpec, childHeightSpec);
                measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                if (spaceVerticalSize != 0) {
                    spaceVerticalSize = ((position == (getChildCount() - 1)) ? 0 : spaceVerticalSize);
                    measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin + spaceVerticalSize;
                }
                if (dividerVerticalSize != 0) {
                    dividerVerticalSize = ((position == (getChildCount() - 1)) ? 0 : dividerVerticalSize);
                    measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin + dividerVerticalSize;
                }
                recycler.recycleView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
