package vn.edu.vnu.uet.crawler.fetcher;

import java.util.Set;

import vn.edu.vnu.uet.crawler.config.Site;
import vn.edu.vnu.uet.crawler.http.URL;

/**
 * Interface for a Web Crawler
 * 
 * @see {@link BaseCrawler}
 * 
 *
 */
public interface ICrawler {

	//public void crawl() throws RuntimeException;

	public Site getConfig();

	//public Set<URL> getListPageURLs() throws RuntimeException;

	public Parser getParser();

	//public Set<URL> getTargetPageURLs() throws RuntimeException;

	public int getTotalsToCrawl();

	public boolean isFinished();

	public void onBeforeExit();
	public  void onBeforeStart();

	public void start();
}
