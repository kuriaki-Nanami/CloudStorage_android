package com.xiandian.openstack.cloud.swiftstorage.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import com.xiandian.openstack.cloud.swiftstorage.fs.SFile;
import com.xiandian.openstack.cloud.swiftstorage.sdk.service.OpenStackClientService;

import static com.xiandian.openstack.cloud.swiftstorage.R.id.start;

/**
 * 操作实现，具体由各Fragment来实现。
 * 默认MainFragment有个通用实现，各个部分，有自己的实现方式。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public interface SFileEditable {

    /**
     * 根据给定内容，搜索文件。
     * @param fileName 支持通配符的字串
     */
    public void search(String fileName);

    /////////////////////获取跟文件系统，并填充listView的任务/////////////////////>

    /**
     * 分享
     */
    void share();

    /**
     * 全选
     */
    public void selectAll();

    /**
     * 取消全选
     */
    public void unselectAll();

    /**
     * 打开一个文件。
     * @param filePath
     */
    public void openFile(SFile filePath);





    /**
     * 创建一个目录。
     * @param filePath
     */

    public void createDir(String filePath);

    /**
     * 上传文件，回调给当前Activity。
     */
    public void upload();

    /**
     * 下载本地，保存在自己存储空间中。
     */
    public void download();

    /**
     * 拍照，并回传到Activity。
     */
    public void takePhoto();

    /**
     * 拍视频，并回传到Activity
     */
    void recordvideo();

    /**
     * 录音，并回传到Activity
     */
    void recordaudio();

    /**
     * 改名。
     * @param oldFilePath
     * @param newFilePath
     */

    public void rename(String oldFilePath, String newFilePath);

    /**
     * 复制文件到另外一个路径。
     * @param fromPath
     * @param toPath
     */
    public void copy(String fromPath, String toPath);

    /**
     * 移动一个文件，从一个路径去另外一个路径。
     * @param fromPath
     * @param toPath
     */
    public void move(String fromPath, String toPath);

    /**
     * 删除文件到回收站。
     * @param filePath
     */
    public void recycle(String filePath);

    /**
     * 对文件进行排序。
     * @param type
     * @param ascend
     */
    public void sort();


    /**
     * 详细信息。
     * @param type
     * @param ascend
     */
    public void details(int type, boolean ascend);

    /**
     * 刷新当前视口。
     */
    public void refresh();


    void restroe();

    void empty();
}
