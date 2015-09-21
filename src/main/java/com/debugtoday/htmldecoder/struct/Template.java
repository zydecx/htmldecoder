package com.debugtoday.htmldecoder.struct;

import java.io.File;

import com.debugtoday.htmldecoder.struct.html.Element;

public class Template {
	private File file;
	private Element headContainer;
	private Element bodyContainer;
	
	public Template(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Element getHeadContainer() {
		return headContainer;
	}
	public void setHeadContainer(Element headContainer) {
		this.headContainer = headContainer;
	}
	public Element getBodyContainer() {
		return bodyContainer;
	}
	public void setBodyContainer(Element bodyContainer) {
		this.bodyContainer = bodyContainer;
	}

}
