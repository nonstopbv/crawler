package vn.edu.vnu.uet.crawler.task;

import javax.xml.soap.Text;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.edu.vnu.uet.crawler.fetcher.FileSystemOutput;
import vn.edu.vnu.uet.crawler.fetcher.Parser;
import vn.edu.vnu.uet.crawler.util.StringUtils;

public class ParserDemo implements Parser {

	private String root = "data";
	@Override
	public void parse(Document doc, String url) {
		try {
			FileSystemOutput fileSystemOutput = new FileSystemOutput(root);
			//String text = doc.text();
			String text = doc.select("#left_calculator").text();
			if(!StringUtils.isEmpty(text)){
				
			
			fileSystemOutput.output(url, text);
			}
			String title = doc.title();
			
			logger.info(title + " " + url + " ");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static final Logger logger = LoggerFactory
			.getLogger(ParserDemo.class);

}
