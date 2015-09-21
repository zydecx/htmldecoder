package com.debugtoday.htmldecoder;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.FileConfiguration;
import com.debugtoday.htmldecoder.exception.GeneralException;

public class HtmlDecoder {
	
	public static void main(String[] args) {
		System.out.println("Welcome to HtmlDecoder project!");
		String confFilePath = args[0];
		Configuration conf = new FileConfiguration(confFilePath);
		try {
			conf.init();
			new HtmlDecoder(conf).start();
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Configuration conf;
	
	public HtmlDecoder(Configuration conf) {
		this.conf = conf;
	}
	
	public void start() throws GeneralException {
		if (conf == null) {
			throw new GeneralException("configuration not initialized");
		}
		
		
	}

}
