package com.debugtoday.htmldecoder.conf;

import java.util.HashMap;
import java.util.Map;

import com.debugtoday.htmldecoder.exception.GeneralException;

public class DynamicConfiguration extends Configuration {
	
	private Map<String, String> conf;
	
	/**
	 * MUST call setConf() before call init()
	 */
	public DynamicConfiguration() {
		this(new HashMap<String, String>());
	}
	
	public DynamicConfiguration(Map<String, String> conf) {
		this.conf = new HashMap<String, String>(conf);
	}
	
	/**
	 * DON'T work util call init()
	 * @param key
	 * @param value
	 */
	public void setConf(String key, String value) {
		this.conf.put(key, value);
	}
	
	/**
	 * DON'T work util call init()
	 * @param conf
	 */
	public void setConf(Map<String, String> conf) throws GeneralException {
		this.conf = new HashMap<String, String>(conf);
	}
	
	@Override
	protected Map<String, String> readConf() {
		return this.conf;
	}
}
 