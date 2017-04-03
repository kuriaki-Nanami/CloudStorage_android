/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.openstack.cloud.swiftstorage.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

public class FileIconHelper {
	//图片工具类
	private Context mContext;
	private static final int MICRO_KIND = 3;
	
	public FileIconHelper(Context _context){
		this.mContext=_context;
	}

    public Bitmap getImageThumbnail(String path) {
		// 图片缩略图
    	Bitmap bitmap = null;
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inPreferredConfig = Bitmap.Config.ARGB_4444;
    	options.inPurgeable = true;
    	options.inInputShareable = true;
    	options.inJustDecodeBounds = true;
    	bitmap = BitmapFactory.decodeFile(path, options);
    	options.inJustDecodeBounds = false; // 设为 false
    	// 计算缩放比
    	int h = options.outHeight;
    	int w = options.outWidth;
    	int beWidth = w / 32;
    	int beHeight = h / 32;
    	int be = 1;
    	if (beWidth < beHeight) {
    		be = beWidth;
    	} else {
    		be = beHeight;
    	}
    	if (be <= 0) {
    		be = 1;
    	}
    	options.inSampleSize = be;
    	try {
			bitmap=BitmapFactory.decodeFile(path,options);
			bitmap=ThumbnailUtils.extractThumbnail(
					bitmap, 32, 32, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
    }

    public Bitmap getVideoThumbnail(String path) {
    	Bitmap bitmap=null;
    	try {
			bitmap=ThumbnailUtils.createVideoThumbnail(path,MICRO_KIND);
			bitmap = ThumbnailUtils.extractThumbnail(
					bitmap, 32, 32, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return bitmap;
    }
    
    public void destoryBimap(Bitmap mBitmap) {
		// TODO Auto-generated method stub
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}
    


}
