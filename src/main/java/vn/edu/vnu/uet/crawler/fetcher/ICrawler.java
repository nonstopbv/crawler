package vn.edu.vnu.uet.crawler.fetcher;

import vn.edu.vnu.uet.crawler.config.Site;

/**
 * Interface for a Web Crawler
 * 
 * @see {@link BaseCrawler}
 * 
 *
 */
public interface ICrawler {
	public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11";

	// public void crawl() throws RuntimeException;

	public Site getConfig();

	// public Set<URL> getListPageURLs() throws RuntimeException;

	public Parser getParser();

	// public Set<URL> getTargetPageURLs() throws RuntimeException;

	public int getTotalsToCrawl();

	public boolean isFinished();

	public void onBeforeExit();

	public void onBeforeStart();

	public void start();
}
