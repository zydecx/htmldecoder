package com.debugtoday.htmldecoder.output;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class PaginationItemOutput extends NavItemOutput {
	
	private static final Logger logger = CommonLog.getLogger();
	
	public PaginationItemOutput(ConfigurationWrapper conf, Theme theme) {
		super(conf, theme);
	}
	
	@Override
	protected TemplateKey getTemplateKey() {
		return TemplateKey.PAGINATION_ITEM;
	}

}
