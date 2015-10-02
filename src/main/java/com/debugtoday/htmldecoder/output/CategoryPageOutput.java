package com.debugtoday.htmldecoder.output;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.object.TagFileOutputArg;
import com.debugtoday.htmldecoder.output.object.TagOutputArg;
import com.debugtoday.htmldecoder.output.object.TagWrapper;
import com.debugtoday.htmldecoder.output.object.TemplateFullTextWrapper;
import com.debugtoday.htmldecoder.struct.Theme;

public class CategoryPageOutput extends AbstractFileOutput {
	
	private ConfigurationWrapper conf;
	private Theme theme;
	private TemplateFullTextWrapper templateFullTextWrapper;
	
	public CategoryPageOutput(ConfigurationWrapper conf, Theme theme, TemplateFullTextWrapper templateFullTextWrapper) {
		this.conf = conf;
		this.theme = theme;
		this.templateFullTextWrapper = templateFullTextWrapper;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		List<TagWrapper> categoryList = (List<TagWrapper>) object;
		
		File rootFile;
		try {
			rootFile = new File(conf.getOutputFile().getCanonicalPath() + File.separator + TagWrapper.extractCategoryRelativePath().replace("/", File.separator));
		} catch (IOException e) {
			throw new GeneralException("fail to locate file[" + conf.getOutputFile().getAbsolutePath() + "]", e);
		}
		
		TagFileOutputArg arg = new TagFileOutputArg();
		arg.setTagOutput(new CategoryOutput(conf, theme));
		arg.setPaginationOutput(new PaginationOutput(conf, theme));
		arg.setPagination(conf.getCategoryPagination());
		arg.setTagList(categoryList);
		arg.setRootFile(rootFile);
		arg.setRootUrl(TagWrapper.formatCategoryUrl(conf.getSiteUrl()));

		String bodyTitle = conf.getConf(Configuration.CATEGORY_TITLE);
		Output bodyTitleOuput = new CategoryTitleOutput(conf, theme);
		arg.setBodyTitle(bodyTitleOuput.export(new TagOutputArg(bodyTitle, arg.getRootUrl(), 0)));
		
		exportTagPage(templateFullTextWrapper, arg);
		
		return DONE;
	}

}
