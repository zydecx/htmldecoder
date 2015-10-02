package com.debugtoday.htmldecoder.output.object;

import com.debugtoday.htmldecoder.struct.Article;

public class ArticleOutputArg {
	
	private boolean outputExcerptOnly;
	private Article article;
	
	/**
	 * @param article
	 * @param outputExcerptOnly If true, output only excerpt of article; otherwise, output fulltext of article
	 */
	public ArticleOutputArg(Article article, boolean outputExcerptOnly) {
		this.article = article;
		this.outputExcerptOnly = outputExcerptOnly;
	}

	public boolean isOutputExcerptOnly() {
		return outputExcerptOnly;
	}

	public void setOutputExcerptOnly(boolean outputExcerptOnly) {
		this.outputExcerptOnly = outputExcerptOnly;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

}
