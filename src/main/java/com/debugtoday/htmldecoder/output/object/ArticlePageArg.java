package com.debugtoday.htmldecoder.output.object;

import java.io.File;
import java.util.List;

import com.debugtoday.htmldecoder.struct.Article;

public class ArticlePageArg {
	private String pageTitle;
	private String bodyTitle;
	private List<Article> articleList;
	private String rootUrl;
	private File rootFile;
	
	public ArticlePageArg(String pageTitle, String bodyTitle, String rootUrl, File rootFile, List<Article> articleList) {
		this.pageTitle = pageTitle;
		this.bodyTitle = bodyTitle;
		this.rootUrl = rootUrl;
		this.rootFile = rootFile;
		this.articleList = articleList;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getBodyTitle() {
		return bodyTitle;
	}

	public void setBodyTitle(String bodyTitle) {
		this.bodyTitle = bodyTitle;
	}

	public List<Article> getArticleList() {
		return articleList;
	}

	public void setArticleList(List<Article> articleList) {
		this.articleList = articleList;
	}

	public String getRootUrl() {
		return rootUrl;
	}

	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}

	public File getRootFile() {
		return rootFile;
	}

	public void setRootFile(File rootFile) {
		this.rootFile = rootFile;
	}

}
