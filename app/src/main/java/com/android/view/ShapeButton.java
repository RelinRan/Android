package com.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import com.android.R;

/**
 * Created by Relin</br>
 * on 2018-10-24.</br>
 * 此控件主要是为了方便不写xml的shape</br>
 * 利用代码创建shape,跟shape一样支持</br>
 * 图形类型，填充颜色、边缘线设置，属性名称也一致，</br>
 * 同时为了避免事件的点击冲突采用的是集成自定义的TextView控件。</br>
 * this control is primarily for shape</br>
 * , which does not write XML</br>
 * create shape with code that supports just like shape</br>
 * graphic type, fill color, edge setting, same property name,</br>
 * at the same time, in order to avoid the click conflict of events,</br>
 * the integration of custom TextView control is adopted.</br>
 */
public class ShapeButton extends AppCompatTextView {

    private static final int[] ATTRS = new int[]{android.R.attr.gravity};
    private int solid = getResources().getColor(R.color.colorPrimary);
    private int strokeWidth = 0;
    private int strokeColor = getResources().getColor(R.color.colorPrimary);
    private int shape = 0;
    private float radius = 0;
    private float topLeftRadius = 0;
    private float topRightRadius = 0;
    private float bottomLeftRadius = 0;
    private float bottomRightRadius = 0;
    private float saturation = 0.25f;
    private Drawable normalDrawable;
    private Drawable pressedDrawable;

    public ShapeButton(Context context) {
        super(context);
        initAttrs(context, null);
    }

    public ShapeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public ShapeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShapeButton);
            solid = typedArray.getColor(R.styleable.ShapeButton_solid, solid);
            strokeWidth = (int) typedArray.getDimension(R.styleable.ShapeButton_strokeWidth, strokeWidth);
            strokeColor = typedArray.getColor(R.styleable.ShapeButton_strokeColor, strokeColor);
            if (typedArray.getString(R.styleable.ShapeButton_shape) != null) {
                shape = Integer.parseInt(typedArray.getString(R.styleable.ShapeButton_shape));
            }
            radius = typedArray.getDimension(R.styleable.ShapeButton_radius, 0);
            topLeftRadius = typedArray.getDimension(R.styleable.ShapeButton_topLeftRadius, 0);
            topRightRadius = typedArray.getDimension(R.styleable.ShapeButton_topRightRadius, 0);
            bottomLeftRadius = typedArray.getDimension(R.styleable.ShapeButton_bottomLeftRadius, 0);
            bottomRightRadius = typedArray.getDimension(R.styleable.ShapeButton_bottomRightRadius, 0);
            saturation = typedArray.getFloat(R.styleable.ShapeButton_saturation, 0.90f);
            typedArray.recycle();
        }
        drawDrawable();
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        setGravity(a.getInt(0, Gravity.CENTER));
    }

    protected void drawDrawable() {
        normalDrawable = createShape(shape, strokeWidth, strokeColor, solid, radius, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius);
        pressedDrawable = createShape(shape, strokeWidth, createPressedColor(strokeColor), createPressedColor(solid), radius, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius);

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_pressed};
        states[1] = new int[]{};
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(states[0], pressedDrawable);
        stateListDrawable.addState(states[1], normalDrawable);
        Drawable wrapDrawable = DrawableCompat.wrap(stateListDrawable);
        setDrawable(wrapDrawable);
    }


    /**
     * 使用HSV创建按下状态颜色
     * hsv[2]:值是大的-是深的或亮的
     * create pressed state color by HSV
     * hsv[2] :values is big - is deep or bright
     *
     * @param color
     * @return
     */
    private int createPressedColor(int color) {
        int alpha = Color.alpha(color);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= saturation;
        return Color.HSVToColor(alpha, hsv);
    }


    /**
     * 适用于所有android api
     * 设置各种背景。
     * for all android api
     * set all kinds of background.
     *
     * @param drawable
     */
    private void setDrawable(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }


    /**
     * 创建Shape
     * 这个方法是为了创建一个Shape来替代xml创建Shape.
     * create shape drawable
     * this method create you self background drawable
     * by shape.the same as code in xml.
     *
     * @param shape             类型 GradientDrawable.RECTANGLE  GradientDrawable.OVAL
     * @param strokeWidth       外线宽度 button stroke width
     * @param strokeColor       外线颜色 button stroke color
     * @param solidColor        填充颜色 button background color
     * @param cornerRadius      圆角大小 all corner is the same as is the radius
     * @param topLeftRadius     左上圆角 top left corner radius
     * @param topRightRadius    右上圆角 top right corner radius
     * @param bottomLeftRadius  底左圆角  bottom left corner radius
     * @param bottomRightRadius 底右圆角 bottom right corner radius
     * @return
     */
    public Drawable createShape(int shape, int strokeWidth,
                                int strokeColor, int solidColor, float cornerRadius,
                                float topLeftRadius, float topRightRadius,
                                float bottomLeftRadius, float bottomRightRadius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(shape);
        drawable.setSize(10, 10);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setColor(solidColor);
        if (cornerRadius != 0) {
            drawable.setCornerRadius(cornerRadius);
        } else {
            drawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomLeftRadius, bottomLeftRadius, bottomRightRadius, bottomRightRadius});
        }
        return drawable;
    }

    public int getSolid() {
        return solid;
    }

    public void setSolid(int solid) {
        this.solid = solid;
        drawDrawable();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        drawDrawable();
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        drawDrawable();
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
        drawDrawable();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        drawDrawable();
    }

    public float getTopLeftRadius() {
        return topLeftRadius;
    }

    public void setTopLeftRadius(float topLeftRadius) {
        this.topLeftRadius = topLeftRadius;
        drawDrawable();
    }

    public float getTopRightRadius() {
        return topRightRadius;
    }

    public void setTopRightRadius(float topRightRadius) {
        this.topRightRadius = topRightRadius;
        drawDrawable();
    }

    public float getBottomLeftRadius() {
        return bottomLeftRadius;
    }

    public void setBottomLeftRadius(float bottomLeftRadius) {
        this.bottomLeftRadius = bottomLeftRadius;
        drawDrawable();
    }

    public float getBottomRightRadius() {
        return bottomRightRadius;
    }

    public void setBottomRightRadius(float bottomRightRadius) {
        this.bottomRightRadius = bottomRightRadius;
        drawDrawable();
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
        drawDrawable();
    }

    public Drawable getNormalDrawable() {
        return normalDrawable;
    }

    public void setNormalDrawable(Drawable normalDrawable) {
        this.normalDrawable = normalDrawable;
        drawDrawable();
    }

    public Drawable getPressedDrawable() {
        return pressedDrawable;
    }

    public void setPressedDrawable(Drawable pressedDrawable) {
        this.pressedDrawable = pressedDrawable;
        drawDrawable();
    }
}
