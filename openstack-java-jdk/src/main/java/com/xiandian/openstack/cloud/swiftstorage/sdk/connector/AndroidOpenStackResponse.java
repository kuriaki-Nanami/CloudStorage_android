/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.openstack.cloud.swiftstorage.sdk.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woorea.openstack.base.client.OpenStackResponse;
import com.woorea.openstack.base.client.OpenStackResponseException;
import com.woorea.openstack.swift.model.ObjectDownload;
import com.xiandian.openstack.cloud.swiftstorage.sdk.service.OpenStackClientService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Http请求响应类。
 * 
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class AndroidOpenStackResponse implements OpenStackResponse {

	/** 调试Tag. */
	public static String TAG = "AndroidOpenStackResponse";
	/** The url connection. */
	private HttpURLConnection urlConnection;
	/** The status phrase. */
	private String statusPhrase;
	/** The status code. */
	private int statusCode;
	/** The headers. */
	private Map<String, String> headers;
	/** 输入流 */
	private InputStream is;

	/**
	 * HttpURLConnection链接响应，封装出OpenStackResponse对象。
	 * 
	 * @param urlConnection      Url链接
	 * @throws IOException        响应异常
	 */
	public AndroidOpenStackResponse(HttpURLConnection urlConnection) throws IOException {
		// 1链接
		this.urlConnection = urlConnection;
		try {
			this.statusCode = urlConnection.getResponseCode();
		} catch (IOException ioe) {
			if ("No authentication challenges found".equals(ioe.getMessage())) {
				this.statusCode = 401;
			}
		}
		try {
			//响应信息
			this.statusPhrase = urlConnection.getResponseMessage();
			//响应头信息
			headers = new HashMap<String, String>();
			for (Entry<String, List<String>> iterable_element : urlConnection.getHeaderFields().entrySet()) {
				for (String value : iterable_element.getValue()) {
					headers.put(iterable_element.getKey(), value);
				}
			}
			//流
			is = OpenStackClientService.copyStream(urlConnection.getInputStream());
		} catch (IOException e) {
			throw new OpenStackResponseException(getStatusPhrase(), getStatusCode());
		} finally {
			try {
				urlConnection.disconnect();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获得实体。
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getEntity(Class<T> returnType) {
		//错误
		if (getStatusCode() >= 400) {
			throw new OpenStackResponseException(getStatusPhrase(), getStatusCode());
		}
		//对象下载
		if (returnType.equals(ObjectDownload.class)) {
			ObjectDownload objectDownload = new ObjectDownload();
			objectDownload.setInputStream(getInputStream());
			return (T) objectDownload;
		} else {//Json数据
			ObjectMapper mapper = OpenStackClientService.mapper(returnType);
			try {
				return mapper.readValue(getInputStream(), returnType);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	/**
	 * 获得输入流。
	 */
	@Override
	public InputStream getInputStream() {
		return is;
	}
	/**
	 * 获得头部属性值。
	 */
	@Override
	public String header(String name) {
		return headers.get(name);
	}
	/**
	 * 获得头部信息。
	 */
	@Override
	public Map<String, String> headers() {
		return headers;
	}

	/**
	 * Gets the url connection.
	 * 
	 * @return the url connection
	 */
	public HttpURLConnection getUrlConnection() {
		return urlConnection;
	}
	/**
	 * Sets the url connection.
	 * 
	 * @param urlConnection
	 *            the new url connection
	 */
	public void setUrlConnection(HttpURLConnection urlConnection) {
		this.urlConnection = urlConnection;
	}

	/**
	 * Gets the status code.
	 * 
	 * @return the status code
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code.
	 * 
	 * @param statusCode
	 *            the new status code
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Gets the status phrase.
	 * 
	 * @return the status phrase
	 */
	public String getStatusPhrase() {
		return statusPhrase;
	}

}
