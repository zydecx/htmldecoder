package com.debugtoday.htmldecoder.decoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.decoder.html.ElementDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Document;
import com.debugtoday.htmldecoder.struct.html.Element;

public abstract class GeneralDecoder {
	/**
	 * return existing format of configuration in file
	 * @param conf
	 * @return
	 */
	public static String formatArgumentRegex(String conf) {
		return "{{" + conf + "}}";
	}
	
	/**
	 * replace general configuration in fulltext, i.g. siteurl
	 * @param fullText
	 * @param conf
	 * @return
	 * @throws GeneralException
	 */
	protected static String replaceGeneralArguments(String fullText, Configuration conf) throws GeneralException {
		return fullText == null ? null : replaceGeneralArgument(fullText, conf, Configuration.SITE_URL);
	}
	
	protected static String replaceGeneralArgument(String fullText, Configuration conf, String argument) throws GeneralException {
		return fullText.replace(formatArgumentRegex(argument), conf.getConf(argument));
	}
	
	/**
	 * decode placeholder wrapper in template
	 * @param document
	 * @param placeholder
	 * @return
	 */
	protected static Element decodeGeneralPlaceholder(Document document, String placeholder, int fromIndex) {
		String fullText = document.getFullText();
		
		String formattedPlaceholder = formatPlaceholderRegex(placeholder);
		int index = fromIndex - 1;
		do {
			index = fullText.indexOf(formattedPlaceholder, ++index);
		} while (index != -1 && isInPreElement(document, index));
		if (index == -1) {
			return null;
		}
		
		Element element = new Element();
		element.setDocument(document);
		/*element.setTag(null);
		element.setAttributes(new HashMap<String, String>());*/
		element.setFullText(formattedPlaceholder);
		element.setFileStartPos(index);
		element.setEndPosOffset(formattedPlaceholder.length() - 1);
		element.setContentStartPosOffset(0);
		element.setContentEndPosOffset(formattedPlaceholder.length() - 1);
		
		return element;
	}
	protected static Element decodeGeneralPlacehoder(Document document, String placeholder) {
		return decodeGeneralPlaceholder(document, placeholder, 0);
	}
	

	
	protected static Element decodeGeneralElement(Document document, String element) {
		return decodeGeneralElement(document, element, 0);
	}
	
	protected static Element decodeGeneralElement(Document document, String element, int fromIndex) {
		String fullText = document.getFullText();
		
		int startIndex = fromIndex - 1;
		do {
			startIndex = ElementDecoder.matchElementStart(fullText, element, ++startIndex);
		} while (startIndex != -1 && !element.equalsIgnoreCase("pre") && isInPreElement(document, startIndex));		
		if (startIndex == -1) {
			return null;
		}
		
		int endIndex = startIndex;
		do {
			endIndex = ElementDecoder.matchElementEnd(fullText, element, ++endIndex);
		} while (endIndex != -1 && !element.equalsIgnoreCase("pre") && isInPreElement(document, endIndex));
		if (endIndex == -1) {
			return null;
		}
		
		int contentStartIndex = ElementDecoder.matchElementContentStart(fullText, element, startIndex);
		int contentEndIndex = ElementDecoder.matchElementContentEnd(fullText, element, startIndex);
		// i.g. <p></p>, under this circumstance, there's no actual content.
		if (contentStartIndex > contentEndIndex) {
			contentStartIndex = contentEndIndex = -1;
		}
		
		
		Element elementBean = new Element();
		elementBean.setDocument(document);
		elementBean.setTag(element);
		elementBean.setAttributes(new HashMap<String, String>());
		elementBean.setFullText(fullText.substring(startIndex, endIndex + 1));
		elementBean.setFileStartPos(startIndex);
		elementBean.setEndPosOffset(endIndex - startIndex);
		elementBean.setContentStartPosOffset(contentStartIndex - startIndex);
		elementBean.setContentEndPosOffset(contentEndIndex - startIndex);
		
		return elementBean;
	}
	
	/**
	 * decode all <pre> element in document
	 * @param document
	 * @return
	 */
	protected static List<Element> decodePreElement(Document document) {
		List<Element> preList = new ArrayList<>();
		
		int index = 0;
		Element pre = decodeGeneralElement(document, "pre", index);
		while (pre != null) {
			preList.add(pre);
			index = pre.getFileStartPos() + pre.getEndPosOffset();
			pre = decodeGeneralElement(document, "pre", index);
		}
		
		return preList;
	}
	
	private static boolean isInPreElement(Document document, int index) {
		List<Element> preList = document.getPreList();
		
		for (Element e : preList) {
			if (index >= (e.getFileStartPos() + e.getContentStartPosOffset())
					&& index <= (e.getFileStartPos() + e.getContentEndPosOffset())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * format placeholder as it's form in template file
	 * @param placeholder
	 * @return
	 */
	public static String formatPlaceholderRegex(String name) {
		return "<!--htmldecoder:" + name + "-->";
	}
}
