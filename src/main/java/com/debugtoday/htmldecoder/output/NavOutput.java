package com.debugtoday.htmldecoder.output;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.output.object.NavItemOutputArg;
import com.debugtoday.htmldecoder.output.object.NavOutputArg;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class NavOutput implements Output {
	
	private static final Logger logger = CommonLog.getLogger();
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public NavOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		NavOutputArg arg = (NavOutputArg) object;
		
		Template template = theme.getTemplates().get(TemplateKey.NAV);
		
		NavItemOutput navItemOutput = new NavItemOutput(conf, theme);
		StringBuilder sb = new StringBuilder();
		for (NavItemOutputArg item : arg.getItemList()) {
			sb.append(navItemOutput.export(item));
		}
		String templateFullText = template.getFullText()
				.replace(GeneralDecoder.formatArgumentRegex("title"), arg.getTitle())
				.replace(GeneralDecoder.formatArgumentRegex("url"), arg.getUrl())
				.replace(GeneralDecoder.formatPlaceholderRegex(TemplateKey.NAV_ITEM.getKey()), sb.toString());
		
		
		return templateFullText;
	}

}
