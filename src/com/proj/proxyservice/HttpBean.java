package com.proj.proxyservice;

public class HttpBean {
	private String responseStr;
	private String contentType;
	public HttpBean(){}
	public HttpBean(String responseStr, String contentType) {
		super();
		this.responseStr = responseStr;
		this.contentType = contentType;
	}
	public String getResponseStr() {
		return responseStr;
	}
	public void setResponseStr(String responseStr) {
		this.responseStr = responseStr;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
}
