package com.android.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Relin
 * on 2018-09-19.
 * 动作控件
 */
public class ActionView extends View {

    public static final int TICK = 0x1a1;
    public static final int EXCLAMATION = 0x1a2;
    public static final int WIRELESS = 0x1a3;
    private int type;
    //错误和正确
    private float centerX, centerY;
    private float width, height;
    private Paint paint;
    private float radius = dpToPx(18);
    private int actionColor = Color.WHITE;
    private float circleStrokeWidth = dpToPx(1);
    private float exclamationUpCircleRadius;
    private float exclamationBottomCircleRadius;
    private float exclamationDotRadius;
    //无网络图
    private float wirelessCircleY;
    private float wirelessCircleX;
    private int wirelessCount = 4;
    private float wirelessStrokeWidth = dpToPx(18);
    private float wirelessInitialRadius = dpToPx(8);
    private float wirelessPadding = dpToPx(5);
    private float wirelessStartAngle = -135;
    private float wirelessSweepAngle = Math.abs(wirelessStartAngle + 90) * 2;

    private List<PathCoordinates> points;

    private boolean isAnimation = false;
    private int duration = 500;
    private float animationSweepAngle = 0;
    private ValueAnimator outCircleAnimator;
    private ValueAnimator pathAnimator;

    public ActionView(Context context) {
        super(context);
        initAttrs(context, null,0,0);
    }

    public ActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs,0,0);
    }

    public ActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs,defStyleAttr,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs,defStyleAttr,defStyleRes);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ActionView,defStyleAttr,defStyleRes);
        type = typedArray.getInt(R.styleable.ActionView_action_type, WIRELESS);
        radius = typedArray.getDimension(R.styleable.ActionView_action_radius, radius);
        actionColor = typedArray.getColor(R.styleable.ActionView_action_color, actionColor);
        circleStrokeWidth = typedArray.getDimension(R.styleable.ActionView_action_circle_stroke_width, circleStrokeWidth);
        exclamationUpCircleRadius = typedArray.getDimension(R.styleable.ActionView_action_exclamation_up_circle_radius, exclamationUpCircleRadius);
        exclamationBottomCircleRadius = typedArray.getDimension(R.styleable.ActionView_action_exclamation_bottom_circle_radius, exclamationBottomCircleRadius);
        exclamationDotRadius = typedArray.getDimension(R.styleable.ActionView_action_exclamation_dot_radius, exclamationDotRadius);
        wirelessCount = typedArray.getInt(R.styleable.ActionView_action_wireless_count, wirelessCount);
        wirelessStrokeWidth = typedArray.getDimension(R.styleable.ActionView_action_wireless_stroke_width, wirelessStrokeWidth);
        wirelessInitialRadius = typedArray.getDimension(R.styleable.ActionView_action_wireless_initial_radius, wirelessInitialRadius);
        wirelessPadding = typedArray.getDimension(R.styleable.ActionView_action_wireless_padding, wirelessPadding);
        wirelessStartAngle = typedArray.getDimension(R.styleable.ActionView_action_wireless_start_angle, wirelessStartAngle);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int w = widthSpecSize;
        int h = heightSpecSize;
        int needHeight = (int) (radius * 2);
        switch (type) {
            case EXCLAMATION:
            case TICK:
                needHeight = (int) ((radius + circleStrokeWidth * 2 + getPaddingTop() + getPaddingBottom()) * 2);
                break;
            case WIRELESS:
                needHeight = (int) ((wirelessInitialRadius + wirelessStrokeWidth + wirelessPadding) * wirelessCount);
                break;
        }
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            w = type == WIRELESS ? (int) (needHeight + wirelessStrokeWidth * 2) : needHeight;
            h = needHeight;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            w = needHeight;
            h = heightSpecSize;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            w = widthSpecSize;
            h = needHeight;
        }
        setMeasuredDimension(w, h);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        centerY = height / 2;
        centerX = width / 2;
        wirelessCircleX = width / 2;
        wirelessCircleY = height - getPaddingBottom();
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        switch (type) {
            case TICK:
                drawTickView(canvas);
                break;
            case EXCLAMATION:
                drawExclamationView(canvas);
                break;
            case WIRELESS:
                drawWirelessView(canvas);
                break;
        }
    }

    /**
     * 创建画笔
     *
     * @return
     */
    private Paint createPaint(int color) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(circleStrokeWidth);
        return paint;
    }

    /**
     * 绘制外边缘圆圈
     *
     * @param canvas 画布
     * @param paint  画笔
     */
    private void drawOutsideCircle(Canvas canvas, Paint paint, boolean isAnimation) {
        if (isAnimation) {
            drawOutsideCircle(canvas, paint, animationSweepAngle);
            if (outCircleAnimator != null) {
                return;
            }
            outCircleAnimator = ValueAnimator.ofFloat(360);
            outCircleAnimator.setDuration(duration);
            outCircleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    animationSweepAngle = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            outCircleAnimator.start();
        } else {
            drawOutsideCircle(canvas, paint, 360);
        }
    }


    /**
     * 绘制外边缘圆圈
     *
     * @param canvas     画布
     * @param paint      画笔
     * @param sweepAngle 弧度
     */
    private void drawOutsideCircle(Canvas canvas, Paint paint, float sweepAngle) {
        RectF outsideRectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(outsideRectF, -270, sweepAngle, false, paint);
    }

    private class PathCoordinates {
        float x;
        float y;

        public PathCoordinates(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private Path path = new Path();
    private float[] pos = new float[2];
    private int count = 0;

    /**
     * 创建打钩路径
     *
     * @return
     */
    private Path createTickPath() {
        float part = radius * 2 / 4;
        if (points == null) {
            points = new ArrayList<>();
            points.add(new PathCoordinates(centerX - part - (part / 4), centerY + part / 4));
            points.add(new PathCoordinates(centerX - part, centerY));
            points.add(new PathCoordinates(centerX - part / 3, centerY + 2 * part / 3));
            points.add(new PathCoordinates(centerX + part + 1 * part / 4, centerY - 4 * part / 5));
        }
        Path path = new Path();
        for (int i = 0; i < points.size(); i++) {
            PathCoordinates coordinates = points.get(i);
            if (i == 0) {
                path.moveTo(coordinates.x, coordinates.y);
            } else {
                path.lineTo(coordinates.x, coordinates.y);
            }
        }
        return path;
    }

    /**
     * 绘制成功视图
     *
     * @param canvas
     */
    private void drawTickView(Canvas canvas) {
        paint = createPaint(actionColor);
        //外圆
        drawOutsideCircle(canvas, paint, isAnimation);
        //内钩
        if (isAnimation) {
            canvas.drawPath(path, paint);
            if (pathAnimator != null) {
                return;
            }
            pathAnimator = createPathAnimator(createTickPath());
            pathAnimator.setStartDelay(duration);
            pathAnimator.start();
        } else {
            canvas.drawPath(createTickPath(), paint);
        }
    }


    /**
     * 绘制错误视图
     *
     * @param canvas
     */
    private void drawExclamationView(Canvas canvas) {
        paint = createPaint(actionColor);
        //外圆
        drawOutsideCircle(canvas, paint, false);
        final float part = radius * 2 / 4;
        exclamationUpCircleRadius = part / 5;
        exclamationBottomCircleRadius = part / 10;
        exclamationDotRadius = part / 6;
        float exclamationFlagHeight = 2 * part / 3;
        //====上部分=====
        Path path = new Path();
        paint.setStyle(Paint.Style.FILL);
        //左边线起点
        path.moveTo(centerX - exclamationUpCircleRadius, centerY - 3 * part / 4);
        //左边线终点
        path.lineTo(centerX - exclamationBottomCircleRadius, centerY + exclamationFlagHeight);
        //下边弧线
        RectF bottomOval = new RectF(centerX - exclamationBottomCircleRadius, centerY + exclamationFlagHeight - exclamationBottomCircleRadius,
                centerX + exclamationBottomCircleRadius, centerY + exclamationFlagHeight + exclamationBottomCircleRadius);
        path.arcTo(bottomOval, -180, -180, false);
        //右边线终点
        path.lineTo(centerX + exclamationUpCircleRadius, centerY - 3 * part / 4);
        RectF upOval = new RectF(centerX - exclamationUpCircleRadius, centerY - part, centerX + exclamationUpCircleRadius, centerY - part / 2);
        //上边弧线
        path.arcTo(upOval, -180, 180, false);
        path.close();

        canvas.drawPath(path, paint);
        //=====下部分====
        canvas.drawCircle(centerX, centerY + 4 * part / 5 + exclamationDotRadius * 2, exclamationDotRadius, paint);
    }

    private void drawWirelessView(Canvas canvas) {
        for (int i = 1; i < wirelessCount; i++) {
            drawWirelessView(canvas, actionColor, i);
        }
        drawWirelessCircle(canvas, actionColor, wirelessCircleX, wirelessCircleY - wirelessStrokeWidth, wirelessStrokeWidth / 2);
    }

    /**
     * 绘制上部分
     *
     * @param canvas        画布
     * @param wirelessColor 颜色
     * @param acrIndex      绘制下标[第几个扇形]
     */
    private void drawWirelessView(Canvas canvas, int wirelessColor, int acrIndex) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(wirelessColor);
        paint.setStrokeWidth(wirelessStrokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        //绘制扇形
        float radius = (wirelessInitialRadius + wirelessStrokeWidth + wirelessPadding) * acrIndex;
        RectF rectF = new RectF(wirelessCircleX - radius, wirelessCircleY - radius - wirelessStrokeWidth, wirelessCircleX + radius, wirelessCircleY + radius - wirelessStrokeWidth);
        canvas.drawArc(rectF, wirelessStartAngle, wirelessSweepAngle, false, paint);
        //绘制两端圆点
        double angle = (wirelessSweepAngle / 2) * Math.PI / 180;
        float x = (float) (Math.sin(angle) * (radius));
        float y = (float) (Math.cos(angle) * (radius));
        //左边圆点
        drawWirelessCircle(canvas, wirelessColor, wirelessCircleX - x, wirelessCircleY - y - wirelessStrokeWidth, wirelessStrokeWidth / 2);
        //右边圆点
        drawWirelessCircle(canvas, wirelessColor, wirelessCircleX + x, wirelessCircleY - y - wirelessStrokeWidth, wirelessStrokeWidth / 2);
    }

    /**
     * 绘制圆
     *
     * @param canvas          画布
     * @param color           颜色
     * @param wirelessCircleX 圆中心X
     * @param wirelessCircleY 圆中心Y
     * @param wirelessRadius  圆半径
     */
    public void drawWirelessCircle(Canvas canvas, int color, float wirelessCircleX, float wirelessCircleY, float wirelessRadius) {
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(wirelessStrokeWidth);
        paint.setAntiAlias(true);
        canvas.drawCircle(wirelessCircleX, wirelessCircleY, wirelessRadius, paint);
    }

    public static float dpToPx(float dp) {
        return dp * getScreenDensity();
    }

    public static float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    private ValueAnimator createPathAnimator(final Path path) {
        final PathMeasure pathMeasure = new PathMeasure(path, false);
        ValueAnimator pathAnimator = ValueAnimator.ofFloat(pathMeasure.getLength());
        pathAnimator.setDuration(duration);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                count++;
                float distance = (float) animation.getAnimatedValue();
                pathMeasure.getPosTan(distance, pos, null);
                if (count == 1) {
                    ActionView.this.path.moveTo(pos[0], pos[1]);
                } else {
                    ActionView.this.path.lineTo(pos[0], pos[1]);
                }
                postInvalidate();
            }
        });
        outCircleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (count == 3) {
                    outCircleAnimator = null;
                    count = 0;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return pathAnimator;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        postInvalidate();
    }
}
