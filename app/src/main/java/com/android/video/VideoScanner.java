package com.android.video;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.android.io.IOUtils;
import com.android.utils.DateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Relin
 * 2019-09-02
 * 视频扫描器，一个简单的的视频扫描功能，可以通过设定扫描路径
 * 扫描的大小区间[minSize,mxSize]
 */
public class VideoScanner {

    public static List<VideoMedia> list;
    public final Context context;
    public final long minSize;
    public final long maxSize;
    private final boolean rescan;
    public final OnVideoScanListener listener;

    public VideoScanner(Builder builder) {
        this.context = builder.context;
        this.rescan = builder.rescan;
        this.minSize = builder.minSize;
        this.maxSize = builder.maxSize;
        this.listener = builder.listener;
        scan();
    }

    public static class Builder {

        private Context context;
        private String path;
        private long minSize = 1024;
        private long maxSize = 20;
        private boolean rescan;
        private OnVideoScanListener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public Context context() {
            return context;
        }

        public String path() {
            return path;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public long minSize() {
            return minSize;
        }

        public Builder minSize(long minSize) {
            this.minSize = minSize;
            return this;
        }

        public long maxSize() {
            return maxSize;
        }

        public Builder maxSize(long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public boolean isRescan() {
            return rescan;
        }

        public Builder rescan(boolean rescan) {
            this.rescan = rescan;
            return this;
        }

        public OnVideoScanListener listener() {
            return listener;
        }

        public Builder listener(OnVideoScanListener listener) {
            this.listener = listener;
            return this;
        }

        public VideoScanner build() {
            return new VideoScanner(this);
        }
    }

    /**
     * 扫面视频
     */
    private void scan() {
        if (list != null && !rescan) {
            if (listener != null) {
                listener.onVideoScan(list);
            }
            return;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                VideoScanner.list = scan(context);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 通知刷新UI
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (listener != null) {
                listener.onVideoScan(list);
            }
        }
    };

    /**
     * 视频扫描监听
     */
    public interface OnVideoScanListener {

        void onVideoScan(List<VideoMedia> list);

    }

    /**
     * 扫描视频
     *
     * @param context 上下文对象
     * @return
     */
    private List<VideoMedia> scan(Context context) {
        List<VideoMedia> videoList = new ArrayList<>();
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};
        String[] mediaColumns = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DATE_MODIFIED, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media.HEIGHT, MediaStore.Video.Media.WIDTH, MediaStore.Video.Media.DURATION};
        Cursor cursor = context.getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);
        if (cursor == null) {
            return videoList;
        }
        if (cursor.moveToFirst()) {
            do {
                VideoMedia info = new VideoMedia();
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                long limit = size / 1024 / 1024;
                if (limit >= minSize && limit <= maxSize) {
                    int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    Cursor thumbCursor = context.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);
                    if (thumbCursor.moveToFirst()) {
                        String thumb = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                        if (TextUtils.isEmpty(thumb)) {
                            MediaMetadataRetriever media = new MediaMetadataRetriever();
                            media.setDataSource(path);
                            Bitmap bitmap = media.getFrameAtTime();
                            thumb = IOUtils.decodeBitmap(bitmap).getAbsolutePath();
                        }
                        info.setThumb(thumb);
                    }
                    info.setSize(size);
                    info.setPath(path);
                    info.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                    info.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                    info.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
                    long modify = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));
                    long added = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
                    modify = modify / 1000;
                    added = added / 1000;
                    info.setDateModified(DateUtils.parseFromTimestamp(String.valueOf(modify)));
                    info.setDateAdded(DateUtils.parseFromTimestamp(String.valueOf(added)));
                    if (info.getDateModified().startsWith("1970")) {
                        File file = new File(info.getPath());
                        long date = file.lastModified();
                        date = date / 1000;
                        String fileDate = DateUtils.parseFromTimestamp(String.valueOf(date));
                        info.setDateModified(fileDate);
                        info.setDateAdded(fileDate);
                    }
                    info.setWidth(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)));
                    info.setHeight(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)));
                    videoList.add(info);
                }
            } while (cursor.moveToNext());
            if (cursor != null) {
                cursor.close();
            }
        }
        return videoList;
    }

    /**
     * 清空数据
     */
    public void clear() {
        list = null;
    }

    /**
     * 销毁对象
     */
    public void destory() {
        list = null;
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }

}
