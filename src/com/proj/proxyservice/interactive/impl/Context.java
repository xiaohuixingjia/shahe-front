package com.proj.proxyservice.interactive.impl;

import java.util.List;
import java.util.Map;

import org.apache.mina.filter.codec.http.HttpMethod;
import org.apache.mina.filter.codec.http.MutableHttpRequest;

import com.proj.proxyservice.interactive.CheckContext;
import com.proj.proxyservice.interactive.InterActiveContext;

public class Context implements InterActiveContext, CheckContext {
	private String reqIp;
	private String reqUrl;
	private Map<String, List<String>> params;
	private String content;
	private HttpMethod reqMethod;
	private String ReqContentType;
	private String respInfo;
	private String respContentType;
	private String merId;
	private String swiftNum;
	private String X_forward_info;
	private MutableHttpRequest request;

	public Context(String swiftNum) {
		super();
		this.swiftNum = swiftNum;
	}

	public String getReqIp() {
		return reqIp;
	}

	public void setReqIp(String reqIp) {
		this.reqIp = reqIp;
	}

	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}

	public Map<String, List<String>> getParams() {
		return params;
	}

	public void setParams(Map<String, List<String>> params) {
		this.params = params;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public HttpMethod getReqMethod() {
		return reqMethod;
	}

	public void setReqMethod(HttpMethod reqMethod) {
		this.reqMethod = reqMethod;
	}

	public String getReqContentType() {
		return ReqContentType;
	}

	public void setReqContentType(String reqContentType) {
		ReqContentType = reqContentType;
	}

	public String getRespInfo() {
		return respInfo;
	}

	public void setRespInfo(String respInfo) {
		this.respInfo = respInfo;
	}

	public String getRespContentType() {
		return respContentType;
	}

	public void setRespContentType(String respContentType) {
		this.respContentType = respContentType;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getSwiftNum() {
		return swiftNum;
	}

	public void setSwiftNum(String swiftNum) {
		this.swiftNum = swiftNum;
	}

	@Override
	public MutableHttpRequest getRequest() {
		return request;
	}

	@Override
	public void setMutableHttpRequest(MutableHttpRequest request) {
		this.request = request;
	}

	@Override
	public String getX_forward_info() {
		return this.X_forward_info;
	}

	@Override
	public void setX_forward_info(String X_forward_info) {
		this.X_forward_info = X_forward_info;
	}

}
