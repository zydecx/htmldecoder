package com.debugtoday.htmldecoder.output;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.object.TagOutputArg;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class TagOutput implements Output {
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public TagOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		TagOutputArg arg = (TagOutputArg) object;
		
		Template template = theme.getTemplates().get(getTemplateKey());
		String templateFullText = template.getFullText()
				.replaceAll(GeneralDecoder.formatArgumentRegex("title"), arg.getTitle())
				.replaceAll(GeneralDecoder.formatArgumentRegex("url"), arg.getUrl())
				.replaceAll(GeneralDecoder.formatArgumentRegex("article_num"), "" + arg.getArticleNum());
		
		return templateFullText;
	}
	
	protected TemplateKey getTemplateKey() {
		return TemplateKey.TAG;
	}

}
