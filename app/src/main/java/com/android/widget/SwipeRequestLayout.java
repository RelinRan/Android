package com.android.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.android.R;
import com.android.utils.Log;
import com.android.view.MeasureListView;
import com.android.view.SwipeRequestView;

/**
 * Created by Relin
 * on 2018-09-26.
 */
public class SwipeRequestLayout extends FrameLayout {

    public static boolean DEBUG = false;
    public String TAG = "SwipeRequestLayout";
    //头部视图
    private SwipeRequestView headerView;
    //内容视图
    private View contentView[];
    //底部视图
    private SwipeRequestView footerView;
    //头部高度
    private float headerHeight = dpToPx(80);
    //脚部高度
    private float footerHeight = dpToPx(80);
    //刷新是否可用
    private boolean refreshEnable = true;
    //加载是否可用
    private boolean loadEnable = false;
    //是否超限距离
    private boolean isTransfinite = false;
    //是否正在刷新
    private boolean isRefreshing = false;
    private boolean isRefreshingRelease = true;
    //是否正在加载
    private boolean isLoading = false;
    private boolean isLoadingRelease = true;
    //按下的坐标
    private float downX, downY;
    //刷新移动距离
    private float refreshMoveY = 0;
    //加载移动距离
    private float loadMoveY = 0;
    //刷新停留距离
    private float refreshRemainY = 0;
    //加载停留距离
    private float loadRemainY = 0;
    //刷新监听
    private OnSwipeRefreshListener refreshListener;
    //加载监听
    private OnSwipeLoadListener loadListener;
    //缩放动画师
    private ValueAnimator animator;
    //缩放时间
    private int scaleDuration = 500;
    //延迟时间
    private int delayDuration = 200;
    //头部阴影
    private int headerShadowColor;
    //脚部阴影
    private int footerShadowColor;
    //内容类型-列表
    private AbsListView absListView;
    //内容类型-ScrollView
    private ScrollView scrollView;
    //内容类型-RecyclerView
    private RecyclerView recyclerView;
    //扇形组合颜色
    private int arcSchemeColors[] = {
            Color.parseColor("#20A5F7"),
            Color.parseColor("#EA4335"),
            Color.parseColor("#34A853"),
            Color.parseColor("#FBBC05")};

    private NestedScrollView nestedScrollView;

    private View emptyView;

    public SwipeRequestLayout(@NonNull Context context) {
        super(context);
        initHeaderFooter(context, null);
    }

    public SwipeRequestLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initHeaderFooter(context, attrs);
    }

    public SwipeRequestLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderFooter(context, attrs);
    }

    /**
     * 初始话头部和底部View
     *
     * @param context
     */
    private void initHeaderFooter(Context context, AttributeSet attrs) {
        headerView = new SwipeRequestView(context);
        footerView = new SwipeRequestView(context);
        headerShadowColor = headerView.getShadowColor();
        footerShadowColor = footerView.getShadowColor();
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeRequestLayout);
            headerHeight = typedArray.getDimension(R.styleable.SwipeRequestLayout_headerHeight, headerHeight);
            footerHeight = typedArray.getDimension(R.styleable.SwipeRequestLayout_footerHeight, footerHeight);
            refreshEnable = typedArray.getBoolean(R.styleable.SwipeRequestLayout_refreshable, refreshEnable);
            loadEnable = typedArray.getBoolean(R.styleable.SwipeRequestLayout_loadable, loadEnable);
            scaleDuration = typedArray.getInt(R.styleable.SwipeRequestLayout_scaleDuration, scaleDuration);
            delayDuration = typedArray.getInt(R.styleable.SwipeRequestLayout_delayDuration, delayDuration);
            headerShadowColor = footerShadowColor = typedArray.getColor(R.styleable.SwipeRequestLayout_shadowColor, headerShadowColor);
            attrsSchemeColors(typedArray);
            typedArray.recycle();
        }

    }

    /**
     * Attrs组合颜色值
     *
     * @param typedArray
     */
    private void attrsSchemeColors(TypedArray typedArray) {
        String schemeColors = typedArray.getString(R.styleable.SwipeRequestLayout_arcSchemeColors);
        if (!TextUtils.isEmpty(schemeColors) && schemeColors.contains(",")) {
            String split[] = schemeColors.split(",");
            arcSchemeColors = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                arcSchemeColors[i] = Color.parseColor(split[i]);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //内容
        contentView = new View[getChildCount()];
        checkViewGroup(this, true);
        if (absListView != null && absListView.getVisibility() == VISIBLE) {
            if (absListView instanceof MeasureListView) {
                absListView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
            }
            absListView.setOnScrollListener(new AbsListViewOnScrollListener());
        }
        if (recyclerView != null) {
            if (scrollView != null && scrollView.getVisibility() == VISIBLE) {
                scrollView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
            }
            if (nestedScrollView != null && nestedScrollView.getVisibility() == VISIBLE) {
                nestedScrollView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
                recyclerView.setNestedScrollingEnabled(false);
            }
            recyclerView.addOnScrollListener(new RecyclerViewOnScrollListener());
        }
        //头部
        LayoutParams headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) headerHeight);
        headerView.setLayoutParams(headerParams);
        headerView.setBackgroundColor(Color.TRANSPARENT);
        addView(headerView);
        //脚部
        LayoutParams footerParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) footerHeight);
        footerView.setLayoutParams(footerParams);
        footerView.setBackgroundColor(Color.TRANSPARENT);
        footerParams.gravity = Gravity.BOTTOM;
        addView(footerView);
    }

    public View getEmptyView() {
        return emptyView;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    /**
     * 是否滑动到顶部
     */
    private boolean isAbsListViewScrollTop = true;
    private boolean isRecyclerViewScrollTop = true;

    /**
     * 是否滑动到底部
     */
    private boolean isAbsListViewScrollBottom;
    private boolean isRecyclerViewScrollBottom;

    /**
     * 列表滑动事件
     */
    private class AbsListViewOnScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (emptyView != null) {
                emptyView.setVisibility(view.getCount() > 0 ? GONE : VISIBLE);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem == 0) {
                View first_view = view.getChildAt(0);
                if (first_view != null && first_view.getTop() == 0) {
                    isAbsListViewScrollTop = true;
                } else {
                    isAbsListViewScrollTop = false;
                }
            }
            if (firstVisibleItem + visibleItemCount == totalItemCount) {
                View last_view = view.getChildAt(view.getChildCount() - 1);
                if (last_view != null && last_view.getBottom() == view.getHeight()) {
                    isAbsListViewScrollBottom = true;
                } else {
                    isAbsListViewScrollBottom = false;
                }
            }
        }
    }

    private class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //检查向上滚动为负，检查向下滚动为正。
            isRecyclerViewScrollTop = !recyclerView.canScrollVertically(-1);
            isRecyclerViewScrollBottom = !recyclerView.canScrollVertically(1);
        }
    }

    /**
     * 检查ViewGroup是否有对应的滑动控件，处理多层嵌套时获取不到滑动控件
     *
     * @param parent ViewGroup
     * @param isInti 是否初始化
     */
    private void checkViewGroup(ViewGroup parent, boolean isInti) {
        if (parent != null && (scrollView == null || absListView == null)) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                if (parent.getChildAt(i) instanceof ScrollView) {
                    scrollView = (ScrollView) parent.getChildAt(i);
                }
                if (scrollView == null && parent.getChildAt(i) instanceof AbsListView) {
                    absListView = (AbsListView) parent.getChildAt(i);
                }
                if (parent.getChildAt(i) instanceof RecyclerView) {
                    recyclerView = (RecyclerView) parent.getChildAt(i);
                }
                if (parent.getChildAt(i) instanceof NestedScrollView) {
                    nestedScrollView = (NestedScrollView) parent.getChildAt(i);
                }
                if (isInti && i < contentView.length) {
                    contentView[i] = parent.getChildAt(i);
                }
                if (parent.getChildAt(i) instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) parent.getChildAt(i);
                    if (group != null) {
                        checkViewGroup(group, false);
                    }
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //头部
        MarginLayoutParams headerParams = (MarginLayoutParams) headerView.getLayoutParams();
        left = getPaddingLeft() + headerParams.leftMargin;
        top = getPaddingTop() + headerParams.topMargin - headerView.getMeasuredHeight() + (int) refreshRemainY + (int) refreshMoveY;
        right = left + headerView.getMeasuredWidth();
        bottom = top + headerView.getMeasuredHeight();
        headerView.layout(left, top, right, bottom);
        //内容区域
        for (int i = 0; i < contentView.length; i++) {
            View child = contentView[i];
            MarginLayoutParams childParams = (MarginLayoutParams) child.getLayoutParams();
            left = getPaddingLeft() + childParams.leftMargin;
            top = getPaddingTop() + childParams.topMargin;
            //对对齐方式的控件处理
            if (child.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams params = (LayoutParams) child.getLayoutParams();
                if (params.gravity == Gravity.RIGHT) {
                    left = getMeasuredWidth() - getPaddingLeft() - childParams.leftMargin - child.getMeasuredWidth();
                }
                if (params.gravity == Gravity.BOTTOM) {
                    top = getMeasuredHeight() - getPaddingTop() - childParams.topMargin - child.getMeasuredHeight();
                }
                if (params.gravity == Gravity.CENTER) {
                    left = getMeasuredWidth() / 2 - getPaddingLeft() - childParams.leftMargin - child.getMeasuredWidth() / 2;
                    top = getMeasuredHeight() / 2 - getPaddingTop() - childParams.topMargin - child.getMeasuredHeight() / 2;
                }
                if (params.gravity == Gravity.CENTER_VERTICAL) {
                    top = getMeasuredHeight() / 2 - getPaddingTop() - childParams.topMargin - child.getMeasuredHeight() / 2;
                }
                if (params.gravity == Gravity.CENTER_HORIZONTAL) {
                    left = getMeasuredWidth() / 2 - getPaddingLeft() - childParams.leftMargin - child.getMeasuredWidth() / 2;
                }
                if (params.gravity == Gravity.RIGHT + Gravity.CENTER_VERTICAL) {
                    left = getMeasuredWidth() - getPaddingLeft() - childParams.leftMargin - child.getMeasuredWidth();
                    top = getMeasuredHeight() / 2 - getPaddingTop() - childParams.topMargin - child.getMeasuredHeight() / 2;
                }
                if (params.gravity == Gravity.BOTTOM + Gravity.RIGHT) {
                    left = getMeasuredWidth() - getPaddingLeft() - childParams.leftMargin - child.getMeasuredWidth();
                    top = getMeasuredHeight() - getPaddingTop() - childParams.topMargin - child.getMeasuredHeight();
                }
            }
            right = left + child.getMeasuredWidth();
            bottom = top + child.getMeasuredHeight();
            child.layout(left, top, right, bottom);
        }
        //脚部
        MarginLayoutParams footerParams = (MarginLayoutParams) footerView.getLayoutParams();
        left = getPaddingLeft() + footerParams.leftMargin;
        top = getMeasuredHeight() + getPaddingTop() + footerParams.topMargin + (int) loadRemainY + (int) loadMoveY;
        right = left + footerView.getMeasuredWidth();
        bottom = top + footerView.getMeasuredHeight();
        footerView.layout(left, top, right, bottom);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                if (DEBUG){
                    Log.i(TAG,"->onInterceptTouchEvent ACTION_DOWN");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX() - downX;
                float moveY = event.getY() - downY;
                if (DEBUG){
                    Log.i(TAG,"->onInterceptTouchEvent ACTION_MOVE moveX:"+moveX+",moveY:"+moveY);
                }
                if (Math.abs(moveY) < Math.abs(moveX)) {
                    return super.onInterceptTouchEvent(event);
                }
                if (moveY > 0 && refreshEnable) {
                    return isContentViewRefreshEnable();
                }
                if (moveY < 0 && loadEnable) {
                    return isContentViewLoadEnable();
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (isRefreshingRelease) {
                    headerView.start();
                    headerView.setShadowColor(headerShadowColor);
                    if (refreshListener != null && refreshMoveY > 0 && refreshEnable && isTransfinite) {
                        refreshListener.onSwipeRefresh();
                        isRefreshingRelease = true;
                    }
                    if (isTransfinite) {
                        isRefreshing = true;
                        refreshRemainY = refreshMoveY < headerHeight ? refreshMoveY : headerHeight;
                        refreshMoveY = 0;
                        requestLayout();
                    } else {
                        createScaleAnimator(headerView, -1).start();
                    }
                    return true;
                }
                if (isLoadingRelease) {
                    footerView.start();
                    footerView.setShadowColor(footerShadowColor);
                    if (loadListener != null && loadMoveY < 0 && loadEnable && isTransfinite) {
                        loadListener.onSwipeLoad();
                        isLoadingRelease = true;
                    }
                    if (isTransfinite) {
                        isLoading = true;
                        loadRemainY = loadEnable ? (loadMoveY > footerView.getMeasuredHeight() ? -loadMoveY : -footerView.getMeasuredHeight()) : 0;
                        refreshRemainY = -headerHeight;
                        loadMoveY = 0;
                        requestLayout();
                    } else {
                        createScaleAnimator(footerView, 1).start();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isLoading || isRefreshing) {
                    break;
                }
                float moveY = event.getY() - downY;
                if (Math.abs(moveY) < 20) {
                    break;
                }
                moveY *= 0.25F;
                //利用屏幕的1/12做界限
                int each = 12;
                float movePercent = (moveY) / (getMeasuredHeight() / each);
                isTransfinite = Math.abs(movePercent) > 1;
                if (Math.abs(movePercent) > each) {
                    break;
                }
                if (moveY > 0 && refreshEnable && !isLoading) {//下滑
                    headerView.setScaleX(1);
                    headerView.setScaleY(1);
                    headerView.cancel();
                    headerView.setArcStartAngle(-90);
                    headerView.setArcSweepAngle(360 * movePercent);
                    headerView.setShadowColor(refreshEnable ? headerView.getArcColor() : headerShadowColor);
                    refreshMoveY = moveY;
                    isRefreshingRelease = true;
                    isLoadingRelease = false;
                    isAbsListViewScrollBottom = false;
                }
                if (moveY < 0 && loadEnable && !isRefreshing) {//上滑
                    footerView.setScaleX(1);
                    footerView.setScaleY(1);
                    footerView.cancel();
                    footerView.setArcStartAngle(-90);
                    footerView.setArcSweepAngle(360 * Math.abs(movePercent));
                    footerView.setShadowColor(loadEnable ? footerView.getArcColor() : footerShadowColor);
                    loadMoveY = moveY;
                    isRefreshingRelease = false;
                    isLoadingRelease = true;
                }
                if (isRefreshingRelease || isLoadingRelease) {
                    requestLayout();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.removeAllUpdateListeners();
            animator.cancel();
            animator = null;
        }
    }

    public float dpToPx(float dp) {
        return dp * getScreenDensity();
    }

    public float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public interface OnSwipeRefreshListener {
        void onSwipeRefresh();
    }

    /**
     * 设置刷新监听
     *
     * @param refreshListener
     */
    public void setOnSwipeRefreshListener(OnSwipeRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        if (isRefreshing) {
            setRefreshing(isRefreshing);
        }
    }

    public interface OnSwipeLoadListener {
        void onSwipeLoad();
    }

    /**
     * 设置加载监听
     *
     * @param loadListener
     */
    public void setOnSwipeLoadListener(OnSwipeLoadListener loadListener) {
        this.loadListener = loadListener;
        if (isLoading) {
            setLoading(isLoading);
        }
    }

    /**
     * 设置正在刷新
     *
     * @param isRefreshing 是否开始刷新
     */
    public void setRefreshing(boolean isRefreshing) {
        if (!isRefreshing) {
            if (!this.isRefreshing) {
                return;
            }
            createScaleAnimator(headerView, 1).start();
        } else {
            if (!refreshEnable) {
                return;
            }
            refreshRemainY = headerHeight;
            headerView.start();
            if (refreshListener != null) {
                refreshListener.onSwipeRefresh();
            }
        }
        this.isRefreshing = isRefreshing;
    }

    /**
     * 设置正在加载
     *
     * @param isLoading 是否开始加载
     */
    public void setLoading(boolean isLoading) {
        if (!isLoading) {
            if (!this.isLoading) {
                return;
            }
            createScaleAnimator(footerView, -1).start();
        } else {
            if (!loadEnable) {
                return;
            }
            footerView.start();
            loadRemainY = -footerHeight;
            if (loadListener != null) {
                loadListener.onSwipeLoad();
            }
        }
        this.isLoading = isLoading;
    }

    /**
     * 创建缩放动画师
     *
     * @param view 控件
     * @param type 下拉1 上拉-1
     * @return 动画师
     */
    private synchronized ValueAnimator createScaleAnimator(final View view, final int type) {
        if (animator != null && animator.isStarted() && animator.isRunning()) {
            animator.removeAllUpdateListeners();
            animator = null;
        }
        animator = ValueAnimator.ofFloat(1, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                refreshRemainY = 0;
                loadRemainY = 0;
                refreshMoveY = 0;
                loadMoveY = 0;
                requestLayout();
                isRefreshing = false;
                isLoading = false;
                isTransfinite = false;
                headerView.cancel();
                footerView.cancel();
                if (type == 1) {
                    isRefreshingRelease = true;
                }
                if (type == -1) {
                    isLoadingRelease = true;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setStartDelay(refreshEnable ? delayDuration : 0);
        animator.setDuration(scaleDuration);
        return animator;
    }

    /**
     * 内容去是否可以刷新
     *
     * @return
     */
    private boolean isContentViewRefreshEnable() {
        if (scrollView != null && scrollView.getScrollY() > 0) {
            return false;
        }
        if (nestedScrollView != null && nestedScrollView.getScrollY() > 0) {
            return false;
        }
        if (absListView != null) {
            if (absListView.getAdapter().getCount() == 0) {
                return true;
            }
            return isAbsListViewScrollTop;
        }
        if (recyclerView != null) {
            if (recyclerView.getChildCount() == 0) {
                return true;
            }
            return isRecyclerViewScrollTop;
        }
        return true;
    }

    /**
     * 内容区域是否可以加载
     *
     * @return
     */
    private boolean isContentViewLoadEnable() {
        if (scrollView != null) {
            if ((scrollView.getScrollY() + scrollView.getHeight()) >= scrollView.getChildAt(0).getMeasuredHeight()) {
                return true;
            }
            return false;
        }
        if (nestedScrollView != null) {
            if ((nestedScrollView.getScrollY() + nestedScrollView.getHeight()) >= nestedScrollView.getChildAt(0).getMeasuredHeight()) {
                return true;
            }
            return false;
        }
        if (absListView != null) {
            if (absListView.getAdapter().getCount() == 0) {
                return false;
            }
            int lastPosition = absListView.getLastVisiblePosition();
            int count = absListView.getAdapter().getCount();
            if (lastPosition != count - 1) {
                return false;
            }
            return isAbsListViewScrollBottom && (isRefreshingRelease || isLoadingRelease);
        }
        if (recyclerView != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager.getInitialPrefetchItemCount() == 0) {
                return false;
            }
            layoutManager.findLastCompletelyVisibleItemPosition();
            int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
            int count = layoutManager.getItemCount();
            if (lastPosition != layoutManager.getItemCount() - 1) {
                return false;
            }
            return isRecyclerViewScrollBottom && (isRefreshingRelease || isLoadingRelease);
        }
        return true;
    }

    public void setHeaderHeight(float headerHeight) {
        this.headerHeight = headerHeight;
    }

    public void setFooterHeight(float footerHeight) {
        this.footerHeight = footerHeight;
    }

    public void setRefreshEnable(boolean refreshEnable) {
        this.refreshEnable = refreshEnable;
    }

    public void setLoadEnable(boolean loadEnable) {
        this.loadEnable = loadEnable;
    }

    public void setScaleDuration(int scaleDuration) {
        this.scaleDuration = scaleDuration;
    }

    public void setDelayDuration(int delayDuration) {
        this.delayDuration = delayDuration;
    }

    public void setHeaderShadowColor(int headerShadowColor) {
        this.headerShadowColor = headerShadowColor;
    }

    public void setFooterShadowColor(int footerShadowColor) {
        this.footerShadowColor = footerShadowColor;
    }
}
