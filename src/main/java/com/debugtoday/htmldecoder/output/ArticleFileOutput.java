package com.debugtoday.htmldecoder.output;

import java.io.File;
import java.util.List;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.object.ArticleOutputArg;
import com.debugtoday.htmldecoder.output.object.FileOutputArg;
import com.debugtoday.htmldecoder.output.object.TemplateFullTextWrapper;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Template;
import com.debugtoday.htmldecoder.struct.TemplateKey;
import com.debugtoday.htmldecoder.struct.Theme;

/**
 * Write articles/static pages to output folder
 * @author zydecx
 *
 */
public class ArticleFileOutput extends AbstractFileOutput {
	
	private ConfigurationWrapper conf;
	private Theme theme;
	private TemplateFullTextWrapper templateFullTextWrapper;
	
	public ArticleFileOutput(ConfigurationWrapper conf, Theme theme, TemplateFullTextWrapper templateFullTextWrapper) {
		this.conf = conf;
		this.theme = theme;
		this.templateFullTextWrapper = templateFullTextWrapper;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		List<Article> articleList = (List<Article>) object;
		
		ArticleOutput articleOutput = new ArticleOutput(conf, theme);
		for (Article article : articleList) {
			ArticleOutputArg arg = new ArticleOutputArg(article, false);
			String fullText = articleOutput.export(arg);
			
			File file = new File(conf.getOutputFile().getAbsolutePath() + File.separator + article.getRelativePath().replace("/", File.separator));
			
			FileOutputArg fileOutputArg = new FileOutputArg();
			fileOutputArg.setBody(fullText);
			fileOutputArg.setHead(article.getHead().getContentText());
			fileOutputArg.setPageTitle(article.getTitle() == null ? "" : article.getTitle().getContentText());
			
			writeToFile(file, templateFullTextWrapper, fileOutputArg);
		}
		return null;
	}

}
