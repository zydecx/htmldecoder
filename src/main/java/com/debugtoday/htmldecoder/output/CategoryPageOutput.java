package com.debugtoday.htmldecoder.output;

import java.util.List;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.object.TagWrapper;
import com.debugtoday.htmldecoder.output.object.TemplateFullTextWrapper;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class CategoryPageOutput implements Output {
	
	private ConfigurationWrapper conf;
	private Theme theme;
	private TemplateFullTextWrapper templateFullTextWrapper;
	
	public CategoryPageOutput(ConfigurationWrapper conf, Theme theme, TemplateFullTextWrapper templateFullTextWrapper) {
		this.conf = conf;
		this.theme = theme;
		this.templateFullTextWrapper = templateFullTextWrapper;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		List<TagWrapper> categoryList = (List<TagWrapper>) object;
		
		
		
		StringBuilder sb = new StringBuilder();
		for (Article article : articleList) {
			
		}
		return null;
	}

}
