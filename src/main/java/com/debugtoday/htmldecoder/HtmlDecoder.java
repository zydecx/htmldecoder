package com.debugtoday.htmldecoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
		
		File templateFile = new File(conf.getConf(Configuration.TEMPLATE_FILE));
		Template template = TemplateDecoder.decode(templateFile);
		
		File resourceFolder = new File(conf.getConf(Configuration.RESOURCE_FOLDER));
		File documentFolder = new File(conf.getConf(Configuration.DOCUMENT_FOLDER));
		
		String destFolderPath = conf.getConf(Configuration.DESTINATION_FOLDER);
		String pageScriptPath = conf.getConf(Configuration.SCRIPT_PAGE_FILE);
		String pageCategoryPath = conf.getConf(Configuration.SCRIPT_CATEGORY_FILE);
		String pageRecentPath = conf.getConf(Configuration.SCRIPT_RECENT_FILE);
		
		for (File file : resourceFolder.listFiles()) {
			FileUtil.copy(file, new File(destFolderPath + File.separator + file.getName()));
		}
		
		List<ArticleAbstract> articleList = new ArrayList<>();
		for (File file : documentFolder.listFiles()) {
			Article article = ArticleDecoder.decode(file);
			ArticleAbstract articleAbstract = article.formatArticleAbsract();
			articleAbstract.setRelativePath("/" + FileUtil.relativePath(documentFolder, file));
			articleList.add(articleAbstract);
			writeDocumentWithTemplate(template, article, new File(destFolderPath + File.separator + file.getName()));
		}
		
		/**
		 * !! Existing Problems !!
		 * 1. only copy files one layer downside given folder
		 * 2. categories.js/recent.js/page.js NOT created
		 */

		writeAccessorialScriptOfPage(new File(pageScriptPath), articleList);
		writeAccessorialScriptOfCategory(new File(pageCategoryPath), articleList);
		writeAccessorialScriptOfRecent(new File(pageRecentPath), articleList);
	}
	
	private void writeAccessorialScriptOfPage(File toFile, List<ArticleAbstract> articleList) throws GeneralException {
		if (!toFile.exists()) {
			try {
				toFile.createNewFile();
			} catch (IOException e) {
				throw new GeneralException("fail to create new file[" + toFile.getName() + "]", e);
			}
		}
		
		try (
				PrintWriter pw = new PrintWriter(toFile);
				) {
			pw.append("htmldecoderPageList=").append(new ObjectMapper().writeValueAsString(articleList));
		} catch (IOException e) {
			throw new GeneralException("fail to write to file [" + toFile.getName() + "]", e);
		}
	}
	
	private void writeAccessorialScriptOfCategory(File toFile, List<ArticleAbstract> articleList) throws GeneralException {
		if (!toFile.exists()) {
			try {
				toFile.createNewFile();
			} catch (IOException e) {
				throw new GeneralException("fail to create new file[" + toFile.getName() + "]", e);
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
			throw new GeneralException("fail to write to file [" + toFile.getName() + "]", e);
		}
	}
	
	private void writeAccessorialScriptOfRecent(File toFile, List<ArticleAbstract> articleList) throws GeneralException {
		if (!toFile.exists()) {
			try {
				toFile.createNewFile();
			} catch (IOException e) {
				throw new GeneralException("fail to create new file[" + toFile.getName() + "]", e);
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
			throw new GeneralException("fail to write to file [" + toFile.getName() + "]", e);
		}
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
			FileUtil.copy(article.getFile(), toFile);
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
				throw new GeneralException("fail to write to file [" + toFile.getName() + "]", e);
			}
		}
	}

}
