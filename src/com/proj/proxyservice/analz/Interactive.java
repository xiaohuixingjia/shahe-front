package com.proj.proxyservice.analz;

import org.apache.mina.filter.codec.http.MutableHttpRequest;

import com.proj.proxyservice.HttpBean;

public interface Interactive {
	/**
	 * 交互获取响应结果
	 * 
	 * @param request
	 *            请求流
	 * @param reqUrl
	 *            请求路径
	 * @param SwiftNumber
	 *            生成的唯一流水
	 * @return
	 */
	public HttpBean interactive(MutableHttpRequest request, String reqUrl, String SwiftNumber) throws Exception;
}
