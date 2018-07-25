package com.proj.proxyservice.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.proj.proxyservice.Constant;
import com.proj.proxyservice.HttpBean;


@SuppressWarnings({ "deprecation", "unused" })
public class HttpClientPoolUtil {
	private final static Logger log = LoggerFactory.getLogger("HttpClientPoolUtil");

	/**
	 * 默认的请求超时时间，1秒
	 */
	public final static int DEFAULT_REQUEST_TIMEOUT = 1 * 1000;
	/**
	 * 默认的响应超时时间，3秒
	 */
	public final static int READ_TIME_OUT = 3 * 1000;

	public static CloseableHttpClient httpclient;

	// 获得池化得HttpClient
	static {
		ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator(
						response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						return Long.parseLong(value) * 1000;
					}
				}
				return 60 * 1000;// 如果没有约定，则默认定义时长为60s
			}
		};
		SSLContext sslcontext = null;
		try {
			sslcontext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 默认信任所有证书
				public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					return true;
				}
			}).build();

		} catch (Exception e) {
			log.error("初始化https连接池失败：", e);
		}

		SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslcontext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, hostnameVerifier);
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		connectionManager.setMaxTotal(500);
		connectionManager.setDefaultMaxPerRoute(50);
		httpclient = HttpClients.custom().setConnectionManager(connectionManager).build();
	}

	/**
	 * 
	 * @param url
	 * @param content
	 * @param contentType
	 * @return
	 * @throws Exception
	 */
	public static HttpBean sendByPost(String url, String content, String contentType) throws Exception {
		log.info("发送的路径："+url+"发送的报文："+content.replace("\r\n", "").replace("\n", ""));
		StringEntity resEntity = new StringEntity(content, Consts.UTF_8);
		HttpPost httpPost = null;
		httpPost = new HttpPost(url);
		httpPost.setEntity(resEntity);
		HttpBean httpBean = sendHttpsRequestByPost(httpPost, contentType);
		log.info("收到的响应报文："+httpBean.getResponseStr().replace("\r\n", "").replace("\n", ""));
		return httpBean;
	}
	
	
	
	/**
	 * 发送HTTPS POST请求
	 * 
	 * @param 要访问的HTTPS地址,POST访问的参数Map对象
	 * @return 返回响应值
	 * @throws Exception
	 */
	public static final HttpBean sendHttpsRequestByPost(HttpPost httpPost, String contenType) throws Exception {
		String responseContent = null;
		CloseableHttpResponse response = null;
		HttpBean httpBean = new HttpBean();
		try {
			HttpClientContext context = HttpClientContext.create();
			// 设置请求的配置
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(READ_TIME_OUT)
					.setConnectTimeout(DEFAULT_REQUEST_TIMEOUT).setConnectionRequestTimeout(READ_TIME_OUT).build();
			httpPost.setConfig(requestConfig);
			httpPost.addHeader("Content-type", contenType);
			log.info("executing request " + httpPost.getURI());
			response = httpclient.execute(httpPost, context);
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity(); // 获取响应实体
				if (entity != null) {
					responseContent = EntityUtils.toString(entity, "UTF-8");
					ContentType contentTypeInfo = ContentType.getOrDefault(entity);
					httpBean.setContentType(contentTypeInfo.getMimeType());
					httpBean.setResponseStr(responseContent);
				}
			} else {
				HttpEntity entity = response.getEntity();
				httpPost.abort();
			}
		} catch (Exception e) {
			MonitorUtil.addError();
			log.error("请求失败：", e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {
				log.error("关闭请求连接失败", e);
			}
		}
		return httpBean;
	}
	
	
	
	/**
	 * 
	 * @param url
	 * @param content
	 * @param contentType
	 * @return
	 */
	public static HttpBean sendByGet(String url, String contentType, String x_real_ip, String x_forward_info) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Content-type", contentType);
		httpGet.setHeader(Constant.X_Real_IP, x_real_ip);
		httpGet.setHeader(Constant.X_FORWARDED_FOR, x_forward_info);
		return sendHttpsRequestByGet(httpGet);
	}

	/**
	 * 发送HTTPS GET请求
	 * 
	 * @param 要访问的HTTPS地址,POST访问的参数Map对象
	 * @return 返回响应值
	 */
	public static final HttpBean sendHttpsRequestByGet(HttpGet httpGet) {
		CloseableHttpResponse response = null;
		HttpBean httpBean = new HttpBean();
		try {
			HttpClientContext context = HttpClientContext.create();
			// 设置请求的配置
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(READ_TIME_OUT)
					.setConnectTimeout(DEFAULT_REQUEST_TIMEOUT).setConnectionRequestTimeout(READ_TIME_OUT).build();
			httpGet.setConfig(requestConfig);

			log.info("executing request " + httpGet.getURI());
			response = httpclient.execute(httpGet, context);
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity(); // 获取响应实体
				if (entity != null) {
					String responseContent = EntityUtils.toString(entity, "UTF-8");
					ContentType contentTypeInfo = ContentType.getOrDefault(entity);
			        httpBean.setContentType(contentTypeInfo.getMimeType());
					httpBean.setResponseStr(responseContent);
				}
			} else {
				HttpEntity entity = response.getEntity();
				httpGet.abort();
			}
		} catch (Exception e) {
			MonitorUtil.addError();
			log.error("请求失败：", e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (Exception e) {
				log.error("关闭请求连接失败", e);
			}
		}
		return httpBean;
	}

}
