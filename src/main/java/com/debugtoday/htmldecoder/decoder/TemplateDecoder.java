package com.debugtoday.htmldecoder.decoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.html.Element;

public class TemplateDecoder {
	
	/**
	 * decode template file
	 * @param file
	 * @return
	 * @throws GeneralException
	 */
	public static Template decode(File file) throws GeneralException {
		try (
				BufferedReader reader = new BufferedReader(new FileReader(file));
				) {
			String inLine;
			Template template = new Template(file);
			StringBuilder fullText = new StringBuilder();
			while ((inLine = reader.readLine()) != null) {
				fullText.append(inLine).append("\n");
			}
			
			template.setFullText(fullText.toString());
			template.setHeadContainer(decodeTemplateContainer(template, CONTAINER_HEAD));
			template.setBodyContainer(decodeTemplateContainer(template, CONTAINER_BODY));
			
			return template;
		} catch (IOException e) {
			throw new GeneralException("fail to read file", e);
		}
	}
	
	/**
	 * decode container wrapper in template
	 * @param template
	 * @param container
	 * @return
	 */
	private static Element decodeTemplateContainer(Template template, String container) {
		String fullText = template.getFullText();
		
		String formattedContainer = formatContainer(container);
		int index = fullText.indexOf(formattedContainer);
		
		if (index < 0) {
			return null;
		}
		
		Element element = new Element();
		element.setDocument(template);
		/*element.setTag(null);
		element.setAttributes(new HashMap<String, String>());*/
		element.setFullText(formattedContainer);
		element.setFileStartPos(index);
		element.setEndPosOffset(formattedContainer.length() - 1);
		element.setContentStartPosOffset(0);
		element.setContentEndPosOffset(formattedContainer.length() - 1);
		
		return element;
	}
	
	/**
	 * format container as it's form in template file
	 * @param container
	 * @return
	 */
	private static String formatContainer(String container) {
		return "<!--" + container + "-->";
	}
	
	private static final String CONTAINER_HEAD = "htmldecoder:head";
	private static final String CONTAINER_BODY = "htmldecoder:body";

}
