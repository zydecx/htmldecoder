package com.debugtoday.htmldecoder.decoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.decoder.html.MetaDecoder;
import com.debugtoday.htmldecoder.decoder.md.MarkdownDecoder;
import com.debugtoday.htmldecoder.decoder.md.MarkdownDecoderArg;
import com.debugtoday.htmldecoder.decoder.md.MarkdownDecoderFactory;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.log.CommonLog;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.ArticleMeta;
import com.debugtoday.htmldecoder.struct.Document;
import com.debugtoday.htmldecoder.struct.html.Element;
import com.debugtoday.htmldecoder.struct.html.Meta;
import com.debugtoday.htmldecoder.util.FileUtil;
import com.sun.corba.se.spi.ior.MakeImmutable;

public class ArticleDecoder extends GeneralDecoder {
	
	private static final Logger logger = CommonLog.getLogger();
	
	public static Article decode(File file, ConfigurationWrapper conf) throws GeneralException {
		String extensionName = FileUtil.fileExtensionName(file);
		if (EXT_FILE_MD.equalsIgnoreCase(extensionName)) {
			return decodeMarkdownFile(file, conf);
		} else {
			return decodeHtmlFile(file, conf);
		}
	}
	
	/**
	 * if file is an article file(supported to decode)
	 * @param file
	 * @return
	 */
	public static boolean isArticleFile(File file) {
		String[] exts = {
				"html",
				"htm",
				"md"
		};
		String extensionName = FileUtil.fileExtensionName(file);
		for (String e : exts) {
			if (e.equalsIgnoreCase(extensionName)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static Article decodeHtmlFile(File file, ConfigurationWrapper conf) throws GeneralException {
		try (
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				) {
			String inLine;
			Article article = new Article(file);
			StringBuilder fullText = new StringBuilder();
			
			while ((inLine = reader.readLine()) != null) {
				fullText.append(inLine).append("\n");
			}
			
			article.setFullText(fullText.toString());
			
			return decodeArticle(article, conf);
			
		} catch (IOException e) {
			throw new GeneralException("fail to read file", e);
		}
		
	}
	
	private static Article decodeMarkdownFile(File file, ConfigurationWrapper conf) throws GeneralException {
		String interpreter = conf.getConf(Configuration.MARKDOWN_INTERPRETER);
		MarkdownDecoder decoder = MarkdownDecoderFactory.getInstance(interpreter);
		
		if (decoder == null) {
			throw new GeneralException("markdown interpreter [" + interpreter + "] not found.");
		}
		
		MarkdownDecoderArg arg = new MarkdownDecoderArg(conf);
		String fullText = decoder.decode(file, arg);

		Article article = new Article(file);
		article.setFullText(fullText);
		
		return decodeArticle(article, conf);		
	}
	
	private static Article decodeArticle(Article article, ConfigurationWrapper conf) throws GeneralException {
		if (article == null || article.getFullText() == null) {
			logger.warn("empty article or article fulltext, fail to decode article");
		}
		
		article.setFullText(replaceGeneralArguments(article.getFullText().toString(), conf.getConfiguration()));

		article.setPreList(decodePreElement(article));
		
		int offsetPos = 0;
		Element head = decodeGeneralElement(article, "head", offsetPos);
		
		Element title = decodeGeneralElement(article, "title", head.getFileStartPos());
		
		offsetPos = head == null ? 0 : (head.getFileStartPos() + head.getEndPosOffset());
		Element body = decodeGeneralElement(article, "body", offsetPos);
		
		Element more = decodeGeneralPlaceholder(article, PLACEHOLDER_MORE, offsetPos);
		
		article.setTitle(title);
		article.setHead(head);
		article.setBody(body);
		article.setMore(more);
		
		// even for markdown file, can be outputted as html file
		String relativePath;
		try {
			relativePath = FileUtil.relativePath(conf.getContentFile(), article.getFile().getParentFile());
		} catch (IOException e) {
			throw new GeneralException(e);
		}
		if (!relativePath.equals("")) {
			relativePath += "/";
		}
		relativePath += FileUtil.fileName(article.getFile()) + ".html";
		article.setRelativePath(relativePath);

		ArticleMeta articleMeta = new ArticleMeta();
		
		article.setMeta(articleMeta);
		
		articleMeta.setAbstractContent("");
		articleMeta.setTags(new String[]{});
		articleMeta.setCategories(new String[]{});
		articleMeta.setEnabled(true);
		articleMeta.setAuthor("");
		articleMeta.setAuthorUrl("");
		articleMeta.setCreateDate(new Date());
		articleMeta.setLastUpdateDate(articleMeta.getCreateDate());
		if (head != null) {
			List<Meta> metaList = new ArrayList<>();
			Meta nextMeta = null;
			offsetPos = head.getFileStartPos();
			// get all meta available
			while(true) {
				nextMeta = MetaDecoder.matchMeta(head.getFullText(), offsetPos);
				if (nextMeta == null) break;
				
				metaList.add(nextMeta);
				offsetPos = nextMeta.getStartPos() + nextMeta.getFullText().length();
			}
			// update article meta
			for (Meta meta : metaList) {
				if (meta.getName() == null) continue;
				
				if (meta.getName().equals(MetaDecoder.META_ABSTRACT)) {
					articleMeta.setAbstractContent(MetaDecoder.decodeAbstract(meta));
				} else if (meta.getName().equals(MetaDecoder.META_TAGS)) {
					articleMeta.setTags(MetaDecoder.decodeTags(meta));
				} else if (meta.getName().equals(MetaDecoder.META_CATEGORY)) {
					articleMeta.setCategories(MetaDecoder.decodeCategory(meta));
				} else if (meta.getName().equals(MetaDecoder.META_ENABLED)) {
					articleMeta.setEnabled(MetaDecoder.decodeEnabled(meta));
				} else if (meta.getName().equals(MetaDecoder.META_AUTHOR)) {
					articleMeta.setAuthor(MetaDecoder.decodeAuthor(meta));
				} else if (meta.getName().equals(MetaDecoder.META_AUTHORURL)) {
					articleMeta.setAuthorUrl(MetaDecoder.decodeAuthorUrl(meta));
				} else if (meta.getName().equals(MetaDecoder.META_DATE)) {
					articleMeta.setCreateDate(formatDate(article, MetaDecoder.decodeDate(meta)));
				} else if (meta.getName().equals(MetaDecoder.META_MODIFIED)) {
					articleMeta.setLastUpdateDate(formatDate(article,MetaDecoder.decodeModified(meta)));
				}
			}
		}
		
		return article;
	}
	
	/**
	 * specially for date&modified, use last-modified date of file in case meta is not set
	 * @param document
	 * @param dateStr
	 * @return
	 */
	private static Date formatDate(Document document, String dateStr) {
		SimpleDateFormat sdfMin = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			return sdfMin.parse(dateStr);
		} catch (ParseException e) {
			System.err.println("fail to parse date[" + dateStr + "]");
			return new Date(document.getFile().lastModified());
		}
	}
	
	/**
	 * format container as it's form in template file
	 * @param container
	 * @return
	 */
	private static String formatContainer(String container) {
		return "<!--" + container + "-->";
	}
	
	private static final String PLACEHOLDER_MORE = "more";
	
	// extension name of markdown file
	private static final String EXT_FILE_MD = "md";

}
