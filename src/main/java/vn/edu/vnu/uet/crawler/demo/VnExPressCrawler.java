package vn.edu.vnu.uet.crawler.demo;

import vn.edu.vnu.uet.crawler.core.BaseCrawler;
import vn.edu.vnu.uet.crawler.core.Config;
import vn.edu.vnu.uet.crawler.core.Parser;

public class VnExPressCrawler extends BaseCrawler {

	public VnExPressCrawler(Config config) {
		super(config);
	}

	public Parser getParser() {
		return new FilePrase();
	}

	public static void main(String[] args) {
		new VnExPressCrawler(Config.me().setUrl("http://vnexpress.net").setDelay(100).setElementSelector("body").setName("vnexpress")
				.setUrlRegex("http://vnexpress.net/tin-tuc/.*")).start();

	}

	public void onBeforeExit() {

	}

	public void onBeforeStart() {

	}

}
