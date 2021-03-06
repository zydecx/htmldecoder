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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;

/**
 * Read configuration from properties file.
 * @author zydecx
 *
 */
public class FileConfiguration extends Configuration {

	private static final Logger logger = CommonLog.getLogger();
	
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
	protected void readConf(Map<String, String> conf) throws GeneralException {
		logger.info("reading configuraions from file...");
		
		if (confFilePath == null) {
			logger.warn("conf file path is undefined");
			return;
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
			logger.info("reading configuraions from file DONE.");
		} catch (IOException e) {
			throw new GeneralException("fail to read configuration from file[" + this.confFilePath + "]", e);
		}
	}

}
