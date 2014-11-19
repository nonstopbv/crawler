package vn.edu.vnu.uet.crawler.config;

public class Site {

	// default charset "UTF-8"
	private String defaultCharset;

	// delay time for crawl page. default 1000 ms
	private long delay;

	private String elementSelector;
	// the max page number.
	// private int maxPageNo;

	// thread name.
	private String name;

	// next page url regex.
	// private String nextPageRegex;

	// target url to crawl
	private String url;
	private String urlRegex;

	public Site() {
		this.name = "null";
		this.defaultCharset = "UTF-8";
		this.url = "";
		this.delay = 1000L;
		// this.nextPageRegex = "";
		this.elementSelector = "body";
		// this.maxPageNo = 1;

	}

	public static Site me() {
		return new Site();
	}

	public String getDefaultCharset() {
		return defaultCharset;
	}

	public long getDelay() {
		return delay;
	}

	/*
	 * public int getMaxPageNo() {
	 * return maxPageNo;
	 * }
	 */

	public String getName() {
		return name;
	}

	/*
	 * public String getNextPageRegex() {
	 * return nextPageRegex;
	 * }
	 */
	public String getUrl() {
		return url;
	}

	public Site setDefaultCharset(String defaultCharset) {
		this.defaultCharset = defaultCharset;
		return this;
	}

	public Site setDelay(long delay) {
		this.delay = delay;
		return this;
	}

	/**
	 * @return the elementSelector
	 */
	public String getElementSelector() {
		return elementSelector;
	}

	/**
	 * @param elementSelector
	 *            the elementSelector to set
	 */
	public Site setElementSelector(String elementSelector) {
		this.elementSelector = elementSelector;
		return this;
	}

	/**
	 * @return the urlRegex
	 */
	public String getUrlRegex() {
		return urlRegex;
	}

	/**
	 * @param urlRegex
	 *            the urlRegex to set
	 */
	public Site setUrlRegex(String urlRegex) {
		this.urlRegex = urlRegex;
		return this;
	}

	/*
	 * public Site setMaxPageNo(int maxPageNo) {
	 * this.maxPageNo = maxPageNo;
	 * return this;
	 * }
	 */
	public Site setName(String name) {
		this.name = name;
		return this;
	}

	/*
	 * public Site setNextPageRegex(String nextPageRegex) {
	 * this.nextPageRegex = nextPageRegex;
	 * return this;
	 * }
	 */
	public Site setUrl(String url) {
		this.url = url;
		return this;
	}

}