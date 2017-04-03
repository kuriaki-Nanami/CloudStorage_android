/*
 * Copyright (c) 2014, 2015, XIANDIAN and/or its affiliates. All rights reserved.
 * XIANDIAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.xiandian.openstack.cloud.swiftstorage.sdk.connector;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woorea.openstack.base.client.OpenStackClientConnector;
import com.woorea.openstack.base.client.OpenStackRequest;
import com.woorea.openstack.base.client.OpenStackResponse;
import com.woorea.openstack.base.client.OpenStackResponseException;
import com.xiandian.openstack.cloud.swiftstorage.sdk.service.OpenStackClientService;

/**
 * http连接类，实现OpenStackRequest 封装成HttpURLConnection请求。
 * 
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class AndroidOpenStackClientConnector implements OpenStackClientConnector {

	/** Debug Tag. */
	public static String TAG = "AndroidOpenStackClientConnector";

	/**
	 * 发起OpenStackRequest请求，转换OpenStackRequest为Java HttpURLConnection请求。
	 * 转换的内容包括方法、Head、参数、Entity、Endpoint URL。 封装成OpenStackResponse进行异步请求。
	 * HttpURLConnection.OpenConnect()建立链接。 链接建立之后设置参数。 获得响应。
	 */
	@Override
	public <T> OpenStackResponse request(OpenStackRequest<T> request) {
		// 1 封装url参数
		AndroidOpenStackResponse resp = null;
		try {
			StringBuilder queryParameters = new StringBuilder();
			for (Map.Entry<String, List<Object>> entry : request.queryParams().entrySet()) {
				for (Object o : entry.getValue()) {
					if (queryParameters.length() == 0) {
						queryParameters.append("?");
					} else {
						queryParameters.append("&");
					}
					queryParameters.append(encode(entry.getKey())).append("=").append(encode(o.toString()));
				}
			}
			// url增加参数
			String sUrl = null;
			if (!request.endpoint().endsWith("/") && !request.path().startsWith("/")) {
				sUrl = request.endpoint() + "/" + request.path();
			} else {
				sUrl = request.endpoint() + request.path();
			}
			if (queryParameters.length() > 0) {
				sUrl += queryParameters;
			}
			// 替换空格
			sUrl = sUrl.replaceAll(" ", "%20");

			// 编码utf
			sUrl = EncodingUtils.getString(sUrl.getBytes("utf-8"), "ISO-8859-1");

			// 2 创建URL
			URL url = new URL(sUrl);
			// 建立链接
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			// 链接时限
			urlConnection.setConnectTimeout(10000);
			// 头信息
			for (Map.Entry<String, List<Object>> h : request.headers().entrySet()) {
				StringBuilder sb = new StringBuilder();
				for (Object v : h.getValue()) {
					sb.append(String.valueOf(v));
				}
				urlConnection.addRequestProperty(h.getKey(), EncodingUtils.getString(sb.toString().getBytes("utf-8"), "ISO-8859-1"));
			}
			// 请求方法(put get post head)
			urlConnection.setRequestMethod(request.method().name());
			// Entity实体（post json或者流）
			if (request.entity() != null) {
				urlConnection.setDoOutput(true);
				urlConnection.setRequestProperty("Content-Type", request.entity().getContentType());
				// json
				if (request.entity().getContentType().equals("application/json")) {
					ObjectMapper mapper = OpenStackClientService.mapper(request.entity().getEntity().getClass());
					StringWriter writer = new StringWriter();
					mapper.writeValue(writer, request.entity().getEntity());
					urlConnection.getOutputStream().write(writer.toString().getBytes());
				} else {// 数据流
					OpenStackClientService.copy((InputStream) request.entity().getEntity(), urlConnection.getOutputStream());
				}
			}
			// 封装Response
			urlConnection.connect();
			resp = new AndroidOpenStackResponse(urlConnection);
			return resp;
		} catch (OpenStackResponseException osre) {
			throw osre;
		} catch (Exception e) {
			throw new OpenStackResponseException(e.getLocalizedMessage(), resp != null ? resp.getStatusCode() : -1);
		}
	}

	/**
	 * 编码为uft8.
	 * 
	 * @param param
	 *            the param
	 * @return the string
	 */
	private String encode(String param) {
		try {
			return URLEncoder.encode(param, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
