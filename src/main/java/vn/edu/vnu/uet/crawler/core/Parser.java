package vn.edu.vnu.uet.crawler.core;

import org.jsoup.nodes.Document;

public interface Parser {

	public void parse(Document doc, String url);

}
