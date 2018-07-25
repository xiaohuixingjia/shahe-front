package com.proj.proxyservice.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSession;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;
import com.proj.proxyservice.Constant;
import com.proj.proxyservice.HttpBean;

@Deprecated
public class HttpClientUtil {
	private final static Logger log = LoggerFactory.getLogger("HttpClientUtil");

    public final static  String CONTENT_TYPE_APPLICATION_JSON=MediaType.JSON_UTF_8.toString(); 
	
    static{
    	HttpsURLConnection.setDefaultHostnameVerifier(new AllowAllHostnameVerifier());
    	SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
    }
    
	private HttpClientUtil() {
	}
	
	
	/**
	 * 默认的请求超时时间，3秒
	 */
	public final static int DEFAULT_REQUEST_TIMEOUT = 1*1000;
	/**
	 * 默认的响应超时时间，3秒
	 */
	public final static int READ_TIME_OUT = 3*1000;
	
	
	
	/**
	 * 
	 * @param url
	 * @param content
	 * @param contentType
	 * @return
	 */
	public static  String sendParmByPost(String url, Map<String, String> map,String contentType) {
	
	    
	    HttpPost httpPost = new HttpPost(url);
		// 设置请求和传输超时时间
//		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeOut)
//				.setConnectTimeout(connectTimeOut).build();
//		httpPost.setConfig(requestConfig);
		// 将请求参数封装的对象解析为 name-value对应的集合
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String value = entry.getValue();
			String key = entry.getKey();
			formparams.add(new BasicNameValuePair(key, value));
		}
		// 将解析后的请求参数放入post对象中
		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		httpPost.setEntity(uefEntity);
		
	    return sendHttpsRequestByPost(httpPost,contentType);
	    //return sendRequest(httpPost,resEntity,headers,true);
	}
	
	
	
	/**
	 * 
	 * @param url
	 * @param content
	 * @param contentType
	 * @return
	 */
	public static  String sendByPost(String url, String content,String contentType) {
		StringEntity resEntity=new StringEntity(content,Consts.UTF_8);
	    List<Header> headers=new ArrayList<Header>();
	    Header header=null;
	    if(!Strings.isNullOrEmpty(contentType)){
	        header=new BasicHeader("Content-type",contentType) ;
	        headers.add(header);
	    }
		HttpPost httpPost = null;
	    httpPost = new HttpPost(url);
	    httpPost.setEntity(resEntity);
	    return sendHttpsRequestByPost(httpPost,contentType);
	    //return sendRequest(httpPost,resEntity,headers,true);
	}
	
	
	/**
	 * 
	 * @param url
	 * @param content
	 * @param contentType
	 * @return
	 */
	public static  HttpBean sendByGet(String url, String contentType,  String x_real_ip, String x_forward_info) {
		HttpGet httpGet =  new HttpGet(url);
	    return sendHttpsRequestByGet(httpGet, contentType, x_real_ip, x_forward_info);
//	    return sendRequest(httpGet,resEntity,headers,true);
	}
	
	
	
	/**
	 * 发送HTTPS	POST请求
	 * 
	 * @param 要访问的HTTPS地址,POST访问的参数Map对象
	 * @return  返回响应值
	 * */
	public static final String sendHttpsRequestByPost(HttpPost httpPost,String contentType) {
		String responseContent = null;
		@SuppressWarnings("resource")
		HttpClient httpClient = new DefaultHttpClient();
		//创建TrustManager
		X509TrustManager xtm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		//这个好像是HOST验证
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
			public void verify(String arg0, SSLSocket arg1) throws IOException {}
			public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {}
			public void verify(String arg0, X509Certificate arg1) throws SSLException {}
		};
		try {
			//TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");
			//使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm }, null);
			//创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			socketFactory.setHostnameVerifier(hostnameVerifier);
			//通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
			//HttpPost httpPost = new HttpPost(url);
/*			List<NameValuePair> formParams = new ArrayList<NameValuePair>(); // 构建POST请求的表单参数
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));*/
			httpPost.addHeader("Content-type", contentType);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity(); // 获取响应实体
			if (entity != null) {
				responseContent = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (Exception e) {
			MonitorUtil.addError();
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			httpClient.getConnectionManager().shutdown();
		}
		return responseContent;
	}	
	
	
	/**
	 * 发送HTTPS	POST请求
	 * 
	 * @param 要访问的HTTPS地址,POST访问的参数Map对象
	 * @return  返回响应值
	 * */
	public static final HttpBean sendHttpsRequestByGet(HttpGet httpGet,String contentType, String x_real_ip, String x_forward_info) {
		log.info("sendHttpsRequestByGet");
		HttpBean httpBean = new HttpBean();
		String responseContent = null;
		@SuppressWarnings("resource")
		HttpClient httpClient = new DefaultHttpClient();
	    RequestConfig config = RequestConfig.custom().setConnectTimeout(DEFAULT_REQUEST_TIMEOUT)
	                .setSocketTimeout(READ_TIME_OUT).build();
        httpGet.setConfig(config);
        
		//创建TrustManager
		X509TrustManager xtm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		//这个好像是HOST验证
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
			public void verify(String arg0, SSLSocket arg1) throws IOException {}
			public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {}
			public void verify(String arg0, X509Certificate arg1) throws SSLException {}
		};
		try {
			//TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");
			//使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm }, null);
			//创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			socketFactory.setHostnameVerifier(hostnameVerifier);
			//通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
			//HttpPost httpPost = new HttpPost(url);
/*			List<NameValuePair> formParams = new ArrayList<NameValuePair>(); // 构建POST请求的表单参数
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));*/
			httpGet.addHeader("Content-type", contentType);
			httpGet.setHeader(Constant.X_Real_IP, x_real_ip);
			httpGet.setHeader(Constant.X_FORWARDED_FOR, x_forward_info);
			
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity(); // 获取响应实体
			if (entity != null) {
				
				responseContent = EntityUtils.toString(entity, "UTF-8");
				ContentType contentTypeInfo = ContentType.getOrDefault(entity);
		        httpBean.setContentType(contentTypeInfo.getMimeType());
		        
				httpBean.setResponseStr(responseContent);
			}
		} catch (Exception e) {
			MonitorUtil.addError();
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			httpClient.getConnectionManager().shutdown();
		}
		return httpBean;
	}	
	
	
   /**
    * 
    * @param httpRequest
    * @param entity
    * @param headers
    * @param checkResponseStatus
    * @return
    */
	@SuppressWarnings("unused")
	private static  String sendRequest(HttpRequestBase httpRequest, HttpEntity  entity, List<Header> headers,boolean checkResponseStatus) {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(DEFAULT_REQUEST_TIMEOUT)
                .setSocketTimeout(READ_TIME_OUT).build();
        httpRequest.setConfig(config);
		httpRequest.setHeader("User-Agent","okHttp");
        if(httpRequest instanceof HttpEntityEnclosingRequestBase){
            checkArgument(null!=entity,"HttpEntity请求体不能为空");
            ((HttpEntityEnclosingRequestBase)httpRequest).setEntity(entity);
        }
        if(null!=headers){
            //添加请求头
            for (Header header : headers) {
                httpRequest.addHeader(header);
            }
        }
        //CloseableHttpClient httpClient = HttpClients.createDefault();
        
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String resString=null;
        try {
        	HttpsURLConnection.setDefaultHostnameVerifier(new AllowAllHostnameVerifier());
        	//SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
        	SSLContextBuilder builder = new SSLContextBuilder();
        	builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        	SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
        	httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();        
            response = httpClient.execute(httpRequest);
            HttpEntity resEntity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();
            resString=EntityUtils.toString(resEntity,"UTF-8");
            if (checkResponseStatus) {
            	checkArgument(Objects.equal(statusCode, HttpStatus.SC_OK),"响应码状态不是200");
            }
            
            return resString;
        } catch (Exception e) {
        	log.error("请求出现异常：",e);
            throw new RuntimeException(resString, e);
        } finally {
            try {
                if (response != null) {
                        response.close();
                }
                if (httpRequest != null) {
                    httpRequest.releaseConnection();
                }
                if (httpClient != null) {
                        httpClient.close();
                }
            } catch (IOException e) {
            	log.error("关闭连接异常：",e);

            }
        }
    }


	public static String sendByPost(String url, String content) {
		return sendByPost(url, content, CONTENT_TYPE_APPLICATION_JSON);
	}
	
	
	public static void main(String[] args) {
		String url = "https://122.112.76.59:443/data-service/auth/cncm/t2";

		String account = "ump-API";
		String cid = "123456199001011233";
		String name = "张三";
		String card = "5555666677778889";
		String mobile = "13800138001";
		
		String reqTid = "test_123456";

		Map<String, String> map = new TreeMap<String, String>();
		map.put("account", account);
		map.put("cid", cid);
		map.put("name", name);
		map.put("card", card);
		map.put("mobile", mobile);
		map.put("reqTid", reqTid);

		String privateKey =  "d33198ca11a14b97a47b6da0d3c49aca";
		
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String value = entry.getValue();
			String key = entry.getKey();
			sb.append(key).append(value);
		}
		sb.append(privateKey);
		
		String sign = MD5Utils.getMD5Str(sb.toString()).toUpperCase();
		
		map.put("sign", sign);
		
		StringBuilder uri = new StringBuilder();
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String value = entry.getValue();
			String key = entry.getKey();
			try {
				uri.append("&").append(key).append("=").append(URLEncoder.encode(value, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		uri.replace(0, 1, "?");
		System.out.println(url+uri);

	}
}
