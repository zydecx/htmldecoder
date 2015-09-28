package com.debugtoday.htmldecoder.struct;

import java.io.File;
import java.util.Date;

import com.debugtoday.htmldecoder.struct.html.Element;

public class Article extends Document {
	
	private boolean enabled;
	private Element title;
	private String abstractContent;
	private String[] tags;
	private String[] categories;
	private Date createDate;
	private Date lastUpdateDate;
	private Element head;
	private Element body;
	private Element more;
	
	public Article(File file) {
		super(file);
	}
	
	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Element getTitle() {
		return title;
	}

	public void setTitle(Element title) {
		this.title = title;
	}

	public String getAbstractContent() {
		return abstractContent;
	}
	public void setAbstractContent(String abstractContent) {
		this.abstractContent = abstractContent;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public String[] getCategories() {
		return categories;
	}
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Element getHead() {
		return head;
	}
	public void setHead(Element head) {
		this.head = head;
	}
	public Element getBody() {
		return body;
	}
	public void setBody(Element body) {
		this.body = body;
	}
	
	public Element getMore() {
		return more;
	}

	public void setMore(Element more) {
		this.more = more;
	}

	public ArticleAbstract formatArticleAbsract() {
		ArticleAbstract articleAbstract = new ArticleAbstract();
		articleAbstract.setTitle(title == null ? getFile().getName() : title.getContentText());
		articleAbstract.setAbstractContent(abstractContent);
		articleAbstract.setExcerpt(extractExcerpt());
		articleAbstract.setCategories(categories);
		articleAbstract.setTags(tags);
		articleAbstract.setCreateDate(createDate);
		articleAbstract.setLastUpdateDate(lastUpdateDate);
		articleAbstract.setArticle(this);
		
		return articleAbstract;
	}
	
	/**
	 * extract excerpt of article. Use content of body if more tag not defined
	 * @return
	 */
	private String extractExcerpt() {
		
		if (more == null) {
			return body.getFullText();
		}
		
		return body.getContentText().substring(0, more.getFileStartPos() - body.getFileStartPos() - body.getContentStartPosOffset());
	}

}
