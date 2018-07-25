package com.proj.proxyservice.ruleTimeService;

public interface InitService {
	
	/**
	 * 初始化缓存
	 */
	public void init() throws Exception;
	/**
	 * 清理缓存
	 */
	public void clear() throws Exception;
}
