package com.debugtoday.htmldecoder.output.object;

import java.util.List;

public class NavOutputArg {
	private String title;
	private String url;
	private List<NavItemOutputArg> itemList;
	
	public NavOutputArg(String title, String url,  List<NavItemOutputArg> itemList) {
		this(title, url);
		this.itemList = itemList;
	}

	public NavOutputArg(String title, String url) {
		this.title = title;
		this.setUrl(url);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<NavItemOutputArg> getItemList() {
		return itemList;
	}

	public void setItemList(List<NavItemOutputArg> itemList) {
		this.itemList = itemList;
	}

}
