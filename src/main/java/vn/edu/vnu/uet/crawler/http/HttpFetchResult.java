package vn.edu.vnu.uet.crawler.http;

import java.io.EOFException;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

/**
 * HttpFetchResult
 * 
 * 
 *
 */
public class HttpFetchResult {

	protected String charset;

	protected byte[] contentData;

	protected HttpEntity entity;

	protected String html;

	protected StatusLine status;

	public HttpFetchResult(HttpResponse rep, String defaultCharset)
			throws Exception {
		entity = rep.getEntity();
		status = rep.getStatusLine();
		this.charset = defaultCharset;
		if (entity.getContentType() != null
				&& entity.getContentType().getValue().contains("text/html")) {
			html = EntityUtils.toString(entity, charset);
		} else {
			contentData = EntityUtils.toByteArray(entity);
		}
	}

	/**
	 * Ensures that the entity content is fully consumed and the content stream,
	 * if exists, is closed.
	 * 
	 */
	public void consume() {
		try {
			if (entity != null) {
				EntityUtils.consume(entity);
			}
		} catch (EOFException e) {
			// We can ignore this exception. It can happen on compressed streams
			// which are not
			// repeatable
		} catch (IOException e) {
			// We can ignore this exception. It can happen if the stream is
			// closed.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCharset() {
		return charset;
	}

	public byte[] getContentData() {
		return contentData;
	}

	public HttpEntity getEntity() {
		return entity;
	}

	public String getHtml() {
		return html;
	}

	public StatusLine getStatus() {
		return status;
	}

}
