/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.openstack.cloud.swiftstorage.sdk.service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.Tenants;
import com.woorea.openstack.keystone.model.authentication.TokenAuthentication;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.keystone.utils.KeystoneUtils;
import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.model.Container;
import com.woorea.openstack.swift.model.Containers;
import com.woorea.openstack.swift.model.ObjectDownload;
import com.woorea.openstack.swift.model.ObjectForUpload;
import com.woorea.openstack.swift.model.Objects;
import com.xiandian.openstack.cloud.swiftstorage.sdk.connector.AndroidOpenStackClientConnector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenStack Client 服务，单个实例缓存数据。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class OpenStackClientService {

    /**
     * 单实例
     */
    private static OpenStackClientService INSTANCE;

    /**
     * 创建单实例,唯一入口，必须制定参数。
     *
     * @return 获取实例.
     */
    public static OpenStackClientService init(String openStackIP, String userName, String userPassword, String tenantName) {
        if (INSTANCE == null) {
            INSTANCE = new OpenStackClientService();
            OpenStackClientService service = INSTANCE;
            service.setOpenstackIP(openStackIP);
            String url = "http://" + openStackIP + ":5000/v2.0";
            service.setKeystoneAuthUrl(url);
            service.setKeystoneAdminAuthUrl(url);
            service.setKeystoneEndpoint(url);
            service.setKeystonePassword(userPassword);
            service.setKeystoneUsername(userName);
            service.setTenantName(tenantName);
        }
        return INSTANCE;
    }

    /**
     * 创建单实例.
     *
     * @return 获取实例.
     */
    public static OpenStackClientService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OpenStackClientService();
        }
        return INSTANCE;
    }

    /**
     * 创建单实例.
     *
     * @return 获取实例.
     */
    public static OpenStackClientService getService() {
        if (INSTANCE == null) {
            INSTANCE = new OpenStackClientService();
        }
        return INSTANCE;
    }

    /**
     * 连机器(基于URLConnection实现Http请求).
     */
    private AndroidOpenStackClientConnector connector = new AndroidOpenStackClientConnector();

    /**
     * The keystone.
     */
    private Keystone keystone;

    /**
     * The admin keystone.
     */
    private Keystone adminKeystone;
    /**
     * The access.
     */
    private Access access;

    /**
     * The admin access.
     */
    private Access adminAccess;

    /**
     * The swift map.
     */
    private Map<String, Swift> swiftMap = new HashMap<String, Swift>();

    /**
     * The default mapper.
     */
    private ObjectMapper defaultMapper = new ObjectMapper();

    /**
     * The wrapped mapper.
     */
    private ObjectMapper wrappedMapper = new ObjectMapper();

    /**
     * The keystone auth url.
     */
    private String keystoneAuthUrl;

    /**
     * The keystone admin auth url.
     */
    private String keystoneAdminAuthUrl;

    /**
     * The keystone username.
     */
    private String keystoneUsername;

    /**
     * The keystone password.
     */
    private String keystonePassword;

    /**
     * The keystone endpoint.
     */
    private String keystoneEndpoint;

    /**
     * The tenant name.
     */
    private String tenantName;
    /**
     * The tenant name. 注意：获得后重新设置
     */
    private String tenantID;

    /**
     * The Keystone controller host ip
     */
    private String openstackIP;

    /**
     * 初始化open stack client service.
     */
    private OpenStackClientService() {
        defaultMapper.setSerializationInclusion(Include.NON_NULL);
        defaultMapper.enable(SerializationFeature.INDENT_OUTPUT);
        defaultMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        defaultMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        wrappedMapper.setSerializationInclusion(Include.NON_NULL);
        wrappedMapper.enable(SerializationFeature.INDENT_OUTPUT);
        wrappedMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        wrappedMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        wrappedMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        wrappedMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Gets the context.
     *
     * @param type the type
     * @return the context
     */
    public ObjectMapper getContext(Class<?> type) {
        return type.getAnnotation(JsonRootName.class) == null ? defaultMapper : wrappedMapper;
    }

    /**
     * Gets the keystone auth url.
     *
     * @return the keystone auth url
     */
    public String getKeystoneAuthUrl() {
        return keystoneAuthUrl;
    }

    /**
     * Sets the keystone auth url.
     *
     * @param keystoneAuthUrl the new keystone auth url
     */
    public void setKeystoneAuthUrl(String keystoneAuthUrl) {
        this.keystoneAuthUrl = keystoneAuthUrl;
    }

    /**
     * Gets the keystone admin auth url.
     *
     * @return the keystone admin auth url
     */
    public String getKeystoneAdminAuthUrl() {
        return keystoneAdminAuthUrl;
    }

    /**
     * Sets the keystone admin auth url.
     *
     * @param keystoneAdminAuthUrl the new keystone admin auth url
     */
    public void setKeystoneAdminAuthUrl(String keystoneAdminAuthUrl) {
        this.keystoneAdminAuthUrl = keystoneAdminAuthUrl;
    }

    /**
     * Gets the keystone username.
     *
     * @return the keystone username
     */
    public String getKeystoneUsername() {
        return keystoneUsername;
    }

    /**
     * Sets the keystone username.
     *
     * @param keystoneUsername the new keystone username
     */
    public void setKeystoneUsername(String keystoneUsername) {
        this.keystoneUsername = keystoneUsername;
    }

    /**
     * Gets the keystone password.
     *
     * @return the keystone password
     */
    public String getKeystonePassword() {
        return keystonePassword;
    }

    /**
     * Sets the keystone password.
     *
     * @param keystonePassword the new keystone password
     */
    public void setKeystonePassword(String keystonePassword) {
        this.keystonePassword = keystonePassword;
    }

    /**
     * Gets the keystone endpoint.
     *
     * @return the keystone endpoint
     */
    public String getKeystoneEndpoint() {
        return keystoneEndpoint;
    }

    /**
     * Sets the keystone endpoint.
     *
     * @param keystoneEndpoint the new keystone endpoint
     */
    public void setKeystoneEndpoint(String keystoneEndpoint) {
        this.keystoneEndpoint = keystoneEndpoint;
    }

    /**
     * Gets the tenant name.
     *
     * @return the tenant name
     */
    public String getTenantName() {
        return tenantName;
    }

    /**
     * Sets the tenant name.
     *
     * @param tenantName the new tenant name
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    /**
     * Gets the tenant ID.
     *
     * @return the tenant ID
     */
    public String getTenantID() {
        return tenantID;
    }

    /**
     * Sets the tenant ID.
     *
     * @param tenantID the new tenant ID
     */
    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    /**
     * Gets theOpenstack ip
     *
     * @return the Openstack ip
     */
    public String getOpenstackIP() {
        return openstackIP;
    }

    /**
     * Sets the Openstack ip name.
     *
     * @param openstackIP ip the Openstack ip
     */
    public void setOpenstackIP(String openstackIP) {
        this.openstackIP = openstackIP;
    }

    /**
     * Gets the keystone.
     *
     * @return the keystone
     */
    public Keystone getKeystone() {
        if (keystone == null) {
            keystone = new Keystone(getKeystoneAuthUrl(), connector);
            try {
                keystone.setTokenProvider(new OpenStackSimpleTokenProvider(getAccess().getToken().getId()));
            } catch (RuntimeException e) {
                keystone = null;
                throw e;
            }
        }
        return keystone;
    }

    /**
     * Gets the admin keystone.
     *
     * @return the admin keystone
     */
    public Keystone getAdminKeystone() {
        if (adminKeystone == null) {
            adminKeystone = new Keystone(getKeystoneAdminAuthUrl(), connector);
            adminKeystone.token(getAdminAccess().getToken().getId());
        }
        return adminKeystone;
    }

    /**
     * Gets the access.
     *
     * @return the access
     */
    public Access getAccess() {
        if (access == null) {
            access = getKeystone().tokens().authenticate(new UsernamePassword(getKeystoneUsername(), getKeystonePassword())).execute();
        }
        return access;
    }

    /**
     * Gets the admin access.
     *
     * @return the admin access
     */
    public Access getAdminAccess() {
        if (adminAccess == null) {
            adminAccess = getKeystone().tokens().authenticate(new TokenAuthentication(getAccess().getToken().getId())).withTenantName("admin").execute();

        }
        return adminAccess;
    }

    /**
     * Gets the swift.
     *
     * @param tenantId the tenant id
     * @return the swift
     */
    public Swift getSwift(String tenantId) {
        Swift swift = swiftMap.get(tenantId);
        if (swift == null) {
            Access access = getKeystone().tokens().authenticate(new TokenAuthentication(getAccess().getToken().getId())).withTenantId(tenantId).execute();
            // swift = new Swift(
            // KeystoneUtils.findEndpointURL(access.getServiceCatalog(),
            // "object-store", null, "public"), connector);
            String url = KeystoneUtils.findEndpointURL(access.getServiceCatalog(), "object-store", null, "public");
            // TODO: replace with configuration on server!
            int i = url.indexOf('/', 7);
            int j = url.indexOf(':', 7);
            if (j > i) {
                url = url.substring(i);
            } else {
                url = url.substring(j);
            }
            url = "http://" + this.openstackIP + url;
            // url = url.replace("swiftdemo",
            // CloudStorageApplication.OPENSTACK_IP);
            swift = new Swift(url, connector);
            swift.setTokenProvider(new OpenStackSimpleTokenProvider(access.getToken().getId()));
            swiftMap.put(tenantId, swift);
        }
        return swift;
    }

    /**
     * Mapper.
     *
     * @param type the type
     * @return the object mapper
     */
    public static ObjectMapper mapper(Class<?> type) {
        return INSTANCE.getContext(type);
    }

    /**
     * Copy stream.
     *
     * @param stream the stream
     * @return the input stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static InputStream copyStream(InputStream stream) throws IOException {
        byte[] entity = new byte[4096];
        int entitySize = 0;
        ByteArrayBuilder baos = new ByteArrayBuilder();
        while ((entitySize = stream.read(entity)) != -1) {
            baos.write(entity, 0, entitySize);
        }
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        baos.close();
        return is;
    }

    /**
     * Copy.
     *
     * @param stream the stream
     * @param os     the os
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void copy(InputStream stream, OutputStream os) throws IOException {
        byte[] entity = new byte[4096];
        int entitySize = 0;
        while ((entitySize = stream.read(entity)) != -1) {
            os.write(entity, 0, entitySize);
        }
    }

    /**
     * Checks if is logged in.
     *
     * @return true, if is logged in
     */
    public boolean isLoggedIn() {
        return access != null;
    }

    /**
     * Reset connection.
     */
    public void resetConnection() {
        this.access = null;
        this.adminAccess = null;
        this.keystone = null;
        this.adminKeystone = null;
        this.swiftMap.clear();

    }

    // ///////////////////////具体操作的APIS/////////////////////////////////////////////

    /**
     * 认证。
     *
     * @return
     */
    public Access auth() {
        return getAccess();
    }


    /**
     * 获得租户。
     *
     * @return
     */
    public Tenants getTenants() {
        return getService().getKeystone().tenants().list().execute();
    }

    /**
     * 获得当前租户。
     *
     * @return
     */
    public Tenant getTenant() {
        List<Tenant> tenants = getService().getKeystone().tenants().list().execute().getList();
        for (Tenant t : tenants) {
            if (t.getName().equals(tenantName)) {
                this.setTenantName(tenantName);
                this.setTenantID(t.getId());
                return t;
            }
        }
        return null;
    }

    /**
     * 获得当前租户对应的所有容器。
     *
     * @return
     */
    public Containers getContainers() {
        Swift swift = getService().getSwift(tenantID);
        return swift.containers().list().execute();
    }

    /**
     * 根据名称获得容器。
     *
     * @param containerName
     * @return
     */
    public Container getContainer(String containerName) {
        List<Container> tempList = getContainers().getList();

        for (Container c : tempList) {
            if (c.getName().equals(containerName)) {
                return c;
            }
        }
        return null;
    }

    /**
     * 创建容器。
     *
     * @param containerName
     * @return
     */
    public Container createContainter(String containerName) {
        return getSwift(tenantID).containers().create(containerName).execute();

    }

    /**
     * 获得容器下对象。
     *
     * @param containerName
     * @return
     */
    public Objects getObjects(String containerName) {
        return getSwift(tenantID).containers().container(containerName).list().execute();
    }

    /**
     * 复制。
     *
     * @param srcPath     原路径
     * @param desPath     目标路径
     * @param contentType 内容类型
     */
    public void copy(String containerName, String srcPath, String desPath, String contentType) {
        try {
            srcPath = URLEncoder.encode(srcPath, "utf-8");
            desPath = URLEncoder.encode(desPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getSwift(tenantID).containers().container(containerName).copy(srcPath, desPath, contentType).execute();
    }

    /**
     * 移动。
     *
     * @param srcPath     原路径
     * @param desPath     目标路径
     * @param contentType 内容类型
     */
    public void move(String containerName, String srcPath, String desPath, String contentType) {
        try {
            srcPath = URLEncoder.encode(srcPath, "utf-8");
            desPath = URLEncoder.encode(desPath, "utf-8");
            containerName = URLEncoder.encode(containerName, "utf-8");
            contentType = URLEncoder.encode(contentType, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getSwift(tenantID).containers().container(containerName).move(srcPath, desPath, contentType).execute();
    }

    /**
     * 重命名。
     *
     * @param srcPath     原路径
     * @param desPath     目标路径
     * @param contentType 内容类型
     */
    public void rename(String containerName, String srcPath, String desPath, String contentType) {
        try {
            srcPath = URLEncoder.encode(srcPath, "utf-8");
            desPath = URLEncoder.encode(desPath, "utf-8");
            containerName = URLEncoder.encode(containerName, "utf-8");
            contentType = URLEncoder.encode(contentType, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getSwift(tenantID).containers().container(containerName).rename(srcPath, desPath, contentType);
    }


    /**
     * 创建目录。
     *
     * @param containerName 当前容器
     * @param path          目录路径
     */
    public void createDirectory(String containerName, String path) {
        try {
            path = URLEncoder.encode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getSwift(tenantID).containers().container(containerName)
                .createDirectory(path).execute();
    }

    /**
     * 上传文件到容器制定Path。
     *
     * @param containerName 容器名称。
     * @param is            文件流。
     * @param contentType   文件类型。
     * @param objPath       上传位置。
     */
    public ObjectForUpload upload(String containerName, InputStream is, String contentType, String objPath) {
        try {
            containerName = URLEncoder.encode(containerName, "utf-8");
            contentType = URLEncoder.encode(contentType, "utf-8");
            objPath = URLEncoder.encode(objPath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ObjectForUpload objectForUpload = new ObjectForUpload();
        objectForUpload.setContainer(containerName);
        objectForUpload.setName(objPath);
        objectForUpload.getProperties().put("Content-Type", contentType);
        objectForUpload.setInputStream(is);
        getSwift(tenantID).containers().container(containerName).upload(objectForUpload).execute();
        // 上传成功,拷贝文件到目录
        return objectForUpload;
    }

    /**
     * 下载文件。
     *
     * @param path 要下载的对象路径
     * @return 返回存储卡本地路径
     */
    public ObjectDownload downloadObject(String containerName, String path) {
        path = converStr(path);
        containerName = converStr(containerName);
        return getSwift(tenantID).containers().container(containerName).download(path).execute();
    }

    ///////////////////对回收站功能的扩展(扩展了SDK ）放在最后讲解//////////////////////////////////

    /**
     * 删除文件和文件夹
     */
    public void recycle(String containerName, String path, String type) {
        try {
            path=URLEncoder.encode(path,"utf-8");
            type=URLEncoder.encode(type,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getSwift(tenantID).containers().container(containerName).recycle(path, type).execute();
    }
    public void restore(String containerName, String path, String type){
        try {
            path=URLEncoder.encode(path,"utf-8");
            type=URLEncoder.encode(type,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getSwift(tenantID).containers().container(containerName).restore(path,type).execute();
    }
    public void delete(String containerName, String path){
        try {
            path=URLEncoder.encode(path,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getSwift(tenantID).containers().container(containerName).delete(path).execute();
    }
    //	回收站处理
    //	RecycleDirectory()
    //	RecycleObject();
    //	emptyRecyle();
    //	deleteDirectory();
    //	deleteObject();
    //	restoreDirectory();
    //	restoreObject();
    //
    ///////////////////对回收站功能的扩展//////////////////////////////////

    /**
     * 默认是utf-8编码
     *
     * @param str
     * @return
     */
    public static String converStr(String str) {
        return converStr(str, "UTF-8");
    }

    /**
     * @param str
     * @param encode
     * @return
     */
    public static String converStr(String str, String encode) {
        if (str == null || str.equals("null")) {
            return "";
        }
        try {
            byte[] tmpbyte = str.getBytes("ISO8859_1");
            if (encode != null) {
                // 如果指定编码方式
                str = new String(tmpbyte, encode);
            } else {
                // 用系统默认的编码
                str = new String(tmpbyte);
            }
            return str;
        } catch (Exception e) {
        }
        return str;
    }

}
