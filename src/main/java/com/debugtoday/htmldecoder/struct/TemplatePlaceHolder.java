package com.debugtoday.htmldecoder.struct;

public class TemplatePlaceHolder {
	private String name;
	private int offsetStart;
	private int offsetEnd;
	
	public TemplatePlaceHolder() {}
	
	public TemplatePlaceHolder(String name) {
		this.name = name;
	}
	
	public TemplatePlaceHolder(String name, int offsetStart, int offsetEnd) {
		this.name = name;
		this.offsetStart = offsetStart;
		this.offsetEnd = offsetEnd;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOffsetStart() {
		return offsetStart;
	}
	public void setOffsetStart(int offsetStart) {
		this.offsetStart = offsetStart;
	}
	public int getOffsetEnd() {
		return offsetEnd;
	}
	public void setOffsetEnd(int offsetEnd) {
		this.offsetEnd = offsetEnd;
	}

}
