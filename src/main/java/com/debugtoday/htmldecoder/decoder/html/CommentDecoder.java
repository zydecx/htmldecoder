package com.debugtoday.htmldecoder.decoder.html;

public class CommentDecoder {

	public static boolean matchCommentStart(String s) {
		if (s == null) {
			return false;
		}
		
		int metaIndex = s.indexOf("<!--");
		return metaIndex >= 0;
	}

	public static boolean matchElementEnd(String s) {
		if (s == null) {
			return false;
		}
		
		int metaIndex = s.indexOf("-->");
		return metaIndex >= 0;
	}
}
