package com.android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.R;

/**
 * Created by Relin
 * on 2018-09-26.
 */
public class BrushRequestLayout extends FrameLayout {

    //开始
    public final int BRUSH_START = 1;
    //过界
    public final int BRUSH_BOUND = 2;
    //停留
    public final int BRUSH_REMAIN = 3;
    //完成
    public final int BRUSH_COMPLETE = 4;
    //恢复
    public final int BRUSH_RECOVERY = 0;
    public final int TYPE_REFRESH = 5;
    public final int TYPE_LOAD_MORE = 6;
    //布局填充器
    private LayoutInflater inflater;
    //头部
    private View headerView;
    private BrushHeader header;
    //脚部
    private View footerView;
    private BrushFooter footer;
    //头部高度
    private float headerHeight = dpToPx(50);
    //脚部高度
    private float footerHeight = dpToPx(50);
    //内容视图
    private View contentView[];
    //内容类型-列表
    private AbsListView absListView;
    //内容类型-ScrollView
    private ScrollView scrollView;
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
    //刷新是否可用
    private boolean refreshEnable = true;
    //加载是否可用
    private boolean loadEnable = false;
    //是否超限距离
    private boolean isTransfinite = false;
    //是否正在刷新
    private boolean isRefreshing;
    private boolean isRefreshingRelease;
    //是否正在加载
    private boolean isLoading;
    private boolean isLoadingRelease;
    //刷新监听
    private OnBrushRefreshListener refreshListener;
    //加载监听
    private OnBrushLoadListener loadListener;
    //延时器
    private TimerHandler timerHandler;
    //头部背景
    private int headerBackgroundColor = Color.parseColor("#EDEDEE");
    //头部背景
    private int footerBackgroundColor = Color.parseColor("#EDEDEE");
    //停留时间
    private int duration = 500;
    //帧动画
    private AnimationDrawable animationDrawable;
    private float movePercent;

    public BrushRequestLayout(@NonNull Context context) {
        super(context);
        initAttrs(context, null);
    }

    public BrushRequestLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public BrushRequestLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        timerHandler = new TimerHandler();
        inflater = LayoutInflater.from(context);
        header = new BrushHeader().findViews();
        footer = new BrushFooter().findViews();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BrushRequestLayout);
        headerHeight = typedArray.getDimension(R.styleable.BrushRequestLayout_headerHeight, headerHeight);
        footerHeight = typedArray.getDimension(R.styleable.BrushRequestLayout_footerHeight, footerHeight);
        headerBackgroundColor = typedArray.getColor(R.styleable.BrushRequestLayout_headerBackgroundColor, headerBackgroundColor);
        footerBackgroundColor = typedArray.getColor(R.styleable.BrushRequestLayout_footerBackgroundColor, footerBackgroundColor);
        refreshEnable = typedArray.getBoolean(R.styleable.BrushRequestLayout_refreshable, refreshEnable);
        loadEnable = typedArray.getBoolean(R.styleable.BrushRequestLayout_loadable, loadEnable);
        duration = typedArray.getInt(R.styleable.BrushRequestLayout_duration, duration);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //内容
        contentView = new View[getChildCount()];
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof ScrollView) {
                scrollView = (ScrollView) getChildAt(i);
                //解决不是顶部问题
                setFocusable(true);
                setFocusableInTouchMode(true);
            }
            if (getChildAt(i) instanceof AbsListView) {
                absListView = (AbsListView) getChildAt(i);
            }
            contentView[i] = getChildAt(i);
        }
        //头部
        LayoutParams headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) headerHeight);
        headerView.setLayoutParams(headerParams);
        headerView.setBackgroundColor(Color.CYAN);
        headerView.setBackgroundColor(headerBackgroundColor);
        addView(headerView);
        //脚部
        LayoutParams footerParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) footerHeight);
        footerParams.gravity = Gravity.BOTTOM;
        footerView.setLayoutParams(footerParams);
        footerView.setBackgroundColor(footerBackgroundColor);
        addView(footerView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        headerView.getLayoutParams().height = ((int) refreshRemainY + (int) refreshMoveY);
        footerView.getLayoutParams().height = Math.abs((int) loadRemainY + (int) loadMoveY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
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
            top = getPaddingTop() + childParams.topMargin + (int) refreshMoveY + (int) refreshRemainY + (int) loadRemainY + (int) loadMoveY;
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
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY() - downY;
                float moveX = event.getX() - downX;
                if (Math.abs(moveY) < moveX) {
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
                    //刷新可用就停留，不可用就不停留
                    if (refreshListener != null && refreshMoveY > 0 && refreshEnable && isTransfinite) {
                        refreshListener.onBrushRefresh();
                    }
                    if (isTransfinite) {
                        refreshRemainY = headerHeight;
                        refreshMoveY = 0;
                        isRefreshing = true;
                        showBrushViewState(BRUSH_REMAIN, true, false);
                        requestLayout();
                    } else {
                        refreshMoveY = 0;
                        refreshRemainY = 0;
                        showBrushViewState(BRUSH_RECOVERY, true, false);
                        requestLayout();
                    }
                    return true;
                }
                if (isLoadingRelease) {
                    loadRemainY = loadEnable ? -footerView.getMeasuredHeight() : 0;
                    if (loadListener != null && loadMoveY < 0 && loadEnable && isTransfinite) {
                        loadListener.onBrushLoad();
                    }
                    if (isTransfinite) {
                        loadRemainY = -footerHeight;
                        loadMoveY = 0;
                        isLoading = true;
                        showBrushViewState(BRUSH_REMAIN, false, true);
                        requestLayout();
                    } else {
                        loadMoveY = 0;
                        loadRemainY = 0;
                        showBrushViewState(BRUSH_RECOVERY, false, true);
                        requestLayout();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isLoading || isRefreshing) {
                    break;
                }
                float moveY = event.getY() - downY;
                //正在刷新不能上拉加载，正在加载不能下拉刷新
                if (isRefreshing && moveY < 0 || isLoading && moveY > 0) {
                    break;
                }
                moveY *= 0.25F;
                //利用屏幕的1/5做界限
                int each = 10;
                movePercent = (moveY) / (getMeasuredHeight() / each);
                movePercent = (moveY) / headerHeight;
                isTransfinite = Math.abs(movePercent) > 1;
                if (Math.abs(movePercent) > each) {
                    break;
                }
                if (moveY > 0) {//下滑
                    headerView.setScaleX(1);
                    headerView.setScaleY(1);
                    refreshMoveY = moveY;
                    isRefreshingRelease = true;
                    isLoadingRelease = false;
                    showBrushViewState(movePercent >= 1 ? BRUSH_BOUND : BRUSH_START, true, false);
                } else {//上滑
                    footerView.setScaleX(1);
                    footerView.setScaleY(1);
                    loadMoveY = moveY;
                    isRefreshingRelease = false;
                    isLoadingRelease = true;
                    showBrushViewState(Math.abs(movePercent) >= 1 ? BRUSH_BOUND : BRUSH_START, false, true);
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
        if (timerHandler != null) {
            timerHandler.removeCallbacksAndMessages(null);
        }
        if (animationDrawable != null) {
            animationDrawable.stop();
            animationDrawable = null;
        }
        super.onDetachedFromWindow();
    }

    public float dpToPx(float dp) {
        return dp * getScreenDensity();
    }

    public float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public interface OnBrushRefreshListener {
        void onBrushRefresh();
    }

    /**
     * 设置刷新监听
     *
     * @param refreshListener
     */
    public void setOnBrushRefreshListener(OnBrushRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        if (isRefreshing) {
            setRefreshing(isRefreshing);
        }
    }

    public interface OnBrushLoadListener {
        void onBrushLoad();
    }

    /**
     * 设置加载监听
     *
     * @param loadListener
     */
    public void setOnBrushLoadListener(OnBrushLoadListener loadListener) {
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
            Message msg = timerHandler.obtainMessage();
            msg.obj = TYPE_REFRESH;
            msg.what = BRUSH_COMPLETE;
            timerHandler.sendMessageDelayed(msg, duration);
        } else {
            if (!refreshEnable) {
                return;
            }
            refreshRemainY = headerHeight;
            refreshMoveY = 0;
            isRefreshing = true;
            showBrushViewState(BRUSH_REMAIN, true, false);
            requestLayout();
            if (refreshListener != null) {
                refreshListener.onBrushRefresh();
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
            Message msg = timerHandler.obtainMessage();
            msg.obj = TYPE_LOAD_MORE;
            msg.what = BRUSH_COMPLETE;
            timerHandler.sendMessageDelayed(msg, duration);
        } else {
            if (!loadEnable) {
                return;
            }
            loadRemainY = -footerHeight;
            loadMoveY = 0;
            isLoading = true;
            showBrushViewState(BRUSH_REMAIN, false, true);
            requestLayout();
            if (loadListener != null) {
                loadListener.onBrushLoad();
            }
        }
        this.isLoading = isLoading;
    }

    /**
     * 显示状态视图
     *
     * @param state     状态
     * @param isRefresh 是否是下拉刷新
     * @param isLoad    是否是上拉加载
     */
    private void showBrushViewState(int state, boolean isRefresh, boolean isLoad) {
        switch (state) {
            case BRUSH_START:
                if (isRefresh) {
                    header.iv_refresh_arrow.setRotation(360F);
                    header.tv_refresh_state.setText("下拉刷新");
                }
                if (isLoad) {
                    footer.iv_load_arrow.setRotation(360F);
                    footer.tv_load_state.setText("上拉加载");
                }
                break;
            case BRUSH_BOUND:
                if (isRefresh) {
                    header.iv_refresh_arrow.setRotation(180F);
                    header.tv_refresh_state.setText("释放刷新");
                }
                if (isLoad) {
                    footer.iv_load_arrow.setRotation(180F);
                    footer.tv_load_state.setText("释放加载");
                }
                break;
            case BRUSH_REMAIN:
                if (isRefresh) {
                    header.iv_refresh_arrow.setVisibility(GONE);
                    header.iv_refresh_state.setVisibility(GONE);
                    header.iv_refresh_loading.setVisibility(VISIBLE);
                    animationDrawable = (AnimationDrawable) header.iv_refresh_loading.getBackground();
                    animationDrawable.start();
                    header.tv_refresh_state.setText("刷新数据");
                }
                if (isLoad) {
                    footer.iv_load_arrow.setVisibility(GONE);
                    footer.iv_load_state.setVisibility(GONE);
                    footer.iv_load_loading.setVisibility(VISIBLE);
                    AnimationDrawable animationDrawable = (AnimationDrawable) footer.iv_load_loading.getBackground();
                    animationDrawable.start();
                    footer.tv_load_state.setText("加载数据");
                }
                break;
            case BRUSH_COMPLETE:
                if (isRefresh) {
                    header.iv_refresh_arrow.setVisibility(GONE);
                    header.iv_refresh_state.setVisibility(VISIBLE);
                    header.iv_refresh_loading.setVisibility(GONE);
                    header.tv_refresh_state.setText("刷新成功");
                }
                if (isLoad) {
                    footer.iv_load_arrow.setVisibility(GONE);
                    footer.iv_load_state.setVisibility(VISIBLE);
                    footer.iv_load_loading.setVisibility(GONE);
                    footer.tv_load_state.setText("加载成功");
                }
                break;
            case BRUSH_RECOVERY:
                refreshRemainY = 0;
                loadRemainY = 0;
                refreshMoveY = 0;
                loadMoveY = 0;
                isRefreshing = false;
                isLoading = false;
                if (isRefresh) {
                    header.iv_refresh_arrow.setVisibility(VISIBLE);
                    header.iv_refresh_loading.setVisibility(GONE);
                    header.iv_refresh_state.setVisibility(GONE);
                }
                if (isLoad) {
                    footer.iv_load_arrow.setVisibility(VISIBLE);
                    footer.iv_load_state.setVisibility(GONE);
                    footer.iv_load_loading.setVisibility(GONE);
                }
                break;
        }
        requestLayout();
    }

//    /**
//     * 内容去是否可以刷新
//     *
//     * @return
//     */
//    private boolean isContentViewRefreshEnable() {
//        if (scrollView != null && scrollView.getScrollY() > 0) {
//            return false;
//        }
//        if (absListView != null && absListView.getFirstVisiblePosition() > 0) {
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 内容区域是否可以加载
//     *
//     * @return
//     */
//    private boolean isContentViewLoadEnable() {
//        if (scrollView != null && scrollView.getChildAt(0).getMeasuredHeight() > scrollView.getScrollY() + scrollView.getHeight()) {
//            return false;
//        }
//        if (absListView != null && absListView.getLastVisiblePosition() < (absListView.getCount() - 1)) {
//            return false;
//        }
//        return true;
//    }

    /**
     * 内容去是否可以刷新
     *
     * @return
     */
    private boolean isContentViewRefreshEnable() {
        if (scrollView != null && scrollView.getScrollY() > 0) {
            return false;
        }
        if (absListView != null) {
            if (absListView.getFirstVisiblePosition() == 0) {
                View firstVisibleItemView = absListView.getChildAt(0);
                if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                    Log.i("RRL", "滚动到顶部");
                    return true;
                }
            }
            return false;
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
        if (absListView != null) {
            if (absListView.getCount() == 0) {
                return false;
            }
//            Log.i(this.getClass().getSimpleName(), "[isContentViewLoadEnable] firstPosition:" + absListView.getFirstVisiblePosition() + ",lastPosition:" + absListView.getLastVisiblePosition() + ",count:" + absListView.getCount());
            if (absListView.getLastVisiblePosition() + 1 == absListView.getCount() && absListView.getBottom() == absListView.getHeight()) {
//                Log.i(this.getClass().getSimpleName(), "滚动到底部");
                return true;
            }
            return false;
        }
        return true;
    }


    class BrushHeader {

        ImageView iv_refresh_arrow;
        ImageView iv_refresh_loading;
        ImageView iv_refresh_state;
        TextView tv_refresh_state;

        public BrushHeader() {
            headerView = inflater.inflate(R.layout.android_brush_header, null);
        }

        public BrushHeader findViews() {
            iv_refresh_arrow = headerView.findViewById(R.id.iv_refresh_arrow);
            iv_refresh_loading = headerView.findViewById(R.id.iv_refresh_loading);
            iv_refresh_state = headerView.findViewById(R.id.iv_refresh_state);
            tv_refresh_state = headerView.findViewById(R.id.tv_refresh_state);
            return this;
        }

        public View view() {
            return headerView;
        }

    }

    class BrushFooter {

        ImageView iv_load_arrow;
        ImageView iv_load_loading;
        ImageView iv_load_state;
        TextView tv_load_state;

        public BrushFooter() {
            footerView = inflater.inflate(R.layout.android_brush_footer, null);
        }

        public BrushFooter findViews() {
            iv_load_arrow = footerView.findViewById(R.id.iv_load_arrow);
            iv_load_loading = footerView.findViewById(R.id.iv_load_loading);
            iv_load_state = footerView.findViewById(R.id.iv_load_state);
            tv_load_state = footerView.findViewById(R.id.tv_load_state);
            return this;
        }

        public View view() {
            return footerView;
        }

    }

    class TimerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int type = (int) msg.obj;
            switch (msg.what) {
                case BRUSH_COMPLETE:
                    switch (type) {
                        case TYPE_REFRESH:
                            showBrushViewState(BRUSH_COMPLETE, true, false);
                            break;
                        case TYPE_LOAD_MORE:
                            showBrushViewState(BRUSH_COMPLETE, false, true);
                            break;
                    }
                    Message message = obtainMessage();
                    message.obj = type;
                    message.what = BRUSH_RECOVERY;
                    sendMessageDelayed(message, duration);
                    break;
                case BRUSH_RECOVERY:
                    Log.e("RRL", "type:" + type);
                    switch (type) {
                        case TYPE_REFRESH:
                            showBrushViewState(BRUSH_RECOVERY, true, false);
                            break;
                        case TYPE_LOAD_MORE:
                            showBrushViewState(BRUSH_RECOVERY, false, true);
                            break;
                    }
                    break;
            }
        }
    }

    /**
     * 设置头部高度
     *
     * @param headerHeight
     */
    public void setHeaderHeight(float headerHeight) {
        this.headerHeight = dpToPx(headerHeight);
    }

    /**
     * 设置脚部高度
     *
     * @param footerHeight
     */
    public void setFooterHeight(float footerHeight) {
        this.footerHeight = dpToPx(footerHeight);
    }

    /**
     * 设置是否能刷新
     *
     * @param refreshEnable
     */
    public void setRefreshEnable(boolean refreshEnable) {
        this.refreshEnable = refreshEnable;
    }

    /**
     * 设置是否能加载
     *
     * @param loadEnable
     */
    public void setLoadEnable(boolean loadEnable) {
        this.loadEnable = loadEnable;
    }

    /**
     * 设置头部背景
     *
     * @param headerBackgroundColor
     */
    public void setHeaderBackgroundColor(int headerBackgroundColor) {
        this.headerBackgroundColor = headerBackgroundColor;
    }

    public void setFooterBackgroundColor(int footerBackgroundColor) {
        this.footerBackgroundColor = footerBackgroundColor;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
