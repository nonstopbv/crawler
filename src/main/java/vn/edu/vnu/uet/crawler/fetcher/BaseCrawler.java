package vn.edu.vnu.uet.crawler.fetcher;

import java.io.IOException;
import java.net.SocketTimeoutException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.edu.vnu.uet.crawler.config.Site;
import vn.edu.vnu.uet.crawler.http.URL;
import vn.edu.vnu.uet.crawler.util.StringUtils;
import vn.edu.vnu.uet.crawler.util.UrlUtil;

public abstract class BaseCrawler implements ICrawler {
	private final UrlGrapManagement urlGrapManagements = new UrlGrapManagement(
			getConfig());

	public class FetcherThread extends Thread {

		@Override
		public void run() {
			activeThreads.incrementAndGet();
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
								logger.info("retry " + i + "th " + url);
							}

							try {
								doc = Jsoup.connect(url).userAgent(USER_AGENT)
										.timeout(10000).get();
								if (doc != null) {
									break;
								}
							} catch (SocketTimeoutException e) {
								logger.debug(e.getMessage());

							} catch (IOException e) {
								logger.warn(e.getMessage());

							} catch (Throwable e) {
								logger.error(e.getMessage());

							}

						}
						if (doc == null) {
							logger.error("Null");
						}

						Set<URL> links = UrlUtil.getAbsoluteURLsByJSoupQuery(
								doc, config);

						addLink(url, links);
						if (doc != null) {
							getParser().parse(doc, url);
						}

					} catch (IllegalArgumentException ex) {

					} catch (Exception ex) {
						logger.info("Exception", ex);
					}
				}

			} catch (Exception ex) {
				logger.info("Exception", ex);

			} finally {
				activeThreads.decrementAndGet();
			}

		}

	}

	public static class FetchQueue {

		public List<URL> queue = Collections
				.synchronizedList(new LinkedList<URL>());

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

	private static final Logger logger = LoggerFactory
			.getLogger(BaseCrawler.class);
	private AtomicInteger activeThreads;
	/**
	 * The crawler configure.
	 */
	private final Site config;

	protected ArrayList<URL> feededUrl = new ArrayList<URL>();
	private FetchQueue fetchQueue;
	/**
	 * true is the crawler task is finished,otherwise task failure.
	 */
	protected volatile boolean isFinished = false;

	// private final HttpClient httpClient = new HttpClient();

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
	protected final Set<URL> urlsToCrawl = Collections
			.synchronizedSet(new HashSet<URL>());
	protected final Set<Integer> urlsCrawled = Collections
			.synchronizedSet(new HashSet<Integer>());

	public BaseCrawler(final Site config) {
		this.config = config;

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

	public Site getConfig() {
		return config;
	}

	/**
	 * Get list pages URL collection.
	 * 
	 * @return Set a list pages URL set
	 * @throws RuntimeException
	 */
	public Set<URL> getListPageURLs() throws RuntimeException {
		if (StringUtils.isBlank(config.getUrl())
				|| config.getUrl().contains("|")) {
			return null;
		}

		Set<URL> set = new HashSet<URL>();
		String url = config.getUrl();
		if (StringUtils.isNotEmpty(url)) {
			if (url.contains(",")) {
				String[] urlArray = url.split(",");
				for (String urlStr : urlArray) {
					urlStr = urlStr.trim();
					set.add(URL.valueOf(urlStr));
				}
			} else {
				set.add(URL.valueOf(url));
			}
		}
		/*
		 * if (StringUtils.isNotEmpty(config.getNextPageRegex())) {
		 * addPagingUrl(set); }
		 */
		return set;
	}

	@Override
	public int getTotalsToCrawl() {
		return toCrawlTotals;
	}

	@Override
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
			throw new IllegalArgumentException(
					"CrawlConfig instance can not be null!");
		}
		onBeforeStart();
		lastRequestStart = new AtomicLong(System.currentTimeMillis());

		activeThreads = new AtomicInteger(0);
		spinWaiting = new AtomicInteger(0);
		fetchQueue = new FetchQueue();
		fetchQueue.addFetchItem(URL.valueOf(config.getUrl()));
		mapURL.put(config.getUrl(), mapURL.size() + 1);

		for (int i = 0; i < 5; i++) {
			FetcherThread fetcherThread = new FetcherThread();
			fetcherThread.start();
		}

		// final ArrayList<Thread> threads = new
		// ArrayList<Thread>(crawlers.size());
		// for (ICrawler crawler : crawlers) {
		// final Thread crawlerThread = new Thread(this);
		// threads.add(crawlerThread);
		// }
		// crawlerThread.start();
		final long startTime = System.currentTimeMillis();
		// starting crawler thread
		// for (Thread t : threads) {
		// t.start();
		// }

		final int startedThreadSize = 1;
		/*
		 * Thread monitorThread = new Thread(new Runnable() {
		 * 
		 * @Override
		 * public void run() {
		 * try {
		 * String unfinishedCrawler = "";
		 * int unfinished = 0;
		 * while (true) {
		 * sleep(50);
		 * 
		 * Thread t = crawlerThread;
		 * if (t != null) {
		 * 
		 * if (t.isAlive() && isFinished()) {
		 * logger.info(getConfig().getName()
		 * + " was crawled total of "
		 * + getTotalsToCrawl() + " pages.");
		 * onBeforeExit();
		 * t.interrupt();
		 * 
		 * break;
		 * } else if (!t.isAlive() && !isFinished()) {
		 * unfinished++;
		 * unfinishedCrawler += t.getName() + " ";
		 * 
		 * break;
		 * }
		 * }
		 * 
		 * }
		 * 
		 * long endTime = (System.currentTimeMillis() - startTime)
		 * / (1000 * 60);
		 * logger.info("All of the crawlers are stopped ... used "
		 * + endTime + " minutes," + startedThreadSize
		 * + " threads are started , " + unfinished
		 * + " threads are unfinished. ");
		 * if (StringUtils.isNotEmpty(unfinishedCrawler)) {
		 * logger.info(unfinishedCrawler + " is unfinished !");
		 * }
		 * 
		 * // shutdown the HTTP connection
		 * HttpConnectionManager.shutdown();
		 * 
		 * } catch (Throwable e) {
		 * e.printStackTrace();
		 * logger.error("Monitor Thread Error " + e.getMessage(), e);
		 * }
		 * 
		 * }
		 * 
		 * }, "Monitor");
		 * monitorThread.start();
		 */

	}

}
