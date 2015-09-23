package com.debugtoday.htmldecoder.conf;

import java.util.HashMap;
import java.util.Map;

import com.debugtoday.htmldecoder.exception.GeneralException;

public abstract class Configuration {
	public static final String TEMPLATE_FILE = "template-file";
	public static final String RESOURCE_FOLDER = "resource-folder";
	public static final String DOCUMENT_FOLDER = "document-folder";
	public static final String DESTINATION_FOLDER = "destination-folder";
	public static final String SCRIPT_PAGE_FILE = "script-page-file";
	public static final String SCRIPT_CATEGORY_FILE = "script-category-file";
	public static final String SCRIPT_RECENT_FILE = "script-recent-file";
	
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
