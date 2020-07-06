package com.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.R;


/**
 * Created by Relin
 * on 2015/11/4.
 * 字母列表控件
 */
public class LetterView extends View {

    /**
     * 画笔工具
     **/
    private Paint paint;
    /**
     * 控件的字母数据
     **/
    private String alphabet[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    /**
     * 控件的高度
     **/
    private int height;
    /**
     * 控件宽度
     **/
    private int width;
    /**
     * 单个字母高度
     **/
    private int one_font_height;
    /**
     * 字母的总数量
     */
    private int font_count;
    /**
     * 选中字母的索引
     **/
    private int selected_index = 0;
    //==========属性==============
    /**
     * 字母颜色
     **/
    private int uncheckColor = Color.parseColor("#B0AEFD");
    /**
     * 状态改变的字母颜色
     **/
    private int checkColor = Color.BLACK;
    /**
     * 字体的大小
     **/
    private int fontSize = (int) (Resources.getSystem().getDisplayMetrics().density * 14);

    public LetterView(Context context) {
        super(context);
    }

    public LetterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LetterView);
        uncheckColor = array.getColor(R.styleable.LetterView_uncheckColor, uncheckColor);
        checkColor = array.getColor(R.styleable.LetterView_checkColor, checkColor);
        fontSize = array.getDimensionPixelSize(R.styleable.LetterView_textSize, fontSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        height = getHeight();//获得控件的高度
        width = getWidth();//获得控件的宽度
        font_count = alphabet.length;//总字数
        one_font_height = height / font_count;//一个字的高度
        for (int i = 0; i < font_count; i++) {
            //设置画笔
            paint = new Paint();
            paint.setColor(uncheckColor);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(fontSize);
            //当选中对应的字母的时候，改变画笔的颜色
            if (i == selected_index) {
                paint.setColor(checkColor);
            }
            //计算字体的显示位置
            float x = width / 2 - paint.measureText(alphabet[i]) / 2;
            float y = one_font_height * i + one_font_height;
            canvas.drawText(alphabet[i], x, y, paint);
            paint.reset();//重置画笔工具
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                return true;
            case MotionEvent.ACTION_MOVE:

                return true;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(true);
                return false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float y = event.getY();
        int index = (int) (y / height * font_count);//计算点击字体的位置
        //注意一定要这样判断，不判断也可以正常滑动，只是有些用户如果滑动过于频繁就崩溃
        if (0 <= index && index != -1 && index < font_count) {
            float x = width / 2 - paint.measureText(alphabet[index]) / 2;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    getParent().requestDisallowInterceptTouchEvent(true);
                    selected_index = index;
                    if (onClickListener != null) {
                        onClickListener.onLetterClick(alphabet,index, alphabet[index]);
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    selected_index = index;
                    if (onLetterTouchListener != null) {
                        onLetterTouchListener.onLetterTouch(alphabet,index, alphabet[index]);
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    getParent().requestDisallowInterceptTouchEvent(false);
                    if (onLetterTouchListener != null) {
                        onLetterTouchListener.onLetterTouchRelease(alphabet,index, alphabet[index]);
                    }
                    break;
            }
        }
        return true;
    }

    public int getUncheckColor() {
        return uncheckColor;
    }

    public void setUncheckColor(int uncheckColor) {
        this.uncheckColor = uncheckColor;
        invalidate();
    }

    public int getCheckColor() {
        return checkColor;
    }

    public void setCheckColor(int checkColor) {
        this.checkColor = checkColor;
        invalidate();
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        invalidate();
    }

    public OnLetterClickListener onClickListener;

    public void setOnLetterClickListener(OnLetterClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnLetterClickListener {
        void onLetterClick(String alphabets[], int position, String text);
    }


    public OnLetterTouchListener onLetterTouchListener;

    public void setOnLetterTouchListener(OnLetterTouchListener onLetterTouchListener) {
        this.onLetterTouchListener = onLetterTouchListener;
    }

    public interface OnLetterTouchListener {

        void onLetterTouch(String alphabets[], int position, String text);

        void onLetterTouchRelease(String alphabets[], int position, String text);

    }

}