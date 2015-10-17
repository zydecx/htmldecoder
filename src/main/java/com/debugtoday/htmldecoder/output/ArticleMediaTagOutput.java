package com.debugtoday.htmldecoder.output;

import java.util.List;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.output.object.TagWrapper;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class ArticleMediaTagOutput implements Output {
	
	private static final Logger logger = CommonLog.getLogger();
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public ArticleMediaTagOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		String[] arg = (String[]) object;
		
		Template template = theme.getTemplates().get(TemplateKey.ARTICLE_MEDIA_TAG);
		String templateFullText = template.getFullText();
		
		StringBuilder sb = new StringBuilder();
		for (String name : arg) {
			sb.append(templateFullText
					.replace(
							GeneralDecoder.formatArgumentRegex("url"),
							TagWrapper.formatTagUrl(conf.getSiteUrl(), name))
					.replace(
							GeneralDecoder.formatArgumentRegex("title"),
							name));
		}
		
		return sb.toString();
	}

}
