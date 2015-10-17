package com.debugtoday.htmldecoder.output;

import java.util.regex.Matcher;

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
		logger.info("[[[" + article.getFile().getAbsolutePath());
		Template template = theme.getTemplates().get(TemplateKey.ARTICLE);
		String templateFullText = template.getFullText()
				.replace(GeneralDecoder.formatArgumentRegex("url"), article.formatUrl(conf.getSiteUrl()))
				.replace(GeneralDecoder.formatArgumentRegex("title"), article.extractTitle())
				.replace(GeneralDecoder.formatArgumentRegex("time"), article.getMeta().getCreateDate() == null ? "" : article.getMeta().getCreateDate().toString())
				.replace(GeneralDecoder.formatArgumentRegex("author"), article.getMeta().getAuthor())
				.replace(GeneralDecoder.formatArgumentRegex("author_url"), article.getMeta().getAuthorUrl())
				.replace(GeneralDecoder.formatArgumentRegex("article_header_display"), conf.isArticleHeaderEnabled() ? "" : "display:none;");
		
		templateFullText = templateFullText
				.replace(
						GeneralDecoder.formatPlaceholderRegex(TemplateKey.ARTICLE_MEDIA_CATEGORY.getKey()),
						new ArticleMediaCategoryOutput(conf, theme).export(article.getMeta().getCategories()))
				.replace(
						GeneralDecoder.formatPlaceholderRegex(TemplateKey.ARTICLE_MEDIA_TAG.getKey()),
						new ArticleMediaTagOutput(conf, theme).export(article.getMeta().getTags()));

		if (arg.isOutputExcerptOnly()) {
			templateFullText = templateFullText
					.replace(
							GeneralDecoder.formatArgumentRegex("content"),
							article.extractExcerpt())
					.replace(
							GeneralDecoder.formatPlaceholderRegex(TemplateKey.ARTICLE_MORE.getKey()),
							article.getMore() == null ? "" : new ArticleMoreOutput(conf, theme).export(article));
		} else {

			templateFullText = templateFullText
					.replace(
							GeneralDecoder.formatArgumentRegex("content"),
							article.getBody().getContentText());
		}
		
		return templateFullText;
	}

}
