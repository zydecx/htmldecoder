package com.debugtoday.htmldecoder.struct;

import java.io.File;
import java.util.List;

import com.debugtoday.htmldecoder.struct.html.Element;

public class Document {
	
	private File file;
	private String fullText;
	private List<Element> preList;
	
	public Document(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public List<Element> getPreList() {
		return preList;
	}

	public void setPreList(List<Element> preList) {
		this.preList = preList;
	}

}
