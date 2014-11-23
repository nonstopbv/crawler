package vn.edu.vnu.uet.crawler.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class UrlGrapManagement {
	private static final String folder = "data_urlgrap";
	private static final int bufferSize = 128;
	private BufferedWriter fileInput;
	private BufferedWriter fileOuput;

	public class PrintThread extends Thread {

		@Override
		public void run() {
			while (true) {
				try {
					if (input.isEmpty() == false) {

						fileInput.write(input.poll().toString() + "\n");

					}
					if (result.isEmpty() == false) {

						fileOuput.write(result.poll().toString() + "\n");

					}

					Thread.sleep(100);
				} catch (InterruptedException e) {

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public UrlGrapManagement(Config config) {
		super();
		try {
			File fInput = new File(folder, config.getName() + "_input.txt");
			File fOutput = new File(folder, config.getName() + "_output.txt");
			fileInput = new BufferedWriter(new FileWriter(fInput), bufferSize);
			fileOuput = new BufferedWriter(new FileWriter(fOutput), bufferSize);
			new PrintThread().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class UrlGrap {
		public Integer id;
		public Integer[] edgeId;

		public UrlGrap(Integer id, Integer[] edgeId) {
			super();
			this.id = id;
			this.edgeId = edgeId;
		}

		@Override
		public String toString() {
			return id + ": " + Arrays.toString(edgeId);
		}

	}

	private final Queue<UrlGrap> result = new LinkedList<UrlGrap>();
	private final Queue<UrlGrap> input = new LinkedList<UrlGrap>();
	private final Queue<UrlGrap> urlGrapQueue = new LinkedList<UrlGrap>();

	public void add(Integer id, Integer[] edgeId) {
		UrlGrap urlGrap = new UrlGrap(id, edgeId);
		input.add(urlGrap);
		urlGrapQueue.add(urlGrap);
		UrlGrap urlGrapBest = compressUrlGrap(urlGrap);
		System.out.println(urlGrapBest);
		result.add(urlGrapBest);
		if (urlGrapQueue.size() > 8) {
			urlGrapQueue.poll();
		}

	}

	public UrlGrap compressUrlGrap(UrlGrap urlGrapTop) {

		UrlGrap urlGrapMax = urlGrapTop;
		Iterator<UrlGrap> iterator = urlGrapQueue.iterator();
		while (iterator.hasNext()) {
			UrlGrap urlGrap = iterator.next();
			if (!iterator.hasNext())
				break;
			UrlGrap urlGrapcompare = caculator(urlGrapTop, urlGrap);
			if (urlGrapcompare.edgeId.length < urlGrapMax.edgeId.length) {
				urlGrapMax = urlGrapcompare;
			}

		}

		return urlGrapMax;

	}

	public void prinResult() {
		System.out.println("Input");
		for (UrlGrap urlGrap : input) {
			System.out.println(urlGrap);
		}
		System.out.println("Result");
		for (UrlGrap urlGrap : result) {
			System.out.println(urlGrap);
		}
	}

	public UrlGrap caculator(UrlGrap urlGrapTop, UrlGrap urlGrap) {
		List<Integer> edgeCompress = new ArrayList<Integer>();
		edgeCompress.add(-urlGrap.id);
		int pedge = 0;
		for (int edge : urlGrapTop.edgeId) {
			if (pedge >= urlGrap.edgeId.length || edge < urlGrap.edgeId[pedge]) {
				edgeCompress.add(edge);
				continue;
			}
			while (pedge < urlGrap.edgeId.length && edge > urlGrap.edgeId[pedge]) {
				edgeCompress.add(-urlGrap.edgeId[pedge]);
				pedge++;
			}
			if (pedge < urlGrap.edgeId.length && edge == urlGrap.edgeId[pedge])
				++pedge;
		}
		while (pedge < urlGrap.edgeId.length) {
			edgeCompress.add(-urlGrap.edgeId[pedge++]);
		}

		return new UrlGrap(urlGrapTop.id, edgeCompress.toArray(new Integer[edgeCompress.size()]));
	}

	public static void main(String[] arg) {
		UrlGrapManagement url = new UrlGrapManagement(null);
		Integer[][] test = { { 1, 2, 4, 8, 16, 32, 64 }, { 1, 4, 9, 16, 25, 36, 49, 64 }, { 1, 2, 4, 8, 16, 32, 64 },
				{ 1, 4, 8, 16, 25, 36, 49, 64 } };
		for (int i = 0; i < test.length; ++i) {
			url.add(i + 1, test[i]);
		}
		url.prinResult();

	}
}
