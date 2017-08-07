/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//
//import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.methods.multipart.FilePart;
//import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
//import org.apache.commons.httpclient.methods.multipart.Part;
//import org.apache.commons.httpclient.methods.multipart.StringPart;
//import org.apache.commons.httpclient.params.HttpMethodParams;

import com.type.sdk.android.TypeSDKLogger;

/**
 * API客户端接口：用于访问网络数据
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class HttpUtil {

	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";

	private final static int TIMEOUT_CONNECTION = 5000;

	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	public static boolean isPostok = true;

	/**
	 * 拼接URL
	 * 
	 * @param params
	 * @param code
	 * @return
	 */
	public static String getUrl(HashMap<String, String> params, String aurl) {
		String url = aurl;
		// 添加url参数
		if (params != null) {
			Iterator<String> it = params.keySet().iterator();
			StringBuffer sb = null;
			while (it.hasNext()) {
				String key = it.next();
				String value = params.get(key);
				if (sb == null) {
					sb = new StringBuffer();
					sb.append("?");
				} else {
					sb.append("&");
				}
				sb.append(key);
				sb.append("=");
				sb.append(value);
			}
			// TypeSDKLogger.i("url:" + url);
			url += sb.toString();
		}
		return url;
	}

	/**
	 * 网络请求（get）
	 * 
	 * @param url
	 * @return
	 */
	public static String mHttpGet(String url) {
		String result = "";
		try {
			url = url.trim().replace(" ", "");
			URL getUrl = new URL(url);
			URLConnection rulConnection = getUrl.openConnection();
			HttpURLConnection httpUrlConnection = (HttpURLConnection) rulConnection;
			httpUrlConnection.setDoOutput(false);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestMethod("GET");
			httpUrlConnection.setReadTimeout(TIMEOUT_CONNECTION);// 设置超时的时间
			httpUrlConnection.setConnectTimeout(TIMEOUT_CONNECTION);
			httpUrlConnection
					.setRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

			if (httpUrlConnection.getResponseCode() == 200) {
				// TypeSDKLogger.i("URLgetCode 200");
				// 获取响应的输入流对象
				InputStream is = httpUrlConnection.getInputStream();
				// 创建字节输出流对象
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				// 定义读取的长度
				int len = 0;
				// 定义缓冲区
				byte buffer[] = new byte[1024];
				// 按照缓冲区的大小，循环读取
				while ((len = is.read(buffer)) != -1) {
					// 根据读取的长度写入到os对象中
					os.write(buffer, 0, len);
				}
				// 释放资源
				is.close();
				os.close();
				// 返回字符串
				result = new String(os.toByteArray());
			} else {
				TypeSDKLogger.e("URLget getResponseCode:"
						+ httpUrlConnection.getResponseCode());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			TypeSDKLogger.e("URLget 请求错误");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 网络请求（get）
	 * 
	 * @param url
	 * @param retry
	 * @return
	 */
	public static String mHttpGet(String url, int retry) {
		String result = "";
		URL getUrl = null;
		URLConnection rulConnection = null;
		HttpURLConnection httpUrlConnection = null;
		url = url.trim().replace(" ", "");
		int time = 0;
		do {
			TypeSDKLogger.d("Http get retry " + time + ", url=" + url);
			try {
				getUrl = new URL(url);
				rulConnection = getUrl.openConnection();
				httpUrlConnection = (HttpURLConnection) rulConnection;
				httpUrlConnection.setDoOutput(false);
				httpUrlConnection.setDoInput(true);
				httpUrlConnection.setUseCaches(false);
				httpUrlConnection.setRequestMethod("GET");
				httpUrlConnection.setReadTimeout(TIMEOUT_CONNECTION);// 设置超时的时间
				httpUrlConnection.setConnectTimeout(TIMEOUT_CONNECTION);
				httpUrlConnection
						.setRequestProperty("User-Agent",
								"Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

				if (httpUrlConnection.getResponseCode() == 200) {

					InputStream is = httpUrlConnection.getInputStream();
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					int len = 0;
					byte buffer[] = new byte[1024];
					while ((len = is.read(buffer)) != -1) {
						os.write(buffer, 0, len);
					}
					is.close();
					os.close();
					result = new String(os.toByteArray());
					break;
				} else {
					TypeSDKLogger.e("URLget getResponseCode:"
							+ httpUrlConnection.getResponseCode());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				TypeSDKLogger.e("URLget 请求错误");
				e.printStackTrace();
			} finally {
				time++;
			}
		} while (time < retry);
		return result;
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param map
	 * @return
	 */
	public static String mHttpPost(String url, Map<String, Object> map) {
		try {
			// 组织请求参数
			url = url.trim().replace(" ", "");
			StringBuffer params = new StringBuffer();
			StringBuffer sb = new StringBuffer();
			if (map != null) {
				Iterator<String> it = map.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					Object value = map.get(key).toString();
					params.append(key);
					params.append("=");
					params.append(value);
					params.append("&");
				}
			}
			if (params.length() > 0) {
				params.deleteCharAt(params.length() - 1);
			}
			// TypeSDKLogger.i("params:" + params.toString());
			URL mURL;

			// TypeSDKLogger.i("url:" + url);
			mURL = new URL(url);
			HttpURLConnection httpConn = (HttpURLConnection) mURL
					.openConnection();
			// 设置参数
			httpConn.setDoOutput(true); // 需要输出
			httpConn.setDoInput(true); // 需要输入
			httpConn.setUseCaches(false); // 不允许缓存
			httpConn.setRequestMethod("POST"); // 设置POST方式连接
			httpConn.setReadTimeout(TIMEOUT_CONNECTION);// 设置超时的时间
			httpConn.setConnectTimeout(TIMEOUT_CONNECTION);
			// 设置请求属性
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpConn.setRequestProperty("Charset", "UTF-8");
			// 连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
			httpConn.connect();
			// 建立输入流，向指向的URL传入参数
			DataOutputStream dos = new DataOutputStream(
					httpConn.getOutputStream());
			dos.writeBytes(URLEncoder.encode(params.toString(), "UTF-8"));
			dos.flush();
			dos.close();
			// 获得响应状态
			int resultCode = httpConn.getResponseCode();
			// TypeSDKLogger.i("resultCode:" + resultCode);
			if (HttpURLConnection.HTTP_OK == resultCode) {

				String readLine = new String();
				BufferedReader responseReader = new BufferedReader(
						new InputStreamReader(httpConn.getInputStream(),
								"UTF-8"));
				while ((readLine = responseReader.readLine()) != null) {
					sb.append(readLine).append("\n");
				}
				responseReader.close();
				// TypeSDKLogger.i("httpPost sb:" + sb.toString());
			} else {
				TypeSDKLogger.e("httpPost error. result code=" + resultCode);
			}
			return sb == null ? "" : sb.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} finally {

		}
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param map
	 * @param retry
	 * @return
	 */
	public static String mHttpPost(String url, Map<String, Object> map,
			int retry) {

		// 组织请求参数
		url = url.trim().replace(" ", "");
		StringBuffer params = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		if (map != null) {
			Iterator<String> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				Object value = map.get(key).toString();
				params.append(key);
				params.append("=");
				params.append(value);
				params.append("&");
			}
		}
		if (params.length() > 0) {
			params.deleteCharAt(params.length() - 1);
		}
		// TypeSDKLogger.i("params:" + params.toString());
		URL mURL;
		HttpURLConnection httpConn;
		int time = 0;
		// TypeSDKLogger.i("url:" + url);
		do {
			try {
				mURL = new URL(url);
				httpConn = (HttpURLConnection) mURL.openConnection();
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				httpConn.setUseCaches(false);
				httpConn.setRequestMethod("POST");
				httpConn.setReadTimeout(TIMEOUT_CONNECTION);
				httpConn.setConnectTimeout(TIMEOUT_CONNECTION);
				httpConn.setRequestProperty("Connection", "Keep-Alive");
				httpConn.setRequestProperty("Charset", "UTF-8");
				httpConn.connect();
				DataOutputStream dos = new DataOutputStream(
						httpConn.getOutputStream());
				dos.writeBytes(URLEncoder.encode(params.toString(), "UTF-8"));
				dos.flush();
				dos.close();
				int resultCode = httpConn.getResponseCode();
				if (HttpURLConnection.HTTP_OK == resultCode) {

					String readLine = new String();
					BufferedReader responseReader = new BufferedReader(
							new InputStreamReader(httpConn.getInputStream(),
									"UTF-8"));
					while ((readLine = responseReader.readLine()) != null) {
						sb.append(readLine).append("\n");
					}
					responseReader.close();
					break;
				} else {
					TypeSDKLogger
							.e("httpPost error. result code=" + resultCode);
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				time++;
			}
		} while (time < retry);
		return sb == null ? "" : sb.toString();
	}

	/**
	 * post json请求
	 * 
	 * @param url
	 * @param map
	 * @param retry
	 * @return
	 */
	public static String jsonHttpPost(String url, String jsonString) {

		StringBuffer sb = new StringBuffer();
		URL mURL;
		HttpURLConnection httpConn;
		
			try {
				mURL = new URL(url);
				httpConn = (HttpURLConnection) mURL.openConnection();
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				httpConn.setUseCaches(false);
				httpConn.setRequestMethod("POST");
				httpConn.setReadTimeout(TIMEOUT_CONNECTION);
				httpConn.setConnectTimeout(TIMEOUT_CONNECTION);
				httpConn.setRequestProperty("Content-Type", "application/json");
				httpConn.connect();
				DataOutputStream dos = new DataOutputStream(
						httpConn.getOutputStream());
				dos.writeBytes(jsonString);
				dos.flush();
				dos.close();
				int resultCode = httpConn.getResponseCode();
				if (HttpURLConnection.HTTP_OK == resultCode) {
					String readLine = "";
					BufferedReader responseReader = new BufferedReader(
							new InputStreamReader(httpConn.getInputStream(),
									"UTF-8"));
					while((readLine=responseReader.readLine())!=null){
						sb.append(readLine);
					}
					responseReader.close();
				} else {
					TypeSDKLogger
							.e("httpPost error. result code=" + resultCode);
				}

			} catch (MalformedURLException e) {
				TypeSDKLogger.e("Http post json error");
				e.printStackTrace();
			} catch (IOException e) {
				TypeSDKLogger.e("Http post json error");
				e.printStackTrace();
			} catch (Exception e) {
				TypeSDKLogger.e("Http post json error");
				e.printStackTrace();
			} finally {
				
			}
		
		return sb == null ? "" : sb.toString();
	}
	
	
	/**
	 * post json请求
	 * 
	 * @param url
	 * @param map
	 * @param retry
	 * @return
	 */
	public static String jsonHttpPost(String url, String jsonString, int retry) {

		StringBuffer sb = new StringBuffer();
		URL mURL;
		HttpURLConnection httpConn;
		int time = 0;
		do {
			try {
				mURL = new URL(url);
				httpConn = (HttpURLConnection) mURL.openConnection();
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				httpConn.setUseCaches(false);
				httpConn.setRequestMethod("POST");
				httpConn.setReadTimeout(TIMEOUT_CONNECTION);
				httpConn.setConnectTimeout(TIMEOUT_CONNECTION);
				httpConn.setRequestProperty("Content-Type", "application/json");
				httpConn.connect();
				DataOutputStream dos = new DataOutputStream(
						httpConn.getOutputStream());
				dos.writeBytes(jsonString);
				dos.flush();
				dos.close();
				int resultCode = httpConn.getResponseCode();
				if (HttpURLConnection.HTTP_OK == resultCode) {
					String readLine = "";
					BufferedReader responseReader = new BufferedReader(
							new InputStreamReader(httpConn.getInputStream(),
									"UTF-8"));
					while((readLine=responseReader.readLine())!=null){
						sb.append(readLine);
					}
					responseReader.close();
				} else {
					TypeSDKLogger
							.e("httpPost error. result code=" + resultCode);
				}

			} catch (MalformedURLException e) {
				TypeSDKLogger.e("Http post json error");
				e.printStackTrace();
			} catch (IOException e) {
				TypeSDKLogger.e("Http post json error");
				e.printStackTrace();
			} catch (Exception e) {
				TypeSDKLogger.e("Http post json error");
				e.printStackTrace();
			} finally {
				time++;
			}
		} while (time < retry);
		return sb == null ? "" : sb.toString();
	}

	// private static HttpClient getHttpClient() {
	// HttpClient httpClient = new HttpClient();
	// // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
	// //
	// httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
	// // 设置 默认的超时重试处理策略
	// httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
	// new DefaultHttpMethodRetryHandler());
	// // 设置 连接超时时间
	// httpClient.getHttpConnectionManager().getParams()
	// .setConnectionTimeout(TIMEOUT_CONNECTION);
	// // 设置 读数据超时时间
	// httpClient.getHttpConnectionManager().getParams()
	// .setSoTimeout(TIMEOUT_SOCKET);
	// // 设置 字符集
	// httpClient.getParams().setContentCharset(UTF_8);
	// return httpClient;
	// }

	// private static GetMethod getHttpGet(String url) {
	// GetMethod httpGet = new GetMethod(url);
	// // 设置 请求超时时间
	// httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
	// // httpGet.setRequestHeader("Host", URLs.HOST);
	// httpGet.setRequestHeader("Connection", "Keep-Alive");
	// // httpGet.setRequestHeader("Cookie", cookie);
	// // httpGet.setRequestHeader("User-Agent", userAgent);
	// return httpGet;
	// }

	// public static GetMethod getHttpGet(String url, int socketTime) {
	// GetMethod httpGet = new GetMethod(url);
	// // 设置 请求超时时间
	// httpGet.getParams().setSoTimeout(socketTime);
	// // httpGet.setRequestHeader("Host", URLs.HOST);
	// httpGet.setRequestHeader("Connection", "Keep-Alive");
	// // httpGet.setRequestHeader("Cookie", cookie);
	// // httpGet.setRequestHeader("User-Agent", userAgent);
	// return httpGet;
	// }

	// public static PostMethod getHttpPost(String url) {
	// PostMethod httpPost = new PostMethod(url);
	// // 设置 请求超时时间
	// httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
	// // httpPost.setRequestHeader("Cookie", "");
	// httpPost.setRequestHeader("Connection", "Keep-Alive");
	// httpPost.addRequestHeader("Content-Type",
	// "application/x-www-form-urlencoded");
	// return httpPost;
	// }

	// public static String _MakeURL(String p_url, Map<String, Object> params) {
	// StringBuilder url = new StringBuilder(p_url);
	// if (url.indexOf("?") < 0)
	// url.append('?');
	//
	// for (String name : params.keySet()) {
	// url.append('&');
	// url.append(name);
	// url.append('=');
	// url.append(String.valueOf(params.get(name)));
	// // 不做URLEncoder处理
	// // url.append(URLEncoder.encode(String.valueOf(params.get(name)),
	// // UTF_8));
	// }
	//
	// return url.toString().replace("?&", "?");
	// }

	/**
	 * get请求URL
	 *
	 * @param url
	 */
	@Deprecated
	public static String http_get(String url){

		return mHttpGet(url,3);
	}

	/**
	 * 公用post方法
	 *
	 * @param url
	 * @param params
	 */
	@Deprecated
	public static String _post(String url, Map<String, Object> params){
		return mHttpPost(url,params,3);
	}

	// 域名解析
	public String getHttpAdress(String dName, String ipAddress) {
		InetAddress inetHost;
		// TypeSDKLogger.i("dName:"+dName+"ipAdress:"+ipAddress);
		try {
			inetHost = InetAddress.getByName(dName);
			// TypeSDKLogger.i("inetHost.getHostAddress():"+inetHost.getHostAddress());
			if (inetHost.getHostAddress().equals(ipAddress)) {
				return dName;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			TypeSDKLogger.e("UnknownHostException:" + e.toString());
			e.printStackTrace();
		}
		return ipAddress;
	}

}
