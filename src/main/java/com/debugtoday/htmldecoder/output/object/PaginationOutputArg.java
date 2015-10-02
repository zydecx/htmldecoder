package com.debugtoday.htmldecoder.output.object;

public class PaginationOutputArg {

	private String rootUrl;
	private int size;
	private int index;
	
	public PaginationOutputArg(String rootUrl, int size, int index) {
		this.rootUrl = rootUrl;
		this.size = size;
		this.index = index;
	}

	public String getRootUrl() {
		return rootUrl;
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
