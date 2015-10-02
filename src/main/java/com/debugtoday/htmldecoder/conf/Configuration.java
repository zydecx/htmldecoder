package com.debugtoday.htmldecoder.conf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.debugtoday.htmldecoder.exception.GeneralException;

/**
 * <strong>Configuration</strong> is an abstract class, which defines constant name of configurations and processes their getting/setting.<br>
 * User should extend this class and implement <i>readConf()</i> method to read configuration whether from file or dialog.<br>
 * Before trying to get any configuraiton, call <i>init()</i>, in which <i>readConf()</i> is called to read configuration.<br>
 * To be noticed, default configuration will be read first, as you can refer to at <i>configuration.properties</i>. So it's OK if user doesn't configure all items. 
 * @author zydecx
 *
 */
public abstract class Configuration {
	public static final String SITE_URL = "site_url";
	public static final String SITE_TITLE = "site_title";
	public static final String SITE_DESCRIPTION = "site_description";
	public static final String SITE_GITHUB_HOME = "site_github_home";
	
	public static final String HOME_STATIC_TITLE = "home_static_title";
	
	public static final String TAG_TITLE = "tag_title";
	public static final String CATEGORY_TITLE = "category_title";
	public static final String RECENT_TITLE = "recent_title";
	public static final String SEARCH_TITLE = "search_title";
	
	public static final String WORKSPACE_PATH = "workspace";
	
	public static final String CONTENT_PATH = "content";
	public static final String OUTPUT_PATH = "output";
	public static final String STATICPAGE_PATH = "staticpage";
	public static final String THEME_PATH = "theme";
	
	public static final String CURRENT_THEME = "current_theme";
	
	public static final String IGNORE_LIST = "ignore_list";
	
	public static final String DEFAULT_PAGINATION = "default_pagination";
	public static final String TAG_PAGINATION = "tag_pagination";
	public static final String CATEGORY_PAGINATION = "category_pagination";
	
	public static final String NAV_TAG_ENABLED = "nav_tag_enabled";
	public static final String NAV_CATEGORY_ENABLED = "nav_category_enabled";
	public static final String NAV_RECENT_ENABLED = "nav_recent_enabled";
	public static final String NAV_SEARCH_ENABLED = "nav_search_enabled";
	
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	private Map<String, String> conf = new HashMap<>();
	
	private String confMode;	// NOT USED by far
	
	private boolean isInited = false;
	
	/**
	 * use this method to initialize configuration. <i>readConf()</i> will be called.
	 * @throws GeneralException
	 */
	public void init() throws GeneralException {
		readDefaultConf(conf);
		readConf(conf);
		isInited = true;
		logOutConfiguration();
	}
	
	private void logOutConfiguration() {
		Iterator<Entry<String, String>> iter = conf.entrySet().iterator();
		StringBuilder sb = new StringBuilder("configurations: \n======================================\n");
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			sb.append("\t").append(entry.getKey()).append("\t: ").append(entry.getValue()).append("\n");
		}
		sb.append("======================================");
		
		logger.info(sb.toString());
	}
	
	/**
	 * Read configuration to <i>conf</i>.<br>
	 * Class that extends should implement this method.
	 * @param conf
	 * @throws GeneralException
	 */
	protected void readConf(Map<String, String> conf) throws GeneralException {
		// waiting for son class to implement this
		logger.warn("override this method in extended class");
	}
	
	public Map<String, String> getConf() throws GeneralException {
		checkConfInited();
		
		return new HashMap<String, String>(conf);
	}
	
	public String getConf(String key) throws GeneralException {
		checkConfInited();
		
		return conf.get(key);
	}
	
	private void readDefaultConf(Map<String, String> conf) throws GeneralException {
		logger.info("reading default configuraions...");
		Properties properties = new Properties();
		
		try (
				InputStream inputStream = Configuration.class.getResourceAsStream("/configuration.properties");
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				) {
			properties.load(reader);
			
			for (Entry<Object, Object> entry : properties.entrySet()) {
				conf.put((String) entry.getKey(), "" + entry.getValue());
			}
			
			logger.info("reading default conifigurations DONE.");
		} catch (IOException e) {
			logger.error("fail to read default configuration");
			throw(new GeneralException("fail to read default configuration", e));
		}
	}
	
	private boolean checkConfInited() throws GeneralException {
		if (!isInited()) {
			logger.error("configuration not initialized");
			throw new GeneralException("configuration not initialized");
		}
		
		return true;
	}
	
	private boolean isInited() {
		return isInited;
	}
}
