package vn.edu.vnu.uet.crawler.task;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import vn.edu.vnu.uet.crawler.config.Site;
import vn.edu.vnu.uet.crawler.fetcher.BaseCrawler;
import vn.edu.vnu.uet.crawler.fetcher.ICrawler;
import vn.edu.vnu.uet.crawler.fetcher.Parser;
import vn.edu.vnu.uet.crawler.http.URL;

public class InfoqCrawler extends BaseCrawler {

	public InfoqCrawler(Site config) {
		super(config);
	}

	/**
	 * Override to remove unnecessary URL.
	 * 
	 */
	
	
	@Override
	public void onBeforeExit() {
	}

	@Override
	public void onBeforeStart() {
	}

	@Override
	public Parser getParser() {
		// TODO Auto-generated method stub

		return new ParserDemo();
	}

	public static void main(String[] args) {
		new InfoqCrawler(Site.me().setUrl("http://vnexpress.net")
				.setDelay(100).setElementSelector("body").setUrlRegex("http://vnexpress.net/tin-tuc/.*")).start();

	}

}
