package com.xiandian.openstack.cloud.swiftstorage.fragment;

import android.graphics.Bitmap;

/**
 * List Model Item 的Java Bean对象，存储List条目展示的信息。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class SFileData {

    /**
     * 默认图标资源。
     */
    private int  imageId;

    /**
     * 展示图标（如果图片，视频）展示缩略图。
     */
    //性能优化考虑Image Cache，
    //参考： https://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
    private Bitmap image;
    /**
     * 文件名称。
     */
    private String fileName;
    /**
     * 最后修改日期。
     */
    private String lastModified;
    /**
     * 文件大小，默认单位KB.
     */
    private int fileSize;
    /**
     * 是否目录还是文件。
     */
    private boolean isFolder;

    /**
     * 当前索引号。
     */
    private int index;
    /**
     * 是否选择。
     */
    private boolean checked = false;


    /**
     * 是否选择。
     */
    private long lastModifiedTime;
    /**
     * 是否展示进度条，如果是0不展示，要是1展示
     */
    private int ispb=0;

    public int ispb() {
        return ispb;
    }

    public void setIspb(int ispb) {
        this.ispb = ispb;
    }

    /**
     * 构造函数。
     */
    public SFileData() {
    }

    /**
     * 获取默认图标。
     * @return
     */
    public int getImageResource() {
        return imageId;
    }

    /**
     * 设置默认图标。
     * @param id
     */
    public void setImageResource(int id) {
        this.imageId = id;
    }

    /**
     * 获取图标。
     *
     * @return
     */
    public Bitmap getImage() {
        return image;
    }

    /**
     * 设置图标。
     *
     * @param image
     */
    public void setImage(Bitmap image) {
        this.image = image;
    }

    /**
     * 获得文件名称。
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置文件名称。
     *
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取最后修改时间。
     *
     * @return
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     * 设置最后修改时间。
     *
     * @param lastModified
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * 设置时间的milisecond单位，用于排序。
     *
     * @param time
     */
    public void setLastModifiedTime(long time) {
        this.lastModifiedTime = time;
    }

    /**
     * 时间的milisecond单位，用于排序。
     *
     * @return
     */
    public long getLastModifiedTime() {
        return this.lastModifiedTime;
    }

    /**
     * 获得文件大小。
     *
     * @return
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * 设置文件大小。
     *
     * @param fileSize
     */
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 是否是目录。
     *
     * @return
     */
    public boolean isFolder() {
        return isFolder;
    }

    /**
     * 是否是目录。
     *
     * @param isFolder
     */
    public void setFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    /**
     * 当前索引号。
     *
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * 设置当前索引号。
     *
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * 是否选择。
     *
     * @return
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * 设置是否选中。
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return this.getFileName();
    }


}
