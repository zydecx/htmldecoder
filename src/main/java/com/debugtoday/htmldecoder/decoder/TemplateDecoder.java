package com.debugtoday.htmldecoder.decoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateArgument;
import com.debugtoday.htmldecoder.struct.TemplatePlaceHolder;
import com.debugtoday.htmldecoder.struct.html.Element;

public class TemplateDecoder extends GeneralDecoder {
	
	public static Template decodeDefault(String templateName, String resourceName, ConfigurationWrapper conf) throws GeneralException {
		File file = new File(ThemeDecoder.class.getResource(resourceName).getFile());	// CANNOT be read
		try (
				InputStream inputStream = ThemeDecoder.class.getResourceAsStream(resourceName);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		) {
			String inLine;
			Template template = new Template(templateName, file);
			StringBuilder fullText = new StringBuilder();
			while ((inLine = reader.readLine()) != null) {
				fullText.append(inLine).append("\n");
			}
			
			template.setFullText(replaceGeneralArguments(fullText.toString(), conf.getConfiguration()));
			template.setPreList(decodePreElement(template));
			
			// Seems useless to decode placeholder and arguments.
			/*decodeTemplateFullText(template);*/
			
			int offsetPos = 0;
			Element head = decodeGeneralElement(template, "head", offsetPos);
			offsetPos = head == null ? 0 : (head.getFileStartPos() + head.getEndPosOffset());
			Element body = decodeGeneralElement(template, "body", offsetPos);
			
			template.setHead(head);
			template.setBody(body);
			
			return template;
		} catch (IOException e) {
			throw new GeneralException("fail to decode template[" + resourceName + "]", e);
		}
	}
	
	public static Template decodeCustomerized(String templateName, File file, ConfigurationWrapper conf) throws GeneralException {
		try (
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		) {
			String inLine;
			Template template = new Template(templateName, file);
			StringBuilder fullText = new StringBuilder();
			while ((inLine = reader.readLine()) != null) {
				fullText.append(inLine).append("\n");
			}
			
			template.setFullText(replaceGeneralArguments(fullText.toString(), conf.getConfiguration()));
			template.setPreList(decodePreElement(template));
			
			// Seems useless to decode placeholder and arguments.
			/*decodeTemplateFullText(template);*/
			
			int offsetPos = 0;
			Element head = decodeGeneralElement(template, "head", offsetPos);
			offsetPos = head == null ? 0 : (head.getFileStartPos() + head.getEndPosOffset());
			Element body = decodeGeneralElement(template, "body", offsetPos);
			
			template.setHead(head);
			template.setBody(body);
			
			return template;
		} catch (IOException e) {
			throw new GeneralException("fail to decode template[" + file.getAbsolutePath() + "]", e);
		}
	}
	
	private static void decodeTemplateFullText(Template template) {
		if (template == null || template.getFullText() == null) {
			return;
		}
		
		// match place holders
		Pattern p = Pattern.compile("<!--htmldecoder:\\w+-->");
		Matcher m = p.matcher(template.getFullText());
		
		while (m.find()) {
			TemplatePlaceHolder placeHolder = new TemplatePlaceHolder();
			placeHolder.setOffsetStart(m.start());
			placeHolder.setOffsetEnd(m.end());
			
			String s = m.group();
			String name = s.substring(17, s.length() - 3);	// match part of \\w+
			placeHolder.setName(name);
			
			List<TemplatePlaceHolder> placeHolders = template.getPlaceHolders().get(name);
			if (placeHolders == null) {
				placeHolders = new ArrayList<>();
				template.getPlaceHolders().put(name, placeHolders);
			}
			placeHolders.add(placeHolder);
		}
		
		// match arguments
		p = Pattern.compile("\\{\\{\\w+\\}\\}");
		m = p.matcher(template.getFullText());
		
		while (m.find()) {
			TemplateArgument arg = new TemplateArgument();
			arg.setOffsetStart(m.start());
			arg.setOffsetEnd(m.end());
			
			String s = m.group();
			String name = s.substring(2, s.length() - 2);	// match part of \\w+
			arg.setName(name);
			
			List<TemplateArgument> args = template.getArguments().get(name);
			if (args == null) {
				args = new ArrayList<>();
				template.getArguments().put(name, args);
			}
			args.add(arg);
		}
	}
	
	public static String formatArgumentRegex(String name) {
		return "{{" + name + "}}";
	}
	
	public static String formatPlaceholderRegex(String name) {
		return "<!--htmldecoder:" + name + "-->";
	}

}
