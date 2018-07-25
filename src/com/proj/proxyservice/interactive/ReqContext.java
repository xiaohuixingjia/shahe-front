package com.proj.proxyservice.interactive;

import java.util.List;
import java.util.Map;

import org.apache.mina.filter.codec.http.HttpMethod;
import org.apache.mina.filter.codec.http.MutableHttpRequest;

public interface ReqContext {
	/**
	 * 获取请求的ip
	 * 
	 * @return
	 */
	public String getReqIp();

	public void setReqIp(String reqIp);

	/**
	 * 获取X_forward_info
	 * 
	 * @return
	 */
	public String getX_forward_info();

	public void setX_forward_info(String X_forward_info);

	/**
	 * 获取请求的url
	 * 
	 * @return
	 */
	public String getReqUrl();

	public void setReqUrl(String reqUrl);

	/**
	 * 获取get形式提交的信息
	 * 
	 * @return
	 */
	public Map<String, List<String>> getParams();

	public void setParams(Map<String, List<String>> params);

	/**
	 * 获取Post形式提交的信息
	 * 
	 * @return
	 */
	public String getContent();

	public void setContent(String content);

	/**
	 * 获取请求信息提交的类型
	 * 
	 * @return
	 */
	public HttpMethod getReqMethod();

	public void setReqMethod(HttpMethod reqMethod);

	/**
	 * 获取请求的报文类型
	 * 
	 * @return
	 */
	public String getReqContentType();

	public void setReqContentType(String ReqContentType);

	/**
	 * 获取请求流
	 * 
	 * @return
	 */
	public MutableHttpRequest getRequest();

	public void setMutableHttpRequest(MutableHttpRequest request);
	
	/**
	 * 校验用的商户号
	 * 
	 * @return
	 */
	public String getMerId();

	public void setMerId(String merId);

}
