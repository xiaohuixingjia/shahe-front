package com.proj.proxyservice.analz;

import org.apache.mina.filter.codec.http.MutableHttpRequest;

import com.proj.proxyservice.Constant;
import com.proj.proxyservice.HttpBean;
import com.proj.proxyservice.util.MonitorUtil;
public class MonitorInterActive implements Interactive{

	@Override
	public HttpBean interactive(MutableHttpRequest request, String reqUrl, String SwiftNumber) {
		return new HttpBean(MonitorUtil.clearAndGetMonitorInfo(), Constant.TEST_PLAN);
	}

}
