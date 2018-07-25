package com.proj.proxyservice.interactive;

public interface CheckContext {
	/**
	 * 校验用的ip
	 * 
	 * @return
	 */
	public String getReqIp();

	public void setReqIp(String reqIp);

	/**
	 * 校验用的商户号
	 * 
	 * @return
	 */
	public String getMerId();

	public void setMerId(String merId);
}
