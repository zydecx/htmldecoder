package com.debugtoday.htmldecoder.decoder;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Document;
import com.debugtoday.htmldecoder.struct.html.Element;

public abstract class GeneralDecoder {
	/**
	 * 返回配置常量在文件中呈现的格式
	 * @param conf
	 * @return
	 */
	private static String formatConfArgument(String conf) {
		return "{{htmldecoder:" + conf + "}}";
	}
	
	/**
	 * 替换文件中的通用常量，如siteurl
	 * @param fullText
	 * @param conf
	 * @return
	 * @throws GeneralException
	 */
	protected static String replaceGeneralArguments(String fullText, Configuration conf) throws GeneralException {
		return fullText == null ? null : replaceGeneralArgument(fullText, conf, Configuration.SITE_URL);
	}
	
	protected static String replaceGeneralArgument(String fullText, Configuration conf, String argument) throws GeneralException {
		return fullText.replace(formatConfArgument(argument), conf.getConf(argument));
	}
	
	/**
	 * decode container wrapper in template
	 * @param document
	 * @param container
	 * @return
	 */
	protected static Element decodeGeneralContainer(Document document, String container, int fromIndex) {
		String fullText = document.getFullText();
		
		String formattedContainer = formatGeneralContainer(container);
		int index = fullText.indexOf(formattedContainer, fromIndex);
		
		if (index < 0) {
			return null;
		}
		
		Element element = new Element();
		element.setDocument(document);
		/*element.setTag(null);
		element.setAttributes(new HashMap<String, String>());*/
		element.setFullText(formattedContainer);
		element.setFileStartPos(index);
		element.setEndPosOffset(formattedContainer.length() - 1);
		element.setContentStartPosOffset(0);
		element.setContentEndPosOffset(formattedContainer.length() - 1);
		
		return element;
	}
	protected static Element decodeGeneralContainer(Document document, String container) {
		return decodeGeneralContainer(document, container, 0);
	}
	
	/**
	 * format container as it's form in template file
	 * @param container
	 * @return
	 */
	protected static String formatGeneralContainer(String container) {
		return "<!--" + container + "-->";
	}
}
