package com.debugtoday.htmldecoder.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonLog {
	private static final Logger logger = LoggerFactory.getLogger(CommonLog.class);
	
	public static Logger getLogger() {
		return logger;
	}
	
}
