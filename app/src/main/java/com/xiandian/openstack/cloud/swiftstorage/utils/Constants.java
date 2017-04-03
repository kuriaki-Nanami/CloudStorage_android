package com.xiandian.openstack.cloud.swiftstorage.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 模拟文件系统的File Java接口定义。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class Constants {

    //类型Key
    public static final String CATEGORY_TYPE = "Category_Type";
    /**
     * 常用的文档类型格式。通常"text/"开头。再加Office文档、pdf文档。
     */
    public static final String[] MIME_DOC = {
            "text/",
            "application/pdf", //"pdf"
            "application/msword",//doc
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",//  "docx
            "application/vnd.openxmlformats-officedocument.wordprocessingml.template",//        "dotx
            "application/vnd.ms-excel",//xls
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",//            "xlsx
            "application/vnd.openxmlformats-officedocument.spreadsheetml.template",//       "xltx
            "application/vnd.ms-powerpoint",//ppt
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",// "pptx"
            "application/vnd.openxmlformats-officedocument.presentationml.template",//       "potx"
            "application/xhtml+xml"//xhtml
    };

    /**
     * 常用的图片类型格式，通常"image/"开头。
     */
    public static final String[] MIME_IMAGE = {
            "image/"
    };

    /**
     * 常用的视频类型格式，通常以"image/"开头。
     */
    public static final String[] MIME_VIDEO = {
            "video/"
    };

    /**
     * 常用的音频类型格式，通常以"audio/"开头。
     */
    public static final String[] MIME_AUDIO = {
            "audio/"
    };



}
