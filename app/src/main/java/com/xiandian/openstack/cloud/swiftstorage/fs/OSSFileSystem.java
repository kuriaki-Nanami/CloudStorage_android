package com.xiandian.openstack.cloud.swiftstorage.fs;

import android.text.TextUtils;

import com.woorea.openstack.swift.model.Objects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装模拟的文件系统的一个节点，如果节点是Root，就是整个树。
 *  对象存储默认每个对象都是一样，没有目录和文件的区分，
 *  都是一个path。 每个Path点由对应的数据和属性。
 *  这里模拟封装了文件系统。
 * 一个文件系统有root的树形结构，有目录和文件区分，目录和文件都有树形。
 * 当前节点由Parent，有children，children包括文件或目录。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class OSSFileSystem implements SFile {

    /** 父节点. */
    private SFile parent;
    /** 当前节点（path）的目录. 以名称为Key，建立Map表 */
    private Map<String, SFile> directories = new LinkedHashMap<String, SFile>();
    /** 当前节点（path）的文件. 以名称为Key，建立Map表 */
    private Map<String, SFile> files = new LinkedHashMap<String, SFile>();
    /** 当前对象 */
    private com.woorea.openstack.swift.model.Object swiftObj;

    /**
     * 初始化文件系统。
     *
     * @param parent            父节点
     * @param childPath       子路径
     */
    public OSSFileSystem(OSSFileSystem parent, String childPath) {
        this.parent = parent;
        //元数据？
        com.woorea.openstack.swift.model.Object object = new com.woorea.openstack.swift.model.Object();
        object.setName(childPath);
        this.setSwiftObject(object);
    }

    /**
     *  获取当前节点下的目录。
     * @return
     */
    @Override
    public Collection<SFile> listDirectories() {
        return directories.values();
    }

    /**
     * 获取当前节点下的文件。
     * @return
     */
    @Override
    public Collection<SFile> listFiles() {
        return files.values();
    }


    /**
     * 增加文件。
     * @param name
     * @param obj
     */
    @Override
    public void putFile(String name, SFile obj) {
        files.put(name, obj);
    }

    /**
     * 增加目录。
     * @param name
     * @param obj
     */
    @Override
    public void putDirectory(String name, SFile obj) {
        directories.put(name, obj);
    }

    /**
     * 获取当前文件或目录对应的云存储对象。
     * @return
     */
    @Override
    public com.woorea.openstack.swift.model.Object getSwiftObject() {
        return swiftObj;
    }

    /**
     *设置当前文件或目录对应的云存储对象。
     * @return
     */
    @Override
    public boolean hasData() {
        return swiftObj !=null;
    }

    /**
     * 获得名称（包含路径）。
     * @return
     */
    @Override
    public String getName()
    {
        return swiftObj.getName();
    }


    /**
     * 获得最后修改时间。
     *
     * @return
     */
    @Override
    public Calendar getLastModified()
    {
        return swiftObj.getLastModified();
    }


    /**
     *设置当前文件或目录对应的云存储对象。
     * @param metaData
     */
    public void setSwiftObject(com.woorea.openstack.swift.model.Object metaData) {
        this.swiftObj = metaData;
    }

	/*
	 * 调试信息。
	 *
	 * @see java.lang.Object#toString()
	 */

    public String toString() {
        return toString("  ");
    }

    /**
     * 调试信息。
     *
     * @param ident
     *            the ident
     * @return the string
     */
    public String toString(String ident) {
        StringBuilder builder = new StringBuilder();
        builder.append(getSwiftObject()).append("\n");
        for (Map.Entry<String, SFile> entry : files.entrySet()) {
            builder.append(ident).append("file ").append(entry.getValue()).append("\n");
        }
        for (Map.Entry<String, SFile> children : directories.entrySet()) {
            builder.append(ident).append("dir ").append(children.getValue().toString(ident + "   "));
        }
        return builder.toString();
    }


    /**
     * 获得父节点。
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
        SFile p = this;
        while (p.getParent() != null) {
            p = p.getParent();
        }
        return p;
    }

    /**
     * 内容类型。（http contenttype）
     * @return
     */
    @Override
    public String getContentType() {

        return swiftObj.getContentType();
    }

    /**
     * 长度，自生的长度，目录为0，文件有自己大小。
     * @return
     */
    @Override
    public int getSize() {

        return 0;
    }

    /**
     * 判断是文件还是文件夹。
     */
    public boolean isFile() {
        return false;
    }

    /////////////////utils methods/////////////////////////

    /**
     * 读取云存储对象。从Root开始读取，即容器顶部。
     * 构造成一个Tree结构。
     *
     * @param objects
     *            the objects
     * @return the swift pseudo file system
     */
    public static SFile readFromObjects(Objects objects) {
        //跟
        OSSFileSystem fs = new OSSFileSystem(null, "");
        //遍历对象
        for (com.woorea.openstack.swift.model.Object object : objects) {
            String name = object.getName();
            //名称，如果是root下的文件
            if (!name.contains("/")) {
                fs.putFile(name, new OSSFile(object));
            } else {
                //以“/"结尾认为是目录
                if (name.endsWith("/")) {
                    //该目录是否存在？
                    SFile targetDirectory = findOrCreateChild(fs, name);
                    targetDirectory.setSwiftObject(object);
                } else {//子目录下的文件
                    String[] path = name.split("/");
                    String directory = "";
                    for (int i = 0; i < path.length - 1; i++) {
                        directory += path[i] + "/";
                    }
                    //寻找目录，目录是否存在，如果存在当前文件加入到目录
                    OSSFileSystem targetDirectory = (OSSFileSystem)findChild(fs, directory);
                    if (targetDirectory != null)
                    {
                        targetDirectory.files.put(name, new OSSFile(object));
                    }
                    else {
                        targetDirectory = (OSSFileSystem)findChild(fs, "/");
                        targetDirectory.files.put(name, new OSSFile(object));
                    }
                }
            }
        }
        //reset leaf(OSSFile)'s parent
        resetOSSFileParent(fs);
        return fs;
    }

    /**
     * 重置所有文件的父目录。
     */
    private static void resetOSSFileParent(SFile root)
    {
        //子目录文件夹
        Collection<SFile> swiftFolders = root.listDirectories();
        Collection<SFile> swiftFiles = new ArrayList<SFile>(root.listFiles());
        //文件
        for (SFile file :  swiftFiles) {
            file.setParent(root);
        }
        //文件夹
        for (SFile dir :  swiftFolders) {
            resetOSSFileParent(dir);
        }
    }

    /**
     * 查找子路径对应的节点。
     *
     * @param root
     * @param childPath
     * @return the swift pseudo file system
     */
    public static SFile findOrCreateChild(SFile root, String childPath) {
        OSSFileSystem currentLevel = (OSSFileSystem)root;
        String[] path = childPath.split("/");
        for (int i = 0; i < path.length; i++) {
            if (!currentLevel.directories.containsKey(path[i])) {
                currentLevel.directories.put(path[i], new OSSFileSystem(currentLevel, childPath));
            }
            currentLevel = (OSSFileSystem)currentLevel.directories.get(path[i]);
        }
        return currentLevel;
    }

    /**
     * Find child.
     *
     * @param root
     *            the root
     * @param childPath
     *            the child path
     * @return the swift pseudo file system
     */
    public static SFile findChild(SFile root, String childPath) {
        OSSFileSystem currentLevel = (OSSFileSystem)root;
        if (childPath.trim().length() == 0) {
            return root;
        }
        String[] path = childPath.split("/");
        for (int i = 0; i < path.length; i++) {
            if (!currentLevel.directories.containsKey(path[i])) {
                return null;
            }
            currentLevel = (OSSFileSystem)currentLevel.directories.get(path[i]);
        }
        return currentLevel;
    }


    /**
     * 收集所有符合该类型的文件。
     *
     * @param collection
     * @param root the root
     * @param contentTypes the child path
     */
    public static void findFiles(List<SFile> collection, SFile root, String[] contentTypes) {
        Collection<SFile> subDirs = root.listDirectories();
        if (subDirs != null)
        {
            for (SFile subDir :  subDirs)
            {
                findFiles(collection, subDir, contentTypes);
            }
        }
        Collection<SFile> subFiles = root.listFiles();
        if (subFiles != null)
        {
            for (SFile subFile :  subFiles)
            {
               if (isContentType(subFile.getContentType(), contentTypes))
               {
                   collection.add(subFile);
               }
            }
        }
    }


    /**
     * 判断一个文件是否是某种文件类型。
     *
     * @param contentType
     * @param contentTypes
     * @return
     */
    public static boolean isContentType(String contentType, String[] contentTypes)
    {
        if (contentType == null || contentType == null || contentType.length() == 0)
        {
            return false;
        }
        for (int i = 0; i < contentTypes.length; i++)
        {
            //contentTypes包含即可，比如"image/"
            if (contentType.toLowerCase().contains(contentTypes[i]))
            {
                return true;
            }
        }
        return false;
    }



    /**
     * 搜索文件或文件夹（注意重复）。
     *
     * @param collection
     * @param root the root
     * @param name the child path
     */
    public static void searchFile(List<SFile> collection, SFile root, String name) {
        Collection<SFile> subDirs = root.listDirectories();
        if (subDirs != null)
        {
            for (SFile subDir :  subDirs)
            {
                searchFile(collection, subDir, name);
            }
        }
        Collection<SFile> subFiles = root.listFiles();
        if (subFiles != null)
        {
            for (SFile subFile :  subFiles)
            {
                //如果名称包含，包含路径，并且没有放入就加入
                if (isContain(subFile,  name) && !collection.contains(subFile))
                {
                    collection.add(subFile);
                }
            }
        }
    }


    /**
     * 判断。
     *
     * @param file
     * @param name
     * @return
     */
    public static boolean isContain(SFile file, String name)
    {
        if (file == null || TextUtils.isEmpty(name))
        {
            return false;
        }
        //此处简单处理，是否包含“name”，找通配符正则表达式的匹配方式
        if (file.getName().toLowerCase().contains(name.toLowerCase()))
        {
            return true;
        }
        return false;
    }
}