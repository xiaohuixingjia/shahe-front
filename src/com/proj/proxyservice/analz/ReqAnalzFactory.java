package com.proj.proxyservice.analz;

import java.net.URLDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.http.MutableHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proj.proxyservice.interactive.ReqContext;
import com.proj.proxyservice.util.GetRequestInfoUtil;

public class ReqAnalzFactory {
	private final static Logger _log = LoggerFactory.getLogger("UrlAnalzFactory");

	public static void analzInfo2reqContext(MutableHttpRequest request,ReqContext context,String url) throws Exception {
		context.setReqIp(GetRequestInfoUtil.getReqIp(request));
		context.setReqContentType(request.getContentType());
		context.setReqUrl(url);
		context.setReqMethod(request.getMethod());
		context.setParams(request.getParameters());
		try {
			IoBuffer ioBuffer = (IoBuffer) request.getContent();
			byte[] conBytes = new byte[ioBuffer.limit()];
			ioBuffer.get(conBytes);
			String reqInfo = URLDecoder.decode(new String(conBytes), "utf-8");
			context.setContent(reqInfo);
		} catch (Exception e) {
			_log.error("解析post请求信息失败：",e);
			throw e;
		}
		
	}
}
