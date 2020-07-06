package com.android.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.io.IOUtils;
import com.android.utils.DateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoHelper {

    /**
     * 标识
     */
    private static String TAG = "IJKHelper";
    /**
     * 当前音量
     */
    private float currentVoice = 0f;
    /**
     * 按下坐标
     */
    private float downX, downY;
    /**
     * 视频当前位置
     */
    private long position;
    /**
     * 是否改变进度
     */
    private boolean isChangeVideoProgress;
    /**
     * 竖屏布局参数
     */
    private ViewGroup.LayoutParams portraitParams;
    /**
     * 是否有ActionBar
     */
    private boolean isHaveActionBar;

    /**
     * 保持屏幕常亮
     * 要在setContentView()之前调用
     */
    public void keepScreenOn(Context context) {
        Activity activity = (Activity) context;
        if (activity == null) {
            return;
        }
        if (activity.findViewById(android.R.id.content) != null) {
            new RuntimeException("Setting screen constants requires a call before the setContentView () method super");
            return;
        }
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 找到容器视图
     *
     * @param context 上下文对象
     * @return
     */
    public FrameLayout findContent(Context context) {
        Activity activity = (Activity) context;
        if (activity == null) {
            return null;
        }
        return activity.findViewById(android.R.id.content);
    }

    /**
     * 转换屏幕
     *
     * @param orientation 方向
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public void switchScreen(Context context, ViewGroup parent, View videoView, Orientation orientation) {
        AppCompatActivity activity = (AppCompatActivity) context;
        FrameLayout content = findContent(context);
        if (content == null) {
            new RuntimeException("switch screen failed,find activity content is null.");
            return;
        }
        if (videoView == null) {
            new RuntimeException("switch screen failed,don't find view to do anything.");
        }
        //切换横屏
        if (orientation == Orientation.Horizontal) {
            portraitParams = videoView.getLayoutParams();
            //隐藏ActionBar
            if (activity.getSupportActionBar() != null) {
                isHaveActionBar = activity.getSupportActionBar().isShowing();
                activity.getSupportActionBar().hide();
            }
            //隐藏状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //全屏标识
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //横屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //将View添加到Content
            ViewGroup parentView = (ViewGroup) videoView.getParent();
            parentView.removeView(videoView);
            if (content != null) {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                content.addView(videoView, params);
//                videoView.setSurface(surface);
            }
        }
        //切换竖屏
        if (orientation == Orientation.Vertical) {
            //显示ActionBar
            if (isHaveActionBar) {
                activity.getSupportActionBar().show();
            }
            //显示状态栏
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //清除全屏标识
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //竖屏
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //将View复原
            ViewGroup parentView = (ViewGroup) videoView.getParent();
            parentView.removeView(videoView);
            parent.addView(videoView, portraitParams);
//            videoView.setSurface(surface);
        }
    }

    /**
     * 当前屏幕亮度
     *
     * @param context 上下文
     * @return
     */
    public float currentBrightness(Context context) {
        Activity activity = (Activity) context;
        if (activity == null) {
            return 0.0f;
        }
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        return lp.screenBrightness;
    }

    /**
     * 改变亮度
     *
     * @param brightness [0-1]
     */
    public void changeBrightness(Context context, float brightness) {
        Activity activity = (Activity) context;
        if (activity == null) {
            return;
        }
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness;
        window.setAttributes(lp);
    }

    /**
     * 当前音量值
     *
     * @param context 上下文对象
     * @return
     */
    public float currentVoice(Context context) {
        if (currentVoice != 0) {
            return currentVoice;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        currentVoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return currentVoice;
    }

    /**
     * 最大音量
     *
     * @param context
     * @return
     */
    public float maxVoice(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 改变声音
     *
     * @param voiceValue 0-1
     */
    public void changeVoice(Context context, float voiceValue) {
        currentVoice = voiceValue;
        AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) voiceValue, AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 触摸事件
     *
     * @param context  上下文对象
     * @param event    事件
     * @param view     控件
     * @param current  视频播放位置
     * @param duration 视频时长
     * @param listener 事件监听
     * @return
     */
    public boolean onTouchEvent(Context context, MotionEvent event, View view, long current, long duration, com.android.video.OnVideoTouchListener listener) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                android.util.Log.i(TAG, "->onTouchEvent ACTION_DOWN");
                downX = event.getRawX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (listener != null) {
                    listener.onVideoControlViewShow(event);
                }
                float distanceXValue = event.getX() - downX;
                float distanceYValue = event.getY() - downY;
                float distanceX = Math.abs(distanceXValue);
                float distanceY = Math.abs(distanceYValue);
                float tan = distanceY / distanceX;
                float width = view.getMeasuredWidth();
                float height = view.getMeasuredHeight();
                if (tan <= 1) {
                    isChangeVideoProgress = true;
                    float horizontalPercent = distanceX / width;
                    position = (long) increaseDecreaseValue(distanceXValue, horizontalPercent, current, duration, 0, 1f, Orientation.Horizontal);
                    android.util.Log.i(TAG, "->onTouchEvent ACTION_MOVE Horizontal percent:" + (position * 1.0f / duration) + ",duration:" + duration + ",current:" + current + ",position:" + position);
                    if (listener != null) {
                        listener.onVideoStartChangeProgress(position, position * 1.0f / duration);
                    }
                } else {
                    float verticalPercent = distanceY / height;
                    //左边右边判断
                    float horizontalMiddleX = view.getMeasuredWidth() / 2;
                    if (downX < horizontalMiddleX) {//左边
                        //当前亮度值
                        float brightness = increaseDecreaseValue(distanceYValue, verticalPercent, currentBrightness(context), 1f, 0f, 0.01f, Orientation.Vertical);
                        android.util.Log.i(TAG, "->onTouchEvent ACTION_MOVE Vertical Left percent:" + (brightness / 1f) + ",brightness：" + brightness);
                        if (listener != null) {
                            listener.onVideoChangeBrightness(brightness, brightness / 1f);
                        }
                    } else {//右边
                        float currentVoice = currentVoice(context);
                        float maxVoice = maxVoice(context);
                        android.util.Log.i(TAG, "->onTouchEvent ACTION_MOVE Vertical Right currentVoice:" + currentVoice + ",maxVoice：" + maxVoice);
                        //当前亮度值
                        float voice = increaseDecreaseValue(distanceYValue, verticalPercent, currentVoice, maxVoice, 0, 0.01f, Orientation.Vertical);
                        android.util.Log.i(TAG, "->onTouchEvent ACTION_MOVE Vertical Right percent:" + verticalPercent + ",voice：" + voice);
                        if (listener != null) {
                            listener.onVideoChangeVoice(voice, voice / maxVoice);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "->onTouchEvent ACTION_UP");
                if (listener != null && isChangeVideoProgress) {
                    listener.onVideoStopChangeProgress(position, position * 1.0f / duration);
                    isChangeVideoProgress = false;
                }
                if (listener != null) {
                    listener.onVideoControlViewHide(event);
                }
                break;
        }
        return true;
    }

    /**
     * 增减值处理
     *
     * @param distanceValue   移动终点 - 起点的值
     * @param verticalPercent 竖向移动百分比
     * @param currentValue    当前值
     * @param maxValue        最大值
     * @param minValue        最小值
     * @param coefficient     系数值
     * @param orientation     滑动方向
     * @return
     */
    public float increaseDecreaseValue(float distanceValue, float verticalPercent, float currentValue, float maxValue, float minValue, float coefficient, Orientation orientation) {
        if (orientation == Orientation.Vertical) {
            if (distanceValue > 0) {//向下滑动
                currentValue -= verticalPercent * maxValue * coefficient;
            } else {//向上滑动
                currentValue += verticalPercent * maxValue * coefficient;
            }
        }
        if (orientation == Orientation.Horizontal) {
            if (distanceValue > 0) {//向右滑动
                currentValue += verticalPercent * maxValue * coefficient;
            } else {//向左滑动
                currentValue -= verticalPercent * maxValue * coefficient;
            }
        }
        if (currentValue > maxValue) {
            currentValue = maxValue;
        }
        if (currentValue < minValue) {
            currentValue = minValue;
        }
        return currentValue;
    }

    /**
     * 获取视频信息
     *
     * @param context 上下文对象
     * @param path    文件路径
     * @return
     */
    public static VideoMedia query(Context context, String path) {
        return query(context, new File(path));
    }

    /**
     * 查询视频信息
     *
     * @param file
     * @return
     */
    public static VideoMedia query(File file) {
        MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        mmr.setDataSource(file.getAbsolutePath());
        long duration = Long.parseLong(mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION));
        VideoMedia info = new VideoMedia();
        info.setTitle(file.getName().substring(file.getName().lastIndexOf(".") + 1));
        info.setDisplayName(file.getName());
        info.setDateAdded(DateUtils.parseFromTimestamp(String.valueOf(file.lastModified() / 1000)));
        info.setDateModified(DateUtils.parseFromTimestamp(String.valueOf(file.lastModified() / 1000)));
        info.setPath(file.getAbsolutePath());
        info.setSize(file.length());
        info.setDuration(duration);
        Bitmap bitmap = mmr.getFrameAtTime();
        info.setThumb(IOUtils.decodeBitmap(bitmap).getAbsolutePath());
        return info;
    }

    /**
     * 获取视频信息
     *
     * @param context 上下文对象
     * @param file    文件
     * @return
     */
    public static VideoMedia query(Context context, File file) {
        VideoMedia info = null;
        String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Video.Media.DISPLAY_NAME + "= ?", new String[]{file.getName()}, null);
        if (cursor == null) {
            return query(file);
        }
        if (cursor.moveToFirst()) {
            info = new VideoMedia();
            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
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
                long date = file.lastModified();
                date = date / 1000;
                String fileDate = DateUtils.parseFromTimestamp(String.valueOf(date));
                info.setDateModified(fileDate);
                info.setDateAdded(fileDate);
            }
            info.setWidth(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)));
            info.setHeight(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)));
        }
        if (cursor != null) {
            cursor.close();
        }
        return info;
    }


    /**
     * 扫描视频
     *
     * @param context 上下文对象
     * @param minSize 最小值
     * @param maxSize 最大值
     * @return
     */
    public static List<VideoMedia> query(Context context, long minSize, long maxSize) {
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
                if (size >= minSize && size <= maxSize) {
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


}
