package com.xiandian.openstack.cloud.swiftstorage.sdk.test;

import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.Token;
import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.model.Container;
import com.woorea.openstack.swift.model.Containers;
import com.woorea.openstack.swift.model.Object;
import com.woorea.openstack.swift.model.Objects;
import com.xiandian.openstack.cloud.swiftstorage.sdk.service.OpenStackClientService;

import java.util.Iterator;

/**
 * OpenStack接口验证。
 * 重点测试Keystone认证和Swift云存储操作。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class OpenStackSDKTest {


    //(1) 常量，根据开发环境而定，包括 IP地址，用户，密码，租户，容器名称，keystone endpoint地址
    private static String OPENSTACK_IP = "58.214.31.6";//"192.168.1.200";//"58.214.31.6";
    private static String OPENSTACK_USER_NAME = "gw050";
    private static String OPENSTACK_USER_PW= "000000";
    private static String OPENSTACK_TENANE_NAME= "gw050";
    private static String OPENSTACK_SWIFT_CONTAINER= "gw050";
    private static String keystoneAuthUrl="http://"+OPENSTACK_IP+":5000/v2.0";



    /**
     * 测试。
     */
    public static Access test()
    {

        try
        {
            //System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

            //(2)  创建服务
            OpenStackClientService service = OpenStackClientService.getInstance();
            service.setKeystoneAuthUrl(keystoneAuthUrl);
            service.setKeystoneAdminAuthUrl(keystoneAuthUrl);
            service.setKeystoneEndpoint(keystoneAuthUrl);
            service.setTenantName(OPENSTACK_TENANE_NAME);
            service.setKeystonePassword(OPENSTACK_USER_PW);
            service.setKeystoneUsername(OPENSTACK_USER_NAME);
            service.setOpenstackIP(OPENSTACK_IP);

            //(3) Keystone认证服务测试
            Keystone keystone = service.getKeystone();
            Access access = service.getAccess();
            Token token = access.getToken();

            System.out.println("Auth token:" +token.getId());

            //3 获取租户和租户ID

            Tenant tenant = service.getTenant();
            System.out.println("Tenant Name:" + tenant.getName() + ", ID:" +tenant.getId());

            //(4) Swift 云存储服务

            //(4.1) 获取Swift服务
            Swift swift = service.getSwift(tenant.getId());

            //(4.2) 获取租户下面的所有容器

            Containers cts = service.getContainers();
            Iterator<Container> ct = cts.iterator();
            while (ct.hasNext())
            {
                Container container = ct.next();
                System.out.println("container:" + container.getName());
            }

            //(4.3)  获取容器下面所有对象
            Objects objs = service.getObjects(OPENSTACK_SWIFT_CONTAINER);
            Iterator<Object> obj = objs.iterator();
            while (obj.hasNext())
            {
                Object ob = obj.next();
                System.out.println("object:" + ob.getName() + " ContentType:" + ob.getContentType());
            }

            return access;
        }
        catch(Exception exp)
        {
            exp.printStackTrace();
            return null;
        }

    }

    /**
     * 测试。
     * @param as
     */
    public static void main(String[] as)
    {

        try
        {
            //System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

            //(2)  创建服务
            OpenStackClientService service = OpenStackClientService.getInstance();
            service.setKeystoneAuthUrl(keystoneAuthUrl);
            service.setKeystoneAdminAuthUrl(keystoneAuthUrl);
            service.setKeystoneEndpoint(keystoneAuthUrl);
            service.setTenantName(OPENSTACK_TENANE_NAME);
            service.setKeystonePassword(OPENSTACK_USER_PW);
            service.setKeystoneUsername(OPENSTACK_USER_NAME);
            service.setOpenstackIP(OPENSTACK_IP);

            //(3) Keystone认证服务测试
            Keystone keystone = service.getKeystone();
            Token token = service.getAccess().getToken();

            System.out.println("Auth token:" +token.getId());

            //3 获取租户和租户ID

            Tenant tenant = service.getTenant();
            System.out.println("Tenant Name:" + tenant.getName() + ", ID:" +tenant.getId());

            //(4) Swift 云存储服务

            //(4.1) 获取Swift服务
            Swift swift = service.getSwift(tenant.getId());

            //(4.2) 获取租户下面的所有容器

            Containers cts = service.getContainers();
            Iterator<Container> ct = cts.iterator();
            while (ct.hasNext())
            {
                Container container = ct.next();
                System.out.println("container:" + container.getName());
            }

            //(4.3)  获取容器下面所有对象
            Objects objs = service.getObjects(OPENSTACK_SWIFT_CONTAINER);
            Iterator<Object> obj = objs.iterator();
            while (obj.hasNext())
            {
                Object ob = obj.next();
                System.out.println("object:" + ob.getName() + " ContentType:" + ob.getContentType());
            }
            //(4.4)  基本一、操作复制，改名，移动（复制saas/insaas.txt，到paas/insaas.txt下，然后改名为inpaas.txt)



//            service.copy(OPENSTACK_SWIFT_CONTAINER, "saas/insaas.txt", "paas/insaas.txt", "ContentType:text/plain");


            //(4.5)基本二、上传和下载









        }
        catch(Exception exp)
        {
            exp.printStackTrace();
        }

    }


}
