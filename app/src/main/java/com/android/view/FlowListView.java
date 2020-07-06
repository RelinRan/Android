package com.android.view;

/**
 * Created by Ice on 2016/11/29.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.R;
import com.android.utils.Screen;


public class FlowListView extends ViewGroup {

    //数据适配器
    private BaseAdapter baseAdapter;
    //竖向间距
    private int verticalSpacing = (int) Screen.dpToPx(10);
    //横向间距
    private int horizontalSpacing = (int) Screen.dpToPx(10);
    //数据监察者
    private AdapterDataSetObserver adapterDataSerObserver;

    public FlowListView(Context context) {
        super(context);
    }

    public FlowListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public FlowListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    /**
     * 初始化Attrs.xml中的数据
     *
     * @param context
     * @param attrs
     */
    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlowListView);
        horizontalSpacing = array.getDimensionPixelSize(R.styleable.FlowListView_horizontalSpacing,
                horizontalSpacing);
        verticalSpacing = array.getDimensionPixelSize(R.styleable.FlowListView_verticalSpacing, verticalSpacing);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取Padding
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        //FlowLayout最终的宽度和高度值
        int resultWidth = 0;
        int resultHeight = 0;
        //测量时每一行的宽度
        int lineWidth = 0;
        //测量时每一行的高度，加起来就是FlowLayout的高度
        int lineHeight = 0;
        //遍历每个子元素
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);
            //测量每一个子view的宽和高
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            //获取到测量的宽和高
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            //因为子View可能设置margin，这里要加上margin的距离
            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
            int realChildWidth = childWidth + mlp.leftMargin + mlp.rightMargin+horizontalSpacing;
            int realChildHeight = childHeight + mlp.topMargin + mlp.bottomMargin+verticalSpacing;
            //如果当前一行的宽度加上要加入的子view的宽度大于父容器给的宽度，就换行
            if ((lineWidth + realChildWidth) > sizeWidth) {
                //换行
                resultWidth = Math.max(lineWidth, realChildWidth);
                resultHeight += realChildHeight;
                //换行了，lineWidth和lineHeight重新算
                lineWidth = realChildWidth;
                lineHeight = realChildHeight;
            } else {
                //不换行，直接相加
                lineWidth += realChildWidth;
                //每一行的高度取二者最大值
                lineHeight = Math.max(lineHeight, realChildHeight);
            }
            //遍历到最后一个的时候，肯定走的是不换行
            if (i == childCount - 1) {
                resultWidth = Math.max(lineWidth, resultWidth);
                resultHeight += lineHeight;
            }
        }
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : resultWidth, modeHeight ==
                MeasureSpec.EXACTLY ? sizeHeight : resultHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int flowWidth = getWidth();
        int childLeft = 0;
        int childTop = 0;
        //遍历子控件，记录每个子view的位置
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);
            //跳过View.GONE的子View
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            //获取到测量的宽和高
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            //因为子View可能设置margin，这里要加上margin的距离
            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
            mlp.rightMargin = horizontalSpacing;
            mlp.bottomMargin = verticalSpacing;
            if (childLeft + mlp.leftMargin + childWidth + mlp.rightMargin > flowWidth) {
                //换行处理
                childTop += (mlp.topMargin + childHeight + mlp.bottomMargin);
                childLeft = 0;
            }
            //布局
            int left = childLeft + mlp.leftMargin;
            int top = childTop + mlp.topMargin;
            int right = childLeft + mlp.leftMargin + childWidth;
            int bottom = childTop + mlp.topMargin + childHeight;
            childView.layout(left, top, right, bottom);
            childLeft += (mlp.leftMargin + childWidth + mlp.rightMargin);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 重新加载刷新数据
     */
    private void notifyDataRefresh() {
        removeAllViews();
        for (int i = 0; i < baseAdapter.getCount(); i++) {
            final View childView = baseAdapter.getView(i, null, this);
            addView(childView, new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            if (onItemClickListener != null) {//Item点击事件
                final int position = i;
                childView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(baseAdapter, childView, position);
                    }
                });
            }
        }
    }

    /**
     * Adapter数据监察者
     */
    public class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataRefresh();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }


    /**
     * 像ListView、GridView一样使用FlowLayout
     *
     * @param adapter
     */
    public void setAdapter(BaseAdapter adapter) {
        if (baseAdapter != null && adapterDataSerObserver != null) {
            baseAdapter.unregisterDataSetObserver(adapterDataSerObserver);
        }
        //清除现有的数据
        removeAllViews();
        baseAdapter = adapter;
        if (baseAdapter != null) {
            adapterDataSerObserver = new AdapterDataSetObserver();
            baseAdapter.registerDataSetObserver(adapterDataSerObserver);
        }
    }


    //Item 点击时间回调函数

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(BaseAdapter adapter, View view, int position);
    }

}


