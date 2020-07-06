package com.android.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Ice on 2015/2/11.
 * RecyclerView Item的分割线
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * Item分割方向
     */
    private int mOrientation;

    /**
     * item之间分割线的size
     */
    private int size;

    /**
     * 绘制item分割线的画笔，和设置其属性
     * 来绘制个性分割线
     */
    private Paint mPaint;


    /**
     * @param orientation
     */
    public DividerItemDecoration(int orientation) {
        this.mOrientation = orientation;
        init(orientation, Color.parseColor("#EBEBEB"), 2);
    }

    /**
     * @param orientation
     * @param color
     */
    public DividerItemDecoration(int orientation, int color) {
        this.mOrientation = orientation;
        init(orientation, color, 2);
    }


    /**
     * @param orientation
     * @param color
     * @param size
     */
    public DividerItemDecoration(int orientation, int color, int size) {
        this.mOrientation = orientation;
        init(orientation, color, size);
    }


    /**
     * @param orientation
     * @param color
     * @param size
     */
    private void init(int orientation, int color, int size) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("请传入正确的参数");
        }
        this.size = size;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(size);
        mPaint.setColor(color);
        /*设置填充*/
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    /**
     * 绘制纵向 item 分割线
     *
     * @param canvas
     * @param parent
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + size;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }

    /**
     * 绘制横向 item 分割线
     *
     * @param canvas
     * @param parent
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight();
            final int right = left + size + layoutParams.rightMargin;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }

    /**
     * 设置item分割线的size
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        int count = state.getItemCount();
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, size);
        } else {
            //如果是Grid不需要绘制每行最后一个的分割线
            if (parent.getLayoutManager() != null && parent.getLayoutManager() instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
                int spanCount = gridLayoutManager.getSpanCount();
                if ((position + 1) % spanCount == 0) {
                    return;
                }
            }
            outRect.set(0, 0, size, 0);
        }
    }
}