package com.debugtoday.htmldecoder.struct.html;

public class Meta extends Element {
	private String name;
	private String content;
	
	public Meta(String name, String content) {
		this.setName(name);
		this.setContent(content);
	}
	
	public Meta() {
		this(null, null);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
