package com.android.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.annotation.ViewUtils;
import com.android.app.page.BaseActivity;
import com.android.app.page.BaseFragment;
import com.android.utils.ListUtils;

import java.util.List;

public abstract class Adapter<T, VH extends Adapter.ViewHolder> extends BaseAdapter {

    /**
     * 数据
     */
    private List<T> data;

    /**
     * 空视图
     */
    private View emptyView;

    /**
     * 上下文对象
     */
    private Context context;

    /**
     * Activity页面
     */
    private BaseActivity activity;

    /**
     * Fragment页面
     */
    private BaseFragment fragment;

    private int position;

    private VH holder;

    /**
     * Activity属性构造函数
     *
     * @param activity
     */
    public Adapter(BaseActivity activity) {
        this.activity = activity;
        this.context = activity;
    }

    /**
     * Fragment属性构造函数
     *
     * @param fragment
     */
    public Adapter(BaseFragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    /**
     * 获取空试图
     *
     * @return
     */
    public View getEmptyView() {
        return emptyView;
    }

    /**
     * 设置空试图
     *
     * @param emptyView
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    /**
     * 获取Activity
     *
     * @return
     */
    public BaseActivity getActivity() {
        return activity;
    }

    /**
     * 设置Activity
     *
     * @param activity
     */
    public void setActivity(BaseActivity activity) {
        this.activity = activity;
        this.context = activity;
    }

    /**
     * 获取Fragment对象
     *
     * @return
     */
    public BaseFragment getFragment() {
        return fragment;
    }

    /**
     * 设置Fragment对象
     *
     * @param fragment
     */
    public void setFragment(BaseFragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    /**
     * 获取数据
     *
     * @return
     */
    public List<T> getItems() {
        return data;
    }


    /**
     * 设置数据源
     *
     * @param data
     */
    public void setItems(List<T> data) {
        setItems(data, true);
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setItems(List<T> data, boolean notify) {
        this.data = data;
        if (emptyView != null) {
            emptyView.setVisibility(getCount() == 0 ? View.GONE : View.VISIBLE);
        }
        if (notify) {
            notifyDataSetChanged();
        }
        getCount();
    }

    /**
     * 添加Item
     *
     * @param t
     */
    public void addItem(T t) {
        if (t != null) {
            data.add(t);
        }
        notifyDataSetChanged();
    }

    /**
     * 添加Item
     *
     * @param items
     */
    public void addItems(List<T> items) {
        if (data != null) {
            data.addAll(items);
        }
        notifyDataSetChanged();
    }

    /**
     * 删除Item
     *
     * @param position
     */
    public void removeItem(int position) {
        if (getCount() > 0 && position < getCount()) {
            data.remove(position);
        }
        notifyDataSetChanged();
    }

    /**
     * 删除Items
     *
     * @param positionStart
     * @param itemCount
     */
    public void removeItems(int positionStart, int itemCount) {
        for (int i = 0; i < getCount(); i++) {
            if (i >= positionStart && i < itemCount) {
                data.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 获取上下文对象
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
        int count = ListUtils.getSize(data);
        if (emptyView != null) {
            emptyView.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        }
        return count;
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 创建item视图
     *
     * @param layoutId 视图布局ID
     * @param parent   父控件
     * @return
     */
    public View createView(@LayoutRes int layoutId, @Nullable ViewGroup parent) {
        View convertView = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        return convertView;
    }

    /**
     * 创建Item容器
     *
     * @param itemView item视图
     * @param parent   父控件
     * @param viewType 视图类型
     * @return
     */
    public abstract VH onCreateHolder(View itemView, ViewGroup parent, int viewType);

    /**
     * 绑定Item视图
     *
     * @param holder   视图容器
     * @param position 位置
     */
    public abstract void onBindView(VH holder, int position);

    /**
     * 获取视图类型
     *
     * @param position
     * @return
     */
    public int getViewType(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        this.position = position;
        if (convertView == null) {
            holder = onCreateHolder(convertView, parent, getViewType(position));
            convertView = holder.itemView;
            ViewUtils.inject(holder, holder.itemView);
            holder.itemView.setTag(holder);
        } else {
            holder = (VH) convertView.getTag();
        }
        onBindView(holder, position);
        return convertView;
    }

    /**
     * 视图容器
     */
    public class ViewHolder {

        /**
         * Item视图
         */
        public View itemView;

        /**
         * 视图类型
         */
        public int viewType;

        /**
         * 带有ItemView的构造函数
         *
         * @param itemView
         */
        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }

        /**
         * 获取视图类型
         *
         * @return
         */
        public int getViewType() {
            return viewType;
        }

        /**
         * 设置视图类型
         *
         * @param viewType
         */
        public void setViewType(int viewType) {
            this.viewType = viewType;
        }
    }

    /**
     * Item点击事件
     */
    private OnItemClickListener<T> onItemClickListener;

    /**
     * 获取Item点击事件
     *
     * @return
     */
    public OnItemClickListener<T> getOnItemClickListener() {
        return onItemClickListener;
    }

    /**
     * 设置Item点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 点击事件回调接口
     *
     * @param <T>
     */
    public interface OnItemClickListener<T> {

        void onItemClick(View itemView, List<T> list, int position);

    }

    /**
     * 添加Item点击事件
     *
     * @param v
     * @return
     */
    public void addItemClick(final View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onItemClick(v, getItems(), position);
                }
            }
        });
    }

    /**
     * 设置焦点改变点击事件
     */
    public OnItemFocusChangeListener<T> onItemFocusChangeListener;

    /**
     * 获取焦点改变事件
     *
     * @return
     */
    public OnItemFocusChangeListener<T> getOnItemFocusChangeListener() {
        return onItemFocusChangeListener;
    }

    /**
     * 获取焦点改变事件
     *
     * @param onItemFocusChangeListener
     */
    public void setOnItemFocusChangeListener(OnItemFocusChangeListener<T> onItemFocusChangeListener) {
        this.onItemFocusChangeListener = onItemFocusChangeListener;
    }

    /**
     * 焦点改变事件
     *
     * @param <T>
     */
    public interface OnItemFocusChangeListener<T> {

        void onItemFocusChange(View v, List<T> list, int position);

    }

    /**
     * 添加Item点击事件
     *
     * @param v
     * @return
     */
    public void addItemFocus(final View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOnItemFocusChangeListener() != null) {
                    getOnItemFocusChangeListener().onItemFocusChange(v, getItems(), position);
                }
            }
        });
    }

    /**
     * 跳转页面
     *
     * @param cls Activity类名
     */
    public void startActivity(Class cls) {
        startActivity(cls, null);
    }

    /**
     * 跳转页面
     *
     * @param cls     Activity类名
     * @param options 参数
     */
    public void startActivity(Class cls, Bundle options) {
        if (getActivity() != null) {
            getActivity().startActivity(cls, options);
        }
        if (getFragment() != null) {
            getFragment().startActivity(cls, options);
        }
    }

    /**
     * 跳转页面获取结果
     *
     * @param cls         Activity类名
     * @param requestCode 请求码
     */
    public void startActivityForResult(Class cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 跳转页面获取结果
     *
     * @param cls         Activity类名
     * @param options     参数
     * @param requestCode 请求码
     */
    public void startActivityForResult(Class cls, Bundle options, int requestCode) {
        if (getActivity() != null) {
            getActivity().startActivityForResult(cls, options, requestCode);
        }
        if (getFragment() != null) {
            getFragment().startActivityForResult(cls, options, requestCode);
        }
    }

}
