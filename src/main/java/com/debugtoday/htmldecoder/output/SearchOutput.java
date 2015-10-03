package com.debugtoday.htmldecoder.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.output.object.FileOutputArg;
import com.debugtoday.htmldecoder.output.object.TemplateFullTextWrapper;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class SearchOutput extends AbstractFileOutput {
	
	private static final Logger logger = CommonLog.getLogger();
	
	private ConfigurationWrapper conf;
	private Theme theme;
	private TemplateFullTextWrapper templateFullTextWrapper;
	
	public SearchOutput(ConfigurationWrapper conf, Theme theme, TemplateFullTextWrapper templateFullTextWrapper) {
		this.conf = conf;
		this.theme = theme;
		this.templateFullTextWrapper = templateFullTextWrapper;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		
		Template template = theme.getTemplates().get(TemplateKey.SEARCH);
		
		String searchEngine = conf.getConf(Configuration.SEARCH_ENGINE);
		TemplateKey engineKey = TemplateKey.parseKey("search_" + searchEngine);
		
		if (engineKey == null) {
			logger.warn("search engine[" + searchEngine + "] not recognized. Fail to create search file.");
			return DONE;
		}
		
		Template engineTemplate = theme.getTemplates().get(engineKey);
		
		String headFullText = template.getHead().getContentText()
				.replaceAll(
						GeneralDecoder.formatPlaceholderRegex("head"),
						exportEnginePageHead(engineTemplate, searchEngine));
		
		String bodyFullText = template.getBody().getContentText()
				.replaceAll(
						GeneralDecoder.formatPlaceholderRegex("body"),
						exportEnginePageBody(engineTemplate, searchEngine));
		
		File file = new File(conf.getOutputFile().getAbsolutePath() + File.separator + template.getFile().getName());
		
		FileOutputArg fileOutputArg = new FileOutputArg();
		fileOutputArg.setHead(headFullText);
		fileOutputArg.setBody(bodyFullText);
		fileOutputArg.setPageTitle(conf.getConf(Configuration.SEARCH_TITLE));
		
		writeToFile(file, templateFullTextWrapper, fileOutputArg);
		
		return DONE;
	}
	
	private String exportEnginePageHead(Template engineTemplate, String engine) throws GeneralException {
		if (engine.equals("google")) {
			return exportGoogleEnginePageHead(engineTemplate);
		} else {
			logger.warn("search engine[" + engine + "] not recognized. Fail to export engine page.");
		}
		
		return "";
	}
	
	private String exportGoogleEnginePageHead(Template template) throws GeneralException {
		String templateFullText = template.getHead().getContentText()
				.replaceAll(
						GeneralDecoder.formatArgumentRegex("customer_search_id"),
						conf.getConf(Configuration.GOOGLE_CUSTOMER_SEARCH_ID));
		return templateFullText;
	}
	
	private String exportEnginePageBody(Template engineTemplate, String engine) throws GeneralException {
		if (engine.equals("google")) {
			return exportGoogleEnginePageBody(engineTemplate);
		} else {
			logger.warn("search engine[" + engine + "] not recognized. Fail to export engine page.");
		}
		
		return "";
	}
	
	private String exportGoogleEnginePageBody(Template template) throws GeneralException {
		String templateFullText = template.getBody().getContentText()
				.replaceAll(
						GeneralDecoder.formatArgumentRegex("customer_search_id"),
						conf.getConf(Configuration.GOOGLE_CUSTOMER_SEARCH_ID));
		return templateFullText;
	}

}
