package com.debugtoday.htmldecoder.decoder.md;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;

public class MarkdownDecoderArg {
	private ConfigurationWrapper conf;
	
	public MarkdownDecoderArg(ConfigurationWrapper conf) {
		this.setConf(conf);
	}

	public ConfigurationWrapper getConf() {
		return conf;
	}

	public void setConf(ConfigurationWrapper conf) {
		this.conf = conf;
	}
}
