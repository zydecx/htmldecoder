package com.debugtoday.htmldecoder.decoder.md;

import java.util.HashMap;
import java.util.Map;

public class MarkdownDecoderFactory {
	public static MarkdownDecoder getInstance(String interpreterName) {
		return DecoderHolder.cache.get(interpreterName);
	}
	
	private static class DecoderHolder {
		static final Map<String, MarkdownDecoder> cache = getCache();
		
		private static Map<String, MarkdownDecoder> getCache() {
			Map<String, MarkdownDecoder> cache = new HashMap<>();
			cache.put("pandoc", new PandocDecoder());
			
			return cache;
		}
	}
}
