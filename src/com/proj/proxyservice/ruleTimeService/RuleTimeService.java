package com.proj.proxyservice.ruleTimeService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proj.proxyservice.config.IpConfing;
import com.proj.proxyservice.config.UrlConfig;

public final class RuleTimeService {
	private final static Logger _log = LoggerFactory.getLogger(RuleTimeService.class);
	/**
	 * 当前线程是否是重载线程
	 */
	public final static ThreadLocal<Boolean> currentIsReloadThread = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return new Boolean(Boolean.FALSE);
		}
	};
	/* 实时对象 */
	private Map<Class<? extends InitService>, Object> ruleTimeInstanceMap;
	/* 备份对象 */
	private Map<Class<? extends InitService>, Object> reserveInstanceMap;
	/* 由实时服务初始化的配置类 */
	private Set<Class<? extends InitService>> initClasssSet;
	@SuppressWarnings("unchecked")
	private static RuleTimeService ruleTimeService = new RuleTimeService(
			new Class[] { IpConfing.class, UrlConfig.class });

	/*
	 * 是否在使用实时通道标识
	 */
	public AtomicBoolean canReadRuntime = new AtomicBoolean(true);

	public static RuleTimeService getRts() {
		return ruleTimeService;
	}

	private RuleTimeService(Class<? extends InitService>[] initClasss) {
		try {
			initRuleTime(initClasss);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void initRuleTime(Class<? extends InitService>[] initClasss) throws Exception {
		ruleTimeInstanceMap = new HashMap<Class<? extends InitService>, Object>();
		if (initClasss == null || initClasss.length == 0) {
			throw new Exception("实时加载服务创建失败，需要实时加载的类为空");
		}
		initClasssSet = new HashSet<Class<? extends InitService>>();
		for (Class<? extends InitService> class1 : initClasss) {
			try {
				InitService newInstance = class1.newInstance();
				newInstance.init();
				initClasssSet.add(class1);
				ruleTimeInstanceMap.put(class1, newInstance);
			} catch (Exception e) {
				throw new Exception("配置类初始化失败", e);
			}
		}
	}

	public <T> T getRuleTimeService(Class<T> class1) throws Exception {
		if (canReadRuntime.get()) {
			return getT(ruleTimeInstanceMap, class1);
		} else {
			_log.info("当前使用备份对象");
			return getT(reserveInstanceMap, class1);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getT(Map<Class<? extends InitService>, Object> map, Class<T> class1) throws Exception {
		if (map.containsKey(class1)) {
			return (T) map.get(class1);
		}
		throw new Exception("配置类未找到");
	}

	@SuppressWarnings("unchecked")
	private void to_reload() throws Exception {
		// 备份当前数据信息
		reserveInstanceMap = ruleTimeInstanceMap;
		// 设置当前通道信息为不可读
		canReadRuntime.set(false);
		// 休息一秒让所有现有的请求线程走完对 ruleTimeInstance 的使用
		sleep(2000);
		// 将集合中的
		Class<? extends InitService>[] arr = new Class[initClasssSet.size()];
		this.initClasssSet.toArray(arr);
		// 重新加载数据库中的配置
		initRuleTime(arr);
		// 设置当前通道信息为可读
		canReadRuntime.set(true);
		// 休息一秒让所有现有的请求线程走完对 reserveMapInfo 的使用
		sleep(2000);
		// 清理无用的引用
		for (Entry<Class<? extends InitService>, Object> entry : reserveInstanceMap.entrySet()) {
			((InitService) entry.getValue()).clear();
		}
		reserveInstanceMap.clear();

	}

	/**
	 * 线程休息 mills毫秒
	 * 
	 * @param mills
	 */
	private static void sleep(long mills) {
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 重载请求，已加锁，防止并发访问
	 * 
	 * @return true--重载成功 false--重载失败
	 */
	public synchronized boolean reload() {
		if (canReadRuntime.get()) {
			// 标记当前线程为重载线程
			currentIsReloadThread.set(Boolean.TRUE);
			try {
				to_reload();
				return true;
			} catch (Exception e) {
				_log.error("重载出现异常：", e);
			} finally {
				// 标记当前线程重载结束
				currentIsReloadThread.set(Boolean.FALSE);
			}
		}
		return false;
	}

	/**
	 * 获取ip配置信息
	 * 
	 * @return
	 */
	public IpConfing getIpConfing() {
		try {
			return getRuleTimeService(IpConfing.class);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取URL配置信息
	 * 
	 * @return
	 */
	public UrlConfig getUrlConfig() {
		try {
			return getRuleTimeService(UrlConfig.class);
		} catch (Exception e) {
		}
		return null;
	}
}
