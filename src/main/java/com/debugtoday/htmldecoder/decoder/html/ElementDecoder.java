package com.debugtoday.htmldecoder.decoder.html;

public class ElementDecoder {

	public static int matchElementStart(String s, String element) {
		return matchElementStart(s, element, 0);
	}

	/**
	 * find start position of element 
	 * @param s
	 * @param element
	 * @param fromIndex
	 * @return
	 */
	public static int matchElementStart(String s, String element, int fromIndex) {
		if (s == null) {
			return -1;
		}
		
		int matchIndex = s.indexOf("<" + element, fromIndex);
		return matchIndex;
	}

	public static int matchElementEnd(String s, String element) {
		return matchElementEnd(s, element, 0);
	}

	/**
	 * find end position of element 
	 * @param s
	 * @param element
	 * @param fromIndex
	 * @return
	 */
	public static int matchElementEnd(String s, String element, int fromIndex) {
		if (s == null) {
			return -1;
		}
		
		String formattedElementEnd = formatElementEnd(element);
		
		int matchIndex = s.indexOf(formattedElementEnd, fromIndex);
		
		return matchIndex < 0 ? -1 : (matchIndex + formattedElementEnd.length() - 1);
	}

	public static int matchElementContentStart(String s, String element) {
		return matchElementContentStart(s, element, 0);
	}

	/**
	 * find start position of content in given element.<br>
	 * Problem maybe occurs when element tag closes at the last position of s, which
	 * resulting in index out of boundary of s.
	 * @param s
	 * @param element
	 * @param fromIndex
	 * @return
	 */
	public static int matchElementContentStart(String s, String element, int fromIndex) {
		if (s == null) {
			return -1;
		}
		
		int matchIndex = s.indexOf(">", fromIndex);
		
		return matchIndex < 0 ? -1 : ++matchIndex;
	}

	public static int matchElementContentEnd(String s, String element) {
		return matchElementContentEnd(s, element, 0);
	}

	/**
	 * find end position of content in given element.<br>
	 * Problem maybe occurs when element tag closes at the first position of s, which
	 * resulting in index out of boundary of s.
	 * @param s
	 * @param element
	 * @param fromIndex
	 * @return
	 */
	public static int matchElementContentEnd(String s, String element, int fromIndex) {
		if (s == null) {
			return -1;
		}
		
		int matchIndex = s.indexOf(formatElementEnd(element), fromIndex);
		
		return matchIndex < 0 ? -1 : --matchIndex;
	}
	
	private static String formatElementEnd(String element) {
		return "</" + element + ">";
	}
}
