package com.debugtoday.htmldecoder.output;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.output.object.NavItemOutputArg;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class NavItemOutput implements Output {
	
	private static final Logger logger = CommonLog.getLogger();
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public NavItemOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		NavItemOutputArg arg = (NavItemOutputArg) object;
		
		Template template = theme.getTemplates().get(getTemplateKey());
		String templateFullText = template.getFullText()
				.replace(GeneralDecoder.formatArgumentRegex("title"), arg.getTitle())
				.replace(GeneralDecoder.formatArgumentRegex("url"), arg.getUrl());
		
		return templateFullText;
	}
	
	protected TemplateKey getTemplateKey() {
		return TemplateKey.NAV_ITEM;
	}

}
