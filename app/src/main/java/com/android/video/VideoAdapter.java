package com.android.video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.R;
import com.android.app.page.BaseFragment;
import com.android.utils.ListUtils;

import java.util.List;


public class VideoAdapter extends BaseAdapter {

    private Context context;
    private List<VideoMedia> list;

    /**
     * 视频封面加载器
     */
    public VideoImageLoader loader;

    /**
     * 视频适配器
     *
     * @param fgt  页面
     * @param list 数据
     */
    public VideoAdapter(BaseFragment fgt, List<VideoMedia> list, VideoImageLoader loader, OnItemClickListener onItemClickListener) {
        this.list = list;
        this.loader = loader;
        this.onItemClickListener = onItemClickListener;
        this.context = fgt.getContext();
    }

    /**
     * 刷新数据
     *
     * @param list
     */
    public void refresh(List<VideoMedia> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ListUtils.getSize(list);
    }

    @Override
    public VideoMedia getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.android_item_video, parent, false);
            holder.android_ll_item = convertView.findViewById(R.id.android_ll_item);
            holder.android_iv_img = convertView.findViewById(R.id.android_iv_img);
            holder.android_tv_name = convertView.findViewById(R.id.android_tv_name);
            holder.android_tv_path = convertView.findViewById(R.id.android_tv_path);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (loader != null) {
            loader.onVideoImageLoad(getItem(position).getPath(), holder.android_iv_img);
        }
        holder.android_tv_name.setText(getItem(position).getDisplayName());
        holder.android_tv_path.setText(getItem(position).getDateModified() + "  " + (getItem(position).getSize() / 1024 / 1024) + "M");
        holder.android_ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(list, position);
                }
            }
        });
        return convertView;
    }

    public class ViewHolder {
        private LinearLayout android_ll_item;
        private ImageView android_iv_img;
        private TextView android_tv_name;
        private TextView android_tv_path;
    }


    /**
     * 封面图片加载器
     */
    public interface VideoImageLoader {

        void onVideoImageLoad(String path, ImageView target);

    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {

        void onItemClick(List<VideoMedia> list, int position);

    }

}
