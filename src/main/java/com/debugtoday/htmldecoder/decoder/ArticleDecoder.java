package com.debugtoday.htmldecoder.decoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.debugtoday.htmldecoder.decoder.html.ElementDecoder;
import com.debugtoday.htmldecoder.decoder.html.MetaDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.html.Element;
import com.debugtoday.htmldecoder.struct.html.Meta;

public class ArticleDecoder {
	public static Article decode(File file) throws GeneralException {
		try (
				BufferedReader reader = new BufferedReader(new FileReader(file));
				) {
			String inLine;
			Article article = new Article(file);
			StringBuilder fullText = new StringBuilder();
			
			while ((inLine = reader.readLine()) != null) {
				fullText.append(inLine).append("\n");
			}
			
			article.setFullText(fullText.toString());
			
			int offsetPos = 0;
			Element head = decodeArticleElement(article, "head", offsetPos);
			
			Element title = decodeArticleElement(article, "title", head.getFileStartPos());
			
			offsetPos = head == null ? 0 : (head.getFileStartPos() + head.getEndPosOffset());
			Element body = decodeArticleElement(article, "body", offsetPos);
			
			article.setTitle(title);
			article.setHead(head);
			article.setBody(body);

			article.setAbstractContent("");
			article.setKeyword(new String[]{});
			article.setCategories(new String[]{});
			article.setEnabled(true);
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
					} else if (meta.getName().equals(MetaDecoder.META_KEYWORD)) {
						article.setKeyword(MetaDecoder.decodeKeyword(meta));
					} else if (meta.getName().equals(MetaDecoder.META_CATEGORY)) {
						article.setCategories(MetaDecoder.decodeCategory(meta));
					} else if (meta.getName().equals(MetaDecoder.META_ENABLED)) {
						article.setEnabled(MetaDecoder.decodeEnabled(meta));
					}
				}
			}
			
			return article;
			
		} catch (IOException e) {
			throw new GeneralException("fail to read file", e);
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

}
