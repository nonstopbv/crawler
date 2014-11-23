package vn.edu.vnu.uet.crawler.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class BaseCrawler implements ICrawler {
	private UrlGrapManagement urlGrapManagements;
	private   Set<URL> getAbsoluteURLsByJSoupQuery(Document doc, Config config) throws RuntimeException {

		Set<URL> urlSet = new HashSet<URL>();
		try {

			Elements urls = doc.select(config.getElementSelector()).select("a[href]");
			if (urls != null) {
				for (Element element : urls) {
					String url = element.attr("abs:href").trim();
					url = url.replace("../", "");
					url = url.replace("./", "");

					if (url != null && url.trim().length() > 0) {

						if (url.matches(config.getUrlRegex()))
							urlSet.add(URL.valueOf(url));
					}
				}
			}
		} catch (RuntimeException e) {
			throw e;
		}
		return urlSet;
	}
	public class FetcherThread extends Thread {

		@Override
		public void run() {
			URL item = null;
			try {

				while (true) {
					try {
						item = fetchQueue.getFetchItem();
						if (item == null) {
							if (fetchQueue.getSize() > 0) {
								spinWaiting.incrementAndGet();
								try {
									Thread.sleep(500);
								} catch (Exception ex) {
								}
								spinWaiting.decrementAndGet();
								continue;
							} else {
								return;
							}
						}

						lastRequestStart.set(System.currentTimeMillis());

						// CrawlDatum crawldatum = new CrawlDatum();
						String url = item.toFullString();
						// crawldatum.setUrl(url);
						Document doc = null;
						for (int i = 0; i <= retry; i++) {
							if (i > 0) {
								System.out.println("retry " + i + "th " + url);
							}

							try {
								doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(10000).get();
								if (doc != null) {
									break;
								}
							} catch (Exception ex) {
								ex.getMessage();

							}

						}
						if (doc == null) {
							System.err.println("Document null point exetipon");
						}

						Set<URL> links = getAbsoluteURLsByJSoupQuery(doc, config);

						addLink(url, links);
						if (doc != null) {
							getParser().parse(doc, url);
						}

					} catch (IllegalArgumentException ex) {

					} catch (Exception ex) {
						ex.getMessage();
					}
				}

			} catch (Exception ex) {
				ex.getMessage();

			}

		}

	}

	public static class FetchQueue {

		public List<URL> queue = Collections.synchronizedList(new LinkedList<URL>());

		public AtomicInteger totalSize = new AtomicInteger(0);

		public void addFetchItem(URL item) {
			if (item == null) {
				return;
			}
			queue.add(item);
			totalSize.incrementAndGet();
		}

		public synchronized void clear() {
			queue.clear();
		}

		public synchronized void dump() {
			queue.clear();

		}

		public synchronized URL getFetchItem() {
			if (queue.size() == 0) {
				return null;
			}
			return queue.remove(0);
		}

		public int getSize() {
			return queue.size();
		}

	}

	// private AtomicInteger activeThreads;

	private final Config config;

	protected ArrayList<URL> feededUrl = new ArrayList<URL>();
	private FetchQueue fetchQueue;

	protected volatile boolean isFinished = false;

	private AtomicLong lastRequestStart;

	protected Map<String, Integer> mapURL = new HashMap<String, Integer>();

	/**
	 * Collection of list pages
	 */

	private int retry = 3;

	private AtomicInteger spinWaiting;

	/**
	 * total number of crawl.
	 */
	protected int toCrawlTotals;

	/**
	 * urls to crawl
	 */
	protected final Set<URL> urlsToCrawl = Collections.synchronizedSet(new HashSet<URL>());
	protected final Set<Integer> urlsCrawled = Collections.synchronizedSet(new HashSet<Integer>());

	public BaseCrawler(final Config config) {
		this.config = config;
		urlGrapManagements = new UrlGrapManagement(getConfig());

	}

	private synchronized void addLink(String url, Set<URL> links) {
		int urlId = mapURL.get(url);
		if (urlsCrawled.contains(urlId) == false) {
			urlsCrawled.add(urlId);
			Integer[] ids = new Integer[links.size()];
			int i = 0;
			for (URL link : links) {

				if (!mapURL.containsKey(link.toFullString())) {
					fetchQueue.addFetchItem(link);
					mapURL.put(link.toFullString(), mapURL.size() + 1);

				}
				ids[i++] = mapURL.get(link.toFullString());

			}
			Arrays.sort(ids);
			urlGrapManagements.add(urlId, ids);
		}

	}

	/**
	 * clear cache.
	 */
	public void clear() {
		if (urlsToCrawl != null) {
			urlsToCrawl.clear();
		}
		if (feededUrl != null) {
			feededUrl.clear();
		}
	}

	public Config getConfig() {
		return config;
	}

	
	public int getTotalsToCrawl() {
		return toCrawlTotals;
	}

	public boolean isFinished() {
		return isFinished;
	}

	protected void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void start() {

		if (config == null) {
			throw new IllegalArgumentException("CrawlConfig instance can not be null!");
		}
		onBeforeStart();
		lastRequestStart = new AtomicLong(System.currentTimeMillis());

		spinWaiting = new AtomicInteger(0);
		fetchQueue = new FetchQueue();
		fetchQueue.addFetchItem(URL.valueOf(config.getUrl()));
		mapURL.put(config.getUrl(), mapURL.size() + 1);

		for (int i = 0; i < 2; i++) {
			FetcherThread fetcherThread = new FetcherThread();
			fetcherThread.start();
		}

	}

}
