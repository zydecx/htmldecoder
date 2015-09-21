package com.debugtoday.htmldecoder.decoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Template;

public class TemplateDecoder {
	
	public static Template decode(File file) throws GeneralException {
		try (
				BufferedReader reader = new BufferedReader(new FileReader(file));
				) {
			String inLine;
			Template template = new Template(file);
			while ((inLine = reader.readLine()) != null) {
				//
			}
			
			return template;
		} catch (IOException e) {
			throw new GeneralException("fail to read file", e);
		}
	}

}
