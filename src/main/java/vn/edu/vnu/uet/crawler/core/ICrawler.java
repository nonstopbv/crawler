package vn.edu.vnu.uet.crawler.core;


/**
 * Interface for a Web Crawler
 * 
 * @see {@link BaseCrawler}
 * 
 *
 */
public interface ICrawler {
	public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11";

	public Config getConfig();

	public Parser getParser();

	public int getTotalsToCrawl();

	public boolean isFinished();

	public void onBeforeExit();

	public void onBeforeStart();

	public void start();
}
