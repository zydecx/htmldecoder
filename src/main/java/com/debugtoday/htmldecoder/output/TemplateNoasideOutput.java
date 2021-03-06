package com.debugtoday.htmldecoder.output;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.output.object.NavItemOutputArg;
import com.debugtoday.htmldecoder.output.object.NavOutputArg;
import com.debugtoday.htmldecoder.output.object.TagWrapper;
import com.debugtoday.htmldecoder.output.object.TemplateOutputArg;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class TemplateNoasideOutput implements Output {
	
	private static final Logger logger = CommonLog.getLogger();
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public TemplateNoasideOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		TemplateOutputArg arg = (TemplateOutputArg) object;
		
		Template template = theme.getTemplates().get(TemplateKey.TEMPLATE_NOASIDE);
		
		return exportFromTemplate(template, arg);
	}
	
	protected String exportFromTemplate(Template template, TemplateOutputArg arg) throws GeneralException {
		String templateFullText = replaceConfigurationArguments(template.getFullText(),
				new String[] { Configuration.SITE_TITLE,
						Configuration.SITE_DESCRIPTION,
						Configuration.SITE_GITHUB_HOME,
						Configuration.HOME_STATIC_TITLE });
		
		templateFullText = templateFullText.replace(GeneralDecoder
				.formatPlaceholderRegex("static_page"),
				new StaticPageOutput(conf, theme).export(arg.getStaticPageList()));

		return templateFullText;
	}
	
	private String replaceConfigurationArguments(String s, String[] confNames) throws GeneralException {
		for (String confName : confNames) {
			s = s.replace(GeneralDecoder.formatArgumentRegex(confName), conf.getConf(confName));
		}
		return s;
	}

	protected ConfigurationWrapper getConf() {
		return conf;
	}

	protected Theme getTheme() {
		return theme;
	}

}
