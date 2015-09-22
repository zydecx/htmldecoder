package com.debugtoday.htmldecoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.FileConfiguration;
import com.debugtoday.htmldecoder.decoder.ArticleDecoder;
import com.debugtoday.htmldecoder.decoder.TemplateDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Template;

public class HtmlDecoder {
	
	public static void main(String[] args) {
		System.out.println("Welcome to HtmlDecoder project!");
		String confFilePath = args[0];
		Configuration conf = new FileConfiguration(confFilePath);
		try {
			conf.init();
			new HtmlDecoder(conf).start();
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Configuration conf;
	
	public HtmlDecoder(Configuration conf) {
		this.conf = conf;
	}
	
	public void start() throws GeneralException {
		if (conf == null) {
			throw new GeneralException("configuration not initialized");
		}
		
		File templateFile = new File(conf.getConf(Configuration.TEMPLATE_FILE));
		Template template = TemplateDecoder.decode(templateFile);
		
		File resourceFolder = new File(conf.getConf(Configuration.RESOURCE_FOLDER));
		File documentFolder = new File(conf.getConf(Configuration.DOCUMENT_FOLDER));
		
		String destFolderPath = conf.getConf(Configuration.DESTINATION_FOLDER);
		
		for (File file : resourceFolder.listFiles()) {
			copy(file, new File(destFolderPath + File.separator + file.getName()));
		}
		
		for (File file : documentFolder.listFiles()) {
			Article article = ArticleDecoder.decode(file);
			writeDocumentWithTemplate(template, article, new File(destFolderPath + File.separator + file.getName()));
		}
		
		/**
		 * !! Existing Problems !!
		 * 1. only copy files one layer downside given folder
		 * 2. search.js/categories.js/recent.js/page.js NOT created
		 */
	}
	
	private void writeDocumentWithTemplate(Template template, Article article, File toFile) throws GeneralException {
		if (!toFile.exists()) {
			try {
				toFile.createNewFile();
			} catch (IOException e) {
				throw new GeneralException("fail to create new file[" + toFile.getName() + "]", e);
			}
		}
		
		if (!article.getEnabled()) {
			copy(article.getFile(), toFile);
		} else {
			try (
					PrintWriter pw = new PrintWriter(toFile);
					) {
				int templateHeadContainerIndex = template.getHeadContainer().getFileStartPos();
				int templateBodyContainerIndex = template.getBodyContainer().getFileStartPos();
				pw.append(template.getFullText().substring(0, templateHeadContainerIndex))
				.append(article.getHead().getContentText())
				.append(template.getFullText().substring(templateHeadContainerIndex, templateBodyContainerIndex))
				.append(article.getBody().getContentText())
				.append(template.getFullText().substring(templateBodyContainerIndex));
				pw.flush();
			} catch (FileNotFoundException e) {
				throw new GeneralException("fail to write article to [" + toFile.getName() + "]", e);
			}
		}
	}
	
	private static void copy(File from, File to) throws GeneralException {
		if (!to.exists()) {
			try {
				to.createNewFile();
			} catch (IOException e) {
				throw new GeneralException("fail to create new file[" + to.getName() + "]", e);
			}
		}
		
		try (
				FileInputStream fis = new FileInputStream(from);
				FileOutputStream fos = new FileOutputStream(to);
				) {
			byte[] data = new byte[1024];
			int length;
			while ((length = fis.read(data)) != -1) {
				fos.write(data, 0, length);
			}
		} catch (IOException e) {
			throw new GeneralException("fail to copy file from [" + from.getName() + "] to [" + to.getName() + "]", e);
		}
		
	}

}
