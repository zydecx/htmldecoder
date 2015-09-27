package com.debugtoday.htmldecoder.conf;

import java.util.HashMap;
import java.util.Map;

import com.debugtoday.htmldecoder.exception.GeneralException;

public abstract class Configuration {
	public static final String SITE_URL = "siteurl";
	public static final String PAGINATION_SIZE = "paginationsize";
	
	public static final String TEMPLATE_PATH = "template";
	public static final String CONTENT_PATH = "content";
	public static final String OUTPUT_PATH = "output";
	public static final String STATIC_PAGE_PATH = "staticpage";
	public static final String IGNORE_PATH_LIST = "ignorelist";
	
	private Map<String, String> conf = new HashMap<>();
	
	private String confMode;	// NOT USED by far
	
	private boolean isInited = false;
	
	public Configuration() {
		//
	}
	
	/**
	 * use this method to initialize configuration
	 * @throws GeneralException
	 */
	public void init() throws GeneralException {
		conf = readConf();
		isInited = true;
	}
	
	protected Map<String, String> readConf() throws GeneralException {
		return new HashMap<String, String>();
	}
	
	public Map<String, String> getConf() throws GeneralException {
		checkConfInited();
		
		return new HashMap<String, String>(conf);
	}
	
	public String getConf(String key) throws GeneralException {
		checkConfInited();
		
		return conf.get(key);
	}
	
	private boolean checkConfInited() throws GeneralException {
		if (!isInited()) {
			throw new GeneralException("configuration not initialized");
		}
		
		return true;
	}
	
	private boolean isInited() {
		return isInited;
	}
}
