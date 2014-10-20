package vn.edu.vnu.uet.crawler.http;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple HttpConnectionManager use Apache HttpClient
 * 
 *
 */
public abstract class HttpConnectionManager {

	// default connection timeout period.
	public static int CONNECTION_TIMEOUT = 20000;

	private static PoolingClientConnectionManager connectionManager;

	private static DefaultHttpClient httpclient;

	private static HttpParams httpParams;

	private static Logger logger = LoggerFactory
			.getLogger(HttpConnectionManager.class);

	// default max total route number.
	public static int MAX_ROUTE_TOTAL = 40;

	// default max total connection number.
	public static int MAX_TOTAL_CONN = 200;

	public static String PROXY_HOST;

	public static String PROXY_PASSWORD;
	public static int PROXY_PORT;
	public static String PROXY_USER_NAME;
	// default socket timeout period.
	public static int SO_TIMEOUT = 20000;
	public static boolean USED_PROXY = false;
	// default user_agent
	public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11";

	static {
		try {
			httpParams = new BasicHttpParams();
			/*
			 * if (UrlUtil.properties == null) { UrlUtil.properties =
			 * PropertiesUtils.loadProperties("crawler4j.properties"); } if
			 * (StringUtils
			 * .isNotEmpty(UrlUtil.properties.getProperty("MAX_TOTAL_CONN"))) {
			 * MAX_TOTAL_CONN =
			 * Integer.parseInt(UrlUtil.properties.getProperty("MAX_TOTAL_CONN"
			 * )); } if (StringUtils.isNotEmpty(UrlUtil.properties.getProperty(
			 * "MAX_ROUTE_TOTAL"))) { MAX_ROUTE_TOTAL =
			 * Integer.parseInt(UrlUtil.
			 * properties.getProperty("MAX_ROUTE_TOTAL")); } if
			 * (StringUtils.isNotEmpty
			 * (UrlUtil.properties.getProperty("CONNECTION_TIMEOUT"))) {
			 * CONNECTION_TIMEOUT =
			 * Integer.parseInt(UrlUtil.properties.getProperty
			 * ("CONNECTION_TIMEOUT")); } if
			 * (StringUtils.isNotEmpty(UrlUtil.properties
			 * .getProperty("SO_TIMEOUT"))) { SO_TIMEOUT =
			 * Integer.parseInt(UrlUtil.properties.getProperty("SO_TIMEOUT")); }
			 * 
			 * if
			 * (StringUtils.isNotEmpty(UrlUtil.properties.getProperty("USER_AGENT"
			 * ))) { USER_AGENT = UrlUtil.properties.getProperty("USER_AGENT");
			 * }
			 */
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
					.getSocketFactory()));
			schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
					.getSocketFactory()));
			connectionManager = new PoolingClientConnectionManager(
					schemeRegistry);
			// Increase max total connection to 200
			connectionManager.setMaxTotal(MAX_TOTAL_CONN);
			// Increase default max connection per route to 20
			// Increase max connections for localhost:80 to 50
			connectionManager.setDefaultMaxPerRoute(MAX_ROUTE_TOTAL);
			// cm.setMaxPerRoute(new HttpRoute(target), 20);
			httpParams.setParameter("User-Agent", USER_AGENT);
			httpParams
					.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
			httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
					CONNECTION_TIMEOUT);
			httpclient = new DefaultHttpClient(connectionManager, httpParams);
			// String proxyHost = UrlUtil.properties.getProperty("PROXY_HOST");
			if (USED_PROXY) {

				if (PROXY_USER_NAME != null) {
					httpclient.getCredentialsProvider().setCredentials(
							new AuthScope(PROXY_HOST, PROXY_PORT),
							new UsernamePasswordCredentials(PROXY_USER_NAME,
									PROXY_PASSWORD));
				}
				HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
				httpclient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
		} catch (Throwable e) {
			logger.error("DefaultHttpClient initial failure! " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Get apache HttpClient instance.
	 * 
	 * @return
	 */
	public static org.apache.http.client.HttpClient getHttpClient() {
		if (httpclient == null) {
			httpclient = new DefaultHttpClient(connectionManager, httpParams);
			return httpclient;
		}
		return httpclient;
	}

	/**
	 * Shut down HttpClient
	 */
	public static void shutdown() {
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		// httpclient.getConnectionManager().shutdown();
	}

}
