package com.debugtoday.htmldecoder.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.debugtoday.htmldecoder.decoder.GeneralDecoder;
import com.debugtoday.htmldecoder.exception.GeneralException;
import com.debugtoday.htmldecoder.output.object.FileOutputArg;
import com.debugtoday.htmldecoder.output.object.PaginationOutputArg;
import com.debugtoday.htmldecoder.output.object.TagFileOutputArg;
import com.debugtoday.htmldecoder.output.object.TagOutputArg;
import com.debugtoday.htmldecoder.output.object.TagWrapper;
import com.debugtoday.htmldecoder.output.object.TemplateFullTextWrapper;
import com.debugtoday.htmldecoder.struct.TemplateKey;

/**
 * base class for output relative to file output, i.g. article/article page/tag page/category page
 * @author chuff
 *
 */
public class AbstractFileOutput implements Output {

	@Override
	public String export(Object object) throws GeneralException {
		// TODO Auto-generated method stub
		return DONE;
	}
	
	/**
	 * @param file
	 * @param template
	 * @param arg
	 * @throws GeneralException
	 */
	public void writeToFile(File file, TemplateFullTextWrapper template, FileOutputArg arg) throws GeneralException {
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new GeneralException("fail to create file[" + file.getAbsolutePath() + "]", e);
			}
		}
		
		try (
				PrintWriter pw = new PrintWriter(new FileOutputStream(file));
				) {
			String formattedHead = "";
			if (arg.getPageTitle() != null) {
				formattedHead += "<title>" + arg.getPageTitle() + "</title>";
			}
			if (arg.getHead() != null) {
				formattedHead += "\n" + arg.getHead();
			}
			String templateFullText = template.getFullText()
					.replaceAll(GeneralDecoder.formatPlaceholderRegex("head"), formattedHead);
			if (arg.getBodyTitle() != null) {
				templateFullText = templateFullText
						.replaceAll(GeneralDecoder.formatPlaceholderRegex("body_title"), arg.getBodyTitle());
			}
			if (arg.getBody() != null) {
				templateFullText = templateFullText
						.replaceAll(GeneralDecoder.formatPlaceholderRegex("body"), arg.getBody());
			}
			if (arg.getPagination() != null) {
				templateFullText = templateFullText
						.replaceAll(GeneralDecoder.formatPlaceholderRegex("pagination"), arg.getPagination());
			}
			
			pw.write(templateFullText);
		} catch (FileNotFoundException e) {
			throw new GeneralException("fail to write to file[" + file.getAbsolutePath() + "]", e);
		}
	}
	
	/**
	 * used for tag page/category page output
	 * @param templateFullTextWrapper
	 * @param arg
	 * @throws GeneralException
	 */
	public void exportTagPage(TemplateFullTextWrapper templateFullTextWrapper, TagFileOutputArg arg) throws GeneralException {
		List<TagWrapper> itemList = arg.getTagList();
		int itemSize = itemList.size();
		int pagination = arg.getPagination();
		int pageSize = (int) Math.ceil(itemSize * 1.0 / pagination);
		
		Output tagOutput = arg.getTagOutput();
		Output paginationOutput = arg.getPaginationOutput();
		
		for (int i = 1; i <= pageSize; i++) {
			List<TagWrapper> subList = itemList.subList((i - 1) * pagination, Math.min(itemSize, i * pagination));
			StringBuilder sb = new StringBuilder();
			for (TagWrapper item : subList) {
				String itemName = item.getName();
				TagOutputArg tagArg = new TagOutputArg(itemName, formatPageUrl(arg.getRootUrl(), i), item.getArticleSet().size());
				sb.append(tagOutput.export(arg));
			}
			
			File file = new File(formatPageFilePath(arg.getRootFile().getAbsolutePath(), i));
			
			FileOutputArg fileOutputArg = new FileOutputArg();
			fileOutputArg.setBodyTitle(arg.getBodyTitle());
			fileOutputArg.setBody(sb.toString());
			fileOutputArg.setPageTitle(arg.getBodyTitle());
			fileOutputArg.setPagination(paginationOutput.export(new PaginationOutputArg(arg.getRootUrl(), pageSize, i)));
			
			writeToFile(file, templateFullTextWrapper, fileOutputArg);
		}
		
	}
	
	protected String formatPageUrl(String rootUrl, int index) {
		return PaginationOutput.formatPaginationUrl(rootUrl, index);
	}
	
	protected String formatPageFilePath(String rootPath, int index) {
		return PaginationOutput.formatPaginationFilePath(rootPath, index);
	}

}
