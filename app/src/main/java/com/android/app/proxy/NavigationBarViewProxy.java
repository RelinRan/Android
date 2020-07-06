package com.android.app.proxy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.android.app.view.NavigationBarView;

/**
 * Created by Relin
 * on 2018-10-12.
 */
public class NavigationBarViewProxy implements NavigationBarView {

    
    @Override
    public void onCreateNavigationBarView(LayoutInflater inflater, View frameView, ViewGroup titleContainerView, int titleBarViewId, int titleBarLayoutId) {
        if (titleBarLayoutId != 0) {
            View titleView = inflater.inflate(titleBarLayoutId, null);
            titleContainerView.addView(titleView);
        }
        if (titleBarViewId != 0) { //自定义Xml中标题[注意必须是在setBaseContentView之后，不然布局中的Title无法找到]

            View xmlTitleView = frameView.findViewById(titleBarViewId);
            if (xmlTitleView == null) {
                return;
            }
            if (xmlTitleView.getParent() != null) {
                ViewParent xmlTitleViewParent = xmlTitleView.getParent();
                if (xmlTitleViewParent instanceof ViewGroup) {
                    ((ViewGroup) xmlTitleViewParent).removeView(xmlTitleView);
                }
            }
            titleContainerView.addView(xmlTitleView);
        }
    }

    @Override
    public void setNavigationBarVisibility(int visibility, View defineNavigationBarView) {
        if (defineNavigationBarView == null) {
            return;
        }
        defineNavigationBarView.setVisibility(visibility);
    }

    @Override
    public void onDestroy() {

    }

}
