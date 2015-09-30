package com.debugtoday.htmldecoder.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.ArticleAbstract;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.util.FileUtil;

public class ArticlePaginationExport {
	private String siteUrl;
	private Template template;
	private int paginationSize;
	private String title;
	private File topFolder;
	private String topRelativePath;
	private List<ArticleAbstract> articleList;
	
	public ArticlePaginationExport(String title, File topFolder, String topRelativePath, List<ArticleAbstract> articleList, int paginationSize, Template template, String siteUrl) {
		this.title = title;
		this.topFolder = topFolder;
		this.topRelativePath = topRelativePath;
		this.articleList = articleList;
		this.siteUrl = siteUrl;
		this.template = template;
		this.paginationSize = paginationSize;
	}
	
	public void export() throws GeneralException {
		int articleSize = articleList.size();
		int pageSize = (int) Math.ceil(articleSize * 1.0 / paginationSize);
		
		for (int i = 1; i <= pageSize; i++) {
			exportPage(articleList.subList((i - 1) * paginationSize, Math.min(i * paginationSize, articleSize)), i, pageSize);
		}
	}
	
	private void exportIndexPage(List<ArticleAbstract> articleList, int pageSize) throws GeneralException {
		exportPage(articleList, 1, pageSize);
	}
	
	private void exportPage(List<ArticleAbstract> articleList, int num, int pageSize) throws GeneralException {
		String paginationHtml = "";
		if (pageSize > 1) {
			paginationHtml = extractPaginationHtml(num, pageSize);
		}
		
		File folder;
		try {
			String folderPath = topFolder.getCanonicalPath() + (num == 1 ? "" : (File.separator + num));
			folder = new File(folderPath);
		} catch (IOException e) {
			throw new GeneralException("fail to access file[" + topFolder.getAbsolutePath() + "]", e);
		}
		
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder.getAbsolutePath() + File.separator + "index.html");	// as for canonicalpath is used when creating folder, it's ok to use absolutepath here
		writePageToFile(file, articleList, paginationHtml);
	}
	
	private void writePageToFile(File file, List<ArticleAbstract> articleList, String paginationHtml) throws GeneralException {
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			throw new GeneralException("fail to create new file[" + file.getAbsolutePath() + "]", e);
		}
		
		try (
				PrintWriter pw = new PrintWriter(file);
				) {
			int templateHeadContainerIndex = template.getHeadContainer().getFileStartPos();
			int templateBodyContainerIndex = template.getBodyContainer().getFileStartPos();
			int templateNavContainerIndex = template.getNavContainer().getFileStartPos();
			pw.append(template.getFullText().substring(0, templateHeadContainerIndex))
			.append("<title>").append(title).append("</title>")
			.append(template.getFullText().substring(templateHeadContainerIndex, templateBodyContainerIndex))
			.append(extractArticleListHtml(articleList))
			.append(paginationHtml)
			.append(template.getFullText().substring(templateBodyContainerIndex, templateNavContainerIndex))
			.append(template.getNavHtml())
			.append(template.getFullText().substring(templateNavContainerIndex));
			pw.flush();
		} catch (FileNotFoundException e) {
			throw new GeneralException("fail to write to file [" + file.getAbsolutePath() + "]", e);
		}
	}
	
	private String extractPaginationHtml(int num, int pageSize) {
		
		if (pageSize == 1 || num > pageSize) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder("<footer class='pagination'><ul>");
		sb.append("<li class='prev").append(num == 1 ? "disabled" : "")
				.append("'><a href='")
				.append(extractPaginationHref(num == 1 ? 1 : (num - 1)))
				.append(">←&nbsp;Previous</a></li>");
		for (int i = 1; i <= pageSize; i++) {
			sb.append("<li class='").append(num == i ? "active" : "")
					.append("'><a href='").append(extractPaginationHref(num))
					.append(">").append(i).append("</a></li>");
		}
		sb.append("<li class='next")
				.append(num == pageSize ? "disabled" : "")
				.append("'><a href='")
				.append(extractPaginationHref(num == pageSize ? pageSize
						: (num + 1))).append(">Next&nbsp;→</a></li>");
		sb.append("</ul></footer>");

		return sb.toString();
	}
	
	private String extractArticleListHtml(List<ArticleAbstract> articleList) {
		StringBuilder sb = new StringBuilder();
		for (ArticleAbstract article : articleList) {
			sb.append("<article>")
			.append("<header><h1><a href='").append(extractArticleHref(article)).append("'>").append(article.getTitle()).append("</a></h1><div class='article-media'><p class='media-extra'><span><strong>Time</strong> <time>").append(article.getCreateDate()).append("</time></span></p></div></header>")
			.append("<div class='article-content'>").append(article.getExcerpt()).append("</div>")
			.append("<div class='article-more'><a href='").append(extractArticleHref(article)).append("'></a></div>")
			.append("</article");
		}
		
		return sb.toString();
	}
	
	private String extractPaginationHref(int num) {
		String href = siteUrl;
		if (topRelativePath != null && topRelativePath.trim().length() > 0) {
			href += "/" + topRelativePath;
		}
		if (num == 1) {
			href += "/index.html";
		} else {
			href += "/pages/" + num + "/index.html";
		}
		
		return href;
	}
	
	private String extractArticleHref(ArticleAbstract article) {
		String href = siteUrl;
		return href + "/" + article.getRelativePath();
	}
	

}
