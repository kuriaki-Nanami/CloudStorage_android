/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.openstack.cloud.swiftstorage.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

/**
 * 错误输出帮助类
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class PromptDialogUtil {

	/**
	 * Show error dialog.
	 * 
	 * @param context
	 *            the context
	 * @param title
	 *            the title
	 * @param e
	 *            the e
	 * @param onOK
	 *            the on ok
	 */
	public static void showErrorDialog(final Context context, String title,
			Exception e, final Intent onOK) {
		showDialog(context, title, e.getMessage() != null ? e.getMessage() : e
				.getClass().getName(), onOK);
	}

	/**
	 * Show error dialog.
	 * 
	 * @param context
	 *            the context
	 * @param ressource
	 *            the ressource
	 * @param e
	 *            the e
	 * @param onOK
	 *            the on ok
	 */
	public static void showErrorDialog(final Context context, int ressource,
			Exception e, final Intent onOK) {
		e.printStackTrace();
		Toast.makeText(context, "错误: " + e.getLocalizedMessage(),
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Show dialog.
	 * 
	 * @param context
	 *            the context
	 * @param title
	 *            the title
	 * @param message
	 *            the message
	 * @param onOK
	 *            the on ok
	 */
	public static void showDialog(final Context context, String title,
			String message, final Intent onOK) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		if (onOK != null) {
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							context.startActivity(onOK);
							return;
						}
					});
		}
		alertDialog.show();
	}

}
