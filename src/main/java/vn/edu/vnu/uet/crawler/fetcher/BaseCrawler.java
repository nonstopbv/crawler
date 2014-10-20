/*
 * Copyright (c) 2012 Zhuoran Wang <zoran.wang@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package vn.edu.vnu.uet.crawler.fetcher;
	
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
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
import vn.edu.vnu.uet.crawler.http.HttpClient;
import vn.edu.vnu.uet.crawler.http.HttpConnectionManager;
import vn.edu.vnu.uet.crawler.http.URL;
import vn.edu.vnu.uet.crawler.util.StringUtils;
import vn.edu.vnu.uet.crawler.util.UrlUtil;

/**
 * This class is a basic abstract WEB crawler.
 * 	
 * 
 *
 */
public abstract class BaseCrawler implements ICrawler {

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
									doc = Jsoup.connect(url)
											.userAgent(HttpConnectionManager.USER_AGENT)
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
						if(doc == null){
							logger.error("Null");
						}
						
						 Set<URL> links = UrlUtil.getAbsoluteURLsByJSoupQuery(doc,config);
						for (URL link : links) {
							
							addLink(link);
						}
						
						if (doc != null) {
							getParser().parse(doc, url);
						}
						
					} catch (IllegalArgumentException ex) {
						
					}
					catch (Exception ex) {
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

	private FetchQueue fetchQueue;
	protected Map<String,Integer> mapURL = new HashMap<String, Integer>();
	protected ArrayList<URL> feededUrl = new ArrayList<URL>();
	
	private final HttpClient httpClient = new HttpClient();

	/**
	 * true is the crawler task is finished,otherwise task failure.
	 */
	protected volatile boolean isFinished = false;

	private AtomicLong lastRequestStart;

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

	/**
	 * Add paging URL to specified collection.
	 * 
	 * @param urlSet
	 * @throws RuntimeException
	 */
	/*
	 * private void addPagingUrl(Set<URL> urlSet) throws RuntimeException { int
	 * pageNo = config.getMaxPageNo(); Set<URL> pagingSet = new HashSet<URL>();
	 * for (URL url : urlSet) { String urlStr = url.toFullString(); while
	 * (pageNo >= 0) { String pageKey = config.getNextPageRegex().substring(0,
	 * config.getNextPageRegex().length() - 1) + pageNo; String target =
	 * urlStr.replace(config.getNextPageRegex(), pageKey);
	 * pagingSet.add(URL.valueOf(target)); pageNo--; } }
	 * urlSet.addAll(pagingSet); }
	 */

	public BaseCrawler(final Site config) {
		this.config = config;
		
	}
	public void addLink(URL url){
		if(!mapURL.containsKey(url.toFullString())){
			
			fetchQueue.addFetchItem(url);
			feededUrl.add(url);
			mapURL.put(url.toFullString(), feededUrl.size());
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

	/**
	 * Crawl or update
	 * 
	 * @throws RuntimeException
	 */
	/*
	public void crawl() throws RuntimeException {
		fe
		try {
			feededUrl = getListPageURLs();
			if (feededUrl != null) {
				for (URL listURL : feededUrl) {
					Set<URL> urls = UrlUtil.extracting(listURL,
							config.getElementIdOrRegex());
					if (urls != null) {
						urlsToCrawl.addAll(urls);
					}

				}
			}
			// Object[] url = urlsToCrawl.toArray();
			// Set<URL> pageURLs = getTargetPageURLs();
			// if (pageURLs != null) {
			// urlsToCrawl.addAll(pageURLs);
			// }

			// Collection<URL> urlsToUpdate = getUrlsToUpdate();

			if (urlsToCrawl == null) {
				throw new IllegalArgumentException(
						"urlsToCrawl must not be null!");
			}

			toCrawlTotals += urlsToCrawl.size();
			parsing(config.getName(), urlsToCrawl, config, false);

			isFinished = true;

		} catch (RuntimeException e) {
			isFinished = false;
			throw e;
		}
	}
	*/
	public Site getConfig() {
		return config;
	}

	/**
	 * Get a httpClient wrapper instance.
	 * 
	 * @return httpClient wrapper instance.
	 */
	public HttpClient getHttpClient() {
		return httpClient;
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

	/**
	 * The urlsToCrawl will exclude the collection.
	 * 
	 * @return exclude collection
	 */
	// public abstract Collection<URL> getUrlsToFilter();;

	/**
	 * The urlsToUpdate collection will be return.
	 * 
	 * @return urlsToUpdate
	 */
	// public abstract Collection<URL> getUrlsToUpdate();

	@Override
	public boolean isFinished() {
		return isFinished;
	}



	/**
	 * Crawl and parse the Web page in the specified collection.
	 * 
	 * @param threadName
	 * @param set
	 * @param config
	 * @param isUpdate
	 * @throws RuntimeException
	

	private void parsing(final String threadName, final Collection<URL> set,
			final Site config, final boolean isUpdate) throws RuntimeException {
		if (set == null) {
			throw new IllegalArgumentException("urlsToCrawl must not be null!");
		}
		if (isUpdate) {
			logger.info(config.getName() + " ready to update " + set.size()
					+ " pages");
		} else {
			logger.info(config.getName() + " ready to crawl " + set.size()
					+ " pages");
		}

		try {
		
			for (URL url : set) {
				this.sleep(config.getDelay());
				HttpFetchResult result = httpClient.requestHttpGet(config, url);
				if (result != null && StringUtils.isNotEmpty(result.getHtml())) {
					this.getParser().parse(result, url.toFullString(),
							threadName, isUpdate);
					result.consume();
				}
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
/*
	public void start() {
		if (config == null) {
			throw new IllegalArgumentException(
					"CrawlConfig instance can not be null!");
		}
		try {
			while (!Thread.interrupted()) {
				onBeforeStart();
				//crawl();
				Thread.sleep(1000L);
			}
		} catch (InterruptedException e) {
		} catch (RuntimeException e) {
			logger.error(config.getName() + " " + e.getMessage());
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
*/
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
		
		
		for (int i = 0; i < 5	; i++) {
            FetcherThread fetcherThread = new FetcherThread();
            fetcherThread.start();
        }
		
		// final ArrayList<Thread> threads = new
		// ArrayList<Thread>(crawlers.size());
		// for (ICrawler crawler : crawlers) {
		//final Thread crawlerThread = new Thread(this);
		// threads.add(crawlerThread);
		// }
		//crawlerThread.start();
		final long startTime = System.currentTimeMillis();
		// starting crawler thread
		// for (Thread t : threads) {
		// t.start();
		// }

		final int startedThreadSize = 1;
/*
		Thread monitorThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String unfinishedCrawler = "";
					int unfinished = 0;
					while (true) {
						sleep(50);

						Thread t = crawlerThread;
						if (t != null) {

							if (t.isAlive() && isFinished()) {
								logger.info(getConfig().getName()
										+ " was crawled total of "
										+ getTotalsToCrawl() + " pages.");
								onBeforeExit();
								t.interrupt();

								break;
							} else if (!t.isAlive() && !isFinished()) {
								unfinished++;
								unfinishedCrawler += t.getName() + " ";

								break;
							}
						}

					}

					long endTime = (System.currentTimeMillis() - startTime)
							/ (1000 * 60);
					logger.info("All of the crawlers are stopped ... used "
							+ endTime + " minutes," + startedThreadSize
							+ " threads are started , " + unfinished
							+ " threads are unfinished. ");
					if (StringUtils.isNotEmpty(unfinishedCrawler)) {
						logger.info(unfinishedCrawler + " is unfinished !");
					}

					// shutdown the HTTP connection
					HttpConnectionManager.shutdown();

				} catch (Throwable e) {
					e.printStackTrace();
					logger.error("Monitor Thread Error " + e.getMessage(), e);
				}

			}

		}, "Monitor");
		monitorThread.start();*/

	}

}
