package com.debugtoday.htmldecoder.output;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.output.object.ArticleOutputArg;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class ArticleOutput implements Output {
	
	private static final Logger logger = CommonLog.getLogger();
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public ArticleOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		ArticleOutputArg arg = (ArticleOutputArg) object;
		Article article = arg.getArticle();
		
		Template template = theme.getTemplates().get(TemplateKey.ARTICLE);
		String templateFullText = template.getFullText()
				.replaceAll(GeneralDecoder.formatArgumentRegex("url"), article.formatUrl(conf.getSiteUrl()))
				.replaceAll(GeneralDecoder.formatArgumentRegex("title"), article.extractTitle())
				.replaceAll(GeneralDecoder.formatArgumentRegex("time"), article.getMeta().getCreateDate() == null ? "" : article.getMeta().getCreateDate().toString())
				.replaceAll(GeneralDecoder.formatArgumentRegex("author"), article.getMeta().getAuthor())
				.replaceAll(GeneralDecoder.formatArgumentRegex("author_url"), article.getMeta().getAuthorUrl())
				.replaceAll(GeneralDecoder.formatArgumentRegex("article_header_display"), conf.isArticleHeaderEnabled() ? "" : "display:none;");
		
		templateFullText = templateFullText
				.replaceAll(
						GeneralDecoder.formatPlaceholderRegex(TemplateKey.ARTICLE_MEDIA_CATEGORY.getKey()),
						new ArticleMediaCategoryOutput(conf, theme).export(article.getMeta().getCategories()))
				.replaceAll(
						GeneralDecoder.formatPlaceholderRegex(TemplateKey.ARTICLE_MEDIA_TAG.getKey()),
						new ArticleMediaTagOutput(conf, theme).export(article.getMeta().getTags()));

		if (arg.isOutputExcerptOnly()) {
			templateFullText = templateFullText
					.replaceAll(
							GeneralDecoder.formatArgumentRegex("content"),
							article.extractExcerpt())
					.replaceAll(
							GeneralDecoder.formatPlaceholderRegex(TemplateKey.ARTICLE_MORE.getKey()),
							new ArticleMoreOutput(conf, theme).export(article));
		} else {

			templateFullText = templateFullText
					.replaceAll(
							GeneralDecoder.formatArgumentRegex("content"),
							article.getBody().getContentText());
		}
		
		return templateFullText;
	}

}
