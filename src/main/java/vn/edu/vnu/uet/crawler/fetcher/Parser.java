package vn.edu.vnu.uet.crawler.fetcher;

import org.jsoup.nodes.Document;

import vn.edu.vnu.uet.crawler.http.HttpFetchResult;

/**
 * Parser for Fetch Result
 * 
 * 
 *
 */
public interface Parser {

	/**
	 * After crawling do parse
	 * 
	 * @param result
	 *            the fetch result
	 * @param url
	 *            the target url
	 * @param threadName
	 *            the crawler name
	 * @param isUpdate
	 *            true is update
	 */
	public void parse(Document doc,  String url);

}
