package com.xiandian.openstack.cloud.swiftstorage.fs;


import com.woorea.openstack.swift.model.Object;

import java.util.Calendar;
import java.util.Collection;

/**
 * 模拟文件系统的File Java接口定义。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public interface SFile{

    /**
     *  获取当前节点下的目录。
     * @return
     */
    public Collection<SFile> listDirectories() ;


    /**
     * 获取当前节点下的文件。
     * @return
     */
    public Collection<SFile> listFiles();


    /**
     * 增加文件。
     * @param name
     * @param obj
     */
    public void putFile(String name, SFile obj);


    /**
     * 增加目录。
     * @param name
     * @param obj
     */
    public void putDirectory(String name, SFile obj);


    /**
     * 获取当前文件或目录对应的云存储对象。
     * @return
     */
    public Object getSwiftObject() ;


    /**
     *设置当前文件或目录对应的云存储对象。
     * @param metaData
     */
    public void setSwiftObject(Object metaData);


    /**
     * 是否有云存储对象数据？
     * @return
     */
    public boolean hasData();


    /**
     * 获得名称（包含路径）。
     * @return
     */
    public String getName();


    /**
     * 获得最后修改时间。
     *
     * @return
     */
    public Calendar getLastModified();


    /**
     * 获得父节点。
     * @return
     */
    public SFile getParent() ;


    /**
     *
     * @param parent
     * @return
     */
    public void setParent(SFile parent) ;


    /**
     * 获得根节点。
     *
     * @return
     */
    public SFile getRoot();


    /**
     * 调试信息。
     * @param ident
     * @return
     */
    public String toString(String ident);


    /**
     * 内容类型。（http contenttype）
     * @return
     */
    public String getContentType();


    /**
     * 长度，自生的长度，目录为0，文件有自己大小。
     * @return
     */
    public int getSize();

    /**
     * 判断是文件还是文件夹。
     */
    public boolean isFile();

}