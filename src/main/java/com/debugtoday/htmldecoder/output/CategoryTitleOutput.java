package com.debugtoday.htmldecoder.output;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class CategoryTitleOutput extends TagTitleOutput {
	
	public CategoryTitleOutput(ConfigurationWrapper conf, Theme theme) {
		super(conf, theme);
	}
	
	@Override
	protected TemplateKey getTemplateKey() {
		return TemplateKey.CATEGORY_TITLE;
	}

}