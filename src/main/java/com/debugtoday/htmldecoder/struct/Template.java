package com.debugtoday.htmldecoder.struct;

import java.io.File;

import com.debugtoday.htmldecoder.struct.html.Element;

public class Template extends Document {
	private Element headContainer;
	private Element bodyContainer;
	private Element navContainer;
	private String navHtml;
	
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

	public Element getNavContainer() {
		return navContainer;
	}

	public void setNavContainer(Element navContainer) {
		this.navContainer = navContainer;
	}

	public String getNavHtml() {
		return navHtml;
	}

	public void setNavHtml(String navHtml) {
		this.navHtml = navHtml;
	}

}
