package com.debugtoday.htmldecoder.output;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.text.html.HTML.Tag;

import com.debugtoday.htmldecoder.conf.Configuration;
import com.debugtoday.htmldecoder.conf.ConfigurationWrapper;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.object.FileOutputArg;
import com.debugtoday.htmldecoder.output.object.PaginationOutputArg;
import com.debugtoday.htmldecoder.output.object.TagFileOutputArg;
import com.debugtoday.htmldecoder.output.object.TagOutputArg;
import com.debugtoday.htmldecoder.output.object.TagWrapper;
import com.debugtoday.htmldecoder.output.object.TemplateFullTextWrapper;
import com.debugtoday.htmldecoder.struct.Theme;

public class TagPageOutput extends AbstractFileOutput {
	
	private ConfigurationWrapper conf;
	private Theme theme;
	private TemplateFullTextWrapper templateFullTextWrapper;
	
	public TagPageOutput(ConfigurationWrapper conf, Theme theme, TemplateFullTextWrapper templateFullTextWrapper) {
		this.conf = conf;
		this.theme = theme;
		this.templateFullTextWrapper = templateFullTextWrapper;
	}

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		List<TagWrapper> tagList = (List<TagWrapper>) object;
		
		File rootFile;
		try {
			rootFile = new File(conf.getOutputFile().getCanonicalPath() + File.separator + TagWrapper.extractTagRelativePath().replace("/", File.separator));
		} catch (IOException e) {
			throw new GeneralException("fail to locate file[" + conf.getOutputFile().getAbsolutePath() + "]", e);
		}
		
		TagFileOutputArg arg = new TagFileOutputArg();
		arg.setTagOutput(new TagOutput(conf, theme));
		arg.setPaginationOutput(new PaginationOutput(conf, theme));
		arg.setPagination(conf.getTagPagination());
		arg.setTagList(tagList);
		arg.setRootFile(rootFile);
		arg.setRootUrl(TagWrapper.formatTagUrl(conf.getSiteUrl()));
		
		String bodyTitle = conf.getConf(Configuration.TAG_TITLE);
		Output bodyTitleOuput = new TagTitleOutput(conf, theme);
		arg.setBodyTitle(bodyTitleOuput.export(new TagOutputArg(bodyTitle, arg.getRootUrl(), 0)));

		exportTagPage(templateFullTextWrapper, arg);
		
		return DONE;
	}

}
