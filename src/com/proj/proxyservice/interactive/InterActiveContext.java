package com.proj.proxyservice.interactive;

public interface InterActiveContext extends ReqContext, RespContext {
	/**
	 * 获取唯一主键
	 * 
	 * @return
	 */
	public String getSwiftNum();

	public void setSwiftNum(String swiftNum);
}
