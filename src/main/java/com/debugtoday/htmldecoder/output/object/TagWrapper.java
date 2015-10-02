package com.debugtoday.htmldecoder.output.object;

import java.util.HashSet;
import java.util.Set;

import com.debugtoday.htmldecoder.struct.Article;

/**
 * Wrap tag/category and it's article set
 * @author zydecx
 *
 */
public class TagWrapper {
	private String name;
	private Set<Article> articleSet;

	
	public TagWrapper(String name) {
		this.name = name;
		this.articleSet = new HashSet<>();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Set<Article> getArticleSet() {
		return this.articleSet;
	}
	
	public void setArticleSet(Set<Article> articleSet) {
		this.articleSet = articleSet;
	}
	
	public static String formatTagUrl(String siteUrl) {
		return siteUrl + "/" + extractTagRelativePath();
	}
	
	public static String formatCategoryUrl(String siteUrl) {
		return siteUrl + "/" + extractCategoryRelativePath();
	}
	
	public static String formatTagUrl(String siteUrl, String name) {
		return formatTagUrl(siteUrl) + "/" + name;
	}
	
	public static String formatCategoryUrl(String siteUrl, String name) {
		return formatCategoryUrl(siteUrl) + "/" + name;
	}
	
	public static String extractTagRelativePath() {
		return "tag";
	}
	
	public static String extractCategoryRelativePath() {
		return "category";
	}
}
