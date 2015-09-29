package com.debugtoday.htmldecoder.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.debugtoday.htmldecoder.HtmlDecoder.TagUtil;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.util.FileUtil;

public class TagPaginationExport {
	private String siteUrl;
	private Template template;
	private int paginationSize;
	private String title;
	private File topFolder;
	private String topRelativePath;
	private List<TagUtil> tagList;
	
	public TagPaginationExport(String title, File topFolder, String topRelativePath, List<TagUtil> tagList, int paginationSize, Template template, String siteUrl) {
		this.title = title;
		this.topFolder = topFolder;
		this.topRelativePath = topRelativePath;
		this.tagList = tagList;
		this.siteUrl = siteUrl;
		this.template = template;
		this.paginationSize = paginationSize;
	}
	
	public void export() throws GeneralException {
		int articleSize = tagList.size();
		int pageSize = (int) Math.ceil(articleSize * 1.0 / paginationSize);
		
		for (int i = 1; i <= pageSize; i++) {
			exportPage(tagList.subList((i - 1) * paginationSize, Math.min(i * paginationSize, articleSize)), i, pageSize);
		}
	}
	
	private void exportIndexPage(List<TagUtil> tagList, int pageSize) throws GeneralException {
		exportPage(tagList, 1, pageSize);
	}
	
	private void exportPage(List<TagUtil> tagList, int num, int pageSize) throws GeneralException {
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
		writePageToFile(file, tagList, paginationHtml);
	}
	
	private void writePageToFile(File file, List<TagUtil> tagList, String paginationHtml) throws GeneralException {
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
			.append(extracttagListHtml(tagList))
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
		
		StringBuilder sb = new StringBuilder("<div class='pagination'><ul>");
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
		sb.append("</ul></div>");

		return sb.toString();
	}
	
	private String extracttagListHtml(List<TagUtil> tagList) {
		StringBuilder sb = new StringBuilder("<div class='article_list'>");
		for (TagUtil tag : tagList) {
			sb.append("<article>")
			.append("<div class='tag_content'><a href='").append(extractTagHref(tag)).append("'>").append(tag.getTag()).append("</a>").append("<span class='tag_num'>").append(tag.getArticleSet().size()).append("</span></div>")
			.append("</article");
		}
		sb.append("</div>");
		
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
	
	private String extractTagHref(TagUtil tag) {
		String href = siteUrl;
		if (topRelativePath != null && topRelativePath.trim().length() > 0) {
			href += "/" + topRelativePath;
		}
		return href + "/" + tag.getTag();
	}
	

}
