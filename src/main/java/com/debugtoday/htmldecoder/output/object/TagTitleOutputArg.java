package com.debugtoday.htmldecoder.output.object;

public class TagTitleOutputArg {
	
	private String url;
	private String title;
	
	public TagTitleOutputArg(String title, String url) {
		this.title = title;
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
