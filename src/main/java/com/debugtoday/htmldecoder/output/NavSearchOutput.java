package com.debugtoday.htmldecoder.output;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.object.NavItemOutputArg;
import com.debugtoday.htmldecoder.output.object.NavOutputArg;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class NavSearchOutput implements Output {
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public NavSearchOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		
		Template template = theme.getTemplates().get(TemplateKey.NAV_SEARCH);
		
		String templateFullText = template.getFullText()
				.replaceAll(GeneralDecoder.formatArgumentRegex("search_engine"), conf.getConf(Configuration.SEARCH_ENGINE));
		
		
		return templateFullText;
	}

}
