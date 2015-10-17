package com.debugtoday.htmldecoder.output;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.output.object.TagWrapper;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class ArticleMediaCategoryOutput  implements Output {
	
	private static final Logger logger = CommonLog.getLogger();
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public ArticleMediaCategoryOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		String[] arg = (String[]) object;
		
		Template template = theme.getTemplates().get(TemplateKey.ARTICLE_MEDIA_CATEGORY);
		String templateFullText = template.getFullText();
		
		StringBuilder sb = new StringBuilder();
		for (String name : arg) {
			sb.append(templateFullText
					.replace(
							GeneralDecoder.formatArgumentRegex("url"),
							TagWrapper.formatCategoryUrl(conf.getSiteUrl(), name))
					.replace(
							GeneralDecoder.formatArgumentRegex("title"),
							name));
		}
		
		return sb.toString();
	}

}
