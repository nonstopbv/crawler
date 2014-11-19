package vn.edu.vnu.uet.crawler.task;

import vn.edu.vnu.uet.crawler.config.Site;
import vn.edu.vnu.uet.crawler.fetcher.BaseCrawler;
import vn.edu.vnu.uet.crawler.fetcher.Parser;

public class InfoqCrawler extends BaseCrawler {

	public InfoqCrawler(Site config) {
		super(config);
	}
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
