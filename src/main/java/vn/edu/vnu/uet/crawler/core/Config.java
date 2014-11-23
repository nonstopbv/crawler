package vn.edu.vnu.uet.crawler.core;

public class Config {

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

	public Config() {
		this.name = "null";
		this.defaultCharset = "UTF-8";
		this.url = "";
		this.delay = 1000L;
		// this.nextPageRegex = "";
		this.elementSelector = "body";
		// this.maxPageNo = 1;

	}

	public static Config me() {
		return new Config();
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

	public Config setDefaultCharset(String defaultCharset) {
		this.defaultCharset = defaultCharset;
		return this;
	}

	public Config setDelay(long delay) {
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
	public Config setElementSelector(String elementSelector) {
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
	public Config setUrlRegex(String urlRegex) {
		this.urlRegex = urlRegex;
		return this;
	}

	/*
	 * public Site setMaxPageNo(int maxPageNo) {
	 * this.maxPageNo = maxPageNo;
	 * return this;
	 * }
	 */
	public Config setName(String name) {
		this.name = name;
		return this;
	}

	/*
	 * public Site setNextPageRegex(String nextPageRegex) {
	 * this.nextPageRegex = nextPageRegex;
	 * return this;
	 * }
	 */
	public Config setUrl(String url) {
		this.url = url;
		return this;
	}

}