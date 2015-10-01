package com.debugtoday.htmldecoder.output.object;

import java.util.List;

public class NavOutputArg {
	private String title;
	private List<NavItemOutputArg> itemList;
	
	public NavOutputArg(String title,  List<NavItemOutputArg> itemList) {
		this(title);
		this.itemList = itemList;
	}

	public NavOutputArg(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<NavItemOutputArg> getItemList() {
		return itemList;
	}

	public void setItemList(List<NavItemOutputArg> itemList) {
		this.itemList = itemList;
	}

}
