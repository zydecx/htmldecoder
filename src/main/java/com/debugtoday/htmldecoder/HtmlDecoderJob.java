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
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.conf.FileConfiguration;
import com.debugtoday.htmldecoder.decoder.ArticleDecoder;
import com.debugtoday.htmldecoder.decoder.ThemeDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.export.ArticlePaginationExport;
import com.debugtoday.htmldecoder.export.TagPaginationExport;
import com.debugtoday.htmldecoder.output.SiteOutput;
import com.debugtoday.htmldecoder.output.object.SiteOutputArg;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.ArticleAbstract;
import com.debugtoday.htmldecoder.struct.Template;
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
		
		// sort descending
		Collections.sort(articleList, new Comparator<ArticleAbstract>() {

			@Override
			public int compare(ArticleAbstract o1, ArticleAbstract o2) {
				return o2.getLastUpdateDate().compareTo(o1.getLastUpdateDate());
			}
		});
		String navRecent = extractNavRecent(articleList, 5, confUtil.siteUrl);
		
		List<TagUtil> categoryList = analyzeArticleCategory(articleList);
		// sort descending
		Collections.sort(categoryList, new Comparator<TagUtil>() {

			@Override
			public int compare(TagUtil o1, TagUtil o2) {
				return o2.getArticleSet().size() - o1.getArticleSet().size();
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
		
		
		// export pagination pages of article
		new ArticlePaginationExport("", outputFolder, "", articleList, paginationSize, template, siteUrl).export();
		
		// export pagination pages of tags
		File topTagFolder = new File(outputFolder.getAbsolutePath() + File.separator + "tag");
		new TagPaginationExport("Tags", topTagFolder, "tag", tagList, paginationSize, template, siteUrl).export();
		
		// export pagination pages of categories
		File topCategoryFolder = new File(outputFolder.getAbsolutePath() + File.separator + "category");
		new TagPaginationExport("Categories", topCategoryFolder, "category", categoryList, paginationSize, template, siteUrl).export();

		// export pagination pages of article of each tag
		for (TagUtil tag : tagList) {
			File tagFolder = new File(outputFolder.getAbsolutePath() + File.separator + "tag" + File.separator + tag.getTag());
			new ArticlePaginationExport(tag.getTag(), tagFolder, "tag/" + tag.getTag(), new ArrayList<>(tag.getArticleSet()), paginationSize, template, siteUrl).export();
		}

		// export pagination pages of article of each category
		for (TagUtil category : categoryList) {
			File categoryFolder = new File(outputFolder.getAbsolutePath() + File.separator + "category" + File.separator + category.getTag());
			new ArticlePaginationExport(category.getTag(), categoryFolder, "category/" + category.getTag(), new ArrayList<>(category.getArticleSet()), paginationSize, template, siteUrl).export();
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
	
	private List<TagUtil> analyzeArticleCategory(List<ArticleAbstract> articleList) {
		Map<String, TagUtil> categoryMap = new HashMap<>();

		Iterator<ArticleAbstract> iter = articleList.iterator();
		while (iter.hasNext()) {
			ArticleAbstract article = iter.next();
			String[] categories = article.getCategories();
			if (categories == null || categories.length == 0) continue;
			
			for (String category : categories) {
				TagUtil categoryUtil = categoryMap.get(category);
				if (categoryUtil == null) {
					categoryUtil = new CategoryUtil(category);
					categoryMap.put(category, categoryUtil);
				}
				
				categoryUtil.getArticleSet().add(article);
			}
		}
		
		return new ArrayList<>(categoryMap.values());
	}
	
	private List<TagUtil> analyzeArticleTag(List<ArticleAbstract> articleList) {
		Map<String, TagUtil> tagMap = new HashMap<>();

		Iterator<ArticleAbstract> iter = articleList.iterator();
		while (iter.hasNext()) {
			ArticleAbstract article = iter.next();
			String[] tags = article.getTags();
			if (tags == null || tags.length == 0) continue;
			
			for (String tag : tags) {
				TagUtil tagUtil = tagMap.get(tag);
				if (tagUtil == null) {
					tagUtil = new TagUtil(tag);
					tagMap.put(tag, tagUtil);
				}
				
				tagUtil.getArticleSet().add(article);
			}
		}
		
		return new ArrayList<>(tagMap.values());
	}
	
	private String extractNavCategory(List<TagUtil> categoryList, int size, String siteUrl) {
		int length = Math.min(size, categoryList.size());
		
		StringBuilder navHtml = new StringBuilder("<nav><ul>").append("<strong>Category</strong>");
		for (int j = 0; j < length; j++) {
			String categoryName = categoryList.get(j).getTag();
			try {
				navHtml.append("<li><a href='").append(siteUrl).append("/category/")
						.append(URLEncoder.encode(categoryName, "UTF-8")).append("'>")
						.append(categoryName).append("(").append(categoryList.get(j).getArticleSet().size()).append(")").append("</a></li>");
			} catch (UnsupportedEncodingException e) {
				System.err.println("fail to create url of category[" + categoryName + "]");
			}
		}
		navHtml.append("</ul></nav>");
		
		return navHtml.toString();
	}
	
	private String extractNavTag(List<TagUtil> tagList, int size, String siteUrl) {
		int length = Math.min(size, tagList.size());
		
		StringBuilder navHtml = new StringBuilder("<nav><ul>").append("<strong>Tag</strong>");
		for (int j = 0; j < length; j++) {
			String tagName = tagList.get(j).tag;
			try {
				navHtml.append("<li><a href='").append(siteUrl).append("/tag/")
						.append(URLEncoder.encode(tagName, "UTF-8")).append("'>")
						.append(tagName).append("(").append(tagList.get(j).getArticleSet().size()).append(")").append("</a></li>");
			} catch (UnsupportedEncodingException e) {
				System.err.println("fail to create url of tag[" + tagName + "]");
			}
		}
		navHtml.append("</ul></nav>");
		
		return navHtml.toString();
	}
	
	private String extractNavRecent(List<ArticleAbstract> articleList, int size, String siteUrl) {
		
		int length = Math.min(size, articleList.size());
		
		StringBuilder navHtml = new StringBuilder("<nav><ul>").append("<strong>Recent</strong>");
		for (ArticleAbstract article : articleList) {
			navHtml.append("<li><a href='").append(siteUrl).append("/")
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
			toFile = new File(confUtil.outputFolder.getCanonicalPath() + File.separator + articleAbstract.getRelativePath().replace("/", File.separator));
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
	
	public class TagUtil {
		private String tag;
		private Set<ArticleAbstract> articleSet;
		
		public TagUtil(String tag) {
			this.tag = tag;
			this.articleSet = new HashSet<>();
		}
		
		public String getTag() {
			return this.tag;
		}
		
		public void setTag(String tag) {
			this.tag = tag;
		}
		
		public Set<ArticleAbstract> getArticleSet() {
			return this.articleSet;
		}
		
		public void setArticleSet(Set<ArticleAbstract> articleSet) {
			this.articleSet = articleSet;
		}
	}
	
	public class CategoryUtil extends TagUtil {
		
		public CategoryUtil(String category) {
			super(category);
		}
		
		public String getCategory() {
			return super.getTag();
		}
		
		public void setCategory(String category) {
			super.setTag(category);
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
