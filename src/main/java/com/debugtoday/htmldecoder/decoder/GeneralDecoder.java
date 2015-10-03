package com.debugtoday.htmldecoder.decoder;

import java.util.HashMap;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.decoder.html.ElementDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Document;
import com.debugtoday.htmldecoder.struct.html.Element;

public abstract class GeneralDecoder {
	/**
	 * return existing format of configuration in file
	 * @param conf
	 * @return
	 */
	public static String formatArgumentRegex(String conf) {
		return "\\{\\{" + conf + "\\}\\}";
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
		return fullText.replaceAll(formatArgumentRegex(argument), conf.getConf(argument));
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
		int index = fullText.indexOf(formattedPlaceholder, fromIndex);
		
		if (index < 0) {
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
		
		int startIndex = ElementDecoder.matchElementStart(fullText, element, fromIndex);
		if (startIndex < 0) {
			return null;
		}
		
		int endIndex = ElementDecoder.matchElementEnd(fullText, element, startIndex);
		if (endIndex < 0) {
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
	 * format placeholder as it's form in template file
	 * @param placeholder
	 * @return
	 */
	public static String formatPlaceholderRegex(String name) {
		return "<!--htmldecoder:" + name + "-->";
	}
}
