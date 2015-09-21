package com.debugtoday.htmldecoder.struct;

import java.io.File;

import com.debugtoday.htmldecoder.struct.html.Element;

public class Document {
	
	private File file;
	private boolean enabled;
	private String abstractContent;
	private String[] keyword;
	private String[] categories;
	private Element head;
	private Element body;
	
	public Document(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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

}
