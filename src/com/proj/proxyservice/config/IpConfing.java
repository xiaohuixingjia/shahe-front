package com.proj.proxyservice.config;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proj.proxyservice.Constant;
import com.proj.proxyservice.exception.BaseException;
import com.proj.proxyservice.exception.ErrorCode;
import com.proj.proxyservice.interactive.CheckContext;
import com.proj.proxyservice.ruleTimeService.InitService;
import com.proj.proxyservice.util.PropertiesUtil;

public class IpConfing implements InitService{
	private final static Logger _log = LoggerFactory.getLogger("UrlConfing");

	private Map<String, List<String>> ipConfigMap;

	public IpConfing(){
	}

	private void initUrlConfigMap() throws BaseException {
		try {
			ipConfigMap = new HashMap<String, List<String>>();
			Properties properties = PropertiesUtil.getInstance("writeIpList.properties").getProperties();
			Enumeration<?> enu2 = properties.propertyNames();
			while (enu2.hasMoreElements()) {
				String key = (String) enu2.nextElement();
				String value = properties.getProperty(key).trim();
				if(ipConfigMap.get(key)!=null){
					ipConfigMap.get(key).addAll(Arrays.asList(value.split(Constant.COMMA_SEPARATOR)));
				}else{
					List<String> list=Arrays.asList(value.split(Constant.COMMA_SEPARATOR));
					ipConfigMap.put(key, list);
				}
				_log.info("加载的ip信息："+key+" "+value.toString());
			}
		} catch (Exception e) {
			throw new BaseException(ErrorCode.ERROR_OTHERS, "初始化ip白名单配置信息失败", e);
		}
	}

	/**
	 * 判断 ip 是否可用
	 * 
	 * @param requInfoBean
	 * @return
	 */
	public boolean commonAuth(CheckContext context) {
		String ip = context.getReqIp();
		String merId = context.getMerId();
		boolean containsKey = ipConfigMap.containsKey(ip);
		containsKey=containsKey&&ipConfigMap.get(ip).contains(merId);
		if(containsKey){
			_log.info(ip+"通过，配置信息："+ipConfigMap.get(ip)+"请求携带的商户号："+merId);
		}else{
			_log.info(ip+"不通过，配置信息："+ipConfigMap.get(ip)+"请求携带的商户号："+merId);
		}
		return containsKey;
	}


	@Override
	public void init() throws Exception{
		initUrlConfigMap();		
	}

	@Override
	public void clear() {
		ipConfigMap.clear();
	}
}
