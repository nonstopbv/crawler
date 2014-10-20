package vn.edu.vnu.uet.crawler.http;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.edu.vnu.uet.crawler.config.Site;

/**
 * Simple HttpClient Wrapper Class
 * 
 * 
 *
 */
public class HttpClient {

	private static Logger logger = LoggerFactory.getLogger(HttpClient.class);

	private HttpFetchResult doGet(final String threadName,
			final String defaultCharset, final URL url, final HttpGet req) {
		int retry = 0;// After the execute request failure of the number of
						// retries
		StatusLine status = null;
		HttpEntity entity = null;
		try {
			while (retry++ < 5) {
				try {
					HttpContext localContext = new BasicHttpContext();
					HttpResponse rsp = HttpConnectionManager.getHttpClient()
							.execute(req, localContext);
					entity = rsp.getEntity();
					status = rsp.getStatusLine();
					if (status.getStatusCode() != HttpStatus.SC_OK) {
						req.abort();
						continue;
					}
					if (entity != null) {
						logger.debug(threadName + " request get "
								+ url.toFullString() + " "
								+ rsp.getStatusLine());
						return new HttpFetchResult(rsp, defaultCharset);
					} else {
						req.abort();
					}
				} catch (IOException ex) {
					// In case of an IOException the connection will be released
					// back to the connection manager automatically
					continue;
				} catch (RuntimeException e) {
					req.abort();
					logger.error(threadName + " request " + url.toFullString()
							+ " " + e.getMessage());
					e.printStackTrace();
					continue;
				} catch (Throwable e) {
					logger.error(threadName + " request " + url.toFullString()
							+ " " + e.getMessage());
					e.printStackTrace();
				}
			}
		} finally {
			try {
				if (entity == null && req != null) {
					req.abort();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		logger.warn(threadName + " request get " + url.toFullString() + " "
				+ status);
		return null;
	}

	public HttpFetchResult requestHttpGet(final Site config,
			final URL url) {
		final HttpGet req = new HttpGet(url.toFullString());
		return doGet(config.getName(), config.getDefaultCharset(), url, req);
	}

	/**
	 * Do get http request use custom header.
	 * 
	 * @param config
	 *            the CrawlerConfig instance
	 * @param url
	 *            the target url instance
	 * @param header
	 *            the custom http header
	 * @return HttpFetchResult the result of fetch
	 */
	public HttpFetchResult requestHttpGet(final Site config,
			final URL url, final Header header) {
		final HttpGet req = new HttpGet(url.toFullString());
		if (header != null) {
			req.addHeader(header);
		}
		return doGet(config.getName(), config.getDefaultCharset(), url, req);
	}

	/**
	 * Do get http request.
	 * 
	 * @param threadName
	 *            the crawler name
	 * @param defaultCharset
	 *            the crawler charset
	 * @param url
	 *            the target url
	 * @return HttpFetchResult the result of fetch
	 */
	public HttpFetchResult requestHttpGet(final String threadName,
			final String defaultCharset, final URL url) {
		final HttpGet req = new HttpGet(url.toFullString());
		return doGet(threadName, defaultCharset, url, req);
	}

	public HttpFetchResult requestHttpGet(final URL url) {
		final HttpGet req = new HttpGet(url.toFullString());
		return doGet("null", "utf-8", url, req);
	}

}
