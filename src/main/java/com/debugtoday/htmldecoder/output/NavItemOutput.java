package com.debugtoday.htmldecoder.output;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.object.NavItemOutputArg;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class NavItemOutput implements Output {
	
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
		
		Template template = theme.getTemplates().get(TemplateKey.NAV);

		String templateFullText = template.getFullText()
				.replaceAll(GeneralDecoder.formatArgumentRegex("url"), arg.getUrl())
				.replaceAll(GeneralDecoder.formatArgumentRegex("title"), arg.getTitle());
		
		return templateFullText;
	}

}
