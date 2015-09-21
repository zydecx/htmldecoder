package com.debugtoday.htmldecoder.decoder.html;

public class ElementDecoder {

	public static boolean matchElementStart(String s, String element) {
		if (s == null) {
			return false;
		}
		
		int metaIndex = s.indexOf("<" + element);
		return metaIndex >= 0;
	}

	public static boolean matchElementEnd(String s, String element) {
		if (s == null) {
			return false;
		}
		
		int metaIndex = s.indexOf("</" + element + ">");
		return metaIndex >= 0;
	}
}
