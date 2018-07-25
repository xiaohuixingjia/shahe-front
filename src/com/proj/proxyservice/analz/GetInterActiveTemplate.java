package com.proj.proxyservice.analz;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.proj.proxyservice.Constant;
import com.proj.proxyservice.HttpBean;
import com.proj.proxyservice.interactive.ReqContext;
import com.proj.proxyservice.interactive.impl.Context;
import com.proj.proxyservice.util.HttpClientPoolUtil;

public class GetInterActiveTemplate extends InteractiveTemplate{

	@Override
	protected void analzReqInfo(ReqContext context) {
		context.setParams(context.getRequest().getParameters());
		List<String> list = context.getParams().get(Constant.ACCOUNT);
		if(CollectionUtils.isEmpty(list)){
			context.setMerId("");
		}else{
			context.setMerId(list.get(0));
		}
	}

	@Override
	protected HttpBean send(String url,Context context,boolean flag) {
		if(flag){
			url=url+Constant.YU_SEPARATOR+Constant.LDSWIFTNUMBER+Constant.EQUAL_SEPARATOR+context.getSwiftNum();
		}
		log.info("url : {}", url);
		return HttpClientPoolUtil.sendByGet(url, "text/html", context.getReqIp(), context.getX_forward_info());
	}

}
