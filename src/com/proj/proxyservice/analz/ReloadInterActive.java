package com.proj.proxyservice.analz;

import org.apache.mina.filter.codec.http.MutableHttpRequest;

import com.proj.proxyservice.Constant;
import com.proj.proxyservice.HttpBean;
import com.proj.proxyservice.ruleTimeService.RuleTimeService;

public class ReloadInterActive implements Interactive{

	@Override
	public HttpBean interactive(MutableHttpRequest request, String reqUrl, String SwiftNumber) {
		return new HttpBean(reload(), Constant.TEST_PLAN);
	}

	private String reload() {
		if (RuleTimeService.getRts().reload()) {
			return "reloadSuccess";
		} else {
			return "reloadError";
		}
	}
	
}
