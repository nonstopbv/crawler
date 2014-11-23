package vn.edu.vnu.uet.crawler.demo;

import org.jsoup.nodes.Document;

import vn.edu.vnu.uet.crawler.core.FileSystemOutput;
import vn.edu.vnu.uet.crawler.core.Parser;

public class FilePrase implements Parser {

	private String root = "data";

	@Override
	public void parse(Document doc, String url) {
		try {
			FileSystemOutput fileSystemOutput = new FileSystemOutput(root);
			//String text = doc.text();
			StringBuilder str = new StringBuilder();
			str.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/></head>");
			String text = doc.select("#left_calculator").html();
			str.append(text);
			str.append("</html>");
			if (str != null && str.length() > 0) {

				fileSystemOutput.output(url, str.toString());
			}
			//String title = doc.title();

			//logger.info(title);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}



}
