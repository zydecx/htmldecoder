package com.debugtoday.htmldecoder.output.object;

import java.io.File;
import java.util.List;

import com.debugtoday.htmldecoder.output.Output;

public class TagFileOutputArg {
	private Output tagOutput;
	private Output paginationOutput;
	private List<TagWrapper> tagList;
	private int pagination;
	private File rootFile;
	private String rootUrl;
	private String bodyTitle;
	
	public Output getTagOutput() {
		return tagOutput;
	}
	public void setTagOutput(Output tagOutput) {
		this.tagOutput = tagOutput;
	}
	public Output getPaginationOutput() {
		return paginationOutput;
	}
	public void setPaginationOutput(Output paginationOutput) {
		this.paginationOutput = paginationOutput;
	}
	public List<TagWrapper> getTagList() {
		return tagList;
	}
	public void setTagList(List<TagWrapper> tagList) {
		this.tagList = tagList;
	}
	public int getPagination() {
		return pagination;
	}
	public void setPagination(int pagination) {
		this.pagination = pagination;
	}
	public File getRootFile() {
		return rootFile;
	}
	public void setRootFile(File rootFile) {
		this.rootFile = rootFile;
	}
	public String getRootUrl() {
		return rootUrl;
	}
	public void setRootUrl(String rootUrl) {
		this.rootUrl = rootUrl;
	}
	public String getBodyTitle() {
		return bodyTitle;
	}
	public void setBodyTitle(String bodyTitle) {
		this.bodyTitle = bodyTitle;
	}

}
