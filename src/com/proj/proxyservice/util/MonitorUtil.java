package com.proj.proxyservice.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorUtil {
	private final static Logger _log = LoggerFactory.getLogger("MonitorInfo");

	/**
	 * 请求错误次数
	 */
	private static final AtomicInteger errorTimes=new AtomicInteger(0);
	/**
	 * 超时次数
	 */
	private static final AtomicInteger timeOutTimes=new AtomicInteger(0);
	
	/**
	 * 增加请求错误的次数
	 */
	public static void addError(){
		_log.info("请求出现异常，异常次数增加");
		errorTimes.incrementAndGet();
	}
	
	/**
	 * 增加请求超时次数
	 */
	public static void addTimeOutTimes(){
		timeOutTimes.incrementAndGet();
	}
	
	/**
	 * 请求结束 
	 * @param time
	 * @param id
	 */
	public static void queryEnd(long time,String id){
		try {
			if((time/1000)>=CastUtil.string2int(ConfigParamsUtil.getProp("monitor_timeOutNum"), 5)){
				_log.info(id+"超时："+time);
				 timeOutTimes.incrementAndGet();
			}
		} catch (Exception e) {
			_log.error(id+"计算超时错误",e);
		}
	}
	
	/**
	 * 获取请求错误的次数以及超时的次数并清空
	 * @return
	 */
	public static String clearAndGetMonitorInfo(){
		return errorTimes.getAndSet(0)+","+timeOutTimes.getAndSet(0);
	}
}
