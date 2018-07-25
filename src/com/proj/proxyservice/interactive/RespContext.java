package com.proj.proxyservice.interactive;

public interface RespContext {
	/**
	 * 获取响应的信息
	 * 
	 * @return
	 */
	public String getRespInfo();

	public void setRespInfo(String respInfo);

	/**
	 * 获取响应的报文类型
	 * 
	 * @return
	 */
	public String getRespContentType();

	public void setRespContentType(String respContentType);
}
