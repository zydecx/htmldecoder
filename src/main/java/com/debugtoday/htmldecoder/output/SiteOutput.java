package com.debugtoday.htmldecoder.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.debugtoday.htmldecoder.HtmlDecoderJob.TagUtil;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.object.SiteOutputArg;
import com.debugtoday.htmldecoder.output.object.TagWrapper;
import com.debugtoday.htmldecoder.output.object.TemplateFullTextWrapper;
import com.debugtoday.htmldecoder.output.object.TemplateOutputArg;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.ArticleAbstract;
import com.debugtoday.htmldecoder.struct.Theme;

public class SiteOutput implements Output {
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public SiteOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		SiteOutputArg arg = (SiteOutputArg) object;
		
		List<Article> staticPageList = arg.getStaticPageList();
		List<Article> articleList = arg.getArticleList();
		List<TagWrapper> tagList = analyzeArticleTag(articleList);
		List<TagWrapper> categoryList = analyzeArticleCategory(articleList);
		
		// sort article list by time descending
		Collections.sort(articleList, new Comparator<Article>() {
			@Override
			public int compare(Article o1, Article o2) {
				return o2.getMeta().getLastUpdateDate().compareTo(o1.getMeta().getLastUpdateDate());
			}
		});
		
		// sort tag list by article sizes descending
		Collections.sort(tagList, new Comparator<TagWrapper>() {
			@Override
			public int compare(TagWrapper o1, TagWrapper o2) {
				return o2.getArticleSet().size() - o1.getArticleSet().size();
			}
		});
		
		// sort category list by article sizes descending
		Collections.sort(categoryList, new Comparator<TagWrapper>() {
			@Override
			public int compare(TagWrapper o1, TagWrapper o2) {
				return o2.getArticleSet().size() - o1.getArticleSet().size();
			}
		});
		
		// output common part of template, leaving only article part to output in the following 
		String templateOutput = new TemplateOutput(conf, theme).export(new TemplateOutputArg(staticPageList, articleList, tagList, categoryList));
		TemplateFullTextWrapper templateWrapper = new TemplateFullTextWrapper(templateOutput);
		
		// output article/tag/category page, writing to output folder
		new ArticlePageOutput(conf, theme, templateWrapper).export(articleList);
		new TagPageOutput(conf, theme, templateWrapper).export(tagList);
		new CategoryPageOutput(conf, theme, templateWrapper).export(categoryList);
		
		return DONE;
	}
	

	
	private List<TagWrapper> analyzeArticleCategory(List<Article> articleList) {
		Map<String, TagWrapper> categoryMap = new HashMap<>();

		Iterator<Article> iter = articleList.iterator();
		while (iter.hasNext()) {
			Article article = iter.next();
			String[] categories = article.getMeta().getCategories();
			if (categories == null || categories.length == 0) continue;
			
			for (String category : categories) {
				TagWrapper categoryWrapper = categoryMap.get(category);
				if (categoryWrapper == null) {
					categoryWrapper = new TagWrapper(category);
					categoryMap.put(category, categoryWrapper);
				}
				
				categoryWrapper.getArticleSet().add(article);
			}
		}
		
		return new ArrayList<>(categoryMap.values());
	}
	
	private List<TagWrapper> analyzeArticleTag(List<Article> articleList) {
		Map<String, TagWrapper> tagMap = new HashMap<>();

		Iterator<Article> iter = articleList.iterator();
		while (iter.hasNext()) {
			Article article = iter.next();
			String[] tags = article.getMeta().getTags();
			if (tags == null || tags.length == 0) continue;
			
			for (String tag : tags) {
				TagWrapper tagWrapper = tagMap.get(tag);
				if (tagWrapper == null) {
					tagWrapper = new TagWrapper(tag);
					tagMap.put(tag, tagWrapper);
				}
				
				tagWrapper.getArticleSet().add(article);
			}
		}
		
		return new ArrayList<>(tagMap.values());
	}

}
