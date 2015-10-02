package com.debugtoday.htmldecoder.output.object;

import java.util.List;

import com.debugtoday.htmldecoder.struct.Article;

public class SiteOutputArg {
	private List<Article> articleList;
	private List<Article> staticPageList;
	
	public SiteOutputArg(List<Article> articleList, List<Article> staticPageList) {
		this.articleList = articleList;
		this.setStaticPageList(staticPageList);
	}
	
	public List<Article> getArticleList() {
		return articleList;
	}
	public void setArticleList(List<Article> articleList) {
		this.articleList = articleList;
	}
	public List<Article> getStaticPageList() {
		return staticPageList;
	}

	public void setStaticPageList(List<Article> staticPageList) {
		this.staticPageList = staticPageList;
	}
}
