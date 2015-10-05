package com.debugtoday.htmldecoder.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.decoder.ThemeDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;

/**
 * wrapper of Configuration class.<br>
 * this class parse some configuration to special format for convenient further use.
 * @author zydecx
 *
 */
public class ConfigurationWrapper {
	
	private static final Logger logger = CommonLog.getLogger();
	
	private String siteUrl;
	
	private File workspaceFile;
	private File contentFile;
	private File outputFile;
	private File staticPageFile;
	private File themeFile;
	
	private String[] ignoreList;
	private int defaultPagination;
	private int tagPagination;
	private int categoryPagination;
	
	private boolean navTagEnabled;
	private boolean navCategoryEnabled;
	private boolean navRecentEnabled;
	private boolean navSearchEnabled;
	
	private boolean articleHeaderEnabled;
	
	private Configuration configuration;
	
	/**
	 * parse ConfigurationWrapper object from Configuration object.<br>
	 * To be noticed, it DONOT check if a file is valid. However, for null or invalid integer value, exception will be throwned.
	 * @param conf
	 * @return
	 * @throws GeneralException
	 */
	public static ConfigurationWrapper parse(Configuration conf) throws GeneralException {
		ConfigurationWrapper wrapper = new ConfigurationWrapper();
		wrapper.setConfiguration(conf);
		
		File workspaceFile = new File(conf.getConf(Configuration.WORKSPACE_PATH));
		File contentFile = new File(workspaceFile.getAbsolutePath() + File.separator + conf.getConf(Configuration.CONTENT_PATH));
		wrapper.setWorkspaceFile(workspaceFile);
		wrapper.setContentFile(contentFile);
		wrapper.setOutputFile(new File(workspaceFile.getAbsolutePath() + File.separator + conf.getConf(Configuration.OUTPUT_PATH)));
		wrapper.setThemeFile(new File(workspaceFile.getAbsolutePath() + File.separator + conf.getConf(Configuration.THEME_PATH)));
		wrapper.setStaticPageFile(new File(contentFile.getAbsolutePath() + File.separator + conf.getConf(Configuration.STATICPAGE_PATH)));
		
		String[] ignoreList = conf.getConf(Configuration.IGNORE_LIST).split(",");
		for (int i = 0, length = ignoreList.length; i < length; i++) {
			String s = ignoreList[i];
			if (s.startsWith(" ") || s.endsWith(" ")) {
				ignoreList[i] = s.trim();
			}
		}
		wrapper.setIgnoreList(ignoreList);
		
		wrapper.setDefaultPagination(Integer.parseInt(conf.getConf(Configuration.DEFAULT_PAGINATION)));
		wrapper.setTagPagination(Integer.parseInt(conf.getConf(Configuration.TAG_PAGINATION)));
		wrapper.setCategoryPagination(Integer.parseInt(conf.getConf(Configuration.CATEGORY_PAGINATION)));

		wrapper.setNavTagEnabled(Boolean.parseBoolean(conf.getConf(Configuration.NAV_TAG_ENABLED)));
		wrapper.setNavCategoryEnabled(Boolean.parseBoolean(conf.getConf(Configuration.NAV_CATEGORY_ENABLED)));
		wrapper.setNavRecentEnabled(Boolean.parseBoolean(conf.getConf(Configuration.NAV_RECENT_ENABLED)));
		wrapper.setNavSearchEnabled(Boolean.parseBoolean(conf.getConf(Configuration.NAV_SEARCH_ENABLED)));
		
		wrapper.setArticleHeaderEnabled(Boolean.parseBoolean(conf.getConf(Configuration.ARTICLE_HEADER_ENABLED)));
		
		String siteUrl = conf.getConf(Configuration.SITE_URL);
		if (siteUrl == null) {
			siteUrl = ".";
		} else if (siteUrl.endsWith("/")) {
			siteUrl = siteUrl.substring(0, siteUrl.length() - 1);
		}
		wrapper.setSiteUrl(siteUrl);
		
		return wrapper;
	}
	
	/**
	 * Check if current configuration is valid, check points include:<br>
	 * * if any configuration value is null<br>
	 * * if content file is a valid directory<br>
	 * * if current theme folder exists(ignore if default)<br>
	 * * if pagination value is position integer<br>
	 * <br>Exception will be throwed if not.
	 * @return
	 * @throws GeneralException 
	 */
	public boolean check() throws GeneralException {
		Map<String, String> conf;
		try {
			conf = configuration.getConf();
		} catch (GeneralException e) {
			throw new GeneralException("configuration not initialized");
		}
		
		for (Entry<String, String> entry : conf.entrySet()) {
			if (entry.getValue() == null) {
				throw new GeneralException("configuration[" + entry.getKey() + "] is null");
			}
		}
		
		if (!contentFile.isDirectory()) {
			throw new GeneralException("content file [" + contentFile.getAbsolutePath() + "] is not a valid folder");
		}
		
		String currentTheme = configuration.getConf(Configuration.CURRENT_THEME);
		File currentThemeFile = new File(themeFile.getAbsolutePath() + File.separator + currentTheme);
		if (!currentThemeFile.isDirectory() && !ThemeDecoder.isDefaultThemeExisted(currentTheme)) {
			throw new GeneralException("theme file [" + currentThemeFile.getAbsolutePath() + "] is not a valid folder");
		}
		
		if (configuration.getConf(Configuration.MARKDOWN_INTERPRETER).equals("pandoc")
				&& configuration.getConf(Configuration.MARKDOWN_INTERPRETER_PANDOC).equals("")) {
			throw new GeneralException("pandoc interpreter is empty");
		}
		
		if (defaultPagination <= 0) {
			throw new GeneralException("default pagination [" + defaultPagination + "] is invalid");
		}
		
		if (tagPagination <= 0) {
			throw new GeneralException("tag pagination [" + tagPagination + "] is invalid");
		}
		
		if (categoryPagination <= 0) {
			throw new GeneralException("category pagination [" + categoryPagination + "] is invalid");
		}
		
		return true;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public File getWorkspaceFile() {
		return workspaceFile;
	}

	public void setWorkspaceFile(File workspaceFile) {
		this.workspaceFile = workspaceFile;
	}

	public File getContentFile() {
		return contentFile;
	}

	public void setContentFile(File contentFile) {
		this.contentFile = contentFile;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public File getStaticPageFile() {
		return staticPageFile;
	}

	public void setStaticPageFile(File staticPageFile) {
		this.staticPageFile = staticPageFile;
	}

	public File getThemeFile() {
		return themeFile;
	}

	public void setThemeFile(File themeFile) {
		this.themeFile = themeFile;
	}

	public String[] getIgnoreList() {
		return ignoreList;
	}

	public void setIgnoreList(String[] ignoreList) {
		this.ignoreList = ignoreList;
	}

	public int getDefaultPagination() {
		return defaultPagination;
	}

	public void setDefaultPagination(int defaultPagination) {
		this.defaultPagination = defaultPagination;
	}

	public int getTagPagination() {
		return tagPagination;
	}

	public void setTagPagination(int tagPagination) {
		this.tagPagination = tagPagination;
	}

	public int getCategoryPagination() {
		return categoryPagination;
	}

	public void setCategoryPagination(int categoryPagination) {
		this.categoryPagination = categoryPagination;
	}

	public boolean isNavTagEnabled() {
		return navTagEnabled;
	}

	public void setNavTagEnabled(boolean navTagEnabled) {
		this.navTagEnabled = navTagEnabled;
	}

	public boolean isNavCategoryEnabled() {
		return navCategoryEnabled;
	}

	public void setNavCategoryEnabled(boolean navCategoryEnabled) {
		this.navCategoryEnabled = navCategoryEnabled;
	}

	public boolean isNavRecentEnabled() {
		return navRecentEnabled;
	}

	public void setNavRecentEnabled(boolean navRecentEnabled) {
		this.navRecentEnabled = navRecentEnabled;
	}

	public boolean isNavSearchEnabled() {
		return navSearchEnabled;
	}

	public void setNavSearchEnabled(boolean navSearchEnabled) {
		this.navSearchEnabled = navSearchEnabled;
	}

	public boolean isArticleHeaderEnabled() {
		return articleHeaderEnabled;
	}

	public void setArticleHeaderEnabled(boolean articleHeaderEnabled) {
		this.articleHeaderEnabled = articleHeaderEnabled;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public String getConf(String key) throws GeneralException {
		return configuration.getConf(key);
	}

}
