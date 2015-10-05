package com.debugtoday.htmldecoder.decoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;
import com.debugtoday.htmldecoder.util.FileUtil;

public class ThemeDecoder extends GeneralDecoder {
	
	/**
	 * decode theme file
	 * @param file
	 * @return
	 * @throws GeneralException
	 */
	public static Theme decode(ConfigurationWrapper conf) throws GeneralException {
		String currentTheme = conf.getConfiguration().getConf(Configuration.CURRENT_THEME);
		boolean isDefault = true/* = "default".equalsIgnoreCase(currentTheme)*/;
		
		Theme theme = new Theme(currentTheme);
		Map<TemplateKey, Template> templates = theme.getTemplates();
		
		// find workspace first; if not, find if package contains the theme
		File themeFile = new File(conf.getThemeFile().getAbsolutePath() + File.separator + currentTheme);
		if (themeFile.isDirectory()) {
			isDefault = false;
		} else {
			themeFile = new File(ThemeDecoder.class.getResource("/theme/" + currentTheme).getFile());
		}
		/*
		if (isDefault) {
			themeFile = new File(ThemeDecoder.class.getResource("/theme/default").getFile());
		} else {
			themeFile = new File(conf.getThemeFile().getAbsolutePath() + File.separator + currentTheme);
		}*/
		
		// if packaged in jar, theme CANNOT be read like an usual file.
		// decodeTemplateFromJarFile() is specially designed to process such situation.
		if (themeFile.isDirectory()) {
			decodeTemplateFromFileSystem(conf, currentTheme, themeFile, templates);
		} else {
			decodeTemplateFromJarFile(conf, currentTheme, themeFile, templates);
		}
		
		return theme;
	}
	
	/**
	 * if there's default theme with given name in package
	 * @param themeName
	 * @return
	 */
	public static boolean isDefaultThemeExisted(String themeName) {
		boolean isThemeExisted = false;
		try (
				InputStream inputStream = ThemeDecoder.class.getResourceAsStream("/theme/" + themeName + "/META");
		) {
			inputStream.read();
			isThemeExisted = true;
		} catch (Exception e) {}
		
		return isThemeExisted;
	}
	
	private static void decodeTemplateFromFileSystem(ConfigurationWrapper conf, String themeName, File themeFile, Map<TemplateKey, Template> templates) throws GeneralException {
		for (File file : themeFile.listFiles()) {
			TemplateKey templateKey = decodeTemplateKey(file.getName());
			if (templateKey == null) {
				File toFile = new File(conf.getOutputFile().getAbsolutePath() + File.separator + file.getName());
				if (file.isDirectory()) {
					FileUtil.copyDirectory(file, toFile);
				} else {
					FileUtil.copy(file, toFile);
				}
			} else {
				Template template = TemplateDecoder.decodeCustomerized(templateKey.getKey(), file, conf);
				templates.put(templateKey, template);
			}
		}
	}
	
	/**
	 * if packaged in jar, theme CANNOT be read like an usual file.<br>
	 * in this method, entries of jar file will be traversed to parse resources under default theme path.<br>
	 * and template resources will be decoded and others copied to output folder.
	 * @param conf
	 * @param themeFile
	 * @param themeName
	 * @param templates
	 * @throws GeneralException
	 */
	private static void decodeTemplateFromJarFile(ConfigurationWrapper conf, String themeName, File themeFile, Map<TemplateKey, Template> templates) throws GeneralException {
		String jarPath = themeFile.getPath().replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
		
		// traverse jar file to extract entries under default theme path
		String resourcePath = "theme/" + themeName + "/";
		List<String> themeEntry = new ArrayList<>();
		try (
				JarFile jarFile = new JarFile(jarPath);
				){
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (entryName.startsWith(resourcePath)) {
					themeEntry.add(entryName);
				}
			}
		} catch (IOException e) {
			throw new GeneralException("fail to read file[" + themeFile.getAbsolutePath() + "]");
		}
		
		// process entries under default theme path
		int length = resourcePath.length();
		for (String s : themeEntry) {
			// entry like theme/default/, theme/default/javascripts will be ignored
			if (s.equals("") || s.endsWith("/")) {
				continue;
			} 
			
			String entryName = s.substring(length);
			TemplateKey templateKey = decodeTemplateKey(entryName);
			if (templateKey != null) {// decode template file
				String keyName = templateKey.getKey();
				Template template = TemplateDecoder.decodeDefault(keyName, "/" + s, conf);
				templates.put(templateKey, template);
			} else if (!entryName.equalsIgnoreCase("META")) { // copy non-template resources
//				System.out.println(entryName);
				// DONOT use replaceAll(). StringIndexOutOfBoundsException when applied for "javascripts/jquery-2.1.4.min.js"
				File file = new File(conf.getOutputFile().getAbsoluteFile() + File.separator + entryName.replace("/", File.separator));
				File parentFile = file.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						throw new GeneralException("fail to create file[" + file.getAbsolutePath() + "]", e);
					}
				}
				
				try (
						InputStream is = ThemeDecoder.class.getResourceAsStream("/" + s);
						FileOutputStream fos = new FileOutputStream(file);
						) {
					byte[] buf = new byte[1024];
					int len;
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
					}
				} catch (IOException e) {
					throw new GeneralException("fail to read resouce[" + s + "]", e);
				}
			}
		}
	}

	private static TemplateKey decodeTemplateKey(String name) {
		if (!name.endsWith(".htm") && !name.endsWith(".html")) {
			return null;
		}
		
		name = name.substring(0, name.indexOf("."));
		
		return TemplateKey.parseKey(name);
	}

}
