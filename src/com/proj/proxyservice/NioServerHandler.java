package com.proj.proxyservice;

import org.apache.mina.filter.codec.http.MutableHttpRequest;
import org.apache.mina.filter.codec.http.MutableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bs3.inf.IProcessors.HSessionInf;
import com.bs3.nio.mina2.Mina2H4Rpc2;
import com.bs3.nio.mina2.codec.IHttp;
import com.proj.proxyservice.ruleTimeService.RuleTimeService;
import com.proj.proxyservice.util.ConfigParamsUtil;
import com.proj.proxyservice.util.GetRequestInfoUtil;
import com.proj.proxyservice.util.MonitorUtil;
import com.proj.proxyservice.util.SeqUtil;
import com.proj.proxyservice.util.TimeCountUtil;

public class NioServerHandler extends Mina2H4Rpc2 {

	private final static Logger _log = LoggerFactory.getLogger("NioServerHandler");

	@Override
	protected void onServerReadReq(HSessionInf session, Object req) {
		// 初始化
		TimeCountUtil.setStartTime();
		// 唯一标识
		String SwiftNumber = SeqUtil.getSeq();
		MutableHttpRequest request = (MutableHttpRequest) req;
		String requestURL = getReqUrl(request);
		HttpBean interactive = null;
		try {
			interactive = RuleTimeService.getRts().getUrlConfig().getInteractiveTemplate(requestURL)
					.interactive(request, requestURL, SwiftNumber);
		} catch (Exception e) {
			MonitorUtil.addError();
			interactive = new HttpBean("{\"resCode\":\"9999\"}", Constant.APPLICATION_JSON);
			_log.error(getSimpInfo(request, requestURL) + "请求出现异常", e);
		}

		_log.info("ldSwiftNumber:{}**共耗时:" + TimeCountUtil.getTimeConsuming() + ":*****返回给商户侧报文:*****\n"
				+ interactive.getResponseStr(), SwiftNumber);
		MonitorUtil.queryEnd(TimeCountUtil.getTimeConsuming(), SwiftNumber);
		this.responseContent(session, interactive, SwiftNumber);
	}

	/**
	 * 获取请求的简要信息
	 * 
	 * @param req
	 * @return
	 */
	private String getSimpInfo(MutableHttpRequest request, String requestURL) {
		StringBuilder builder = new StringBuilder();
		builder.append("请求的路径：" + requestURL + " 请求的类型：" + request.getMethod().name() + " 请求的报文类型："
				+ request.getContentType() + " 请求的get报文：" + request.getParameters() + " 请求的post类型的报文："
				+ GetRequestInfoUtil.getPostInfoWithNotException(request));
		return builder.toString();
	}

	/**
	 * 获取请求的url路径
	 * 
	 * @param request
	 * @return
	 */
	public String getReqUrl(MutableHttpRequest request) {
		String requestURL = request.getRequestUri().getPath();
		_log.info("*****接收到商户侧请求路径：{}: ", requestURL);
		if ("true".equals(ConfigParamsUtil.getProp("isTast")) && requestURL.endsWith("/test")) {
			requestURL = requestURL.substring(0, requestURL.lastIndexOf("/test"));
		}
		return requestURL;
	}

	/**
	 * 返回响应给商户的方法
	 * 
	 * @param session
	 * @param responseStr
	 */
	private void responseContent(HSessionInf session, HttpBean httpBean, String transId) {
		try {
			/* 第四步：返回 */
			_log.info("transId:" + transId + "返回的信息类型：" + httpBean.getContentType() + "返回的报文如下:\n"
					+ httpBean.getResponseStr());
			MutableHttpResponse res = IHttp.makeResp(new IHttp.HResponse(), IHttp.HConst.SC_OK, "", null,
					httpBean.getContentType() != null ? httpBean.getContentType() : Constant.TEST_PLAN,
					httpBean.getResponseStr() != null ? httpBean.getResponseStr().getBytes() : "".getBytes());
			session.write(res);
		} catch (Exception e) {
			_log.error("", e);
			session.close("");
		}
	}

}
