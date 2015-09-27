package com.debugtoday.htmldecoder.decoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.html.Element;

public class TemplateDecoder extends GeneralDecoder {
	
	/**
	 * decode template file
	 * @param file
	 * @return
	 * @throws GeneralException
	 */
	public static Template decode(File file, Configuration conf) throws GeneralException {
		try (
				BufferedReader reader = new BufferedReader(new FileReader(file));
				) {
			String inLine;
			Template template = new Template(file);
			StringBuilder fullText = new StringBuilder();
			while ((inLine = reader.readLine()) != null) {
				fullText.append(inLine).append("\n");
			}
			
			template.setFullText(replaceGeneralArguments(fullText.toString(), conf));
			template.setHeadContainer(decodeTemplateContainer(template, CONTAINER_HEAD));
			template.setBodyContainer(decodeTemplateContainer(template, CONTAINER_BODY));
			template.setNavContainer(decodeTemplateContainer(template, CONTAINER_NAV));
			
			return template;
		} catch (IOException e) {
			throw new GeneralException("fail to read file", e);
		}
	}
	
	private static Element decodeTemplateContainer(Template template, String container) {
		return decodeGeneralContainer(template, container);
	}
	
	private static final String CONTAINER_HEAD = "htmldecoder:head";
	private static final String CONTAINER_BODY = "htmldecoder:body";
	private static final String CONTAINER_NAV = "htmldecoder:nav";

}
