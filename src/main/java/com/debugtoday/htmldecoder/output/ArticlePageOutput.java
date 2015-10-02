package com.debugtoday.htmldecoder.output;

import java.io.File;
import java.util.List;

import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.object.ArticleOutputArg;
import com.debugtoday.htmldecoder.output.object.ArticlePageArg;
import com.debugtoday.htmldecoder.output.object.FileOutputArg;
import com.debugtoday.htmldecoder.output.object.PaginationOutputArg;
import com.debugtoday.htmldecoder.output.object.TemplateFullTextWrapper;
import com.debugtoday.htmldecoder.struct.Article;
import com.debugtoday.htmldecoder.struct.Theme;

public class ArticlePageOutput extends AbstractFileOutput {
	
	private ConfigurationWrapper conf;
	private Theme theme;
	private TemplateFullTextWrapper templateFullTextWrapper;
	
	public ArticlePageOutput(ConfigurationWrapper conf, Theme theme, TemplateFullTextWrapper templateFullTextWrapper) {
		this.conf = conf;
		this.theme = theme;
		this.templateFullTextWrapper = templateFullTextWrapper;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		ArticlePageArg arg = (ArticlePageArg) object;
		
		String bodyTitle = arg.getBodyTitle();
		String pageTitle = arg.getPageTitle();
		File rootFile = arg.getRootFile();
		String rootUrl = arg.getRootUrl();
		List<Article> articleList = arg.getArticleList();
		
		Output articleOutput = new ArticleOutput(conf, theme);
		Output paginationOutput = new PaginationOutput(conf, theme);
		
		int pagination = conf.getDefaultPagination();
		int itemSize = articleList.size();
		int pageSize = (int) Math.ceil(itemSize * 1.0 / pagination);
		
		for (int i = 1; i <= pageSize; i++) {
			List<Article> subList = articleList.subList((i - 1) * pagination, Math.min(itemSize, i * pagination));
			
			StringBuilder sb = new StringBuilder();
			for (Article article : subList) {
				sb.append(articleOutput.export(new ArticleOutputArg(article, true)));
			}
			
			FileOutputArg fileArg = new FileOutputArg();
			fileArg.setBodyTitle(bodyTitle);
			fileArg.setPageTitle(pageTitle);
			fileArg.setBody(sb.toString());
			fileArg.setPagination(paginationOutput.export(new PaginationOutputArg(rootUrl, pageSize, i)));
			
			File file = new File(formatPageFilePath(rootFile.getAbsolutePath(), i));
			writeToFile(file, templateFullTextWrapper, fileArg);
		}
		return DONE;
	}

}
