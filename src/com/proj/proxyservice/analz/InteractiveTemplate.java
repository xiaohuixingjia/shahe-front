package com.proj.proxyservice.analz;

import org.apache.mina.filter.codec.http.MutableHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proj.proxyservice.Constant;
import com.proj.proxyservice.HttpBean;
import com.proj.proxyservice.interactive.CheckContext;
import com.proj.proxyservice.interactive.ReqContext;
import com.proj.proxyservice.interactive.impl.Context;
import com.proj.proxyservice.ruleTimeService.RuleTimeService;
import com.proj.proxyservice.util.ConfigParamsUtil;
import com.proj.proxyservice.util.GetRequestInfoUtil;

public abstract class InteractiveTemplate implements Interactive{
	
	protected final static Logger log = LoggerFactory.getLogger("InteractiveTemplate");
	private final static Logger ipAuthSimpInfo = LoggerFactory.getLogger("ipAuthSimpInfo");

	enum urlType{
		serverUrl{

			@Override
			String urlKey() {
				return "serverUrl";
			}
			
		},
		transferUrl{

			@Override
			String urlKey() {
				return "transferUrl";
			}
			
		};
		abstract String urlKey();
	}
	@Override
	public HttpBean interactive(MutableHttpRequest request, String reqUrl, String SwiftNumber) throws Exception{
		Context context = createCommonContext(request, reqUrl, SwiftNumber);
		analzReqInfo(context);
		boolean check = check(context);
		return execute(check,context);
	}

	/**
	 * 判断交互
	 * @param check
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	private HttpBean execute(boolean check, Context context) throws Exception{
		String url="";
		if(check){
			//获取黑盒的url
			 url =packageUrl(context,urlType.serverUrl.urlKey());
		}else{
			//获取中诚信的url
			 url = packageUrl(context,urlType.transferUrl.urlKey());
		}		
		return send(url,context,check);
	}

	/**
	 * 发送
	 * @param context
	 * @return
	 */
	protected abstract HttpBean send(String url,Context context,boolean flag) throws Exception;

	/**
	 * 校验ip以及商户号是否合格
	 * @param context
	 * @return
	 */
	private boolean check(CheckContext context) {
		
		boolean commonAuth = RuleTimeService.getRts().getIpConfing().commonAuth(context);
		ipAuthSimpInfo.info(context.getReqIp() + Constant.COMMA_SEPARATOR + context.getMerId()
				+ Constant.COMMA_SEPARATOR + commonAuth);	
		return commonAuth;
	}

	/**
	 * 解析请求流中的报文到上下文中 
	 * @param context
	 * @param request
	 */
	protected abstract void analzReqInfo(ReqContext context)throws Exception;

	/**
	 * 封装公共的请求参数信息
	 * @param request
	 * @param reqUrl
	 * @param SwiftNumber
	 * @return
	 */
	private Context createCommonContext(MutableHttpRequest request, String reqUrl, String SwiftNumber) {
		Context context = new Context(SwiftNumber);
		context.setReqIp(GetRequestInfoUtil.getReqIp(request));
		context.setReqContentType(request.getContentType());
		context.setReqUrl(reqUrl);
		context.setReqMethod(request.getMethod());
		context.setMutableHttpRequest(request);
		return context;
	}
	
	protected String packageUrl(Context context,String urlKey) {
		return ConfigParamsUtil.getProp(urlKey) + GetRequestInfoUtil.decode(context.getRequest().getRequestUri().toString());
	}

}
