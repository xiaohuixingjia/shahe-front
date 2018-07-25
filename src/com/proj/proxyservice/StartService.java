package com.proj.proxyservice;import java.net.URISyntaxException;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import com.bs3.ioc.core.BeansContext;import com.proj.proxyservice.ruleTimeService.RuleTimeService;public class StartService {	private final static Logger log = LoggerFactory.getLogger("StartService");	public static void main(String[] args) throws URISyntaxException, Exception {		try {			RuleTimeService.getRts();			BeansContext ctx = BeansContext.getInstance();			ctx.setMappingFile("resource/mina2niocs.properties");			ctx.start();			log.info("初始化服务成功");		} catch (Exception e) {			log.error("初始化服务失败", e);		}	}}