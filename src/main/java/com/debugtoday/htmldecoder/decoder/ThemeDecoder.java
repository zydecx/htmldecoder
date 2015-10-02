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
import com.debugtoday.htmldecoder.util.FileUtil;

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
		
		File themeFile;
		if (isDefault) {
			themeFile = new File(ThemeDecoder.class.getResource("/theme/default").getFile());
		} else {
			themeFile = conf.getThemeFile();
		}
		
		if (!themeFile.isDirectory()) {
			throw new GeneralException("invalid theme file[" + themeFile.getAbsolutePath() + "]");
		}
		
		for (File file : themeFile.listFiles()) {
			TemplateKey templateKey = decodeTemplateFile(file);
			if (templateKey == null) {
				File toFile = new File(conf.getOutputFile().getAbsolutePath() + File.separator + file.getName());
				if (file.isDirectory()) {
					FileUtil.copyDirectory(file, toFile);
				} else {
					FileUtil.copy(file, toFile);
				}
			} else {
				Template template = TemplateDecoder.decodeCustomerized(templateKey.getKey(), file, conf);
				templates.put(templateKey, template);
			}
		}
		
		return theme;
	}

	private static TemplateKey decodeTemplateFile(File file) {
		String fileName = file.getName();
		
		if (!fileName.endsWith(".html") && !fileName.endsWith(".html")) {
			return null;
		}
		
		fileName = fileName.substring(0, fileName.indexOf("."));
		
		return TemplateKey.parseKey(fileName);
	}

}
