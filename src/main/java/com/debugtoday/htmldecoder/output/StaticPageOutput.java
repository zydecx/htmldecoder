package com.debugtoday.htmldecoder.output;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;
import com.debugtoday.htmldecoder.util.FileUtil;

public class StaticPageOutput implements Output {
	
	private static final Logger logger = CommonLog.getLogger();
	
	private ConfigurationWrapper conf;
	private Theme theme;
	
	public StaticPageOutput(ConfigurationWrapper conf, Theme theme) {
		this.conf = conf;
		this.theme = theme;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		List<Article> arg = (List<Article>) object;

		Template itemTemplate = theme.getTemplates().get(TemplateKey.STATIC_PAGE_ITEM);
		Template groupTemplate = theme.getTemplates().get(TemplateKey.STATIC_PAGE_GROUP);
		
		List<StaticPageWrapper> wrapperList = parseStaticPageList(arg);
		StringBuilder sb = new StringBuilder();
		for (StaticPageWrapper wrapper : wrapperList) {
			if (wrapper.getArticle() != null) {
				sb.append(exportFullTextOfItem(itemTemplate, wrapper));
			} else {
				sb.append(exportFullTextOfGroup(itemTemplate, groupTemplate, wrapper));
			}
		}
		
		return sb.toString();
	}
	
	private String exportFullTextOfItem(Template template, StaticPageWrapper wrapper) {
		Article article = wrapper.getArticle();
		
		String fullText = template.getFullText()
				.replaceAll(
						GeneralDecoder.formatArgumentRegex("url"),
						article.formatUrl(conf.getSiteUrl()))
				.replaceAll(
						GeneralDecoder.formatArgumentRegex("title"),
						article.extractTitle());
		return fullText;
		
	}
	
	private String exportFullTextOfGroup(Template itemTemplate, Template groupTemplate, StaticPageWrapper wrapper) {
		String fullText = groupTemplate.getFullText();
		
		StaticPageWrapper indexWrapper = null;
		StringBuilder sb = new StringBuilder();
		for (StaticPageWrapper subWrapper : wrapper.getSubArticles()) {
			if (subWrapper.getArticle() != null) {
				if (subWrapper.getFile().getName().startsWith("index.htm")) {
					indexWrapper = subWrapper;
				} else {
					sb.append(exportFullTextOfItem(itemTemplate, subWrapper));
				}
			} else {
				sb.append(exportFullTextOfGroup(itemTemplate,groupTemplate, subWrapper));
			}
		}
		
		fullText = fullText
				.replaceAll(
						GeneralDecoder.formatArgumentRegex("url"),
						indexWrapper == null ? "#" : indexWrapper.getArticle().formatUrl(conf.getSiteUrl()))
				.replaceAll(
						GeneralDecoder.formatArgumentRegex("title"),
						indexWrapper == null ? FileUtil.fileName(wrapper.getFile()) : indexWrapper.getArticle().extractTitle())
				.replaceAll(
						GeneralDecoder.formatPlaceholderRegex("sub_menu_container"),
						sb.toString());
		
		return fullText;
		
	}
	
	private List<StaticPageWrapper> parseStaticPageList(List<Article> articleList) throws GeneralException {
		List<StaticPageWrapper> mainList = new ArrayList<>();
		File rootFile = conf.getStaticPageFile();
		
		Map<String, StaticPageWrapper> wrapperMap = new HashMap<>();
		for (Article article : articleList) {
			File file = article.getFile();
			try {
				String relativePath = FileUtil.relativePath(rootFile, file);
				int depth = -1;
				int separatorIndex = -1;
				do {
					depth++;
					separatorIndex = relativePath.indexOf("/", separatorIndex + 1);
				} while (separatorIndex != -1);
				
				File parentFile = file.getParentFile();
				String parentRelativePath = FileUtil.relativePath(rootFile, parentFile);
				StaticPageWrapper wrapper = new StaticPageWrapper(article, relativePath, parentRelativePath, depth);
				wrapperMap.put(relativePath, wrapper);
				
				if (depth > 0) {
					// add parent file of article recursive until root file of static page
					while (--depth >= 0) {
						file = parentFile;
						relativePath = parentRelativePath;
						parentFile = file.getParentFile();
						parentRelativePath = FileUtil.relativePath(rootFile, parentFile);
						if (!wrapperMap.containsKey(relativePath)) {
							wrapper = new StaticPageWrapper(file, relativePath, parentRelativePath, depth);
							wrapperMap.put(relativePath, wrapper);
						}
					}
				}
			} catch (IOException e) {
				logger.error("unrecognized error");
				throw new GeneralException("unrecognized error", e);
			}
			
		}
		
		// fill <i>subArticles</i> of each wrapper
		for (StaticPageWrapper wrapper : wrapperMap.values()) {
			if (wrapper.getDepth() == 0) {
				mainList.add(wrapper);	// main menu of static page
				continue;
			}
			
			wrapperMap.get(wrapper.getParentRelativePath()).getSubArticles().add(wrapper);
		}
		
		return mainList;
	}
	
	/**
	 * wrap each article or folder under staticpage folder.<br>
	 * Obviously, <i>article</i> and <i>subArticles</i> CANNOT have value at the same time.
	 * @author zydecx
	 *
	 */
	private static class StaticPageWrapper {
		
		private static int INDEX_GENERATOR = 1;
		
		private int index;
		private Article article;
		private File file;
		private String relativePath;
		private String parentRelativePath;
		private int depth;
		private StaticPageWrapper parent;
		private List<StaticPageWrapper> subArticles;
		
		/**
		 * For an article
		 * @param article
		 * @param relativePath
		 * @param parentRelativePath
		 * @param depth
		 */
		public StaticPageWrapper(Article article, String relativePath, String parentRelativePath, int depth) {
			this.file = article.getFile();
			this.relativePath = relativePath;
			this.parentRelativePath = parentRelativePath;
			this.depth = depth;
			this.parent = null;
			this.article = article;
			this.subArticles = null;
			this.index = INDEX_GENERATOR++;
		}
		
		/**
		 * For a folder
		 * @param file
		 * @param relativePath
		 * @param parentRelativePath
		 * @param depth
		 */
		public StaticPageWrapper(File file, String relativePath, String parentRelativePath, int depth) {
			this.file = file;
			this.relativePath = relativePath;
			this.parentRelativePath = parentRelativePath;
			this.depth = depth;
			this.parent = null;
			this.article = null;
			this.subArticles = new ArrayList<>();
			this.index = INDEX_GENERATOR++;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getRelativePath() {
			return relativePath;
		}

		public void setRelativePath(String relativePath) {
			this.relativePath = relativePath;
		}

		public String getParentRelativePath() {
			return parentRelativePath;
		}

		public void setParentRelativePath(String parentRelativePath) {
			this.parentRelativePath = parentRelativePath;
		}

		public int getDepth() {
			return depth;
		}

		public void setDepth(int depth) {
			this.depth = depth;
		}
		
		public StaticPageWrapper getParent() {
			return parent;
		}
		
		public void setParent(StaticPageWrapper parent) {
			this.parent = parent;
		}

		public Article getArticle() {
			return article;
		}

		public void setArticle(Article article) {
			this.article = article;
		}

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}

		public List<StaticPageWrapper> getSubArticles() {
			return subArticles;
		}

		public void setSubArticles(List<StaticPageWrapper> subArticles) {
			this.subArticles = subArticles;
		}

	}

}
