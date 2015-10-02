package com.debugtoday.htmldecoder.output.object;

import java.util.List;

import com.debugtoday.htmldecoder.struct.Article;

public class TemplateOutputArg {
	private List<Article> articleList;
	private List<Article> staticPageList;
	private List<TagWrapper> tagList;
	private List<TagWrapper> categoryList;
	
	public TemplateOutputArg(List<Article> staticPageList, List<Article> articleList, List<TagWrapper> tagList, List<TagWrapper> categoryList) {
		this.articleList = articleList;
		this.setStaticPageList(staticPageList);
		this.tagList = tagList;
		this.categoryList = categoryList;
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

	public List<TagWrapper> getTagList() {
		return tagList;
	}
	public void setTagList(List<TagWrapper> tagList) {
		this.tagList = tagList;
	}
	public List<TagWrapper> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(List<TagWrapper> categoryList) {
		this.categoryList = categoryList;
	}

}
