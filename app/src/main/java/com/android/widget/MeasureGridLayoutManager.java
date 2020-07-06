package com.android.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MeasureGridLayoutManager extends GridLayoutManager {

    private int spaceVerticalSize = 0;
    private int dividerVerticalSize = 0;

    public MeasureGridLayoutManager(Context context, int spanCount, int spaceVerticalSize, int dividerVerticalSize) {
        super(context, spanCount);
        if (spaceVerticalSize != 0 && dividerVerticalSize != 0) {
            Log.e(this.getClass().getSimpleName(), "spaceSize and dividerSize not both of is not zero!");
        } else {
            spaceVerticalSize = getOrientation() == HORIZONTAL ? -2 : spaceVerticalSize;
            this.spaceVerticalSize = spaceVerticalSize;
            dividerVerticalSize = getOrientation() == HORIZONTAL ? -2 : (dividerVerticalSize == 0 ? 0 : dividerVerticalSize + 2);
            this.dividerVerticalSize = dividerVerticalSize;
        }
    }

    public MeasureGridLayoutManager(Context context, int spanCount, int orientation, int spaceVerticalSize, int dividerVerticalSize, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
        if (spaceVerticalSize != 0 && dividerVerticalSize != 0) {
            Log.e(this.getClass().getSimpleName(), "spaceSize and dividerSize not both of is not zero!");
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
        int count = getItemCount();
        int span = getSpanCount();
        for (int i = 0; i < count; i++) {
            measureScrapChild(recycler, i, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), mMeasuredDimension);
            if (getOrientation() == HORIZONTAL) {
                if (i % span == 0) {
                    width = width + mMeasuredDimension[0];
                }
                if (i == 0) {
                    height = mMeasuredDimension[1];
                }
            } else {
                if (i % span == 0) {
                    height = height + mMeasuredDimension[1];
                }
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
        if (position < getItemCount()) {
            try {
                View view = recycler.getViewForPosition(0);//fix 动态添加时报IndexOutOfBoundsException
                if (view != null) {
                    RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
                    int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), p.width);
                    int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), p.height);
                    view.measure(childWidthSpec, childHeightSpec);
                    measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin + spaceVerticalSize;
                    //实现Item为1的Vertical的列表
                    if (getOrientation() == LinearLayout.VERTICAL && getSpanCount() == 1) {
                        if (spaceVerticalSize != 0) {
                            spaceVerticalSize = ((position == (getChildCount() - 1)) ? 0 : spaceVerticalSize);
                            measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin + spaceVerticalSize;
                        }
                        if (dividerVerticalSize != 0) {
                            dividerVerticalSize = ((position == (getChildCount() - 1)) ? 0 : dividerVerticalSize);
                            measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin + dividerVerticalSize;
                        }
                    } else if (getOrientation() == LinearLayout.VERTICAL && getSpanCount() != 1) {
                        //实现Item为>1的Horizontal的列表
                        measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin + isHaveLastClumnItemHorizontalLine(position, false);
                    } else {
                        if (spaceVerticalSize != 0) {
                            spaceVerticalSize = ((position == (getChildCount() - 1)) ? 0 : spaceVerticalSize);
                            measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin + spaceVerticalSize;
                        }
                        if (dividerVerticalSize != 0) {
                            dividerVerticalSize = ((position == (getChildCount() - 1)) ? 0 : dividerVerticalSize);
                            measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin + dividerVerticalSize;
                        }
                    }
                    recycler.recycleView(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否显示最后一排的Item
     *
     * @param position
     * @return
     */
    private int isHaveLastClumnItemHorizontalLine(int position, boolean isHave) {
        int lastClumnCount;
        int lastItemPosition = getItemCount() - 1;
        if (getItemCount() % getSpanCount() != 0) {
//          clumn = getItemCount() / getSpanCount() + 1;
            lastClumnCount = getItemCount() % getSpanCount();
        } else {
            lastClumnCount = getSpanCount();
//            clumn = getItemCount() / getSpanCount();
        }
        int result = 0;
        if (isHave) {
            if (spaceVerticalSize != 0) {
                result = spaceVerticalSize + 2;
            }
            if (dividerVerticalSize!=0){
                result = dividerVerticalSize;
            }
        } else {
            for (int i = 0; i < lastClumnCount; i++) {
                if (position != (lastItemPosition - i)) {
                    if (spaceVerticalSize != 0) {
                        result = spaceVerticalSize + 2;
                    }
                    if (dividerVerticalSize!=0){
                        result = dividerVerticalSize;
                    }
                } else {
                    result = 0;
                }
            }
        }
        return result;
    }

}