package com.debugtoday.htmldecoder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.conf.FileConfiguration;
import com.debugtoday.htmldecoder.decoder.ArticleDecoder;
import com.debugtoday.htmldecoder.decoder.ThemeDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.SiteOutput;
import com.debugtoday.htmldecoder.output.object.SiteOutputArg;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Theme;
import com.debugtoday.htmldecoder.util.FileUtil;

public class HtmlDecoderJob {
	
	public static void main(String[] args) {
		System.out.println("Welcome to HtmlDecoder project!");
		String confFilePath = args[0];
		Configuration conf = new FileConfiguration(confFilePath);
		try {
			conf.init();
			ConfigurationWrapper confWrapper = ConfigurationWrapper.parse(conf);
			confWrapper.check();
			new HtmlDecoderJob(confWrapper).start();
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ConfigurationWrapper conf;
	
	public HtmlDecoderJob(ConfigurationWrapper conf) {
		this.conf = conf;
	}
	
	public void start() throws GeneralException {
		
		Theme theme = ThemeDecoder.decode(conf);
		
		List<Article> articleList = analyzeContentFolderAndMoveResources(conf.getContentFile(), true);
		List<Article> staticPageList = analyzeContentFolderAndMoveResources(conf.getStaticPageFile(), false);
		
		new SiteOutput(conf, theme).export(new SiteOutputArg(articleList, staticPageList));
		
	}
	
	/**
	 * analyze content folder, decode article to Java Object and copy other resources to output folder.
	 * @param contentFolder
	 * @param skipStaticPage
	 * @return
	 * @throws GeneralException
	 */
	private List<Article> analyzeContentFolderAndMoveResources(File contentFolder, boolean skipStaticPage) throws GeneralException {
		List<Article> articleList = new ArrayList<>();
		
		for (File file : contentFolder.listFiles()) {
			if (isIgnored(file, skipStaticPage)) {
				continue;
			}
			
			if (file.isDirectory()) {
				try {
					String relativePath = FileUtil.relativePath(conf.getContentFile(), file);
					File tempDir = new File(conf.getOutputFile().getCanonicalPath() + File.separator + relativePath.replace("/", File.separator));
					if (!tempDir.exists()) {
						tempDir.mkdirs();
					}
				} catch (IOException e) {
					throw new GeneralException(e);
				}
				articleList.addAll(analyzeContentFolderAndMoveResources(file, skipStaticPage));
			} else {
				Article article = analyzeContentFileAndMoveResource(file);
				if (article != null) {
					articleList.add(article);
				}
			}
		}
		
		return articleList;
	}
	
	/**
	 * analyze content file, decode to Java Object if the file is an article; otherwise, copy to output folder.
	 * @param file
	 * @return
	 * @throws GeneralException
	 */
	private Article analyzeContentFileAndMoveResource(File file) throws GeneralException {
		if (file.getName().endsWith(".htm") || file.getName().endsWith(".html")) {
			Article article = ArticleDecoder.decode(file, conf);
			
			return article;
		} else {
			try {
				String relativePath = FileUtil.relativePath(conf.getContentFile(), file);
				FileUtil.copy(file, new File(conf.getOutputFile().getCanonicalPath() + File.separator + relativePath.replace("/", File.separator)));
			} catch (IOException e) {
				throw new GeneralException(e);
			}
			return null;
		}
	}
	
	private boolean isIgnored(File file, boolean skipStaticPage) {
		String filePath = file.getAbsolutePath();
		String contentFilePath = conf.getContentFile().getAbsolutePath();
		for (String s : conf.getIgnoreList()) {
			if (filePath.indexOf(contentFilePath + File.separator + s) == 0) {
				return true;
			}
		}
		
		// ignore static pages if skipStaticPage=true
		if (skipStaticPage) {
			String staticPagePath = conf.getStaticPageFile().getAbsolutePath();
			if (filePath.indexOf(staticPagePath) == 0) {
				return true;
			}
		}
		
		return false;
	}
}
