package com.debugtoday.htmldecoder.output;

import java.util.List;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class StaticPageOutput implements Output {
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public StaticPageOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		List<Article> arg = (List<Article>) object;
		
		Template template = theme.getTemplates().get(TemplateKey.STATIC_PAGE);
		String templateFullText = template.getFullText();
		
		StringBuilder sb = new StringBuilder();
		for (Article article : arg) {
			sb.append(templateFullText
					.replaceAll(
							GeneralDecoder.formatArgumentRegex("url"),
							article.formatUrl(conf.getSiteUrl()))
					.replaceAll(
							GeneralDecoder.formatArgumentRegex("title"),
							article.getTitle().getContentText()));
		}
		
		return sb.toString();
	}

}
