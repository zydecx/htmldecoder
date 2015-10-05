package com.debugtoday.htmldecoder.output;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class PaginationItemDisabledOutput extends PaginationItemOutput {
	
	private static final Logger logger = CommonLog.getLogger();
	
	public PaginationItemDisabledOutput(ConfigurationWrapper conf, Theme theme) {
		super(conf, theme);
	}
	
	@Override
	protected TemplateKey getTemplateKey() {
		return TemplateKey.PAGINATION_ITEM_DISABLED;
	}

}
