package com.proj.proxyservice.analz;

import com.proj.proxyservice.Constant;
import com.proj.proxyservice.HttpBean;
import com.proj.proxyservice.interactive.ReqContext;
import com.proj.proxyservice.interactive.impl.Context;
import com.proj.proxyservice.util.GetRequestInfoUtil;
import com.proj.proxyservice.util.HttpClientPoolUtil;

import net.sf.json.JSONObject;

public class PostInterActiveTemplate extends InteractiveTemplate{


	@Override
	protected void analzReqInfo(ReqContext context) throws Exception {
		context.setContent(GetRequestInfoUtil.getPostInfo(context.getRequest()));
		JSONObject jsonObject = JSONObject.fromObject(context.getContent());
		context.setMerId(jsonObject.get(Constant.ACCOUNT).toString());
	}

	@Override
	protected HttpBean send(String url,Context context,boolean flag) throws Exception {
		log.info("url : {}", url);
		String content=context.getContent();
		if(flag){
			JSONObject jsonObject = JSONObject.fromObject(context.getContent());
			jsonObject.put(Constant.LDSWIFTNUMBER,context.getSwiftNum());
			content=jsonObject.toString();
		}
		return HttpClientPoolUtil.sendByPost(url, content,context.getReqContentType());
	}

}
