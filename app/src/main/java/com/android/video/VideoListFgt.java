package com.android.video;

import android.app.Activity;
import android.content.Intent;
import android.widget.ListView;

import com.android.R;
import com.android.app.mode.LoadingMode;
import com.android.app.page.BaseFragment;
import com.android.io.IOUtils;

import java.util.List;

public abstract class VideoListFgt extends BaseFragment implements VideoScanner.OnVideoScanListener, VideoAdapter.OnItemClickListener {

    public static final String VIDEO_MEDIA = "video_media";
    public static final String VIDEO_MIN_SIZE = "videoMinSize";
    public static final String VIDEO_MAX_SIZE = "videoMaxSize";

    private long minSize;
    private long maxSize;

    private VideoAdapter adapter;
    private List<VideoMedia> list;
    private ListView android_lv_content;


    @Override
    protected int setContentLayoutById() {
        return R.layout.android_video_list;
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        minSize = getActivity().getIntent().getLongExtra(VIDEO_MIN_SIZE, 0);
        maxSize = getActivity().getIntent().getLongExtra(VIDEO_MAX_SIZE, 20);
        android_lv_content = getView().findViewById(R.id.android_lv_content);
        if (VideoScanner.list == null) {
            showLoadingDialog(LoadingMode.DIALOG);
            VideoScanner.Builder builder = new VideoScanner.Builder(getContext());
            builder.path(IOUtils.getSDCardPath());
            builder.minSize(minSize);
            builder.maxSize(maxSize);
            builder.listener(this);
            builder.build();
        } else {
            list = VideoScanner.list;
            adapter = new VideoAdapter(this, list, onCreateVideoImageLoader(), this);
            android_lv_content.setAdapter(adapter);
        }
    }

    public VideoAdapter getAdapter() {
        return adapter;
    }

    public List<VideoMedia> getList() {
        return list;
    }


    /**
     * 设置视频图片加载器
     *
     * @return
     */
    public abstract VideoAdapter.VideoImageLoader onCreateVideoImageLoader();

    @Override
    public void onVideoScan(List<VideoMedia> list) {
        adapter = new VideoAdapter(VideoListFgt.this, list, onCreateVideoImageLoader(), VideoListFgt.this);
        android_lv_content.setAdapter(adapter);
        dismissLoadingDialog();
    }

    @Override
    public void onItemClick(List<VideoMedia> list, int position) {
        Intent intent = new Intent();
        intent.putExtra(VIDEO_MEDIA, list.get(position));
        if (getActivity() != null) {
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            throw new RuntimeException("The Activity is null");
        }
    }

}
