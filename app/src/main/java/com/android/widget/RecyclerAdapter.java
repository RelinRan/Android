package com.android.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.annotation.ViewUtils;
import com.android.app.page.BaseActivity;
import com.android.app.page.BaseFragment;
import com.android.utils.ListUtils;

import java.util.List;

public abstract class RecyclerAdapter<T, VH extends RecyclerAdapter.ViewHolder> extends RecyclerView.Adapter {

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

    /**
     * 数据对象
     */
    private List<T> data;

    /**
     * 空视图
     */
    private View emptyView;

    /**
     * 位置
     */
    private int position;

    /**
     * Activity属性构造函数
     *
     * @param activity 页面
     */
    public RecyclerAdapter(BaseActivity activity) {
        this.activity = activity;
        this.context = activity;
    }

    /**
     * Fragment属性构造函数
     *
     * @param fragment 页面
     */
    public RecyclerAdapter(BaseFragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    /**
     * 构造函数
     *
     * @param activity 页面
     * @param data     数据
     */
    public RecyclerAdapter(BaseActivity activity, List<T> data) {
        this.activity = activity;
        this.context = activity;
        this.data = data;
    }

    /**
     * 构造函数
     *
     * @param fragment 页面
     * @param data     数据
     */
    public RecyclerAdapter(BaseFragment fragment, List<T> data) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.data = data;
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
            emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
        if (notify) {
            notifyDataSetChanged();
        }
        getItemCount();
    }


    /**
     * 添加Items
     *
     * @param data
     */
    public void addItems(List<T> data) {
        int size = getItemCount();
        int positionStart = size == 0 ? 0 : size;
        if (data != null) {
            data.addAll(data);
        }
        notifyItemRangeInserted(positionStart, getItemCount());
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
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * 删除Item
     *
     * @param position
     */
    public void removeItem(int position) {
        if (getItemCount() > 0) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * 删除Item
     *
     * @param positionStart
     */
    public void removeItems(int positionStart, int itemCount) {
        if (getItemCount() > 0) {
            for (int i = 0; i < getItemCount() && itemCount <= getItemCount(); i++) {
                if (i >= positionStart && i < itemCount) {
                    data.remove(i);
                }
            }
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    /**
     * 移动Item
     *
     * @param fromPosition 原位置
     * @param toPosition   新位置
     */
    public void moveItem(int fromPosition, int toPosition) {
        data.add(toPosition, data.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
        notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition - toPosition) + 1);
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
     * 获取Item
     *
     * @param position 位置
     * @return
     */
    public T getItem(int position) {
        if (data == null) {
            return null;
        }
        return data.get(position);
    }

    /**
     * 获取空视图
     *
     * @return
     */
    public View getEmptyView() {
        return emptyView;
    }

    /**
     * 设置空视图
     *
     * @param emptyView 视图
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 获取上下文对象
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取页面对象
     *
     * @return
     */
    public BaseActivity getActivity() {
        return activity;
    }

    /**
     * 设置页面对象
     *
     * @param activity
     */
    public void setActivity(BaseActivity activity) {
        this.activity = activity;
    }

    /**
     * 获取页面对象
     *
     * @return
     */
    public BaseFragment getFragment() {
        return fragment;
    }

    /**
     * 设置页面对象
     *
     * @param fragment
     */
    public void setFragment(BaseFragment fragment) {
        this.fragment = fragment;
    }


    public View createView(@LayoutRes int layoutId, @Nullable ViewGroup parent) {
        View convertView = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        return convertView;
    }

    @Override
    public int getItemCount() {
        int itemCount = ListUtils.getSize(data);
        if (emptyView != null) {
            emptyView.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
        }
        return itemCount;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        this.position = position;
        VH vh = (VH) holder;
        onBindView(vh, position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateHolder(parent, viewType);
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     */
    public abstract void onBindView(@NonNull VH holder, int position);

    /**
     * 创建数据视图
     *
     * @param parent
     * @param viewType
     * @return
     */
    public abstract VH onCreateHolder(@NonNull ViewGroup parent, int viewType);

    /**
     * 视图容器
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * 视图容器构造函数
         *
         * @param itemView 视图控件
         */
        public ViewHolder(View itemView) {
            super(itemView);
            ViewUtils.inject(this, itemView);
        }

    }

    /**
     * Item点击事件
     */
    private OnItemClickListener<T> onItemClickListener;

    /**
     * 设置Item点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 获取Item点击事件
     *
     * @return
     */
    public OnItemClickListener<T> getOnItemClickListener() {
        return onItemClickListener;
    }

    /**
     * Item点击事件回调
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
    public RecyclerAdapter<T, VH> addItemClick(final View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onItemClick(v, getItems(), position);
                }
            }
        });
        return this;
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
    public RecyclerAdapter<T, VH> addItemFocus(final View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getOnItemFocusChangeListener() != null) {
                    getOnItemFocusChangeListener().onItemFocusChange(v, getItems(), position);
                }
            }
        });
        return this;
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
