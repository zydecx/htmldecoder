package com.debugtoday.htmldecoder.decoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;
import com.debugtoday.htmldecoder.struct.html.Element;

public class ThemeDecoder extends GeneralDecoder {
	
	/**
	 * decode theme file
	 * @param file
	 * @return
	 * @throws GeneralException
	 */
	public static Theme decode(ConfigurationWrapper conf) throws GeneralException {
		String currentTheme = conf.getConfiguration().getConf(Configuration.CURRENT_THEME);
		boolean isDefault = "default".equalsIgnoreCase(currentTheme);
		
		Theme theme = new Theme(currentTheme);
		Map<TemplateKey, Template> templates = theme.getTemplates();
		
		if (isDefault) {
			for (TemplateKey templateKey : TemplateKey.values()) {
				String keyName = templateKey.getKey();
				Template template = TemplateDecoder.decodeDefault(keyName, "/theme/default/" + keyName + ".html", conf);
				templates.put(templateKey, template);
			}
		} else {
			String currentThemePath = conf.getThemeFile().getAbsolutePath() + File.separator;
			for (TemplateKey templateKey : TemplateKey.values()) {
				String keyName = templateKey.getKey();
				Template template = TemplateDecoder.decodeCustomerized(keyName, new File(currentThemePath + keyName + ".html"), conf);
				templates.put(templateKey, template);
			}
		}
		
		return theme;
	}

}
