package com.proj.proxyservice.config;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proj.proxyservice.analz.Interactive;
import com.proj.proxyservice.exception.BaseException;
import com.proj.proxyservice.exception.ErrorCode;
import com.proj.proxyservice.ruleTimeService.InitService;
import com.proj.proxyservice.util.PropertiesUtil;

public class UrlConfig implements InitService {
	private final static Logger _log = LoggerFactory.getLogger("UrlConfig");
	private final static String DEFAULT = "*";
	/**
	 * 根据不同url获取不同的交互的模板
	 */
	private Map<String, Interactive> map;

	public Interactive getInteractiveTemplate(String url) {
		if (map.containsKey(url)) {
			return map.get(url);
		}
		return map.get(DEFAULT);
	}

	@Override
	public void init() throws Exception {
		map = new HashMap<String, Interactive>();
		try {
			Properties properties = PropertiesUtil.getInstance("urlConfig.properties").getProperties();
			Enumeration<?> enu2 = properties.propertyNames();
			while (enu2.hasMoreElements()) {
				String key = (String) enu2.nextElement();
				String value = properties.getProperty(key).trim();
				_log.info("加载的url配置信息：" + key + " 对应的模板处理类 " + value.toString());
				map.put(key, (Interactive) Class.forName(value).newInstance());
			}
		} catch (Exception e) {
			throw new BaseException(ErrorCode.ERROR_OTHERS, "初始化url配置信息失败", e);
		}
	}

	@Override
	public void clear() throws Exception {
		map.clear();
	}
}
