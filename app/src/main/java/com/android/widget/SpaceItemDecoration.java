package com.android.widget;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by Ice on 2016/5/18.
 * RecycleView Item 间距类
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 间隔
     */
    private int space;

    /**
     * 方向
     */
    private int orientation;


    /**
     * 构造函数
     *
     * @param orientation
     * @param space
     */
    public SpaceItemDecoration(int orientation, int space) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("请传入正确的参数");
        }
        this.orientation = orientation;
        this.space = space;

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemCount = parent.getAdapter().getItemCount();
        int itemPosition = parent.getChildLayoutPosition(view);
        int lastItemIndex = itemCount == 0 ? 0 : (itemCount - 1);
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager == null) {
            throw new IllegalArgumentException("SpaceItemDecoration need to set after setLayoutManager()");
        }
        if (manager.getClass() == LinearLayoutManager.class) {
            if (orientation == LinearLayoutManager.VERTICAL) {
                if (itemPosition != lastItemIndex) {
                    outRect.bottom = space;
                }
            }
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                if (itemPosition != lastItemIndex) {
                    outRect.right = space;
                }
            }
        }
        if (manager.getClass() == GridLayoutManager.class) {
            int spanCount = ((GridLayoutManager) manager).getSpanCount();
            if (orientation == LinearLayoutManager.VERTICAL) {
                if (itemPosition > spanCount - 1) {
                    outRect.top = space;
                }
            }
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                if (itemPosition % spanCount != (spanCount - 1)) {
                    outRect.right = space;
                }
            }
        }
        if (manager.getClass() == StaggeredGridLayoutManager.class) {
            outRect.left = space / 2;
            outRect.right = space / 2;
            outRect.bottom = space;
            if (itemPosition == 0) {
                outRect.top = space;
            }
        }
    }
}
