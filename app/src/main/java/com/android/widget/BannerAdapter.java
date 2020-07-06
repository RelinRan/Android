package com.android.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.view.BannerPager;

import java.util.List;

/**
 * Created by Relin
 * on 2018-10-09.
 */
public abstract class BannerAdapter<T> extends PagerAdapter {

    private Context context;
    private List<T> data;
    private List<T> cache;
    private View convertView;
    private int position;
    private boolean isLoop = true;
    private BannerPager.OnBannerPagerClickListener listener;
    private OnMeasureConvertViewListener onMeasureListener;

    /**
     * 设置是否循环滑动
     *
     * @param isLoop
     */
    public void setLoop(boolean isLoop) {
        setLoop(isLoop, true);
    }

    /**
     * @param isLoop
     */
    public void setLoop(boolean isLoop, boolean isNotify) {
        this.isLoop = isLoop;
        if (getCount() > 0 && isLoop) {
            data.add(0, data.get((getCount() - 1)));
            data.add(data.get(1));
        }
        if (isNotify) {
            notifyDataSetChanged();
        }
    }

    /**
     * 构造参数
     *
     * @param context
     */
    public BannerAdapter(Context context) {
        this.context = context;
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setItems(List<T> data) {
        this.data = cache = data;
        setLoop(isLoop, false);
        notifyDataSetChanged();
    }

    /**
     * 是否循环滑动
     *
     * @return
     */
    public boolean isLoop() {
        return isLoop;
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
     * 获取数据
     *
     * @return
     */
    public List<T> getItems() {
        return data;
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    public int getPosition() {
        return position;
    }

    /**
     * 获取Item对象
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        return data.get(position);
    }

    /**
     * 获取Item View
     *
     * @return
     */
    public View getConvertView() {
        return convertView;
    }

    /**
     * 设置页面点击事件
     *
     * @return
     */
    public BannerPager.OnBannerPagerClickListener getBannerPagerClickListener() {
        return listener;
    }

    /**
     * 设置Pager点击事件
     *
     * @param listener
     */
    public void setOnBannerPagerClickListener(BannerPager.OnBannerPagerClickListener listener) {
        this.listener = listener;
    }

    /**
     * 设置View Measure事件
     *
     * @param onMeasureListener
     */
    public void setOnMeasureConvertViewListener(OnMeasureConvertViewListener onMeasureListener) {
        this.onMeasureListener = onMeasureListener;
    }

    /**
     * View Measure事件
     */
    public interface OnMeasureConvertViewListener {
        void onMeasureConvertView(BannerAdapter adapter, View convertView);
    }

    /**
     * 获取数据大小
     *
     * @return
     */
    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * 判断是否是同一个Item
     *
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 实例化Item
     *
     * @param container 容器
     * @param position  位置
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        this.position = position;
        convertView = getView(LayoutInflater.from(context), position, null, container);
        container.addView(convertView);
        if (onMeasureListener != null) {
            onMeasureListener.onMeasureConvertView(this, convertView);
        }
        return convertView;
    }

    /**
     * 摧毁item
     *
     * @param container 容器
     * @param position  位置
     * @param object    对象
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        this.position = position;
        container.removeView((View) object);
    }

    /**
     * 自定义item视图
     *
     * @return
     */
    protected int getItemViewByLayoutId() {
        return 0;
    }

    /**
     * 获取item
     *
     * @param inflater    布局转换器
     * @param position    位置
     * @param convertView item View
     * @param parent      父控件
     * @return
     */
    protected View getView(LayoutInflater inflater, final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (getItemViewByLayoutId() == 0) {
                convertView = new ImageView(context);
                holder.target = (ImageView) convertView;
                holder.target.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                convertView = inflater.inflate(getItemViewByLayoutId(), null);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        onBindViewHolder(holder, position);
        convertView = holder.target;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBannerPagerClick(position, position - 1);
                }
            }
        });
        return convertView;
    }

    /**
     * View容器
     */
    public class ViewHolder {
        public ImageView target;
    }

    /**
     * 绑定View数据
     *
     * @param holder   控件容器
     * @param position 位置
     */
    public abstract void onBindViewHolder(ViewHolder holder, int position);

}
