package com.debugtoday.htmldecoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.FileConfiguration;
import com.debugtoday.htmldecoder.decoder.ArticleDecoder;
import com.debugtoday.htmldecoder.decoder.TemplateDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.ArticleAbstract;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.util.FileUtil;

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
		
		String siteUrl = conf.getConf(Configuration.SITE_URL);
		if (siteUrl == null) {
			siteUrl = ".";
		} else if (siteUrl.endsWith("/")) {
			siteUrl = siteUrl.substring(0, siteUrl.length() - 1);
		}
		
		String ignoreListStr = conf.getConf(Configuration.IGNORE_PATH_LIST);
		String[] ignoreList = ignoreListStr == null ? new String[]{} : ignoreListStr.split(",");
		for (int i = 0; i < ignoreList.length; i++) {
			String s = ignoreList[i];
			if (s.startsWith(" ") || s.endsWith(" ")) {
				ignoreList[i] = s.trim();
			}
		}
		
		int paginationSize = 10;
		try {
			paginationSize = Integer.parseInt(conf.getConf(Configuration.PAGINATION_SIZE));
		} catch (Exception e) {
			System.err.println("fail to read paginationsize, use default instead;" + e.getMessage());
		}
		
		File templateFile = new File(conf.getConf(Configuration.TEMPLATE_PATH));
		Template template = TemplateDecoder.decode(templateFile, conf);
		
		File contentFolder = new File(conf.getConf(Configuration.CONTENT_PATH));
		File outputFolder = new File(conf.getConf(Configuration.OUTPUT_PATH));
		File staticPageFolder = new File(contentFolder.getAbsoluteFile() + File.separator + conf.getConf(Configuration.STATIC_PAGE_PATH));
		
		ConfigurationUtil confUtil = new ConfigurationUtil(conf, siteUrl, paginationSize, ignoreList, templateFile, contentFolder, outputFolder, staticPageFolder);
		
		/**
		 * !! Waiting to be fininshed !!
		 */
		List<ArticleAbstract> articleList = analyzeContentFolderAndMoveResources(contentFolder, confUtil);
		// sort descending
		Collections.sort(articleList, new Comparator<ArticleAbstract>() {

			@Override
			public int compare(ArticleAbstract o1, ArticleAbstract o2) {
				return o2.getLastUpdateDate().compareTo(o1.getLastUpdateDate());
			}
		});
		String navRecent = extractNavRecent(articleList, 5, confUtil.siteUrl);
		
		List<CategoryUtil> categoryList = analyzeArticleCategory(articleList);
		// sort descending
		Collections.sort(categoryList, new Comparator<CategoryUtil>() {

			@Override
			public int compare(CategoryUtil o1, CategoryUtil o2) {
				return o2.articleSet.size() - o1.articleSet.size();
			}
		});
		String navCategory = extractNavCategory(categoryList, 5, confUtil.siteUrl);

		List<TagUtil> tagList = analyzeArticleTag(articleList);
		// sort descending
		Collections.sort(tagList, new Comparator<TagUtil>() {

			@Override
			public int compare(TagUtil o1, TagUtil o2) {
				return o2.articleSet.size() - o1.articleSet.size();
			}
		});
		String navTag = extractNavTag(tagList, 5, confUtil.siteUrl);
		
		
		String navHtml = navRecent + navCategory + navTag;
		template.setNavHtml(navHtml);
		for (ArticleAbstract article : articleList) {
			writeDocumentWithTemplate(template, article, confUtil);
		}
		
		/**
		 * !! Existing Problems !!
		 * 1. only copy files one layer downside given folder
		 * 2. categories.js/recent.js/page.js NOT created
		 */

		/*writeAccessorialScriptOfPage(new File(pageScriptPath), articleList);
		writeAccessorialScriptOfCategory(new File(pageCategoryPath), articleList);
		writeAccessorialScriptOfRecent(new File(pageRecentPath), articleList);*/
	}
	
	/**
	 * analyze content folder, decode article to Java Object and copy other resources to output folder.
	 * @param contentFolder
	 * @param confUtil
	 * @return
	 * @throws GeneralException
	 */
	private List<ArticleAbstract> analyzeContentFolderAndMoveResources(File contentFolder, ConfigurationUtil confUtil) throws GeneralException {
		List<ArticleAbstract> articleAbstractList = new ArrayList<>();
		
		for (File file : contentFolder.listFiles()) {
			if (confUtil.isIgnored(file)) {
				continue;
			}
			
			if (file.isDirectory()) {
				try {
					String relativePath = FileUtil.relativePath(confUtil.contentFolder, file);
					File tempDir = new File(confUtil.outputFolder.getCanonicalPath() + File.separator + relativePath.replace("/", File.separator));
					if (!tempDir.exists()) {
						tempDir.mkdirs();
					}
				} catch (IOException e) {
					throw new GeneralException(e);
				}
				articleAbstractList.addAll(analyzeContentFolderAndMoveResources(file, confUtil));
			} else {
				ArticleAbstract article = analyzeContentFileAndMoveResource(file, confUtil);
				if (article != null) {
					articleAbstractList.add(article);
				}
			}
		}
		
		return articleAbstractList;
	}
	
	/**
	 * analyze content file, decode to Java Object if the file is an article; otherwise, copy to output folder.
	 * @param file
	 * @param confUtil
	 * @return
	 * @throws GeneralException
	 */
	private ArticleAbstract analyzeContentFileAndMoveResource(File file, ConfigurationUtil confUtil) throws GeneralException {
		String relativePath;
		try {
			relativePath = FileUtil.relativePath(confUtil.contentFolder, file);
		} catch (IOException e) {
			throw new GeneralException(e);
		}
		
		if (file.getName().endsWith(".htm") || file.getName().endsWith(".html")) {
			Article article = ArticleDecoder.decode(file, confUtil.conf);
			ArticleAbstract articleAbstract = article.formatArticleAbsract();
			
			articleAbstract.setRelativePath("/" + relativePath);
			
			return articleAbstract;
		} else {
			try {
				FileUtil.copy(file, new File(confUtil.outputFolder.getCanonicalPath() + File.separator + relativePath.replace("/", File.separator)));
			} catch (IOException e) {
				throw new GeneralException(e);
			}
			return null;
		}
	}
	
	private List<CategoryUtil> analyzeArticleCategory(List<ArticleAbstract> articleList) {
		Map<String, CategoryUtil> categoryMap = new HashMap<>();

		Iterator<ArticleAbstract> iter = articleList.iterator();
		while (iter.hasNext()) {
			ArticleAbstract article = iter.next();
			String[] categories = article.getCategories();
			if (categories == null || categories.length == 0) continue;
			
			for (String category : categories) {
				CategoryUtil categoryUtil = categoryMap.get(category);
				if (categoryUtil == null) {
					categoryUtil = new CategoryUtil(category);
					categoryMap.put(category, categoryUtil);
				}
				
				categoryUtil.articleSet.add(article);
			}
		}
		
		return new ArrayList<>(categoryMap.values());
	}
	
	private List<TagUtil> analyzeArticleTag(List<ArticleAbstract> articleList) {
		Map<String, TagUtil> tagMap = new HashMap<>();

		Iterator<ArticleAbstract> iter = articleList.iterator();
		while (iter.hasNext()) {
			ArticleAbstract article = iter.next();
			String[] categories = article.getCategories();
			if (categories == null || categories.length == 0) continue;
			
			for (String tag : categories) {
				TagUtil tagUtil = tagMap.get(tag);
				if (tagUtil == null) {
					tagUtil = new TagUtil(tag);
					tagMap.put(tag, tagUtil);
				}
				
				tagUtil.articleSet.add(article);
			}
		}
		
		return new ArrayList<>(tagMap.values());
	}
	
	private String extractNavCategory(List<CategoryUtil> categoryList, int size, String siteUrl) {
		int length = Math.min(size, categoryList.size());
		
		StringBuilder navHtml = new StringBuilder("<nav><ul>");
		for (int j = 0; j < length; j++) {
			String categoryName = categoryList.get(j).category;
			try {
				navHtml.append("<li><a href='").append(siteUrl).append("/category/")
						.append(URLEncoder.encode(categoryName, "UTF-8")).append("'>")
						.append(categoryName).append("</a></li>");
			} catch (UnsupportedEncodingException e) {
				System.err.println("fail to create url of category[" + categoryName + "]");
			}
		}
		navHtml.append("</ul></nav>");
		
		return navHtml.toString();
	}
	
	private String extractNavTag(List<TagUtil> tagList, int size, String siteUrl) {
		int length = Math.min(size, tagList.size());
		
		StringBuilder navHtml = new StringBuilder("<nav><ul>");
		for (int j = 0; j < length; j++) {
			String tagName = tagList.get(j).tag;
			try {
				navHtml.append("<li><a href='").append(siteUrl).append("/tag/")
						.append(URLEncoder.encode(tagName, "UTF-8")).append("'>")
						.append(tagName).append("</a></li>");
			} catch (UnsupportedEncodingException e) {
				System.err.println("fail to create url of tag[" + tagName + "]");
			}
		}
		navHtml.append("</ul></nav>");
		
		return navHtml.toString();
	}
	
	private String extractNavRecent(List<ArticleAbstract> articleList, int size, String siteUrl) {
		
		int length = Math.min(size, articleList.size());
		
		StringBuilder navHtml = new StringBuilder("<nav><ul>");
		for (ArticleAbstract article : articleList) {
			navHtml.append("<li><a href='").append(siteUrl)
					.append(article.getRelativePath()).append("'>")
					.append(article.getTitle()).append("</a></li>");
		}
		navHtml.append("</ul></nav>");
		
		return navHtml.toString();
	}
	
	private void writeAccessorialScriptOfPage(File toFile, List<ArticleAbstract> articleList) throws GeneralException {
		if (!toFile.exists()) {
			try {
				toFile.createNewFile();
			} catch (IOException e) {
				throw new GeneralException("fail to create new file[" + toFile.getPath() + "]", e);
			}
		}
		
		try (
				PrintWriter pw = new PrintWriter(toFile);
				) {
			pw.append("htmldecoderPageList=").append(new ObjectMapper().writeValueAsString(articleList));
		} catch (IOException e) {
			throw new GeneralException("fail to write to file [" + toFile.getPath() + "]", e);
		}
	}
	
	private void writeAccessorialScriptOfCategory(File toFile, List<ArticleAbstract> articleList) throws GeneralException {
		if (!toFile.exists()) {
			try {
				toFile.createNewFile();
			} catch (IOException e) {
				throw new GeneralException("fail to create new file[" + toFile.getPath() + "]", e);
			}
		}
		
		class ArticleIndexWrapper {
			String category;
			Set<Integer> articleIndex;
			
			public ArticleIndexWrapper(String category) {
				this.category = category;
				this.articleIndex = new HashSet<>();
			}
		}
		
		Map<String, ArticleIndexWrapper> categoryMap = new HashMap<>();
		int i = -1;
		Iterator<ArticleAbstract> iter = articleList.iterator();
		while (iter.hasNext()) {
			i++;
			ArticleAbstract article = iter.next();
			String[] categories = article.getCategories();
			if (categories == null || categories.length == 0) continue;
			
			for (String category : categories) {
				ArticleIndexWrapper articleIndex = categoryMap.get(category);
				if (articleIndex == null) {
					articleIndex = new ArticleIndexWrapper(category);
					categoryMap.put(category, articleIndex);
				}
				
				articleIndex.articleIndex.add(i);
			}
		}
		
		List<ArticleIndexWrapper> articleIndexList = new ArrayList<>(categoryMap.values());
		Collections.sort(articleIndexList, new Comparator<ArticleIndexWrapper>() {

			@Override
			public int compare(ArticleIndexWrapper o1, ArticleIndexWrapper o2) {
				return o1.articleIndex.size() - o2.articleIndex.size();
			}
		});
		
		Map<String, Set<Integer>> selectedCategoryMap = new LinkedHashMap<>();
		for (ArticleIndexWrapper articleIndex : articleIndexList.subList(0, Math.min(5, articleIndexList.size()))) {
			selectedCategoryMap.put(articleIndex.category, articleIndex.articleIndex);
		}
		
		
		try (
				PrintWriter pw = new PrintWriter(toFile);
				) {
			pw.append("htmldecoderCategoryObject=").append(new ObjectMapper().writeValueAsString(selectedCategoryMap));
		} catch (IOException e) {
			throw new GeneralException("fail to write to file [" + toFile.getPath() + "]", e);
		}
	}
	
	private void writeAccessorialScriptOfRecent(File toFile, List<ArticleAbstract> articleList) throws GeneralException {
		if (!toFile.exists()) {
			try {
				toFile.createNewFile();
			} catch (IOException e) {
				throw new GeneralException("fail to create new file[" + toFile.getPath() + "]", e);
			}
		}
		
		class ArticleIndexWrapper {
			int index;
			ArticleAbstract article;
			
			public ArticleIndexWrapper(ArticleAbstract article, int index) {
				this.article = article;
				this.index = index;
			}
		}
		
		List<ArticleIndexWrapper> articleIndexList = new ArrayList<>();
		
		int i = -1;
		Iterator<ArticleAbstract> iter = articleList.iterator();
		while (iter.hasNext()) {
			i++;
			articleIndexList.add(new ArticleIndexWrapper(iter.next(), i));
		}
		
		Collections.sort(articleIndexList, new Comparator<ArticleIndexWrapper>() {

			@Override
			public int compare(ArticleIndexWrapper o1, ArticleIndexWrapper o2) {
				return o1.article.getLastUpdateDate().compareTo(o2.article.getLastUpdateDate());
			}
		});
		
		List<Integer> selectedArticleIndexList = new ArrayList<>();
		int length = Math.min(5, articleIndexList.size());
		i = 0;
		for (; i < length; i++) {
			selectedArticleIndexList.add(articleIndexList.get(i).index);
		}
		
		
		try (
				PrintWriter pw = new PrintWriter(toFile);
				) {
			pw.append("htmldecoderRecentObject=").append(new ObjectMapper().writeValueAsString(selectedArticleIndexList));
		} catch (IOException e) {
			throw new GeneralException("fail to write to file [" + toFile.getPath() + "]", e);
		}
	}
	
	private void writeDocumentWithTemplate(Template template, ArticleAbstract articleAbstract, ConfigurationUtil confUtil) throws GeneralException {
		File toFile;
		try {
			toFile = new File(confUtil.outputFolder.getCanonicalPath() + articleAbstract.getRelativePath().replace("/", File.separator));
			if (!toFile.exists()) {
				toFile.createNewFile();
			}
		} catch (IOException e) {
			throw new GeneralException("fail to create new file", e);
		}
		
		Article article  = articleAbstract.getArticle();
		
		if (!article.getEnabled()) {
			FileUtil.copy(article.getFile(), toFile);
		} else {
			try (
					PrintWriter pw = new PrintWriter(toFile);
					) {
				int templateHeadContainerIndex = template.getHeadContainer().getFileStartPos();
				int templateBodyContainerIndex = template.getBodyContainer().getFileStartPos();
				int templateNavContainerIndex = template.getNavContainer().getFileStartPos();
				pw.append(template.getFullText().substring(0, templateHeadContainerIndex))
				.append(article.getHead().getContentText())
				.append(template.getFullText().substring(templateHeadContainerIndex, templateBodyContainerIndex))
				.append(article.getBody().getContentText())
				.append(template.getFullText().substring(templateBodyContainerIndex, templateNavContainerIndex))
				.append(template.getNavHtml())
				.append(template.getFullText().substring(templateNavContainerIndex));
				pw.flush();
			} catch (FileNotFoundException e) {
				throw new GeneralException("fail to write to file [" + toFile.getPath() + "]", e);
			}
		}
	}
	
	private class TagUtil {
		String tag;
		Set<ArticleAbstract> articleSet;
		
		public TagUtil(String tag) {
			this.tag = tag;
			this.articleSet = new HashSet<>();
		}
	}
	
	private class CategoryUtil {
		String category;
		Set<ArticleAbstract> articleSet;
		
		public CategoryUtil(String category) {
			this.category = category;
			this.articleSet = new HashSet<>();
		}
	}
	
	private class ConfigurationUtil {
		public ConfigurationUtil(Configuration conf, String siteUrl, int paginationSize, String[] ignoreList, File templateFile, File contentFolder, File outputFolder, File staticPageFolder) {
			this.conf = conf;
			this.siteUrl  =siteUrl;
			this.paginationSize = paginationSize;
			this.ignoreList = ignoreList;
			this.templateFile = templateFile;
			this.contentFolder = contentFolder;
			this.outputFolder = outputFolder;
			this.staticPageFolder = staticPageFolder;
		}
		
		Configuration conf;
		String siteUrl;
		int paginationSize;
		String[] ignoreList;
		File templateFile;
		File contentFolder;
		File outputFolder;
		File staticPageFolder;
		
		public boolean isIgnored(File file) {
			String filePath = file.getAbsolutePath();
			String contentFilePath = contentFolder.getAbsolutePath();
			for (String s : ignoreList) {
				if (filePath.indexOf(contentFilePath + File.separator + s) == 0) {
					return true;
				}
			}
			
			return false;
		}
	}

}
