package com.android.video;

import java.io.Serializable;

public class VideoMedia implements Serializable {

    /**
     * 视频路径
     */
    private String path;

    /**
     * 视频名字
     */
    private String displayName;

    /**
     * 视频标题
     */
    private String title;

    /**
     * 修改时间
     */
    private String dateModified;


    /**
     * 创建时间
     */
    private String dateAdded;

    /**
     * 大小
     */
    private long size;

    /**
     * 视频时长
     */
    private long duration;

    /**
     * 缩略图
     */
    private String thumb;

    /**
     * 视频宽度
     */
    private int width;

    /**
     * 视频高度
     */
    private int height;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
