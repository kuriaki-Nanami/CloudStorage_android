package com.xiandian.openstack.cloud.swiftstorage.utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

	private static FileUtils instance=null;
	private Context context;

	public FileUtils(Context _context){
		this.context=_context;
	}

	public static FileUtils getSingleton(Context _context){
		if(instance==null){
			instance=new FileUtils(_context);
		}
		return instance;
	}

	/**
	 * 获取文件名
	 * @param objectName
	 * @return
	 */
	public  String cleanName(String objectName) {
		String[] parts = objectName.split("/");
		return parts[parts.length - 1];
	}

	/**
	 * 复制单个文件
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public boolean copyFile(String oldPath, String newPath) { 
		boolean isok = true;
		try { 
			int bytesum = 0; 
			int byteread = 0; 
			File oldfile = new File(oldPath); 
			if (oldfile.exists()) { //文件存在时 
				InputStream inStream = new FileInputStream(oldPath); //读入原文件 
				FileOutputStream fs = new FileOutputStream(newPath); 
				byte[] buffer = new byte[1024]; 
				int length; 
				while ( (byteread = inStream.read(buffer)) != -1) { 
					bytesum += byteread; //字节数 文件大小 
					fs.write(buffer, 0, byteread); 
				} 
				fs.flush(); 
				fs.close(); 
				inStream.close(); 
			}
			else
			{
				isok = false;
			}
		} 
		catch (Exception e) { 
			System.out.println("复制单个文件操作出错"); 
			e.printStackTrace(); 
			isok = false;
		} 
		return isok;

	} 

	/**
	 * 复制整个文件夹内容
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public boolean copyFolder(String oldPath, String newPath) { 
		boolean isok = true;
		try { 
			(new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
			File a=new File(oldPath); 
			String[] file=a.list(); 
			File temp=null; 
			for (int i = 0; i < file.length; i++) { 
				if(oldPath.endsWith(File.separator)){ 
					temp=new File(oldPath+file[i]); 
				} 
				else
				{ 
					temp=new File(oldPath+File.separator+file[i]); 
				} 

				if(temp.isFile()){ 
					FileInputStream input = new FileInputStream(temp); 
					FileOutputStream output = new FileOutputStream(newPath + "/" + 
							(temp.getName()).toString()); 
					byte[] b = new byte[1024 * 5]; 
					int len; 
					while ( (len = input.read(b)) != -1) { 
						output.write(b, 0, len); 
					} 
					output.flush(); 
					output.close(); 
					input.close(); 
				} 
				if(temp.isDirectory()){//如果是子文件夹 
					copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]); 
				} 
			} 
		} 
		catch (Exception e) { 
			isok = false;
		} 
		return isok;
	}
	
	public boolean renameFile(String oldPath, String newPath){
		boolean isok = true;
		try { 
			File oldfile = new File(oldPath); 
			if (oldfile.exists()) { //文件存在时 
				oldfile.renameTo(new File(newPath));
			}
			else
			{
				isok = false;
			}
		} 
		catch (Exception e) { 
			System.out.println("重命名文件操作出错"); 
			e.printStackTrace(); 
			isok = false;
		} 
		return isok;
	}
}
