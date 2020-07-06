package com.android.annotation;

import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author RelinRan
 * @date 2017-04-15
 * @description 注解工具类</br>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     br>
 * 通过注解的方式，反射对应类的字段和方法名对</br>
 * 控件进行自动findViewById()</br>
 * by annotation, reflect the field and method name pairs of the corresponding class</br>
 * control to perform automatic findViewById()</br>
 */
public class ViewUtils {

    private static final String TAG = "ViewUtils";
    private static long time = 0;

    /**
     * 注解查找控件
     *
     * @param object Fragment对象或者ViewHolder
     */
    public static void inject(Object object) {
        reflectFindView(object, null);
        injectMethod(object, null);
    }

    /**
     * 注解查找控件
     *
     * @param object Fragment对象或者ViewHolder
     * @param view   视图控件，列如ViewHolder的itemView
     */
    public static void inject(Object object, View view) {
        reflectFindView(object, view);
        injectMethod(object, view);
    }

    /**
     * 反射找到View Id
     *
     * @param object Activity Fragment 或者 ViewHolder
     * @param view   视图控件，列如ViewHolder的itemView
     */
    private static void reflectFindView(Object object, View view) {
        Class<?> fieldClass = object.getClass();
        Field[] fields = fieldClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if (viewInject != null) {
                reflectFindView(object, view, field, viewInject.value());
            }
        }
    }

    /**
     * 反射找控件
     *
     * @param field 注解字段
     * @param view  视图控件，列如ViewHolder的itemView
     * @param id    控件ID
     */
    private static void reflectFindView(Object object, View view, Field field, @IdRes int id) {
        Class<?> fieldCls = object.getClass();
        Class<?> findViewClass = (view == null ? object : view).getClass();
        try {
            // 获取类中的findViewById方法，参数为int
            Method method = (findViewClass == null ? fieldCls : findViewClass).getMethod("findViewById", int.class);
            // 执行该方法，返回一个Object类型的View实例
            Object resView = method.invoke(view == null ? object : view, id);
            field.setAccessible(true);
            // 把字段的值设置为该View的实例
            field.set(object, resView);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 反射方法
     *
     * @param object 注解类
     * @param view   视图控件，列如ViewHolder的itemView
     */
    private static void injectMethod(Object object, View view) {
        reflectMethod(object, view);
    }

    /**
     * 反射方法
     *
     * @param object 注解类
     * @param view   视图控件，列如ViewHolder的itemView
     */
    private static void reflectMethod(Object object, View view) {
        Class<?> objectClass = object.getClass();
        Method[] objectMethods = objectClass.getDeclaredMethods();
        for (int i = 0; i < objectMethods.length; i++) {
            Method method = objectMethods[i];
            method.setAccessible(true);//if method is private
            //get annotation method
            OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick != null) {
                int[] values = onClick.value();
                for (int index = 0; index < values.length; index++) {
                    int id = values[index];
                    reflectMethod(object, view, id, index, method);
                }
            }
        }
    }

    /**
     * 反射方法
     *
     * @param object 注解类
     * @param view   视图控件，列如ViewHolder的itemView
     * @param id     控件ID
     * @param method 注解的方法
     */
    private static void reflectMethod(final Object object, View view, int id, int index, final Method method) {
        Class<?> objectClass = object.getClass();
        try {
            Method findViewMethod = (view == null ? objectClass : view.getClass()).getMethod("findViewById", int.class);
            final View resView = (View) findViewMethod.invoke(view == null ? object : view, id);
            if (resView == null) {
                Log.e(TAG, "@OnClick annotation value view id (index = " + index + ") isn't find any view in " + object.getClass().getSimpleName());
                return;
            }
            resView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (System.currentTimeMillis() - time > 100) {
                        try {
                            method.invoke(object, resView);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    time = System.currentTimeMillis();
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
