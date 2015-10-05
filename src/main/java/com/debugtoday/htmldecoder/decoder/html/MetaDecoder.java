package com.debugtoday.htmldecoder.decoder.html;

import java.util.ArrayList;
import java.util.List;

import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.html.Meta;

public class MetaDecoder {

	public static final String META_DATE = "htmldecoder:date";
	public static final String META_MODIFIED = "htmldecoder:modified";
	public static final String META_AUTHOR = "htmldecoder:author";
	public static final String META_AUTHORURL = "htmldecoder:authorurl";
	public static final String META_TAGS = "htmldecoder:tags";
	public static final String META_CATEGORY = "htmldecoder:category";
	public static final String META_ABSTRACT = "htmldecoder:abstract";
	public static final String META_ENABLED = "htmldecoder:enabled";
	
	private static final String META_DELIMITER = ",";	// 内容关键字分隔符
	
	public static String[] decodeMeta(Meta meta) throws GeneralException {
		switch (meta.getName()) {
		case META_DATE:
			return new String[]{decodeDate(meta)};
		case META_MODIFIED:
			return new String[]{decodeModified(meta)};
		case META_AUTHOR:
			return new String[]{decodeAuthor(meta)};
		case META_AUTHORURL:
			return new String[]{decodeAuthorUrl(meta)};
		case META_TAGS:
			return decodeTags(meta);
		case META_CATEGORY:
			return decodeCategory(meta);
		case META_ABSTRACT:
			return new String[]{decodeAbstract(meta)};
		case META_ENABLED:
			return new String[] {Boolean.toString(decodeEnabled(meta))};
		}
		
		throw new GeneralException("unrecognized meta");
	}
	
	private static String decodeString(Meta meta) {
		return meta.getContent();
	}
	
	private static String decodeString(Meta meta, String name, boolean forceNullIfNotMatched) {
		return forceNullIfNotMatched && !meta.getName().equalsIgnoreCase(name) ? null : decodeString(meta);
	}
	
	private static String[] decodeStringArray(Meta meta) {
		List<String> list = new ArrayList<>();
		for (String s : meta.getContent().split(META_DELIMITER)) {
			if (!"".equals(s.trim())) {
				list.add(s.trim());
			}
		}
		return list.toArray(new String[]{});
	}
	
	private static String[] decodeStringArray(Meta meta, String name, boolean forceNullIfNotMatched) {
		return forceNullIfNotMatched && !meta.getName().equalsIgnoreCase(name) ? null : decodeStringArray(meta);
	}
	
	public static String decodeDate(Meta meta) {
		return decodeString(meta);
	}
	
	public static String decodeDate(Meta meta, boolean forceNullIfNotMatched) {
		return decodeString(meta, META_DATE, forceNullIfNotMatched);
	}
	
	public static String decodeModified(Meta meta) {
		return decodeString(meta);
	}
	
	public static String decodeModified(Meta meta, boolean forceNullIfNotMatched) {
		return decodeString(meta, META_MODIFIED, forceNullIfNotMatched);
	}
	
	public static String decodeAuthor(Meta meta) {
		return decodeString(meta);
	}
	
	public static String decodeAuthor(Meta meta, boolean forceNullIfNotMatched) {
		return decodeString(meta, META_AUTHOR, forceNullIfNotMatched);
	}
	
	public static String decodeAuthorUrl(Meta meta) {
		return decodeString(meta);
	}
	
	public static String decodeAuthorUrl(Meta meta, boolean forceNullIfNotMatched) {
		return decodeString(meta, META_AUTHORURL, forceNullIfNotMatched);
	}
	
	public static String[] decodeTags(Meta meta) {
		return decodeStringArray(meta);
	}
	
	public static String[] decodeTags(Meta meta, boolean forceNullIfNotMatched) {
		return decodeStringArray(meta, META_TAGS, forceNullIfNotMatched);
	}
	
	public static String[] decodeCategory(Meta meta) {
		return decodeStringArray(meta);
	}
	
	public static String[] decodeCategory(Meta meta, boolean forceNullIfNotMatched) {
		return decodeStringArray(meta, META_TAGS, forceNullIfNotMatched);
	}
	
	public static String decodeAbstract(Meta meta) {
		return decodeString(meta);
	}
	
	public static String decodeAbstract(Meta meta, boolean forceNullIfNotMatched) {
		return decodeString(meta, META_ABSTRACT, forceNullIfNotMatched);
	}
	
	public static Boolean decodeEnabled(Meta meta) {
		return Boolean.parseBoolean(meta.getContent());
	}
	
	public static Boolean decodeEnabled(Meta meta, boolean forceNullIfNotMatched) {
		return forceNullIfNotMatched && !meta.getName().equalsIgnoreCase(META_ENABLED) ? null : decodeEnabled(meta);
	}
	
	public static Meta matchMeta(String s) {
		return matchMeta(s, 0);
	}
	
	public static Meta matchMeta(String s, int fromIndex) {
		if (s == null) {
			return null;
		}
		
		int metaIndex = s.indexOf("<meta", fromIndex);
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
		
		if (meta.getName() == null || meta.getContent() == null) {
			return null;
		}
		
		meta.setFullText(s);
		meta.setStartPos(metaIndex);
		
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
	
	public static String formatMetaName(String name) {
		return "htmldecoder:" + name;
	}

}
