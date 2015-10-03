package com.debugtoday.htmldecoder.struct;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.debugtoday.htmldecoder.struct.html.Element;

public class Template extends Document {
	private String key;
	private Element head;
	private Element body;
	private Map<String, List<TemplateArgument>> arguments;
	private Map<String, List<TemplatePlaceHolder>> placeHolders;
	
	public Template(String key, File file) {
		super(file);
		this.key = key;
		this.arguments = new HashMap<>();
		this.placeHolders = new HashMap<>();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Element getHead() {
		return head;
	}

	public void setHead(Element head) {
		this.head = head;
	}

	public Element getBody() {
		return body;
	}

	public void setBody(Element body) {
		this.body = body;
	}

	public Map<String, List<TemplateArgument>> getArguments() {
		return arguments;
	}

	public void setArguments(Map<String, List<TemplateArgument>> arguments) {
		this.arguments = arguments;
	}

	public Map<String, List<TemplatePlaceHolder>> getPlaceHolders() {
		return placeHolders;
	}

	public void setPlaceHolders(Map<String, List<TemplatePlaceHolder>> placeHolders) {
		this.placeHolders = placeHolders;
	}

}
