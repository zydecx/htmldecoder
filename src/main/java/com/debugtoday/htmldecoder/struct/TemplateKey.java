package com.debugtoday.htmldecoder.struct;

import java.util.HashMap;
import java.util.Map;

/**
 * defines a list of keys of template.<br>
 * <i>getKey()</i> returns that can be used to locate template file.
 * @author chuff
 *
 */
public enum TemplateKey {
	TEMPLATE("template"),
	
	STATIC_PAGE("static_page"),
	
	PAGINATION("pagination"),
	PAGINATION_ITEM("pagination_item"),
	PAGINATION_ITEM_ACTIVE("pagination_item_active"),
	PAGINATION_ITEM_DISABLED("pagination_item_disabled"),
	
	NAV("nav"),
	NAV_ITEM("nav_item"),
	
	ARTICLE("article"),
	ARTICLE_MEDIA_CATEGORY("article_media_category"),
	ARTICLE_MEDIA_TAG("article_media_tag"),
	ARTICLE_MORE("article_more"),
	
	CATEGORY("category"),
	CATEGORY_TITLE("category_title"),
	
	TAG("tag"),
	TAG_TITLE("tag_title");
	
	private String key;
	
	TemplateKey(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public static TemplateKey parseKey(String key) {
		return TemplateKeyCacheHolder.cache.get(key);
	}
	
	private static final class TemplateKeyCacheHolder {
		static Map<String, TemplateKey> cache = getCache();
		
		static Map<String, TemplateKey> getCache() {
			Map<String, TemplateKey> cache = new HashMap<>();
			for (TemplateKey t : TemplateKey.values()) {
				cache.put(t.getKey(), t);
			}
			
			return cache;
		}
	}

}
