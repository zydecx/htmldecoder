package com.debugtoday.htmldecoder.decoder.html;

import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.html.Meta;

public class MetaDecoder {
	
	public static final String META_ABSTRACT = "htmldecoder:abstract";
	public static final String META_KEYWORD = "htmldecoder:keyword";
	public static final String META_CATEGORY = "htmldecoder:category";
	
	private static final String META_DELIMITER = ",";	// 内容关键字分隔符
	
	public static String[] decodeMeta(Meta meta) throws GeneralException {
		switch (meta.getName()) {
		case META_ABSTRACT:
			return new String[]{decodeAbstract(meta)};
		case META_CATEGORY:
			return decodeCategory(meta);
		case META_KEYWORD:
			return decodeKeyword(meta);
		}
		
		throw new GeneralException("unrecognized meta");
	}
	
	public static String decodeAbstract(Meta meta) {
		return meta.getContent();
	}
	
	public static String decodeAbstract(Meta meta, boolean forceNullIfNotMatched) {
		return forceNullIfNotMatched && !meta.getName().equalsIgnoreCase(META_ABSTRACT) ? null : decodeAbstract(meta);
	}
	
	public static String[] decodeKeyword(Meta meta) {
		return meta.getContent().split(META_DELIMITER);
	}
	
	public static String[] decodeKeyword(Meta meta, boolean forceNullIfNotMatched) {
		return forceNullIfNotMatched && !meta.getName().equalsIgnoreCase(META_KEYWORD) ? null : decodeKeyword(meta);
	}
	
	public static String[] decodeCategory(Meta meta) {
		return meta.getContent().split(META_DELIMITER);
	}
	
	public static String[] decodeCategory(Meta meta, boolean forceNullIfNotMatched) {
		return forceNullIfNotMatched && !meta.getName().equalsIgnoreCase(META_CATEGORY) ? null : decodeCategory(meta);
	}
	
	public static Meta matchMeta(String s) {
		if (s == null) {
			return null;
		}
		
		int metaIndex = s.indexOf("<meta");
		if (metaIndex < 0) {
			return null;
		}
		
		int metaEndIndex = s.indexOf(">", metaIndex);
		if (metaEndIndex < 0) {
			return null;
		}
		
		s = s.substring(metaIndex, metaEndIndex + 1);
		
		Meta meta = new Meta();
		meta.setName(readMeta(s, "name"));
		meta.setContent(readMeta(s, "content"));
		
		return meta;
	}
	
	private static String readMeta(String metaStr, String key) {
		int index = metaStr.indexOf(key + "=");
		if (index < 0) {
			return null;
		}
		
		int firstQuote = metaStr.indexOf("\"", index);
		if (firstQuote < 0) {
			return null;
		}
		
		int secondQuote = metaStr.indexOf("\"", firstQuote + 1);
		if (secondQuote < 0) {
			return null;
		}
		
		return metaStr.substring(firstQuote + 1, secondQuote);
	}

}
