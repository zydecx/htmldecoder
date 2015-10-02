package com.debugtoday.htmldecoder.output;

import java.util.ArrayList;
import java.util.List;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.object.NavItemOutputArg;
import com.debugtoday.htmldecoder.output.object.NavOutputArg;
import com.debugtoday.htmldecoder.output.object.TagWrapper;
import com.debugtoday.htmldecoder.output.object.TemplateOutputArg;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

public class TemplateOutput implements Output {
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public TemplateOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		TemplateOutputArg arg = (TemplateOutputArg) object;
		
		Template template = theme.getTemplates().get(TemplateKey.TEMPLATE);
		String templateFullText = replaceConfigurationArguments(template.getFullText(),
				new String[] { Configuration.SITE_TITLE,
						Configuration.SITE_DESCRIPTION,
						Configuration.SITE_GITHUB_HOME,
						Configuration.HOME_STATIC_TITLE });
		
		templateFullText = templateFullText.replaceAll(GeneralDecoder
				.formatPlaceholderRegex(TemplateKey.STATIC_PAGE.getKey()),
				new StaticPageOutput(conf, theme).export(arg.getStaticPageList()));

		NavOutput navOutput = new NavOutput(conf, theme);
		StringBuilder sb = new StringBuilder();
		if (conf.getNavSearchEnabled()) {
			sb.append(navOutput.export(formatNavSearchOutputArg()));
		}
		if (conf.getNavRecentEnabled()) {
			sb.append(navOutput.export(formatNavRecentOutputArg(arg.getArticleList())));
		}
		if (conf.getNavTagEnabled()) {
			sb.append(navOutput.export(formatNavTagOutputArg(arg.getTagList())));
		}
		if (conf.getNavCategoryEnabled()) {
			sb.append(navOutput.export(formatNavCategoryOutputArg(arg.getCategoryList())));
		}
		
		
		templateFullText = templateFullText.replaceAll(GeneralDecoder.formatPlaceholderRegex(TemplateKey.NAV.getKey()), sb.toString());
		
		return templateFullText;
	}
	
	private String replaceConfigurationArguments(String s, String[] confNames) throws GeneralException {
		for (String confName : confNames) {
			s = s.replaceAll(GeneralDecoder.formatArgumentRegex(confName), conf.getConf(confName));
		}
		return s;
	}
	
	private NavOutputArg formatNavRecentOutputArg(List<Article> articleList) throws GeneralException {
		int length = Math.min(articleList.size(), 5);
		
		List<NavItemOutputArg> itemArgList = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			Article article = articleList.get(i);
			String title = article.getTitle().getContentText();
			String url = article.formatUrl(conf.getSiteUrl());
			itemArgList.add(new NavItemOutputArg(title, url));
		}
		
		return new NavOutputArg(conf.getConf(Configuration.RECENT_TITLE), itemArgList);
	}
	
	private NavOutputArg formatNavTagOutputArg(List<TagWrapper> tagList) throws GeneralException {
		int length = Math.min(tagList.size(), 5);
		
		List<NavItemOutputArg> itemArgList = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			TagWrapper tag = tagList.get(i);
			String title = tag.getName();
			String url = TagWrapper.formatTagUrl(conf.getSiteUrl(), title);
			itemArgList.add(new NavItemOutputArg(title, url));
		}
		
		return new NavOutputArg(conf.getConf(Configuration.TAG_TITLE), itemArgList);
	}
	
	private NavOutputArg formatNavCategoryOutputArg(List<TagWrapper> categoryList) throws GeneralException {
		int length = Math.min(categoryList.size(), 5);
		
		List<NavItemOutputArg> itemArgList = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			TagWrapper tag = categoryList.get(i);
			String title = tag.getName();
			String url = TagWrapper.formatCategoryUrl(conf.getSiteUrl(), title);
			itemArgList.add(new NavItemOutputArg(title, url));
		}
		
		return new NavOutputArg(conf.getConf(Configuration.CATEGORY_TITLE), itemArgList);
	}
	
	private NavOutputArg formatNavSearchOutputArg() throws GeneralException {
		return new NavOutputArg(conf.getConf(Configuration.SEARCH_TITLE), new ArrayList<NavItemOutputArg>());
	}

}
