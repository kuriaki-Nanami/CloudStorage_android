package com.woorea.openstack.swift.api;

import com.woorea.openstack.base.client.Entity;
import com.woorea.openstack.base.client.HttpMethod;
import com.woorea.openstack.base.client.OpenStackClient;
import com.woorea.openstack.base.client.OpenStackRequest;
import com.woorea.openstack.base.client.OpenStackResponse;
import com.woorea.openstack.swift.model.ObjectDownload;
import com.woorea.openstack.swift.model.ObjectForUpload;
import com.woorea.openstack.swift.model.Objects;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;


public class ContainerResource {
    private final OpenStackClient CLIENT;
    private String container;

    public ContainerResource(OpenStackClient client, String container) {
        CLIENT = client;
        this.container = container;
    }

    public List list() {
        return new List(container, null);
    }

    public List list(String containerName) {
        return new List(containerName, null);
    }

    public CreateDirectory createDirectory(String path) {
        return new CreateDirectory(container, path);
    }

    public Show show(String path) {
        return new Show(container, path);
    }

    public Upload upload(ObjectForUpload objectForUpload) {
        return new Upload(objectForUpload);
    }

    public Download download(String path) {
        return new Download(container, path);
    }

    public Delete delete(String path) {
        return new Delete(container, path);
    }

    public Copy copy(String path, String path2, String type) {
        return new Copy(container, path, path2, type);
    }

    public Move move(String path, String path2, String type) {
        return new Move(container, path, path2, type);
    }

    public Rename rename(String path, String path2, String type) {
        return new Rename(container, path, path2, type);
    }

    public Recycle recycle(String path, String type) {
        return new Recycle(container, path, type);
    }

    public Restore restore(String path, String type) {
        return new Restore(container, path, type);
    }

    public class List extends OpenStackRequest<Objects> {
        public List(String containerName, Map<String, String> filters) {
            super(CLIENT, HttpMethod.GET, container + "/", null,
                    Objects.class);
        }
    }

    public class CreateDirectory extends OpenStackRequest<Void> {
        public CreateDirectory(String container, String path) {
            super(CLIENT, HttpMethod.PUT, buildPath(container, "/", path),
                    Entity.json("*"), null);
        }
    }

    public class Show extends OpenStackRequest<Object> {
        public Show(String containerName, String objectName) {
            super(CLIENT, HttpMethod.GET,
                    buildPath(containerName, "/", objectName), Entity.json("*"),
                    Object.class);
        }
    }

    public class Upload extends OpenStackRequest<OpenStackResponse> {
        public Upload(ObjectForUpload objectForUpload) {
            super(CLIENT, HttpMethod.PUT,
                    buildPath(objectForUpload.getContainer(), "/",
                            objectForUpload.getName()),
                    new Entity<InputStream>(objectForUpload.getInputStream(),
                            objectForUpload.getProperties().get("Content-Type")
                                    .toString()), null);

            for (String key : objectForUpload.getProperties().keySet()) {
                header("x-object-meta-" + key,
                        objectForUpload.getProperties().get(key));
            }
        }
    }

    public class Download extends OpenStackRequest<ObjectDownload> {
        public Download(String containerName, String objectName) {
            super(CLIENT, HttpMethod.GET,
                    buildPath(containerName, "/", objectName), null,
                    ObjectDownload.class);
        }
    }

    public class Delete extends OpenStackRequest<Void> {
        public Delete(String containerName, String objectName) {
            super(CLIENT, HttpMethod.DELETE,
                    buildPath(containerName, "/", objectName), null, null);
        }
    }

    public class Copy extends OpenStackRequest<Void> {
        public Copy(String containerName, String objectName, String objectName2, String type) {
            super(CLIENT, HttpMethod.PUT,
                    buildPath(containerName, "/", objectName2), null, null); // 目标文件
            header("X-Copy-From", buildPath(containerName, "/", objectName)); // 源文件
            header("Content-Length", 0);
            header("Content-Type", type);
        }
    }

    public class Move extends OpenStackRequest<Void> {
        public Move(String containerName, String objectName, String objectName2, String type) {
            OpenStackRequest<Void> openStackRequest;
            openStackRequest = new OpenStackRequest<Void>(CLIENT, HttpMethod.PUT,
                    buildPath(containerName, "/", objectName2), null, null); // 目标文件

            openStackRequest.header("X-Copy-From", buildPath(containerName, "/", objectName)); // 源文件
            openStackRequest.header("Content-Length", 0);
            openStackRequest.header("Content-Type", type);

            openStackRequest.execute();

            openStackRequest = new OpenStackRequest<Void>(CLIENT, HttpMethod.DELETE,
                    buildPath(containerName, "/", objectName), null, null);
            openStackRequest.execute();
        }
    }

    public class Rename extends OpenStackRequest<Void> {
        public Rename(String containerName, String objectName, String objectName2, String type) {
            OpenStackRequest<Void> openStackRequest;
            openStackRequest = new OpenStackRequest<Void>(CLIENT, HttpMethod.PUT,
                    buildPath(containerName, "/", objectName2), null, null); // 目标文件
            openStackRequest.header("X-Copy-From", buildPath(containerName, "/", objectName)); // 源文件
            openStackRequest.header("Content-Length", 0);
            openStackRequest.header("Content-Type", type);

            openStackRequest.execute();

            openStackRequest = new OpenStackRequest<Void>(CLIENT, HttpMethod.DELETE,
                    buildPath(containerName, "/", objectName), null, null);
            openStackRequest.execute();
        }
    }

    public class Recycle extends OpenStackRequest<Void> {
        public Recycle(String containerName, String objectName, String type) {
            OpenStackRequest<Void> openStackRequest;
            openStackRequest = new OpenStackRequest<Void>(CLIENT, HttpMethod.PUT,
                    buildPath("garbage_" + containerName, "/", objectName), null, null); // 目标文件
            openStackRequest.header("X-Copy-From", buildPath(containerName, "/", objectName)); // 源文件
            openStackRequest.header("Content-Length", 0);
            openStackRequest.header("Content-Type", type);

            openStackRequest.execute();

            openStackRequest = new OpenStackRequest<Void>(CLIENT, HttpMethod.DELETE,
                    buildPath(containerName, "/", objectName), null, null);
            openStackRequest.execute();
        }
    }

    public class Restore extends OpenStackRequest<Void> {
        public Restore(String containerName, String objectName, String type) {
            OpenStackRequest<Void> openStackRequest;
            openStackRequest = new OpenStackRequest<Void>(CLIENT, HttpMethod.PUT,
                    buildPath(containerName, "/", objectName), null, null); // 目标文件

            openStackRequest.header("X-Copy-From", buildPath("garbage_" + containerName, "/", objectName)); // 源文件
            openStackRequest.header("Content-Length", 0);
            openStackRequest.header("Content-Type", type);

            openStackRequest.execute();

            openStackRequest = new OpenStackRequest<Void>(CLIENT, HttpMethod.DELETE,
                    buildPath("garbage_" + containerName, "/", objectName), null, null);
            openStackRequest.execute();
        }
    }
}
