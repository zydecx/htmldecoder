package com.debugtoday.htmldecoder.struct;

import java.util.HashMap;
import java.util.Map;

public class Theme {
	
	private String name;
	private Map<TemplateKey, Template> templates;
	
	public Theme(String name) {
		this.name = name;
		this.templates = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<TemplateKey, Template> getTemplates() {
		return templates;
	}

	public void setTemplates(Map<TemplateKey, Template> templates) {
		this.templates = templates;
	}

}
