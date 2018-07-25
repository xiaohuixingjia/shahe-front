package com.proj.proxyservice.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.http.MutableHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proj.proxyservice.Constant;

public class GetRequestInfoUtil {
	private final static Logger log = LoggerFactory.getLogger("NioServerHandler");

	/**
	 * 获取请求ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getReqIp(MutableHttpRequest request) {
		String ip = request.getHeader(Constant.X_Real_IP);
		if (StringUtils.isEmpty(ip)) {
			ip = "unknow";
		}
		return ip;
	}

	public static String getPostInfo(MutableHttpRequest request) throws Exception {
		IoBuffer ioBuffer = (IoBuffer) request.getContent();
		byte[] conBytes = new byte[ioBuffer.limit()];
		ioBuffer.get(conBytes);
		String reqInfo = URLDecoder.decode(new String(conBytes), "utf-8");
		return reqInfo;
	}

	public static String getPostInfoWithNotException(MutableHttpRequest request) {
		try {
			return getPostInfo(request);
		} catch (Exception e) {
			log.error("获取post报文失败",e);
		}
		return "";
	}
	
	public static String decode(String reqInfo){
		try {
			if (StringUtils.isNotEmpty(reqInfo)) {
				reqInfo = decode(reqInfo, Constant.UTF8);
			}
		} catch (Exception e) {
			log.error("解码失败,原信息：" + reqInfo, e);
		}
		return reqInfo;
	}

	public static String decode(String reqInfo, String code) throws UnsupportedEncodingException {
			return URLDecoder.decode(reqInfo, code);
	}

}
