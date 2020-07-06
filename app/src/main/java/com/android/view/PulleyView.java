package com.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import java.util.ArrayList;

/**
 * Created by Relin
 * on 2018-10-16.
 */
public class PulleyView extends View implements GestureDetector.OnGestureListener {

    private Paint paint;
    private float itemHeight = dpToPx(10);
    private float dividerHeight = dpToPx(1F);
    private float selectedTextSize = dpToPx(16F);
    private float unSelectedTextSize = dpToPx(16F);
    private int selectedColor = Color.parseColor("#0EB692");
    private int unSelectedColor = Color.parseColor("#AEAEAE");
    private int dividerColor = Color.parseColor("#CDCDCD");
    private int visibleCount = 7;
    private int selectedPosition = 6;
    private ArrayList<String> list;
    private ArrayList<String> items;
    private float width, height;

    private Context context;
    private float centerX, centerY;
    private GestureDetector gestureDetector;
    private OverScroller mScroller;

    private float scrollY = 0;
    private float scrollRatio = 0.25F;

    public PulleyView(Context context) {
        super(context);
        initAttrs(context, null);
    }

    public PulleyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public PulleyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        this.context = context;
        paint = new Paint();
        paint.setTextSize(unSelectedTextSize);
        gestureDetector = new GestureDetector(context, this);
        gestureDetector.setIsLongpressEnabled(false);
        mScroller = new OverScroller(context);
        items = new ArrayList<>();
        list = new ArrayList<>();
        list.add("2012年");
        list.add("2013年");
        list.add("2014年");
        list.add("2015年");

        list.add("2016年");

        list.add("2017年");
        list.add("2018年");
        list.add("2019年");
        list.add("2020年");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        centerX = width / 2;
        centerY = height / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int listSize = list == null ? 0 : list.size();
        int upVisibleCount = visibleCount % 2 == 0 ? visibleCount / 2 - 1 : visibleCount / 2;
        int downVisibleCount = visibleCount % 2 == 0 ? visibleCount / 2 : visibleCount / 2;
        int upStartPosition = selectedPosition - 1 < 0 ? 0 : selectedPosition - 1;
        int downStartPosition = selectedPosition + 1;

        items.clear();

        //2 -2
        for (int i = upStartPosition; i > upStartPosition - upVisibleCount; i--) {
            int position = i;
            if (i < 0) {
                position = position + listSize;
            }
            items.add(list.get(position));
        }
        for (int i = 0; i < list.size(); i++) {
            if (i == selectedPosition) {
                items.add(list.get(i));
            }
        }
        //5 9
        for (int i = downStartPosition; i < downStartPosition + downVisibleCount; i++) {
            int position = i;
            if (i > listSize - 1) {
                position = i % listSize;
            }
            items.add(list.get(position));
        }

        Log.e("RRL", "items:" + items.toString());
        int itemSize = items == null ? 0 : items.size();
        //中间文字
        String centerText = list.get(selectedPosition);

        float outsideItemHeight = 4.0F * itemHeight;
        for (int i = 3; i > -1; i--) {
            canvas.save();
            float ratio = 1F - 0.2F * i;
            canvas.scale(1, ratio);
            float centerItemX = centerX - measureText(paint, centerText)[0] / 2;
            float centerItemY = centerY + measureText(paint, centerText)[1] / 2;
            centerItemY += i * measureText(paint, centerText)[1] / 2;
            canvas.translate(0, centerItemY * -0.2F);
            canvas.drawText(centerText, centerItemX, centerItemY, paint);
            canvas.restore();
        }


        //分割线
        paint.setColor(selectedColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dividerHeight);
        canvas.drawLine(0, centerY - measureText(paint, centerText)[1] / 2, getMeasuredWidth(), centerY - measureText(paint, centerText)[1] / 2, paint);
        canvas.drawLine(0, centerY + measureText(paint, centerText)[1] / 2, getMeasuredWidth(), centerY + measureText(paint, centerText)[1] / 2, paint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * 计算字体的宽高
     *
     * @param text
     * @return
     */
    private float[] measureText(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return new float[]{rect.width(), rect.height()};
    }


    public float dpToPx(float dp) {
        return dp * getScreenDensity();
    }

    public float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    //================================手势处理===============================
    @Override
    public boolean onDown(MotionEvent e) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        scrollY += distanceY;
        scrollRatio += (distanceY / getMeasuredHeight());
        invalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }
    
}
