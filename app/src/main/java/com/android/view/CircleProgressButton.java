package com.android.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.R;

public class CircleProgressButton extends View {

    private float width;
    private float height;
    private float radius = 20f;
    private int circleColor = Color.parseColor("#FFFFFF");
    private int strokeColor = Color.parseColor("#E0DEDD");
    private float strokeWidth = 12;
    private float strokeWidthIncremental = 15;
    private int progressColor = getResources().getColor(R.color.colorPrimary);
    private float progressWidth = 12;
    private boolean isUp = true;
    private float sweepAngle = 0f;
    private ValueAnimator animator;
    private long duration = 15 * 1000;
    private OnCircleProgressButtonListener listener;

    public CircleProgressButton(Context context) {
        super(context);
    }

    public CircleProgressButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs, 0, 0);
    }

    public CircleProgressButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleProgressButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        radius = (width >= height ? height : width) / 2 - strokeWidth - strokeWidthIncremental;
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressButton, defStyleAttr, defStyleRes);
        radius = typedArray.getDimension(R.styleable.CircleProgressButton_radius, radius);
        circleColor = typedArray.getColor(R.styleable.CircleProgressButton_circleColor, circleColor);
        strokeColor = typedArray.getColor(R.styleable.CircleProgressButton_strokeColor, strokeColor);
        strokeWidth = typedArray.getDimension(R.styleable.CircleProgressButton_strokeWidth, strokeWidth);
        strokeWidthIncremental = typedArray.getDimension(R.styleable.CircleProgressButton_strokeWidthIncremental, strokeWidthIncremental);
        progressColor = typedArray.getColor(R.styleable.CircleProgressButton_progressColor, progressColor);
        progressWidth = typedArray.getDimension(R.styleable.CircleProgressButton_progressColor, progressWidth);
        duration = typedArray.getInt(R.styleable.CircleProgressButton_duration, 15 * 1000);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        //绘制圆
        paint.setColor(circleColor);
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        //绘制边缘背景
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(strokeColor);
        paint.setStrokeWidth(strokeWidth);
        RectF rectF = new RectF(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);
        canvas.drawArc(rectF, 0, 360, false, paint);
        //绘制进度
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(progressColor);
        paint.setStrokeWidth(progressWidth);
        RectF progressRectF = new RectF(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);
        canvas.drawArc(progressRectF, -90, sweepAngle, false, paint);
    }

    private boolean isStart;
    private long touchTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (System.currentTimeMillis() - touchTime < 1500) {
                    return false;
                }
                touchTime = System.currentTimeMillis();
                return true;
            case MotionEvent.ACTION_UP:
                isUp = true;
                isStart = !isStart;
                if (isStart) {
                    strokeWidth += strokeWidthIncremental;
                    progressWidth += strokeWidthIncremental;
                    if (listener != null) {
                        listener.onAnimationStart();
                    }
                    startProgress();
                } else {
                    strokeWidth -= strokeWidthIncremental;
                    progressWidth -= strokeWidthIncremental;
                    if (animator != null) {
                        animator.cancel();
                    }
                    if (duration <= 0) {
                        if (listener != null) {
                            listener.onAnimationEnd();
                        }
                    }
                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 开始进度动画
     */
    private void startProgress() {
        if (animator != null && animator.isStarted()) {
            animator.cancel();
            animator = null;
        }
        if (duration > 0) {
            animator = ValueAnimator.ofFloat(0f, 360);
            animator.setDuration(duration);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    sweepAngle = (float) valueAnimator.getAnimatedValue();
                    if (listener != null) {
                        listener.OnCircleProgress(sweepAngle * 100f / 360f);
                    }
                    invalidate();
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (listener != null) {
                        listener.onAnimationEnd();
                    }
                }
            });
            animator.start();
        }
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnCircleProgressButtonListener(OnCircleProgressButtonListener listener) {
        this.listener = listener;
    }

    public interface OnCircleProgressButtonListener {

        void onAnimationStart();

        void OnCircleProgress(float percent);

        void onAnimationEnd();

    }

    /**
     * 重置
     */
    public void reset() {
        sweepAngle = 0;
        invalidate();
    }

    public void setDuration(long duration) {
        this.duration = duration;
        invalidate();
    }
}
