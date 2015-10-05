package com.debugtoday.htmldecoder.output;

import java.io.File;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.output.object.NavItemOutputArg;
import com.debugtoday.htmldecoder.output.object.PaginationOutputArg;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class PaginationOutput implements Output {
	
	private static final Logger logger = CommonLog.getLogger();
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public PaginationOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		PaginationOutputArg arg = (PaginationOutputArg) object;
		
		Template template = theme.getTemplates().get(TemplateKey.PAGINATION);
		String templateFullText = template.getFullText();
		
		int size = arg.getSize();
		int index = arg.getIndex();
		String rootUrl = arg.getRootUrl();
		if (size <= 1) {	// donot display pagination where page size less-equal than 1
			return "";
		}
		
		PaginationItemOutput itemOutput = new PaginationItemOutput(conf, theme);
		PaginationItemActiveOutput itemActiveOutput = new PaginationItemActiveOutput(conf, theme);
		PaginationItemDisabledOutput itemDisabledOutput = new PaginationItemDisabledOutput(conf, theme);
		
		
		StringBuilder sb = new StringBuilder();
		NavItemOutputArg itemArg;
		Output output;
		int currentPage;
		
		currentPage = index == 1 ? 1 : (index - 1);
		output = index == 1 ? itemDisabledOutput : itemOutput;
		itemArg = new NavItemOutputArg("Prev", formatPaginationUrl(rootUrl, currentPage));
		sb.append(output.export(itemArg));
		
		for (int i = 1; i <= size; i++) {
			output = i == index ? itemActiveOutput : itemOutput;
			itemArg = new NavItemOutputArg("" + i, formatPaginationUrl(rootUrl, i));
			sb.append(output.export(itemArg));
		}		

		currentPage = index == size ? size : (index + 1);
		output = index == size ? itemDisabledOutput : itemOutput;
		itemArg = new NavItemOutputArg("Next", formatPaginationUrl(rootUrl, currentPage));
		sb.append(output.export(itemArg));
		
		templateFullText = templateFullText.replaceAll(GeneralDecoder.formatPlaceholderRegex(TemplateKey.PAGINATION_ITEM.getKey()), sb.toString());
		return templateFullText;
	}
	
	public static String formatPaginationUrl(String rootUrl, int i) {
		if (i <= 1) {	// in case that calculate previous page of 1st page.
			return rootUrl;
		} else {
			return rootUrl + "/page/" + i; 
		}
	}
	
	public static String formatPaginationFilePath(String rootPath, int index) {
		if (index <= 1) {
			return rootPath + File.separator + "index.html";
		} else {
			return rootPath + File.separator + "page" + File.separator + "index.html";
		}
	}

}
