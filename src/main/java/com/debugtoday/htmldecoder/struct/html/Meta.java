package com.debugtoday.htmldecoder.struct.html;

public class Meta extends Element {
	private String name;
	private String content;
	private String fullText;
	private int startPos;

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

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public int getStartPos() {
		return startPos;
	}
	
	public static Meta emptyMeta() {
		Meta meta = new Meta();
		meta.setName(null);
		meta.setContent(null);
		meta.setFullText(null);
		meta.setStartPos(-1);
		
		return meta;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}
}
