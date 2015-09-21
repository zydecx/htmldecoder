package com.debugtoday.htmldecoder.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.debugtoday.htmldecoder.exception.GeneralException;

public class FileConfiguration extends Configuration {
	
	private String confFilePath;
	
	/**
	 * MUST call setFilePath() before call init()
	 */
	public FileConfiguration() {
		this(null);
	}
	
	public FileConfiguration(String filePath) {
		this.confFilePath = filePath;
	}
	
	public void setFilePath(String filePath) {
		this.confFilePath = filePath;
	}
	
	@Override
	protected Map<String, String> readConf() throws GeneralException {
		Map<String, String> conf = new HashMap<>();
		
		if (confFilePath == null) {
			throw new NullPointerException("conf file path is undefined");
		}
		
		Properties properties = new Properties();
		
		try (
				InputStream inputStream = new FileInputStream(confFilePath);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				) {
			properties.load(reader);
			
			for (Entry<Object, Object> entry : properties.entrySet()) {
				conf.put((String) entry.getKey(), "" + entry.getValue());
			}
		} catch (IOException e) {
			throw(new GeneralException("fail to read configuration from file[" + this.confFilePath + "]", e));
		}
		
		return conf;
	}

}
