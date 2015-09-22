package com.debugtoday.htmldecoder.struct;

import java.io.File;

import com.debugtoday.htmldecoder.struct.html.Element;

public class Template extends Document {
	private Element headContainer;
	private Element bodyContainer;
	
	public Template(File file) {
		super(file);
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
