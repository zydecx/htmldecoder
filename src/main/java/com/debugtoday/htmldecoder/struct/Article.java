package com.debugtoday.htmldecoder.struct;

import java.io.File;
import java.util.Date;

import com.debugtoday.htmldecoder.struct.html.Element;

public class Article extends Document {

	private ArticleMeta meta;
	private String relativePath;
	private Element title;
	private Element head;
	private Element body;
	private Element more;

	public Article(File file) {
		super(file);
	}
	
	public ArticleMeta getMeta() {
		return meta;
	}

	public void setMeta(ArticleMeta meta) {
		this.meta = meta;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public Element getTitle() {
		return title;
	}

	public void setTitle(Element title) {
		this.title = title;
	}

	public Element getHead() {
		return head;
	}
	public void setHead(Element head) {
		this.head = head;
	}
	public Element getBody() {
		return body;
	}
	public void setBody(Element body) {
		this.body = body;
	}
	
	public Element getMore() {
		return more;
	}

	public void setMore(Element more) {
		this.more = more;
	}
	
	/**
	 * extract title of article. Use file name if <title> not defined.
	 * @return
	 */
	public String extractTitle() {
		return title == null ? getFile().getName() : title.getContentText();
	}
	
	/**
	 * extract excerpt of article. Use content of body if more tag not defined
	 * @return
	 */
	public String extractExcerpt() {
		
		if (more == null) {
			return body.getFullText();
		}
		
		return body.getContentText().substring(0, more.getFileStartPos() - body.getFileStartPos() - body.getContentStartPosOffset());
	}
	
	/**
	 * format url of article.
	 * @param siteUrl root url of site
	 * @return
	 */
	public String formatUrl(String siteUrl) {
		return siteUrl + "/" + relativePath;
	}

}
