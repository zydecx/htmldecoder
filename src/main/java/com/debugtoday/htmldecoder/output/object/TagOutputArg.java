package com.debugtoday.htmldecoder.output.object;

public class TagOutputArg {
	
	private String url;
	private String title;
	private int articleNum;
	
	public TagOutputArg(String title, String url, int articleNum) {
		this.title = title;
		this.url = url;
		this.articleNum = articleNum;
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

	public int getArticleNum() {
		return articleNum;
	}

	public void setArticleNum(int articleNum) {
		this.articleNum = articleNum;
	}

}
