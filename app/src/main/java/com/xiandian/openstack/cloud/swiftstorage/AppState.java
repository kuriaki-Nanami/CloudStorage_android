/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.openstack.cloud.swiftstorage;

import android.content.Intent;
import android.os.Environment;

import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.swift.model.Container;
import com.woorea.openstack.swift.model.Objects;
import com.xiandian.openstack.cloud.swiftstorage.fs.OSSFileSystem;
import com.xiandian.openstack.cloud.swiftstorage.fs.SFile;


/**
 * 存储应用当前的状态，单个实例。保持当前账号、租户、容器、选择的路径等信息。 如果涉及同步（如后台刷新Service），需要特别注意Thread安全。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class AppState {


    // 默认
    private  String openstack_ip = "192.168.1.200";
    private  String openstack_localpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/openstack/";

    /** 当前租户. */
    private Tenant selectedTenant;
    /** 当前容器，默认一个账户给2个容器，存放文件和存放删除的文件. */
    private Container selectedContainer;
    /** 当前选择的对象 */
    private Object selectedObject;
    /** 文件系统 */
    private SFile swiftFS;
    /** 选的文件系统（当前节点） */
    private SFile selectedDirectory;
    /** 公用的Intent */
    private Intent shareIntent;
    /** 是否需要回调. */
    private boolean shouldReturnToCaller;
    /** 状态单实例. 保持当前账号、租户、容器、选择的路径等信息。 */
    private static AppState INSTANCE = new AppState();


    /**
     * 获得状态单实例.
     *
     * @return 获得状态单实例.
     */
    public static AppState getInstance() {
        return INSTANCE;
    }
    /**
     * 初始化单例.
     */
    private AppState() {    }

    /**
     * 返回OpenStackIP地址。
     * @return
     */
    public String getOpenStackIP()
    {
        return this.openstack_ip;
    }

    /**
     * 设置OpenStackIP地址。
     * @param ip
     */
    public void setOpenStackIP(String ip)
    {
        openstack_ip = ip;
    }

    /**
     * 本地存储路径。
     * @return
     */
    public String getOpenStackLocalPath()
    {
        return this.openstack_localpath;
    }


    /**
     * 获得当前租户。
     *
     * @return 当前租户
     */
    public Tenant getSelectedTenant() {
        return selectedTenant;
    }

    /**
     * 设置选择的租户。
     *
     * @param
     */
    public void setSelectedTenant(Tenant selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    /**
     * 设置当前容器。
     *
     * @return the selected container
     */
    public Container getSelectedContainer() {
        return selectedContainer;
    }

    /**
     * 获得当前容器。
     *
     * @param selectedContainer
     *            the new selected container
     */
    public void setSelectedContainer(Container selectedContainer) {
        this.selectedContainer = selectedContainer;
    }

    /**
     * 获得文件系统。
     *
     * @return 文件系统
     */
    public SFile getOSSFS() {
        return swiftFS;
    }

    /**
     * 设置文件系统
     * @param ossFile
     */
    public void setOSSFS(SFile ossFile) {
        this.swiftFS = ossFile;
        setSelectedDirectory(ossFile);
    }

    /**
     * 设置当前对象。
     *
     * @param item
     *            当前选择对象。
     */
    public void setSelectedObject(Object item) {
        this.selectedObject = item;
    }

    /**
     * 获得当前对象。
     *
     * @return 选择对象。
     */
    public Object getSelectedObject() {
        return this.selectedObject;
    }

    /**
     * 选择的目录.
     *
     * @return 当前选择目录
     */
    public SFile getSelectedDirectory() {
        return selectedDirectory;
    }


    /**
     * 设置当前目录。
     * @param selectedDirectory
     */
    public void setSelectedDirectory(SFile selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
    }

    /**
     * 公用的Intent。
     *
     * @return 获得共享的Intent。
     */
    public Intent getShareIntent() {
        return shareIntent;
    }

    /**
     * 设置共享的Intent。
     *
     * @param shareIntent
     */
    public void setShareIntent(Intent shareIntent) {
        this.shareIntent = shareIntent;
    }

    /**
     * 是否支持共享。
     *
     * @return true, if is in sharing mode
     */
    public boolean isInSharingMode() {
        return this.shareIntent != null;
    }

    /**
     * 是否返回回调。
     *
     * @return true, if successful
     */
    public boolean shouldReturnToCaller() {
        return shouldReturnToCaller;
    }

    /**
     * 设置回调。
     *
     * @param shouldReturnToCaller
     *            the new should return to caller
     */
    public void setShouldReturnToCaller(boolean shouldReturnToCaller) {
        this.shouldReturnToCaller = shouldReturnToCaller;
    }

    /**
     * 根据path查找节点。
     *
     * @param root
     * @return
     */
    public SFile findChild(SFile root, String path)
    {
        return OSSFileSystem.findChild(root, path);
    }
    /**
     * 读取对象转成文件。
     *
     * @param objects
     * @return
     */
    public SFile readFromObjects(Objects objects) {
        {
            return OSSFileSystem.readFromObjects(objects);
        }
    }

    /**
     * 查找子文件。
     *
     * @param root
     * @param childPath
     * @return
     */
    public SFile findOrCreateChild(SFile root, String childPath) {

        return OSSFileSystem.findOrCreateChild(root,  childPath);

    }

    /**
     * 清理临时变量。
     */
    public void clear() {
        selectedTenant = null;
        selectedContainer = null;
        selectedObject = null;
        swiftFS = null;
        selectedDirectory = null;
    }



}
