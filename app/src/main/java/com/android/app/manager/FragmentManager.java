package com.android.app.manager;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.android.app.page.BaseActivity;
import com.android.app.page.BaseFragment;
import com.android.utils.ListUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Relin on 2015/11/28.<br/>
 * Fragment管理器<br/>
 * 主要针对Fragment的添加，显示隐藏控制功能。<br/>
 */
public class FragmentManager {

    /**
     * Fragment类
     */
    private Class<?> cls;

    /**
     * 页面
     */
    private BaseActivity baseActivity;

    /**
     * 是否添加到栈
     */
    private boolean addToBackStack;

    /**
     * Fragment控制类型
     */
    private Type type;

    /**
     * Fragment列表
     */
    private List<BaseFragment> fragments;

    /**
     * Fragment页面
     */
    private BaseFragment currentFragment;

    /**
     * Fragment控制类型枚举
     */
    public enum Type {
        /**
         * 添加方式
         */
        ADD,
        /**
         * 替代方式
         */
        REPLACE
    }

    /**
     * 构造函数
     *
     * @param baseActivity 页面
     */
    public FragmentManager(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
        type = Type.ADD;
        addToBackStack = true;
    }

    /**
     * 处理Fragment
     *
     * @param cls                 Fragment类
     * @param data                数据传递对象
     * @param fragmentContainerId Fragment显示布局ID
     */
    public void processFragment(Class cls, Object data, int fragmentContainerId) {
        this.cls = cls;
        if (cls != null) {
            try {
                String tag = this.getFragmentTag(this);
                BaseFragment fragment = (BaseFragment) baseActivity.getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null) {
                    fragment = (BaseFragment) cls.newInstance();
                }
                if (data instanceof Bundle) {
                    fragment.setArguments((Bundle) data);
                } else {
                    fragment.onActivityArguments(data);
                }
                if (currentFragment != null) {
                    currentFragment.onFragmentLeave();
                }
                if (this.fragments == null) {
                    this.fragments = new ArrayList();
                }
                ListUtils.addDistinctEntry(this.fragments, fragment);
                FragmentTransaction fragmentTransaction = baseActivity.getSupportFragmentManager().beginTransaction();
                if (type != Type.ADD) {
                    fragmentTransaction.replace(fragmentContainerId, fragment, tag);
                } else if (!fragment.isAdded()) {
                    fragmentTransaction.add(fragmentContainerId, fragment, tag);
                } else {
                    Iterator iterator = this.fragments.iterator();
                    while (iterator.hasNext()) {
                        BaseFragment lastFragment = (BaseFragment) iterator.next();
                        fragmentTransaction.hide(lastFragment);
                    }
                    currentFragment.onPause();
                    fragmentTransaction.show(fragment);
                    fragment.onResume();
                }
                currentFragment = fragment;
                if (addToBackStack) {
                    fragmentTransaction.addToBackStack(tag);
                }
                fragmentTransaction.commitAllowingStateLoss();
            } catch (InstantiationException var9) {
                var9.printStackTrace();
            } catch (IllegalAccessException var10) {
                var10.printStackTrace();
            }
        }
    }

    /**
     * 添加Fragment
     *
     * @param cls             Fragment类
     * @param data            数据传递对象
     * @param contentLayoutId Fragment显示布局ID
     */
    public void addFragment(Class<?> cls, Object data, int contentLayoutId) {
        addToBackStack = false;
        processFragment(cls, data, contentLayoutId);
    }

    /**
     * 替代Fragment
     *
     * @param cls             Fragment类
     * @param data            数据传递对象
     * @param contentLayoutId Fragment显示布局ID
     */
    public void replaceFragment(Class<?> cls, Object data, int contentLayoutId) {
        type = Type.REPLACE;
        addToBackStack = false;
        processFragment(cls, data, contentLayoutId);
    }

    /**
     * 获取Fragment标识
     *
     * @param manager Fragment管理对象
     * @return
     */
    private String getFragmentTag(FragmentManager manager) {
        StringBuilder sb = new StringBuilder(manager.cls.toString());
        return sb.toString();
    }

    /**
     * Fragment入栈
     *
     * @param cls               Fragment类
     * @param data              数据传递对象
     * @param contentLayoutById Fragment显示布局ID
     */
    public void pushFragmentToBackStack(Class<?> cls, Object data, int contentLayoutById) {
        addToBackStack = true;
        processFragment(cls, data, contentLayoutById);
    }

    /**
     * 显示对应Fragment
     *
     * @param cls  Fragment类
     * @param data 数据传递对象
     */
    public void goToFragment(Class<?> cls, Object data) {
        if (cls != null) {
            BaseFragment fragment = (BaseFragment) baseActivity.getSupportFragmentManager().findFragmentByTag(cls.toString());
            if (fragment != null) {
                this.currentFragment = fragment;
                fragment.onFragmentBack(data);
            }
            baseActivity.getSupportFragmentManager().popBackStackImmediate(cls.toString(), 0);
        }
    }

    /**
     * 弹出栈顶Fragment
     *
     * @param data 数据传递对象
     */
    public void popTopFragment(Object data) {
        android.support.v4.app.FragmentManager fm = baseActivity.getSupportFragmentManager();
        fm.popBackStackImmediate();
        this.currentFragment = null;
        int cnt = fm.getBackStackEntryCount();
        String name = fm.getBackStackEntryAt(cnt - 1).getName();
        this.currentFragment = (BaseFragment) fm.findFragmentByTag(name);
        this.currentFragment.onFragmentBack(data);
    }

    /**
     * 弹出显示栈底Fragment
     *
     * @param data 数据传递对象
     */
    public void popToRoot(Object data) {
        android.support.v4.app.FragmentManager fm = baseActivity.getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 1) {
            //异步出栈，返回栈中数量不为0，则返回true，否则返回false。
            fm.popBackStackImmediate();
        }
        this.popTopFragment(data);
    }

    /**
     * 获取当前Fragment
     *
     * @return BaseFragment
     */
    public BaseFragment getCurrentFragment() {
        return currentFragment;
    }

}
