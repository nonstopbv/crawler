package vn.edu.vnu.uet.crawler.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.edu.vnu.uet.crawler.config.Site;
import vn.edu.vnu.uet.crawler.fetcher.ICrawler;
import vn.edu.vnu.uet.crawler.http.URL;

/**
 * UrlUtil
 * 
 * 
 *
 */

public abstract class UrlUtil {

	// public static Properties properties = null;

	private static final Logger logger = LoggerFactory.getLogger(UrlUtil.class);

	

	private static final String URL_PREFIX = "/";

	
	public static Document getJsoupDocumentByUrl(String url) {
		Document doc = null;
		
		int retry = 0;
		while (retry++ < 5) {
			try {
				doc = Jsoup.connect(url)
						.userAgent(ICrawler.USER_AGENT)
						.timeout(10000).get();
				if (doc != null) {
					return doc;
				}
			} catch (SocketTimeoutException e) {
				logger.debug(e.getMessage());
			} catch (IOException e) {
				logger.warn(e.getMessage());
				break;
			} catch (Throwable e) {
				logger.error(e.getMessage());
				break;
			}
		}
		return doc;
	}


	public static Set<URL> getAbsoluteURLsByJSoupQuery(Document doc,
			Site config) throws RuntimeException {
		
		Set<URL> urlSet = new HashSet<URL>();
		try {
			
			Elements urls = doc.select(config.getElementSelector()).select("a[href]");
			if (urls != null) {
				for (Element element : urls) {
					String url = element.attr("abs:href").trim();
					url = url.replace("../", "");
					url = url.replace("./", "");
					
					if (StringUtils.isNotEmpty(url)) {
						
						if(url.matches(config.getUrlRegex()))
						urlSet.add(URL.valueOf(url));
					}
				}
			}
		} catch (RuntimeException e) {
			throw e;
		}
		return urlSet;
	}

/*
	private static Set<URL> getAbsoluteURLsByRegex(URL url,
			String elementIdOrRegex) throws Exception {
		Set<URL> urlSet = new HashSet<URL>();
		String reg = elementIdOrRegex.substring(1);
		HttpFetchResult fetchResult = httpClient.requestHttpGet(url);
		if (fetchResult != null && fetchResult.getHtml() != null) {
			List<String> extractedList = StringUtils.regexExtractor(
					fetchResult.getHtml(), reg);
			for (String partUrlStr : extractedList) {
				int i = url.getPath().lastIndexOf(URL_PREFIX);
				String path = url.getPath().substring(0, i);
				String hostAndPath = url.getHost();
				if (!partUrlStr.contains(path)) {
					hostAndPath = url.getHost() + URL_PREFIX + path;
				}
				String fullStr = fixAbsoluteLink(partUrlStr, hostAndPath,
						url.getProtocol());
				if (fullStr != null && !fullStr.isEmpty()) {
					urlSet.add(URL.valueOf(fullStr));
				}
			}
		}
		return urlSet;
	}*/


	public static String fixAbsoluteLink(String path, String hostAndPath,
			String protocol) {
		path = path.replace("../", "");
		path = path.replace("./", "");
		if (!path.startsWith(protocol)) {
			if ((hostAndPath.endsWith(URL_PREFIX) && !path
					.startsWith(URL_PREFIX))
					|| (!hostAndPath.endsWith(URL_PREFIX) && path
							.startsWith(URL_PREFIX))) {
				path = hostAndPath + path;
			} else if (!hostAndPath.endsWith(URL_PREFIX)
					&& !path.startsWith(URL_PREFIX)) {
				path = hostAndPath + URL_PREFIX + path;
			} else if (hostAndPath.endsWith(URL_PREFIX)
					&& path.startsWith(URL_PREFIX)) {
				path = hostAndPath + path.substring(1, path.length());
			}
			path = protocol + "://" + path;
			return path;
		}
		return path;
	}


/*	public static Set<URL> extracting(URL url, String queryOrRegex) {
		Set<URL> result = null;
		Document doc = null;
		
		try {
			if (queryOrRegex.startsWith("#")) {
				doc = getJsoupDocumentByUrl(url.toFullString());
				
				//result = getAbsoluteURLsByJSoupQuery(doc, queryOrRegex);
			} else {
				result = getAbsoluteURLsByRegex(url, queryOrRegex);
			}
		} catch (Throwable e) {
			logger.error(url.toFullString() + " extracting links failure "
					+ e.getMessage());
		}
		return result;
	}
*/
}
