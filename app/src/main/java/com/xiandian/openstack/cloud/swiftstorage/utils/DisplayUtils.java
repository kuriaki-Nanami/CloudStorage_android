package com.xiandian.openstack.cloud.swiftstorage.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 用于显示的帮助类。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class DisplayUtils {

    // debug信息
    private static String TAG = DisplayUtils.class.getSimpleName();
    // 文件大小信息
    private static final String[] sizeSuffixes = { "B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };

    // 数据类型格式转换
    private static HashMap<String, String> mimeType2HumanReadable;
    static {
        mimeType2HumanReadable = new HashMap<String, String>();
        // images
        mimeType2HumanReadable.put("image/jpeg", "JPEG image");
        mimeType2HumanReadable.put("image/jpg", "JPEG image");
        mimeType2HumanReadable.put("image/png", "PNG image");
        mimeType2HumanReadable.put("image/bmp", "Bitmap image");
        mimeType2HumanReadable.put("image/gif", "GIF image");
        mimeType2HumanReadable.put("image/svg+xml", "JPEG image");
        mimeType2HumanReadable.put("image/tiff", "TIFF image");
        // music
        mimeType2HumanReadable.put("audio/mpeg", "MP3 music file");
        mimeType2HumanReadable.put("application/ogg", "OGG music file");
    }

    private static final String TYPE_APPLICATION = "application";
    private static final String TYPE_AUDIO = "audio";
    private static final String TYPE_IMAGE = "image";
    private static final String TYPE_TXT = "text";
    private static final String TYPE_VIDEO = "video";

    private static final String SUBTYPE_PDF = "pdf";
    private static final String[] SUBTYPES_DOCUMENT = { "msword", "vnd.openxmlformats-officedocument.wordprocessingml.document", "vnd.oasis.opendocument.text",
            "rtf" };
    private static Set<String> SUBTYPES_DOCUMENT_SET = new HashSet<String>(Arrays.asList(SUBTYPES_DOCUMENT));
    private static final String[] SUBTYPES_SPREADSHEET = { "msexcel", "vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "vnd.oasis.opendocument.spreadsheet" };
    private static Set<String> SUBTYPES_SPREADSHEET_SET = new HashSet<String>(Arrays.asList(SUBTYPES_SPREADSHEET));
    private static final String[] SUBTYPES_PRESENTATION = { "mspowerpoint", "vnd.openxmlformats-officedocument.presentationml.presentation",
            "vnd.oasis.opendocument.presentation" };
    private static Set<String> SUBTYPES_PRESENTATION_SET = new HashSet<String>(Arrays.asList(SUBTYPES_PRESENTATION));
    private static final String[] SUBTYPES_COMPRESSED = { "x-tar", "x-gzip", "zip" };
    private static final Set<String> SUBTYPES_COMPRESSED_SET = new HashSet<String>(Arrays.asList(SUBTYPES_COMPRESSED));
    private static final String SUBTYPE_OCTET_STREAM = "octet-stream";
    private static final String EXTENSION_RAR = "rar";
    private static final String EXTENSION_RTF = "rtf";
    private static final String EXTENSION_3GP = "3gp";

    /**
     * 转换文件大小bytes到可以读格式。
     *
     * @param bytes  文件大小
     * @return
     */
    public static String bytesToHumanReadable(long bytes) {
        double result = bytes;
        int attachedsuff = 0;
        while (result > 1024 && attachedsuff < sizeSuffixes.length) {
            result /= 1024.;
            attachedsuff++;
        }
        result = ((int) (result * 100)) / 100.0;
        return result + " " + sizeSuffixes[attachedsuff];
    }

    /**
     * 删除HTML的entities
     *
     * @param s
     * @return
     */
    public static String HtmlDecode(String s) {
        String ret = "";
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '%') {
                ret += (char) Integer.parseInt(s.substring(i + 1, i + 3), 16);
                i += 2;
            } else {
                ret += s.charAt(i);
            }
        }
        return ret;
    }

    /**
     * 转换MIME类型可读。
     *
     * @param mimetype
     * @return
     */
    public static String convertMIMEtoPrettyPrint(String mimetype) {
        if (mimeType2HumanReadable.containsKey(mimetype)) {
            return mimeType2HumanReadable.get(mimetype);
        }
        if (mimetype.split("/").length >= 2)
            return mimetype.split("/")[1].toUpperCase() + " file";
        return "Unknown type";
    }

    public static String getExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1);

        return extension;
    }

    /**
     * 转换时间格式可读。
     *
     * @param milliseconds，起点01/01/1970
     * @return
     */
    public static String unixTimeToHumanReadable(long milliseconds) {
        Date date = new Date(milliseconds);
        return date.toLocaleString();
    }
    /**
     * 获取APK的图标。
     *
     * @param context
     * @param apkPath
     * @return
     */
    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 输入流写到输出流中.
     *
     * @param stream
     * @param is
     */
    public static void write(OutputStream stream, InputStream is) {
        try {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                stream.write(buffer, 0, len);
            }
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * Gets the camera photo orientation.
     *
     * @param context
     *            the context
     * @param imageUri
     *            the image uri
     * @param imagePath
     *            the image path
     * @return the camera photo orientation
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static int getCameraPhotoOrientation(Context context, Uri imageUri,
                                                String imagePath) throws IOException {
        int rotate = 0;
        context.getContentResolver().notifyChange(imageUri, null);
        File imageFile = new File(imagePath);

        ExifInterface exif = new android.media.ExifInterface(
                imageFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        return rotate;
    }

    /**
     * Gets the orientation.
     *
     * @param context
     *            the context
     * @param imageUri
     *            the image uri
     * @return the orientation
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static int getOrientation(Context context, Uri imageUri)
            throws IOException {
        String[] filePathColumn = { MediaStore.MediaColumns.DATA };
        Cursor cursor = context.getContentResolver().query(imageUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return getCameraPhotoOrientation(context, imageUri, filePath);
    }

    /**
     * Gets the original file path.
     *
     * @param context
     *            the context
     * @param imageUri
     *            the image uri
     * @return the original file path
     */
    public static String getOriginalFilePath(Context context, Uri imageUri) {
        if (imageUri.getScheme().equals("file")) {
            return imageUri.getPath();
        }
        String[] filePathColumn = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DATA };
        Cursor cursor = context.getContentResolver().query(imageUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        return cursor.getString(columnIndex);
    }

}
