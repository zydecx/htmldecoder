package com.debugtoday.htmldecoder.decoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.decoder.html.ElementDecoder;
import com.debugtoday.htmldecoder.decoder.html.MetaDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Document;
import com.debugtoday.htmldecoder.struct.html.Element;
import com.debugtoday.htmldecoder.struct.html.Meta;

public class ArticleDecoder extends GeneralDecoder {
	public static Article decode(File file, Configuration conf) throws GeneralException {
		try (
				BufferedReader reader = new BufferedReader(new FileReader(file));
				) {
			String inLine;
			Article article = new Article(file);
			StringBuilder fullText = new StringBuilder();
			
			while ((inLine = reader.readLine()) != null) {
				fullText.append(inLine).append("\n");
			}
			
			article.setFullText(replaceGeneralArguments(fullText.toString(), conf));;
			
			int offsetPos = 0;
			Element head = decodeArticleElement(article, "head", offsetPos);
			
			Element title = decodeArticleElement(article, "title", head.getFileStartPos());
			
			offsetPos = head == null ? 0 : (head.getFileStartPos() + head.getEndPosOffset());
			Element body = decodeArticleElement(article, "body", offsetPos);
			
			Element more = decodeGeneralContainer(article, CONTAINER_MORE, offsetPos);
			
			article.setTitle(title);
			article.setHead(head);
			article.setBody(body);
			article.setMore(more);

			article.setAbstractContent("");
			article.setTags(new String[]{});
			article.setCategories(new String[]{});
			article.setEnabled(true);
			article.setCreateDate(new Date());
			article.setLastUpdateDate(article.getCreateDate());
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
						article.setAbstractContent(MetaDecoder.decodeAbstract(meta));
					} else if (meta.getName().equals(MetaDecoder.META_TAGS)) {
						article.setTags(MetaDecoder.decodeTags(meta));
					} else if (meta.getName().equals(MetaDecoder.META_CATEGORY)) {
						article.setCategories(MetaDecoder.decodeCategory(meta));
					} else if (meta.getName().equals(MetaDecoder.META_ENABLED)) {
						article.setEnabled(MetaDecoder.decodeEnabled(meta));
					} else if (meta.getName().equals(MetaDecoder.META_DATE)) {
						article.setCreateDate(formatDate(article, MetaDecoder.decodeDate(meta)));
					} else if (meta.getName().equals(MetaDecoder.META_MODIFIED)) {
						article.setLastUpdateDate(formatDate(article,MetaDecoder.decodeModified(meta)));
					}
				}
			}
			
			return article;
			
		} catch (IOException e) {
			throw new GeneralException("fail to read file", e);
		}
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
	
	private static Element decodeArticleElement(Article article, String element) {
		return decodeArticleElement(article, element, 0);
	}
	
	private static Element decodeArticleElement(Article article, String element, int fromIndex) {
		String fullText = article.getFullText();
		
		int startIndex = ElementDecoder.matchElementStart(fullText, element, fromIndex);
		if (startIndex < 0) {
			return null;
		}
		
		int endIndex = ElementDecoder.matchElementEnd(fullText, element, startIndex);
		if (endIndex < 0) {
			return null;
		}
		
		int contentStartIndex = ElementDecoder.matchElementContentStart(fullText, element, startIndex);
		int contentEndIndex = ElementDecoder.matchElementContentEnd(fullText, element, startIndex);
		// i.g. <p></p>, under this circumstance, there's no actual content.
		if (contentStartIndex > contentEndIndex) {
			contentStartIndex = contentEndIndex = -1;
		}
		
		
		Element elementBean = new Element();
		elementBean.setDocument(article);
		elementBean.setTag(element);
		elementBean.setAttributes(new HashMap<String, String>());
		elementBean.setFullText(fullText.substring(startIndex, endIndex + 1));
		elementBean.setFileStartPos(startIndex);
		elementBean.setEndPosOffset(endIndex - startIndex);
		elementBean.setContentStartPosOffset(contentStartIndex - startIndex);
		elementBean.setContentEndPosOffset(contentEndIndex - startIndex);
		
		return elementBean;
	}
	
	/**
	 * format container as it's form in template file
	 * @param container
	 * @return
	 */
	private static String formatContainer(String container) {
		return "<!--" + container + "-->";
	}
	
	private static final String CONTAINER_MORE = "htmldecoder:more";

}
