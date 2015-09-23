package com.debugtoday.htmldecoder.struct;

import java.io.File;
import java.util.Date;

import com.debugtoday.htmldecoder.struct.html.Element;

public class Article extends Document {
	
	private boolean enabled;
	private Element title;
	private String abstractContent;
	private String[] keyword;
	private String[] categories;
	private Element head;
	private Element body;
	
	public Article(File file) {
		super(file);
	}
	
	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Element getTitle() {
		return title;
	}

	public void setTitle(Element title) {
		this.title = title;
	}

	public String getAbstractContent() {
		return abstractContent;
	}
	public void setAbstractContent(String abstractContent) {
		this.abstractContent = abstractContent;
	}
	public String[] getKeyword() {
		return keyword;
	}
	public void setKeyword(String[] keyword) {
		this.keyword = keyword;
	}
	public String[] getCategories() {
		return categories;
	}
	public void setCategories(String[] categories) {
		this.categories = categories;
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
	
	public ArticleAbstract formatArticleAbsract() {
		ArticleAbstract articleAbstract = new ArticleAbstract();
		articleAbstract.setTitle(title == null ? getFile().getName() : title.getContentText());
		articleAbstract.setAbstractContent(abstractContent);
		articleAbstract.setCategories(categories);
		articleAbstract.setKeyword(keyword);
		articleAbstract.setCreateDate(new Date(getFile().lastModified()));
		articleAbstract.setLastUpdateDate(new Date(getFile().lastModified()));
		
		return articleAbstract;
	}

}
