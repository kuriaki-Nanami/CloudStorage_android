package com.xiandian.openstack.cloud.swiftstorage.fs;


import com.woorea.openstack.swift.model.Object;

import java.util.Calendar;
import java.util.Collection;

/**
 * 模拟文件系统的File 节点的实现。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class OSSFile implements SFile {

    /**
     * 父节点
     */
    private SFile parent;
    /**
     * 当前节点对应的云存储对象
     */
    private com.woorea.openstack.swift.model.Object obj;


    /**
     * 构造文件对象。
     *
     * @param obj
     */
    public OSSFile(com.woorea.openstack.swift.model.Object obj) {
        this.parent = null;
        this.obj = obj;
    }


    /**
     * 构造文件对象。
     *
     * @param parent
     * @param obj
     */
    public OSSFile(SFile parent, com.woorea.openstack.swift.model.Object obj) {
        this.parent = parent;
        this.obj = obj;
    }

    /**
     * 获取当前节点下的目录。
     *
     * @return
     */
    @Override
    public Collection<SFile> listDirectories() {
        return null;
    }

    /**
     * 获取当前节点下的文件。
     *
     * @return
     */
    @Override
    public Collection<SFile> listFiles() {
        return null;
    }

    /**
     * 增加文件。
     *
     * @param name
     * @param obj
     */
    @Override
    public void putFile(String name, SFile obj) {

    }

    /**
     * 增加目录。
     *
     * @param name
     * @param obj
     */
    @Override
    public void putDirectory(String name, SFile obj) {

    }

    /**
     * 获取当前文件或目录对应的云存储对象。
     *
     * @return
     */
    @Override
    public Object getSwiftObject() {

        return obj;
    }

    /**
     * 设置当前文件或目录对应的云存储对象。
     *
     * @param metaData
     */
    @Override
    public void setSwiftObject(Object metaData) {
        this.obj = metaData;

    }

    /**
     * 是否有云存储对象数据？
     *
     * @return
     */
    @Override
    public boolean hasData() {

        return true;
    }


    /**
     * 获得名称（包含路径）。
     *
     * @return
     */
    @Override
    public String getName() {

        return obj.getName();
    }

    /**
     * 获得最后修改时间。
     *
     * @return
     */
    @Override
    public Calendar getLastModified() {

        return obj.getLastModified();
    }

    /**
     * 获得父节点。
     *
     * @return
     */
    @Override
    public SFile getParent() {
        return parent;
    }

    @Override
    public void setParent(SFile parent) {
        this.parent = parent;
    }


    /**
     * 获得根节点。
     *
     * @return
     */
    @Override
    public SFile getRoot() {

        return parent.getRoot();
    }

    /**
     * 调试信息。
     *
     * @param ident
     * @return
     */
    @Override
    public String toString(String ident) {

        return this.obj.toString();
    }


    /**
     * 内容类型。（http contenttype）
     *
     * @return
     */
    @Override
    public String getContentType() {
        return obj.getContentType();
    }

    /**
     * 长度，自生的长度，目录为0，文件有自己大小。
     *
     * @return
     */
    @Override
    public int getSize() {
        return obj.getBytes();
    }

    /**
     * 判断是文件还是文件夹。
     */
    public boolean isFile() {
        return true;
    }
}