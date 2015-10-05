package com.debugtoday.htmldecoder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.conf.FileConfiguration;
import com.debugtoday.htmldecoder.decoder.ArticleDecoder;
import com.debugtoday.htmldecoder.decoder.ThemeDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.output.SiteOutput;
import com.debugtoday.htmldecoder.output.object.SiteOutputArg;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Theme;
import com.debugtoday.htmldecoder.util.FileUtil;

public class HtmlDecoderJob {
	
	private static final Logger logger = CommonLog.getLogger();
	
	public static void main(String[] args) {
		logger.info("Welcome to HtmlDecoder project!");
		String confFilePath = args.length > 0 ? args[0] : null;
		Configuration conf = new FileConfiguration(confFilePath);
		try {
			logger.info("init configuration...");
			conf.init();
			logger.info("init configuration done.");
			ConfigurationWrapper confWrapper = ConfigurationWrapper.parse(conf);
			logger.info("check configuration...");
			confWrapper.check();
			logger.info("check configuration done.");
			logger.info("output site...");
			new HtmlDecoderJob(confWrapper).start();
			logger.info("output site done.");
		} catch (GeneralException e) {
			logger.warn("work fails", e);
		}
	}
	
	private ConfigurationWrapper conf;
	
	public HtmlDecoderJob(ConfigurationWrapper conf) {
		this.conf = conf;
	}
	
	public void start() throws GeneralException {
		logger.info("decode theme...");
		Theme theme = ThemeDecoder.decode(conf);
		logger.info("decode theme done.");
		
		logger.info("decode articles...");
		List<Article> articleList = analyzeContentFolderAndMoveResources(conf.getContentFile(), true);
		logger.info("decode articles of size [" + articleList.size() + "]");
		logger.info("decode static pages...");
		List<Article> staticPageList = analyzeContentFolderAndMoveResources(conf.getStaticPageFile(), false);
		logger.info("decode static pages of size [" + articleList.size() + "]");
		logger.info("export site...");
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
		
		if (!contentFolder.isDirectory()) {
			return articleList;
		}
		
		for (File file : contentFolder.listFiles()) {
			if (isIgnored(file, skipStaticPage)) {
				continue;
			}
			
			if (file.isDirectory()) {
				try {
					String relativePath = FileUtil.relativePath(conf.getContentFile(), file);
					File tempDir = new File(conf.getOutputFile().getCanonicalPath() + File.separator + relativePath.replace("/", File.separator));
					if (!tempDir.exists()) {
						logger.info("move resouce- make directory[" + tempDir.getAbsolutePath() + "]");
						tempDir.mkdirs();
					}
				} catch (IOException e) {
					throw new GeneralException(e);
				}
				articleList.addAll(analyzeContentFolderAndMoveResources(file, skipStaticPage));
			} else {
				logger.info("read article from [" + file.getAbsolutePath() + "]");
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
		if (ArticleDecoder.isArticleFile(file)) {
			Article article = ArticleDecoder.decode(file, conf);
			
			return article;
		} else {
			try {
				String relativePath = FileUtil.relativePath(conf.getContentFile(), file);
				File toFile = new File(conf.getOutputFile().getCanonicalPath() + File.separator + relativePath.replace("/", File.separator));
				logger.info("move resouce- copy resource to [" + toFile.getAbsolutePath() + "]");
				FileUtil.copy(file, toFile);
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
